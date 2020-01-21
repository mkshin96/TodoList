# TodoList

> Spring MVC와 JPA를 공부하기 위해, ToDoList를 구현한 프로젝트입니다.

### 프로젝트 목적
- Spring Boot, JPA학습
- Spring Security를 사용한 ID/Password 로그인, 회원가입 구현
- OAuth2 인증방식 구현
- Jupiter를 사용한 테스트 코드 작성
- APACHE JMETER 기본 사용법 학습 

### 개발 환경
- [SpringBoot2.2.2](https://start.spring.io/)
- [Java8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [MySQL Server 5.7](https://www.mysql.com/)
- `Gradle5.1`

### 사용 방법
1. 우측 상단 `Clone or download` 클릭
2. `Download ZIP` 클릭
3. 압축 풀기
4. `IntelliJ`에서 열기
5. `/src/main/resources/` 경로에 `application.yml` 만들기
6. `application.yml`에 다음과 같은 코드 추가

~~~yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/tdl?serverTimezone=Asia/Seoul
    username: TDLuser
    password:
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: create
  jackson:
    deserialization:
      fail-on-unknown-properties: true
  security:
    oauth2:
      client:
        registration:
          facebook:
            client-id: {Facebook app id}
            client-secret: {Facebook app secret}
logging:
  level:
    org:
      hibernate:
        SQL: DEBUG

app-properties:
  test-email: user@email.com
  test-password: password

custom:
  oauth2:
    kakao:
        client-id: {Kakao REST API KEY}
~~~
7. [Kakao developers](https://developers.kakao.com/)에서 앱 만들기
7. `설정` -> `일반` -> `REST API키` 복사
8. `application.yml` -> `custom: oauth: kakao: client-id:`에 붙여넣기
9. [facebook for developers](https://developers.facebook.com/)에서 앱 만들기
10. `설정` -> `기본설정` -> `앱 ID`, `앱 시크릿 코드` 복사
11. `application.yml` -> `spring: security: oauth2: client: registration: facebook: client-id, client-secret`에 붙여넣기
12. `MySQL`에서 스키마 `tdl` 생성
13. `MySQL`에서 유저 `TDLuser` 생성
14. 유저 `TDLuser`에게 스키마 `tdl`의 모든 권한 부여
12. `실행`
