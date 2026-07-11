package gui;

import java.awt.*;

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
import util.Validate;
import util.Validate;

public class AirportPanel extends JPanel {
	
	private Data data;
	private JTextField codeField, nameField, xField, yField;
	 private DefaultTableModel tableModel;
	private Runnable onAirportAdded;
	 public void setOnAirportAdded(Runnable r) {
		    onAirportAdded = r;
		}
	public AirportPanel(Data data) {
		this.data = data;
		setLayout(new BorderLayout(5, 5));
	    setBorder(BorderFactory.createTitledBorder("Airports"));
	   

	JPanel form = new JPanel(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 5, 3, 5);
  

	
	codeField = new JTextField(5);
	nameField = new JTextField(15);
	xField = new JTextField(6);
	yField = new JTextField(6);
	
	
	gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Code (3 letters):"), gbc);
	gbc.gridx = 1; form.add(codeField, gbc);
	gbc.gridx = 2; form.add(new JLabel("Name:"), gbc);
	gbc.gridx = 3; form.add(nameField, gbc);
	gbc.gridx = 4; form.add(new JLabel("X (-180..180):"), gbc);
	gbc.gridx = 5; form.add(xField, gbc);
	gbc.gridx = 6; form.add(new JLabel("Y (-90..90):"), gbc);
	gbc.gridx = 7; form.add(yField, gbc);
	
	 JButton addBtn = new JButton("Add airport");
     addBtn.addActionListener(e -> addAirport());
     gbc.gridx = 8; form.add(addBtn, gbc);

     
     tableModel = new DefaultTableModel(new String[]{"Code", "Name", "X", "Y"}, 0) {
         @Override public boolean isCellEditable(int r, int c) { return false; }
     };
     JTable table = new JTable(tableModel);
     table.getColumnModel().getColumn(0).setPreferredWidth(50);
     table.getColumnModel().getColumn(1).setPreferredWidth(200);
     table.setRowHeight(22);

     add(form, BorderLayout.NORTH);
     add(new JScrollPane(table), BorderLayout.CENTER);
 }
	 private void addAirport() {
	        String codeErr = Validate.validateAirportCode(codeField.getText());
	        if (codeErr != null) { showError(codeErr); return; }
	        String nameErr = Validate.validateAirportName(nameField.getText());
	        if (nameErr != null) { showError(nameErr); return; }
	        String xErr = Validate.validateAirportCoordinate(xField.getText(), -180, 180, "X");
	        if (xErr != null) { showError(xErr); return; }
	        String yErr = Validate.validateAirportCoordinate(yField.getText(), -90, 90, "Y");
	        if (yErr != null) { showError(yErr); return; }

	        String code = codeField.getText().trim().toUpperCase();
	        String name = nameField.getText().trim();
	        double x = Double.parseDouble(xField.getText().trim());
	        double y = Double.parseDouble(yField.getText().trim());

	        try {
	            data.addAirport(new Airport(code, name, x, y));
	            refreshTable();
	            if (onAirportAdded != null) {
	                onAirportAdded.run();
	            }
	          
	            
	        } catch (IllegalArgumentException ex) {
	            showError(ex.getMessage());
	        }
	    }
	
	 private void showError(String msg) {
	        JOptionPane.showMessageDialog(this, msg, "Error ", JOptionPane.WARNING_MESSAGE);

	 }
	 public void refreshTable() {
	        tableModel.setRowCount(0);
	        for (Airport a : data.getAirports()) {
	            tableModel.addRow(new Object[]{
	                a.getCode(), a.getName(),
	                String.format("%.0f", a.getX()), String.format("%.0f", a.getY())
	            });
	        }
	 }
}


