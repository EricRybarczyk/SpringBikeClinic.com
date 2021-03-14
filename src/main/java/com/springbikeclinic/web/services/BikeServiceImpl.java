package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.repositories.BikeRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeServiceImpl implements BikeService {

    private final BikeRepository bikeRepository;
    private final BikeMapper bikeMapper;
    private final UserRepository userRepository;

    @Override
    public Set<Bike> getBikes(Long userId) {
        final Set<Bike> bikes = bikeRepository.findAllByUserId(userId, Sort.by(Sort.Order.asc("id")));

        log.debug("-------> Found {} bikes for user ID {}", bikes.size(), userId);
        bikes.forEach(b -> log.debug("-------> Bike ID: {}", b.getId()));

        return bikes;
    }

    @Override
    public Bike getBikeForUser(Long bikeId, Long userId) {
        return bikeRepository.findBikeByIdAndUserId(bikeId, userId).orElseThrow(); // TODO: ControllerAdvice and NotFoundException
    }

    @Override
    public Bike save(BikeDto bikeDto, Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(); // TODO: ControllerAdvice and NotFoundException

        if (bikeDto.isNew()) {
            log.debug("-------> Save new bike for user");
            final Bike bike = bikeMapper.bikeDtoToBike(bikeDto);
            user.getBikes().add(bike);
            bike.setUser(user);
            return bikeRepository.save(bike);

        } else {
            Bike bike = bikeRepository.findBikeByIdAndUserId(bikeDto.getId(), userId).orElseThrow(); // TODO: ControllerAdvice and NotFoundException
            log.debug("-------> Update existing bike for user");

            // existing bike already related to User so we only need to update field values, no change to User-Bike relationships

            bike.setDescription(bikeDto.getDescription());
            bike.setBikeType(bikeDto.getBikeType());
            bike.setManufacturerName(bikeDto.getManufacturerName());
            bike.setModelName(bikeDto.getModelName());
            bike.setModelYear(bikeDto.getModelYear());

            return bikeRepository.save(bike);
        }
    }

}
