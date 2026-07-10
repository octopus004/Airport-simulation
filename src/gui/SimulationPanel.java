package gui;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import data.Data;
import model.Airplane;

public class SimulationPanel extends JPanel {
	private MainPanel mainpanel;
	private MapPanel mappanel;
	private Data data;
	private Thread simThread;
	private JButton startBtn,pauseBtn, resetBtn;
	private JLabel timeLabel;
	private int minutes = 0;
	private boolean running = false;
	public SimulationPanel(Data data, MainPanel p, MapPanel map) {
		this.data = data;
		this.mainpanel = p;
		this.mappanel = map;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		
		timeLabel = new JLabel("Time: 00:00");
		startBtn = new JButton("Run");
        pauseBtn = new JButton("Pause");
        resetBtn = new JButton("Reset");

        pauseBtn.setEnabled(false);
        
        startBtn.addActionListener(e -> start());
        pauseBtn.addActionListener(e -> pause());
        resetBtn.addActionListener(e -> reset());

        add(timeLabel);
        add(startBtn);
        add(pauseBtn);
        add(resetBtn);
        
        
	}

	 private void start() {
		    data.buildAirplanes();
		    running = true;
		    mainpanel.stopTimer();
		    startBtn.setEnabled(false);
		    pauseBtn.setEnabled(true);

		    simThread = new Thread(() -> {
		        while (running) {
		            minutes += 2;
		            if (minutes > 1440) minutes = 0;

		            SwingUtilities.invokeLater(() -> update());

		            try {
		                Thread.sleep(200);
		            } catch (InterruptedException e) {
		                Thread.currentThread().interrupt();
		                break;
		            }
		        }
		    });
		    simThread.setDaemon(true);
		    simThread.start();
		}


	    private void pause() {
	        running = false;
	       
	        mainpanel.resetInactivity();
	        startBtn.setEnabled(true);
            pauseBtn.setEnabled(false);
	    }


	    private void reset() {
	        
	        running = false;
	        if (simThread != null) simThread.interrupt();
	        mainpanel.resetInactivity();
	        minutes = 0;
	        startBtn.setEnabled(true);
	        pauseBtn.setEnabled(false);
	        SwingUtilities.invokeLater(() -> {
	            timeLabel.setText("Time: 00:00");
	            mappanel.setSimulationData(List.of(), 0);
	        });

	        timeLabel.setText("Time: 00:00");
	        mappanel.setSimulationData(List.of(), 0);
	    }
	    
	    private void update() {
	        timeLabel.setText( String.format("Time: %02d:%02d", minutes / 60, minutes % 60));

	        List<Airplane> planes = data.getAirplanes();
	        mappanel.setSimulationData(planes, minutes);
	    }
	    public boolean isRunning() {
	        return running;
	    }
		
	}

