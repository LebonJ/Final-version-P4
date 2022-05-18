package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    private final TicketDAO ticketDAO;

    public FareCalculatorService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double durationMinutes = TimeUnit.MINUTES.convert(ticket.getOutTime().getTime() - ticket.getInTime().getTime(), TimeUnit.MILLISECONDS);
        if (durationMinutes < Fare.FREE_PARK_PER_MINUTE) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(durationMinutes * Fare.CAR_RATE_PER_MINUTES);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(durationMinutes * Fare.BIKE_RATE_PER_MINUTES);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
            System.out.println (userRecurrent(ticket.getVehicleRegNumber()));
            if (userRecurrent(ticket.getVehicleRegNumber())) {
                ticket.setPrice(ticket.getPrice() * Fare.RECURRENT_USER_PRICE);
            }
        }
    }

    public boolean userRecurrent(String vehicleRegNumber) {
        return ticketDAO.userRecurrent(vehicleRegNumber);
    }
}
