package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private double FREE_TIME_HOUR = 0.5;
	private double DISCOUNT_FOR_RECURRING = 0.05;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inMillis = ticket.getInTime().getTime();
        long outMillis = ticket.getOutTime().getTime();

        double durationHour = convertMillisToHour(outMillis - inMillis);
        durationHour = checkFreeTime(durationHour);

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
        
        double price = durationHour*ratePerHour;
        if(ticket.isRecurringUser()) price -= DISCOUNT_FOR_RECURRING*price;
        ticket.setPrice( roundCent(price) );
    }
    
    private double convertMillisToHour(long timeMillis) {
    	return ((double) timeMillis/(1000*60*60));
    }
    
    private double checkFreeTime(double durationHour) {
    	if (durationHour < FREE_TIME_HOUR) return 0;
    	else return durationHour;
    }
    
    public double getFreeTimeHour() {
    	return this.FREE_TIME_HOUR;
    }
    
    public void setFreeTimeHour(double time) {
    	this.FREE_TIME_HOUR = time;
    }
    
    static public double roundCent(double x) {
    	// les fractions de centimes sont offerts par la maison !!
    	return Math.floor(Math.round(x*10000)/100)/100;
    }
}