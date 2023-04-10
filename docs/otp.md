Initialize OTP authentication

Post phone

Request
```json
{
  "type": "phone", 
  "destination": "+7-999-123-45-67",
  "template": "registration"
}
```

Response
```json
{
  "sessionId": "some uuid",
  "sentTo" :"+7-999-123-45-67",
  "resendAllowedAt": 1000000000,
  "remainingAttempts": 5
}
```
Error response:
```json
{
  "error": "phone_is_not_valid",
  "errorMessage": "Phone is not valid",
  "resendAllowedAt": 1000000000,
  "attempts": 5
}
```

Validate Code
```json
{
  "code": "123456",
  "sessionId": "some uuid"
}
```

POST Email

Request
```json
{
  "phone": "test@test.com",
  "template": "authentication"
}
```

Validate URL
```json
{
  "urlParameter": "123456",
  "sessionId": "some uuid"
}
```