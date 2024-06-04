# PlayMoney(플레이모네)

## ⏱ 개발 기간

---

**2024.04.17 - 2024.05.17**

<br>

## ✏️ 프로젝트 소개

--- 

- 동영상을 업로드한 사용자에 대한 정산 시스템을 spring batch 기반으로 개발.

<br>

## 💻 개발 환경

---

- Version : Java 21
- Framework : SpringBoot 3.2.5
- DB : MySQL
- ORM : JPA

환경설정[참고]
<details>
<summary>Docker-compose.yml</summary>

```yaml
version: '3.8'
services:
  mysql-container:
    container_name: mysql-container
    image: mysql:latest
    environment:
      MYSQL_ROOT_PASSWORD: # root 비번
      MYSQL_DATABASE: streamingDB
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```
</details>

<details>
<summary>application.yml</summary>

```yaml
server:
  port: 8085

spring:
  # spring batch를 위한 메타데이터 테이블 자동생성
  batch:
    job:
      # 스프링 부트 실행시 배치 작업 자동 실행 끄기
      enabled: false
    jdbc:
      initialize-schema: always
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        ddl-auto: update
    defer-datasource-initialization: true
  datasource:
    url: jdbc:mysql://localhost:3306/streamingDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: # db 사용자 id
    password: # db 비밀번호
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: # 구글 client-id
            client-secret: # 구글 client-secret
            scope:
              - email
              - profile
  application:
    name: PlayMoney
jwt:
  issuer: leejh9128@gmail.com
  secret_key: streamingDB



```

</details>

## ERD 설계

---

![hanghe99(streaming)](https://github.com/jeongho96/PlayMoney/assets/47017708/fc6a5293-31ed-493d-8157-46bce1800126)



<h2>
<a href="https://puffy-puppet-72b.notion.site/API-7dd973b4431845528d0efa5813c446d2?pvs=4">
    API 명세서
</a>
</h2>

---

## ▶️ 기능 구현

---

1. 정산 기능  
   - 주간, 월간 조회수 누적  
   - 업로더 별 일일, 주간, 월간 정산 금액 누적


2. 통계 관련 기능  
   - 기간 별 조회수 TOP5 조회  
   - 기간 별 영상 길이 TOP5 조회

   
3. Spring Security 기반 로그인 및 로그아웃 구현
   - jwt를 활용한 로그인 구현.
   - 구글을 통한 소셜 로그인 추가.


<br>

## 📒 기술 의사결정

---
| 기술스택         | 해당 기술 사용 이유                                                                         |
|--------------|-------------------------------------------------------------------------------------|
| spring batch | 통계나 정산 작업 시 scheduler만 사용해서 특정 시간에 <br/>처리하는 것보다 더 복잡한 로직을 처리 가능.                   |
| Docker       | 로컬 환경에서 DB를 직접 설치해서 사용하는 것보다는 Docker를 활용해 <br/>container로 띄워서 사용하는 것이 배포와 접근성이 용이함. |


[//]: # (## 🔨️ 성능 개선 사례)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (- 단일쓰레드의 배치작업을 멀티쓰레드 환경으로 전환&#40;현재 트러블 슈팅 중&#41;)

[//]: # (  - 작업 종료 시 구체적인 성능 개선 수치와 함께 작성 예정)

## 🔧 트러블 슈팅
- [통계 시 값이 컬럼 수만큼 중복](https://www.notion.so/b732e36012a84e02884a04ab0c62d3d4?pvs=4)
- [ItemReader 구현 후 값 전달 시 NullPointerException 발생](https://www.notion.so/ItemReader-NullPointerException-e025b41b36b848138fd0ad15ca4706bf?pvs=4)
- [주간, 월간 정산 데이터가 제대로 저장되지 않음](https://www.notion.so/dfac65ef9d214ce88b94a47141b8ad64?pvs=4)
- [새로운 환경에서 Bean 충돌 오류](https://puffy-puppet-72b.notion.site/Scheduler-6431669ec0824e5cb1594324659b77ea?pvs=4)
--- 


