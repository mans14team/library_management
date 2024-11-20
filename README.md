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

### 트러블 슈팅
<details>
<summary><b>🔹 동시성 제어</b></summary>
</details>

<details>
<summary><b>🔹 문제 해결 과정</b></summary>
</details>

<details>
<summary><b>🔹 리뷰 조회를 하며 생겼던 트러블 슈팅</b></summary>
</details>

<details>
<summary><b>🔹 도서 반납 알림, 스터디룸 예약 알림 트러블 슈팅</b></summary>
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
- 테스트 자동화 강화
- MSA 아키텍처 전환 검토
