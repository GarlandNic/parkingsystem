package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

	@Test
	void testSaveTicket() {
		// GIVEN
    	int prevNbRowsTicketTable = nRowsTicketTable();
		Ticket ticket = new Ticket();
		ticket.setId(1);
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber("ABCDE");
        ticket.setRecurringUser(false);
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);
		
		// WHEN
		boolean saved = ticketDAO.saveTicket(ticket);
		
		// THEN
		assertTrue(saved);
        assertEquals(1 + prevNbRowsTicketTable, nRowsTicketTable()); // one more ticket
	}

	@Test
	void testGetTicket() {
		// GIVEN
		Ticket savedTicket = new Ticket();
		savedTicket.setId(1);
        savedTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        savedTicket.setVehicleRegNumber("ABCDE");
        savedTicket.setRecurringUser(false);
        savedTicket.setPrice(0);
        savedTicket.setInTime(new Date());
        savedTicket.setOutTime(null);
		ticketDAO.saveTicket(savedTicket);
		
		// WHEN
		Ticket ticket = ticketDAO.getTicket("ABCDE");
		
		// THEN
		assertEquals(savedTicket.getId(), ticket.getId());
		assertEquals((long) Math.round((double)savedTicket.getInTime().getTime()/1000), ticket.getInTime().getTime()/1000);
		assertNull(ticket.getOutTime());
		assertEquals(savedTicket.getParkingSpot(), ticket.getParkingSpot());
		assertEquals(savedTicket.getPrice(), ticket.getPrice());
		assertEquals(savedTicket.isRecurringUser(), ticket.isRecurringUser());
		assertEquals(savedTicket.getVehicleRegNumber(), ticket.getVehicleRegNumber());
	}

	@Test
	void testUpdateTicket() {
		// GIVEN
		Ticket savedTicket = new Ticket();
		savedTicket.setId(1);
        savedTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        savedTicket.setVehicleRegNumber("ABCDE");
        savedTicket.setRecurringUser(false);
        savedTicket.setPrice(0);
        savedTicket.setInTime(new Date());
        savedTicket.setOutTime(null);
		ticketDAO.saveTicket(savedTicket);
		
		Ticket ticket = ticketDAO.getTicket("ABCDE");
        ticket.setPrice(50);
        ticket.setOutTime(new Date());

		// WHEN
		boolean updated = ticketDAO.updateTicket(ticket);
		
		// THEN
		assertTrue(updated);
		Ticket updatedTicket = ticketDAO.getTicket("ABCDE");
		assertEquals((long) Math.round((double)ticket.getOutTime().getTime()/1000), updatedTicket.getOutTime().getTime()/1000);
		assertEquals(ticket.getPrice(), updatedTicket.getPrice());
	}

	@Test
	void testSearchLastExit() {
		// GIVEN
		Ticket savedTicket = new Ticket();
		savedTicket.setId(1);
        savedTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        savedTicket.setVehicleRegNumber("ABCDE");
        savedTicket.setRecurringUser(false);
        savedTicket.setPrice(0);
        savedTicket.setInTime(new Date());
        savedTicket.setOutTime(null);
		ticketDAO.saveTicket(savedTicket);
		
		Ticket ticket = ticketDAO.getTicket("ABCDE");
        ticket.setPrice(50);
        Date exit = new Date();
        ticket.setOutTime(exit);
        ticketDAO.updateTicket(ticket);
		
		// WHEN
		Date exiting = ticketDAO.searchLastExit("ABCDE");
		
		// THEN
		assertEquals((long) Math.round((double)exit.getTime()/1000), exiting.getTime()/1000);
	}
	

    private int nRowsTicketTable() {
        int result = 0;
    	String query = "select * from ticket";
        Connection con = null;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result++;
            }
            dataBaseTestConfig.closeResultSet(rs);
            dataBaseTestConfig.closePreparedStatement(ps);
		} catch (ClassNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}finally {
        	dataBaseTestConfig.closeConnection(con);
        }
		return result;
	}

}
