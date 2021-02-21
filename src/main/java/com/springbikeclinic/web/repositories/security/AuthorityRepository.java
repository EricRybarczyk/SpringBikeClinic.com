package com.springbikeclinic.web.repositories.security;

import com.springbikeclinic.web.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByRole(String role);

}
