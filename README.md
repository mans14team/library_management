# LibMate - 도서관 웹 서비스
![LibMate_Project_300x150](https://github.com/user-attachments/assets/e5cf22bd-17cb-423a-92c8-d138338b3639)

## 1. 프로젝트 소개
LibMate는 도서 대여와 스터디룸 예약 관리를 위한 웹 서비스로, 동시성 제어와 실시간 알림을 통해 효율적인 도서관 자원 관리를 제공합니다.

**개발 기간**: 2024.10.21 ~ 2024.11.22

## 2. 프로젝트 핵심 목표
- 대규모 트래픽 환경에서 안정적인 도서 예약/대여 시스템 구축
- 결제 API 활용해 멤버십 구현
- 실시간 알림을 통한 사용자 경험 개선
- 동시성 제어 및 캐싱 처리
- 성능 최적화 및 모니터링 시스템 구축
- 자동화된 CI-CD 구축

## 3. KEY SUMMARY
### 🔷 성능 개선
- 메시지 송신 방법 동기-> 비동기방식으로 변경
### 개선 전
![image](https://github.com/user-attachments/assets/17353b6d-6ff4-4e3e-9b07-e3c7f6a0d4ee)

### 개선 후
![image](https://github.com/user-attachments/assets/3b6d6462-64b7-4863-8add-52ce03b27583)

- 응답 시간이 크게 줄어들어 성능이 약 10배 개선된 것을 확인 할 수 있었습니다.
### 🔷 기술적 성과
- Redis를 활용한 캐싱 시스템 구현
- AWS 인프라 기반 자동화된 배포 환경 구축
- JMeter를 통한 성능 테스트 및 최적화
- RabbitMQ 활용한 알림 시스템 구현

## 4. 인프라 아키텍처 & 적용 기술
### Backend
- Java 17
- Spring Boot,
- Spring Security, Batch
- JPA, JWT, Gradle

### Database & Cache
- MySQL
- Redis

### DevOps
- AWS (Elastic Beanstalk, RDS, ECR, ElastiCache)
- Docker
- Github Actions

### cooperation
- Github
- Notion
- Slack

### Rest Api
- Toss Payment API
- Kakao Login

### 인프라 아키텍쳐
![image (4)](https://github.com/user-attachments/assets/b82c06a6-8f24-4779-aab9-478f2438d440)


## 5. 주요 기능
### 도서 대여/반납 및 예약 
### 스터디룸 예약
### 리뷰 기능
### 게시글 작성
### 멤버십 시스템
### 실시간 알림

## 6. 기술적 고도화

### 기술적 의사결정
<details>
<summary><b>🔹 스터디룸 예약 동시성 제어</b></summary>
</details>

<details>
<summary><b>🔹 리뷰 조회 의사결정 과정</b></summary>
</details>

<details>
<summary><b>🔹 스케줄러를 관리하는 방법 의사결정 과정</b></summary>
### 배경
- dockerImage를 사용하여 서버를 증축한다고 할 때 스케줄러가 복제되는 문제가 생겨 동시성 제어 문제가 발생할거라 생각하게 됨
### 선택지
- 서버를 2개로 나누어 관리(한정적인 비용과 구현되어 있던 기능들이 많이 없었기 때문에 비효율적이라판단
- redisson 분산락을 사용하기(동시적으로 접근을 못하게 만들기 때문에 적합하다 판단)
### tryLock(),Lock() 중 Lock()을 선택한 이유
- tryLock()은 락을 걸어준 시간만큼 락이 걸려있다가 시간이 지나면 작업이 끝나지 않았어도 락이 해제되는 문제가 발생
- 따라서 Lock()메서드를 사용
- lock()메서드는 시간이 지났는데 작업이 끝나지 않았다면 락을 30초씩 연장해주고 작업이 끝나면 그때 락을 해제 이 때 다른 쓰레드가 접근하려한다면 블로킹하여 무한대기를 시킴
</details>

<details>
<summary><b>🔹 도서 반납 알림, 스터디룸 예약 알림 의사결정 과정</b></summary>
</details>

<details>
<summary><b>🔹 도서 대여/반납</b></summary>
</details>

<details>
<summary><b>🔹 도서 대여 예약</b></summary>
</details>

<details>
<summary><b>🔹 결제 시스템 도입</b></summary>
</details>

<details>
<summary><b>🔹 CI/CD 구축</b></summary>
</details>

<details>
<summary><b>🔹 메시징 큐 선택: RabbitMQ vs Kafka</b></summary>

### RabbitMQ 사용 목적
- 신뢰성 있는 메시지 전달이 주요 목적
- 개별 메시지의 안정적인 처리 보장
- 메시지 처리 실패에 대한 재처리 매커니즘 제공

### Kafka 사용 목적 
- 대용량 실시간 데이터 스트리밍 처리 목적
- 이벤트의 순서 보장 및 영구 저장
- 데이터 파이프라인, 로그 집계 등에 적합

### 프로젝트 상황
- 도서 대여/반납 알림 이메일 발송 필요
- 알림 실패시 재처리 필요
- 메시지 순서 보장 불필요

### 최종 선택: RabbitMQ
- 대용량 처리, 실시간 스트리밍 불필요
- 복잡한 클러스터 구성이 필요없음
- 메시지 전달의 신뢰성이 더 중요
</details>


### 트러블 슈팅
<details>
<summary><b>🔹 동시성 제어</b></summary>
</details>

<details>
<summary><b>🔹 문제 해결 과정</b></summary>
</details>

<details>
<summary><b>🔹 리뷰 조회를 하며 생겼던 트러블 슈팅</b></summary>
### 문제 인식
- QueryDsl로 작성한 리뷰 조회에서 bookId에 대한 조건을 넣어 실행시켰더니 NullPointerException 발생
### 해결방안
- `log`를 찍어 controller와 service코드에서 확인 결과 서비스 코드에서 에러 발생
- 코드 분석 결과 `reviewStar(int)=1`을 `래퍼클래스(Integer)=null`과 비교를하고 있어서 NullPointerException 발생
- 원시타입은 Null 값을 처리할 수 가없음 따라서 NullPointerException 발생
### 해결완료
- Null 값이 아닐 때 별점 값을 검증하는 로직이 실행될 수 있게 조치 후 애플리케이션 정상 작동
  
</details>

<details>
<summary><b>🔹 도서 반납 알림, 스터디룸 예약 알림 트러블 슈팅</b></summary>
</details>

<details>
<summary><b>🔹 Github .ENV 파일 노출 문제</b></summary>

### 문제 상황
- Github 커밋 로그에 환경설정 파일이 노출되는 문제 발생
- 최근 버전에는 gitignore에 추가되어 있었으나 로그 히스토리에 남아있는 문제 발견

### 해결 방안
- BFG Repo-Cleaner 도구 활용
- Git 저장소의 히스토리에서 민감한 정보 제거
- 명령어: `java -jar bfg.jar --delete-files .env`

### 조치 결과
- env 파일을 히스토리에서 완전히 제거
- 새로운 보안 정책 수립 및 적용
</details>

<details> 
<summary><b>🔹 게시글 캐싱 적용시 발생한 문제</b></summary>

### Redis 캐싱 시 Cache Miss 지속 발생
- 동일한 게시글 목록 요청에 대해 지속적인 Cache Miss 발생
- Redis 데이터 직렬화/역직렬화 과정에서 타입 정보 손실
- 캐시 데이터 구조 개선 및 직렬화/역직렬화 로직 최적화로 해결

### Page 객체의 불필요한 데이터로 인한 메모리 낭비
- Redis 메모리 사용량 지속적 증가
- Spring Data의 Page<T> 객체가 가진 여러 메타데이터 모두 저장
- 캐시용 DTO 최적화로 필요한 데이터만 선별하여 저장
</details>

## 7. 역할 분담 및 협업 방식
## 7. 역할 분담 및 협업 방식

| 팀원명 | 포지션 | 담당(개인별 기여점) | Github |
|------|--------|-------------------|---------|
| 여준서 | 팀장 | **▶ 리뷰**<br>- 리뷰 CRUD 기능 구현<br>- QueryDSL을 사용하여 리뷰 조회 구현<br>- 인덱싱 사용하여 쿼리 조회 속도 개선<br><br>**▶ 알림**<br>- 스케줄러를 사용해 구글 Gmail로 알림 송신 기능 구현<br>- Redisson 분산락을 사용해 스케줄러 동시성 제어 문제 방지<br>- AOP를 사용해 도서 대여 알림 구현<br>- 알림 방식을 동기에서 비동기로 변경하여 성능 개선 | [Github](https://github.com/duwnstj/) |
| 정원석 | 부팀장 | **▶ 도서 정보 관리**<br>- 도서 CRUD 기능 구현<br>- 도서 정보의 물리적, 메타적 데이터 분리<br><br>**▶ 도서 예약**<br>- 도서 예약 상태 관리<br>- 낙관적 락 및 특정 시간대 상태 변경 차단으로 인한 동시성 제어<br><br>**▶ 도서 검색**<br>- QueryDsl 기반 세부 필드값 입력 검색 기능 구현<br>- 엘라스틱 서치를 이용한 검색 구현<br><br>**▶ 태그 검색(예정)**<br>- 캐싱을 통한 빠른 태그 조회 | [Github](https://github.com/Aakaive) |
| 이동휘 | 팀원 | **▶ 유저**<br>- 회원가입/로그인<br>- 유저 CRUD<br>- Spring Security 필터<br>- @PreAuthorize를 활용한 권한관리<br>- Redis를 활용한 Refresh Token 관리<br>- OAUTH2.0을 이용한 카카오 로그인<br><br>**▶ 게시글**<br>- 게시글 CRUD<br>- 게시글 댓글 CRUD<br>- 권한에 따른 게시글 작성 권한 구분<br><br>**▶ 멤버십**<br>- 토스 페이를 활용한 멤버십 구현<br>- 멤버십 및 결제 내역 CRUD 구현<br><br>**▶ CI/CD**<br>- AWS Elastic Beanstalk 활용<br>- RDS를 통해 MySQL 사용<br>- ECR 활용해 이미지 관리<br>- ElastiCache 활용해 Redis 관리<br>- Github Actions를 활용한 배포 | [Github](https://github.com/webstrdy00) |
| 조성래 | 팀원 | **▶ 스터디룸 관리**<br>- 스터디룸 CRUD 기능 구현<br><br>**▶ 스터디룸 예약**<br>- 예약 시스템의 CRUD 및 예약 충돌 방지 로직 구현<br>- 예약 시스템 모듈화 및 사용자 편의성 강화<br><br>**▶ 동시성 제어**<br>- 낙관적 락과 Redis 분산락 혼용방식 도입<br>- Jmeter를 사용한 부하 테스트<br><br>**▶ Spring Batch**<br>- 데이터의 백업<br>- CSV 파일 추출 데이터 분석 및 보존 지원 | [Github](https://github.com/Sungrae-kogi) |

### **Ground Rule**

🍁 문제 발생 시 즉시 공유
- 문재 발생 시 팀원들에게 상황을 공유하여 협력하여 해결

🍁 문제 발생시 마감기한 이전에 상의해서 마감기한 맞추기

🍁 회의시간 의견 공유
- 회의 시간에 어떤 것을 진행할 예정인지 공유
- 코드리뷰 1주일에 1번이상을 꼭 하기

🍁 PR시 코드리뷰 1명이상 충족 시 merge

## 8. 성과 및 회고
### 잘된 점
- 성능 최적화를 통한 가시적인 개선 효과 달성
- 체계적인 문서화와 코드 리뷰 문화 정착

### 아쉬운 점
- 초기 설계 단계에서의 충분한 논의 부족
- 테스트 코드 커버리지 부족

### 향후 계획
1. **알림 기능 고도화**
  - 사용자 맞춤 언어로 알림 메일 송신 기능
  - 사용자가 직접 알림 시간 설정 가능하도록 개선
  - 알림 종류와 빈도 사용자 맞춤화

2. **백업 데이터 복구 기능 구현**
  - 데이터 손실을 최소화하는 시스템 구축
  - 백업 데이터를 활용한 신속한 복구 시스템 구현
  - 주기적인 데이터 백업 자동화

3. **ECS/EKS 도입**
  - MSA 전환을 위한 컨테이너 오케스트레이션 도입
  - 여러 독립적인 서비스의 효율적 관리
  - 자동 스케일링 및 로드밸런싱 구현
  - 서비스 모니터링 및 로깅 시스템 구축
