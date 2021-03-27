package com.springbikeclinic.web.domain.security;

import lombok.Getter;
import javax.persistence.*;
import java.time.LocalDateTime;


// adapted from https://www.baeldung.com/registration-verify-user-by-email


@Entity
@Getter
public class VerificationToken {

    private static final int EXPIRATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String token;

    private LocalDateTime expirationDateTime;

    public VerificationToken(User user, String token) {
        this();
        this.user = user;
        this.token = token;
    }

    public VerificationToken() {
        expirationDateTime = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
    }

}
