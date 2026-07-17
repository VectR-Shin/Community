## 1. 인증 구조(Authentication Architecture)
- 본 시스템은 OAuth2 Authorization Code Grant 와 OpenID Connect(OIDC)를 기반으로 사용자 인증을 수행한다. 인증 서버는 Keycloak 을 사용하며, BFF 는 OAuth2 Client 역할을 수행하여 인증을 처리한다. 인증이 완료되면 BFF 는 Access Token, Refresh Token 을 서버에서 관리하며, Resource Server 와의 통신에 Access Token 을 사용한다. 각 Resource Sever 는 JWT 를 검증하고 사용자 권한에 따른 접근 제어를 수행한다.
- BFF, OAuth2/OIDC, Keycloak 을 채택한 배경과 전체 시스템 구성에 대한 내용은 본 문서 하단의 Related Documents 에서 다음의 문서들을 참조하기를 바란다.
  - System Architecture Design
  - 설계 과정

<br><br><br>

## 2. 인증 흐름(Authentication Flow)
<img width="539" height="741" alt="Authentication Flow Sequence Diagram" src="https://github.com/user-attachments/assets/a7ba2519-b063-41b4-b7ee-3530df07d4ba" />

<br>

### 2.1. 보호된 페이지 접근
- 사용자가 인증이 필요한 페이지에 접근하면 BFF 는 인증 여부를 확인하고, 인증되지 않은 경우 Keycloak 로그인 페이지로 리다이렉트한다.
- 혹은 사용자가 Keycloak 의 로그인 페이지로 직접 접근하여 인증을 시작할 수 있다.

<br>

### 2.2. 로그인 페이지 표시
- Keycloak 은 로그인 페이지를 반환하고, SPA 는 이를 사용자에게 표시한다.

<br>

### 2.3. 사용자 로그인
- 사용자는 계정 정보를 입력한 뒤 로그인하고, SPA 는 인증 정보를 Keycloak 에 전달한다.

<br>

### 2.4. Authorization Code 발급
- 인증이 완료되면 Keycloak 은 Authorization Code 를 BFF 에 전달한다.

<br>

### 2.5. 토큰 발급
- BFF 는 Authorization Code 를 이용하여 Access Token 과 Refresh Token 을 요청하고, Keycloak 은 검증 후 토큰을 발급한다.

<br>

### 2.6. 세션 생성
- BFF 는 발급받은 토큰을 서버에 저장하고, Security Context 와 세션을 생성한 뒤 Session Cookie 를 클라이언트에 전달한다.

<br>

### 2.7. 인증 완료
- 클라이언트는 발급받은 세션 쿠키를 이용해 인증 상태를 유지하며 원래 요청했던 보호된 페이지를 정상적으로 조회한다.

<br><br><br>

## 3. JWT 관리(JWT Management)
<img width="619" height="82" alt="JWT Managerment Diagram" src="https://github.com/user-attachments/assets/d5d3a86e-98fe-4898-bf6c-06306340fe92" />

<br>

### 3.1. JWT 발급
- 사용자가 인증을 완료하면 Keycloak 은 Access Token 과 Refresh Token 을 발급한다.
- 발급된 토큰은 BFF 가 수신하여 이후 인증 및 인가 과정에서 사용한다.

<br>

### 3.2. JWT 저장 방식
- 발급된 Access Token 과 Refresh Token 은 사용자 세션과 연계하여 BFF 서버에서 안전하게 관리된다.
- 클라이언트에는 JWT 를 저장하지 않으며, 인증 상태 유지를 위해 Session ID 가 포함된 HttpOnly Session Cookie 만 전달한다.

<br>

### 3.3. JWT 사용 방식
- BFF 는 Resource Server 와 통신할 때 저장된 Access Token 을 Authorization 헤더에 포함하여 요청을 전송한다.
- Resource Server 는 JWT 를 검증한 후 사용자 정보를 확인하고, 필요한 권한(Role)을 검사하여 요청을 처리한다.
- 
<br>

### 3.4. JWT 사용시 보안상 장점
- JWT 를 클라이언트에 저장하지 않음으로써 XSS 를 통한 토큰 탈취 위험을 줄일 수 있다.
- Access Token, Refresh Token 을 서버에서 중앙 관리함으로써 토큰 갱신 및 폐기와 같은 인증 관련 처리를 일관되게 수행할 수 있다.

<br><br><br>

## 4. Session 관리(Session Management)
<img width="781" height="82" alt="Session Management Diagram" src="https://github.com/user-attachments/assets/643b5924-7f0e-4ec8-969f-ec65f1a00e91" />

<br>

- BFF 는 Session 과 SecurityContext 를 이용하여 사용자의 인증 상태를 관리한다.

<br>

### 4.1. Session 생성
- 사용자가 인증을 완료하면 BFF 는 사용자의 인증 정보를 기반으로 세션을 생성한다.
- 생성된 세션에는 사용자의 인증 상태를 유지하기 위한 정보가 저장된다.

<br>

### 4.2. Session 유지
- 클라이언트는 HttpOnly Session Cookie 를 이용하여 인증 상태를 유지한다.
- 이후의 모든 요청부터는 Session Cookie 를 통해 세션을 식별하며, 클라이언트는 JWT 를 직접 전송하지 않는다.

<br>

### 4.3. Session 검증
- 요청을 수신한 BFF 는 Session Cookie 를 이용하여 사용자의 세션을 조회한다.
- 유효한 세션인 경우 세션에 저장된 인증 정보를 복원하여 요청을 인증된 사용자로 처리한다.
- 이후 BFF는 저장된 Access Token을 Authorization 헤더에 포함하여 Resource Server에 요청을 전달한다.
- 세션이 존재하지 않거나 만료된 경우에는 인증을 요구한다.

<br>

### 4.4. Session 만료
- 세션이 만료되면 사용자는 다시 로그인하여 인증을 수행해야 한다.
- 로그아웃 시에는 세션을 삭제하여 인증 상태를 종료한다.

<br><br><br>

## 5. 권한 설계(Role Design)

### 5.1. 권한(Role) 정의
|권한|설명|
|-|-|
|ADMIN|시스템 전체에 대한 관리 권한을 가지는 최고 관리자|
|MANAGER|특정 게시판에 대한 관리 권한을 가지는 관리자|
|SUB_MANAGER|특정 게시판에 대한 일부 관리 권한을 가지는 관리자|
|USER|일반 회원|

<br>

### 5.2. 권한 계층 구조
<img width="149" height="442" alt="Role Hierarchy" src="https://github.com/user-attachments/assets/3db1538d-9342-4a10-b947-5dd52cfeffd3" />

- 본 시스템은 Spring Security 의 Role Hierarchy 를 적용하여 역할 간 계층 구조를 적용한다.
- 상위 역할은 하위 역할의 모든 권한을 저동으로 포함한다.

<br>

### 5.3. 권한별 책임
|권한|주요 책임|
|-|-|
|ADMIN|게시판 관리, 게시판 생성 및 숨김, 관리자 권한 관리, 사용자 관리 등 시스템 전체를 관리|
|MANAGER|담당 게시판 관리, 담당 게시판의 게시글과 댓글 관리, 담당 게시판의 부관리자 권한 관리, 사용자 관리 등 담당 게시판을 관리|
|SUB_MANAGER|담당 게시판의 게시글과 댓글 관리, 사용자 관리 등 담당 게시판을 일부 관리|
|USER|게시글 작성, 댓글 작성, 추천, 신고 등 일반적인 커뮤니티 기능 이용|

- 권한별 책임에 대한 보다 자세한 내용은 문서 하단의 Common Policies 링크를 참조한다.

<br>

### 5.4. 권한 검증 방식
- 일반 기능은 역할 기반으로 접근 권한을 제어(RBAC: Role-based Access Control)한다.
- 게시판 관리 기능은 역할(Role)과 담당 게시판(Resource)을 함께 검증(Resource-based Authorization)하여 접근 권한을 결정한다.

<br><br><br>

## 6. 권한 검증 방식(Authorization Policy)

<br><br><br>

## 7. 인증이 필요한 API

<br><br><br>

## 8. 인가 정책(Authorization Policy)

<br><br><br>

## 9. 로그아웃(Logout Process)

<br><br><br>

## 10. 보안 고려사항(Security Considerations)

<br><br><br>

## Related Documents
- [System Architecture Design](https://github.com/VectR-Shin/Community/blob/main/docs/architecture/system%20architecture.md)
- [설계 과정](https://app.notion.com/p/Project-Community-37f682e99f4f80b39046fadb0f2d634b?p=37f682e99f4f80dd9495f879e85933cf&pm=s)
- [Common Policies](https://github.com/VectR-Shin/Community/blob/main/docs/requirments/common%20policies.md)
