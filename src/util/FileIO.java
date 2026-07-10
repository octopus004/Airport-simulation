package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import data.Data;
import model.Airport;
import model.Flight;

public class FileIO {
	public static void saveCSV(Data store, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter( new FileOutputStream(file)))) {
            pw.println("# AIRPORTS");
            pw.println("CODE,NAME,X,Y");
            for (Airport a : store.getAirports()) {
                pw.printf("%s,%s,%.0f,%.0f%n", a.getCode(), a.getName(), a.getX(), a.getY());
            }
            pw.println("# FLIGHTS");
            pw.println("FROM,TO,DEPARTURE,DURATION");
            for(Flight f:store.getFlights()) {
            	pw.printf("%s,%s,%s,%d%n", f.getFrom(), f.getTo(), f.getDepartureString(), f.getDuration());
            }
        }
        
	}
	public static void loadCSV(Data store, File file) throws IOException{
		 if (!file.exists()) throw new FileNotFoundException(
	            "File '" + file.getName() + "' not found.");
	        if (!file.canRead()) throw new IOException(
	            "File '" + file.getName() + "' cannot be read. Check permissions.");

	        List<String> lines;
	        try {
	            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
	        } catch (IOException e) {
	            throw new IOException("Error while reading the file: " + e.getMessage());
	        }
	        store.clearAll();
	        String section = null;
	        int lineNum = 0;

	        for (String raw : lines) {
	            lineNum++;
	            String line = raw.trim();
	            if (line.isEmpty()) continue;
	            if (line.startsWith("#")) {
	                section = line;
	                continue;
	            }
	            if (line.startsWith("CODE,") || line.startsWith("FROM,")) continue; 

	            String[] parts = line.split(",", -1);
	            try {
	            	 if (section != null && section.contains("AIRPORTS")) {
	            		 String code = parts[0].trim();
	            		 double x = Double.parseDouble(parts[2].trim());
	                     double y = Double.parseDouble(parts[3].trim());
	                     store.addAirport(new Airport(code, parts[1].trim(), x, y));
	            	 } 
	            	 else if(section!= null && section.contains("FLIGHTS")) {
	            		 Airport from = store.findAirport(parts[0].trim());
	                     Airport to = store.findAirport(parts[1].trim());
	                     int dep = parseTime(parts[2].trim(), lineNum);
	                     int dur = Integer.parseInt(parts[3].trim());
	                     store.addFlight(new Flight(from, to, dep, dur));
	            	 }
	            }catch (NumberFormatException e) {
	                throw new IOException("Line " + lineNum + ": Incorrect number – " + e.getMessage()+". Check column format.");
	            }
	        }
	
	}
	 public static void saveJSON(Data store, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            pw.println("{");
            pw.println("\"airports\":[");
            List<Airport> airports = store.getAirports();
            for (int i = 0; i < airports.size(); i++) {
                Airport a = airports.get(i);
                pw.printf("{\"code\":\"%s\",\"name\":\"%s\",\"x\":%.0f,\"y\":%.0f}%s%n",
                    a.getCode(), escape(a.getName()), a.getX(), a.getY(),
                    i < airports.size()-1 ? "," : "");
            }
            pw.println("],");
            pw.println("\"flights\":[");
            List<Flight> flights = store.getFlights();
            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);
                pw.printf("{\"from\":\"%s\",\"to\":\"%s\",\"departure\":\"%s\",\"duration\":%d}%s%n",
                    f.getFrom().getCode(), f.getTo().getCode(),
                    f.getDepartureString(), f.getDuration(),
                    i < flights.size()-1 ? "," : "");
            }
            pw.println("]");
            pw.println("}");
        }
	    }

	 public static void loadJSON(Data store, File file) throws IOException {
     if (!file.exists()) throw new FileNotFoundException(
         "File '" + file.getName() + "' not found.");
        String content;
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IOException("Error: " + e.getMessage());
        }

        store.clearAll();
        try {
            // Parse airports
            String airportSection = extractArray(content, "airports");
            for (String obj : splitObjects(airportSection)) {
                String code = jsonString(obj, "code").toUpperCase();
               
                String name = jsonString(obj, "name");
                double x = jsonDouble(obj, "x");
                double y = jsonDouble(obj, "y");
               
                store.addAirport(new Airport(code, name, x, y));
            }
            // Parse flights
            String flightSection = extractArray(content, "flights");
            for (String obj : splitObjects(flightSection)) {
                Airport from = store.findAirport(jsonString(obj, "from"));
                Airport to = store.findAirport(jsonString(obj, "to"));
               
                int dep = parseTime(jsonString(obj, "departure"), 0);
                int dur = (int) jsonDouble(obj, "duration");
                store.addFlight(new Flight(from, to, dep, dur));
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Error: " + e.getMessage());
        }
	    }

	    
	  
	 private static int parseTime(String s, int lineNum) throws IOException {
		 String[] parts = s.split(":");
		 if (parts.length != 2) throw new IOException(
			 (lineNum > 0 ? "Line " + lineNum + ": " : "") + "Incorrect time format  '" + s + "'. Expected HH:MM.");
		 try {
		      int h = Integer.parseInt(parts[0].trim());
		      int m = Integer.parseInt(parts[1].trim());
		      if (h < 0 || h > 23 || m < 0 || m > 59) throw new IOException(
		          "Time '" + s + "' is OutOfBound.");
		          return h * 60 + m;
		 } catch (NumberFormatException e) {
		   throw new IOException("Incorrect time format '" + s + "'.");
		 }
	}

	 private static String escape(String s) {
	        return s.replace("\\", "\\\\").replace("\"", "\\\"");
	    }
	 
	 private static String extractArray(String json, String key) throws IOException {
	        String search = "\"" + key + "\"";
	        int idx = json.indexOf(search);
	        if (idx < 0) throw new IOException("JSON doesnt contain section '" + key + "'.");
	        int start = json.indexOf('[', idx);
	        if (start < 0) throw new IOException("Missing '[' for section '" + key + "'.");
	        int depth = 0, end = start;
	        for (int i = start; i < json.length(); i++) {
	            if (json.charAt(i) == '[') depth++;
	            else if (json.charAt(i) == ']') { depth--; if (depth == 0) { end = i; break; } }
	        }
	        return json.substring(start + 1, end);
	    }

	 private static List<String> splitObjects(String s) {
	        List<String> result = new ArrayList<>();
	        int depth = 0, start = -1;
	        for (int i = 0; i < s.length(); i++) {
	            char c = s.charAt(i);
	            if (c == '{') { depth++; if (depth == 1) start = i; }
	            else if (c == '}') { depth--; if (depth == 0 && start >= 0) result.add(s.substring(start, i+1)); }
	        }
	        return result;
	    }

	    private static String jsonString(String obj, String key) {
	        String search = "\"" + key + "\":\"";
	        int idx = obj.indexOf(search);
	        if (idx < 0) return "";
	        int start = idx + search.length();
	        int end = obj.indexOf('"', start);
	        return end < 0 ? "" : obj.substring(start, end);
	    }

	    private static double jsonDouble(String obj, String key) {
	        String search = "\"" + key + "\":";
	        int idx = obj.indexOf(search);
	        if (idx < 0) return 0;
	        int start = idx + search.length();
	        int end = start;
	        while (end < obj.length() && "-0123456789.".indexOf(obj.charAt(end)) >= 0) end++;
	        try { return Double.parseDouble(obj.substring(start, end)); }
	        catch (NumberFormatException e) { return 0; }
	    }
}