package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Airplane;
import model.Airport;
import model.Flight;

public class Data {
	public Data() {}
	
	private List<Airport> airports = new ArrayList<>();
	private List<Flight> flights = new ArrayList<>();
	private List<Airplane> airplanes = new ArrayList<>();
	
	 public void addAirport(Airport a) throws IllegalArgumentException {
	        for (Airport existing : airports) {
	            if (existing.getCode().equalsIgnoreCase(a.getCode())) {
	                throw new IllegalArgumentException(
	                    "Airport '" + a.getCode() + "' already exists.");
	            }
	        }
	        airports.add(a);
	    }
	 public Airport findAirport(String code) {
	        for (Airport a : airports) {
	            if (a.getCode().equalsIgnoreCase(code)) return a;
	        }
	        return null;
	    }
	 public List<Airport> getAirports() {
		
		 return airports;
		
	 }
	 public void clearAirports() { 
		 airports.clear(); 
     }
	public void addFlight(Flight f) throws IllegalArgumentException{
		
		flights.add(f);
	}
	public Flight findFlight(String from, String to) {
		
		for(Flight f:flights) {
			if(f.getFrom().equals(from)&& f.getTo().equals(to)) {
				return f;
			}
		} 
		return null;
	}
	public List<Flight> getFlights(){
		return flights;
	}
	
	 public void clearFlights() { 
		 flights.clear(); 
     }
	 
	public void buildAirplanes() {
	    airplanes.clear();

	    int[] nextSlot = new int[airports.size()];

	    List<Flight> sorted = new ArrayList<>(flights);
	    Collections.sort(sorted, (f1, f2) ->
	    Integer.compare(f1.getDepartureMin(), f2.getDepartureMin()));
	    for (Flight f : sorted) {

	        int index = airports.indexOf(f.getFrom());
	        int wanted = f.getDepartureMin();

	        if (wanted < nextSlot[index]) {
	            wanted = nextSlot[index];
	        }

	        nextSlot[index] = wanted + 10;
	       
	        airplanes.add(new Airplane(f, wanted));
	    }
	 }
	public List<Airplane> getAirplanes(){
		return airplanes;
	}
	 public void clearAirplanes() {
		 airplanes.clear(); 
     }
	public void clearAll() {
		clearAirports();
		clearFlights();
		clearAirplanes();
	}
}
