package com.springbikeclinic.web.domain.security;

import lombok.Getter;
import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
public class PasswordResetToken {

    private static final int EXPIRATION_MINUTES = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String token;

    private LocalDateTime expirationDateTime;

    public PasswordResetToken(User user, String token) {
        this();
        this.user = user;
        this.token = token;
    }

    public PasswordResetToken() {
        this.expirationDateTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }

}
