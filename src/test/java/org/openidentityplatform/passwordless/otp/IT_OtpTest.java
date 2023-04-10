package org.openidentityplatform.passwordless.otp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.openidentityplatform.passwordless.otp.repositories.SentOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IT_OtpTest {

    @LocalServerPort
    private int port;

    @Autowired
    SentOtpRepository sentOtpRepository;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/otp/v1";
        sentOtpRepository.deleteAll();
    }

    final static String SEND_REQUEST_BODY = """
            {
                "destination": "+7999999999",
                "sender": "sms"
            }
            """;

    final static String VERIFY_REQUEST_BODY_TEMPLATE = """
            {
                "sessionId": "%s",
                "otp": "%s"
            }
            """;

    @Test
    void otpSendValidate() throws Exception {
        ValidatableResponse response = given()
                .contentType(ContentType.JSON).body(SEND_REQUEST_BODY)
                .when()
                .post("/send")
                .then().log().all()
                .assertThat().statusCode(200)
                .body("sessionId", not(emptyString()));

        JsonPath jsonPath = response.extract().body().jsonPath();
        String sessionId = jsonPath.getString("sessionId");

        Optional<SentOtp> session = sentOtpRepository.findById(UUID.fromString(sessionId));
        assertFalse(session.isEmpty());

        given()
                .contentType(ContentType.JSON)
                .body(VERIFY_REQUEST_BODY_TEMPLATE.formatted(sessionId, session.get().getOtp()))
                .when().log().all()
                .post("/verify")
                .then().log().all()
                .assertThat().statusCode(200)
                .body("valid", equalTo(true));
    }

    @Test
    void testFrequentSend() {
        given()
                .contentType(ContentType.JSON).body(SEND_REQUEST_BODY)
                .when()
                .post("/send")
                .then().log().all()
                .assertThat().statusCode(200)
                .body("sessionId", not(emptyString()));

        //check frequent sending
        given()
                .contentType(ContentType.JSON).body(SEND_REQUEST_BODY)
                .when()
                .post("/send")
                .then().log().all()
                .assertThat().statusCode(400)
                .body("error", not(emptyString()));
    }
}
