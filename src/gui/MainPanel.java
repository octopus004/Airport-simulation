package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import data.Data;
import util.FileIO;


public class MainPanel extends JFrame {
	
	private AirportPanel airportpanel;
	private FlightPanel flightpanel;
	private MapPanel mappanel;
	private SimulationPanel simpanel;
	private Data data = new Data();
	
	
	public Timer timer;
	private int inactivitySeconds = 0;
    private static final int timeout = 60;
    private JDialog countdownDialog;
    private JLabel countdownLabel;
    private boolean countdownShowing = false;
    private static final int warning = 5;

	public MainPanel() {
		super("Simulation – OOP2 2026");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 800);
		setMinimumSize(new Dimension(900, 600));
		
		
        
		build();
		
		airportpanel.setOnAirportAdded(() ->{ mappanel.refresh();});
		setupInactivityTimer();
		setupActivityTracking();
		setVisible(true);

	}
	
	private JToolBar buildToolbar() {
		
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton saveCSV = new JButton("Save CSV");
        JButton loadCSV = new JButton("Load CSV");
        JButton saveJSON = new JButton("Save JSON");
        JButton loadJSON = new JButton("Load JSON");

        saveCSV.addActionListener(e -> saveFile("csv"));
        loadCSV.addActionListener(e -> loadFile("csv"));
        saveJSON.addActionListener(e -> saveFile("json"));
        loadJSON.addActionListener(e -> loadFile("json"));

        tb.add(saveCSV); tb.add(loadCSV);
        tb.addSeparator();
        tb.add(saveJSON); tb.add(loadJSON);
        tb.addSeparator();
        return tb;
        
	}
	private void build() {
		setLayout(new BorderLayout(5, 5));
		flightpanel = new FlightPanel(data);
		mappanel = new MapPanel(data,this);
		airportpanel = new AirportPanel(data);
		simpanel = new SimulationPanel(data,this,mappanel);
		
		 JToolBar toolbar = buildToolbar();
	     add(toolbar, BorderLayout.NORTH);
	    
	     add(mappanel, BorderLayout.CENTER);
	     JTabbedPane dataTabs = new JTabbedPane();
	     dataTabs.addTab("Airports", airportpanel);
	     dataTabs.addTab("Fligths", flightpanel);
	     dataTabs.setPreferredSize(new Dimension(0, 220));
	     
	     JPanel south = new JPanel(new BorderLayout(3, 3));
	     south.add(simpanel, BorderLayout.NORTH);
	     south.add(dataTabs, BorderLayout.CENTER);
	    
	     add(south, BorderLayout.SOUTH);
	     
	}
	

	private void saveFile(String file) {
		resetInactivity();
		JFileChooser fc = new JFileChooser();
		fc.showSaveDialog(this);
		File f = fc.getSelectedFile();
		 try {
	            if (file.equals("csv")) FileIO.saveCSV(data, f);
	            else FileIO.saveJSON(data, f);
	        } catch (Exception ex) {
	            showError("Error " + ex.getMessage());
	        }
	}
	private void loadFile(String file) {
		resetInactivity();
		JFileChooser fc = new JFileChooser();
		fc.showSaveDialog(this);
		File f = fc.getSelectedFile();
	 try {
         if (file.equals("csv")) FileIO.loadCSV(data, f);
         else FileIO.loadJSON(data, f);
         airportpanel.refreshTable();
         flightpanel.refreshTable();
         mappanel.refresh();
        
     } catch (Exception ex) {
         showError(" Error " + ex.getMessage());
     }
	 }
	
	
	private void setupInactivityTimer() {
        timer = new Timer(1000, e -> {
            inactivitySeconds++;
            int remaining = timeout - inactivitySeconds;
            if (remaining <= warning && !countdownShowing) {
                showCountdownDialog(remaining);
              //!!
            } else if (countdownShowing && remaining > 0) {
                countdownLabel.setText("Closing: " + remaining + " seconds");
            } else if (remaining <= 0) {
                timer.stop();
                if (countdownDialog != null) countdownDialog.dispose();
                System.exit(0);
            }
        });
        timer.start();
    }
	public boolean isSimRunning() {
	    return simpanel != null && simpanel.isRunning();
	}
	public void startTimer() {
		 timer.start();
	}
	public void stopTimer() {
		    timer.stop();
		    
		
	}
	 private void showCountdownDialog(int remaining) {
		 	countdownDialog = new JDialog(this, false);
	        countdownShowing = true;
	        countdownDialog.setLayout(new BorderLayout(10, 10));
	        countdownDialog.setSize(360, 160);
	        countdownDialog.setLocationRelativeTo(this);

	        countdownLabel = new JLabel("Closing in: " + remaining + " seconds", SwingConstants.CENTER);
	       
	        JLabel msg = new JLabel("Program is closing due to inactivity.", SwingConstants.CENTER );

	        JButton continueBtn = new JButton("Continue work");
	        continueBtn.addActionListener(e -> {
	            resetInactivity();
	            countdownDialog.dispose();
	            countdownShowing = false;
	        });

	        JPanel btnPanel = new JPanel();
	        btnPanel.add(continueBtn);

	        countdownDialog.add(msg, BorderLayout.NORTH);
	        countdownDialog.add(countdownLabel, BorderLayout.CENTER);
	        countdownDialog.add(btnPanel, BorderLayout.SOUTH);
	        countdownDialog.setVisible(true);
	    }
	 
	 
	 public void resetInactivity() {
		 inactivitySeconds = 0;
		 if (!timer.isRunning()&& !isSimRunning()) timer.start();
		 
     }
	 private void setupActivityTracking() {
	        // Track mouse and keyboard events globally
	        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
	            if (event instanceof MouseEvent || event instanceof KeyEvent) {
	                resetInactivity();
	            }
	        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
	    }
	private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
	
	
}
