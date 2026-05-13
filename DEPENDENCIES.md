# 의존성

**Gradle에 선언된 라이브러리·빌드 도구**만 정리한다. 배포 환경 변수·비밀값 목록은 여기 없다.

## 툴체인·플러그인

| 항목 | 버전·설명 |
|------|-----------|
| JDK | **21** (`build.gradle` 의 `java.toolchain`) |
| Gradle (Wrapper 설정) | **9.4.1** (`gradle/wrapper/gradle-wrapper.properties`) |
| `java` 플러그인 | 표준 Gradle Java 빌드 |
| `org.springframework.boot` | **4.0.5** |
| Jackson BOM (`ext`) | **2.21.2** (`jackson-bom.version`) |

## Gradle 의존성 선언

아래 좌표는 `build.gradle` 과 동일하다. `org.springframework.boot:*` 버전은 `spring-boot-dependencies` BOM이 관리한다(별도 오버라이드 없음).

| 구분 | Gradle 좌표 |
|------|-------------|
| `implementation` | `org.springframework.boot:spring-boot-starter-web` |
| `implementation` | `org.springframework.boot:spring-boot-starter-data-jpa` |
| `implementation` | `org.springframework.boot:spring-boot-starter-oauth2-resource-server` |
| `implementation` | `org.springframework.boot:spring-boot-starter-validation` |
| `implementation` | `org.springframework.boot:spring-boot-starter-actuator` |
| `implementation` | `org.jspecify:jspecify:1.0.0` |
| `runtimeOnly` | `org.postgresql:postgresql` |

### 참고

- **PostgreSQL**: JDBC 드라이버만 포함한다. DB 서버는 별도로 띄운다.
- **JWT**: `spring-boot-starter-oauth2-resource-server` 로 Bearer JWT(HS256)를 검증한다.
