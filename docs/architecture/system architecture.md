## 1. 시스템 아키택처 개요
### 1.1. 전체 아키텍처
- **BFF(Backend for Frontend)** 패턴을 적용하여 프론트엔드와 백엔드 간의 역할을 분리하였다. BFF는 클라이언트에 최적화된 API를 제공하며, 사용자의 인증 정보를 세션(SecurityContext)에 저장하고 Access Token 및 Refresh Token을 관리한다. 또한 인증 처리와 요청 중계 역할을 수행하여 클라이언트와 리소스 서버 간의 결합도를 낮추고, 인증 및 토큰 관리 로직을 중앙에서 일관되게 관리할 수 있도록 설계하였다.
- **MSA(Microservice Architecture)** 를 기반으로 구성하였다. 인증, 회원, 게시판 등의 주요 도메인을 독립적인 서비스로 분리하여 서비스 간의 독립성을 높이고 이후의 유지보수성과 확장성을 고려하였다.

<br>

### 1.2. 인증 구조
- **OAuth2.0** 및 **OIDC(OpenID Connect)** 기반으로 인증을 수행하며, 사용자 인증 정보는 **JWT(JSON Web Token)** 를 이용하여 서비스 간에 전달한다. 이를 통해 각 리소스 서버는 중앙 인증 서버에서 발급한 토큰을 검증해 사용자 인증/인가를 수행한다.
- **Keycloak** 을 사용하여 중앙 인증 서버를 구축한다. 이를 통해 인증 기능을 비지니스 서비스와 분리했으며, 이후 다른 애플리케이션이 추가되어도 동일한 인증 시스템을 사용할 수 있도록 **SSO(Single Sign-On)** 을 지원하는 구조로 설계하였다.

<br>

### 1.3. 데이터 계층
- 영속 데이터는 MySQL 데이터베이스에 저장한다.
- 변경 빈도가 낮으면서 조회 빈도가 높은 데이터(게시판 목록, 공지 목록, 인기글 등)는 **Redis** 를 이용하여 캐싱할 수 있도록 해, 데이터베이스의 부하를 줄이고 응답 성능이 향상되도록 설계하였다.

<br><br><br>

## 2. 시스템 아키텍처 다이어그램
<img width="1169" height="700" alt="System Architecture Diagram V1 0 2" src="https://github.com/user-attachments/assets/a6a62651-5447-4739-8462-5735eb556153" />

<br>

- 각 서비스는 논리적으로 독립된 데이터 저장소를 갖도록 설계 및 구현 예정이나, 위 그림에서는 복잡도를 낮추기 위해 하나의 MySQL 인스턴스만을 표현하였다.
- Redis 는 아래 항목들을 캐싱한다.
  - 게시판 목록
  - 인기글
- Spring Cloud Gateway 를 사용하지 않은 이유
  - 본 프로젝트의 BFF 서버는 Keycloak 과 인증을 수행하고, JWT 를 관리하며, 여러 마이크로서비스의 데이터를 조합하여 SPA 에게 제공하는 기능을 수행하기 때문이다.
<br>

- 설계에 대한 보다 자세한 내용은 Notion 의 설계 문서를 참조하기를 바란다.
  - 설계 문서는 Github - README - 설계 과정(설계 문서) 에 링크로 연결되어있다.

<br><br><br>

## 3. 구성 요소 설명
|구성 요소|설명|
|-|-|
|User|웹 브라우저를 통해 SPA 에 접속하고, 시스템의 다양한 기능을 이용하는 사용자이다.|
|SPA|사용자 인터페이스를 제공하는 프론트엔드 애플리케이션이다. 모든 API 요청은 BFF 를 통해 수행한다.|
|BFF|클라이언트 요청을 처리하는 진입점이다. OAuth2 인증, JWT 관리, API Aggregation 을 수행하며 각 마이크로서비스와 통신한다.|
|Keycloak|OAuth2/OIDC 기반 인증 서버이다. 사용자 인증, 토큰 발급을 담당하며 외부 Identity Provider(IdP) 와의 인증을 중앙에서 관리한다.|
|Identity Provider(IdP)|Google, Kakao, Naver 와 같은 외부 인증 제공자이다. 사용자 인증을 수행하며 Keycloak 과 연동된다.|
|Member Service|회원 정보, 프로필, 권한 등 사용자 관련 기능을 제공하는 마이크로서비스이다.|
|Community Service|게시판, 게시글, 댓글 등의 커뮤니티 기능을 제공하는 마이크로서비스이다.|
|Admin Service|신고 처리, 회원 관리, 게시물 및 댓글 관리 등의 관리자 기능을 제공하는 마이크로서비스이다.|
|Redis|조회 빈도가 높고 변경 빈도가 낮은 데이터를 캐싱하여 데이터베이스 부하를 줄이고 응답 성능을 향상시키는 캐시 저장소이다.|
|MySQL|각 마이크로서비스의 데이터를 저장하는 관계형 데이터베이스이다. Database per Service 패턴에 따라 서비스별 데이터베이스를 독립적으로 관리한다.|

<br><br><br>

## 4. 요청 처리 흐름
### 4.1. 로그인 요청
1. 사용자가 SPA 에서 로그인 버튼 클릭한다.
2. SPA 는 BFF 에 로그인 요청 전달한다.
3. BFF 는 사용자를 Keycloak 로그인 페이지로 리다이렉트한다.
4. 사용자는 Keycloak 로그인 페이지에서 원하는 Identity Provider(Google, Kakao, Naver) 를 선택한다.
5. Keycloak 은 사용자를 선택한 외부 권한부여 서버의 로그인 페이지로 리다이렉트한다.
6. 사용자는 해당 외부 권한부여 서버에서 로그인 및 동의를 수행한다.
7. 외부 권한부여 서버는 인증 결과를 Keycloak 에 전달한다.
8. Keycloak 은 인증을 완료한 뒤 Access Token, ID Token, Refresh Token 을 발급하여 BFF 에 전달한다.
9. BFF는 발급받은 토큰을 서버에서 관리하고 사용자의 로그인 상태를 유지한다.
10. BFF 는 SPA 에 로그인 완료 응답을 반환한다.

<br>

### 4.2. 일반 API 요청
1. 사용자가 SPA 에서 기능을 요청한다.
2. SPA 는 BFF 에 API 요청을 전달한다.
3. BFF 는 사용자의 인증 및 권한을 확인한 뒤 요청을 처리한다.
4. BFF 는 요청에 따라 적절한 마이크로서비스로 요청을 전달한다.
5. 각 마이크로서비스는 요청에 따라 Redis 또는 MySQL을 이용하여 데이터를 조회하거나 저장한다.
6. 여러 마이크로서비스의 데이터가 필요한 경우, BFF 는 API Aggregation 을 수행하여 하나의 응답으로 조합한다.
7. BFF 는 최종 응답을 SPA 에 반환한다.
8. SPA 는 응답 데이터를 사용자에게 표시한다.

<br><br><br>

## 5. 기술 스택
### >>>>>>>>>>>>>>>>>>>>>>>>>>>> 버전은 구현할 때 추가
|구분|기술|용도|
|-|-|-|
|Language|Java|백엔드 개발|
|Backend|Spring Boot|백엔드 애플리케이션 개발|
|Security|Spring Security|인증 및 권한 관리|
|Authentication|Keycloak|OAuth2/OIDC 기반 인증 및 SSO|
|Database|MySQL|관계형 데이터 저장|
|Cache|Redis|데이터 캐싱|
|Build Tool|Maven|프로젝트 빌드 및 의존성 관리|
|Container|Docker|컨테이너 기반 환경 실행|
|Version Control|Git, Github|형상 관리|

