package org.openidentityplatform.passwordless.totp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "registered_totps")
@Data
public class RegisteredTotp {
    @Id
    private String username;

    @Column
    private String secret; //TODO add encryption and decryption

}
