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
|인기글|게시 후 12시간 내에 받은 추천의 수가 30개 이상인 글 중에서 비추천의 수가 추천수의 30% 미만인 게시글. 인기글로 등록된 게시글은 작성자가 삭제할 수 없다.|
|인기댓글|등록 후 12시간 내에 받은 추천의 수가 10개 이상은 댓글 중에서 비추천의 수가 추천수의 30% 미만인 게시글|
|추천|사용자가 게시글 혹은 댓글에 긍정적인 평가를 표시하는 기능|
|비추천|사용자가 게시글 혹은 댓글에 부정적인 평가를 표시하는 기능|
|신고|관리자에게 부적절한 행위를 한 사용자의 게시판 이용 정지를 요청하는 기능|

<br><br><br>

## 2. 역할 정의

|역할|설명|
|-|-|
|ADMIN|관리자|
|MANAGER|매니저|
|SUB_MANAGER|부매니저|
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
|FR-AUTH-002|MANAGER, SUB_MANAGER, USER|회원을 탈퇴할 수 있다.|
<br>

### 회원정보(PROFILE)
|ID|역할|요구사항|
|-|-|-|
|FR-PROFILE-001|ADMIN, MANAGER, SUB_MANAGER, USER|자신의 회원 정보를 열람할 수 있다.|
|FR-PROFILE-002|ADMIN, MANAGER, SUB_MANAGER, USER|자신의 닉네임을 수정할 수 있다.|
<br>

### 게시판(BOARD)
|ID|역할|요구사항|
|-|-|-|
|FR-BOARD-001|ADMIN|새로운 게시판을 생성할 수 있다.|
|FR-BOARD-002|ADMIN|게시판을 폐쇄할 수 있다.|
|FR-BOARD-003|ADMIN, MANAGER|게시판에 게시글 작성을 금지할 수 있다.|
|FR-BOARD-004|ADMIN, MANAGER|게시판에 게시글 작성을 허가할 수 있다.|
|FR-BOARD-005|ADMIN, MANAGER|게시판에 속한 모든 게시글에 댓글 작성을 금지할 수 있다.|
|FR-BOARD-006|ADMIN, MANAGER|게시판에 속한 모든 게시글에 댓글 작성을 허가할 수 있다.|
|FR-BOARD-007|ADMIN, MANAGER|게시판의 이름을 변경할 수 있다.|
|FR-BOARD-008|ADMIN, MANAGER|게시판의 소개문구를 변경할 수 있다.|
|FR-BOARD-009|ADMIN, MANAGER, SUB_MANAGER, USER|게시글을 10개씩 페이징하여 목록을 열람할 수 있다.|
|FR-BOARD-010|ADMIN, MANAGER, SUB_MANAGER, USER|제목, 내용 등을 기반으로 게시글을 검색할 수 있다.|
|FR-BOARD-011|ADMIN, MANAGER, SUB_MANAGER, USER|인기글 목록을 열람할 수 있다.|
|FR-BOARD-012|ADMIN, MANAGER, SUB_MANAGER, USER|공지글을 게시판 상단에 별도의 항목으로 제공한다.|
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
|FR-POST-006|ADMIN, MANAGER, SUB_MANAGER, USER|게시글을 비추천 할 수 있다.|
<br>

### 댓글(COMMENT)
|ID|역할|요구사항|
|-|-|-|
|FR-COMMENT-001|ADMIN, MANAGER, SUB_MANAGER|다른 회원이 작성한 댓글을 삭제할 수 있다.|
|FR-COMMENT-002|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 작성할 수 있다.|
|FR-COMMENT-003|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 수정할 수 있다.|
|FR-COMMENT-004|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 삭제할 수 있다.|
|FR-COMMENT-005|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 추천 할 수 있다.|
|FR-COMMENT-006|ADMIN, MANAGER, SUB_MANAGER, USER|댓글을 비추천 할 수 있다.|
<br>

### 신고(REPORT)
|ID|역할|요구사항|
|-|-|-|
|FR-REPORT-001|USER|다른 사용자를 신고할 수 있다.|

<br><br><br>

## 4. 비기능 요구사항
|ID||
|-|-|
|NFR-001||

<br><br><br>
