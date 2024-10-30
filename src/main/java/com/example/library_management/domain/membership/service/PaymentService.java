package com.example.library_management.domain.membership.service;

import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.membership.dto.request.PaymentConfirmRequest;
import com.example.library_management.domain.membership.dto.request.PaymentSearchCondition;
import com.example.library_management.domain.membership.dto.response.PaymentRequestResponse;
import com.example.library_management.domain.membership.dto.response.PaymentResponse;
import com.example.library_management.domain.membership.dto.response.PaymentSearchResult;
import com.example.library_management.domain.membership.dto.response.TossPaymentResponse;
import com.example.library_management.domain.membership.entity.Membership;
import com.example.library_management.domain.membership.entity.MembershipPayment;
import com.example.library_management.domain.membership.enums.MembershipStatus;
import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.example.library_management.domain.membership.exception.ActiveMembershipExistsException;
import com.example.library_management.domain.membership.exception.DuplicatePaymentException;
import com.example.library_management.domain.membership.exception.PaymentException;
import com.example.library_management.domain.membership.exception.PaymentServerException;
import com.example.library_management.domain.membership.repository.MembershipPaymentRepository;
import com.example.library_management.domain.membership.repository.MembershipRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.service.UserService;
import com.example.library_management.global.config.TossPaymentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final MembershipPaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final TossPaymentProperties tossPaymentProperties;

    // 결제 요청 생성
    public PaymentRequestResponse createPaymentRequest(User user) {
        validateMembershipStatus(user);  // 활성 멤버십 체크

        String orderId = UUID.randomUUID().toString();

        // orderId에 userId 포함
        String orderIdWithUser = user.getId() + "_" + orderId;

        return PaymentRequestResponse.builder()
                .amount(Membership.MONTHLY_FEE)
                .orderId(orderIdWithUser)
                .orderName("도서관 멤버십 구독")
                .successUrl(tossPaymentProperties.getSuccessUrl())
                .failUrl(tossPaymentProperties.getFailUrl())
                .build();
    }

    // 결제 승인 처리
    public PaymentResponse confirmPayment(String paymentKey, String orderId, Long amount, User user) {
        log.info("Payment confirmation started - orderId: {}, amount: {}, user: {}", orderId, amount, user.getId());

        try {
            validatePaymentAmount(amount);  // 결제 금액이 일치하는지 확인

            // orderId에서 userId 추출
            Long userId = Long.valueOf(orderId.split("_")[0]);

            // 현재 로그인한 사용자와 결제 요청 사용자가 일치하는지 확인
            if (!userId.equals(user.getId())) {
                throw new PaymentException(GlobalExceptionConst.UNAUTHORIZED_PAYMENT);
            }

            // 토스페이먼츠 결제 승인 API 호출
            HttpHeaders headers = createHeaders();
            String url = tossPaymentProperties.getApi().getBaseUrl() + tossPaymentProperties.getApi().getConfirmUrl();

            PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(paymentKey, orderId, amount);


            ResponseEntity<TossPaymentResponse> responseEntity = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(confirmRequest, headers),
                    TossPaymentResponse.class
            );

            // 응답 처리
            if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
                log.error("Toss API failed - Status: {}", responseEntity.getStatusCode());
                throw new PaymentException(GlobalExceptionConst.PAYMENT_PROCESSING_ERROR);
            }

            TossPaymentResponse tossResponse = responseEntity.getBody();
            log.info("Toss API response successful - status: {}", tossResponse.getStatus());

            // 4. 중복 결제 체크
            if (paymentRepository.findByPaymentKey(paymentKey).isPresent()) {
                log.error("Duplicate payment detected - paymentKey: {}", paymentKey);
                throw new DuplicatePaymentException();
            }

            // 5. 멤버십 생성/갱신 및 결제 기록 저장
            Membership membership = createOrRenewMembership(userId);
            MembershipPayment payment = createPaymentRecord(membership, tossResponse);

            log.info("Payment process completed successfully - orderId: {}", orderId);

            // 6. 응답 생성
            return PaymentResponse.builder()
                    .paymentKey(tossResponse.getPaymentKey())
                    .orderId(tossResponse.getOrderId())
                    .amount(tossResponse.getAmount())
                    .status(PaymentStatus.SUCCESS)
                    .build();
            }catch (RestClientException e){
            log.error("Payment confirmation failed", e);
            throw new PaymentServerException();
        }catch (Exception e) {
            log.error("Payment confirmation error: ", e);  // 상세 에러 로깅 추가
            throw new PaymentException(GlobalExceptionConst.PAYMENT_PROCESSING_ERROR);
        }
    }

    // 결제 실패 처리
    public String handlePaymentFailure(String code, String message, String orderId) {
        log.error("Payment failed - Code: {}, Message: {}, OrderId: {}", code, message, orderId);

        // 실패한 결제 기록 저장
        MembershipPayment failedPayment = MembershipPayment.builder()
                .orderId(orderId)
                .status(PaymentStatus.FAILED)
                .failReason(String.format("Error code: %s, Message: %s", code, message))
                .amount(Membership.MONTHLY_FEE)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(failedPayment);

        return "결제에 실패했습니다.";
    }
    
    // 결제 내역 조회
    @Transactional(readOnly = true)
    public Page<PaymentSearchResult> getPaymentHistory(User user, PaymentSearchCondition condition, Pageable pageable) {
        return paymentRepository.search(condition, user, pageable);
    }
    
    // 활성화된 멤버십 검증 메서드
    private void validateMembershipStatus(User user) {
        Optional<Membership> activeMembership = membershipRepository.findByUserAndStatus(user, MembershipStatus.ACTIVE);
        if (activeMembership.isPresent() && !activeMembership.get().isExpired()) {
            throw new ActiveMembershipExistsException();
        }
    }

    // 결제 금액 검증 메서드
    private void validatePaymentAmount(Long amount) {
        if (!amount.equals(Membership.MONTHLY_FEE)){
            throw new PaymentException(GlobalExceptionConst.INVALID_PAYMENT_AMOUNT);
        }
    }
    // 헤더 생성 메서드
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((tossPaymentProperties.getSecretKey() + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    // 멤버십 생성 또는 갱신
    private Membership createOrRenewMembership(Long userId) {
        User user = userService.findUser(userId);

        Optional<Membership> existingMembership = membershipRepository.findByUserAndStatus(user, MembershipStatus.ACTIVE);

        if (existingMembership.isPresent()){    // 이미 멤버십이 있다면 갱신
            Membership membership = existingMembership.get();
            membership.renewMembership();
            return membershipRepository.save(membership);
        }else {   // 멤버십이 없다면 생성
            Membership newMembership = Membership.createMembership(user);
            return membershipRepository.save(newMembership);
        }
    }

    // 결제 기록 생성
    private MembershipPayment createPaymentRecord(Membership membership, TossPaymentResponse response) {
        MembershipPayment payment = MembershipPayment.createPayment(membership, response.getPaymentKey(), response.getBillingKey(), response.getOrderId());
        return paymentRepository.save(payment);
    }
}
