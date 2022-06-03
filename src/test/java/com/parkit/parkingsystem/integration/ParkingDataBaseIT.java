package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
    	int prevNbRowsTicketTable = nRowsTicketTable();
    	int prevNbFalseAvailable = nAvailableParkingTable(false);
    	int prevNbTrueAvailable = nAvailableParkingTable(true);
    	
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        assertEquals(1 + prevNbRowsTicketTable, nRowsTicketTable());
        assertEquals("ABCDEF", firstRegistrationNumberInDB());
        assertEquals(1 + prevNbFalseAvailable, nAvailableParkingTable(false));
        assertEquals(prevNbTrueAvailable - 1, nAvailableParkingTable(true));
    }

	@Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
        	dataBaseTestConfig.closeConnection(con);
        }
		return result;
	}
    
	private String firstRegistrationNumberInDB() {
        String result = "";
    	String query = "select VEHICLE_REG_NUMBER from ticket";
        Connection con = null;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getString("VEHICLE_REG_NUMBER");
            }
            dataBaseTestConfig.closeResultSet(rs);
            dataBaseTestConfig.closePreparedStatement(ps);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
        	dataBaseTestConfig.closeConnection(con);
        }
		return result;
	}

	private int nAvailableParkingTable(boolean wantedAvailable) {
		int result = 0;
    	String query = "select AVAILABLE from parking";
        Connection con = null;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
            	if(rs.getBoolean("AVAILABLE") == wantedAvailable) {
            		result++;
            	}
            }
            dataBaseTestConfig.closeResultSet(rs);
            dataBaseTestConfig.closePreparedStatement(ps);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
        	dataBaseTestConfig.closeConnection(con);
        }
		return result;
    }
}
