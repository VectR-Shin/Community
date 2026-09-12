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
|nickname|String(20)|사용자 닉네임|

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
|nickname|String(20)|변경할 닉네임|
|isPublic|Boolean|프로필 공개 여부|

<br><br>

## 1.3. Profile DTO

<br><br>

## 1.4. Board DTO

<br><br>

## 1.5. Post DTO
### 1.5.1. PostReactionRequestDTO
#### Example
```
{
  "reactionType": "LIKE"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|reactionType|ReactionType(Enum)|추천/비추천(LIKE | DISLIKE)|

<br>

### 1.5.2. PostUpdateRequestDTO
#### Example
```
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|title|String(50)|수정된 제목. 수정하지 않을 경우 생략(null)|
|content|String(5000)|수정된 내용. 수정하지 않을 경우 생략(null)|

<br><br>

## 1.6. Comment DTO
### 1.6.1. CommentRequestDTO
#### Example
```
{
  "parentId": 12,
  "content": "이 댓글을 새로 추가해야지!",
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|parentId|Long|댓글의 부모 댓글 ID. 최상위 댓글인 경우 null|
|content|String|댓글 내용|

<br><br>

## 1.7. Report DTO
### 1.7.1. PostReportRequestDTO
#### Example
```
{
  "reportType": "SPAM",
  "content": "혐오감을 조성하는 게시글이라 신고했습니다."
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|reportType|ReportType(Enum)|신고 타입(ABUSE | SPAM | INAPPROPRIATE_CONTENT | OTHER)|
|content|String(100)|신고 상세 내용|

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
|nickname|String(20)|닉네임|
|email|String(320)|이메일|
|isPublic|Boolean|프로필 공개 여부|
|status|MemberStatus(Enum)|회원 상태(ACTIVE, SUSPENDED, DELETED)|
|createdAt|Instant|회원 가입 일시(UTC)|

<br>

### 2.2.2. UserSummaryResponseDTO
#### Example
```
{
  "memberId": 10,
  "nickname": "닉네임"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|memberId|Long|사용자 ID|
|nickname|String(20)|사용자 닉네임|

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
|nickname|String(20)|사용자 닉네임|

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
|name|String(20)|게시판 이름|
|categoryId|Long|게시판 카테고리 ID|
|categoryName|String(20)|게시판 카테고리 이름|

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
  "reactionCount": 100,
  "createdAt": "2026-08-15T08:30:00Z"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|postId|Long|게시글 ID|
|boardId|Long|게시판 ID|
|boardName|String(20)|게시판 이름|
|title|String(50)|게시글 제목|
|commentCount|Integer|게시글의 댓글 개수|
|viewCount|Integer|게시글 조회수|
|reactionCount|Integer|추천 수 - 비추천 수|
|createdAt|Instant|게시글 작성 일시|

<br>

### 2.5.2. PostResponseDTO
#### Example
```
{
  "postId": 1,
  "boardId": 1,
  "title": "제목",
  "content": "내...용",
  "author": {
    "memberId": 10,
    "nickname": "닉네임"
  },
  "viewCount": 120,
  "likeCount": 10,
  "dislikeCount": 0,
  "commentCount": 3,
  "myReaction": "LIKE"
  "createdAt": "2026-08-03T22:01:23Z",
  "updatedAt": "2026-09-03T20:11:03Z"
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|postId|Long|게시글 ID|
|boardId|Long|게시판 ID|
|title|String(50)|게시글 제목|
|content|String(5000)|게시글 내용|
|author|UserSummaryResponseDTO|작성자 정보|
|viewCount|Integer|조회수|
|likeCount|Integer|추천 수|
|dislikeCount|Integer|비추천 수|
|commentCount|Integer|댓글 수|
|myReaction|String|이 게시물에 대한 나의 추천/비추천 여부. 추천/비추천을 하지 않았다면 null|
|createdAt|Instant|작성일시|
|updatedAt|Instant|수정일시. 수정된 적이 없다면 null|

<br>

### 2.5.3. PostReactionResponseDTO
#### Example
```
{
  "reactionType": "LIKE",
  "likeCount": 13,
  "dislikeCount": 1
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|reactionType|String|나의 게시글 추천/비추천 여부|
|likeCount|Integer|나의 추천/비추천 이후 좋아요 수|
|dislikeCount|Integer|나의 추천/비추천 이후 싫어요 수|

<br><br>

## 2.6. Comment DTO
### 2.6.1. CommentResponseDTO
#### Example
```
// 일반적인 댓글 응답
{
  "commentId": 11,
  "postId": 10,
  "parentId": 2,
  "author": {
    "memberId": 5,
    "nickname": "닉네임"
  },
  "content": "좋은 글 감사합니다.",
  "likeCount": 12,
  "dislikeCount": 1,
  "isDeleted": false,
  "createdAt": "2026-08-01T22:01:23Z",
  "updatedAt": null
}

// 삭제된 댓글 응답
{
  "commentId": 1,
  "postId": 10,
  "parentId": 2,
  "author": null,
  "content": null,
  "likeCount": null,
  "dislikeCount": null,
  "isDeleted": true,
  "createdAt": null,
  "updatedAt": null
}
```

#### Fields
|Field|Type|Description|
|-|-|-|
|commentId|Long|댓글 ID|
|postId|Long|댓글이 속한 게시글 ID|
|parentId|Long|댓글의 부모 댓글 ID. 최상위 댓글인 경우 null|
|author|UserSummaryResponseDTO|댓글 작성자 정보|
|content|String(100)|댓글 내용|
|likeCount|Integer|추천 수|
|dislikeCount|Integer|비추천 수|
|isDeleted|Boolean|댓글의 삭제 여부|
|createdAt|Instant|작성일시|
|updatedAt|Instant|수정일시. 수정된 적이 없다면 null|

#### 비고
- 삭제된 댓글의 경우, commentId, postId, parentId, isDeleted 를 제외한 나머지 필드는 null 로 제공한다.

<br><br>

## 2.7. Report DTO


<br><br><br>
