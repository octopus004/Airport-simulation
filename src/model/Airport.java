package model;

public class Airport {
	
	private String name;
	private String code;
	private double x;
	private double y;

	public Airport(String code,String name, double x, double y) {
		this.name = name;
		this.code = code;
		this.x = x;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}


}
