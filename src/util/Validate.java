package util;

public class Validate {

	public static String validateAirportCode(String code) {
		if(code == null) return "Airports code is mandatory";
		if(!code.equals(code.toUpperCase()))  return "Airport code must be uppercase";
		if(!code.matches("[A-Z]{3}")) return "Airport code must only be letters";
		return null;
	}
	
	public static String validateAirportName(String name) {
		if(name == null) return "Airports name is mandatory";
		return null;
	}
	public static String validateAirportCoordinate(String coordinate, int min , int max, String axis) {
		if (coordinate == null || coordinate.trim().isEmpty())
            return axis + " coordinate must not be empty.";
        try {
            double d = Double.parseDouble(coordinate.trim());
            if (d < min || d > max)
                return axis + " coordinate value has to be from " + (int)min + " to " + (int)max + ".";
            return null;
        } catch (NumberFormatException e) {
            return axis + " coordinate has to be number.";
        }
	}
	public static String validateFlightTime(String time) {
        if (time == null || time.trim().isEmpty())
            return "Departure time must not be empty.";
        String[] parts = time.trim().split(":");
        if (parts.length != 2)
            return "Time has to be in format HH:MM.";
        try {
            int h = Integer.parseInt(parts[0].trim());
            int m = Integer.parseInt(parts[1].trim());
            if (h < 0 || h > 23) return "Hours have to be from 00 to 23.";
            if (m < 0 || m > 59) return "Minutes have to be from 00 to 59.";
        } catch (NumberFormatException e) {
            return "Time has to be number in format HH:MM.";
        }
        return null;
    }

    public static String validateFlightDuration(String duration) {
        if (duration == null || duration.trim().isEmpty())
            return "Flight duration must not be empty.";
        try {
            int d = Integer.parseInt(duration.trim());
            if (d <= 0) return "Flight duration has to be bigger than 0 min.";
        } catch (NumberFormatException e) {
            return "Flight duration has to be integer.";
        }
        return null;
    }
}
