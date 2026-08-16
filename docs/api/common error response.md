## 1. Error Response Format
```
ErrorResponseDTO
{
  "status": 401,
  "code": "UNAUTHENTICATED",
  "message": "인증되지 않은 사용자입니다."
}
```

##### ErrorResponseDTO Fields
|Field|Type|Description|
|-|-|-|
|status|Integer|HTTP 상태 코드|
|code|String|애플리케이션에서 정의한 오류 코드|
|message|String|오류에 대한 설명|

<br><br><br>

## 2. Common Error Code
|Status|Code|Message|
|-|-|-|
|400 Bad Request|INVALID_REQUEST|잘못된 요청입니다.|
|401 Unauthorized|UNAUTHENTICATED|인증되지 않은 사용자입니다.|
|403 Forbidden|ACCESS_DENIED|해당 요청에 대한 권한이 없습니다.|
|403 Forbidden|ONBOARDING_REQUIRED|온보딩이 완료되지 않은 사용자입니다.|
|403 Forbidden|MEMBER_SUSPENDED|활동이 제한된 사용자입니다.|
|404 Not Found|RESOURCE_NOT_FOUND|요청한 리소스를 찾을 수 없습니다.|
|409 Conflict|RESOURCE_CONFLICT|요청이 현재 리소스 상태와 충돌합니다.|
|500 Internal Server Error|INTERNAL_SERVER_ERROR|서버 내부 오류가 발생했습니다.|

<br><br><br>
