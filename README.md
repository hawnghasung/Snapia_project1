# 📸 Snapia

> SNS 기반 사업자 예약 플랫폼 — 일반 사용자와 사업자가 함께하는 소셜 서비스

<br>

## 📌 프로젝트 소개

Snapia는 인스타그램 같은 SNS 기능에 **네이버 예약과 같은 사업자 예약 시스템**을 결합한 플랫폼입니다.

일반 사용자는 피드, 스토리, 하이라이트, 실시간 DM 등 SNS 기능을 이용하고,  
사업자 계정은 서비스 항목 등록 · 예약 수락/거절 · 고객과의 실시간 채팅을 통해 비즈니스를 운영할 수 있습니다.

| | |
|---|---|
| **개발 기간** | 2025.12.13 ~ 2026.01.02 (약 1개월) |
| **개발 인원** | 1인 (개인 프로젝트) |
| **GitHub** | https://github.com/hawnghasung/Snapia_project1 |

<!--
[TODO] 화면 미리보기 섹션 — 스크린샷/GIF를 올린 뒤 이 주석을 풀어서 사용하세요.
(README 상단에 두는 것이 가장 효과적입니다)

## 🎬 화면 미리보기

| 피드 / 스토리 | 실시간 DM | 사업자 예약 |
|---|---|---|
| ![feed](이미지 URL) | ![dm](이미지 URL) | ![booking](이미지 URL) |
-->

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
- 게시글 업로드 (이미지 / 영상), 좋아요, 댓글(대댓글), 해시태그
- 스토리 업로드 (24시간 후 자동 만료) 및 조회자 목록 확인
- 하이라이트 (만료된 스토리를 영구 보관)
- 팔로우 / 알림 (팔로우 요청, 수락 등)
- WebSocket(STOMP) 기반 1:1 실시간 DM (답장, 읽음 처리)
- 사업자 계정에 예약 요청

### 🏢 사업자 계정
- 사업자 프로필 등록 (업종, 소개 등)
- 서비스 항목 등록
- 예약 요청 수락 / 거절
- 예약 관련 알림 수신
- 고객과 실시간 채팅

<br>

## 🗂 DB 설계 (ERD)

> 불필요한 테이블을 늘리지 않고, 서비스 범위에 맞는 최소한의 구조로 설계했습니다.

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

### ① DM 스키마를 1:1 채팅에 맞춰 단순화
**서비스 범위(1:1)에 맞게 불필요한 테이블과 조인 제거**

**문제**

이 서비스의 DM은 1:1만 지원합니다. 그런데 일반적인 채팅 설계대로 `room_member` 테이블을 두면, 참여자를 확인할 때마다 조인이 추가되고 테이블 수도 늘어납니다. 또한 읽음 여부를 `message`에 직접 두면, 수신자가 메시지를 읽을 때마다 `message` 행을 UPDATE하게 되어 메시지 조회·전송과 같은 행을 두고 경합할 수 있습니다.

**결정 근거 & 트레이드오프**

- `room_member` 제거 → `room.sender_id / receiver_id`로 참여자를 직접 참조.
  - 트레이드오프: 그룹채팅으로 확장하려면 스키마 변경이 필요합니다. 서비스 범위가 1:1에 한정되므로 감수했습니다.
  - 트레이드오프: "내 채팅방 목록" 조회 시 `sender_id`, `receiver_id` 양쪽을 조건으로 걸어야 합니다.
- 읽음 여부를 `message_read_status`로 분리 → 읽음 처리 시 `message` 행을 건드리지 않고, 메시지 본문과 읽음 상태의 책임을 분리.
- `message.reply_id` 자기 참조 FK로 답장 구현. 별도 테이블 불필요.

<!-- [TODO] 위 "문제/근거"가 실제 설계 의도와 다르면 본인의 이유로 고쳐주세요. 면접에서 가장 많이 물어볼 항목입니다. -->

**결과**

`room` · `message` · `message_read_status` 3개 테이블로 채팅방 생성, 메시지 송수신, 읽음 처리, 답장을 모두 구현. 참여자 조회 시 `room_member` 조인 1회를 제거했습니다.

---

### ② 단일 `users` 테이블로 일반/사업자 통합 관리
**Spring Security Role 기반 권한 분리와의 정합성 확보**

**문제**

일반 사용자와 사업자를 별도 테이블로 나누면 로그인 시 어느 테이블에서 조회할지 분기 로직이 필요하고, Spring Security의 `UserDetailsService` 구현도 복잡해집니다. 인증 흐름이 두 갈래가 되는 것 자체가 버그 포인트입니다.

**결정 근거 & 트레이드오프**

- 모든 회원을 `users` 하나로 관리하고, 사업자 추가 정보는 `business_profile`에 분리해 1:1 관계로 참조.
- `users.is_business` + Spring Security Role 조합으로 일반/사업자 권한 분리. `UserDetailsService`를 단일 구현으로 유지해 인증 로직을 단순하게 유지.
- 트레이드오프: 사업자 전용 정보는 `business_profile`로 분리해 `users`에는 사업자 전용 컬럼이 남지 않도록 했습니다. 대신 사업자 정보 조회 시 1:1 조인이 한 번 필요합니다.

**결과**

로그인 · 인증 · 권한 처리가 단일 흐름으로 정리됨. 사업자 전환 시에도 `business_profile` 행 추가만으로 처리 가능.

---

### ③ 스토리 만료 처리 & 하이라이트 연계
**스케줄러 없이 조회 시점 필터링으로 만료 처리**

**문제**

스토리 24시간 만료를 배치 삭제 방식으로 구현하면 만료 시각 정밀도가 낮고, 하이라이트로 보관한 스토리까지 삭제될 위험이 있습니다. 만료된 스토리와 하이라이트 스토리를 어떻게 구분할지가 핵심이었습니다.

**결정 근거 & 트레이드오프**

- 업로드 시 `story.expired_at = NOW() + 24h`로 저장하고, 조회 쿼리에 `WHERE expired_at > NOW()` 조건을 붙여 **조회 시점에** 만료를 판단. 별도 스케줄러나 배치 없이 처리.
- 만료된 스토리도 `highlight_content` 테이블을 통해 하이라이트와 N:M으로 연결해 영구 보관. 하이라이트 조회는 만료 여부와 무관하게 별도 쿼리로 처리.
- 트레이드오프: 만료된 스토리 행이 테이블에 계속 남습니다. 데이터가 커지면 `expired_at` 인덱스와 하이라이트에 속하지 않은 만료 데이터의 정리 배치 도입을 검토할 수 있습니다.

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

**사전 설치**: Java 17, MariaDB, FFmpeg

```bash
# 1. 저장소 클론
git clone https://github.com/hawnghasung/Snapia_project1.git
cd Snapia_project1
```

```properties
# 2. src/main/resources/application.properties 생성 후 아래 항목 설정
spring.datasource.url=jdbc:mariadb://localhost:3306/<DB 이름>
spring.datasource.username=<DB 사용자>
spring.datasource.password=<DB 비밀번호>

# 파일 업로드 경로, FFmpeg 실행 파일 경로는
# 프로젝트에서 사용하는 설정 키 이름에 맞춰 입력
```

```bash
# 3. 빌드 및 실행
./gradlew bootRun
```

> `application.properties`는 비밀번호 등 민감 정보를 포함하므로 `.gitignore`에 등록되어 있습니다.

<!-- [TODO] 위 문장은 실제로 .gitignore에 등록한 뒤에만 유지하세요. 가능하면 application-example.properties를 함께 올려두면 더 좋습니다. -->
