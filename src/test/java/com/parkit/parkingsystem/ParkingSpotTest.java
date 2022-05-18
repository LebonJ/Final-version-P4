package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotTest {


    @Test
    public void ParkingSpotTrueEquals() {
        ParkingSpot parkingSpotCar = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpotCar2 = new ParkingSpot(1, ParkingType.CAR, true);

        assertTrue(parkingSpotCar.equals(parkingSpotCar2));
    }

    @Test
    public void ParkingSpotFalseEquals() {
        ParkingSpot parkingSpotCar = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpotBike = new ParkingSpot(2, ParkingType.BIKE, true);

        assertFalse(parkingSpotCar.equals(parkingSpotBike));
    }

}