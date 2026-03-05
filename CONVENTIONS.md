# 코딩 컨벤션

## 패키지 구조

도메인 단위로 패키지를 구성한다.

```
com.delivery.project.{domain}/
├── controller/
├── service/
├── entity/
├── repository/
└── dto/
    ├── request/
    └── response/

com.delivery.project.global/
├── config/
├── exception/
├── jwt/
├── response/
└── security/
```

---

## 네이밍

| 대상 | 규칙 | 예시 |
|---|---|---|
| 클래스 | PascalCase | `OrderService`, `LoginRequestDto` |
| 메서드 / 변수 | camelCase | `findByUsername`, `createdAt` |
| 상수 | SCREAMING_SNAKE_CASE | `BEARER_PREFIX`, `TOKEN_TIME` |
| DB 테이블 | `p_` 접두사 + snake_case | `p_user`, `p_order` |
| DB 컬럼 | snake_case | `created_at`, `owner_username` |
| 패키지 | lowercase | `com.delivery.project.order` |

---

## Entity

```java
@Entity
@Table(name = "p_{domain}")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // FK는 @ManyToOne 없이 컬럼 ID만 보유 (느슨한 결합)
    @Column(name = "owner_username", nullable = false, length = 100)
    private String ownerUsername;

    // Enum은 EnumType.STRING 사용
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Soft Delete 감사 필드 (모든 엔티티 공통)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;
}
```

- `@Setter` 사용 금지 — 불변 객체 유지
- PK는 UUID 사용 (`GenerationType.UUID`), User는 예외적으로 username을 PK로 사용
- FK는 `@ManyToOne` 없이 컬럼 ID만 보유
- Enum은 별도 파일로 분리 (`UserRole.java`) 또는 도메인 내부 enum으로 정의
- 모든 엔티티에 Soft Delete 감사 필드 포함

---

## Controller

```java
@RestController
@RequestMapping("/api/v1/{domain}")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // TODO: GET    /api/v1/stores      - 가게 목록 조회
    // TODO: POST   /api/v1/stores      - 가게 등록 (OWNER+)
    // TODO: PUT    /api/v1/stores/{id} - 가게 수정 (OWNER 본인 or MANAGER+)
    // TODO: DELETE /api/v1/stores/{id} - 가게 삭제 (MANAGER+)

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStore(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStore(id)));
    }
}
```

- API 버전 prefix: `/api/v1/`
- 반환 타입: `ResponseEntity<ApiResponse<T>>`
- Request Body: `@Valid @RequestBody`
- TODO 주석 형식: `// TODO: {HTTP Method} {경로} - {설명} ({권한}+)`
- 권한 표기: `OWNER+`, `MANAGER+`, `MASTER` (+ 는 해당 권한 이상)

---

## Service

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본 readOnly
public class StoreService {

    private final StoreRepository storeRepository;

    // 조회는 기본 readOnly 그대로 사용

    @Transactional  // 쓰기 작업에만 별도 선언
    public void createStore(StoreRequestDto request) {
        // ...
        log.info("가게 등록 완료: {}", store.getId());
    }
}
```

- 클래스 레벨에 `@Transactional(readOnly = true)` 선언
- 쓰기 작업 메서드에만 `@Transactional` 추가 선언
- 주요 처리 완료 후 `log.info()` 로 기록

---

## DTO

```java
// Request
@Getter
public class SignupRequestDto {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
    private String username;
}

// Response
@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String username;
    private String role;
}
```

- `dto/request/`, `dto/response/` 하위 패키지 분리
- `@Getter`만 선언, `@Setter` 사용 금지
- Request DTO: Jakarta Validation 어노테이션 + 한국어 메시지
- Response DTO: `@AllArgsConstructor`로 생성

---

## 예외 처리

```java
// 예외 발생
throw new CustomException(ErrorCode.USER_NOT_FOUND);

// ErrorCode 추가 방식
USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
```

- 비즈니스 예외는 `CustomException(ErrorCode)` 사용
- `ErrorCode`는 도메인별로 구분해서 추가
- `GlobalExceptionHandler`에서 일괄 처리

---

## 공통 응답 포맷

```java
// 성공
ApiResponse.success(data)
ApiResponse.success("메시지", data)

// 실패
ApiResponse.fail("에러 메시지")
```

모든 API 응답은 `ApiResponse<T>`로 감싸서 반환한다.

---

## 권한 체크

```java
// 메서드 레벨 권한 체크
@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
public void deleteStore(UUID id) { ... }
```

- URL 레벨: `SecurityConfig`의 `requestMatchers`
- 메서드 레벨: `@PreAuthorize`

권한 계층: `CUSTOMER` < `OWNER` < `MANAGER` < `MASTER`

---

## 주석

```java
// 단순 설명 주석
// TODO: 미구현 기능 (TODO 형식 유지)

// 복잡한 로직은 한국어로 설명
// 삭제된 유저는 조회에서 제외
```

- 주석은 한국어 작성
- 미구현 기능은 반드시 `// TODO:` 형식으로 남긴다
