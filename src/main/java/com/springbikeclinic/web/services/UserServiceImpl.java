package com.springbikeclinic.web.services;

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
    public void updateUser(Long id, CustomerAccountDto customerUpdateDto) {
        final User existingUser = userRepository.findById(id)
                .orElseThrow( () -> new UserNotFoundException("No user found for ID " + id));

        // NOTE: this method does not update Authorities because it is meant for use by User on website and they can't change their own security Authorities

        existingUser.setFirstName(customerUpdateDto.getFirstName());
        existingUser.setLastName(customerUpdateDto.getLastName());

        // not updating email YET, until email verification process is in place, and that will likely be a separate flow, not using this method

        userRepository.save(existingUser);
    }

}
