# Passwordless Authentication service

Service, authenticates without providing password

# Quick start

Run from sources
```
./mvnw spring-boot:run
```

Build and run docker image
```
./mvnw install
docker-compose up --build 
```

Sample usage

Send OTP to client:
```
curl -X POST -d '{"destination": "+1999999999"}'  -H "Content-Type: application/json" 'http://localhost:8080/otp/v1/sms/send' 
```
Sample response:
```
{"operationId":"993e61be-23cf-412d-8273-f02e316e8689"}
```

Validate OTP:
```
curl -X POST -d '{"operationId": "993e61be-23cf-412d-8273-f02e316e8689", "otp": "123456"}'  -H "Content-Type: application/json" 'http://localhost:8080/otp/v1/verify'
```
Sample response: 
```
{"verified":false}
```