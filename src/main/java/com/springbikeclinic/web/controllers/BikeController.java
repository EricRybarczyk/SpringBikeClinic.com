package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.services.BikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("account/bikes")
@RequiredArgsConstructor
@Slf4j
public class BikeController {

    private static final String MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT = "customerAccountDto";
    private static final String MODEL_ATTRIBUTE_BIKE_LIST = "bikeList";
    private static final String MODEL_ATTRIBUTE_BIKE_DTO = "bikeDto";

    private final BikeService bikeService;
    private final BikeMapper bikeMapper;

    @GetMapping
    public String accountBikes(Model model, Principal principal) {
        model.addAttribute(MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT, CustomerAccountDto.from(principal));

        final Long userId = SecurityUser.from(principal).getUser().getId();

        List<BikeDto> bikes = bikeService.getBikes(userId)
                .stream()
                .map(bikeMapper::bikeToBikeDto)
                .collect(Collectors.toList());

        model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, bikes);
        model.addAttribute(MODEL_ATTRIBUTE_BIKE_DTO, new BikeDto());

        return "account/bikes";
    }

    @PostMapping("/create")
    public String saveBike(@ModelAttribute("bikeDto") @Valid BikeDto bikeDto, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account/bikes";
        }

        final Long userId = SecurityUser.from(principal).getUser().getId();
        final Bike bike = bikeMapper.bikeDtoToBike(bikeDto);

        bikeService.save(bike, userId);

        return "redirect:/account/bikes";
    }
}
