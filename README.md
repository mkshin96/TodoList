# TodoList

> Spring MVC와 JPA를 공부하기 위해, ToDoList를 구현한 프로젝트입니다.

### 프로젝트 목적
- Spring Boot, JPA학습
- Spring Security학습을 통한 로그인, 회원가입 구현
- Junit4를 사용한 테스트 코드 작성

### 개발 환경
- [SpringBoot2.1.3](https://start.spring.io/)
- [Java8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [MySQL Server 5.7](https://www.mysql.com/)
- `Gradle5.2.1`

### 사용 방법
1. 우측 상단 `Clone or download` 클릭
2. `Download ZIP` 클릭
3. 압축 풀기
4. `IntelliJ`에서 열기
5. `OAuth2`
  5-1. [Kakao developers](https://developers.kakao.com/)에서 앱 만들기
  5-2. `설정` -> `일반` -> `REST API키` 복사
  5-3. `application.yml` -> `custom: oauth: kakao: client-id:`에 붙여넣기
  5-4. [facebook for developers](https://developers.facebook.com/)에서 앱 만들기
  5-5. `설정` -> `기본설정` -> `앱 ID`, `앱 시크릿 코드` 복사
  5-6. `application.yml` -> `spring: security: oauth2: client: registration: facebook: client-id, client-secret`에 붙여넣기
6. 실행
