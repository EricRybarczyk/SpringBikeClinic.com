package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.*;
import com.springbikeclinic.web.dto.CreateAccountDto;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import com.springbikeclinic.web.security.*;
import com.springbikeclinic.web.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private static final String GET_ACCOUNT_BASE_PATH = "/account";
    private static final String EXPECTED_ACCOUNT_VIEW_NAME = "account";
    private static final String EXPECTED_ACCOUNT_DETAILS_VIEW_NAME = "account/details";
    private static final String POST_CREATE_ACCOUNT_PATH = "/account/create";
    private static final String EXPECTED_CREATE_ACCOUNT_RESULT_VIEW_NAME = "redirect:/account/pending";
    private static final String POST_UPDATE_ACCOUNT_PATH = "/account/update";
    private static final String EXPECTED_PASSWORD_RESET_VIEW_NAME = "account/reset";
    private static final String EXPECTED_PASSWORD_RESET_PENDING_VIEW_NAME = "account/resetPending";
    private static final String EXPECTED_PASSWORD_RESET_FORM_VIEW_NAME = "account/passwordReset";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsManager userDetailsManager;

    @MockBean
    private UserService userService;

    @MockBean
    private UserVerificationService userVerificationService;

    @MockBean
    private PasswordResetService passwordResetService;

    // Spring limitation, we can't actually verify against this ApplicationEventPublisher mock, at least as a @MockBean
    // Source: https://github.com/spring-projects/spring-framework/issues/18907 and https://github.com/spring-projects/spring-boot/issues/6060
    // Work-around: verify against RegistrationListener instead, so verify the event is received instead of verifying the event is sent.
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private RegistrationListener registrationListener;


    @Nested
    @DisplayName("Create Account")
    class CreateAccountTests {

        @Test
        void postCreateAccount_withValidInput_accountIsCreated() throws Exception {
            when(userDetailsManager.userExists(any(String.class))).thenReturn(false);
            when(userDetailsManager.loadUserByUsername(any(String.class))).thenReturn(getMockCreatedUser());

            final CreateAccountDto createAccountDto = getCreateAccountDto();

            mockMvc.perform(post(POST_CREATE_ACCOUNT_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    //.flashAttr("createAccountDto", getCreateAccountDto()) // another option for a @ModelAttribute param on controller method
                    .param("firstName", createAccountDto.getFirstName())
                    .param("lastName", createAccountDto.getLastName())
                    .param("email", createAccountDto.getEmail())
                    .param("createPassword", createAccountDto.getCreatePassword())
                    .param("confirmPassword", createAccountDto.getConfirmPassword())
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name(EXPECTED_CREATE_ACCOUNT_RESULT_VIEW_NAME));

            verify(userDetailsManager, times(1)).userExists(anyString());
            verify(userDetailsManager, times(1)).createUser(any(UserDetails.class));
            verify(userDetailsManager, times(1)).loadUserByUsername(any(String.class));
            verify(registrationListener, times(1)).onApplicationEvent(any(OnRegistrationCompleteEvent.class));
        }

        private SecurityUser getMockCreatedUser() {
            User user = User.builder()
                    .id(1L)
                    .firstName("first")
                    .lastName("last")
                    .email("a@b.com")
                    .authorities(List.of(Authority.builder().id(123L).role("unimportant").build()))
                    .build();
            return new SecurityUser(user);
        }

        @Test
        void postCreateAccount_withDuplicateUserEmail_accountNotCreated() throws Exception {
            when(userDetailsManager.userExists(any(String.class))).thenReturn(true);

            mockMvc.perform(post(POST_CREATE_ACCOUNT_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .flashAttr("createAccountDto", getCreateAccountDto())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("duplicateEmailError"))
                    .andExpect(view().name(EXPECTED_ACCOUNT_VIEW_NAME));

            verify(userDetailsManager, times(0)).createUser(any(UserDetails.class));
            verifyNoInteractions(registrationListener);
        }

        @Test
        void postCreateAccount_withInvalidInput_validationFailsAndNoAccountCreated() throws Exception {
            mockMvc.perform(post(POST_CREATE_ACCOUNT_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("firstName", "firsty")
                    .param("lastName", "")              // empty is not valid
                    .param("email", "not-email")        // not valid email format
                    .param("createPassword", "asdf")
                    .param("confirmPassword", "diff")   // passwords do not match
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("createAccountDto","lastName"))
                    .andExpect(model().attributeHasFieldErrors("createAccountDto","email"))
                    .andExpect(model().attributeHasFieldErrors("createAccountDto","confirmPassword"))
                    .andExpect(view().name(EXPECTED_ACCOUNT_VIEW_NAME));

            verifyNoInteractions(userDetailsManager);
            verifyNoInteractions(registrationListener);
        }

        private CreateAccountDto getCreateAccountDto() {
            return CreateAccountDto.builder()
                    .email("a@b.com")
                    .createPassword("password")
                    .confirmPassword("password")
                    .firstName("Firstname")
                    .lastName("Lastname")
                    .build();
        }

    }


    @Nested
    @DisplayName("Update Account")
    class UpdateAccountTests {

        @WithMockCustomUser
        @Test
        void postUpdateAccount_withValidInput_accountDetailsAreUpdated() throws Exception {
            final CustomerAccountDto customerAccountDto = getCustomerAccountDto();
            ArgumentCaptor<CustomerAccountDto> argumentCaptor = ArgumentCaptor.forClass(CustomerAccountDto.class);

            mockMvc.perform(post(POST_UPDATE_ACCOUNT_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("firstName", customerAccountDto.getFirstName())
                    .param("lastName", customerAccountDto.getLastName())
                    .param("email", customerAccountDto.getEmail())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_ACCOUNT_DETAILS_VIEW_NAME))
                    .andExpect(model().attributeExists("updateSuccessful"));

            verify(userService, times(1)).updateUser(any(SecurityUser.class), argumentCaptor.capture());
            final CustomerAccountDto captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getFirstName()).isEqualTo(customerAccountDto.getFirstName());
            assertThat(captorValue.getLastName()).isEqualTo(customerAccountDto.getLastName());
            assertThat(captorValue.getEmail()).isEqualTo(customerAccountDto.getEmail());
        }

        private CustomerAccountDto getCustomerAccountDto() {
            return CustomerAccountDto.builder()
                    .firstName("Firstname")
                    .lastName("Lastname")
                    .email("a@b.com")
                    .build();
        }
    }


    @Nested
    @DisplayName("Basic GET Requests")
    class BasicGetRequests {

        @WithMockCustomUser
        @Test
        void getAccount_asAuthenticatedUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_ACCOUNT_DETAILS_VIEW_NAME));
        }

        @Test
        void getAccount_asAnonymousUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_ACCOUNT_VIEW_NAME));
        }

        @WithMockCustomUser
        @Test
        void getAccountDetails_asAuthenticatedUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/details"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_ACCOUNT_DETAILS_VIEW_NAME));
        }

        @Test
        void getAccountDetails_asAnonymousUser_isUnauthorized() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/details"))
                    .andExpect(status().isUnauthorized());
        }

    }


    @Nested
    @DisplayName("Password Reset")
    class PasswordResetTests {

        @Test
        void getResetPassword_asAnonymousUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/reset"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("resetPasswordRequestDto"))
                    .andExpect(view().name(EXPECTED_PASSWORD_RESET_VIEW_NAME));
        }

        @WithMockCustomUser
        @Test
        void getResetPassword_asAuthenticated_isOk() throws Exception {
            // this is not exactly designed, but it is allowed and should work
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/reset"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("resetPasswordRequestDto"))
                    .andExpect(view().name(EXPECTED_PASSWORD_RESET_VIEW_NAME));
        }

        @Test
        void getPasswordResetForm_asAnonymousUser_isOk() throws Exception {
            when(passwordResetService.getPasswordResetToken(anyString())).thenReturn(new PasswordResetToken(new User(), "mock-token"));

            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/resetPassword?token=mock-token-value"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("resetPasswordResultDto"))
                    .andExpect(view().name(EXPECTED_PASSWORD_RESET_FORM_VIEW_NAME));
        }

        @WithMockCustomUser
        @Test
        void getPasswordResetForm_asAuthenticated_isOk() throws Exception {
            // this is not exactly designed, but it is allowed

            when(passwordResetService.getPasswordResetToken(anyString())).thenReturn(new PasswordResetToken(new User(), "mock-token"));

            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/resetPassword?token=mock-token-value"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("resetPasswordResultDto"))
                    .andExpect(view().name(EXPECTED_PASSWORD_RESET_FORM_VIEW_NAME));
        }

        @Test
        void getPendingPasswordReset_asAnonymousUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/reset/pending"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_PASSWORD_RESET_PENDING_VIEW_NAME));
        }

        @WithMockCustomUser
        @Test
        void getPendingPasswordReset_asAuthenticated_isOk() throws Exception {
            // this is not exactly designed, but it is allowed and should work
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/reset/pending"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_PASSWORD_RESET_PENDING_VIEW_NAME));
        }
    }


    @Nested
    @DisplayName("Account Verification")
    class AccountVerificationTests {

        @Test
        void testGetAccountPendingView() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/pending"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/pending"));
        }

        @Test
        void testGetAccountVerifiedView() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/verified"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/verified"));
        }

        @Test
        void testConfirmTokenRequest_withValidToken_accountVerifiedDisplayed() throws Exception {
            when(userVerificationService.verifyUser(anyString())).thenReturn(UserVerificationResult.SUCCESS);
            final String MOCK_TOKEN = "mock-token-value";

            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/confirmToken?token=" + MOCK_TOKEN))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/account/verified"));

            verify(userVerificationService, times(1)).verifyUser(MOCK_TOKEN);
        }

        @Test
        void testConfirmTokenRequest_alreadyVerified_accountVerifiedDisplayed() throws Exception {
            when(userVerificationService.verifyUser(anyString())).thenReturn(UserVerificationResult.UNNECESSARY);
            final String MOCK_TOKEN = "already-verified-token-value";

            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/confirmToken?token=" + MOCK_TOKEN))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/account/verified"));

            verify(userVerificationService, times(1)).verifyUser(MOCK_TOKEN);
        }

        @Test
        void testConfirmTokenRequest_withExpiredToken_tokenExpiredDisplayed() throws Exception {
            when(userVerificationService.verifyUser(anyString())).thenReturn(UserVerificationResult.EXPIRED);
            final String MOCK_TOKEN = "expired-token-value";

            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/confirmToken?token=" + MOCK_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/expired"));

            verify(userVerificationService, times(1)).verifyUser(MOCK_TOKEN);
        }

        @Test
        void testConfirmTokenRequest_withInvalidToken_tokenInvalidDisplayed() throws Exception {
            when(userVerificationService.verifyUser(anyString())).thenReturn(UserVerificationResult.INVALID);
            final String MOCK_TOKEN = "invalid-token-value";

            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/confirmToken?token=" + MOCK_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/invalid"));

            verify(userVerificationService, times(1)).verifyUser(MOCK_TOKEN);
        }

    }
}
