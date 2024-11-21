# 📚 LibMate - 도서관 웹 서비스
![LibMate_Project_300x150](https://github.com/user-attachments/assets/e5cf22bd-17cb-423a-92c8-d138338b3639)

## 🏫 1. 프로젝트 소개
**LibMate**는 도서 대여와 스터디룸 예약 관리를 위한 웹 서비스로, **동시성 제어**와 **실시간 알림**을 통해 효율적인 도서관 자원 관리를 제공합니다.

- **개발 기간**: ⏳ *2024.10.21 ~ 2024.11.22*  
- **팀원 수**: 👥 *4명*

## ✅ 2. 프로젝트 핵심 목표
- **📈 안정적인 시스템**: 대규모 트래픽 환경에서 **도서 예약/대여 시스템** 구축  
- **💳 결제 기능**: Toss API를 활용한 **멤버십 결제 시스템** 구현  
- **🔔 실시간 알림**: 예약 상태 및 대여 기간 알림 제공  
- **🔒 동시성 제어**: Redis 및 낙관적 락을 혼합하여 **데이터 정합성 보장**  
- **⚡ 성능 최적화**: 성능 최적화 및 **모니터링 시스템** 구축 
- **🔧 CI/CD 구축**: GitHub Actions 및 AWS Elastic Beanstalk을 활용한 **자동화 배포**

## 📌 3. KEY SUMMARY
### 🔷 성능 개선
- 메시지 송신 방법 동기-> 비동기방식으로 변경
### 개선 전
![image](https://github.com/user-attachments/assets/17353b6d-6ff4-4e3e-9b07-e3c7f6a0d4ee)

### 개선 후
![image](https://github.com/user-attachments/assets/3b6d6462-64b7-4863-8add-52ce03b27583)

- 응답 시간이 크게 줄어들어 성능이 약 10배 개선된 것을 확인 할 수 있었습니다.
### 🔧 기술적 성과
-  **Redis**를 활용한 캐싱 시스템 구현
-  **AWS 인프라** 기반 자동화된 배포 환경 구축
-  **JMeter**를 통한 성능 테스트 및 최적화
-  **RabbitMQ** 활용한 실시간 알림 시스템 구현

## 🏗️ 4. 인프라 아키텍처 & 적용 기술

### Backend
- ![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)  
- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.0-6DB33F?logo=springboot&logoColor=white)  
- ![Spring Security](https://img.shields.io/badge/Spring%20Security-Active-6DB33F?logo=springsecurity&logoColor=white)  
- ![Spring Batch](https://img.shields.io/badge/Spring%20Batch-Automation-6DB33F?logo=spring&logoColor=white)  
- ![JPA](https://img.shields.io/badge/JPA-Persistence-6DB33F?logo=hibernate&logoColor=white)  
- ![JWT](https://img.shields.io/badge/JWT-Security-000000?logo=jsonwebtokens&logoColor=white)  
- ![Gradle](https://img.shields.io/badge/Gradle-Build%20Tool-02303A?logo=gradle&logoColor=white)  

### Database & Cache
- ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)  
- ![Redis](https://img.shields.io/badge/Redis-In%20Memory-DC382D?logo=redis&logoColor=white)  

### DevOps
- ![AWS](https://img.shields.io/badge/AWS-Elastic%20Beanstalk%2C%20RDS%2C%20ECR%2C%20ElastiCache-232F3E?logo=amazonaws&logoColor=white)  
- ![Docker](https://img.shields.io/badge/Docker-Containerization-2496ED?logo=docker&logoColor=white)  
- ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?logo=githubactions&logoColor=white)  

### Cooperation
- ![GitHub](https://img.shields.io/badge/GitHub-Version%20Control-181717?logo=github&logoColor=white)  
- ![Notion](https://img.shields.io/badge/Notion-Documentation-000000?logo=notion&logoColor=white)  
- ![Slack](https://img.shields.io/badge/Slack-Communication-4A154B?logo=slack&logoColor=white)  

### Rest API
- ![Toss Payment API](https://img.shields.io/badge/Toss%20Payment%20API-Payment-0055FF?logo=toss&logoColor=white)  
- ![Kakao Login](https://img.shields.io/badge/Kakao%20Login-Authentication-FFCD00?logo=kakao&logoColor=black) 

### 인프라 아키텍쳐
![image (4)](https://github.com/user-attachments/assets/b82c06a6-8f24-4779-aab9-478f2438d440)


## 🌟 5. 주요 기능
### 도서 대여/반납 및 예약 
### 스터디룸 예약
### 리뷰 기능
### 게시글 작성
### 멤버십 시스템
### 실시간 알림

## 🧠 6. 기술적 고도화

### 기술적 의사결정
<div markdown="1">
  <ul>
    <details>
<summary><b>🔹 스터디룸 예약 동시성 제어</b></summary>

  ### [ 배경 ]
  스터디룸 예약 시스템에서 동시성 문제는 중요한 요소입니다. 특히 대학교 도서관에서 중간·기말고사 기간에는 예약 경쟁이 심화되고 트래픽이 증가하는 특성을 고려하여, **시험기간**과 **비시험기간**에 맞는 동시성 제어 방식을 설계했습니다.


## 📝 요구사항

- **시험기간**: 트래픽이 높은 시기에는 강력한 동시성 제어가 필요합니다.  
- **비시험기간**: 트래픽이 낮은 시기에는 가벼운 동시성 제어가 적합합니다.  
- 하나의 시스템으로 **시험기간**과 **비시험기간** 모두 유연하게 대응해야 합니다.

## 🔧 선택지

- **비관적 락**  
- **낙관적 락**  
- **Redis 기반의 분산 락**

## ✅ 의사결정 및 이유

낙관적 락과 Redis 기반의 분산 락을 혼용하여 동시성 제어를 구현했습니다.

- **비시험기간**: 낙관적 락을 기본으로 적용하여 가볍고 효율적인 동시성 제어를 수행합니다.  
- **시험기간**: 트래픽이 높은 시기에는 Redis 분산 락을 추가적으로 적용하여 예약 요청을 **순차적**으로 처리합니다.  

## 💡 **시험기간 판단 로직**:  
- 예약 요청 시간(`LocalDateTime`)에서 **월/일 데이터를 추출**합니다.  
- 일정표에 따라 시험기간 여부를 판단한 후, Redis 락 사용 여부를 동적으로 결정합니다.  
- Redis 락은 시험기간에만 활성화되며, 이외에는 낙관적 락만 사용합니다.

</details>

<details>
<summary><b>🔹 리뷰 조회 의사결정 과정</b></summary>

### 배경
  리뷰를 조회를 하려할 때 어떤 조회 목록이 필요할지 먼저 고민 후 구현해보았습니다.
### 📝요구사항
- 로그인한 유저가 작성한 리뷰를 조회하고 싶을 때
- 책에 대한 리뷰 전체를 조회하고 싶을때
- 별점에 해당하는 책을 찾아보고 싶을 때
- 모든 리뷰를 검색하고 싶을때
### 🔧선택지
첫번째는 필요한 조회별로 API를 여러개 만들기였고
두번째는 JPQL을 사용하여 쿼리를 하나의 API로 여러개의 조건을 해결하는방법
세번째는 QueryDSL을 사용하여 하나의 API로 여러개의 조건을 해결하는 방법이 있었습니다.
### 의사결정 사유
- 이 중 첫번째 방법으로 구현을 할 필요 없이 JPQL이나 QueryDSL로 동적인 쿼리로 해결해야겠다 생각을 하게 되었고, 두번째 방법인 JPQL을 사용하게 되면 가독성이 떨어질수도 있겠다 생각이 들어서 세번째 방법인 QueryDSL을 사용해 구현을 해야겠다 생각을 했습니다.
- 따라서 RequestParam으로 받는 매개변수의 값들을 해당 조건에 맞게 설정을 해서 동적 쿼리로 문제를 해결하게 되었습니다.
- 로그인한 유저가 작성한 리뷰를 조회하고 싶을 때도 같이 QueryDSL로 해결하려 했지만 SpringSecurity를 통해 이미 인증/인가 처리를 하고 난 후여서 user가 null일 수가 없는 걸 알게 되었고 따라서 두개의 API로 나눠 구현을 했습니다.
</details>

<details>
<summary><b>🔹 스케줄러를 관리하는 방법 의사결정 과정</b></summary>
  
### 배경
- dockerImage를 사용하여 서버를 증축한다고 할 때 스케줄러가 복제되는 문제가 생겨 동시성 제어 문제가 발생할거라 생각하게 됨
### 🔧선택지
- 서버를 2개로 나누어 관리(한정적인 비용과 구현되어 있던 기능들이 많이 없었기 때문에 비효율적이라판단
- redisson 분산락을 사용하기(동시적으로 접근을 못하게 만들기 때문에 적합하다 판단)
### ✅tryLock(),Lock() 중 Lock()을 선택한 이유
- tryLock()은 락을 걸어준 시간만큼 락이 걸려있다가 시간이 지나면 작업이 끝나지 않았어도 락이 해제되는 문제가 발생
- 따라서 Lock()메서드를 사용
- lock()메서드는 시간이 지났는데 작업이 끝나지 않았다면 락을 30초씩 연장해주고 작업이 끝나면 그때 락을 해제 이 때 다른 쓰레드가 접근하려한다면 블로킹하여 무한대기를 시킴
</details>

<details>
<summary><b>🔹 도서 반납 알림, 스터디룸 예약 알림 의사결정 과정</b></summary>

## [ 배경 ]
- 도서관에서 어떤 알림을 보내줘야할까 고민 후 SA를 작성을 했습니다 

## 📝 요구사항
- 책 반납일 1일전 , 3일전 , 당일날에 알림이 해당 유저에게 발송되어야함
- 스터디룸 예약일 1일전 , 당일날에 알림이 해당 유저에게 발송되어야함

## 🔧 선택지
- 자동으로 알람을 보내야했기 때문에 선택지는 스케줄러를 사용하는 방법 밖에는 없었습니다.

## ✅ 의사결정 및 이유
- AOP로 구현은 특정메서드나 클래스를 타겟팅하여 메서드가 실행이 되었을때 aop가 실행이 되기 때문에 적합하지 않다 판단
- 스케줄러를 사용해 특정한 시간에 매일 한번씩 자동으로 보내주는로직이 맞다 판단
- 사용자의 편의성을 높이기 위해 구글 Gmail로 메일 송신하는 방법 선택
- 스케줄러를 redisson 분산 락을 사용하여 동시성 제어 문제 방지

</details>

<details>
<summary><b>🔹 도서 대여/반납</b></summary>

## [ 배경 ]
- 도서관 웹 서비스 프로젝트를 본 프로젝트의 목표로 설정하였다. 프로젝트 목표를 정한 이후에는, 해당 도메인에 필수적으로 들어가야 할 요소들을 열거하여, 해당 요소들을 통해 제공할 수 있는 서비스를 모색해보았다.
- 도서관에 필수적으로 필요한 전산 정보로는 [메타적 도서 정보], [물리적 도서 정보] 등이 존재하고, 이 중 물리적 도서 정보를 통해 제공할 수 있는 서비스 중 ‘대여/반납’이라는 기초적인 서비스를 포커싱하여 서비스를 구현해보았다.

## 📝 요구사항
- 물리적 객체와 해당 객체를 이용할 회원 정보를 포함한 요청을 통해 ‘대여’ 기록을 생성한다.
- 대여 기록 생성 시 대여 상태는 ‘대여 중’으로 설정하며, 대여 시점에 ‘대여일자’를 저장한다.
- ‘대여일자’로부터 7일이 지날 경우 자동으로 ‘연체’ 상태로 전환된다.
- 대여 상태가 ‘대여 중’일 때, ‘반납’ 요청을 통해 대여 상태를 ‘반납’으로 전환한다.

## 🔧 선택지
- ‘대여/반납’을 위한 ‘도서 정보’에 관하여 메타적 데이터와 물리적 데이터를 결합할 것인지, 아니면 서로 분리하여 연관관계를 설정할 것인지에 대한 논의가 있었다.

## ✅ 의사결정 및 이유
- 도서 정보의 메타적 데이터와 물리적 데이터를 분리하여 별도의 엔티티로 구현하였다.
- 도서관에서 관리되는 서적의 경우, 같은 종류의 서적이 하나의 물리적 객체를 가지는 케이스만 존재하는 것이 아닐 뿐더러, 물리적 객체마다 별도의 ID를 부여하여 관리하기 때문이다.
- 또한, 물리적 객체의 상태제어를 통해 대여/반납 시에 필요한 ‘상태 정보’를 취득하기에 유리하다.

</details>

<details>
<summary><b>🔹 도서 대여 예약</b></summary>

## [ 배경 ]
- 도서관 웹 서비스를 구현하며, 본 프로젝트에 적용하고 싶은 기술 범위에 대해 논의해 본 결과 ‘비즈니스 모델’을 도입하여 결제API를 통한 결제 기능을 추가하고 싶다는 의견이 제기되었다.
- 해당 의견을 반영하여 논의를 진행하여, 해당 기능 도입을 통해 발생할 수 있는 시나리오를 상정해보았고 그 결과 일반회원과 멤버쉽 회원을 설정하여 서비스에 차등을 두는 비즈니스 모델을 선택하여 도입하기로 결정하였다.
- 해당 비즈니스 모델에 대한 어드밴티지의 일부로 ‘스터디룸 대여 예약’이나 ‘도서 대여 예약’ 기능 등이 제안되었다.

## 📝 요구사항
- 도서 대여 예약 시 ‘현재 대여 가능한 도서’만 예약 대상으로 선택할 수 있다.
- ‘메타적 데이터’를 통하여 예약을 원하는 도서를 선택하고, 해당 메타적 데이터를 공유하는 ‘물리적 객체’ 중 현재 대여 가능 상태인 도서를 예약 대상으로 지정한다.
- 도서 대여 예약 등록 시 예약일자를 기록하며, 대여 예약 상태를 ‘진행중’으로 설정한다.
- 도서 대여 예약일자로부터 3일이 초과한 도서 대여 예약 기록의 상태가 ‘진행중’일 경우 자동으로 ‘대여 예약 만료’상태로 전환한다.
- 도서 대여 시, 해당 유저의 도서 대여 예약 내역 중 ‘진행중’인 예약 내역을 조회하고, 현재 대여하려는 도서와 같은 물리적 데이터를 가진 책을 대여할 경우, 예약 내역을 ‘완료’로 전환한다.
- 만약 대여 하려는 도서가, 대여 예약 진행중인 도서와 같은 메타적 데이터를 가졌지만, 다른 물리적 데이터를 가졌을 경우, 해당 대여 요청을 반려하고, 예약 중인 물리적 객체에 대한 정보를 제공한다.

## 🔧 선택지
- 대여 예약 시, 물리적 객체에 대해 전산 상으로 생성되는 ‘데이터’를 통해 접근하기 때문에 발생할 수 있는 동시성에 대한 의사결정 사항이 존재하였다.

## ✅ 의사결정 및 이유
- 첫 번째로, 같은 도서에 대해 여러 사용자가 동시에 대여 예약을 신청할 경우, 해당 요청에 대한 처리방식이라는 논제에 대하여, 락을 통한 요청 처리를 결정하였다.
- 이슈 발생 시나리오의 경우, 도서관이라는 특정한 장소에 존재하는 물리적 공간에 방문할 수 있는 유저에 한하여 해당 서비스를 이용할 것이라는 전제를 설정하였다.
- 해당 전제에 입각하여, 발생할 수 있는 동시성 이슈의 빈도나 그 정도가 과도하게 나타나지는 않을 것이라 가정하여 락의 설정 방식을 고려하였고, 그 결과 ‘낙관적 락’을 통한 동시성 처리가 결정되었다
- 두 번째로, 대여 예약 만료로 인한 ‘특정 시간대에 발생하는 일괄적 상태 변경’에 대해서는, 일정한 시간에 발생하는 이벤트임을 고려하여 ‘해당 시간대에 발생하는 물리적 객체의 상태 변화’에 대한 요청을 일시 차단하는 것으로 결정하였다.

</details>

<details>
<summary><b>🔹 결제 시스템 도입</b></summary>
  
## [ 배경 ]
- 도서관 관리 프로그램에서 멤버십 결제를 위해 결제 기능 구현 필요
- 결제 데이터의 안전한 처리와 관리가 필요

## 📝 요구사항
- 안정적인 결제 시스템 구축
- 쉬운 구현 환경 구축
- 거래 취소 및 환불 프로세스 지원
- 테스트하기 쉬운 환경 제공

## 🔧 선택지
### 1. 토스 페이먼츠
#### 장점
 - 국내 환경 최적화
 - 쉬운 연동
 - 안정적인 운영
 - 높은 보안 신뢰도 (PCI-DSS 인증)
#### 단점
 - 국내 서비스 중심
 - 일부 해외 카드 결제 제한

### 2. 아임포트
#### 장점
 - 다양한 PG사 연동 가능
 - 글로벌 결제 지원
 - 자동 결제수단 최적화
#### 단점
 - 상대적으로 높은 비용
 - 연동 PG사 마다 정책이 다름

### 3. 직접 PG사 연동
#### 장점
 - 커스터마이징 자유도 높음
#### 단점
 - 개발 리소스 많이 필요
 - 다양한 결제 수단 적용 힘듦
 - 결제 FLOW가 길어짐

## ✅ 의사결정 및 이유
**1. 기술적 측면**
- RESTful API 제공으로 Spring Boot 와의 연동 용이
- 상세한 한국어 개발 문서 제공
- 테스트 환경 제공으로 개발 안전성 확보
- PCI-DSS 인증으로 보안 요구사항 충족

**2. 비지니스 측면**
- 결제 성공률이 높음
- 초기 구축 비용과 시간 최소화
- 고객 신뢰도가 높은 브랜드

**3. 사용자 경험**
- 간편한 결제 프로세스
</details>

<details>
<summary><b>🔹 CI/CD 구축</b></summary>

## [ 배경 ]
- 안정적인 배포 환경 구축 필요
- 개발 및 배포 프로세스 자동화 요구
- Mysql, Redis 등 다양한 서비스 연동 필요

## 📝 요구사항
**1. 인프라 요구사항**
- 다중 서버 환경 구성
- 로드밸런싱을 통한 트래픽 분산
- Rdis 서버 구성
- 데이터베이스 안정적 운영

**2. 배포 요구사항**
- 자동화된 빌드 및 배포 프로세스
- 무중단 배포 지원
- 배포 실패시 롤백 기능

**3. 보안 요구사항**
- 환경 변수 및 시크릿 정보 보호
- 서버 접근 제한

## 🔧 선택지
<details>
<summary><b>1. 인프라 및 배포 환경 구성 방식</b></summary>

### EC2 + Docker Compose 구성
#### 장점
 - 구성이 단순하고 직관적
 - 로컬 개발 환경과의 일관성
 - 완전한 커스터마이징 가능
 - 초기 구축 비용 최소화
  
#### 단점
 - 스케일링 시 관리 어려움
 - 수동 운영 부담 증가
 - 무중단 배포 구현 복잡
 - 고가용성 구성의 한계

### ECS/EKS 기반 구성
#### 장점
 - 높은 확장성과 가용성
 - 선언적 인프라 관리
 - 자동 스케일링 지원
  
#### 단점
 - 초기 설정 복잡
 - 높은 학습 곡선
 - 운영 비용 증가
 - 운영 전문성 필요

### Elastic Beanstalk 활용
#### 장점
 - 인프라 자동 관리
 - 배포 자동화 용이
 - 블루/그린 배포 기본 지원
 - AWS 서비스 통합 용이
 - 모니터링 도구 기본 제공
 - Docker 환경 기본 지원
  
#### 단점
 - AWS 서비스 종속성
 - 플랫폼 제약 존재
 - 세부 설정의 한계
 - 고급 커스터마이징 제한
</details>

<details>
<summary><b>2. CI/CD 도구 선택</b></summary>

### Jenkins
#### 장점
 - 풍부한 플러그인
 - 커스터마이징 자유도
 - 온프레미스 구축 가능
  
#### 단점
 - 관리 부담 큼
 - 초기 설정 복잡
 - 서버 자원 필요

### Github Actions
#### 장점
 - Github 통합 용이
 - 클라우드 기반 실행
 - 간편한 구성
 - 무료 tier 제공
  
#### 단점
 - Github 종속성
 - 실행 시간 제한
 - 복잡한 파이프라인 구성의 한계

### AWS CodePipeline
#### 장점
 - AWS 서비스 통합
 - 관리형 서비스
 - 높은 확장성

#### 단점
 - 비용 발생
 - AWS 종속성
 - 타 서비스 연동의 제약
</details>

## ✅ 의사결정 및 이유

<details>
<summary><b>1. 인프라 및 배포 환경 결정: AWS Elastic Beanstalk</b></summary>

#### 선택 이유
- 인프라 자동 프로비저닝으로 운영 부담 감소
- 내장된 로드 밸런싱 및 오토스케일링 기능
- 환경 구성의 버전 관리 용이
- 블루/그린 배포 기본 지원으로 무중단 배포 구현 간편
- Docker 환경 기본 지원
- 모니터링 및 로깅 통합 제공
</details>

<details>
<summary><b>2. CI/CD 파이프라인: Github Actions</b></summary>

#### 선택 이유
- Elastic Beanstalk과의 원활한 통합
- Github 저장소와의 긴밀한 연계
- YAML 기반의 직관적인 워크플로우 구성
- 다양한 marketplace actions 활용 가능
</details>

<details>
<summary><b>3. 데이터 계층 구성</b></summary>

#### RDS 선택 (MySQL)
- 관리 용이성
- 백업 및 복구 기능

#### ElastiCache (Redis)
- 관리형 서비스로 운영 부담 감소
- 자동 백업 및 복구 기능
- 고가용성 구성 용이 (Multi-AZ)
- Auto Scaling 지원
</details>

</details>


<details>
<summary><b>🔹 메시징 큐 선택: RabbitMQ vs Kafka</b></summary>

## [ 배경 ]
- 도서 대여/반납 시 안정적인 알림 전송 필요
- 메시지 실패 시 재처리 방안 필요
- 시스템 간 비동기 통신 구현 필요

## 📝 요구사항
- 신뢰성 있는 메시지 전달
- 실패한 메시지의 재처리 보장
- 시스템 부하 분산
- 관리의 용이성

## 🔧 선택지

### 1. RabbitMQ
#### 장점
 - 메시지 전달 신뢰성 보장
 - 개별 메시지 처리 추적 용이
 - Dead Letter Queue를 통한 실패 처리
 - 간단한 설정과 관리
 - 다양한 메시징 패턴 지원
#### 단점
 - 대용량 처리 시 성능 제한
 - 확장성이 상대적으로 제한적

### 2. Kafka
#### 장점
 - 높은 처리량과 확장성
 - 영구적인 메시지 저장
 - 순서 보장
 - 데이터 파이프라인 구성 용이
#### 단점
 - 복잡한 클러스터 구성
 - 개별 메시지 추적 어려움
 - 운영 복잡도가 높음

## ✅ 의사결정 및 이유

### RabbitMQ 선택
- 도서관 시스템의 메시지 처리량이 상대적으로 적음
- 개별 알림의 신뢰성 있는 전달이 중요
- Dead Letter Queue를 통한 실패 메시지 관리 용이
- 운영 복잡도가 낮고 관리가 용이
- 메시지 순서 보장이 필수적이지 않음
  
</details>
  </ul>
</div>




### 트러블 슈팅
<div markdown="1">
  <ul>
    <details>
    <summary><b>🔹 스터디룸 예약 생성 시스템의 동시성 제어 문제</b></summary>
        
  ## [ 문제 인식 ]
  스터디룸 예약 시스템은 트래픽이 몰린 상황에서도 **단 하나의 예약만 정확히 처리**되어야 합니다. 그러나 JMeter를 사용한 부하 테스트에서 **동시에 두 개의 예약 요청이 처리되는 문제**가 발견되었습니다.
  이는 시스템의 신뢰성을 저하시킬 수 있는 중요한 문제였습니다.

  ### 🧪 문제가 발생한 테스트 결과

  ![image](https://github.com/user-attachments/assets/094ac8f2-06d3-4584-a1e2-ac9629f2a4ab)

---

## [ 문제 원인 ]
- **문제가 발생한 코드**
    ```java
    lock.tryLock(3, TimeUnit.SECONDS);
    ```
- **원인 분석**
    - 위 코드에서 **첫 번째 요청**이 락을 획득해 작업을 수행하는 동안, **두 번째 요청**은 락 대기 시간(최대 3초) 내에 락을 획득할 가능성이 있었습니다.
    - 이로 인해 동시성 문제로 **최대 2개의 예약 요청이 동시에 처리**되는 상황이 발생했습니다.

---

## [ 해결 방법 및 개선 사항 ]
- **수정 내용**
    ```java
    lock.tryLock(3, TimeUnit.SECONDS) → lock.tryLock(0, TimeUnit.SECONDS);
    ```
    - 락 대기 시간을 **0초**로 설정하여 첫 번째 요청 이후 모든 요청이 **즉시 실패**하도록 수정했습니다.
- **결과**
    - 개선 후에는 **단 한 개의 예약 요청만 처리**되도록 동시성 문제가 해결되었습니다.

### ✅ 문제를 해결한 테스트 결과

![image](https://github.com/user-attachments/assets/6924d040-4bb4-4687-becd-48cf9dc4a53c)
<br>
    </details>
  <details>
    <summary><b>🔹 Jmeter를 사용한 부하 테스트간 동시성 제어 문제</b></summary>

  ### [ 문제 정의 ]
  - **상황**: 대용량 트래픽 상황에서 예약 생성 메소드에 트랜잭션이 적용된 상태에서, 데이터 저장보다 락 해제가 먼저 발생하는 문제가 발견되었습니다.  
  - **결과**: 동일 시간대에 여러 요청이 동시에 처리되며, 하나의 예약만 처리되어야 하는 시스템 요구사항을 충족하지 못했습니다.

  ### [ 문제 원인 ]
  - `@Transactional`이 메소드 전체에 적용되어, 예약 정보가 DB에 저장되기 전에 `lock.unlock()`이 호출되었습니다.
  - 이로 인해 트랜잭션 범위 내에서 락이 의도한 대로 동작하지 않아 다른 요청들이 락이 해제된 것으로 간주되어 중복 처리가 발생했습니다.

  ### [ 해결 방안 ]
  - **의사결정**: 트랜잭션 종료 시점과 락 해제 시점을 일치시키기 위해 `@Transactional` 어노테이션을 제거하였습니다.
  - **해결 과정**:
    1. `createRoomReserve` 메소드에서 `@Transactional`을 제거.
    2. 락 해제와 데이터 저장이 순차적으로 이루어지도록 수정.
    3. 수정 후 JMeter 부하 테스트를 실행하여 중복 예약이 발생하지 않는 것을 확인.

  ### [ 개선된 결과 ]
  - **수정 전**: 1000개의 트래픽 중 2개의 요청이 동시에 처리됨.
  - **수정 후**: 1000개의 트래픽 중 1개의 예약만 처리되며, 중복 요청이 발생하지 않음.
  - **트래픽 테스트 결과 비교**:
    - **수정 전**  
      ![image](https://github.com/user-attachments/assets/a7f82fd0-8a29-4977-82dc-bc0119aee7d5)

    - **수정 후**  
      ![image](https://github.com/user-attachments/assets/33719753-6463-4584-86c2-9101fb1c4b8d)


  ### [ 개선된 실행 흐름 ]
  - **여러 스레드에서 락 획득을 요청**: 4번 스레드가 락을 획득.
    ![image](https://github.com/user-attachments/assets/978fdf58-6af4-4d38-88ca-3a06772edffe)

  - **락을 획득한 스레드가 예약을 저장하고 락 해제**: insert문 이후 락 해제 로그가 출력되며 의도한 대로 동작.
    ![image](https://github.com/user-attachments/assets/99bb4ba7-9593-4180-b7e6-807dd8e20649)


  ### [ 느낀점 ]
  - 트랜잭션과 락의 상호작용이 대용량 트래픽에서 동시성 제어에 미치는 영향을 명확히 이해하게 되었습니다.
  - 트랜잭션 적용 방식에 따라 락 해제 시점이 영향을 받을 수 있음을 고려하며, 향후 테스트 범위를 확대하고, 시스템 신뢰성을 높이는 설계가 필요하다고 느꼈습니다.

</details>
  
<details>
<summary><b>🔹 문제 해결 과정</b></summary>

  ### [성능 개선 / 코드 개선 요약]
  - 다대다 연관관계를 가진 엔티티 등록 시 별도 엔티티의 ‘고유성’을 점검하기 위해 발생하는 쿼리 과다 발생 문제를 캐싱을 통해 해결하는 과정에서 발생한 영속화 문제
  - 재실행 시 캐시에 저장된 엔티티들에 대한 비영속화 문제

 ### [문제 정의]
 - A 엔티티 등록 시, 다대다 연관관계인 B 엔티티의 고유성을 점검하는 과정에서 캐시를 먼저 조회함으로써 쿼리를 생략하고 해당하는 B 엔티티를 가져 올 경우, A 엔티티를 등록하는 과정에서 hibernate가 거부되는 문제 발생

### [가설]
- 스프링부트 어플리케이션 재실행 시 레디스에 저장된 엔티티들이 비영속화 되면서, 비영속화 된 엔티티를 통한 새로운 엔티티 등록을 hibernate가 거부함.
- 레디스의 캐시에 저장된 엔티티와 JPA 영속성 컨텍스트는 별도의 영속성 컨텍스트에서 관리되어, 캐시의 엔티티를 가져올 경우 비영속 상태이기 때문에 hibernate 동작에 에러가 발생함.

### [해결 방안]
- 레디스의 캐시에 저장된 엔티티가 JPA 영속성 컨텍스트에 영속화된 엔티티와 다른 영속성을 가진다는 것을 파악함.
- 해당 문제에 대해, 캐시에 저장된 엔티티를 영속화하려고 할 경우, 결국엔 쿼리가 발생하므로 해당 문제에서의 ‘쿼리 최소화’가 무의미해지는 것을 파악함.

### [해결 과정]
- 캐시와 JPA 영속성 컨텍스트에서의 영속화 문제를 파악하고, 해당 쿼리 최소화 이슈에 있어서 캐시 처리가 무의미하다는 것을 인지.
- 해당 이슈에 대해 캐시처리가 아닌 별도의 방식을 사용하여 쿼리 최소화 방식을 모색함.

### [해결 완료]
- 다대다 연관관계를 가진 엔티티 등록 시 쿼리 최소화 방식에 대하여 캐시 처리를 배제하고, 다른 방식을 선택함.
#### 캐시 대신 Batch 혹은 지연 로딩 방식을 사용하여 문제를 해결한다.
- Batch - 대용량 처리에 유리하지만 메모리 과다 사용 등의 문제가 발생할 우려가 있음.
- 지연로딩 - N+1 문제가 발생할 수 있기 때문에 해당 방식 사용 시 fetch join 등을 통해 한번의 쿼리로 해결할 수 있도록 한다.
  
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

### [문제 인식]
-스케줄러 기능을 구현을 한 후 구글 이메일에 잘 보내지는지 테스트 하기 위해 10초마다 알림을 보내는 스케줄러를 만들어 보내봤습니다. 하지만 기대한 값은 반납일 1일전 ,3일전 ,당일 총 3개의 메시지가 보내지는것이였는데 확인을 해보니 2개의 메시지만 전송된것을 Gmail에서 확인할 수 있었습니다.

### [해결 방안]
-문제를 해결하기 위해 반납일 1일전 , 3일전 , 당일을 한번에 조회해오는 리스트의 사이즈를 확인해봤습니다. 확인을 해보니 사이즈가 2개가 나온것을 알 수 있었고 이 쿼리에서 잘못되었다는것을 알게 되었습니다. 
- 따라서 그 쿼리메서드를 호출하는 메서드를 확인해보니 문제를 찾을 수 있었습니다.

### [해결 과정]
- 문제는 가져오는 시간의 범위에 문제가 있었습니다. 먼저 조회 쿼리를 해오는 쿼리에서 시간의 범위를 log를 찍어 확인을 했습니다.
- 코드가 현재 시간을 기준으로 반납일을 계산하는 식으로 코드가 짜여있었는데 타입이 LocalDateTime이였습니다. 이 LocalDateTime 타입은 년,월,일,시,분,초를 계산할 수 있기 때문에 날짜는 일치하지만 현재 시간에 따라 달라지기 때문에 누락되는 알림 정보가 있었던 것입니다.

### [해결 완료]
- 따라서 LocalDate로 변경 한 후 자정값을 표현하는 atStartOfday()메서드를 사용해 시작날을 정해주고 atTime(LocalDate.Max)메서드를 사용해 endDate를 정해주었습니다.
- 결과 : 그 후 스케줄러를 실행시켜 list의 사이즈를 확인해보니 3으로 원하는 값이 잘 담겼고 구글 Gmail에서 3개의 메시지가 잘 담겨있는것을 확인할 수 있었습니다.

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

### [문제 인식]
**1. Cache Miss 지속 발생**
  - 동일 게시글 목록 요청에도 캐시 미적용
  - 불필요한 DB 조회 발생
    
**2. Redis 메모리 사용량 증가**
  - 캐시 데이터 크기 비정상적 증가
  - 불필요한 메타데이터 저장

### [원인 분석]
**1. Cache Miss 원인**
  - Redis 데이터 직렬화/역직렬화 과정의 타입 정보 손실
  - 캐시 키 생성 로직 문제
    
**2. 메모리 사용량 증가 원인**
  - Page<T> 객체의 모든 메타데이터가 캐시에 저장
  - 불필요한 데이터 포함

### [해결 과정]
**1. 캐시 데이터 구조 개선**
  - 직렬화/역직렬화 로직 최적화
  - 캐시 키 생성 방식 수정
    
**2. 캐시용 DTO 설계**
  - 필요한 데이터만 선별하여 저장
  - 메타데이터 최소화

### [결과]
- Cache Hit Rate 개선
- Redis 메모리 사용량 최적화
- 캐시 성능 향상

</details>
  </ul>
</div>



## 🧑‍🤝‍🧑 7. 역할 분담 및 협업 방식

| 팀원명 | 포지션 | 담당(개인별 기여점) | Github |
|------|--------|-------------------|---------|
| 여준서 | 팀장 | **▶ 리뷰**<br>- 리뷰 CRUD 기능 구현<br>- QueryDSL을 사용하여 리뷰 조회 구현<br>- 인덱싱 사용하여 쿼리 조회 속도 개선<br><br>**▶ 알림**<br>- 스케줄러를 사용해 구글 Gmail로 알림 송신 기능 구현<br>- Redisson 분산락을 사용해 스케줄러 동시성 제어 문제 방지<br>- AOP를 사용해 도서 대여 알림 구현<br>- 알림 방식을 동기에서 비동기로 변경하여 성능 개선 | [Github](https://github.com/duwnstj/) |
| 정원석 | 부팀장 | **▶ 도서 정보 관리**<br>- 도서 CRUD 기능 구현<br>- 도서 정보의 물리적, 메타적 데이터 분리<br><br>**▶ 도서 예약**<br>- 도서 예약 상태 관리<br>- 낙관적 락 및 특정 시간대 상태 변경 차단으로 인한 동시성 제어<br><br>**▶ 도서 검색**<br>- QueryDsl 기반 세부 필드값 입력 검색 기능 구현<br>- 엘라스틱 서치를 이용한 검색 구현<br><br>**▶ 태그 검색(예정)**<br>- 캐싱을 통한 빠른 태그 조회 | [Github](https://github.com/Aakaive) |
| 이동휘 | 팀원 | **▶ 유저**<br>- 회원가입/로그인<br>- 유저 CRUD<br>- Spring Security 필터<br>- @PreAuthorize를 활용한 권한관리<br>- Redis를 활용한 Refresh Token 관리<br>- OAUTH2.0을 이용한 카카오 로그인<br><br>**▶ 게시글**<br>- 게시글 CRUD<br>- 게시글 댓글 CRUD<br>- 권한에 따른 게시글 작성 권한 구분<br><br>**▶ 멤버십**<br>- 토스 페이를 활용한 멤버십 구현<br>- 멤버십 및 결제 내역 CRUD 구현<br><br>**▶ CI/CD**<br>- AWS Elastic Beanstalk 활용<br>- RDS를 통해 MySQL 사용<br>- ECR 활용해 이미지 관리<br>- ElastiCache 활용해 Redis 관리<br>- Github Actions를 활용한 배포 | [Github](https://github.com/webstrdy00) |
| 조성래 | 팀원 | **▶ 스터디룸 관리**<br>- 스터디룸 CRUD 기능 구현<br><br>**▶ 스터디룸 예약**<br>- 예약 시스템의 CRUD 및 예약 충돌 방지 로직 구현<br>- 예약 시스템 모듈화 및 사용자 편의성 강화<br><br>**▶ 동시성 제어**<br>- 낙관적 락과 Redis 분산락 혼용방식 도입<br>- Jmeter를 사용한 부하 테스트<br><br>**▶ Spring Batch**<br>- 데이터의 백업<br>- CSV 파일 추출 데이터 분석 및 보존 지원 | [Github](https://github.com/Sungrae-kogi) |

### **Ground Rule**

🍁 **문제 발생 시 즉시 공유**  
- 문제가 발생하면 즉시 팀원들과 상황을 공유하고, 협력하여 해결 방안을 모색합니다.

🍁 **문제 발생 시 마감 기한 내 해결**  
- 문제가 발생했을 경우, 마감 기한에 영향을 미치지 않도록 빠르게 상의하고 조치를 취하여 기한 내 작업을 마무리합니다.

🍁 **회의 시간에 의견 적극 공유**  
- 회의 중 진행할 예정인 작업이나 이슈를 사전에 공유하여 팀원들의 피드백을 받을 수 있도록 합니다.  
- **코드 리뷰**는 최소 **주 1회 이상** 필수로 진행하여, 팀의 코드 품질을 지속적으로 향상시킵니다.

🍁 **PR 시 코드 리뷰 필수**  
- PR을 merge하기 전에 **1명 이상의 코드 리뷰어**가 반드시 검토해야 하며, 이를 통해 코드 품질을 보장합니다.


## 📊 8. 성과 및 회고
### 잘된 점
- **성능 최적화**를 통해 눈에 띄는 개선 효과를 달성하였습니다.
- 체계적인 **문서화와 코드 리뷰 문화**가 팀 내에 정착되어, 협업 효율성이 크게 향상되었습니다.

### 아쉬운 점
- **초기 설계 단계에서의 충분한 논의 부족**으로 인해 일부 기능에 대한 미흡한 부분이 발생했습니다.
- **테스트 코드 커버리지**가 부족하여, 전체 시스템의 안정성에 대한 추가적인 검증이 필요했습니다.

### 향후 계획
1. **알림 기능 고도화**
  - 사용자 맞춤 언어로**알림 메일 송신** 기능 추가
  - 사용자가 직접 알림 **시간을 설정**할 수 있도록 개선하여 유연성을 높입니다.
  - 알림의 **종류와 빈도**를 사용자별로 맞춤 설정할 수 있도록 개선하여, 개인화된 경험을 제공합니다.

2. **백업 데이터 복구 기능 구현**
  - **데이터 손실 최소화**를 위한 안정적인 복구 시스템을 구축합니다. 
  - **주기적인 자동 백업**을 통해, 백업 데이터를 활용한 빠르고 신뢰성 있는 **데이터 복구 시스템**을 구현할 예정입니다.

3. **ECS/EKS 도입**
  - **MSA** 전환을 위한 컨테이너 오케스트레이션 도입
  - 여러 독립적인 서비스의 효율적 관리
  - **자동 스케일링** 및 **로드밸런싱** 구현
  - **서비스 모니터링** 및 **로깅 시스템** 구축
