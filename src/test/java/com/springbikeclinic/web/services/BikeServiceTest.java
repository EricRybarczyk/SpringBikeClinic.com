package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.security.User;
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

    @InjectMocks
    private BikeServiceImpl bikeService;


    @Test
    void testSaveBike_bikeIsSavedAndReturned() throws Exception {
        Bike bike = new Bike();
        bike.setId(1L);
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final Bike savedBike = bikeService.save(bike, 1L);

        assertThat(savedBike).isNotNull();
        assertThat(savedBike.getId()).isEqualTo(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(bikeRepository, times(1)).save(any(Bike.class));
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

}
