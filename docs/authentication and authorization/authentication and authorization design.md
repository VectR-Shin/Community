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
- ㅇ

<br>

### 2.2. 로그인 페이지 표시
- 0

<br>

### 2.3. 사용자 로그인
- 0

<br>

### 2.4. Authorization Code 발급
- 0

<br>

### 2.5. 토큰 발급
- 0

<br>

### 2.6. 세션 생성
- 0

<br>

### 2.7. 인증 완료
- 0

<br><br><br>

## 3. OAuth2 Authorization Code Flow

<br><br><br>

## 4. JWT 관리 방식(JWT Management)

<br><br><br>

## 5. SecurityContext 관리(Session Management)

<br><br><br>

## 6. 권한(Role) 설계(Role Design)

<br><br><br>

## 7. 권한 검증 방식(Authorization Policy)

<br><br><br>

## 8. 인증이 필요한 API

<br><br><br>

## 9. 인가 정책(Authorization Policy)

<br><br><br>

## 10. 로그아웃(Logout Process)

<br><br><br>

## 11. 보안 고려사항(Security Considerations)

<br><br><br>

## Related Documents
- [System Architecture Design](https://github.com/VectR-Shin/Community/blob/main/docs/architecture/system%20architecture.md)
- [설계 과정](https://app.notion.com/p/Project-Community-37f682e99f4f80b39046fadb0f2d634b?p=37f682e99f4f80dd9495f879e85933cf&pm=s)

