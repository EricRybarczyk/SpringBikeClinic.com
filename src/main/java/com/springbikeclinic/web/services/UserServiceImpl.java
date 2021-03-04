package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import com.springbikeclinic.web.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void updateUser(SecurityUser securityUser, CustomerAccountDto customerUpdateDto) {
        final User existingUser = userRepository.findById(securityUser.getUserId())
                .orElseThrow( () -> new UserNotFoundException("No user found for ID " + securityUser.getUserId()));

        // NOTE: this method does not update Authorities because it is meant for use by User on website and they can't change their own security Authorities

        existingUser.setFirstName(customerUpdateDto.getFirstName());
        existingUser.setLastName(customerUpdateDto.getLastName());

        // not updating email YET, until email verification process is in place, and that will likely be a separate flow, not using this method

        userRepository.save(existingUser);

        // update the Principal (which is the SecurityUser object) so Spring will see the changes. Otherwise the "dirty" Principal will still be active.
        securityUser.setFirstName(customerUpdateDto.getFirstName());
        securityUser.setLastName(customerUpdateDto.getLastName());
    }

}
