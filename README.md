# 🎸 중고 악기 거래 플랫폼

중고 악기를 사고팔 수 있는 웹 애플리케이션입니다.  
Spring Boot REST API + Vue 3 SPA 구조로 구성되어 있습니다.

---

## 📌 주요 기능

### 🔐 인증
- 이메일/비밀번호 회원가입 · 로그인
- JWT 기반 인증 (Access Token + Refresh Token 자동 갱신)
- Silent refresh: 만료된 Access Token을 Axios interceptor에서 자동 재발급

### 🎵 악기 게시글
- 카테고리별 악기 등록 (기타, 피아노, 드럼, 베이스, 바이올린, 관악기, 현악기 등)
- 키워드 · 카테고리 · 가격 범위 · 지역 · 판매상태 복합 검색 (QueryDSL)
- 다중 이미지 업로드 (썸네일 자동 지정)
- 판매중 / 예약중 / 판매완료 상태 관리
- Soft Delete (삭제 시 `deleted_at` 기록, 목록에서 자동 제외)
- 조회수 자동 증가

### 🤝 거래 요청
- 구매 희망자가 판매자에게 거래 요청 메시지 전송
- 판매자 수락 시 악기 상태 → **예약중** 자동 변경
- 거래 완료 처리 시 악기 상태 → **판매완료** 자동 변경
- 거래 거절 / 구매자 취소 지원
- 중복 요청 방지 (진행 중인 요청이 있으면 재요청 불가)

### ❤️ 찜하기
- 관심 악기 찜 추가 / 해제
- 찜 목록 페이지 제공

### 👤 마이페이지
- 내 판매 목록
- 찜 목록
- 거래 관리 (받은 요청 목록 / 보낸 요청 목록)

---

## 🛠 기술 스택

### Backend
| 항목 | 내용 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| ORM | Spring Data JPA + Hibernate 6 |
| 동적 쿼리 | QueryDSL 5.0.0 (Jakarta) |
| 인증 | Spring Security 6 + JWT (jjwt 0.12.6) |
| Database | MySQL 8.x |
| Build | Gradle |

### Frontend
| 항목 | 내용 |
|------|------|
| Framework | Vue 3 (Composition API) |
| Build Tool | Vite 4 |
| 상태 관리 | Pinia |
| 라우팅 | Vue Router 4 |
| HTTP Client | Axios |
| Node.js | 16+ |

---

## 📁 프로젝트 구조

```
instrument-trade/
├── backend/                  # Spring Boot 프로젝트
│   └── src/main/java/hello/instrumenttrade/
│       ├── common/           # ApiResponse, BaseTimeEntity, 예외 처리
│       ├── config/           # Security, QueryDSL, FileUpload 설정
│       ├── security/         # JWT Provider, Filter, UserDetailsService
│       ├── domain/           # JPA 엔티티 (User, Instrument, TradeRequest 등)
│       ├── repository/       # JPA Repository + QueryDSL 구현체
│       ├── service/          # 비즈니스 로직
│       ├── controller/       # REST API 컨트롤러
│       ├── dto/              # Request / Response DTO
│       └── enums/            # TradeStatus, TradeRequestStatus 등
└── frontend/                 # Vue 3 프로젝트
    └── src/
        ├── api/              # Axios 인스턴스 + 도메인별 API 모듈
        ├── stores/           # Pinia 스토어 (auth, categories)
        ├── router/           # Vue Router (인증 가드 포함)
        ├── pages/            # 페이지 컴포넌트
        │   ├── auth/         # 로그인, 회원가입
        │   ├── instruments/  # 목록, 상세, 등록, 수정
        │   └── user/         # 마이페이지, 거래관리, 찜목록
        └── components/       # 공통 컴포넌트 (헤더, 페이지네이션 등)
```

---

## 🚀 실행 방법

### 사전 준비
- Java 21
- Node.js 16+
- MySQL 8.x

### 1. 데이터베이스 생성

```sql
CREATE DATABASE instrument_trade
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 2. 백엔드 설정

```bash
cd backend/src/main/resources
cp application.yml.example application.yml
# application.yml 열어서 DB 계정 정보, JWT secret 입력
```

```bash
cd backend
./gradlew bootRun
# 서버 기동: http://localhost:8080
```

> 최초 실행 시 `ddl-auto: create`로 설정하면 테이블이 자동 생성됩니다.  
> 이후에는 `update`로 변경하세요.

### 3. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
# 개발 서버: http://localhost:5173
```

> Vite dev server가 `/api`, `/uploads` 요청을 `localhost:8080`으로 프록시합니다.

---

## 🔌 API 목록

### 인증 (`/api/v1/auth`)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/signup` | 회원가입 |
| POST | `/login` | 로그인 (Access + Refresh Token 반환) |
| POST | `/refresh` | Access Token 재발급 |
| POST | `/logout` | 로그아웃 |

### 악기 게시글 (`/api/v1/instruments`)
| Method | Endpoint | Auth | 설명 |
|--------|----------|------|------|
| GET | `/` | - | 목록 검색 (키워드/카테고리/가격/지역/상태) |
| POST | `/` | ✓ | 게시글 등록 |
| GET | `/{id}` | - | 상세 조회 (조회수 +1) |
| PUT | `/{id}` | ✓ | 게시글 수정 |
| DELETE | `/{id}` | ✓ | 게시글 삭제 (Soft Delete) |
| PATCH | `/{id}/status` | ✓ | 판매 상태 변경 |
| POST | `/{id}/images` | ✓ | 이미지 업로드 |
| DELETE | `/{id}/images/{imageId}` | ✓ | 이미지 삭제 |
| POST | `/{id}/wishlist` | ✓ | 찜 추가 |
| DELETE | `/{id}/wishlist` | ✓ | 찜 해제 |
| GET | `/{id}/my-status` | ✓ | 찜 여부 + 거래요청 여부 조회 |

### 거래 요청
| Method | Endpoint | Auth | 설명 |
|--------|----------|------|------|
| POST | `/api/v1/instruments/{id}/trade-requests` | ✓ | 거래 요청 생성 |
| PATCH | `/api/v1/trade-requests/{id}/accept` | ✓ | 수락 (판매자) |
| PATCH | `/api/v1/trade-requests/{id}/reject` | ✓ | 거절 (판매자) |
| PATCH | `/api/v1/trade-requests/{id}/complete` | ✓ | 거래 완료 (판매자) |
| PATCH | `/api/v1/trade-requests/{id}/cancel` | ✓ | 취소 (구매자) |
| GET | `/api/v1/users/me/trade-requests/sent` | ✓ | 보낸 요청 목록 |
| GET | `/api/v1/users/me/trade-requests/received` | ✓ | 받은 요청 목록 |

### 사용자 / 카테고리
| Method | Endpoint | Auth | 설명 |
|--------|----------|------|------|
| GET | `/api/v1/users/me` | ✓ | 내 프로필 |
| PUT | `/api/v1/users/me` | ✓ | 프로필 수정 |
| GET | `/api/v1/users/{id}/profile` | - | 타인 프로필 |
| GET | `/api/v1/users/me/instruments` | ✓ | 내 게시글 목록 |
| GET | `/api/v1/users/me/wishlists` | ✓ | 찜 목록 |
| GET | `/api/v1/categories` | - | 카테고리 목록 |

---

## 🔄 거래 상태 흐름

```
[구매자] 거래 요청
       ↓
  REQUESTED ──────────────→ REJECTED  (판매자 거절)
       │                    CANCELLED (구매자 취소)
       ↓ 판매자 수락
  ACCEPTED  → 악기 상태: 예약중
       │
       ↓ 판매자 완료 처리
  COMPLETED → 악기 상태: 판매완료
```

---

## ⚙️ 환경변수 / 설정 파일

| 파일 | 설명 |
|------|------|
| `backend/src/main/resources/application.yml` | 실제 설정 파일 (**git 제외**) |
| `backend/src/main/resources/application.yml.example` | 설정 항목 양식 (git 포함) |

`application.yml`에서 반드시 직접 입력해야 하는 항목:

```yaml
spring.datasource.username:   # DB 사용자명
spring.datasource.password:   # DB 비밀번호
jwt.secret:                   # 256-bit 이상 랜덤 문자열
```

JWT secret 생성 예시:
```bash
openssl rand -hex 32
```
