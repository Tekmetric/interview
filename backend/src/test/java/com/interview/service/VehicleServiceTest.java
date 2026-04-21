package com.interview.service;

import com.interview.dto.VehicleFilter;
import com.interview.dto.VehiclePatchRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;
import com.interview.exception.VehicleNotFoundException;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;
    @InjectMocks
    private VehicleService service;

    private static Vehicle sample() {
        return new Vehicle("1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000L);
    }

    @Test
    void getThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(VehicleNotFoundException.class);
    }

    @Test
    void updateOnlyUpdatesProvidedFields() {
        Vehicle v = sample();
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        when(repository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        VehiclePatchRequest req = new VehiclePatchRequest(null, null, null, null, null, 50_000L);
        VehicleResponse resp = service.update(1L, req);

        assertThat(resp.mileage()).isEqualTo(50_000L);
        assertThat(resp.make()).isEqualTo("Honda"); // untouched
        assertThat(resp.model()).isEqualTo("Accord"); // untouched
        assertThat(resp.year()).isEqualTo(2021); // untouched
    }

    @Test
    void updateAllFieldsNullLeavesEntityUnchanged() {
        Vehicle v = sample();
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        when(repository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        service.update(1L, new VehiclePatchRequest(null, null, null, null, null, null));

        assertThat(v.getVin()).isEqualTo("1HGCM82633A004352");
        assertThat(v.getMake()).isEqualTo("Honda");
    }

    @Test
    void deleteIsResponseIdempotent() {
        // Whether or not the row exists, delete returns normally and calls
        // deleteById. Clients retrying after a lost response always see 204.
        service.delete(7L);
        verify(repository, times(1)).deleteById(7L);
    }

    @Test
    void createPersistsAndReturnsResponse() {
        VehicleRequest req = new VehicleRequest("1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000L);
        when(repository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        VehicleResponse resp = service.create(req);

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getVin()).isEqualTo("1HGCM82633A004352");
        assertThat(resp.mileage()).isEqualTo(42000L);
    }

    @Test
    void searchComposesSpecificationFromFilter() {
        Vehicle v = sample();
        Page<Vehicle> page = new PageImpl<>(List.of(v));
        when(repository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<VehicleResponse> result = service.search(
                new VehicleFilter("Honda", null, null), PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).make()).isEqualTo("Honda");
    }
}
