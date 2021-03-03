package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.dto.CreateAccountDto;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import com.springbikeclinic.web.security.StandAloneAuthenticator;
import com.springbikeclinic.web.security.WithMockCustomUser;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;

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
    private static final String EXPECTED_ACCOUNT_BIKES_VIEW_NAME = "account/bikes";
    private static final String EXPECTED_ACCOUNT_HISTORY_NAME = "account/history";
    private static final String POST_CREATE_ACCOUNT_PATH = "/account/create";
    private static final String EXPECTED_CREATE_ACCOUNT_RESULT_VIEW_NAME = "redirect:/account";
    private static final String POST_UPDATE_ACCOUNT_PATH = "/account/update";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsManager userDetailsManager;

    @MockBean
    private StandAloneAuthenticator standAloneAuthenticator;

    @MockBean
    private UserService userService;


    @Nested
    @DisplayName("Create Account")
    class CreateAccountTests {

        @Test
        void postCreateAccount_withValidInput_accountIsCreated() throws Exception {
            when(userDetailsManager.userExists(any(String.class))).thenReturn(false);

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
            verify(standAloneAuthenticator, times(1)).authenticateUser(any(), anyString(), anyString());
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
            verifyNoInteractions(standAloneAuthenticator);
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
            verifyNoInteractions(standAloneAuthenticator);
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

            verify(userService, times(1)).updateUser(anyLong(), argumentCaptor.capture());
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

        @WithMockCustomUser
        @Test
        void getAccountBikes_asAuthenticatedUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/bikes"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_ACCOUNT_BIKES_VIEW_NAME));
        }

        @WithMockCustomUser
        @Test
        void getAccountHistory_asAuthenticatedUser_isOk() throws Exception {
            mockMvc.perform(get(GET_ACCOUNT_BASE_PATH + "/history"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(EXPECTED_ACCOUNT_HISTORY_NAME));
        }
    }

}
