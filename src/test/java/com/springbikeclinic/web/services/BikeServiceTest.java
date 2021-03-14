package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.BikeType;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.repositories.BikeRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeServiceTest {

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BikeMapper bikeMapper;

    @InjectMocks
    private BikeServiceImpl bikeService;


    @Test
    void testSaveNewBike_bikeIsSavedAndReturned() throws Exception {
        Bike bike = getBike();
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);

        BikeDto bikeDto = getNewBikeDto();
        when(bikeMapper.bikeDtoToBike(any(BikeDto.class))).thenReturn(bike);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        final Bike result = bikeService.save(bikeDto, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bike.getId());

        verify(userRepository, times(1)).findById(argumentCaptor.capture());
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(user.getId());

        verify(bikeMapper, times(1)).bikeDtoToBike(any(BikeDto.class));
        verify(bikeRepository, times(1)).save(any(Bike.class));
    }

    @Test
    void testUpdateExistingBike_bikeIsSavedAndReturned() throws Exception {
        Bike bike = getBike();
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        when(bikeRepository.findBikeByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(bike));

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        BikeDto bikeDto = getExistingBikeDto();

        final Bike result = bikeService.save(bikeDto, user.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bike.getId());

        verify(userRepository, times(1)).findById(argumentCaptor.capture());
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(user.getId());

        verify(bikeRepository, times(1)).save(any(Bike.class));
        verifyNoInteractions(bikeMapper);
    }

    @Test
    void testGetBikes_bikeListReturned() throws Exception {
        Set<Bike> bikeSet = new HashSet<>();
        Bike bike = new Bike();
        bike.setId(1L);
        bikeSet.add(bike);
        when(bikeRepository.findAllByUserId(anyLong(), any(Sort.class))).thenReturn(bikeSet);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        final Set<Bike> bikes = bikeService.getBikes(1L);
        assertThat(bikes).isNotNull();
        assertThat(bikes.size()).isEqualTo(1);

        verify(bikeRepository, times(1)).findAllByUserId(argumentCaptor.capture(), any(Sort.class));
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(1L);
    }

    private Bike getBike() {
        Bike bike = new Bike();
        bike.setId(1L);
        bike.setBikeType(BikeType.MOUNTAIN);
        bike.setDescription("description");
        bike.setManufacturerName("manufacturer");
        bike.setModelName("model");
        bike.setModelYear(2020);
        return bike;
    }

    private BikeDto getNewBikeDto() {
        return BikeDto.builder()
                .bikeType(BikeType.MOUNTAIN)
                .description("bike")
                .manufacturerName("manufacturer")
                .modelName("model")
                .modelYear(2020)
                .build();
    }

    private BikeDto getExistingBikeDto() {
        return BikeDto.builder()
                .id(1L)
                .bikeType(BikeType.MOUNTAIN)
                .description("bike")
                .manufacturerName("manufacturer")
                .modelName("model")
                .modelYear(2020)
                .build();
    }


}
