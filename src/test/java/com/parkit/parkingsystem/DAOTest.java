package com.parkit.parkingsystem;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class DAOTest {
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static final Logger logger = LogManager.getLogger("DAOTest");
    Connection con = null;

    @BeforeEach
    public void setUp() {
        try {
            con = dataBaseTestConfig.getConnection();
        } catch (Exception ex) {
            logger.error("Error connecting to data base", ex);

        }
    }

    @AfterEach
    public void tearDown() {
        dataBaseTestConfig.closeConnection(con);
    }

    @Test
    public void getNextAvailableSlotTest() {

        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        int parkkedId = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(1, parkkedId);


    }

    @Test
    public void updateParkingTest() {
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        ParkingSpot parkingSpot = mock(ParkingSpot.class);
        parkingSpot.setAvailable(true);
        parkingSpot.setId(1);
        parkingSpotDAO.updateParking(parkingSpot);
        verify(parkingSpot, times(1)).isAvailable();
    }

    @Test
    public void updateParkingTestFailour() {
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        ParkingSpot parkingSpot = mock(ParkingSpot.class);
        assertFalse(parkingSpotDAO.updateParking(parkingSpot));

    }
}