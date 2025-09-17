# Auth Template
해당 템플릿은 Java 21과 Spring Boot를 통해 제작되었습니다. 사용 시 YML 파일에 있는 기본 세팅을 각자에 맞게 변경하신 후 사용해 주세요.
> 템플릿 사용 시 저장소에 Star 한 번씩 부탁드립니다. 다른 문의사항은 개인 이메일로 부탁드려요.

**사용방법**
터미널에서 아래 명령어 입력
```text
git clone https://github.com/lgwk42/auth-template.git
```
생성된 프로젝트 파일로 들어가 아래 명령어 차례대로 실행 <br>
(사전에 깃과 java 21이 설치되어 있고 YML 설정이 되어 있어야 합니다!)
```shell
./gradlew build
```
```shell
./gradlew bootRun
```

# API DOCS

## Sign Up
```POST /auth/sign-up``` <br>
**Request**
```json
{
  "email": "test@gmail.com",
  "name": "테스트",
  "password": "testPassword1234!"
}
```
|Parameter|Type|Description|
|---------|----|-----------|
|Email|String|이메일 (test@gmail.com 처럼 이메일 형식 준수)|
|Name|String|이름|
|Password|String|비밀번호 (영문 대/소문자, 특수문자 포함, 10자 이상이어야 함)|

**Response**
- 201 CREATED
```json
{
  "status": 201,
  "message": "회원가입 성공"
}
```
- 403 CONFLICT
```json
{
  "status": 403,
  "message": "이미 존재하는 유저입니다."
}
```

## Sign In
```POST /auth/sign-in``` <br>
**Request**
```json
{
  "email": "test@gmail.com",
  "password": "testPassword1234!"
}
```
|Parameter|Type|Description|
|---------|----|-----------|
|Email|String|이메일 (test@gmail.com 처럼 이메일 형식 준수)|
|Password|String|비밀번호 (영문 대/소문자, 특수문자 포함, 10자 이상이어야 함)|

**Response**
- 200 OK
```json
{
  "status": 200,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.access.payload.signature",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh.payload.signature"
  }
}
```
- 404 NOT FOUND
```json
{
  "status": 404,
  "message": "유저를 찾을 수 없습니다."
}
```
- 400 BAD REQUEST
```json
{
  "status": 400,
  "message": "비밀번호가 맞지 않습니다."
}
```

## Refresh
```POST /auth/refresh``` <br>
**Request**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh.payload.signature"
}
```
|Parameter|Type|Description|
|---------|----|-----------|
|RefreshToken|String|Refresh 토큰|

**Response**
- 200 OK
```json
{
  "status": 200,
  "message": "재발급 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.newAccess.payload.signature"
  }
}
```

## Get User
```GET /user``` <br>
**Request** <br>
Header (Bearer Token)
|Parameter|Type|Description|
|---------|----|-----------|
|AccessToken|String|Access 토큰|

**Response**
- 200 OK
```json
{
  "status": 200,
  "message": "조회 성공",
  "data": {
      "email": "test@gmail.com",
      "name": "테스트",
      "userRole": "USER",
      "createdAt": "2024-07-28T09:00:00",
      "modifiedAt": "2024-08-10T18:15:10"
   }
}
```
- 404 NOT FOUND
```json
{
  "status": 404,
  "message": "유저를 찾을 수 없습니다."
}
```
