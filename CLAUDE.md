# Shoot - Real-time Chat Application

> Spring Boot Kotlin 실시간 채팅 애플리케이션

## Tech Stack

- Spring Boot 3.x, Kotlin, Gradle
- WebSocket (STOMP), Redis Stream, Kafka
- PostgreSQL (Users, Friends, Notifications)
- MongoDB (Chat messages, Reactions)
- Redis (Session, Cache, Stream)
- JWT Authentication

## Architecture

- **Hexagonal Architecture** (Ports & Adapters)
- **Domain-Driven Design** (DDD) - **Maturity: 10.0/10** ⭐
- **Event-Driven Architecture**
- **CQRS** (Chat operations)

### DDD Implementation
- **15 Aggregate Roots** with clear transaction boundaries
- **ID Reference Pattern**: 100% compliance (no direct entity references)
- **100% ID Value Objects**: All Aggregates use typed IDs (@JvmInline value classes)
- **Custom Annotations**: `@AggregateRoot`, `@ValueObject`, `@DomainEntity`, `@DomainEvent`, `@DomainService`
- **Rich Domain Model**: Business logic encapsulated in domain objects
- **Anti-Corruption Layer (ACL)**: Context 간 변환 처리

### DTO Layer Separation
**완료 날짜**: 2025-12-22

Application 레이어와 Adapter 레이어 간 명확한 DTO 분리로 Hexagonal Architecture 원칙 준수:

**분리된 도메인 (8개)**:
- ✅ **User**: 사용자 인증 및 정보 조회
- ✅ **ChatRoom**: 채팅방 생성, 조회, 즐겨찾기
- ✅ **ScheduledMessage**: 예약 메시지
- ✅ **Friend**: 친구 조회, 검색, 추천
- ✅ **MessagePin**: 메시지 고정
- ✅ **MessageReaction**: 메시지 리액션
- ✅ **Notification**: 알림 조회 및 읽음 처리
- ✅ **FriendGroup**: 친구 그룹 관리

**계층별 책임**:
- **Application DTO** (`application/dto/`): Application 레이어 내부 데이터 전달
- **Application Mapper** (`application/mapper/`): Domain Entity → Application DTO 변환
- **Adapter DTO** (`adapter/in/rest/dto/`): REST API 응답 형식
- **Controller**: Application DTO → Adapter DTO 변환 (확장 함수 사용)

**아키텍처 개선**:
- UseCase 인터페이스가 Domain Entity 대신 Application DTO 반환
- Adapter 레이어가 Application 레이어에 의존하지 않도록 분리
- 계층 간 의존성 역전 원칙(DIP) 완벽 준수

## Project Structure

```
src/main/kotlin/com/shoot/
├── domain/              # 핵심 비즈니스 로직, 엔티티
├── application/         # Use cases, 서비스 레이어
│   ├── dto/            # Application 레이어 DTO
│   ├── mapper/         # Domain → Application DTO 변환
│   ├── port/in/        # Inbound ports (use cases)
│   ├── port/out/       # Outbound ports (persistence, messaging)
│   └── service/        # 애플리케이션 서비스, 이벤트 리스너
├── adapter/
│   ├── in/             # Controllers, WebSocket handlers, Adapter DTO
│   └── out/            # DB adapters, messaging adapters
└── infrastructure/      # Config, 공통 기능
```

## Port Naming Convention

- **LoadPort**: 조회 (findById, findAll)
- **SavePort**: 저장/수정 (save, update)
- **QueryPort**: 복잡한 쿼리, 검색
- **CommandPort**: 명령 (create, delete)

## Code Placement

### domain/
- 엔티티, Value Objects
- 도메인 이벤트
- 비즈니스 규칙/로직

### application/
- **dto/**: Application 레이어 DTO (도메인 → 외부 전달용)
- **mapper/**: Domain Entity → Application DTO 변환 컴포넌트
- **port/in/**: Use cases (반드시 Application DTO 반환)
- **port/out/**: Port interfaces (persistence, messaging)
- **service/**: 애플리케이션 서비스
- 이벤트 리스너 (`@TransactionalEventListener`)

### adapter/
- **in/**: REST Controllers, WebSocket handlers, Adapter DTO
  - Controller에서 Application DTO → Adapter DTO 변환 (확장 함수 사용)
- **out/**: JPA/MongoDB repositories, Kafka producers, Redis clients

### infrastructure/
- Configuration (Security, WebSocket, DB)
- 공통 유틸리티, 예외 처리

## Business Rules

### Aggregate Roots (16개)

**Chat Context (5개)**
- **ChatMessage**: 메시지 본문 및 메타데이터 (243 lines)
- **MessagePin**: 메시지 고정 (53 lines)
- **MessageReadReceipt**: 메시지 읽음 표시 (53 lines)
- **MessageReaction**: 메시지 리액션 (84 lines)
- **MessageBookmark**: 메시지 북마크 (34 lines)

**Social Context (4개)**
- **FriendRequest**: 친구 요청 및 수락/거절
- **Friendship**: 친구 관계 (양방향)
- **BlockedUser**: 사용자 차단
- **FriendGroup**: 친구 그룹 관리

**User Context (2개)**
- **User**: 사용자 정보 및 프로필
- **RefreshToken**: JWT 리프레시 토큰

**ChatRoom Context (3개)**
- **ChatRoom**: 채팅방 관리 (includes ChatRoomSettings as Value Object)
- **ChatRoomFavorite**: 사용자별 채팅방 즐겨찾기

**Notification Context (2개)**
- **Notification**: 알림
- **NotificationSettings**: 사용자별 알림 설정 (userId as Natural Key)

### 메시지 (Message)
- 상태: SENDING → SENT_TO_KAFKA → PROCESSING → SAVED / FAILED
- 최대 길이: 4,000자 (DomainConstants)
- 첨부파일: 최대 50MB
- 채팅방당 최대 고정 메시지: 5개
- 삭제: 소프트 삭제 (isDeleted 플래그)
- 수정: TEXT 타입만 가능, 삭제된 메시지 수정 불가
- **수정 시간 제한: 24시간** (생성 후 24시간 이후 수정 불가)
- 빈 내용으로 수정 불가

### 메시지 읽음 표시 (MessageReadReceipt)
- **별도 Aggregate로 분리**: ChatMessage와 독립적 트랜잭션 경계
- 사용자별 메시지 읽음 시간 기록
- 동시성 제어: 여러 사용자가 동시에 읽음 처리 가능
- Unique 제약: (messageId, userId) 복합 인덱스
- Eventual Consistency: 읽음 수 집계는 비동기 처리

### 사용자 (User)
- Username: 3-20자
- Nickname: 2-30자
- Password: 최소 8자
- UserCode: 8자리 대문자+숫자, 중복 불가
- 최대 친구 수: 1,000명

### 채팅방 (ChatRoom)
- 1:1 채팅: 정확히 2명
- 그룹 채팅: 2~100명
- 자기 자신과 채팅 생성 불가
- 참여자 없으면 자동 삭제
- 최대 핀 채팅방: 사용자별 제한 (DomainConstants)

### 친구 (Friend)
- 요청 상태: PENDING → ACCEPTED / REJECTED / CANCELLED
- PENDING 상태에서만 처리 가능 (이미 처리된 요청 재처리 불가)
- 추천: BFS 알고리즘 (최대 depth: 3)
- 중복 요청 불가
- **자기 자신에게 친구 요청 불가**
- 이미 친구인 경우 요청 불가
- 이미 보낸 요청이 있으면 재요청 불가
- 상대방으로부터 이미 받은 요청이 있으면 새 요청 불가
- 친구 관계: 양방향 (Friendship 2개 생성)

### 리액션 (Reaction)
- 타입: LIKE, LOVE, HAHA, WOW, SAD, ANGRY
- 사용자당 메시지별 1개 리액션 (다른 리액션 선택 시 교체)

### 채팅방 설정 (ChatRoomSettings)
**타입**: Value Object (ChatRoom에 임베드)
- 알림 활성화 (isNotificationEnabled, 기본: true)
- 메시지 보존 기간 (retentionDays, 기본: null = 무기한)
- 암호화 설정 (isEncrypted, 기본: false)
- 커스텀 설정 (customSettings: Map<String, Any>, JSON 저장)

### WebSocket 제한
- Heartbeat: 5초 (서버 ↔ 클라이언트)
- 메시지 크기: 최대 64KB
- 버퍼 크기: 256KB
- 전송 시간 제한: 10초
- 첫 메시지 대기: 30초
- SockJS disconnect delay: 2초
- Rate Limiting: 타이핑 인디케이터 1초 제한

### 도메인 상수 (DomainConstants)
- `chatRoom.maxParticipants`: 100
- `chatRoom.minGroupParticipants`: 2
- `chatRoom.maxPinnedMessages`: 5
- `message.maxContentLength`: 4000
- `message.maxAttachmentSize`: 52428800 (50MB)
- `message.batchSize`: 100
- `friend.maxFriendCount`: 1000
- `friend.recommendationLimit`: 20

### 이벤트 발행 규칙
- 트랜잭션 커밋 후: `@TransactionalEventListener` 사용
- SpringEventPublisher로 발행
- 이벤트 타입: MESSAGE_CREATED, FRIEND_ADDED, CHAT_ROOM_CREATED 등
- Kafka 토픽: 채팅방 ID 기반 파티셔닝으로 메시지 순서 보장

### 동시성 제어
- Redis 기반 분산락 사용 (`RedisLockManager`)
- 락 키: `chatroom:{roomId}`, `user:{userId}` 등
- 자동 만료 시간 설정으로 데드락 방지
- Lua 스크립트로 안전한 락 해제 (소유자 검증)
- 지수 백오프 재시도 메커니즘
- 채팅방별 독립적 락으로 병렬성 유지

**📖 상세 정보**:
- 도메인 모델: `docs/architecture/DOMAIN.md`
- Context Map: `docs/architecture/CONTEXT_MAP.md`
- Bounded Contexts: `docs/architecture/BOUNDED_CONTEXTS.md`
- ACL 패턴: `knowledge/patterns/ACL_PATTERN_GUIDE.md`

## Development Rules

### DO
- Domain 우선 설계 (엔티티, 이벤트 먼저)
- Port 인터페이스 정의 후 구현
- **Aggregate Root에 `@AggregateRoot` 어노테이션 명시**
- **Value Object에 `@ValueObject` 어노테이션 명시**
- **ID Reference Pattern 사용** (다른 Aggregate는 ID로만 참조)
- **UseCase는 반드시 Application DTO 반환** (Domain Entity 직접 반환 금지)
- **Mapper를 통해 Domain → Application DTO 변환**
- **Controller는 확장 함수로 Application DTO → Adapter DTO 변환**
- 단일 표현식 함수 사용
- `in` 연산자 사용 (`.contains()` 대신)
- 불필요한 `this` 제거
- Event-driven으로 도메인 간 통신
- DomainConstants에서 상수값 참조
- ACL을 통한 Context 간 변환

### DON'T
- Domain에서 infrastructure 직접 의존 금지
- Adapter에 비즈니스 로직 작성 금지
- Controller에서 직접 repository 호출 금지
- **Aggregate 간 직접 객체 참조 금지** (ID만 사용)
- **하나의 트랜잭션에서 여러 Aggregate 수정 금지**
- **UseCase에서 Domain Entity 직접 반환 금지** (Application DTO 사용)
- **Application 레이어에서 Adapter DTO 참조 금지**
- 중복 주석 작성 금지
- 매직넘버 하드코딩 금지 (DomainConstants 사용)

## Workflow

1. **새 기능 추가**:
   - domain/에 엔티티/값 객체 생성
   - application/port/에 인터페이스 정의
   - application/service/에 Use case 구현
   - adapter/에 구현체 작성
   - 필요시 이벤트 리스너 추가

2. **WebSocket 기능**:
   - `/topic/chat/{roomId}` 토픽 사용
   - Redis Stream으로 메시지 큐잉
   - 재시도 로직 구현
   - 오프라인 사용자는 Redis fallback

3. **이벤트 기반 개발**:
   - domain/event/에 도메인 이벤트 생성
   - `@TransactionalEventListener` 사용
   - SpringEventPublisher로 발행

## Build & Run

```bash
./gradlew bootRun      # 실행
./gradlew test         # 테스트
./gradlew build        # 빌드
```

## Common Issues

### WebSocket 연결 실패
- Redis 연결 확인
- WebSocket 세션 검증 확인

### 메시지 전송 실패
- Kafka 토픽 확인
- Redis Stream 상태 확인

### 인증 오류
- JWT 토큰 유효성 확인
- Security filter 순서 확인

## Testing Strategy

- **Unit**: Domain 로직
- **Integration**: Adapter 레이어
- **WebSocket**: 실시간 기능
- **Event**: 이벤트 처리 로직

## Security

- 모든 엔드포인트 JWT 인증
- WebSocket 세션 검증
- 입력값 검증 (Commands)
- SQL Injection 방지

## Performance

- Redis 캐싱 (자주 조회되는 데이터)
- Kafka 메시지 영속화
- DB Connection pooling
- 비동기 처리 (비중요 작업)

---

*Last updated: 2025-12-26*
