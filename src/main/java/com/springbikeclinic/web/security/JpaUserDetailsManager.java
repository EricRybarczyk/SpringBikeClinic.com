package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.Authority;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.repositories.security.AuthorityRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        Authority authorityCustomer = authorityRepository.findByRole("CUSTOMER").orElseThrow(() -> new RuntimeException("Missing Role: CUSTOMER"));
        SecurityUser securityUser = (SecurityUser) user;
        userRepository.save(User.builder()
                .firstName(securityUser.getFirstName())
                .lastName(securityUser.getLastName())
                .email(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .authority(authorityCustomer)
                .build());
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found"));

        log.debug("User retrieved from JPA: Username: {} with authorities count: {}", user.getEmail(), user.getAuthorities().size());

        return new SecurityUser(user);
    }

}
