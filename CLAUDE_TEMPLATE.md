# CLAUDE.md - Backend Convention Guide

## Project Overview
This project is a Java 21-based Spring Boot backend server.

---

## Tech Stack
- **JDK**: 21
- **Framework**: Spring Boot
- **ORM**: JPA (Hibernate)
- **Query**: QueryDSL
- **Cache**: Redis
- **Build**: Gradle

---

## Architecture

### Layer Structure
```
API Handler → UseCase → Service / QueryService → Repository
     ↓           ↓          ↓           ↓             ↓
  Request    Response   Domain      Domain         Entity
    DTO        DTO      Object      Object         (JPA)
```

### Layer Responsibilities

#### 1. API Handler (Controller)
- Handles HTTP request/response
- Passes Request DTO to UseCase
- Returns Response DTO to client
- Uses `BaseResponse` / `BaseResponseData` for all responses
- All parameters must be `final`

#### 2. UseCase
- Orchestrates business use cases (Facade pattern)
- Decomposes Request DTO into parameters and calls Service/QueryService
- Converts Domain objects into Response DTOs
- **Only layer where multiple Services can be combined**
- Annotated with `@Component`

#### 3. Service (Command)
- Performs **command** operations only: save, edit, delete
- Returns Domain objects or void (NOT Request/Response DTOs)
- Converts Entity ↔ Domain objects
- **Injects only ONE Repository** (no service-to-service dependency)
- One method = one responsibility

#### 4. QueryService (Query)
- Performs **query** operations only: find, check, exists
- Returns Domain objects
- **Injects only ONE Repository** (or QueryRepository)
- Follows CQS (Command Query Separation) principle
- **Location**: `application/query/{Domain}QueryService.java`

#### 5. Repository
- Database access only
- Returns Entity objects
- JPA: `{Domain}JpaRepository` extends `JpaRepository`
- QueryDSL: `{Domain}QueryRepository` with `JPAQueryFactory`

---

## Object Types & Conversion Methods

| Object | Location | Conversion Method |
|--------|----------|-------------------|
| Request DTO | `domain/{domain}/client/request/` | None (decomposed in UseCase) |
| Response DTO | `domain/{domain}/application/response/` | `of(Domain)` |
| Domain Object | `domain/{domain}/domain/model/` | `of(Entity)` |
| Entity | `domain/{domain}/domain/entity/` | Business methods (e.g., `editUser()`) |

### Conversion Rules
- Entity → Domain: `Domain.of(entity)`
- Entity → DTO (direct): `Dto.to(entity)`
- Domain → Response: `Response.of(domain)`

### DTO Rules
- Use Java Record classes for all DTOs
- Response DTOs: add `@Builder`
- Domain Objects: add `@Builder`
- Request DTOs: plain record with validation annotations

---

## Data Flow

### Read Operations (via QueryService)
```
1. API Handler: Receives request params
2. UseCase: Calls QueryService with decomposed params
3. QueryService: Queries Entity → converts to Domain → returns
4. UseCase: Converts Domain → Response DTO
5. API Handler: Returns BaseResponseData<Response>
```

### Write Operations (via Service)
```
1. API Handler: Receives @RequestBody Request DTO
2. UseCase: Decomposes Request → calls Service with params
3. Service: Builds/edits Entity → saves via Repository
4. Service: Returns Domain or void
5. UseCase: Converts to Response (if needed)
6. API Handler: Returns BaseResponse or BaseResponseData
```

---

## Code Examples

### Domain Object
```java
@Builder
public record Asset(
    Long id,
    AssetType assetType,
    String itemName
) {
    public static Asset of(AssetEntity entity) {
        return Asset.builder()
            .id(entity.getId())
            .assetType(entity.getAssetType())
            .itemName(entity.getItemName())
            .build();
    }
}
```

### Service (Command only)
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetJpaRepository repository;

    @Transactional
    public void save(AssetType assetType, String itemName) {
        log.info("[AssetService] save - assetType={}, itemName={}", assetType, itemName);
        AssetEntity entity = AssetEntity.builder()
            .assetType(assetType)
            .itemName(itemName)
            .build();
        repository.save(entity);
    }

    @Transactional
    public void edit(Long id, AssetType assetType, String itemName) {
        log.info("[AssetService] edit - id={}", id);
        AssetEntity entity = repository.findById(id)
            .orElseThrow(() -> AssetNotFoundException.EXCEPTION);
        entity.editAsset(assetType, itemName);
    }

    @Transactional
    public void delete(Long id) {
        log.info("[AssetService] delete - id={}", id);
        AssetEntity entity = repository.findById(id)
            .orElseThrow(() -> AssetNotFoundException.EXCEPTION);
        entity.delete();
        repository.save(entity);
    }

}
```

### QueryService (Query only)
```java
// domain/asset/application/query/AssetQueryService.java
@Service
@RequiredArgsConstructor
public class AssetQueryService {

    private final AssetJpaRepository repository;

    public Asset find(Long id) {
        AssetEntity entity = repository.findById(id)
            .orElseThrow(() -> AssetNotFoundException.EXCEPTION);
        return Asset.of(entity);
    }

    public List<Asset> findAll() {
        return repository.findAll().stream()
            .map(Asset::of)
            .toList();
    }

}
```

### UseCase
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class AssetUseCase {

    private final AssetService assetService;
    private final AssetQueryService assetQueryService;

    public void register(CreateAssetRequest request) {
        log.info("[AssetUseCase] register - itemName={}", request.itemName());
        assetService.save(
            request.assetType(),
            request.itemName()
        );
    }

    public AssetResponse getById(Long assetId) {
        Asset asset = assetQueryService.find(assetId);
        return AssetResponse.of(asset);
    }

    public List<AssetResponse> getAll() {
        List<Asset> assets = assetQueryService.findAll();
        return assets.stream()
            .map(AssetResponse::of)
            .toList();
    }

    public void update(UpdateAssetRequest request) {
        log.info("[AssetUseCase] update - assetId={}", request.assetId());
        assetService.edit(
            request.assetId(),
            request.assetType(),
            request.itemName()
        );
    }

    public void delete(Long assetId) {
        log.info("[AssetUseCase] delete - assetId={}", assetId);
        assetService.delete(assetId);
    }

}
```

### Response DTO
```java
@Builder
public record AssetResponse(
    Long id,
    AssetType assetType,
    String itemName
) {
    public static AssetResponse of(Asset asset) {
        return AssetResponse.builder()
            .id(asset.id())
            .assetType(asset.assetType())
            .itemName(asset.itemName())
            .build();
    }
}
```

---

## Package Structure

```
src/main/java/com/{company}/{project}/
├── global/
│   ├── common/
│   │   ├── dto/
│   │   │   ├── request/          # PageRequest
│   │   │   └── response/         # BaseResponse, BaseResponseData, PageResponse
│   │   ├── entity/               # BaseEntity
│   │   └── repository/           # Shared interfaces (RedisRepository)
│   ├── config/
│   │   ├── async/                # AsyncConfig (ThreadPool)
│   │   ├── redis/                # RedisConfig
│   │   ├── web/                  # WebClientConfig
│   │   ├── query/                # QueryDslConfig
│   │   └── ...                   # Other configs
│   ├── security/
│   │   ├── config/               # SecurityConfig
│   │   ├── jwt/                  # JWT provider, filters, properties
│   │   └── auth/                 # Authentication helpers
│   ├── exception/
│   │   ├── handler/              # ExceptionAdvice (Global Handler)
│   │   └── error/                # ErrorCode, ErrorProperty interface
│   ├── infra/                    # External service integrations
│   │   ├── email/                # Email (SMTP + @Async)
│   │   ├── firebase/             # FCM push notifications
│   │   ├── gcp/                  # Google Cloud Storage
│   │   ├── iap/                  # In-App Purchase (iOS, Android)
│   │   ├── toss/                 # Payment gateway
│   │   └── slack/                # Slack webhook
│   └── log/                      # API logging (filter, service, entity)
│
└── domain/
    └── {domainName}/
        ├── client/
        │   ├── api/              # {Domain}ApiHandler (Controller)
        │   └── request/          # Request DTOs
        ├── application/
        │   ├── query/            # {Domain}QueryService (CQS queries)
        │   ├── response/         # Response DTOs
        │   ├── service/          # {Domain}Service (CQS commands)
        │   ├── usecase/          # {Domain}UseCase
        │   └── scheduler/        # Scheduled tasks
        ├── domain/
        │   ├── entity/           # {Domain}Entity extends BaseEntity
        │   ├── model/            # Domain Objects (records)
        │   ├── enums/            # Domain enums
        │   └── repository/
        │       ├── jpa/          # {Domain}JpaRepository
        │       └── query/        # {Domain}QueryRepository
        └── exception/
            ├── error/            # {Domain}Error enum
            └── ...               # Singleton exception classes
```

---

## Naming Conventions

### Class Names
| Target | Pattern | Example |
|--------|---------|---------|
| Entity | `{Domain}Entity` | `UserEntity`, `MeetingEntity` |
| Domain Model | `{Domain}` | `User`, `Meeting` |
| Controller | `{Domain}ApiHandler` | `UserApiHandler` |
| Service | `{Domain}Service` | `UserService` |
| Query Service | `{Domain}QueryService` | `MeetingQueryService` |
| UseCase | `{Domain}UseCase` | `UserUseCase` |
| JPA Repository | `{Domain}JpaRepository` | `UserJpaRepository` |
| Query Repository | `{Domain}QueryRepository` | `MeetingQueryRepository` |
| Request DTO | `{Verb}{Domain}Request` | `UpdateUserRequest` |
| Response DTO | `{Domain}Response` | `UserResponse` |
| Exception | `{Domain}{Error}Exception` | `UserNotFoundException` |
| Error Enum | `{Domain}Error` | `UserError` |
| Scheduler | `{Domain}Scheduler` | `TrialScheduler` |

### Method Names by Layer

| Action | UseCase | Service | QueryService | Entity |
|--------|---------|---------|-------------|--------|
| Create | `register` | `save` | - | - |
| Read (single) | `get` | - | `find` | - |
| Read (list) | `getAll`, `getList` | - | `findAll`, `findList` | - |
| Update | `update` | `edit` | - | `edit{Field}` |
| Delete | `delete` | `delete` | - | `delete` |
| Validate | `check` | - | `check`, `exists` | - |
| Entity mutation | - | - | - | `deactivate()`, `resetCredit()` |

**Examples:**
```
UseCase.update()       → Service.edit()        → Entity.editUser()
UseCase.getById()      → QueryService.find()
UseCase.register()     → Service.save()
UseCase.delete()       → Service.delete()       → Entity.delete()
UseCase.checkExists()  → QueryService.exists()
```

### Variable Names
| Target | Pattern | Example |
|--------|---------|---------|
| Request DTO param | `request` | `final UpdateUserRequest request` |
| Entity variable | `entity` | `UserEntity entity` |
| Domain variable | lowercase domain | `User user` |
| Boolean | `is{Condition}` | `isActive`, `isTrialUsed` |
| Multiple same type | use descriptive name | `User targetUser`, `User currentUser` |
| Constant | `CONSTANT_CASE` | `DEFAULT_CREDIT`, `MAX_RETRY` |

### Enum Values
- UPPER_CASE with underscores: `USER_NOT_FOUND`, `GOOGLE_IAP`

---

## Java Code Style

### Basic Rules
- Based on Google Java Style Guide
- Indentation: 1 tab (4 spaces)
- Wildcard imports prohibited (`java.util.*` ❌)
- `var` type inference prohibited
- C-style array declaration prohibited (`String args[]` ❌)

### Blank Line Rules
- After class declaration: 1 blank line
- Between methods: 1 blank line
- Before closing brace: 1 blank line
- **Inside methods: NO blank lines**

### Operator Spacing
- 1 space before and after all operators: `a = 1`, `b != 0`

### Long Type
- Uppercase suffix: `300000L`

### Brace Rules
```java
public class Example {

    public String testMethod() {
        int a = 1;
        if (a != 0) {
            try {
                testMethod();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "test";
        } else if (a == 1) {
            return "fail";
        } else {
            return "1234";
        }
    }

    public void methodWithManyParams(
        String parameter1,
        String parameter2,
        String parameter3
    ) {}

    public void blankMethod() {}

}
```

---

## Annotation Ordering

**Order by length (shortest → longest), alphabetical if equal length.**

### Entity
```java
@Entity
@Getter
@SuperBuilder
@Table(name = "tb_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity { }
```

### Service (Command)
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService { }
```

### QueryService (Query)
```java
@Service
@RequiredArgsConstructor
public class UserQueryService { }
```

### UseCase
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class UserUseCase { }
```

### Controller
```java
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserApiHandler { }
```

---

## Comment & JavaDoc Rules

### Method JavaDoc
```java
/**
 * Method description
 * @param paramName Type Parameter description
 * @return Type Return value description
 */
public ReturnType methodName(ParamType paramName) { }
```

### Field Comments
```java
/** Credit in minutes (null means no credit) */
private Long credit;

// or

// Credit in minutes (null means no credit)
private Long credit;
```

### Rules
- 2+ lines: Use `/** */` with line breaks
- 1 line: Use `//` with 1 space after `//`
- Place comments directly above related code
- Public/protected methods: JavaDoc required
- Private methods: `//` comment optional

---

## API Handler (Controller) Rules

### Annotations
```java
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/{domain}")
```

### Request URI
- Match domain name: `/auth`, `/user`, `/meeting`
- List endpoints: append `/list`
- Search endpoints: append `/search`

### Parameter Rules
- All parameters require `final`
- POST: Prefer `@RequestBody` (use `@RequestParam` if ≤1 param)
- GET: Prefer `@RequestParam` (use `@ModelAttribute` if ≥2 params)
- PUT/PATCH/DELETE: `@RequestParam` if ≤1, `@RequestBody` if ≥2

### Response Rules
```java
@PostMapping("/sign-in")
public BaseResponseData<SignInResponse> signIn(
    @RequestBody @Valid final SignInRequest request
) {
    return BaseResponseData.ok(
        "Login successful.",
        authUseCase.signIn(request)
    );
}
```
- Always use `BaseResponse` / `BaseResponseData`
- Only in Controller (prohibited in Service layer)

---

## Exception Handling

### Hierarchy
```
RuntimeException
  └── BusinessException (abstract base)
        └── {Domain}NotFoundException
        └── {Domain}AlreadyExistException
        └── ...
```

### Error Enum Pattern
```java
@Getter
@RequiredArgsConstructor
public enum UserError implements ErrorProperty {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
    USER_ALREADY_EXIST(HttpStatus.CONFLICT, "User already exists.");

    private final HttpStatus status;
    private final String message;
}
```

### Singleton Exception Pattern
```java
public class UserNotFoundException extends BusinessException {
    public static final UserNotFoundException EXCEPTION = new UserNotFoundException();
    private UserNotFoundException() {
        super(UserError.USER_NOT_FOUND);
    }
}

// Usage:
throw UserNotFoundException.EXCEPTION;
```

---

## Security & Authentication

### JWT Overview
- **Algorithm**: HS256 (HMAC-SHA256) with symmetric secret key
- **Token Types**: `ACCESS`, `REFRESH` (enum `JwtType`)
- **Location**: `Authorization: Bearer {token}` header
- **Issuer Validation**: Verified on every token parse
- **Session**: STATELESS (no server-side session)
- **Config**: `@ConfigurationProperties(prefix = "application.jwt")`

### JWT Properties
```java
@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(
    String secretKey,
    long expiration,          // ACCESS token: 86400000 (24h)
    long refreshExpiration,   // REFRESH token: 604800000 (7d)
    String issuer             // e.g., "bigtablet-insight"
) {}
```

```yaml
# application.yml
application:
  jwt:
    secret-key: ${secrets.JWT_SECRET_KEY}
    expiration: 86400000
    refresh-expiration: 604800000
    issuer: bigtablet-insight
```

### Token Structure

#### ACCESS Token
```
Header:  { "typ": "JWT", "alg": "HS256" }
Payload: {
  "sub": "user-id",
  "iss": "bigtablet-insight",
  "token_type": "ACCESS",
  "authority": "ROLE_USER",    // UserRole.getKey()
  "iat": 1700000000,
  "exp": 1700086400
}
Signature: HMAC-SHA256(header + payload, secretKey)
```

#### REFRESH Token
```
Header:  { "typ": "JWT", "alg": "HS256" }
Payload: {
  "sub": "user-id",
  "iss": "bigtablet-insight",
  "token_type": "REFRESH",
  // No "authority" claim (renewal-only purpose)
  "iat": 1700000000,
  "exp": 1700604800
}
Signature: HMAC-SHA256(header + payload, secretKey)
```

### JWT Component Structure

```
global/security/
├── config/
│   └── SecurityConfig.java              # Filter chain, endpoint permissions
├── jwt/
│   ├── JwtProvider.java                 # Token generation & validation
│   ├── JwtExtract.java                  # Token extraction & Authentication creation
│   ├── config/
│   │   └── JwtConfig.java              # @EnableConfigurationProperties
│   ├── enums/
│   │   └── JwtType.java                # ACCESS, REFRESH enum
│   ├── filter/
│   │   ├── JwtExceptionFilter.java     # Catches JWT exceptions → 401 JSON
│   │   ├── JwtAuthenticationFilter.java # Validates token, sets SecurityContext
│   │   └── SseAuthenticationFilter.java # SSE-specific auth (/sse endpoint)
│   ├── handler/
│   │   ├── JwtAuthenticationEntryPoint.java  # 401 Unauthorized handler
│   │   └── ApiAccessDeniedHandler.java       # 403 Forbidden handler
│   ├── properties/
│   │   └── JwtProperties.java          # Config record
│   └── exception/
│       └── TokenTypeException.java      # Wrong token type (singleton)
└── auth/
    └── CustomUserDetails.java           # UserDetails implementation
```

### JwtProvider (Token Generation & Validation)

```java
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    /** Generate ACCESS token with user ID and role */
    public String generateAccessToken(String id, UserRole userRole) {
        return Jwts.builder()
            .header().type("JWT").and()
            .subject(id)
            .issuer(jwtProperties.issuer())
            .claim("token_type", JwtType.ACCESS)
            .claim("authority", userRole.getKey())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
            .signWith(getSigningKey())
            .compact();
    }

    /** Generate REFRESH token with user ID only */
    public String generateRefreshToken(String id) {
        return Jwts.builder()
            .header().type("JWT").and()
            .subject(id)
            .issuer(jwtProperties.issuer())
            .claim("token_type", JwtType.REFRESH)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.refreshExpiration()))
            .signWith(getSigningKey())
            .compact();
    }

    /** Parse and validate token, returns Claims */
    public Claims getClaims(String token) {
        // Validates signature, issuer, expiration
        // Throws IllegalArgumentException on any failure
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

}
```

### JwtExtract (Token Extraction & Authentication)

```java
@Component
@RequiredArgsConstructor
public class JwtExtract {

    private final JwtProvider jwtProvider;

    /** Extract token from "Authorization: Bearer {token}" header */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return extractToken(header);  // removes "Bearer " prefix
    }

    /** Validate token and create Spring Security Authentication */
    public Authentication getAuthentication(String token) {
        Claims claims = jwtProvider.getClaims(token);
        // 1. Verify token type is ACCESS (not REFRESH)
        // 2. Look up user from DB by subject (user ID)
        // 3. Create CustomUserDetails with user and authorities
        // 4. Return UsernamePasswordAuthenticationToken
    }

}
```

### Filter Chain Order

```
HTTP Request
    │
    ▼
┌─────────────────────────┐
│ 1. JwtExceptionFilter   │  Catches JWT exceptions → returns 401 JSON response
│    (before UsernamePass) │
└───────────┬─────────────┘
            ▼
┌─────────────────────────────────┐
│ 2. JwtAuthenticationFilter      │  Extracts token → validates → sets SecurityContext
│    (after UsernamePassFilter)   │  Skips /sse requests (delegated to SseFilter)
└───────────┬─────────────────────┘
            ▼
┌─────────────────────────────────┐
│ 3. SseAuthenticationFilter      │  Handles /sse with text/event-stream Accept header
│    (after JwtAuthFilter)        │  Returns 401 immediately if no token
└───────────┬─────────────────────┘
            ▼
┌─────────────────────────┐
│ 4. ApiLogFilter         │  Logs request/response (userId, URI, method, IP, duration)
└───────────┬─────────────┘
            ▼
       Controller
```

### JwtExceptionFilter (Exception Handling)

```java
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(...) {
        try {
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
        } catch (ServletException e) {
            if (e.getCause() instanceof IllegalArgumentException) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
            } else {
                setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
            }
        }
    }

    /** Returns JSON error: { "status": "401", "message": "..." } */
    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Exception ex) {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        // Write JSON body
    }

}
```

### JwtAuthenticationFilter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtExtract jwtExtract;

    /** Skip filter for SSE requests (handled by SseAuthenticationFilter) */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/sse")
            && request.getHeader("Accept") != null
            && request.getHeader("Accept").contains("text/event-stream");
    }

    @Override
    protected void doFilterInternal(...) {
        String token = jwtExtract.extractTokenFromRequest(request);
        if (token != null) {
            Authentication auth = jwtExtract.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

}
```

### Authentication Error Handlers

```java
/** 401 - No token or invalid token */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // Returns: { "status": 401, "message": "Authentication required." }
}

/** 403 - Valid token but insufficient role */
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    // Returns: { "status": 403, "message": "Access denied." }
}
```

### CustomUserDetails

```java
public class CustomUserDetails implements UserDetails {
    private final User user;
    private final Collection<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = List.of(
            new SimpleGrantedAuthority(user.userRole().getKey())
            // e.g., "ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER", "ROLE_MASTER"
        );
    }
}
```

### UserSecurity (Current User Helper)

```java
/** Interface in global/common/repository/ */
public interface UserSecurity {
    User getUser();           // Throws if not authenticated
    User getUserOrNull();     // Returns null if not authenticated
}

/** Implementation reads from SecurityContextHolder */
@Component
public class UserSecurityImpl implements UserSecurity {
    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
        return details.getUser();
    }
}
```

### Role-Based Access Control (RBAC)

| Role | Key | Description |
|------|-----|-------------|
| USER | `ROLE_USER` | Basic authenticated user |
| ADMIN | `ROLE_ADMIN` | Organization administrator |
| OWNER | `ROLE_OWNER` | Organization owner |
| MASTER | `ROLE_MASTER` | System administrator |

### SecurityConfig Endpoint Permissions

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // Public endpoints (no auth required)
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/faq").permitAll()

            // USER+ (all authenticated users)
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "OWNER", "MASTER")
            .requestMatchers("/meeting/**").hasAnyRole("USER", "ADMIN", "OWNER", "MASTER")

            // ADMIN+ (organization managers)
            .requestMatchers(HttpMethod.POST, "/organization/invitation").hasAnyRole("ADMIN", "OWNER", "MASTER")

            // OWNER+ (organization owners)
            .requestMatchers(HttpMethod.PATCH, "/organization").hasAnyRole("OWNER", "MASTER")

            // MASTER only (system admin)
            .requestMatchers("/admin/**").hasRole("MASTER")
            .requestMatchers("/log/**").hasRole("MASTER")

            .anyRequest().authenticated()
        )
        .exceptionHandling(e -> e
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)   // 401
            .accessDeniedHandler(apiAccessDeniedHandler)             // 403
        )
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
        .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(sseAuthenticationFilter, JwtAuthenticationFilter.class);

    return http.build();
}
```

### JWT Token Exception Handling

| Exception | Cause | HTTP Status |
|-----------|-------|-------------|
| `ExpiredJwtException` | Token expired | 401 Unauthorized |
| `SignatureException` | Invalid signature | 401 Unauthorized |
| `MalformedJwtException` | Corrupted token | 401 Unauthorized |
| `UnsupportedJwtException` | Unsupported format | 401 Unauthorized |
| `TokenTypeException` | REFRESH used as ACCESS | 400 Bad Request |
| `AccessDeniedException` | Insufficient role | 403 Forbidden |

### Authentication Flow Diagram

```
Client → "Authorization: Bearer eyJhbG..."
    │
    ▼
JwtExceptionFilter (try-catch wrapper)
    │
    ▼
JwtAuthenticationFilter
    ├─ extractTokenFromRequest() → "eyJhbG..."
    ├─ getAuthentication()
    │   ├─ getClaims()          → Verify signature, issuer, expiration
    │   ├─ Check token_type     → Must be ACCESS
    │   ├─ Find user in DB      → By subject (user ID)
    │   └─ CustomUserDetails    → With ROLE_* authority
    └─ SecurityContext.setAuthentication()
    │
    ▼
Controller
    ├─ @PreAuthorize / hasRole() check
    └─ UserSecurity.getUser() → Current authenticated user
```

---

## QueryDSL Pattern

### QueryRepository
```java
@Repository
@RequiredArgsConstructor
public class ClientQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ClientEntity> findAllClients(int page, int size, String userId, String organizationId) {
        return jpaQueryFactory
            .selectFrom(clientEntity)
            .where(condition)
            .offset((long) (page - 1) * size)
            .limit(size)
            .orderBy(clientEntity.createdAt.desc())
            .fetch();
    }

}
```

### QueryService (with QueryRepository)
```java
// domain/{domain}/application/query/{Domain}QueryService.java
@Service
@RequiredArgsConstructor
public class ClientQueryService {

    private final ClientQueryRepository queryRepository;

    public PageResponse<Client> findAll(int page, int size, String userId, String organizationId) {
        List<Client> content = queryRepository.findAllClients(page, size, userId, organizationId)
            .stream()
            .map(Client::of)
            .toList();
        long count = queryRepository.countClients(userId, organizationId);
        return PageResponse.of(content, count, size);
    }

}
```

---

## Pagination

### PageRequest
```java
@Getter
@Setter
public class PageRequest {
    @NotNull @Positive private int page;
    @NotNull @Positive private int size;
}
```

### PageResponse
```java
public record PageResponse<T>(List<T> content, int totalPages) {
    public static <T> PageResponse<T> of(List<T> content, long totalElements, int size) {
        int totalPages = (int) ((totalElements + size - 1) / size);
        return new PageResponse<>(content, totalPages);
    }
    public <U> PageResponse<U> map(Function<? super T, U> converter) {
        return new PageResponse<>(content.stream().map(converter).toList(), totalPages);
    }
}
```

---

## Logging

### Tracking Log Pattern
```java
// UseCase: log all business operations
log.info("[ClassName] Description - key={}", value);

// Service: log write operations only (save/update/delete)
log.info("[ClassName] Description - key={}", value);

// Error: use log.error with getMessage(), no stack trace
log.error("[ClassName] Description - key={}, error={}", value, e.getMessage());

// Warning: non-critical failures
log.warn("[ClassName] Description - key={}, error={}", value, e.getMessage());
```

### Rules
- No sensitive data in logs (passwords, tokens, secrets)
- No stack traces (use `e.getMessage()`)

---

## Async Configuration

```java
@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }
}
```

---

## Redis Repository

### Interface
```java
public interface RedisRepository {
    void save(String key, Object value, long timeout, TimeUnit unit);
    <T> T getByKey(String key, Class<T> type);
    void editByKey(String key, Object value, long timeout, TimeUnit unit);
    void delete(String key);
}
```

---

## WebClient Configuration

### Multiple WebClient Beans
```java
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient sttWebClient() {
        // 5s connection, 3min response timeout
    }
    @Bean
    public WebClient slackWebClient() {
        // 2s connection, 5s response timeout
    }
    @Bean
    public WebClient tossWebClient() {
        // 5s connection, 3min response timeout
    }
}
```

---

## Base Classes

### BaseEntity
```java
@Getter
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
```

### BaseResponse & BaseResponseData
```java
BaseResponse.ok("Success message");
BaseResponseData.ok("Success message", data);
BaseResponseData.created("Created message", data);
```

---

## Record vs Class Usage

| Use Record | Use Class |
|------------|-----------|
| Request DTO | Entity (extends BaseEntity) |
| Response DTO | Service |
| Domain Model | Controller |
| PageResponse | Repository (interface) |
| ConfigurationProperties | Exception |

---

## Git Convention

### Commit Message Format
```
label: message
```

### Labels
| Label | Description |
|-------|-------------|
| feat | New feature |
| fix | Non-urgent bug fix |
| bug | Critical/urgent bug fix |
| merge | Branch merge |
| deploy | Deployment changes |
| docs | Documentation |
| delete | Code/file deletion |
| note | Comments/annotations |
| style | Formatting (no logic change) |
| config | Configuration changes |
| etc | Other |
| tada | Project initialization |

### Branch Naming
```
label/domain
```
Examples: `feat/auth`, `fix/meeting`, `config/redis`

### Rules
- NEVER commit unless explicitly asked
- Do NOT commit `CLAUDE.md` or files in `.gitignore`

---

## Prohibited
- [ ] Wildcard imports
- [ ] `var` type inference
- [ ] C-style array declaration (`String args[]`)
- [ ] Blank lines inside methods
- [ ] BaseResponse in Service layer
- [ ] @Setter on Entity (unless necessary)
- [ ] Request/Response DTO in Service layer
- [ ] Sensitive data in logs
- [ ] Service-to-Service dependency injection (use UseCase to combine)
- [ ] Query methods in Service (move to QueryService)
- [ ] Command methods in QueryService (move to Service)
