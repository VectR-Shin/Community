## 1. Overview
- 본 시스템의 API는 REST API 기반으로 설계되었으며, 클라이언트와 서버 간의 일관된 데이터 교환을 위해 JSON 형식을 사용한다.
- JSON 의 Property 는 `camelCase` 명명 규칙을 따른다.
- 본 시스템은 BFF(Backend For Frontend) 아키텍처를 적용하였으며, 클라이언트는 모든 API를 BFF를 통해 호출한다.
- BFF는 인증 및 세션을 관리하며, 각 마이크로서비스(Member, Community, Admin)와 통신하여 클라이언트에 필요한 데이터를 제공한다.
- 마이크로서비스의 API Specification 은 Related Documents 항목에 명시된 각 서비스 API 문서를 참조한다.

<br><br><br>

## 2. Design Principal
### 2.1. RESTful Style
- 리소스를 중심으로 URI 를 설계한다.
- URI 에는 동사 대신 명사를 사용한다.
- HTTP Method 를 이용하여 리소스에 대한 행위를 표현한다.

<br>

URI 설계 예시
|Method|URI|Description|
|-|-|-|
|GET|/posts|전체 게시글 목록 조회|
|GET|/boards/{boardId}/posts|특정 게시판의 게시글 목록 조회|
|GET|/posts/{postId}|특정 게시글 조회|
|POST|/boards/{boardId}/posts|특정 게시판에 게시글 작성|
|PATCH|/posts/{postId}|게시글 수정|
|DELETE|/posts/{postId}|게시글 삭제|

<br>

### 2.2. HTTP Method
|Method|Description|
|GET|리소스 조회|
|POST|리소스 생성|
|PATCH|리소스 수정|
|DELETE|리소스 삭제|

<br>

### 2.3. URL Naming 관례
- URI 는 소문자를 사용한다.
- 리소스는 복수형 명사를 사용한다.
- 리소스 식별자는 Path Variable 을 사용한다.

<br>

예시
```
/posts
/posts/{postId}
/comments/{commentId}
```

<br>

### 2.4. HTTP Status Code
|Code|Description|
|-|-|
|200 OK|요청 성공|
|201 Created|리소스 생성 성공|
|204 No Content|본문이 없는 응답 성공|
|400 Bad Request|잘못된 요청|
|401 Unauthorized|인증 필요 or 인증 실패|
|403 Forbidden|권한 없음|
|404 Not Found|리소스를 찾을 수 없음|
|409 Conflict|요청 충돌|
|500 Internal Server Error|서버 내부 오류|

<br>

### 2.5. Pagination
- 목록 조회 API 는 Pagination 을 사용한다.
- 페이지 번호는 `page` Query Parameter 를 사용하며, 0 부터 시작한다.
- 페이지 크기는 API 별 정책에 따라 고정되며, 클라이언트에서 변경할 수 없다.
- 정렬 기준은 또한 API 별 정책에 따라 고정된다.
  - 정책은 본 문서 하단의 Related Documents 에서 Common Policies 문서를 참조한다.

<br>

예시
```
GET /boards/{boardId}/posts?page={pageNumber}
```

<br><br><br>

## 3. Authentication and Authorization
### 3.1. Authentication
- 클라이언트는 모든 API 를 BFF 를 통해 호출한다.
- BFF 는 세션에 저장된 인증 정보를 기반으로 사용자를 식별하며, 각 마이크로서비스와 통신할 때 Access Token 을 사용한다.
- 자세한 인증 방식 및 OAuth2/OIDC 인증 흐름은 본 문서 하단의 Related Documents 에서 Authentication and Authorization 문서를 참조한다.

<br>

### 3.2. Authorization Matrix
|API Category|Guest|User|Sub Manager|Manager|Admin|
|-|-|-|-|-|-|
|Profile|R|CRUD|CRUD|CRUD|CRUD|
|Board|R|R|R|RU|CRUD|
|Post|R|CRUD|CRUD|CRUD|CRUD|
|Notice|R|R|CRUD|CRUD|CRUD|
|Comment|R|CRUD|CRUD|CRUD|CRUD|
|Report|-|C|RU|RU|RU|

- 위 권한 매트릭스는 각 도메인에 대한 권한을 요약한 것이다.
- 작성자 여부, 공개 여부, 게시판 관리 권한 등 세부 접근 조건과 표에 명시되지 않은 권한은 각 API의 Authorization 항목을 따른다.
- Report 의 U(Update)는 신고 상태 변경(신고 처리)를 의미한다.

<br><br><br>

## 4. Common Response
### 4.1. Response Format
- API 응답은 별도의 공통 Wrapper 객체를 사용하는 대신, 각 API 의 Response DTO 를 직접 반환한다.

<br>

### 4.2. Error Response
예시
```
{
  "code":"NICKNAME_DUPLICATION",
  "message":"Nickname already exists."
}
```

<br>

|Field|Description|
|-|-|
|code|오류 코드|
|message|오류 메시지|

<br>

### 4.3. Pagination Response
- 목록 조회 API 는 Pagination 정보를 포함한 Response DTO 를 반환한다.

<br>

예시
```
>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 나중에 API Spec 작성하고 예시 하나 넣어놓기
```

<br>

|Field|Description|
|-|-|
|||
|||


<br>

### 4.4. Content-Type/Encoding
|항목|규칙|
|-|-|
|Data Format|JSON|
|Media Type|application/json|
|Character Encoding|UTF-8|

<br>

### 4.5. Date-time Format
- 모든 날짜 및 시간 데이터는 ISO-8601 표준 형식을 사용하며, UTC 기준으로 표현한다.

<br>

|항목|규칙|
|-|-|
|Application Type|Instant(JAVA)|
|Database Type|DATETIME|
|Format|yyyy-MM-dd'T'HH:mm:ssX|
|Timezone|UTC|

<br>

예시
```
{
  "created_at":"2026-08-01T22:01:23Z"
}
```

<br>

### 4.6. Empty Response
- 리소스 생성, 조회 등 응답 데이터가 필요한 API 이외의 단순 상태 변경 또는 삭제 작업 성공 시 Response Body를 반환하지 않는다.
- 성공 응답은 HTTP Status Code로 결과를 표현하며, Response Body가 없는 경우 HTTP 204 No Content를 사용한다.

<br><br><br>

## 5. BFF API Specification
### 5.1. Authentication API
#### 5.1.1. GET /oauth2/authorization/keycloak
```
Description
- OAuth2/OIDC Authorization Code Flow 를 시작한다.
- Spring Security 가 OAuth2 Authorization Request 를 생성한 후, 사용자를 Keycloak 로그인 페이지로 리다이렉트한다.

Authorization
- Public
```

##### Request
```
Header
- None

Path Parameter
- None

Query Parameter
- None

RequestBody
- None
```

##### Response
```
Status
- 302 Found

Header
- Location: (Desc)Keycloak Authorization Endpoint URL

Body
- None
```

##### Error Response
- None

##### Processing Flow
```
1. 사용자가 로그인 버튼을 클릭한다.
2. 클라이언트가 GET /oauth2/authorization/keycloak 을 호출한다.
3. Spring Security가 OAuth2 Authorization Request 를 생성한다.
4. Spring Security는 Keycloak Authorization Endpoint 로 302 Redirect 를 반환한다.
5. 브라우저는 Keycloak Authorization Endpoint 로 이동한다.
6. Keycloak 은 로그인 페이지를 제공하고 사용자의 인증을 수행한다.
```

##### 비고
- 본 Endpoint 는 Spring Security OAuth2 Client 가 제공하며, 애플리케이션에서 직접 구현하지 않는다.

<br>

#### 5.1.2. POST /logout
```
Description
- 현재 로그인한 사용자를 로그아웃한다.
- Spring Security 의 Logout 기능을 사용하여 SecurityContext 및 Http Session 을 제거한다.
- OIDC Logout 을 사용해 Keycloak 세션을 종료한다.

Authorization
- Authenticated
```

##### Request
```
Header
- Cookie: Session Cookie

Path Parameter
- None

Query Parameter
- None

RequestBody
- None
```

##### Response
```
Status
- 204 No Content

Header
- None

Body
- None
```

##### Error Response
|Status|Message|
|-|-|
|401 Unauthorizaed|인증되지 않은 사용자입니다.|

- Error Response 의 상세한 내용은 본 문서 하단의 Related Documents 의 Error Response Design 문서를 참조한다.

##### Processing Flow
```
1. 클라이언트가 POST /logout을 호출한다.
2. Spring Security가 현재 사용자의 Authentication을 확인한다.
3. SecurityContext 및 Http Session을 제거한다.
4. OIDC Logout을 통해 Keycloak 세션을 종료한다.
5. 로그아웃 완료 후 클라이언트에 204 No Content를 반환한다.
```

##### 비고
- 본 Endpoint 는 Spring Security 가 제공하는 Logout Endpoint 를 사용한다.

<br>

#### 5.1.3. GET /auth/me
```
Description
- 현재 사용자의 인증 상태를 조회한다.
- 인증된 사용자인 경우 회원 식별자 및 권한 정보를 반환한다.

Authorization
- Public
```

##### Request
```
Header
- Cookie: Session Cookie (Optional)

Path Parameter
- None

Query Parameter
- None

RequestBody
- None
```

##### Response
```
Status
- 200 OK

Header
- None

Body
- AuthInfoResponseDTO
(// 인증된 사용자
  "authenticated": true,
  "memberId": 1,
  "roles": [
    "USER"
  ]
}

{// 인증되지 않은 사용자
  "authenticated": false,
  "memberId": null,
  "roles": []
}
```

##### AuthInfoResponseDTO Fields
|Field|Type|Description|
|-|-|-|
|authenticated|Boolean|현재 사용자의 인증 여부|
|memberId|Long|회원 식별자(인증되지 않은 경우 null)|
|roles|List<String>|사용자 역할(USER, MANAGER, SUB_MANAGER, ADMIN)|

##### Error Response
- None

##### Processing Flow
```
1. 클라이언트가 GET /auth/me 를 호출한다.
2. Spring Security 가 Http Session 에서 SecurityContext 를 조회한다.
3. 사용자의 Authentication 정보를 확인한다.
4. 인증 여부 및 권한 정보를 응답으로 반환한다.
```

<br><br>

### 5.2. User API (사용자 소유 데이터)
#### 5.2.1. GET /users/me
```
Description
- 현재 로그인한 사용자의 정보(Member, Profile)를 조회한다.

Authorization
- Authenticated
```

##### Request
```
Header
- Cookie: Session Cookie

Path Parameter
- None

Query Parameter
- None

RequestBody
- None
```

##### Response
```
Status
- 200 OK

Header
- None

Body
- UserResponseDTO
{
  "memberId": 1,
  "nickname": "shin",
  "email": "shin@example.com",
  "isPublic": true,
  "status": "ACTIVE",
  "createdAt": "2026-08-03T22:01:23Z"
}
```

##### UserResponseDTO Fields
|Field|Type|Description|
|-|-|-|
|memberId|Long|회원 식별자|
|nickname|String|닉네임|
|email|String|이메일|
|isPublic|Boolean|프로필 공개 여부|
|status|MemberStatus(Enum)|회원 상태(ACTIVE, SUSPENDED, DELETED)|
|createdAt|Instant|회원 가입 일시(UTC)|

##### Error Response
|Status|Message|
|-|-|
|401 Unauthorized|인증되지 않은 사용자입니다.|

##### Processing Flow
```
1. 클라이언트가 GET /users/me를 호출한다.
2. Spring Security가 현재 사용자의 Authentication을 확인한다.
3. BFF가 Member Service에 사용자 정보 조회를 요청한다.
4. Member Service가 회원 및 프로필 정보를 조회한다.
5. Member Service가 사용자 정보를 반환한다.
6. BFF가 사용자 정보를 클라이언트에 반환한다.
```

<br>

#### 5.2.2. GET /users/me/posts?page={pageNumber}
- 현재 사용자가 작성한 게시글 목록 조회

<br>

#### 5.2.3. GET /users/me/favorite-boards?page={pageNumber}
- 현재 사용자가 즐겨찾기 등록한 게시판 목록 조회

<br>

#### 5.2.4. POST /users/onboarding
- 사용자 세부 정보(닉네임 등) 제출


#### 5.2.5. PATCH /users/me
- 내 프로필 수정

<br>

#### 5.2.6. DELETE /users/me
- 회원 탈퇴

<br><br>

### 5.3. Profile API
- 해당 사용자 정보 조회
  - GET /profiles/{userId}
- 해당 사용자가 작성한 게시글 목록 조회
  - GET /profiles/{userId}/posts?page={pageNumber}

<br><br>

### 5.4. Post API
- 전체 인기글 목록 조회
  - GET /posts/popular?page={pageNumber}
- 전체 인기글 검색 (제목/내용 필터 포함)
  - GET /posts/popular?keyword={keyword}&searchType={TITLE | CONTENT}&page={pageNumber}
- 게시글 통합 검색
  - GET /posts?keyword={keyword}&searchType={TITLE | CONTENT}&page={pageNumber}
- 게시글 조회
  - GET /posts/{postId}
- 게시글 삭제
  - DELETE /posts/{postId}
- 게시글 추천/비추천
  - POST /posts/{postId}/reactions
- 게시글 추천/비추천 취소
  - DELETE /posts/{postId}/reactions
- 게시글 신고
  - POST /posts/{postId}/reports
- 게시글의 댓글 목록 조회
  - GET /posts/{postId}/comments?page={pageNumber}
- 게시글의 인기댓글 목록 조회
  - GET /posts/{postId}/comments/popular
- 게시글 수정
  - PATCH /posts/{postId}

<br><br>

### 5.5. Board API
- 전체 게시판 목록 조회
  - GET /boards
- 게시판 이름 기반의 게시판 검색 결과 조회
  - GET /boards?keyword={keyword}&page={pageNumber}
- 현재 게시판 즐겨찾기 등록
  - POST /boards/{boardId}/favorite
- 현재 게시판 즐겨찾기 해제
  - DELETE /boards/{boardId}/favorite
- 현재 게시판의 게시글 목록 조회
  - GET /boards/{boardId}/posts?page={pageNumber}
- 현재 게시판의 인기글 목록 조회
  - GET /boards/{boardId}/posts/popular?page={pageNumber}
- 현재 게시판의 공지글 목록 조회
  - GET /boards/{boardId}/notices?page={pageNumber}
- 현재 게시판의 게시글 탭 검색
  - GET /boards/{boardId}/posts?keyword={keyword}&searchType={TITLE | CONTENT}&page={pageNumber}
- 현재 게시판의 인기글 탭 검색
  - GET /boards/{boardId}/posts/popular?keyword={keyword}&searchType={TITLE | CONTENT}&page={pageNumber}
- 현재 게시판의 공지글 탭 검색
  - GET /boards/{boardId}/notices?keyword={keyword}&searchType={TITLE | CONTENT}&page={pageNumber}
- 게시글 게시
  - POST /boards/{boardId}/posts
- 공지글 게시
  - POST /boards/{boardId}/notices

<br><br>

### 5.6. Comment API
- 대댓글 목록 조회
  - GET /comments/{commentsId}/replies?page={pageNumber}
- 게시글에 댓글 작성
  - POST /posts/{postId}/comments
- 대댓글 작성
  - POST /comments/{commentId}/replies
- 댓글 및 대댓글 수정
  - PATCH /comments/{commentId}
- 댓글 및 대댓글 삭제
  - DELETE /comments/{commentId}
- 댓글 및 대댓글 추천/비추천
  - POST /comments/{commentId}/reactions
- 댓글 및 대댓글 추천/비추천 취소
  - DELETE /comments/{commentId}/reactions
- 댓글 및 대댓글 신고
  - POST /comments/{commentId}/reports

<br><br>

### 5.7. Notice API
- 공지글 조회
  - GET /notices/{noticeId}
- 공지글 삭제
  - DELETE /notices/{noticeId}
- 공지글의 댓글 목록 조회
  - GET /notices/{noticeId}/comments?page={pageNumber}
- 공지글의 인기댓글 목록 조회
  - GET /notices/{noticeId}/comments/popular
- 공지글에 댓글 작성
  - POST /notices/{noticeId}/comments
- 공지글 수정
  - PATCH /notices/{noticeId}

<br><br>

### 5.8. Admin API
- 신고 관리
  - 게시글 신고 목록 조회
    - GET /admin/reports/posts?boardId={boardId}&page={pageNumber}
  - 댓글 신고 목록 조회
    - GET /admin/reports/comments?boardId={boardId}&page={pageNumber}
  - 게시글 신고 상세 정보 조회
    - GET /admin/reports/posts/{reportId}
  - 댓글 신고 상세 정보 조회
    - GET /admin/reports/comments/{reportId}
  - 게시글 신고 처리
    - PATCH /admin/reports/posts/{reportId}
  - 댓글 신고 처리
    - PATCH /admin/reports/comments/{reportId}
- 사용자 관리
  - 사용자 ID, 닉네임 기반의 사용자 검색
    - GET /admin/users?keyword={keyword}&searchType={ID | NICKNAME}&page={pageNumber}
  - 사용자 상세 정보 조회, 이용 정지된 사용자 상세 정보 조회
    - GET /admin/users/{userId}
  - 이용 정지된 사용자 목록 조회
    - GET /admin/users?status=SUSPENDED&page={pageNumber}
  - 사용자 ID, 닉네임 기반의 이용 정지된 사용자 검색
    - GET /admin/users?keyword={keyword}&searchType={ID | NICKNAME}&status=SUSPENDED&page={pageNumber}
  - 사용자 권한 변경
    - PATCH /admin/users/{userId}/role
  - 사용자 이용 정지, 사용자 이용 정지 해제
    - PATCH /admin/users/{userId}/status
- 게시판 관리
  - 게시판 이름 기반의 게시판 검색
    - GET /admin/boards?keyword={keyword}&page={pageNumber}
  - 게시판 상세 정보 조회, 숨김 처리된 게시판 상세 정보 조회
    - GET /admin/boards/{boardId}
  - 숨김 처리된 게시판 목록 조회
    - GET /admin/boards?status=HIDDEN
  - 게시판 이름 기반의 숨김 처리된 게시판 검색
    - GET /admin/boards?keyword={keyword}&hidden=true&page={pageNumber}
  - 게시판 숨김, 게시판 숨김 해제
    - PATCH /admin/boards/{boardId}/hidden
  - 게시판 게시글 작성 금지, 게시글 작성 허용
    - PATCH /admin/boards/{boardId}/post-permission
  - 게시판 댓글 작성 금지, 댓글 작성 허용
    - PATCH /admin/boards/{boardId}/comment-permission
- 게시글 관리
  - 게시글 ID, 게시글 제목 기반의 게시글 검색
    - GET /admin/posts?keyword={keyword}&searchType={ID | TITLE}&page={pageNumber}
  - 게시글 상세 정보 조회, 삭제된 게시글 상세 정보 조회
    - GET /admin/posts/{postId}
  - 삭제된 게시글 목록 조회
    - GET /admin/posts?status=DELETED
  - 게시글 ID, 게시글 제목 기반의 삭제된 게시글 검색
    - GET /admin/posts?keyword={keyword}&searchType={ID | TITLE}&status=DELETED&page={pageNumber}
  - 게시글 삭제
    - DELETE /admin/posts/{postId}
  - 게시글 복구
    - PATCH /admin/posts/{postId}/deleted
- 공지글 관리
  - 공지글 ID, 공지글 제목 기반의 공지글 검색
    - GET /admin/notices?keyword={keyword}&searchType={ID | TITLE}&page={pageNumber}
  - 공지글 상세 정보 조회, 삭제된 공지글 상세 정보 조회
    - GET /admin/notices/{noticeId}
  - 삭제된 공지글 목록 조회
    - GET /admin/notices?status=DELETED
  - 공지글 ID, 공지글 제목 기반의 삭제된 공지글 검색
    - GET /admin/notices?keyword={keyword}&searchType={ID | TITLE}&status=DELETED&page={pageNumber}
  - 공지글 삭제
    - DELETE /admin/notices/{noticeId}
  - 공지글 복구
    - PATCH /admin/notices/{noticeId}/deleted
- 댓글 관리
  - 댓글 ID 기반의 댓글 검색
    - GET /admin/comments?commentId={commentId}&page={pageNumber}
  - 댓글 상세 정보 조회, 삭제된 댓글 상세 정보 조회
    - GET /admin/comments/{commentId}
  - 삭제된 댓글 목록 조회
    - GET /admin/comments?status=DELETED
  - 댓글 ID 기반의 삭제된 댓글 검색
    - GET /admin/comments?commentId={commentId}&page={pageNumber}
  - 댓글 삭제
    - DELETE /admin/comments/{commentId}
  - 댓글 복구
    - PATCH /admin/comments/{commentId}/deleted

<br><br><br>

## Related Documents
- [Common Policies](https://github.com/VectR-Shin/Community/blob/main/docs/requirements/common%20policies.md)
- [Authentication and Authorization](https://github.com/VectR-Shin/Community/blob/main/docs/authentication%20and%20authorization/authentication%20and%20authorization%20design.md)
- [Member Service API Design](https://github.com/VectR-Shin/Community/blob/main/docs/api/microservices/member%20service%20api%20design.md)
- [Community Service API Design](https://github.com/VectR-Shin/Community/blob/main/docs/api/microservices/community%20service%20api%20design.md)
- [Admin Service API Design](https://github.com/VectR-Shin/Community/blob/main/docs/api/microservices/admin%20service%20api%20design.md)
- [Error Response Design](https://github.com/VectR-Shin/Community/blob/main/docs/api/error%20response%20design.md)
