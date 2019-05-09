package org.openidentityplatform.passwordless.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Account {

    @Id
    private String id;

    private String token;

    private String userName;

    private String password;

}
