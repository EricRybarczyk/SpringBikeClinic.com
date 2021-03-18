package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.services.BikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("account/bikes")
@RequiredArgsConstructor
@Slf4j
public class BikeController {

    private static final String MODEL_ATTRIBUTE_BIKE_LIST = "bikeList";
    private static final String MODEL_ATTRIBUTE_BIKE_DTO = "bikeDto";

    private final BikeService bikeService;

    @GetMapping
    public String accountBikes(Model model, Principal principal) {
        final Long userId = SecurityUser.from(principal).getUser().getId();
        List<BikeDto> bikes = bikeService.getBikes(userId);

        model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, bikes);
        model.addAttribute(MODEL_ATTRIBUTE_BIKE_DTO, new BikeDto());

        return "account/bikes";
    }

    @PostMapping("/save")
    public String saveOrUpdateBike(@ModelAttribute("bikeDto") @Valid BikeDto bikeDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account/bikes";
        }

        final Long userId = SecurityUser.from(principal).getUser().getId();

        bikeService.save(bikeDto, userId);

        return "redirect:/account/bikes";
    }

    @GetMapping("/edit/{bikeId}")
    public String editBike(@PathVariable("bikeId") Long bikeId, Model model, Principal principal) {
        // get the BikeDto - make sure it belongs to the current user
        final Long userId = SecurityUser.from(principal).getUser().getId();
        final BikeDto bikeDto = bikeService.getBikeForUser(bikeId, userId);

        model.addAttribute(MODEL_ATTRIBUTE_BIKE_DTO, bikeDto);

        List<BikeDto> bikes = bikeService.getBikes(userId);
        model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, bikes);

        return "account/bikes";
    }

    @GetMapping("/delete/{bikeId}")
    public String deleteBike(@PathVariable("bikeId") Long bikeId, Principal principal) {
        final Long userId = SecurityUser.from(principal).getUser().getId();

        bikeService.deleteBikeForUser(bikeId, userId);

        return "redirect:/account/bikes";
    }
}
