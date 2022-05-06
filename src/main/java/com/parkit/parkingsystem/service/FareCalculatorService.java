package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private double FREETIMEHOUR = 0.5;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double durationHour = convertMillisToHour(outHour - inHour);
        durationHour = freeTime(durationHour, FREETIMEHOUR);

        double ratePerHour;
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	ratePerHour = Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
            	ratePerHour = Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        
        double price = Math.floor(durationHour*ratePerHour * 1000)/1000;
        ticket.setPrice(price);
    }
    
    private double convertMillisToHour(long timeMillis) {
    	return ((double) timeMillis/(1000*60*60));
    }
    
    private double freeTime(double durationHour, double timeHour) {
    	if (durationHour < timeHour) return 0;
    	else return durationHour;
    }
    
    public double getFreeTimeHour() {
    	return this.FREETIMEHOUR;
    }
    
    public void setFreeTimeHour(double time) {
    	this.FREETIMEHOUR = time;
    }
}