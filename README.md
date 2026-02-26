# deliveryapp

배달 주문 플랫폼 백엔드 서버

## 기술 스택

- Java 17
- Spring Boot 3.3.0
- Spring Security + JWT
- Spring Data JPA + QueryDSL
- MySQL 8.0
- WebFlux (Gemini API 연동)
- Docker / Docker Compose

## 프로젝트 구조

```
src/main/java/com/delivery/project/
│
├── ProjectApplication.java
│
├── ai/                        # AI 로그 (Gemini API)
│   ├── controller/
│   ├── entity/
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
│   ├── repository/
│   └── service/
│
├── delivery/                  # 배달 주소
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── order/                     # 주문
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── payment/                   # 결제
│   ├── controller/
│   ├── entity/
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
│   ├── repository/
│   └── service/
│
├── review/                    # 리뷰
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── store/                     # 가게
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── user/                      # 유저
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
└── global/                    # 공통
    ├── config/                # Security, WebClient 설정
    ├── exception/             # 예외 처리
    ├── jwt/                   # JWT 필터 및 유틸
    └── response/              # 공통 응답 형식
```

## 환경 변수

프로젝트 루트에 `.env` 파일을 생성하고 아래 항목을 설정하세요.

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=delivery_db
DB_USER=root
DB_PASS=

JWT_SECRET=
JWT_EXPIRATION=86400000

GEMINI_API_KEY=

SPRING_PROFILES_ACTIVE=prod
```

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
