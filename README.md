# 📸 Snapia

> SNS 기반 사업자 예약 플랫폼 — 일반 사용자와 사업자가 함께하는 소셜 서비스

<br>

## 📌 프로젝트 소개

Snapia는 인스타그램 같은 SNS 기능에 **네이버 예약과 같은 사업자 예약 시스템**을 결합한 플랫폼입니다.

일반 사용자는 피드, 스토리, 하이라이트, 실시간 DM 등 SNS 기능을 이용하고,  
사업자 계정은 서비스 항목 등록 · 예약 수락/거절 · 고객과의 실시간 채팅을 통해 비즈니스를 운영할 수 있습니다.

| | |
|---|---|
| **개발 기간** | 2025.12 ~ 2026.02 (약 1개월 반) |
| **개발 인원** | 1인 (개인 프로젝트) |
| **GitHub** | https://github.com/hawnghasung/Snapia_project1 |

<br>

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| ORM | Spring Data JPA |
| Security | Spring Security |
| Template Engine | Thymeleaf |
| Real-time | WebSocket (STOMP) |
| Database | MariaDB |
| Media | FFmpeg |
| Build Tool | Gradle |

<br>

## ✅ 주요 기능

### 👤 일반 사용자
- Spring Security 기반 회원가입 / 로그인
- 공개 / 비공개 계정 설정 및 팔로우 요청 · 수락
- 게시글 업로드 (이미지 / 영상), 좋아요, 댓글, 해시태그
- 스토리 업로드 (24시간 후 자동 만료) 및 조회자 목록 확인
- 하이라이트 (만료된 스토리를 영구 보관)
- 팔로우 / 알림 (팔로우 요청, 수락 등)
- WebSocket(STOMP) 기반 1:1 실시간 DM
- 사업자 계정에 예약 요청

### 🏢 사업자 계정
- 사업자 프로필 등록 (업종, 소개 등)
- 서비스 항목 등록
- 예약 요청 수락 / 거절
- 예약 관련 알림 수신
- 고객과 실시간 채팅

<br>

## 🗂 DB 설계 (ERD)

> 전체 테이블 수를 최소화하는 방향으로 설계했습니다.  
> 아래 ERD 이미지 또는 [ERD 원본 보기](#) 를 참고해주세요.

<img width="4320" height="6380" alt="ERD" src="https://github.com/user-attachments/assets/c4cdc80e-c62f-45c3-b7d5-ce37fae128d0" />

### 📋 전체 테이블 목록

| 테이블 | 설명 |
|--------|------|
| `users` | 회원 정보 (일반/사업자 구분 포함) |
| `business_profile` | 사업자 프로필 (users와 1:1 관계) |
| `post` | 게시글 (이미지/영상 타입 구분) |
| `story` | 스토리 (24시간 만료 시각 저장) |
| `story_view_users` | 스토리 조회자 기록 |
| `highlight` | 하이라이트 |
| `highlight_content` | 하이라이트에 포함된 스토리 (N:M 연결) |
| `comment` | 댓글 (자기 참조로 대댓글 구현) |
| `comment_like` | 댓글 좋아요 |
| `likes` | 게시글 좋아요 |
| `hash_tag` | 해시태그 |
| `post_hash_tag` | 게시글-해시태그 연결 (N:M) |
| `follow_list` | 팔로우 관계 및 수락 상태 |
| `room` | DM 채팅방 |
| `message` | DM 메시지 (자기 참조로 답장 구현) |
| `message_read_status` | 메시지 읽음 상태 분리 관리 |
| `notification` | 일반 알림 (팔로우 등) |
| `business_notification` | 사업자 알림 (예약 관련) |
| `appointment` | 예약 정보 |
| `service_item` | 사업자 서비스 항목 |
| `review` | 리뷰 (개발 예정) |
| `inquiry` | 문의 (개발 예정) |
| `faq` | FAQ (개발 예정) |

<br>

## ⚙️ 설계 결정 & 문제 해결

### ① DM 시스템 테이블 최소화
**1:1 채팅에 최적화된 스키마 설계로 조인 비용 절감**

**문제**

1:1 채팅만 지원하는 구조임에도 일반적인 방식대로 `room_member` 테이블을 두면, 메시지 조회 시마다 불필요한 조인이 추가되고 테이블 수도 늘어납니다. 읽음 여부를 `message`에 인라인으로 두면 다중 수신자 확장 시 컬럼 폭증 문제가 생깁니다.

**결정 근거 & 트레이드오프**

- `room_member` 별도 테이블 제거 → `room.sender_id / receiver_id` 직접 참조로 대체. 그룹채팅 확장은 불가하지만 서비스 범위가 1:1에 한정되므로 감수.
- 읽음 여부를 `message_read_status`로 분리해 메시지 조회와 읽음 처리 책임을 명확히 구분. 읽음 상태 업데이트 시 메시지 행 전체를 건드리지 않아 쓰기 충돌 최소화.
- `message.reply_id FK` 자기 참조로 답장 기능 구현, 별도 테이블 불필요.

**결과**

`room` · `message` · `message_read_status` 3개 테이블로 채팅방 생성, 메시지 송수신, 읽음 처리, 답장을 모두 구현. 참여자 조회 쿼리에서 `room_member` 조인 1회 제거.

---

### ② 단일 `users` 테이블로 일반/사업자 통합 관리
**Spring Security Role 기반 권한 분리와의 정합성 확보**

**문제**

일반 사용자와 사업자를 별도 테이블로 나누면 로그인 시 어느 테이블에서 조회할지 분기 로직이 필요하고, Spring Security의 `UserDetailsService` 구현도 복잡해집니다. 인증 흐름이 두 갈래가 되는 것 자체가 버그 포인트입니다.

**결정 근거 & 트레이드오프**

- 모든 회원을 `users` 하나로 관리하고, 사업자 추가 정보는 `business_profile`에 분리해 1:1 관계로 참조.
- `users.is_business` + Spring Security Role 조합으로 일반/사업자 권한 분리. `UserDetailsService`를 단일 구현으로 유지해 인증 로직을 단순하게 유지.
- 트레이드오프: 사업자 전용 컬럼이 일반 사용자 행에 null로 남을 수 있으나, `business_profile` 1:1 분리로 `users` 테이블 자체는 깔끔하게 유지.

**결과**

로그인 · 인증 · 권한 처리가 단일 흐름으로 정리됨. 사업자 전환 시에도 `business_profile` 행 추가만으로 처리 가능.

---

### ③ 스토리 만료 처리 & 하이라이트 연계
**스케줄러 없이 DB 레벨에서 만료 상태 관리**

**문제**

스토리 24시간 만료를 배치 삭제 방식으로 구현하면 만료 시각 정밀도가 낮고, 하이라이트로 보관한 스토리까지 삭제될 위험이 있습니다. 만료된 스토리와 하이라이트 스토리를 어떻게 구분할지가 핵심이었습니다.

**결정 근거**

- 업로드 시 `story.expired_at = NOW() + 24h`로 저장, 조회 쿼리에 `WHERE expired_at > NOW()` 조건을 붙여 실시간 만료 처리. 별도 스케줄러나 배치 없이 DB 레벨에서 해결.
- 만료된 스토리도 `highlight_content` 테이블을 통해 하이라이트와 N:M으로 연결해 영구 보관. 만료 여부와 무관하게 하이라이트 조회는 별도 쿼리로 처리.

**결과**

스케줄러 없이 만료 처리 구현. 만료된 스토리도 하이라이트 맥락에서는 정상 조회 가능하며, 일반 피드에서는 자동 필터링됨.

<br>

## 🔧 구현 상세

### WebSocket 기반 실시간 DM
- STOMP 프로토콜을 활용한 1:1 채팅방 구현
- 채팅방 입장 시 `message_read_status` 일괄 READ 처리
- `message` 테이블 자기 참조(`reply_id`)로 답장 기능 구현

### Spring Security 인증/인가
- 일반 사용자 / 사업자 Role 분리 및 권한 처리
- 비공개 계정: 팔로우 요청 → 수락 시에만 콘텐츠 접근 가능

### 미디어 처리 (FFmpeg)
- 영상 업로드 시 FFmpeg로 썸네일 자동 생성
- `post`, `story` 테이블의 `media_type` 컬럼으로 이미지/영상 구분 저장

<br>

## 🚧 개발 현황

| 기능 | 상태 |
|------|------|
| 회원가입 / 로그인 | ✅ 완료 |
| 게시글 CRUD (이미지/영상) | ✅ 완료 |
| 해시태그 | ✅ 완료 |
| 스토리 / 하이라이트 | ✅ 완료 |
| 팔로우 / 알림 | ✅ 완료 |
| 실시간 DM (WebSocket) | ✅ 완료 |
| 사업자 프로필 / 예약 | ✅ 완료 |
| 리뷰 / 문의 / FAQ | 🚧 개발 예정 |
| 팔로우 추천 | 🚧 개발 예정 |
| 배포 (AWS EC2 + RDS) | 🚧 예정 |

<br>

## 🚀 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/hawnghasung/Snapia_project1.git
cd Snapia_project1

# 2. application.properties 설정
# src/main/resources/application.properties 에 아래 항목 설정 필요
# - spring.datasource.url
# - spring.datasource.username / password
# - 파일 업로드 경로
# - FFmpeg 경로

# 3. 빌드 및 실행
./gradlew bootRun
```

> **사전 설치 필요**: MariaDB, FFmpeg (로컬 설치)
