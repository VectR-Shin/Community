## 1. Overview
- 본 시스템의 API는 RESTful 스타일을 기반으로 설계되었으며, 클라이언트와 서버 간의 일관된 데이터 교환을 위해 JSON 형식을 사용한다.
- 본 시스템은 BFF(Backend For Frontend) 아키텍처를 적용하였으며, 클라이언트는 모든 API를 BFF를 통해 호출한다.
- BFF는 인증 및 세션을 관리하며, 각 마이크로서비스(Member, Community, Admin)와 통신하여 클라이언트에 필요한 데이터를 제공한다.

<br><br><br>

## 2. Design Principal
### 2.1. RESTful Style
- 리소스를 중심으로 URI 를 설계한다.
- URI 에는 동사 대신 명사를 사용한다.
- HTTP Method 를 이용하여 리소스에 대한 행위를 표현한다.

<br>

URI 설계 예시
|Method|URI|설명|
|-|-|-|
|GET|/posts|전체 게시글 목록 조회|
|GET|/boards/{boardId}/posts|특정 게시판의 게시글 목록 조회|
|GET|/posts/{postId}|특정 게시글 조회|
|POST|/boards/{boardId}/posts|특정 게시판에 게시글 작성|
|PATCH|/posts/{postId}|게시글 수정|
|DELETE|/posts/{postId}|게시글 삭제|

<br>

### 2.2. HTTP Method
|Method|설명|
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
|Code|설명|
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
- 페이지 번호는 Query Parameter: page 를 사용하며, 0 부터 시작한다.
- 페이지 크기는 API 별 정책에 따라 고정되며, 클라이언트에서 변경할 수 없다.
  - 정책은 본 문서 하단의 Related Documents 에서 Common Policies 문서를 참조하자.
- 




### 2.6. JSON Naming 관례


## 3. Authentication



## 4. Common Response



## 5. Error Code



## 6. API Specification



<br><br><br>

## Related Documents
- [Common Policies](https://github.com/VectR-Shin/Community/blob/main/docs/requirements/common%20policies.md)
