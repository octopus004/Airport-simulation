package model;

public class Flight {

	private Airport from;
	private Airport to;
	private int duration;
	private int departure;
	
	public Flight(Airport from,  Airport to, int departure, int duration) {
		this.from = from;
		this.to = to;
		this.duration = duration;
		this.departure = departure;
	}
	
	public Airport getFrom() {
		return from;
	}
	
	public Airport getTo() {
		return to;
	}
	
	public int getDuration() {
		return duration; //in min
	}
	
	public int getDepartureMin() {
		return departure;
	}
	public String getDepartureString() {
		return departure/60  + " : " + departure%60;
	}
}
