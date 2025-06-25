# ![favicon.ico](src/main/resources/static/favicon.ico) URLib

> 관심 있는 웹페이지를 북마크로 저장하고 공유할 수 있는 링크 큐레이션 플랫폼입니다.  
> 다른 유저를 팔로우하고, 북마크를 탐색하거나 좋아요와 댓글로 소통할 수 있습니다.

---

## 🛠️ 기술 스택

- **Backend**: Java 17, Spring Boot 3.4.4, Spring Security, JPA
- **Database**: MySQL (AWS RDS), Redis (AWS ElastiCache), H2
- **Authentication**: JWT, OAuth2 (Kakao)
- **Infrastructure**: Docker, GitHub Actions, AWS EC2, Nginx

---

## 🛠️ 프로젝트 아키텍쳐

![Image](https://github.com/user-attachments/assets/70b882f3-3061-4365-bd16-43ddec8fb62c)


---

## 🛠️ ERD

![Image](https://github.com/user-attachments/assets/f5626afc-3dc5-475b-9394-c003fcd2883e)


---

## ✨ 주요 기능

- 이메일/비밀번호 기반 회원가입 및 로그인 (JWT 인증)
- Kakao OAuth2 로그인
- 북마크 생성, 수정, 삭제
- 북마크 좋아요 및 댓글 기능
- 팔로우 / 언팔로우 기능
- 북마크 public 공개 설정
- 공유 북마크 초대 및 권한 관리(editor, reader)

---

## 📁 프로젝트 구조

```bash
src/main/java/com.heez.urlib/
│
├── domain/
│   ├── auth/        # 인증 (JWT, OAuth2)
│   ├── bookmark/    # 북마크 및 폴더
│   ├── comment/     # 댓글
│   ├── link/        # 개별 링크
│   ├── member/      # 회원
│   └── tag/         # 해시태그
├── global/
│   ├── common.domain/  # 공통 엔티티/기능
│   ├── config/         # 전역 설정
│   ├── error/          # 예외 처리
│   ├── log/            # 로깅 처리
│   └── swagger/        # Swagger 설정
```

---

## 🔐 인증 및 보안 전략

- Access / Refresh Token 기반 JWT 인증 방식 적용
- Access Token은 헤더로, Refresh Token은 쿠키로 관리
- 로그인 시 두 토큰 모두 발급되며, Refresh Token은 Redis에 저장 (TTL로 자동 만료 처리)
- Access Token 만료 시, 유효한 Refresh Token으로 Access / Refresh 토큰 모두 재발급
- 로그아웃 시 Redis에서 Refresh Token을 제거하여 무효화 처리

--- 

## 📋 CI/CD 파이프라인

> GitHub Actions와 Docker, Nginx를 활용하여 자동 배포 파이프라인을 구성

- **1. Build & Test**
    - main 브랜치에 Push 또는 PR 발생 시 실행
    - Java 17 + Gradle 빌드 & JUnit5 테스트
    - JaCoCo로 테스트 커버리지 측정

- **2. Docker 이미지 배포**
    - gradle build 후 이미지 생성
    - Docker Hub에 push

- **3. EC2에 무중단 배포**
    - Actuator Health Check로 배포 안정성 검증
    - Nginx 리버스 프록시 설정을 통해 Blue Green 무중단 배포
    - 실패 시 자동 롤백

---

## 🔧 배포 스크립트 주요 로직

- `.env` 파일을 자동 로드, 배포 후 삭제 처리
- 실행 중인 컨테이너 감지 → 반대 컨테이너로 신규 배포
- Health Check (`/actuator/health`)를 통한 정상 기동 확인
- Nginx 설정 자동 전환 (Blue/Green 트래픽 전환)
- 실패 시 자동 종료 및 롤백 대응

---

## ✅ 서비스 상태 확인

- 배포 후 `/actuator/health`를 활용해 컨테이너 상태 확인
- GitHub Actions Workflow를 통해 배포 과정의 성공 여부 추적
- [Swagger UI](http://3.36.136.75/swagger-ui/index.html)에서 API 테스트 가능

---

## ✅ 테스트 및 코드 품질

- **테스트 프레임워크**: JUnit 5
- **테스트 인프라**: Embedded Redis, H2 Database (In-Memory)
- **커버리지 도구**: JaCoCo
- **목표 커버리지**: 라인 및 브랜치 커버리지 최소 70%
- **테스트 범위**: Controller, Service 레이어 단위 테스트 및 예외 처리 검증

---

## 🗺️ TODO

- [ ] 해시태그 기반 북마크 분류
- [ ] Elasticsearch를 활용한 태그/링크 전체 검색
- [ ] S3 기반 프로필 이미지 업로드
- [ ] 사용자 신고 / 차단 기능

---

## 🙋🏻‍♂️ 기타

- 문의 또는 피드백: [123rkdrkd@gmail.com] 또는 GitHub Issue
