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

### 배경
도서관에서 어떤 알림을 보내줘야할까 고민 후 SA를 작성을 했습니다 
그 리스트는 이러합니다.
### 요구사항
- 책 반납일 1일전 , 3일전 , 당일날에 알림이 해당 유저에게 발송되어야함
- 스터디룸 예약일 1일전 , 당일날에 알림이 해당 유저에게 발송되어야함
### 의사결정 사유
- AOP로 구현은 특정메서드나 클래스를 타겟팅하여 메서드가 실행이 되었을때 aop가 실행이 되기 때문에 적합하지 않다 판단
- 스케줄러를 사용해 특정한 시간에 매일 한번씩 자동으로 보내주는로직이 맞다 판단
- 사용자의 편의성을 높이기 위해 구글 Gmail로 메일 송신하는 방법 선택
- 스케줄러를 redisson 분산 락을 사용하여 동시성 제어 문제 방지
  
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
