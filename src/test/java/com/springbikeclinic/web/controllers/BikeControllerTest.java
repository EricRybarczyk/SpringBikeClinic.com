package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.security.WithMockCustomUser;
import com.springbikeclinic.web.services.BikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BikeController.class)
@ExtendWith(MockitoExtension.class)
class BikeControllerTest {

    private static final String GET_BIKES_BASE_PATH = "/account/bikes";
    private static final String EXPECTED_ACCOUNT_BIKES_VIEW_NAME = "account/bikes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BikeService bikeService;

    @MockBean
    private BikeMapper bikeMapper;

    // not used directly, but required by Spring Security for mock user authentication
    @MockBean
    private UserDetailsManager userDetailsManager;


    @WithMockCustomUser
    @Test
    void getAccountBikes_asAuthenticatedUser_isOk() throws Exception {
        mockMvc.perform(get(GET_BIKES_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_ACCOUNT_BIKES_VIEW_NAME));
    }

    @Test
    void getAccountBikes_asAnonymousUser_isForbidden() throws Exception {
        mockMvc.perform(get(GET_BIKES_BASE_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPostNewBike_asAnonymousUser_isForbidden() throws Exception {
        mockMvc.perform(post(GET_BIKES_BASE_PATH + "/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("description", "bike")
                .param("bikeType", "MOUNTAIN")
                .param("manufacturerName", "Manufacturer")
                .param("modelName", "Model")
                .param("modelYear", "2020")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithMockCustomUser
    @Test
    void testPostNewBike_withValidInput_bikeIsSaved() throws Exception {
        Bike bike = new Bike();
        bike.setId(1L);
        when(bikeService.save(any(BikeDto.class), anyLong())).thenReturn(TestData.getExistingBikeDto());
        when(bikeMapper.bikeDtoToBike(any(BikeDto.class))).thenReturn(bike);

        mockMvc.perform(post(GET_BIKES_BASE_PATH + "/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("description", "bike")
                .param("bikeType", "MOUNTAIN")
                .param("manufacturerName", "Manufacturer")
                .param("modelName", "Model")
                .param("modelYear", "2020")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/bikes"));

        verify(bikeService, times(1)).save(any(BikeDto.class), anyLong());

    }

    @WithMockCustomUser
    @Test
    void testPostExistingBikeForUpdate_withValidInput_bikeIsSaved() throws Exception {
        // Note: from Controller perspective the only difference in this test is the addition of the "id" parameter to the POST

        Bike bike = new Bike();
        bike.setId(1L);
        when(bikeService.save(any(BikeDto.class), anyLong())).thenReturn(TestData.getExistingBikeDto());
        when(bikeMapper.bikeDtoToBike(any(BikeDto.class))).thenReturn(bike);

        mockMvc.perform(post(GET_BIKES_BASE_PATH + "/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("description", "bike")
                .param("bikeType", "MOUNTAIN")
                .param("manufacturerName", "Manufacturer")
                .param("modelName", "Model")
                .param("modelYear", "2020")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/bikes"));

        verify(bikeService, times(1)).save(any(BikeDto.class), anyLong());
    }

    @WithMockCustomUser
    @Test
    void testPostNewBike_withInvalidInput_bikeIsNotSaved() throws Exception {
        Bike bike = new Bike();
        bike.setId(1L);
        when(bikeService.save(any(BikeDto.class), anyLong())).thenReturn(TestData.getExistingBikeDto());
        when(bikeMapper.bikeDtoToBike(any(BikeDto.class))).thenReturn(bike);

        mockMvc.perform(post(GET_BIKES_BASE_PATH + "/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                // values are too short so they are invalid, but all fields are present like the real form post
                .param("description", "x")
                .param("bikeType", "OTHER")
                .param("manufacturerName", "x")
                .param("modelName", "x")
                .param("modelYear", "0")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("bikeDto","description","Size"))
                .andExpect(model().attributeHasFieldErrorCode("bikeDto","manufacturerName","Size"))
                .andExpect(model().attributeHasFieldErrorCode("bikeDto","modelName","Size"))
                .andExpect(model().attributeHasFieldErrorCode("bikeDto","modelYear","BikeModelYear"))
                .andExpect(model().attributeExists("bikeDto"))
                .andExpect(view().name(EXPECTED_ACCOUNT_BIKES_VIEW_NAME));

        verify(bikeService, times(0)).save(any(BikeDto.class), anyLong());

    }

    @WithMockCustomUser
    @Test
    void testPostEditBike_withInvalidBikeId_showsError404() throws Exception {
        when(bikeService.save(any(BikeDto.class), anyLong())).thenThrow(new NotFoundException("Bike not found"));

        mockMvc.perform(post(GET_BIKES_BASE_PATH + "/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("description", "bike")
                .param("bikeType", "MOUNTAIN")
                .param("manufacturerName", "Manufacturer")
                .param("modelName", "Model")
                .param("modelYear", "2020")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("errors/error404"));

        verify(bikeService, times(1)).save(any(BikeDto.class), anyLong());
    }

}
