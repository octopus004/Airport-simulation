package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import data.Data;
import model.Airport;
import model.Flight;
import util.Validate;

public class FlightPanel extends JPanel {
	private Data data;
	private JTextField fromField, toField, xField, yField;
	 private DefaultTableModel tableModel;
	
	 public FlightPanel(Data data) {
		this.data = data;
		setLayout(new BorderLayout(5, 5));
	    setBorder(BorderFactory.createTitledBorder("Flights"));
	
	    JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 5);
		fromField = new JTextField(5);
		toField = new JTextField(15);
		xField = new JTextField(6);
		yField = new JTextField(6);
		
		
		gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("From (code):"), gbc);
		gbc.gridx = 1; form.add(fromField, gbc);
		gbc.gridx = 2; form.add(new JLabel("To (code):"), gbc);
		gbc.gridx = 3; form.add(toField, gbc);
		gbc.gridx = 4; form.add(new JLabel("Departure:"), gbc);
		gbc.gridx = 5; form.add(xField, gbc);
		gbc.gridx = 6; form.add(new JLabel("Duration:"), gbc);
		gbc.gridx = 7; form.add(yField, gbc);
		
		JButton addBtn = new JButton("Add flight");
	     addBtn.addActionListener(e -> addFlight());
	     gbc.gridx = 8; form.add(addBtn, gbc);
		
	     
	     tableModel = new DefaultTableModel(new String[]{"From", "To", "Departure", "Duration"}, 0) {
	         @Override public boolean isCellEditable(int r, int c) { return false; }
	     };
	     JTable table = new JTable(tableModel);
	     table.getColumnModel().getColumn(0).setPreferredWidth(50);
	     table.getColumnModel().getColumn(1).setPreferredWidth(200);
	     table.setRowHeight(22);
	     
	     add(form, BorderLayout.NORTH);
	     add(new JScrollPane(table), BorderLayout.CENTER);
	 }
	     
	     private void addFlight() {
		       String nameErr = Validate.validateFlightTime(xField.getText());
		       if (nameErr != null) { showError(nameErr); return; }
		       
		       String codeErr = Validate.validateFlightDuration(yField.getText());
		       if (codeErr != null) { showError(codeErr); return; }
		        

		        String from = fromField.getText().trim().toUpperCase();
		        String to = toField.getText().trim();
		        String[] parts = xField.getText().trim().split(":");
		        int h = Integer.parseInt(parts[0].trim());
	            int m = Integer.parseInt(parts[1].trim());
	            int x= 60*h+m;
	            System.out.println(x);
		        int y = Integer.parseInt(yField.getText().trim());

		        try {
		        	Airport fromAirport = null;
		        	Airport toAirport = null;
		        
		        	for(Airport a:data.getAirports()) {
		        		if(a.getCode().equalsIgnoreCase(from))  fromAirport = a;
		        		if(a.getCode().equalsIgnoreCase(to)) toAirport = a;
		        	}
		        
		            data.addFlight(new Flight(fromAirport, toAirport, x, y));
		            refreshTable();
		        } catch (IllegalArgumentException ex) {
		            showError(ex.getMessage());
		        }
	     }
		        private void showError(String msg) {
			        JOptionPane.showMessageDialog(this, msg, "Error ", JOptionPane.WARNING_MESSAGE);

			 }
		        public void refreshTable() {
			        tableModel.setRowCount(0);
			        for (Flight a : data.getFlights()) {
			            tableModel.addRow(new Object[]{
			                a.getFrom().getCode(), a.getTo().getCode(), a.getDepartureString(),  a.getDuration()
			            });
			        }
	}
}
