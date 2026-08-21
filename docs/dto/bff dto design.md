# 1. Request DTO
## 1.1. Common DTO

<br><br>

## 1.2. User DTO
### 1.2.1. UserOnboardingRequestDTO
#### Example
```
{
  "nickname": "Vect_R"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|nickname|String|사용자 닉네임|

<br>

### 1.2.2. UserProfileUpdateRequestDTO
#### Example
```
// 변경할 필드만 포함하여 요청한다.
{
  "nickname": "NewNickname",
  "isPublic": false
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|nickname|String|변경할 닉네임|
|isPublic|Boolean|프로필 공개 여부|

<br><br>

## 1.3. Profile DTO

<br><br>

## 1.4. Board DTO

<br><br>

## 1.5. Post DTO

<br><br>

## 1.6. Comment DTO

<br><br>

## 1.7. Report DTO

<br><br><br>

# 2. Response DTO
## 2.1. Common DTO
### 2.1.1. AuthInfoResponseDTO
#### Example
```
// 인증된 사용자
{
  "authenticated": true,
  "memberId": 1,
  "roles": [
    "USER"
  ]
}
```
```
// 인증되지 않은 사용자
{
  "authenticated": false,
  "memberId": null,
  "roles": []
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|authenticated|Boolean|현재 사용자의 인증 여부|
|memberId|Long|회원 식별자(인증되지 않은 경우 null)|
|roles|List<String>|사용자 역할(USER, MANAGER, SUB_MANAGER, ADMIN)|

<br>

### 2.1.2. PageResponseDTO<T>
#### Example
```
{
  "content": [
    {
      ...
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

#### Type Parameter
|Parameter|Type|Description|
|-|-|-|
|T|Generic|응답할 리소스 목록 페이지의 각 항목에 사용되는 DTO 타입|

#### Fields
|Field|Type|Description|
|-|-|-|
|content|List<T>|응답할 목록 페이지의 데이터 목록|
|page|Integer|현재 페이지 번호|
|size|Integer|페이지 크기|
|totalElements|Long|전체 게시글 수|
|totalPages|Integer|전체 페이지 수|

<br><br>

## 2.2. User DTO
### 2.2.1. CurrentUserResponseDTO
#### Example
```
{
  "memberId": 1,
  "nickname": "shin",
  "email": "shin@example.com",
  "isPublic": true,
  "status": "ACTIVE",
  "createdAt": "2026-08-03T22:01:23Z"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|memberId|Long|회원 식별자|
|nickname|String|닉네임|
|email|String|이메일|
|isPublic|Boolean|프로필 공개 여부|
|status|MemberStatus(Enum)|회원 상태(ACTIVE, SUSPENDED, DELETED)|
|createdAt|Instant|회원 가입 일시(UTC)|

<br><br>

## 2.3. Profile DTO
### 2.3.1. UserProfileResponseDTO
#### Example
```
{
  "memberId": 1,
  "nickname": "user1",
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|memberId|Long|사용자 ID|
|nickname|String|사용자 닉네임|

<br><br>

## 2.4. Board DTO
### 2.4.1. BoardSummaryResponseDTO
#### Example
```
{
  "boardId": 1,
  "name": "자유게시판",
  "categoryId": 10,
  "categoryName": "커뮤니티"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|boardId|Long|게시판 ID|
|name|String|게시판 이름|
|categoryId|Long|게시판 카테고리 ID|
|categoryName|String|게시판 카테고리 이름|

<br><br>

## 2.5. Post DTO
### 2.5.1. PostSummaryResponseDTO
#### Example
```
{
  "postId": 1,
  "boardId": 10,
  "boardName": "자유게시판",
  "title": "게시글 제목",
  "commentCount": 100,
  "viewCount": 1000,
  "createdAt": "2026-08-15T08:30:00Z"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|postId|Long|게시글 ID|
|boardId|Long|게시판 ID|
|boardName|String|게시판 이름|
|title|String|게시글 제목|
|commentCount|Integer|게시글의 댓글 개수|
|viewCount|Integer|게시글 조회수|
|createdAt|Instant|게시글 작성 일시|

<br><br>

## 2.6. Comment DTO

<br><br>

## 2.7. Report DTO


<br><br><br>
