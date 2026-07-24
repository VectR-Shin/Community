## 1. Database Overview
- 본 시스템은 MySQL 기반의 관계형 데이터베이스를 사용한다.
- 회원, 게시판, 게시글, 댓글, 신고 등 도메인별로 엔티티를 분리하여 설계하였으며, 데이터 무결성과 확장성을 고려하여 데이터 모델을 구성하였다.
- 본 시스템은 MSA(Microservice Architecture)를 적용하므로 서비스별로 독립적인 데이터베이스(Database per Service)를 사용한다.
- 각 서비스는 자신의 데이터만 직접 관리하며, 다른 서비스의 데이터는 Foreign Key 로 참조하지 않는다.
- 서비스 간 연관 관계는 식별자(ID)를 통해 표현하며, 필요한 정보는 서비스 간 통신을 통해 조회하도록 설계하였다.

<br><br><br>

## 2. ERD


<br><br><br>

## 3. 엔티티 설명
### 3.1. Member Service
|Entity|설명|비고|
|-|-|-|
|Member|||
|Profile|||

<br>

### 3.2. Community Service
|Entity|설명|비고|
|-|-|-|
||||

<br>

### 3.1. Admin Service
|Entity|설명|비고|
|-|-|-|
||||

<br><br><br>

## 4. 관계(Relationship) 설계


<br><br><br>

## 5. 데이터베이스 및 JPA 설계 고려사항
|설계|이유|
|-|-|
|Soft Delete 적용|게시글, 댓글 등 사용자 콘텐츠는 Soft Delete 를 적용하여 데이터 간의 관계를 유지하고 감사 및 복구 가능성을 확보하도록 설계하였다.|
|MANAGER 설계|MANAGER, SUB_MANAGER 는 특정 게시판에 종속되므로, 사용자와 게시판 간의 별도 매핑 엔티티를 통해 관리하도록 설계하였다.|
|정규화|데이터 중복을 최소화하기 위해 제3정규형을 기준으로 엔티티를 분리하였다.|
|Cascade 사용 최소화|연관 데이터 삭제 시 의도치 않은 데이터 손실을 방지하기 위해 Cascade 사용을 최소화하였다.|
|Fetch 전략|연관 엔티티에 기본적으로 FetchType.LAZY 를 적용하여 불필요한 조회(N+1 문제)를 방지하고 조회 성능을 향상시켰다. <br>필요한 경우에는 Fetch Join 을 사용하여 연관 데이터를 함께 조회하도록 설계하였다.|

<br><br><br>
