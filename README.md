# 🚲 deliveryapp 배달주문 플랫폼 🏍️

사용자에게는 간편한 음식 주문 및 결제 경험을, 사장님에게는 효율적인 가게 관리 및 상품 등록 시스템등을 제공하는 백엔드 중심의 배달 플랫폼 서비스입니다.

## 프로젝트 목적

1️⃣ 사용자를 위한 맞춤 서비스 제공 - 사용자가 빠르고 편리하게 음식 주문과 배달을 이용할 수 있는 시스템 구현 <br>

2️⃣ 가게 사장을 위한 가게 관리 기능 제공 - 가게 등록, 상품 관리, 주문 관리, 리뷰 관리 등 가게 운영에 필요한 기능 제공 <br>

3️⃣ 백엔드 중심의 안정적 서비스 설계 - Spring Boot와 MySQL 기반 CRUD, 인증/인가, 결제 등 핵심 기능 구현 <br>

4️⃣ AI 추천 기능 연동 - Gemini API를 활용한 사용자 맞춤 추천 및 AI 로그 관리로 편의성 향상 <br>

5️⃣ 실제 결제 및 클라우드 배포 환경 적용 - toss payments 연동과 Docker + AWS 배포 환경 구축으로 실무 환경 경험 <br>

## 팀원 담당

| 이름      | 담당                                                |
| ------- | ------------------------------------------------- |
| 김현희     |  가게 - 가게 관리, 가게 카테고리 등록/삭제, 카테고리·지역 관리                        |
| 김민성     |  주문, 결제 - 주문 관리, toss payments 연동 결제 및 취소       |
| 문연희   |  상품, 결제 -상품 관리, 결제 완료 관리          |
| 김란미 |    ai, 리뷰 - gemini api 연동, 주문 완료 리뷰 관리                |
| 박세영 |   회원가입, 로그인, JWT, Spring Security - 인증/인가 처리, 사용자 보안 관리, JWT 기반 인증 설계                |

## 기술 스택

- Java 17
- Spring Boot 3.3.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- WebFlux (Gemini API 연동)
- Docker / Docker Compose

## ERD
<img width="2210" height="1322" alt="Copy of delivery" src="https://github.com/user-attachments/assets/270d631a-ca89-4723-9a57-64c21c29a40b" />

## Architecture

<img width="616" height="646" alt="인프라 설계서 drawio" src="https://github.com/user-attachments/assets/79cb66ee-39e6-45ea-95dd-2c7a378a290d" />


## 프로젝트 구조

```
src/main/java/com/delivery/project/
│
├── ProjectApplication.java
│
├── ai/                        # AI 로그 (Gemini API)
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── auth/                      # 인증 (로그인 / 회원가입)
│   ├── controller/
│   └── service/
│
├── category/                  # 카테고리
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── order/                     # 주문
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── payment/                   # 결제
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── product/                   # 상품
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── region/                    # 지역
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── review/                    # 리뷰
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── store/                     # 가게
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── user/                      # 유저
│   ├── controller/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── service/
│
└── global/                    # 공통
    ├── config/                # Security, WebClient 설정
    ├── exception/             # 예외 처리
    ├── jwt/                   # JWT 필터 및 유틸
    └── response/              # 공통 응답 형식
```

## Swagger
http://54.252.56.23:8080/swagger-ui/index.html

## 실행 방법

**로컬 실행**
```bash
./gradlew clean build -x test
./gradlew bootRun
```

**Docker 실행**
```bash
docker-compose up --build
```
