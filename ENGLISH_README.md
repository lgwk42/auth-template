# Auth Template
This template is built with Java 21 and Spring Boot. Before using it, please modify the default settings in the YML file to match your configuration.
> When using this template, please give the repository a Star. For other inquiries, please contact via personal email.
> 
+ Please note that all returned messages are written in Korean.

**How to Use**
<p> 
  (Git and Java 21 must be pre-installed and YML configuration must be completed!) <br>
  Enter the following command in the terminal 
</p>
```````text
git clone https://github.com/lgwk42/auth-template.git
```````

Navigate to the created project directory and execute the following commands in order <br>
- Linux / MacOS
```````shell
   ./gradlew build
   ./gradlew bootRun
   java -jar build/libs/auth-template-0.0.1-SNAPSHOT.jar
```````
- Window
```````shell
   gradlew build
   gradlew bootRun
   java -jar build/libs/auth-template-0.0.1-SNAPSHOT.jar
```````

# API DOCS

## Sign Up
```POST /auth/sign-up``` <br>
**Request**
``````json
{
  "email": "test@gmail.com",
  "name": "테스트",
  "password": "testPassword1234!"
}
``````
|Parameter|Type|Description|
|---------|----|-----------|
|Email|String|Email (must follow email format like test@gmail.com)|
|Name|String|Name|
|Password|String|Password (must include uppercase/lowercase letters, special characters, and be at least 10 characters long)|

**Response**
- 201 CREATED
``````json
{
  "status": 201,
  "message": "회원가입 성공"
}
``````
- 403 CONFLICT
``````json
{
  "status": 403,
  "message": "이미 존재하는 유저입니다."
}
``````

## Sign In
```POST /auth/sign-in``` <br>
**Request**
`````json
{
  "email": "test@gmail.com",
  "password": "testPassword1234!"
}
`````
|Parameter|Type|Description|
|---------|----|-----------|
|Email|String|Email (must follow email format like test@gmail.com)|
|Password|String|Password (must include uppercase/lowercase letters, special characters, and be at least 10 characters long)|

**Response**
- 200 OK
`````json
{
  "status": 200,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.access.payload.signature",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh.payload.signature"
  }
}
`````
- 404 NOT FOUND
`````json
{
  "status": 404,
  "message": "유저를 찾을 수 없습니다."
}
`````
- 400 BAD REQUEST
`````json
{
  "status": 400,
  "message": "비밀번호가 맞지 않습니다."
}
`````

## Refresh
```POST /auth/refresh``` <br>
**Request**
````json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh.payload.signature"
}
````
|Parameter|Type|Description|
|---------|----|-----------|
|RefreshToken|String|Refresh token|

**Response**
- 200 OK
````json
{
  "status": 200,
  "message": "재발급 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.newAccess.payload.signature"
  }
}
````

## Get User
```GET /user``` <br>
**Request** <br>
Header (Bearer Token)
|Parameter|Type|Description|
|---------|----|-----------|
|AccessToken|String|Access token|

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
````