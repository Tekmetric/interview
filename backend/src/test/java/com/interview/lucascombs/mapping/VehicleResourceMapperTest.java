package com.interview.lucascombs.mapping;

import com.interview.lucascombs.entity.Vehicle;
import com.interview.lucascombs.resource.VehicleResource;
import de.danielbechler.diff.ObjectDifferBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class VehicleResourceMapperTest {

    private static final Long ID = 14L;
    private static final Integer MILEAGE = 123456;
    private static final Integer YEAR = 2018;
    private static final String MAKE = "Honda";
    private static final String MODEL = "Civic";
    private static final String COLOR = "Victory Red";
    private static final String LICENSE_PLATE = "ABC123";
    private static final String VIN = "ABC-DEF-GHI";
    private static final String OWNER_NAME = "Steve";


    final VehicleResourceMapper mapper = Mappers.getMapper(VehicleResourceMapper.class);

    private Vehicle getExpectedEntity() {
        final Vehicle vehicle = new Vehicle();
        vehicle.setId(ID);
        vehicle.setMileage(MILEAGE);
        vehicle.setYear(YEAR);
        vehicle.setMake(MAKE);
        vehicle.setModel(MODEL);
        vehicle.setColor(COLOR);
        vehicle.setLicensePlate(LICENSE_PLATE);
        vehicle.setVin(VIN);
        vehicle.setOwnersName(OWNER_NAME);
        return vehicle;
    }

    private VehicleResource getExpectedResource() {
        final VehicleResource vehicleResource = new VehicleResource();
        vehicleResource.setVehicleId(ID.intValue());
        vehicleResource.setMileage(MILEAGE);
        vehicleResource.setYear(YEAR);
        vehicleResource.setMake(MAKE);
        vehicleResource.setModel(MODEL);
        vehicleResource.setColor(COLOR);
        vehicleResource.setLicensePlate(LICENSE_PLATE);
        vehicleResource.setVin(VIN);
        vehicleResource.setOwner(OWNER_NAME);
        return vehicleResource;
    }

    @Test
    void testEntityToResource() {
        final VehicleResource result = mapper.toResource(getExpectedEntity());
        assertFalse(ObjectDifferBuilder.buildDefault().compare(result, getExpectedResource()).hasChanges());
    }

    @Test
    void testResourceToEntity() {
        final Vehicle result = mapper.toEntity(getExpectedResource());
        final Vehicle expected = getExpectedEntity();

        expected.setId(null);

        assertFalse(ObjectDifferBuilder.buildDefault().compare(result, expected).hasChanges());
    }

    @Test
    void testUpdateEntity() {
        final VehicleResource resource = getExpectedResource();
        final Vehicle entity = new Vehicle();

        mapper.updateEntity(resource, entity);

        final Vehicle expected = getExpectedEntity();
        assertNull(entity.getId(), "ID should be null from update");
        expected.setId(null);
        assertFalse(ObjectDifferBuilder.buildDefault().compare(entity, expected).hasChanges());
    }
}
