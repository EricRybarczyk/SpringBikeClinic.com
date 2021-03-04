package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import com.springbikeclinic.web.repositories.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    void testUpdateUser_withValidInput_userSavedToRepository() throws Exception {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        final User user = getUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final CustomerAccountDto customerAccountDto = getCustomerAccountDto();
        SecurityUser principal = new SecurityUser(user);
        userService.updateUser(principal, customerAccountDto);

        verify(userRepository, times(1)).save(argumentCaptor.capture());
        final User captorValue = argumentCaptor.getValue();

        // verify values passed to repository
        assertThat(captorValue.getFirstName()).isEqualTo(customerAccountDto.getFirstName());
        assertThat(captorValue.getLastName()).isEqualTo(customerAccountDto.getLastName());

        // verify the Principal was also updated
        assertThat(principal.getFirstName()).isEqualTo(customerAccountDto.getFirstName());
        assertThat(principal.getLastName()).isEqualTo(customerAccountDto.getLastName());

        // not verifying email because the method under test does not update email
    }

    @Test
    void testUpdateUser_userNotFound_shouldThrowUserNotFoundException() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy( () -> {
           userService.updateUser(new SecurityUser(getUser()), getCustomerAccountDto());
        }).isInstanceOf(UserNotFoundException.class);

        verifyNoMoreInteractions(userRepository);
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .firstName("First")
                .lastName("Last")
                .email("a@b.com")
                .build();
    }

    private CustomerAccountDto getCustomerAccountDto() {
        return CustomerAccountDto.builder()
                .firstName("First")
                .lastName("Last")
                .email("a@b.com")
                .build();
    }

}
