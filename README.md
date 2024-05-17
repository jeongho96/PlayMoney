# PlayMoney(플레이모네)

## ⏱ 개발 기간

---

**2024.04.17 - 현재 진행중**

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

## ERD 설계

![hanghe99(streaming)](https://github.com/jeongho96/PlayMoney/assets/47017708/fc6a5293-31ed-493d-8157-46bce1800126)


---
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

## 🔨️ 성능 개선 사례

---

- 단일쓰레드의 배치작업을 멀티쓰레드 환경으로 전환

## [🔧 트러블 슈팅 및 기타 정보](https://puffy-puppet-72b.notion.site/play-money-cbabc400b6d9469489304ae2680b6b2e?pvs=4)

--- 


