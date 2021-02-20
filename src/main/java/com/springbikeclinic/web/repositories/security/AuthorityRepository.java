package com.springbikeclinic.web.repositories.security;

import com.springbikeclinic.web.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    //Optional<Authority> findByRole(String role);

}
