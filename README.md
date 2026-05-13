# profile-svc

Spring Boot **사용자 프로필 REST API**다. 표시용 닉네임 등을 PostgreSQL에 두고, **JWT(HS256)** 로 상태 없이 인증한다. 클라이언트는 **Authorization: Bearer <token>** 만내며, 쿠키에 실린 토큰은 이 서비스에서 직접 읽지 않는다.

의존성·JDK·Gradle 버전 요약은 **[DEPENDENCIES.md](DEPENDENCIES.md)** 를 본다.

**단일 진실 소스(SOT):** 배포·운영에서 쓰는 값의 기준은 **무조건 `infra` 폴더**(Helm values, 매니페스트, 환경 변수 정의 등)에 있다. 이 저장소의 `application.properties` 와 여기 문서는 편의·개발용 설명이며, 충돌하면 **`infra` 쪽이 정답이다.**

## 목차

- [빠른 시작](#빠른-시작)
- [설정](#설정)
- [프로젝트 구조](#프로젝트-구조)
- [HTTP API](#http-api)

## 빠른 시작

- **JDK 21**, 저장소에 포함된 **Gradle Wrapper** (`./gradlew`) 를 쓴다.
- **빌드:** `./gradlew bootJar`
- **로컬 실행:** `./gradlew bootRun` — DB·JWT 등은 아래 [설정](#설정)을 맞춘다.

## 설정

런타임은 **`src/main/resources/application.properties`** 를 따른다. **DB 연결은 `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` 를 반드시 준다(기본값 없음).** 로컬 `bootRun` 도 동일하게 환경 변수를 맞춘다. `JWT_SECRET`·`SPRING_JPA_HIBERNATE_DDL_AUTO` 만 프로퍼티에 `${변수:기본값}` 가 있다.

**SOT 재확인:** 클러스터·배포에 실제로 쓰는 키·값은 **`infra` 폴더**를 따른다(이 절의 표는 이름·역할 참고용).

| 환경 변수 | 바인딩(요지) | 설명 |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` | JDBC URL (**필수**) |
| `SPRING_DATASOURCE_USERNAME` | `spring.datasource.username` | DB 사용자 (**필수**) |
| `SPRING_DATASOURCE_PASSWORD` | `spring.datasource.password` | DB 비밀번호 (**필수**) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `spring.jpa.hibernate.ddl-auto` | 예: `validate`, `update` |
| `JWT_SECRET` | `jwt.secret` | HS256 검증용 비밀키(UTF-8 **32바이트 이상** 권장). 토큰을 **발급하는 서비스(예: auth-svc)와 같은 값**이어야 한다. |

**JWT:** 이 서비스는 토큰을 **검증만** 한다. 클레임·발급 정책은 발급 쪽과 맞춘다. 자세한 헤더·규칙은 아래 [보안](#보안-구현-요약)을 본다.

기본 HTTP 포트는 **8082** (`server.port`).

## 프로젝트 구조

| 경로 | 역할 |
| --- | --- |
| `controller/` | `ProfileController` |
| `service/` | 프로필 조회·수정 로직 (`UserProfileService`) |
| `dto/profile/` | `ProfileResponse`, `PatchProfileRequest` |
| `db/domain/` | JPA 엔티티 `UserProfile` |
| `db/repository/` | `UserProfileRepository` |
| `security/` | `JwtConfig` |
| `config/` | `SecurityConfig` 등 |

## HTTP API

베이스 URL·리버스 프록시 접두 경로는 이 저장소에서 고정하지 않는다.

### 보안 (구현 요약)

- Spring Security, **무상태**(세션 미사용), **CSRF 비활성화**. CORS는 이 애플리케이션에 두지 않았고, 필요하면 게이트웨이·프록시에서 맞춘다.
- JWT는 **Authorization: Bearer <token>** 만 처리한다.
- **OAuth2 Resource Server** + `JwtDecoder` 가 토큰을 검증한다 (`JwtConfig`, HS256).
- **규칙 순서 (`SecurityConfig`):** 위에서 아래로 먼저 매칭된다.
  1. `GET /actuator/health`, `GET /actuator/info` → 허용.
  2. `GET /profiles` → 허용(배치 조회는 쿼리 `ids` 필요, 아래 엔드포인트 참고).
  3. `GET /profiles/**` → 허용.
  4. `PATCH /profiles/me` → 인증 필요(유효 JWT). `sub` 을 숫자 `userId` 로 파싱한다.
  5. 그 외 → **접근 거부** (`denyAll`).

**토큰 검증 시점에 쓰는 클레임(다른 서비스에서 맞출 때):** HS256, `sub` 는 **숫자 문자열**(사용자 ID). `PATCH /profiles/me` 는 본인 `userId` 와 `sub` 가 일치해야 한다.

### 엔드포인트

| 메서드 | 경로 | 인증 | 비고 |
| --- | --- | --- | --- |
| GET | `/profiles?ids=` | 불필요 | 콤마 구분 정수 목록. **요청 순서·중복 id 유지**, DB에 없는 id는 응답에서 생략. `ids` 비어 있음 → `[]`. 잘못된 형식 → **400**. `ids` 없이 `GET /profiles` 만 호출하면 매칭되는 핸들러가 없어 **거부**된다. |
| GET | `/profiles/{userId}` | 불필요 | 단건 `ProfileResponse`. 없으면 **404**. |
| PATCH | `/profiles/me` | 필요 | 본인 닉네임 수정. 프로필 없으면 생성(기본 닉네임 `user-{userId}`). |
| GET | `/actuator/health`, `/actuator/info` | 불필요 | Actuator |

JSON 은 camelCase 다.

### 응답·요청 필드 요약

- **ProfileResponse:** `userId`, `nickname`
- **PatchProfileRequest (`PATCH /profiles/me`):** `nickname` (선택, 최대 64자). `null` 이면 닉네임 변경 없음.

배치 조회 응답은 `ProfileResponse` 의 **JSON 배열**이다.

### 데이터 (JPA)

테이블 **`user_profiles`:** `user_id`(PK), `nickname`, `bio`, `locale`. `bio`·`locale` 은 엔티티에만 있고 **현재 REST JSON에는 포함하지 않는다**.

스키마 생성·변경은 `spring.jpa.hibernate.ddl-auto` (`SPRING_JPA_HIBERNATE_DDL_AUTO`) 정책에 따라 Hibernate 가 관리한다.
