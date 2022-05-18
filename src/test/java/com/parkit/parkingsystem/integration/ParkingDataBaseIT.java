package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.constants.Fare;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import junit.framework.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability --> DONE

        Statement stmt = dataBaseTestConfig.getConnection().createStatement();
        String SQL = "SELECT * FROM test.ticket t\n" +
                "INNER JOIN test.parking p ON p.PARKING_NUMBER = t.PARKING_NUMBER";
        ResultSet rs = stmt.executeQuery(SQL);

        if (rs.next()) {
            System.out.println("Field IN_TIME:" + rs.getTimestamp(5));
            System.out.println("Field AVAILABLE:" + rs.getInt(8));
        }
        //Check ticket is saved in DB
        Assert.assertNotNull(rs.getTimestamp(5));

        //Check AVAILABLE = 0 in DB
        Assert.assertEquals(0, rs.getInt(8));
    }

    @Test
    public void testParkingLotExit() throws Exception {
        //testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //TODO: check that the fare generated and out time are populated correctly in the database --> DONE

        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2022-05-17 10:50:10.0");

        Connection conn = dataBaseTestConfig.getConnection();
        String SQL = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME) values(?,?,?,?)";
        PreparedStatement preparedStmt = conn.prepareStatement(SQL);

        preparedStmt.setInt(1, 2);
        preparedStmt.setString(2, "ABCDEF");
        preparedStmt.setDouble(3, 0);
        preparedStmt.setTimestamp(4, timestamp);

        preparedStmt.execute();
        conn.close();

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        parkingService.processExitingVehicle();


        //Check fare generated is populated correctly
        String vehicleRegNumber = "ABCDEF";
        Double price = ticketDAO.getTicket(vehicleRegNumber).getPrice();
        long outTime = ticketDAO.getTicket(vehicleRegNumber).getOutTime().getTime();
        long inTime = ticketDAO.getTicket(vehicleRegNumber).getInTime().getTime();
        Double fareCar = Fare.CAR_RATE_PER_MINUTES;
        Double fareCalculator = (outTime - inTime) / 60000 * fareCar;

        Assert.assertEquals(fareCalculator, price);
        System.out.println(fareCalculator);

        //Check outTime is not null
        Assert.assertNotNull(ticketDAO.getTicket(vehicleRegNumber).getOutTime());
        System.out.println("out-time: " + ticketDAO.getTicket(vehicleRegNumber).getOutTime());
    }

}