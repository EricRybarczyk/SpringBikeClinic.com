package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("account/bikes")
@RequiredArgsConstructor
@Slf4j
public class BikeController {

    private static final String MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT = "customerAccountDto";
    private static final String MODEL_ATTRIBUTE_BIKE_LIST = "bikeList";
    private static final String MODEL_ATTRIBUTE_BIKE_DTO = "bikeDto";

    @GetMapping
    public String accountBikes(Model model, Principal principal) {
        model.addAttribute(MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT, CustomerAccountDto.from(principal));

        // TODO: get bike list for current user
        List<BikeDto> bikes = new ArrayList<>();

        model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, bikes);
        model.addAttribute(MODEL_ATTRIBUTE_BIKE_DTO, new BikeDto());

        return "account/bikes";
    }
}
