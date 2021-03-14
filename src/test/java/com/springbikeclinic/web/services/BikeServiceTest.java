package com.springbikeclinic.web.services;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.exceptions.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        Bike bike = TestData.getBike();
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);

        BikeDto bikeDto = TestData.getNewBikeDto();
        when(bikeMapper.bikeDtoToBike(any(BikeDto.class))).thenReturn(bike);
        when(bikeMapper.bikeToBikeDto(any(Bike.class))).thenReturn(bikeDto);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        final BikeDto result = bikeService.save(bikeDto, user.getId());

        assertThat(result).isNotNull();

        verify(userRepository, times(1)).findById(argumentCaptor.capture());
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(user.getId());

        verify(bikeRepository, times(1)).save(any(Bike.class));
        verify(bikeMapper, times(1)).bikeToBikeDto(any(Bike.class));
    }

    @Test
    void testUpdateExistingBike_bikeIsSavedAndReturned() throws Exception {
        Bike bike = TestData.getBike();
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        when(bikeRepository.findBikeByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(bike));

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        BikeDto bikeDto = TestData.getExistingBikeDto();
        when(bikeMapper.bikeToBikeDto(any(Bike.class))).thenReturn(bikeDto);

        final BikeDto result = bikeService.save(bikeDto, user.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bike.getId());

        verify(userRepository, times(1)).findById(argumentCaptor.capture());
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(user.getId());

        verify(bikeRepository, times(1)).save(any(Bike.class));
        verify(bikeMapper, times(1)).bikeToBikeDto(any(Bike.class));
    }

    @Test
    void testGetBikes_bikeListReturned() throws Exception {
        Set<Bike> bikeSet = new HashSet<>();
        Bike bike = new Bike();
        bike.setId(1L);
        bikeSet.add(bike);
        when(bikeRepository.findAllByUserId(anyLong(), any(Sort.class))).thenReturn(bikeSet);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        final Set<BikeDto> bikes = bikeService.getBikes(1L);
        assertThat(bikes).isNotNull();
        assertThat(bikes.size()).isEqualTo(1);

        verify(bikeRepository, times(1)).findAllByUserId(argumentCaptor.capture(), any(Sort.class));
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(1L);
    }

    @Test
    void testDeleteBike_validInput() throws Exception {
        Bike bike = TestData.getBike();
        when(bikeRepository.findBikeByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(bike));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        bikeService.deleteBikeForUser(bike.getId(), 1L);

        verify(bikeRepository, times(1)).findBikeByIdAndUserId(anyLong(), anyLong());

        verify(bikeRepository, times(1)).deleteById(argumentCaptor.capture());
        final Long captorValue = argumentCaptor.getValue();
        assertThat(captorValue).isEqualTo(bike.getId());
    }

    @Test
    void testDeleteBike_invalidInput_ThrowsNotFoundException() throws Exception {
        when(bikeRepository.findBikeByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bikeService.deleteBikeForUser(9999L, 1L));

        verify(bikeRepository, times(1)).findBikeByIdAndUserId(anyLong(), anyLong());
    }

}
