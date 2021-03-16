package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.repositories.BikeRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeServiceImpl implements BikeService {

    private final BikeRepository bikeRepository;
    private final BikeMapper bikeMapper;
    private final UserRepository userRepository;

    @Override
    public List<BikeDto> getBikes(Long userId) {
        final List<Bike> bikes = bikeRepository.findAllByUserId(userId);

        log.debug("-------> Found {} bikes for user ID {}", bikes.size(), userId);
        bikes.forEach(b -> log.debug("-------> Bike ID: {}", b.getId()));

        return bikes.stream()
                .map(bikeMapper::bikeToBikeDto)
                .collect(Collectors.toList());
    }

    @Override
    public BikeDto getBikeForUser(Long bikeId, Long userId) {
        return bikeMapper.bikeToBikeDto(
                bikeRepository.findBikeByIdAndUserId(bikeId, userId)
                        .orElseThrow(() -> new NotFoundException("Requested bike was not found")));
    }

    @Override
    public BikeDto save(BikeDto bikeDto, Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User account was not found"));

        if (bikeDto.isNew()) {
            log.debug("-------> Save new bike for user");
            final Bike bike = bikeMapper.bikeDtoToBike(bikeDto);
            user.getBikes().add(bike);
            bike.setUser(user);
            return bikeMapper.bikeToBikeDto(bikeRepository.save(bike));

        } else {
            Bike bike = bikeRepository.findBikeByIdAndUserId(bikeDto.getId(), userId)
                    .orElseThrow(() -> new NotFoundException("Requested bike was not found"));
            log.debug("-------> Update existing bike for user");

            // existing bike already related to User so we only need to update field values, no change to User-Bike relationships

            bike.setDescription(bikeDto.getDescription());
            bike.setBikeType(bikeDto.getBikeType());
            bike.setManufacturerName(bikeDto.getManufacturerName());
            bike.setModelName(bikeDto.getModelName());
            bike.setModelYear(bikeDto.getModelYear());

            return bikeMapper.bikeToBikeDto(bikeRepository.save(bike));
        }
    }

    @Override
    public void deleteBikeForUser(Long bikeId, Long userId) {
        // get the Bike first to make sure it belongs to the specified user
        Bike bikeForUser = bikeRepository.findBikeByIdAndUserId(bikeId, userId)
                .orElseThrow(() -> new NotFoundException("Requested bike was not found"));
        bikeRepository.deleteById(bikeForUser.getId());
    }

}
