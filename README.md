# Passwordless Authentication Service

[![Build](https://github.com/maximthomas/passwordless/actions/workflows/build.yml/badge.svg)](https://github.com/maximthomas/passwordless/actions/workflows/build.yml)
[![License](https://img.shields.io/github/license/maximthomas/passwordless)](https://github.com/maximthomas/passwordless/blob/master/LICENSE)
![CodeQL](https://github.com/maximthomas/passwordless/workflows/CodeQL/badge.svg)

Helps to authenticate users without providing password.

# Table of contents

- [How it works](#how-it-works)
- [Quick start](#quick-start)
- [One Time Password Authentication](#one-time-password-authentication)
  * [Introduction](#introduction)
  * [Sample Use Cases](#sample-use-cases)
    + [Registration Process](#registration-process)
    + [Authentication Process](#authentication-process)
    + [Essential Operation Confirmation (Authorization)](#essential-operation-confirmation--authorization-)
  * [Customize Settings](#customize-settings)
- [Using Time-Based One Time Password Authentication (TOTP)](#using-time-based-one-time-password-authentication--totp-)
  * [Prerequisites](#prerequisites)
  * [Registration](#registration)
  * [Authentication](#authentication)
- [Using Web Authentication (WebAuthn)](#using-web-authentication--webauthn-)
  * [Prerequisites](#prerequisites-1)
  * [Using Javascript SDK](#using-javascript-sdk)
    + [Registration](#registration-1)
    + [Login](#login)
- [Persistence](#persistence)


# How it works
So, you have a site or a web service that needs passwordless authentication or needs second-factor authentication.
Passwordless service is the simpler way to implement it. You just install it and integrate it with your site.
Passwordless service is used for authentication with either the
[one time password](https://en.wikipedia.org/wiki/One-time_password) (OTP) or using the
[Time-based one time password](https://en.wikipedia.org/wiki/Time-based_one-time_password) (TOTP) or using the
[Web Authentication](https://en.wikipedia.org/wiki/WebAuthn) (WebAuthn) protocol.

You just call the Passwordless API service and in the case of OTP authentication service generates, sends, and validates a one-time password.
In the case of WebAuthn, the Passwordless service registers or authenticates the user's public key.

You can also use it as a second authentication factor (2FA) alongside login and password or to authorize essential
operations (for example, change password, or confirm payment) for the already authenticated user.


# Quick start

There are several ways to run the Passwordless service:

Run from source code
```
$> ./mvnw spring-boot:run
```

Run as a Docker image
```
$> docker run --publish=8080:8080  maximthomas/passwordless
```

Build and run docker image using docker-compose
```
$> ./mvnw install
$> docker-compose up --build 
```

# One Time Password Authentication

## Introduction

A user enters credentials on your site, you get a phone or email from the user's credentials and call Passwordless service API.
Passwordless service generates and sends a one-time password (OTP) to the user's phone via SMS or to a user's E-mail.
You can use any custom provider.
The user enters the received OTP and then you verify it at Passwordless service.
If verification was successful, the user can be authenticated.

## Sample Use Cases

### Registration Process
While registering the user enters his phone number or email among other data.
Site calls Passwordless service to comfirm users email or phone number, to be sure that phone or email belongs to the user.
After user enters valid OTP, user account with confirmed phone or email can be created.

This process shown on the diagram below:
![Registration diab](docs/images/Registration.png)

### Authentication Process
While authentication the user enters his login, site gets users phone number or email from his profile and calls
Passwordless service. Passwordless service sends OTP to the users phone or email. Users enters OTP, if OTP is valid,
the user can be authenticated.

### Essential Operation Confirmation (Authorization)
If there'a need to change password, restore password or confirm purchase or payment, site calls Passwordless service
to be sure that exactly the user performs this critical operation.

## Customize Settings

Adjust settings in [application.yml](./src/main/resources/application.yml) in the `otp/settings` section
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
where `/sms/` - otp settings ID from `.yaml` settings file
Sample response:
```
{"operationId":"993e61be-23cf-412d-8273-f02e316e8689"}
```

Validate OTP with `operationId`:
```
curl -X POST -d '{"operationId": "993e61be-23cf-412d-8273-f02e316e8689", "otp": "123456"}'  -H "Content-Type: application/json" 'http://localhost:8080/otp/v1/verify'
```
Sample response:
```
{"verified":false}
```

# Using Time-Based One Time Password Authentication (TOTP)

TOTP authentication can be used as a 2 factor during the authentication process.

## Prerequisites
To use TOTP authentication the user should have an app such as Google Authenticator or Microsoft Authenticator installed on his device.

## Registration
To register a user or update user registration, send a POST request with the username
```
curl -X POST -d '{"username": "johndoe"}'  -H "Content-Type: application/json" 'http://localhost:8080/totp/v1/register'
```

The service will respond back TOTP URI and QR code image in Base64 format to scan in authenticator mobile application

```json
{"uri":"otpauth://totp/Acme+LLC:johndoe@acme.com?secret=5HFDYTGWPC3T72CCWDDXM7SY33ITKLC3&issuer=Acme+LLC","qr":"data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAACQElEQVR4Xu2WQW7sIBBE2yuOwU3B3NRHyJIVpKqwJzETKdl8fblFy/Iw8FhQXd3G+l/iw+aZH2NhUyxsioVNsbApFjbFv8CaMRKfuJttmKpm4eBs8IVxfADLeIe+awvG+VzyhB04fgoHpCjVcoUmmgHvErNYatwpSOxKvVcsc9iBlU6TJ58YXkeSFA0VzS34+27y52PGoAL3h7O+sDNqbKhoJJ2rLO1XuMEaM26ZXQskfH4avrConWH8i7ekGKuHZiJL2xvGEibT2bXs9WFyhunsXV/bRFlG7/rygBtMISBwyz7U6HhTIlcYck1BqInR2Ad+aPKrffnCcFckWWpsL9JuJneAKd08fgo0AINSHPmtnB+PmXGSxn6NyWyVtveEdbYs3RgD67eolk36ZK27weDq3nmJUsblhJH9GkW5wppKeFMVYxWe72AklCeMauhJ4bxHfR94wprGpdPtMvlpg2R33VxgOD4yDmWMvRp/kX2OR7jBBtmZ+tGZxVvc74I4wBrNzOMXfZJ2dWZKJH1cYYYeFYsE2bQqAJrcBXk+1k8RMB9LZWcmgMEVjjB4+9KEgOVxm1LT9oQ1VW7iE5usnsYd480hT8dGNKMOG6v7JLF9LLnBms6eKyZZzimwlvFO3OgLo5M1LxF24zeX40B9fGFSQEDnZyg2qSFZHGL5Wk3SoXFmSr0bjAqYbGBsWWfefWFcGlkuKGcagHWdL9IPZgwmHReMzv6c+da89rrBfo2FTbGwKRY2xcKmWNgU/wf7BJvt/ZLujX4VAAAAAElFTkSuQmCC"}
```

Scan the QR code in Authenticator app it will start generatig TOTP

## Authentication

To verify TOTP send a user's code from an authenticator mobile app in a POST request
```
curl -X POST -d '{"username": "johndoe", "totp": 879580 }'  -H "Content-Type: application/json" 'http://localhost:8080/totp/v1/verify'
```

The server will respond with verification result
```json
{"valid":true}
```

# Using Web Authentication (WebAuthn)

Passwordless service can be used to provide WebAuthn Registration and Login functions both on server using API and on client using JavaScript SDK.

## Prerequisites
Setup required origin in `webauthn-sample-settings.yaml` in `origin` setting.

And run Passwordless Service from docker compose

## Using Javascript SDK

Just add to your web application SDK script and initialize SDK:
```html
<script src="http://passwordless-service:8080/js/passwordless-sdk.js"></script>
<script>
    Passwordless.init({host: 'http://passwordless-service:8080'});
</script>
``` 
Full example is [here](./examples/jssdk)

### Registration

Just call
```javascript
Passwordless.webauthn.startRegistration(login);
```
where `login` - your username, and dialog asking you to insert USB Token will appear.
After successful registration SDK will return credenital Id value.


### Login
If your account already registered via startRegistration function and you want to authenticate, call
```javascript
Passwordless.webauthn.startLogin(login);
```

# Persistence

Passwordless service PostgresSQL and H2 databases.