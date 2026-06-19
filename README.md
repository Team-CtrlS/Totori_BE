# <img width="40" height="40" alt="White" src="https://github.com/user-attachments/assets/e18ada55-9880-445e-84a4-0bd955c9ab07" /> 토토리(Totori)

> 프로젝트 기간 | 2025.09.01 ~ ing
<img width="1628" height="892" alt="스크린샷 2026-06-19 오후 6 48 47" src="https://github.com/user-attachments/assets/f785a1b5-997c-41a3-a19d-247f33bdafbe" />

# Totori_BE
> 토토리는 난독 아동과 읽기에 어려움을 겪는 아동에게 관심사 기반 맞춤 동화, 수준별 읽기 학습, 취약 음운 퀴즈를 제공하는 모바일 학습 서비스입니다. 보호자와 특수교사는 정량화된 리포트를 통해 아동의 학습 현황과 오류 추세를 확인할 수 있습니다.

`Totori_BE`는 토토리의 **중앙 API 및 데이터 서버**입니다. iOS 앱의 요청을 인증하고 회원·동화·학습 데이터를 관리하며, AI 서버와 이미지·음성 생성 서비스를 하나의 학습 흐름으로 연결합니다.

### ⭐️ 주요 기능

- 일반 로그인, JWT 인증, Kakao OAuth
- 아동·보호자 회원 관리와 연결 코드 발급
- 맞춤 동화 생성 요청 및 콘텐츠 저장
- 낭독 결과·퀴즈·배지·출석 관리
- 주간·전체 학습 리포트 제공

<p align="center">
<img width="1920" height="1080" alt="10 주요 기능" src="https://github.com/user-attachments/assets/8de2b2ae-832b-4afa-a6c3-f75e4f7ea905" />
<img width="1920" height="1080" alt="11 주요 기능" src="https://github.com/user-attachments/assets/942c517e-c511-45f0-8054-55ed1c77fe1e" />
<img width="1920" height="1080" alt="12 주요 기능" src="https://github.com/user-attachments/assets/17073971-2dfa-44bb-a38e-41da8ffd0c5d" />
<img width="1920" height="1080" alt="13 주요 기능" src="https://github.com/user-attachments/assets/1e72be1a-dba8-4158-bb8a-eda1d235685f" />
</p>

### 📺 시연 영상

[토토리 데모 영상 보기](https://youtu.be/WHZ7pYWFDvs)

## 🐣 담당 팀원

| 정윤아 | 복지희 | 정유진 |
| :---: | :---: | :---: |
| <img width="120" alt="정윤아 GitHub 프로필" src="https://avatars.githubusercontent.com/u/166522604?v=4" /> | <img width="120" alt="복지희 GitHub 프로필" src="https://avatars.githubusercontent.com/u/129582481?v=4" /> | <img width="120" alt="정유진 GitHub 프로필" src="https://avatars.githubusercontent.com/u/127232686?v=4" /> |
| [`@laura-jung`](https://github.com/laura-jung) | [`@jettieb`](https://github.com/jettieb) | [`@nomellc`](https://github.com/nomellc) |
| **Backend Developer** | **Backend Developer** | **Backend·AI Developer** |
|  |  |  |

## ⚒️ 기술 스택

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- Spring Security·OAuth 2.0·JWT
- MySQL
- Redis
- Spring WebFlux `WebClient`
- AWS S3 SDK
- Springdoc OpenAPI
- Gradle Wrapper

## 📁 프로젝트 구조

```text
Totori_BE/
├── src/main/java/ctrlS/totori/
│   ├── attendance/              # 출석
│   ├── auth/                    # 로그인·회원가입·OAuth
│   ├── badge/                   # 배지
│   ├── book/                    # 동화 생성·조회·읽기 기록
│   ├── connect/                 # 보호자와 아이 연결
│   ├── member/                  # 회원 정보
│   ├── quiz/                    # 퀴즈 생성·채점
│   ├── report/                  # 주간·종합 학습 리포트
│   └── global/                  # 보안, 예외, Redis, S3, 공통 응답
├── src/main/resources/
│   └── application.yml          # 직접 생성, Git 추적 제외
├── src/test/
├── build.gradle
├── settings.gradle
└── gradlew
```

## 실행 환경

- Java 17
- MySQL 8
- Redis 7
- 실행 중인 [`Totori_AI`](https://github.com/Team-CtrlS/Totori_AI) 서버

동화의 이미지·음성 기능까지 사용하려면 AWS S3, Stability AI, ElevenLabs 자격 증명이 필요합니다. Kakao 로그인을 사용할 경우 Kakao Developers 애플리케이션도 필요합니다.

## 설치 및 설정

### 1. 저장소 복제

백엔드의 기본 브랜치는 `develop`입니다.

```bash
git clone -b develop https://github.com/Team-CtrlS/Totori_BE.git
cd Totori_BE
```

### 2. MySQL과 Redis 실행

로컬 서비스를 사용하거나 Docker로 실행할 수 있습니다.

```bash
docker run --name totori-mysql \
  -e MYSQL_ROOT_PASSWORD=totori \
  -e MYSQL_DATABASE=totori \
  -p 3306:3306 -d mysql:8

docker run --name totori-redis \
  -p 6379:6379 -d redis:7-alpine
```

### 3. 애플리케이션 설정

`src/main/resources/application.yml`을 생성합니다. 이 파일과 환경별 설정 파일은 `.gitignore`에 포함되어 있습니다.

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/totori?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: totori
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
  data:
    redis:
      host: localhost
      port: 6379
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: your-kakao-rest-api-key
            client-secret: your-kakao-client-secret
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: profile_nickname, account_email
        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id

jwt:
  secret: replace-with-base64-secret

fastapi:
  base-url: http://localhost:8000

cloud:
  aws:
    credentials:
      access-key: your-aws-access-key
      secret-key: your-aws-secret-key
    region:
      static: ap-northeast-2
    s3:
      bucket: your-s3-bucket

stability:
  api-key: your-stability-api-key

elevenlabs:
  api-key: your-elevenlabs-api-key
  base-url: https://api.elevenlabs.io/v1
  default-voice-id: your-voice-id
  default-model-id: eleven_multilingual_v2
  timeout-seconds: 60

tts:
  parallelism: 3
```

JWT 키는 256비트 이상의 값을 Base64로 인코딩해야 합니다.

```bash
openssl rand -base64 32
```

출력값을 `jwt.secret`에 입력하세요.

Kakao 로그인을 사용한다면 Kakao Developers 콘솔에 아래 Redirect URI를 등록합니다.

```text
http://localhost:8080/login/oauth2/code/kakao
```

AI 서버의 기본 주소는 `http://localhost:8000`입니다. 다른 호스트나 포트를 사용한다면 `fastapi.base-url`을 변경하세요.

## 실행

```bash
./gradlew bootRun
```

기본 포트는 `8080`입니다.

## 빌드 및 테스트

```bash
# 전체 빌드와 테스트
./gradlew clean build

# 테스트만 실행
./gradlew test

# 컴파일 확인
./gradlew classes
```

## API 문서

서버 실행 후 다음 주소에서 확인할 수 있습니다.

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>

주요 API 그룹:

```text
/api/auth          인증·회원가입
/api/members       회원 정보
/api/connect       보호자·아이 연결
/api/books         동화
/api/quiz          퀴즈
/api/reports       학습 리포트
/api/badges        배지
/api/child/attendance  출석
```

## 외부 서비스

| 서비스 | 용도 | 설정 |
| --- | --- | --- |
| Totori_AI | 동화·퀴즈·음성 분석 | `fastapi.base-url` |
| AWS S3 | 이미지·오디오 저장 | `cloud.aws.*` |
| Stability AI | 동화 이미지 생성 | `stability.api-key` |
| ElevenLabs | TTS 오디오 생성 | `elevenlabs.*` |
| Kakao OAuth | 소셜 로그인 | `spring.security.oauth2.*` |

## 🧪 데이터와 실험 결과

### 실행 데이터

별도의 사전 구축 DB 덤프는 필요하지 않습니다. 빈 `totori` 데이터베이스를 생성하고 `spring.jpa.hibernate.ddl-auto=update`로 실행하면 JPA 엔티티를 기준으로 테이블이 만들어집니다. 회원과 학습 데이터는 앱 또는 Swagger API를 통해 생성합니다.

| 데이터 | 저장 위치 | 재현 방법 |
| --- | --- | --- |
| 회원·동화·퀴즈·리포트 | MySQL | 빈 DB 생성 후 API 호출 |
| JWT·로그아웃 토큰·연결 코드 | Redis | Redis 실행 후 애플리케이션이 자동 생성 |
| 동화 이미지·음성 | AWS S3 | S3 버킷과 자격 증명 설정 |
| 읽기 분석 임시 결과 | AI 서버의 Redis | 낭독 분석 API 호출 |

### 발표자료 기준 성능 개선 결과

동화 생성 과정에서 이미지 생성과 TTS 생성을 순차 처리하지 않고 병렬 처리하도록 개선했습니다.

| 항목 | 개선 전 | 개선 후 |
| --- | ---: | ---: |
| 동화 생성 처리 시간 | 약 60초 | 약 42초 |
| 단축률 | - | 약 30% |

측정 시간은 발표자료의 개발 환경 기준이며, 외부 API 응답 시간과 네트워크 환경에 따라 달라질 수 있습니다.

### 재현 시 필요한 외부 자원

OpenAI, AWS S3, Stability AI, ElevenLabs, Kakao OAuth의 실제 키와 생성 결과물은 보안 및 용량 문제로 저장소에 포함하지 않습니다. README의 `application.yml` 예시에 각자 발급한 값을 입력해야 전체 기능을 재현할 수 있습니다.

## 🚀 문제 해결

#### 설정값을 찾지 못해 서버가 시작되지 않습니다

현재 저장소는 `application.yml`, `application-local.yml`, `application-prod.yml`을 추적하지 않습니다. `src/main/resources/application.yml`을 직접 생성했는지 확인하세요.

#### JWT 키 오류가 발생합니다

일반 문자열이 아니라 Base64로 인코딩된 충분히 긴 키가 필요합니다. `openssl rand -base64 32`로 새 키를 생성하세요.

#### AI 요청이 실패합니다

`Totori_AI`가 실행 중인지 확인한 뒤 다음 주소를 호출해 보세요.

```bash
curl http://localhost:8000/
```

백엔드의 `fastapi.base-url`과 AI 서버의 실제 주소도 일치해야 합니다.

### 알려진 API 경로 차이

`FastApiStoryClient`에는 `/ai/story/generate` 호출이 남아 있지만 현재 AI 저장소가 제공하는 음성 기반 동화 생성 경로는 `/ai/story/make`입니다. `/generate`를 사용하는 기능은 두 저장소의 계약을 먼저 맞춰야 합니다.

## 📁 관련 저장소

- iOS 앱: [`Totori_FE`](https://github.com/Team-CtrlS/Totori_FE)
- AI 서버: [`Totori_AI`](https://github.com/Team-CtrlS/Totori_AI)

## 라이선스

현재 별도의 라이선스 파일이 없습니다.
