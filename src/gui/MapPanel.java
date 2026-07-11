package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import data.Data;
import model.Airplane;
import model.Airport;
import model.Flight;


public class MapPanel extends JPanel {
	
	private MainPanel mainpanel;
	private MapCanvas canvas;
	private Timer blinkTimer;
	private boolean blinkOn = false;
	private JPanel sidebar;
	private List<JCheckBox> checkBoxes = new ArrayList<>();
	private List<String> hidden = new ArrayList<>();
	private Airport selected = null;
	private Data data;
	
	public MapPanel(Data d, MainPanel p) {
		this.data = d;
		this.mainpanel = p;
		 setLayout(new BorderLayout(5, 5));
		 canvas = new MapCanvas();
	        add(canvas, BorderLayout.CENTER);

	        sidebar = new JPanel();
	        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
	        sidebar.setPreferredSize(new Dimension(300, 0));
	        
	        add(new JScrollPane(sidebar), BorderLayout.EAST);

	        blinkTimer = new Timer(500, e -> {
	            blinkOn = !blinkOn;
	            canvas.repaint();
	        });
	        blinkTimer.start();

	}
	public void refresh() {
        // Sync visible map
        
        rebuildSidebar();
        canvas.repaint();
    }
	private void rebuildSidebar() {
	    sidebar.removeAll();

	    for (Airport a : data.getAirports()) {

	        JCheckBox cb = new JCheckBox(a.getCode() +" - "+ a.getName() +"  "+a.getX()+","+a.getY(),!hidden.contains(a.getCode()));

	        cb.addActionListener(e -> {
	        	if (cb.isSelected()) {
	                hidden.remove(a.getCode()); 
	            } else {
	                hidden.add(a.getCode()); a.change();
	            }canvas.repaint();
	        });

	        sidebar.add(cb);
	    }

	    sidebar.revalidate();
	    sidebar.repaint();
	}
	
	private List<Airplane> airplanes = new ArrayList<>();
    private int simMinutes = 0;

    public void setSimulationData(List<Airplane> airplanes, int simMinutes) {
        this.airplanes = airplanes;
        this.simMinutes = simMinutes;
        canvas.repaint();
    }
   private  Airport sel = null;
   private double startx,starty,endX,endY;
   private boolean selecting = false;
   boolean released = false;
   boolean vis = false;
   List<Airport>w = new ArrayList<>();
	private class MapCanvas extends JPanel{
		public MapCanvas() {
	    setBackground(Color.WHITE);
	    
	    addMouseListener(new MouseAdapter() {
	    	
		       @Override
		       public void mouseClicked(MouseEvent e) {
		    	   if(SwingUtilities.isRightMouseButton(e)) {
		    		   handleRight(e.getX(), e.getY());
		    	   }else {
		    	   handleClick(e.getX(), e.getY());
		        }
		    	   }
		       @Override
		       public void mousePressed(MouseEvent e) {
		    	   if(!SwingUtilities.isLeftMouseButton(e)) {
		    		   return;
		    	   }
		    	   if(!released) {
		    	   startx = e.getX();
		    	   starty = e.getY();
		    	   return;
		    	   }
		    	   
		    	   for(Airport a:data.getAirports()) {
		    		   int sx = toScreenX(a.getX());
		    		   int sy = toScreenY(a.getY());
		    		   if(Math.hypot(e.getX()-sx,e.getY()-sy)<8) {
		    			   sel= a;
		    			   return;
		    		   }
		    	   }
		    	   
		       }
		      @Override
		       public void mouseReleased(MouseEvent e) {
		    	   released = true;
		    	   System.out.println("wwwwwwwwwwww");
		    	   System.out.println(starty);
		    	   System.out.println(endY);
		    	   
		    	   for(Airport a :data.getAirports()) {
		    		  System.out.println("sssssssssssssssss");
		    		   int x = toScreenX(a.getX());
		    		   int y = toScreenY(a.getY());
		    		  System.out.println(y);
		    		   if((x>startx&&x<endX)&&(y>starty&&y<endY)) {
		    			   w.add(a);
		    		   }
		    	   }
		    	  
		       }
		    });
		     
	    
	    addMouseMotionListener(new MouseAdapter(){
		       @Override
		       public void mouseDragged(MouseEvent e) {
		    	   
		    	   endX = e.getX();
		    	   endY = e.getY();
		    	   selecting =true;
		    	   vis = true;
		    	   
		    	   sel.setX(fromScreenX(e.getX()));
		           sel.setY(fromScreenY(e.getY()));

		           repaint();
		       }
		       
		});
		}
	
	    public void handleRight(int mx, int my) {
	    	
	    	int sz = 14;
	    	 if(w.isEmpty()) System.out.println("aaaaaaaaaaa");
		    for (Airport a : w) {
System.out.println(a.getName());
		        int x = toScreenX(a.getX());
		        int y = toScreenY(a.getY());
		        System.out.println(x);
		        if(Math.hypot(mx-x,my-y)<8) {
		        	hidden.add(a.getCode());
		        	vis = false;
		        	repaint();
		        	return;
		        }
		        if (mx >= x - sz/2 && mx <= x + sz/2 && my >= y - sz/2 && my <= y + sz/2) {
		        	//a.change();
		        	
		        }
		        }
	    }
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	
	 
	    drawAirports(g);
	    drawAirplanes(g);
	}
	
	
	
	private void drawAirplanes(Graphics g) {
	    g.setColor(Color.BLUE);

	    for (Airplane ap : airplanes) {
	    	if(ap.getFlight().getFrom().is()||ap.getFlight().getTo().is()) continue;
	    	 if (!ap.isActive(simMinutes))
	            continue;
	        
	        double p = ap.getProgress(simMinutes);

	        double x = ap.getFlight().getFrom().getX()
	                + p * (ap.getFlight().getTo().getX() - ap.getFlight().getFrom().getX());

	        double y = ap.getFlight().getFrom().getY()
	                + p * (ap.getFlight().getTo().getY() - ap.getFlight().getFrom().getY());

	        int sx = toScreenX(x);
	        int sy = toScreenY(y);

	        g.fillOval(sx - 5, sy - 5, 10, 10);
	    }
	}
	public void trecina() {
		List<Airport> air = data.getAirports();
		int len = air.size();
		List<Airport> listamin = new ArrayList<>(); 
		List<Airport> listamax = new ArrayList<>(); 

		air.sort(Comparator.comparingInt(Airport::inn));
		for(int i =0;i<len/3;i++) {
			listamin.add(air.get(i));
		}
		air.reversed();
		for(int i =0;i<len/3;i++) {
			listamax.add(air.get(i));
		}
		
		}
	
	private void drawAirports(Graphics g) {
		
	    g.setColor(Color.GRAY);
	    int sz = 14;
	    for (Airport a : data.getAirports()) {
	        if(hidden.contains(a.getCode())) continue;
	        int x = toScreenX(a.getX());
	        int y = toScreenY(a.getY());
	        
	        int sx = toScreenX(a.getX()), sy = toScreenY(a.getY());
            boolean isSel = a == selected;
			if(a.is()) {
				System.out.println(a.is());
				g.setColor(Color.BLACK);
			}
			else  if (isSel && blinkOn) {
            	
                g.setColor(Color.RED);
            } else if (isSel) {
                g.setColor(new Color(180, 0, 0));
            } else {
            	
                g.setColor(new Color(160, 160, 160));
            }
			
            g.fillRect(sx - sz/2, sy - sz/2, sz, sz);

            
	
	        g.drawString(a.getCode(), x + 5, y);
	        
	        if(selecting) {
	        	g.setColor(Color.BLUE);
	        	if(vis)
	        	
	        	g.fillRect((int)startx, (int)starty, (int)Math.abs(startx-endX),(int)Math.abs(starty-endY));
	        }
	    }
	}
	private double fromScreenX(int x) {
	    return (double)x / getWidth() * 360 - 180;
	}
	private double fromScreenY(int y) {
	    return 90 - (double)y / getHeight() * 180;
	}
	
	private int toScreenX(double x) {
	    return (int)((x + 180) / 360 * getWidth());
	}
	
	private int toScreenY(double y) {
	    return (int)((90 - y) / 180 * getHeight());
	}
	
	
	private void handleClick(int mx, int my) {
		int sz = 14;
		    for (Airport a : data.getAirports()) {

		        int x = toScreenX(a.getX());
		        int y = toScreenY(a.getY());

		        if (mx >= x - sz/2 && mx <= x + sz/2 && my >= y - sz/2 && my <= y + sz/2) {
		            if (selected == a) {
		                selected = null;
		            mainpanel.startTimer();
		            }
		        
		            else {
		             
			            selected = a;
			            mainpanel.stopTimer();
		            }

		            repaint();
		            return;
		        }
		    }
		    

		  
		    repaint();
		}
	
	public boolean hasSelected() {
		if(selected!=null) return true;
		return false;
	}
	}

}
	
