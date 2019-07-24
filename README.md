# Passwordless Authentication Service

Helps to authenticate users without providing password.

This service can be used to authenticate user, using 
[one time password](https://en.wikipedia.org/wiki/One-time_password) (OTP) authentication or 
[Web Authentication](https://en.wikipedia.org/wiki/WebAuthn) (WebAuthn) 

One can also use it as second authentication factor (2FA) alongside with login and password or to authorize essential 
operations (for example, change password, or confirm payment) for the already authenticated user.

# Table of contents

1. [How it works](#how-it-works)
    1. [Example Use Cases](#example-use-cases)
        1. [Registration](#registration)
        1. [Essential Operation Confirmation (Authorization)](#essential-operation-confirmation-authorization)
1. [Quick Start](#quick-start)

# How it works

You have site or web service what needs to be protected, or needs additional protection. 
You set up Passworless service and integrate it with your site.
A user enters credentials on your site, you get phone or email from the users credentials, and call Passwordless service API.
Passwordless service sends one time password (OTP) to the users phone or email.
The user enters this OTP and then you verify it at Passwordless service. 
If verification was successful, the user can be authenticated.

## Example Use Cases

### Registration
While registering the user enters his phone number or email among other data. 
Site calls Passwordless service to comfirm users email or phone number, to be sure that phone or email belongs to the user.
After user enters valid OTP, user account with confirmed phone or email can be created.

This process shown on the diagram below:
![Registration diab](diagrams/Registration.png)

### Authentication
While authentication the user enters his login, site gets users phone number or email from his profile and calls 
Passwordless service. Passwordless service sends OTP to the users phone or email. Users enters OTP, if OTP is valid, 
the user can be authenticated.

### Essential Operation Confirmation (Authorization)
If there'a need to change password, restore password or confirm purchase or payment, site calls Passwordless service
to be sure that exactly the user performs this critical operation. 


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

Adjust settings in [otp-sample-settings.yaml](./otp-sample-settings.yaml)
```yaml
#dummy OTP sender (does noting just logs)
- id: "sms"
  name: "Dummy SMS OTP Setting"
  messageTitle: "Acme LLC"
  messageTemplate: "Confirmation code: ${otp}"
  otpLength: 5
  useLetters: false
  useDigits: true
  ttlMinutes: 3
  sender: "dummyOTPSender"

#Twilio SMS Sender
- id: "twilioSms"
  name: "Twilio SMS OTP Setting"
  messageTitle: "Acme LLC"
  messageTemplate: "Confirmation code: ${otp}"
  otpLength: 5
  useLetters: false
  useDigits: true
  ttlMinutes: 3
  sender: "twilioOTPSender"

#Email OTP Link Sender
- id: "email"
  name: "TEST Email"
  messageTitle: "Thank yor for registration"
  messageTemplate: "Temporary link: http://acme.com?link=${otp}"
  otpLength: 36
  useLetters: true
  useDigits: true
  ttlMinutes: 180 #three hours
  sender: "emailOTPSender"
```

Send OTP to client with SMS setting:
```
curl -X POST -d '{"destination": "+1999999999"}'  -H "Content-Type: application/json" 'http://localhost:8080/otp/v1/sms/send' 
```
where /sms/ - otp settings ID
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

More details in [swagger.yaml](./swagger.yaml)
