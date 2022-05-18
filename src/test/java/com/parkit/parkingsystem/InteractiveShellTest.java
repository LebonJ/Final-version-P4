package com.parkit.parkingsystem;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.service.InteractiveShell;

public class InteractiveShellTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void loadInterfaceOption3Test() {

        InputStream sysInBackup = System.in; // backup System.in to restore it later
        ByteArrayInputStream in = new ByteArrayInputStream("3".getBytes());
        System.setIn(in);

        InteractiveShell.loadInterface();
        System.setIn(sysInBackup);

        assertTrue(outputStreamCaptor.toString().contains("Welcome to Parking System!"));
        assertTrue(outputStreamCaptor.toString().contains("Please select an option. Simply enter the number to choose an action"));
        assertTrue(outputStreamCaptor.toString().contains("1 New Vehicle Entering - Allocate Parking Space"));
        assertTrue(outputStreamCaptor.toString().contains("2 Vehicle Exiting - Generate Ticket Price"));
        assertTrue(outputStreamCaptor.toString().contains("3 Shutdown System"));
        assertTrue(outputStreamCaptor.toString().contains("Exiting from the system!"));

    }

}