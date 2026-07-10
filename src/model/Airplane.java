package model;

public class Airplane {
	    private Flight flight;
	    private int actualDepartureMinutes; // may be delayed
	    private boolean active;

	    public Airplane(Flight f, int actualDepartureMinutes) {
	        this.flight = f;
	        this.actualDepartureMinutes = actualDepartureMinutes;
	        this.active = false;
	    }


		public Flight getFlight() { 
	    	return flight; 
	    }
	    public int getActualDepartureMinutes() {
	    	return actualDepartureMinutes; 
	    }
	 
	    public boolean isActive(int simMinutes) {
	        return simMinutes >=actualDepartureMinutes
	                && simMinutes < actualDepartureMinutes + flight.getDuration();
	    }
	    public boolean isLanded(int simMinutes) {
	        return simMinutes >= actualDepartureMinutes + flight.getDuration();
	    }

	    
	    public double getProgress(int simMinutes) {
	        if (!isActive(simMinutes)) return isLanded(simMinutes) ? 1.0 : 0.0;
	        return (double)(simMinutes - actualDepartureMinutes) / flight.getDuration();
	    }
}
