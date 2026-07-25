## 1. 프로젝트 개요
### 프로젝트명: Community

### 목적
- 사용자 간의 자유로운 소통을 위한 커뮤니티 서비스 제공
- 게시글 및 댓글 기능 지원
- 다양한 사용자 인증 방법 제공
- 사용자의 권한을 세분화하여 서비스를 체계적으로 관리
<br><br><br>

## 2. 용어 정의

|용어|정의|
|-|-|
|관리자|사이트 전체 운영 권한을 가지는 최고 관리자|
|매니저|특정 게시판 운영에 대한 전체 권한을 가지는 중간 관리자|
|부매니저|특정 게시판 운영에 대한 일부 권한을 가지는 중간 관리자|
|회원|사이트에 가입한 일반 사용자|
|공지글|관리자가 사이트 이용자에게 공지사항 전달을 목적으로 작성한 게시글|
|인기글|인기글 정책에 따라 선정된 게시글|
|인기댓글|인기댓글 정책에 따라 선정된 댓글|
|추천|사용자가 게시글 혹은 댓글에 긍정적인 평가를 표시하는 기능|
|비추천|사용자가 게시글 혹은 댓글에 부정적인 평가를 표시하는 기능|
|즐겨찾기|사용자가 자주 이용하는 게시판을 등록하여 빠르게 이동할 수 있도록 하는 기능|
|신고|관리자에게 부적절한 행위를 한 사용자의 게시판 이용 정지를 요청하는 기능|

<br><br><br>

## 2. 역할 정의

|역할|설명|
|-|-|
|ADMIN|최고 관리자|
|MANAGER|게시판 관리자|
|SUB_MANAGER|게시판 부관리자|
|USER|회원|

<br><br><br>

## 3. 기능 요구사항

### 운영(MODERATION)
|ID|역할|요구사항|
|-|-|-|
|FR-MOD-001|ADMIN|회원을 매니저로 지정할 수 있다.|
|FR-MOD-002|ADMIN|매니저 지정을 해제할 수 있다.|
|FR-MOD-003|ADMIN, MANAGER|회원을 부매니저로 지정할 수 있다.|
|FR-MOD-004|ADMIN, MANAGER|부매니저 지정을 해제할 수 있다.|
|FR-MOD-005|ADMIN, MANAGER, SUB_MANAGER|회원을 정지할 수 있다.|
|FR-MOD-006|ADMIN, MANAGER, SUB_MANAGER|회원의 정지를 해제할 수 있다.|
<br>

### 인증(AUTHENTICATION)
|ID|역할|요구사항|
|-|-|-|
|FR-AUTH-001|USER|Google OAuth2 기반의 회원가입을 할 수 있다.|
|FR-AUTH-002|USER|신규 사용자는 온보딩(필수 프로필 정보 입력)을 통해 회원가입을 완료할 수 있다.|
|FR-AUTH-002|MANAGER, SUB_MANAGER, USER|회원을 탈퇴할 수 있다.|
|FR-AUTH-003|MANAGER, SUB_MANAGER, USER|회원 탈퇴 후에도 작성한 게시글과 댓글은 유지된다.|
|FR-AUTH-004|MANAGER, SUB_MANAGER, USER|회원 탈퇴 후에는 기존에 작성한 게시글과 댓글을 수정하거나 삭제할 수 없다.|
<br>

### 회원정보 - 내 프로필(MYPROFILE)
|ID|역할|요구사항|
|-|-|-|
|FR-MYPROFILE-001|ADMIN, MANAGER, SUB_MANAGER, USER|자신의 회원 정보를 조회할 수 있다.|
|FR-MYPROFILE-002|ADMIN, MANAGER, SUB_MANAGER, USER|자신의 닉네임을 수정할 수 있다.|
|FR-MYPROFILE-003|ADMIN, MANAGER, SUB_MANAGER, USER|자신의 프로필 공개 여부를 설정할 수 있다.|
|FR-MYPROFILE-004|ADMIN, MANAGER, SUB_MANAGER, USER|자신이 작성한 게시글 목록을 조회할 수 있다.|
|FR-MYPROFILE-005|ADMIN, MANAGER, SUB_MANAGER, USER|자신이 작성한 게시글 상세 페이지로 이동할 수 있다.|
|FR-MYPROFILE-006|ADMIN, MANAGER, SUB_MANAGER, USER|자신인 즐겨찾기 등록한 게시판 목록을 조회할 수 있다.|
|FR-MYPROFILE-007|ADMIN, MANAGER, SUB_MANAGER, USER|자신이 즐겨찾기 등록한 게시판 페이지로 이동할 수 있다.|
<br>

### 회원정보 - 타인 프로필(USERPROFILE)
|ID|역할|요구사항|
|-|-|-|
|FR-USERPROFILE-001|ADMIN, MANAGER, SUB_MANAGER, USER|공개된 다른 사용자의 프로필을 조회할 수 있다.|
|FR-USERPROFILE-002|ADMIN, MANAGER, SUB_MANAGER, USER|다른 사용자가 작성한 게시글 목록을 조회할 수 있다.|
|FR-USERPROFILE-003|ADMIN, MANAGER, SUB_MANAGER, USER|다른 사용자가 작성한 게시글 상세 페이지로 이동할 수 있다.|
<br>

### 게시판(BOARD)
|ID|역할|요구사항|
|-|-|-|
|FR-BOARD-001|ADMIN|새로운 게시판을 생성할 수 있다.|
|FR-BOARD-002|ADMIN|게시판을 폐쇄할 수 있다.|
|FR-BOARD-003|ADMIN|게시판 카테고리를 변경할 수 있다.|
|FR-BOARD-004|ADMIN, MANAGER|게시판에 게시글 작성을 금지할 수 있다.|
|FR-BOARD-005|ADMIN, MANAGER|게시판에 게시글 작성을 허가할 수 있다.|
|FR-BOARD-006|ADMIN, MANAGER|게시판에 속한 모든 게시글에 댓글 작성을 금지할 수 있다.|
|FR-BOARD-007|ADMIN, MANAGER|게시판에 속한 모든 게시글에 댓글 작성을 허가할 수 있다.|
|FR-BOARD-008|ADMIN, MANAGER|게시판의 이름을 변경할 수 있다.|
|FR-BOARD-009|ADMIN, MANAGER|게시판의 소개문구를 변경할 수 있다.|
|FR-BOARD-010|ADMIN, MANAGER, SUB_MANAGER, USER|게시글을 20개씩 페이징하여 목록을 열람할 수 있다.|
|FR-BOARD-011|ADMIN, MANAGER, SUB_MANAGER, USER|인기글을 20개씩 페이징하여 목록을 열람할 수 있다.|
|FR-BOARD-012|ADMIN, MANAGER, SUB_MANAGER, USER|제목, 내용 등을 기반으로 게시글을 검색할 수 있다.|
|FR-BOARD-013|ADMIN, MANAGER, SUB_MANAGER, USER|제목, 내용 등을 기반으로 인기글을 검색할 수 있다.|
|FR-BOARD-014|ADMIN, MANAGER, SUB_MANAGER, USER|공지글을 게시판 상단에 별도의 항목으로 제공한다.|
|FR-BOARD-015|ADMIN, MANAGER, SUB_MANAGER, USER|게시판을 즐겨찾기 등록할 수 있다.|
<br>

### 공지글(NOTICE)
|ID|역할|요구사항|
|-|-|-|
|FR-NOTICE-001|ADMIN|타인이 작성한 공지글을 삭제할 수 있다.|
|FR-NOTICE-002|ADMIN, MANAGER, SUB_MANAGER|공지글을 작성할 수 있다.|
|FR-NOTICE-003|ADMIN, MANAGER, SUB_MANAGER|자신이 작성한 공지글을 수정할 수 있다.|
|FR-NOTICE-004|ADMIN, MANAGER, SUB_MANAGER|자신이 작성한 공지글을 삭제할 수 있다.|
<br>

### 게시글(POST)
|ID|역할|요구사항|
|-|-|-|
|FR-POST-001|ADMIN, MANAGER, SUB_MANAGER|다른 회원이 작성한 게시글을 삭제할 수 있다.|
|FR-POST-002|ADMIN, MANAGER, SUB_MANAGER, USER|게시글을 작성할 수 있다.|
|FR-POST-003|ADMIN, MANAGER, SUB_MANAGER, USER|자신이 작성한 게시글을 수정할 수 있다.|
|FR-POST-004|ADMIN, MANAGER, SUB_MANAGER, USER|자신이 작성한 게시글을 삭제할 수 있다.|
|FR-POST-005|ADMIN, MANAGER, SUB_MANAGER, USER|게시글을 추천 할 수 있다.|
|FR-POST-006|ADMIN, MANAGER, SUB_MANAGER, USER|게시글 추천을 취소 할 수 있다.|
|FR-POST-007|ADMIN, MANAGER, SUB_MANAGER, USER|게시글을 비추천 할 수 있다.|
|FR-POST-008|ADMIN, MANAGER, SUB_MANAGER, USER|게시글 비추천을 취소 할 수 있다.|
<br>

### 댓글(COMMENT)
|ID|역할|요구사항|
|-|-|-|
|FR-COMMENT-001|ADMIN, MANAGER, SUB_MANAGER|다른 회원이 작성한 댓글을 삭제할 수 있다.|
|FR-COMMENT-002|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 작성할 수 있다.|
|FR-COMMENT-003|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 수정할 수 있다.|
|FR-COMMENT-004|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 삭제할 수 있다.|
|FR-COMMENT-005|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 추천 할 수 있다.|
|FR-COMMENT-006|ADMIN, MANAGER, SUB_MANAGER, USER|댓글 추천을 취소 할 수 있다.|
|FR-COMMENT-007|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 비추천 할 수 있다.|
|FR-COMMENT-008|ADMIN, MANAGER, SUB_MANAGER, USER|댓글 비추천을 취소 할 수 있다.|
<br>

### 신고(REPORT)
|ID|역할|요구사항|
|-|-|-|
|FR-REPORT-001|USER|다른 사용자의 게시글을 신고할 수 있다.|
|FR-REPORT-002|USER|다른 사용자의 공지글을 신고할 수 있다.|

<br><br><br>

## 4. 비기능 요구사항

### 보안(SECURITY)
|ID|요구사항|
|-|-|
|NFR-SECURITY-001|OAuth2 와 JWT 기반으로 사용자 인증을 구현하여 안전한 인증/인가를 제공한다.|
|NFR-SECURITY-002|인증이 필요한 API 는 인증되지 않은 사용자의 접근을 제한한다.|
<br>

### 성능(PERFORMANCE)
|ID|요구사항|
|-|-|
|NFR-PERFORMANCE-001|게시글 조회는 2초 이내에 응답해야 한다.|
|NFR-PERFORMANCE-002|게시글 및 댓글 목록은 페이지네이션을 지원한다.|
|NFR-PERFORMANCE-003|다수의 동시 접속자 환경에서도 안정적으로 동작해야 한다.|
<br>

### 확장성(SCALABILITY)
|ID|요구사항|
|-|-|
|NFR-SCALABILITY-001|각 서비스는 독립적으로 확장이 가능해야 한다.|
|NFR-SCALABILITY-002|특정 서비스의 부하가 과중한 경우, 해당 서비스만 증설할 수 있어야 한다.|
|NFR-SCALABILITY-003|서비스 간의 결합도를 최소화한다.|
<br>

### 가용성(AVAILABILITY)
|ID|요구사항|
|-|-|
|NFR-AVAILABILITY-001|일부 서비스의 장애가 전체 서비스의 중단으로 이어지지 않아야 한다.|
|NFR-AVAILABILITY-002|장애 발생 시 빠른 복구가 가능해야 한다.|
<br>

### 유지보수성(MAINTAINABILITY)
|ID|요구사항|
|-|-|
|NFR-MAINTAINABILITY-001|기능별로 서비스를 분리하여 유지보수를 용이하게 한다.|
|NFR-MAINTAINABILITY-002|코드를 모듈화하여 코드 수정의 영향을 최소화한다.|
|NFR-MAINTAINABILITY-003|새로운 기능의 추가가 기존 기능에 미치는 영향을 최소화한다.|
<br>

### 신뢰성(RELIABILITY)
|ID|요구사항|
|-|-|
|NFR-RELIABILITY-001|데이터의 무결성을 보장한다.|
|NFR-RELIABILITY-002|예외 발생 시 적절한 오류 메시지를 제공한다.|
|NFR-RELIABILITY-003|트랜잭선을 사용해 데이터 일관성을 유지한다.|
<br>

### 사용성(USABILITY)
|ID|요구사항|
|-|-|
|NFR-USABILITY-001|오류 발생 시 사용자 친화적인 안내 메시지를 제공한다.|
<br>

### 로그 및 감사(AUDIT) << 이건 나중에 확장
|ID|요구사항|
|-|-|
|NFR-SECURITY-001|OAuth2 기반의 사용자 인증을 제공한다.|
|NFR-SECURITY-002|JWT 를 이용하여 안전하게 사용자 인증 및 인가를 수행한다.|
|NFR-SECURITY-003|인증이 필요한 API 는 인증되지 않은 사용자의 접근을 제한한다.|
<br>

### 운영성(OPERABILITY) >> 이것도 나중에 확장
|ID|요구사항|
|-|-|
|NFR-SECURITY-001|OAuth2 기반의 사용자 인증을 제공한다.|
|NFR-SECURITY-002|JWT 를 이용하여 안전하게 사용자 인증 및 인가를 수행한다.|
|NFR-SECURITY-003|인증이 필요한 API 는 인증되지 않은 사용자의 접근을 제한한다.|
<br>



<br><br><br>
