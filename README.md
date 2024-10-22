# Auth Template
Java 21, Spring Boot를 사용한 Auth Template입니다.

## API Docs
API Docs는 Swagger를 사용하였으며 다음과 같은 단계로 확인하실 수 있습니다.

1. 폴더 생성
2. 폴더 진입 후 터미널에 아래 명령어 복사 후 붙여넣기
```shell
git init
git remote add origin https://github.com/lgwk42/auth-template.git
git pull origin main
```
3. 서버 실행
4. 아래 링크로 접속하시면 API Docs를 확인하실 수 있습니다.
```
http://localhost:8080/swagger-ui/index.html#/

```

## YML
```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${ schema }?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: ${ password }

  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQL8Dialect
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    hibernate:
      ddl-auto: update
    show-sql: true

application:
  jwt:
    secretKey: aa71d145159683a27c3c1ddc4c2f5bfbd1e979a4708355fa9ae614ef61bc8f90
    expiration: 86400000    # 24시간
    refreshExpiration: 604800000   # 7일
```
password - DB 비밀번호 (MySQL 기준)
schema - DB 스키마 이름

YML은 MySQL과 JPA를 사용하는 기준으로 작성되었습니다.

## Gradle
버전 관리는 Gradle를 사용하였으며 아래와 같은 라이브러리를 사용하였습니다.
```gradle
// jpa
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

// security
implementation 'org.springframework.boot:spring-boot-starter-security'

// lombok
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'

// mysql
runtimeOnly 'com.mysql:mysql-connector-j'

// junit
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

// swagger
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2'

// jwt
implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
implementation 'io.jsonwebtoken:jjwt-impl:0.12.5'
implementation 'io.jsonwebtoken:jjwt-jackson:0.12.5'

// web
implementation 'org.springframework.boot:spring-boot-starter-web'

// validation
implementation 'org.springframework.boot:spring-boot-starter-validation'
```
