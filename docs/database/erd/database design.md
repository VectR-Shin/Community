## 1. Database Overview
- 본 시스템은 MySQL 기반의 관계형 데이터베이스를 사용한다.
- 회원, 게시판, 게시글, 댓글, 신고 등 도메인별로 엔티티를 분리하여 설계하였으며, 데이터 무결성과 확장성을 고려하여 데이터 모델을 구성하였다.
- 본 시스템은 MSA(Microservice Architecture)를 적용하므로 서비스별로 독립적인 데이터베이스(Database per Service)를 사용한다.
- 각 서비스는 자신의 데이터만 직접 관리하며, 다른 서비스의 데이터는 Foreign Key 로 참조하지 않는다.
- 서비스 간 연관 관계는 식별자(ID)를 통해 표현하며, 필요한 정보는 서비스 간 통신을 통해 조회하도록 설계하였다.

<br><br><br>

## 2. ERD
### 2.1. Member Service ERD
<img width="1288" height="488" alt="Member Service ERD" src="https://github.com/user-attachments/assets/b803b692-aa57-418f-984c-b35e30f85bfa" />

<br>

### 2.2. Community Service ERD
<img width="1318" height="827" alt="Community Service ERD" src="https://github.com/user-attachments/assets/f37a1095-bece-49f0-b27a-fc1c4f75eef6" />

<br>

### 2.3. Admin Service ERD
<img width="716" height="567" alt="Admin Service ERD" src="https://github.com/user-attachments/assets/41067bb8-3bc8-412f-8c4c-cae28d3cf447" />

<br><br><br>

## 3. Entity Description
### 3.1. Member Service
|Entity|설명|비고|
|-|-|-|
|Member|서비스 관점의 사용자 정보를 저장한다.|Keycloak User ID 를 참조한다.|
|MemberSuspension|사용자 정지 정보를 저장한다.||
|Profile|회원의 프로필 정보를 저장한다.|회원이 수정 가능한 정보를 관리한다.|

- 최초의 소셜 로그인 시 Member 가 생성되며, 온보딩 과정에서 Profile 이 생성된다.
- Profile 의 존재 여부를 통해 온보딩 완료 여부를 판단한다.

<br>

### 3.2. Community Service
|Entity|설명|비고|
|-|-|-|
|Board|게시판 정보를 저장한다.||
|BoardCategory|게시판의 카테고리 정보를 저장한다.||
|BoardFavorite|게시판 즐겨찾기 정보를 저장한다.||
|BoardManager|게시판 관리자 정보를 저장한다.|MANAGER, SUB_MANAGER|
|Post|게시글 및 공지글 정보를 저장한다.||
|PostReaction|게시글 추천 및 비추천 정보를 저장한다.||
|Comment|댓글 정보를 저장한다.||
|CommentReaction|댓글 추천 및 비추천 정보를 저장한다.||
|PostReport|게시글 신고 정보를 저장한다.||
|CommentReport|댓글 신고 정보를 저장한다.||

<br>

### 3.3. Admin Service
|Entity|설명|비고|
|-|-|-|
|AdminActionLog|관리자의 작업 이력을 저장한다.||

<br><br><br>

## 4. Database Naming Convention
|대상|규칙|예시|
|-|-|-|
|Entity Class|PascalCase|PostReport, CommentReaction|
|Entity Field|camelCase|createdAt, isDeleted|
|Table|snake_case|post_report, comment_reaction|
|Column|snake_case|created_at, is_deleted|

<br><br><br>

## 5. Data Modeling Decision
### 5.1. 집계 칼럼 적용
- 게시글의 조회 수, 추천 수, 비추천 수, 댓글 수는 조회 성능 향상을 위해 집계 칼럼으로 관리한다.
- 추천 및 댓글 생성 시 비지니스 로직을 통해 갱신하도록 설계하였다.

<br>

### 5.2. 사용자별 추천 정보 분리
- 사용자별 추천 및 비추천 여부를 관리하기 위해 PostReaction, CommentReaction 엔티티를 별도로 분리하였다.
- 동일 사용자 중복 추천을 방지하기 위해 (게시글 ID, 사용자 ID), (댓글 ID, 사용자 ID) 에 복합 유니크(UNIQUE) 제약조건을 적용하였다.

<br>

### 5.3. 신고 엔티티 분리
- 게시글과 댓글은 참조 대상이 다르므로, PostReport, CommentReport 로 분리하여 신고 엔티티를 설계하였다.

<br>

### 5.4. Self FK 를 이용한 댓글 구조
- 댓글과 대댓글을 하나의 테이블에서 관리하기 위해 parent_id 를 이용한 Self FK 를 적용하였다.
- 운영 정책에 따라 대댓글은 최대 1단계까지만 허용한다.

<br>

### 5.5. Soft Delete 적용
- 게시글과 댓글은 삭제 시 데이터를 즉시 제거하는 대신 Soft Delete 방식을 적용한다.
- 삭제된 데이터의 추적 및 복구가 가능하도록 설계하였다.

<br>

### 5.6. 카테고리 삭제 정책
- 게시판과의 참조 무결성을 유지하기 위해 게시판 카테고리는 삭제를 허용하지 않는다.

<br>

### 5.7. 게시글과 공지글 통합
- 게시글과 공지글은 대부분의 속성이 동일하므로 Post 엔티티로 함께 관리한다.
- Post 엔티티의 post_type 칼럼을 통해 게시글과 공지글을 구분한다.

<br>

### 5.8. 날짜 및 시간 데이터를 UTC 기준으로 관리
-  서비스별 실행 환경이나 지역별 시간대 차이로 발생할 수 있는 시간 데이터 불일치를 방지하기 위해서 UTC 를 기준으로 한다.
-  API 응답에서는 ISO-8601 형식의 UTC 시간(yyyy-MM-dd’T’HH:mm:ssX)으로 날짜 및 시간 데이터를 제공한다.

<br>

### 5.9. 데이터베이스의 날짜 및 시간 데이터를 DATETIME 으로 선정
- 날짜 및 시간 데이터 저장을 위해 MySQL 의 DATETIME 타입을 사용한다.
- DATETIME 타입은 저장되는 값을 그대로 유지하므로, 애플리케이션에서 관리하는 UTC 기준 시간 데이터를 변환 없이 저장하기 적합하다고 판단하였다.

<br><br><br>

## 6. 데이터베이스 및 JPA 설계 고려사항
|설계|이유|
|-|-|
|Soft Delete 적용|게시글, 댓글 등 사용자 콘텐츠는 Soft Delete 를 적용하여 데이터 간의 관계를 유지하고 감사 및 복구 가능성을 확보하도록 설계하였다.|
|MANAGER 설계|MANAGER, SUB_MANAGER 는 특정 게시판에 종속되므로, 사용자와 게시판 간의 별도 매핑 엔티티를 통해 관리하도록 설계하였다.|
|정규화|데이터 중복을 최소화하기 위해 제3정규형을 기준으로 엔티티를 분리하였다.|
|Cascade 사용 최소화|연관 데이터 삭제 시 의도치 않은 데이터 손실을 방지하기 위해 Cascade 사용을 최소화하였다.|
|Fetch 전략|연관 엔티티에 기본적으로 FetchType.LAZY 를 적용하여 불필요한 조회(N+1 문제)를 방지하고 조회 성능을 향상시켰다. <br>필요한 경우에는 Fetch Join 을 사용하여 연관 데이터를 함께 조회하도록 설계하였다.|

<br><br><br>
