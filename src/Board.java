package SHEPherd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Board implements ActionListener, MouseListener, KeyListener {	
    private ArrayList<Sheep> sheep;
    private ArrayList<Tower> towers;
    private ArrayList<Fence> fences;
    public int score = 0;
    public Random rand;
    
    public JFrame jframe;
    public JPanel infoPanel;
    public JLabel jlabel;
    public JButton button;
    public Renderer renderer;
    
    public boolean gameStart = false;
    
    public final int trailLength = 10;
	public final int WIDTH = 800, HEIGHT = 800;
	public int goalCenterX = WIDTH/2;
	public int goalCenterY = HEIGHT/2;
	public int goalRadius = 100;
	public int smallGoalRadius = 70;
	public int diameter = 10;
	
	public int sideMenuWidth = 200;
	
	public String help;
	
    /**
     * above code initialized various variable
     * @param sheepSize
     */
	
    public Board(int sheepSize){
    	renderer = new Renderer();
    	infoPanel = new JPanel();
    	infoPanel.setPreferredSize(new Dimension(300, 300));
    	infoPanel.setBackground(Color.GRAY);
		jframe = new JFrame();
		jframe.setIconImage(new ImageIcon("src/spaceship.png").getImage());
		jframe.add(renderer);
		button = makeStartButton(jframe);
		button.setFont(new Font("Monospaced", Font.BOLD, 40));
		button.setBackground(Color.YELLOW);
		button.setForeground(Color.BLUE);
		
		infoPanel.add(button, BorderLayout.CENTER);
		help = "<HTML>Press the button to start the game. "
				+ "<BR>You can use left click and right click to respectivally"
				+ "<BR>push away or attrac the blue dot. "
				+ "<BR>The goal is to get as much as you can into the goal marked as a ring!</HTML>";
		JLabel information = new JLabel(help);
		information.setFont(new Font("Monospaced", Font.ITALIC, 20));
		information.setBackground(Color.ORANGE);
		information.setForeground(Color.GREEN);
		jlabel = new JLabel("This is a start button!");
		infoPanel.add(jlabel, BorderLayout.CENTER);
		infoPanel.add(information, BorderLayout.CENTER);
		jframe.add(infoPanel, BorderLayout.CENTER);
		//this above line adding a JPanel is screwing up the display of the game somehow
		jframe.setTitle("the SHEPherd");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH+sideMenuWidth, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);
		
		if(!gameStart) {
			paintStartScreen();
		}
		
		rand = new Random();
		sheep = new ArrayList<Sheep>();
		for(int i = 0; i < sheepSize; i++) {
			double randX = rand.nextDouble() * (WIDTH - 50);
			double randY = rand.nextDouble() * (HEIGHT - 50);
			sheep.add(new Sheep(randX, randY));
		}
		
		towers = new ArrayList<Tower>();
//		tRepulsor.add(new TowerRepulsor(100.0, 100.0, 5000.0));
//		tRepulsor.add(new TowerRepulsor(800.0, 800.0, 5000.0));
		//makeStartButton(jframe);

		fences = new ArrayList<Fence>();
		fences.add(new Fence(20, 100, 600, 100));
		
		Timer timer = new Timer(17, this);
		timer.start();		
		
		
    }
    
    public void addTower(int[] coordinates, int power){
        towers.add(new TowerRepulsor(coordinates[0], coordinates[1], power));
    }

    private void paintStartScreen() {
    	//infoPanel.add(makeStartButton(jframe));
	}

	private JButton makeStartButton(JFrame frame) {
    	ImageIcon playImage = new ImageIcon("src/startbutton260260.jpg");
    	//JButton button = new JButton("Play", playImage);
    	JButton button = new JButton("Play");
    	button.setBounds(WIDTH + sideMenuWidth/2, HEIGHT/2, 400, 400);
    	button.addActionListener(this);
    	/**
    	button.addActionListener(new ActionListener() {
	        
			public void actionPerformed(ActionEvent arg0) {
					jlabel.setText("Game starting!");
					gameStart = true;
			}          
	      });
	      **/
		
    	return button;
	}
    
	private void checkSheepGoal() {
		for(int i = 0; i < sheep.size(); i ++) {
			double x = sheep.get(i).x + diameter/4.0;
			double y = sheep.get(i).y + diameter/4.0;  //1/4 of the diameter
			double r = Math.sqrt((x-goalCenterX)*(x-goalCenterX) + (y-goalCenterY)*(y-goalCenterY));
			if(r<=smallGoalRadius && !sheep.get(i).destroyed) {
				sheep.get(i).destroyed = true;
				scoreUp();
			}
			//if sheep is in circle
				//if r < radius
			//get rid of sheep 
			//add score
		}

	}
	private void scoreUp() {
		score++;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button) {
			gameStart = true;
			System.out.println(gameStart);
			jframe.remove(infoPanel);
			jframe.add(renderer);
			renderer.revalidate();
		}
		if(gameStart) {
			this.tick();
			renderer.repaint();
		}
		
	}

	public void repaint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		//paints background
		//everything else should come after this
		drawGoal(g);
		
		g.setColor(Color.blue);
		drawSheep(g);
			
		drawFences(g);
		
		g.setColor(Color.ORANGE);
		g.fillRect(WIDTH, 0, sideMenuWidth, HEIGHT);
		drawScore(g);
		drawHelp(g);
	}

	private void drawSheep(Graphics g){
		for(int i = 0; i < sheep.size(); i++) {
			for(int j = 0; j < sheep.get(i).trail.size() - 1; j++) {
				if(!sheep.get(i).destroyed) {
					int x1 = (int) Math.round(sheep.get(i).trail.get(j)[0]);
					int y1 = (int) Math.round(sheep.get(i).trail.get(j)[1]);
					int x2 = (int) Math.round(sheep.get(i).trail.get(j + 1)[0]);
					int y2 = (int) Math.round(sheep.get(i).trail.get(j + 1)[1]);
					g.drawLine(x1, y1, x2, y2);
				}
				
			}
			
			if(sheep.get(i).destroyed!=true)
				g.fillOval( (int) Math.round(sheep.get(i).x) , (int) Math.round(sheep.get(i).y), diameter, diameter);
		}
	}
	
	private void drawHelp(Graphics g) {
		g.setColor(Color.RED);
		g.setFont(new Font("SanSerif", Font.ITALIC, 20));
		String info = "Press the button to start the game. "
				+ "\nYou can use left click and right click to respectivally"
				+ "\npush away or attrac the blue dot. "
				+ "\nThe goal is to get as much as you can into the goal marked as a ring!";
		g.drawString(info, WIDTH, HEIGHT/3);
		
	}

	private void drawScore(Graphics g) {
		g.setColor(Color.RED);
		g.setFont(new Font("SanSerif", Font.PLAIN, 20));
		g.drawString("score is: " + score, WIDTH, 20);
		
	}
	
	private void drawFences(Graphics g){
		g.setColor(Color.BLACK);
		 for(int i = 0; i < fences.size(); i++){
			 g.drawLine(fences.get(i).pX1, fences.get(i).pY1, fences.get(i).pX2, fences.get(i).pY2);
		 }
	 }

	private void drawGoal(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(goalCenterX-goalRadius, goalCenterY-goalRadius, goalRadius*2, goalRadius*2);
		g.setColor(Color.WHITE);
		g.fillOval(goalCenterX-smallGoalRadius, goalCenterY-smallGoalRadius, smallGoalRadius*2, smallGoalRadius*2);
	}	
	
    public void tick(){
		checkSheepGoal();
		
        for(int i = 0; i < sheep.size(); i++) {
    		sheep.get(i).clearForce();
            for(int j = 0; j < towers.size(); j++){
                sheep.get(i).addForce(towers.get(j).calculateForce(sheep.get(i)));	                
            }
            
			if(sheep.get(i).y > HEIGHT - 42) {
				sheep.get(i).y = HEIGHT - Math.abs(HEIGHT - sheep.get(i).y);
				sheep.get(i).dY *= -1;
			}
			if(sheep.get(i).y < 0) {
				sheep.get(i).y = Math.abs(sheep.get(i).y);
				sheep.get(i).dY *= -1;
			}
			if(sheep.get(i).x > WIDTH - diameter) {
				sheep.get(i).x = WIDTH - Math.abs(WIDTH - sheep.get(i).x);
				sheep.get(i).dX *= -1;
			}
			if(sheep.get(i).x < 0) {
				sheep.get(i).x = Math.abs(sheep.get(i).x);
				sheep.get(i).dX *= -1;
			}
			Fence bestFence = null;
            double closestDistance = 1e18;
            for(int j = 0; j < fences.size(); j++) {
            	if(closestDistance > fences.get(j).distance(sheep.get(i))) {
            		bestFence = fences.get(j);
            		closestDistance = fences.get(j).distance(sheep.get(i));
            	}
            }
            
            if(bestFence != null) {
            	sheep.set(i, bestFence.reflect(sheep.get(i)));
            } else {
            	sheep.get(i).applyForce();            	
            }
		}
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

//		int x = e.getX();
//		int y = e.getY();
//		if(tRepulsor.size() == 0) {
//			tRepulsor.add(new TowerRepulsor(x, y, 500));
//		} else {
//			tRepulsor.set(0, new TowerRepulsor(x, y, 500));
//		}		
		
		int x = e.getX();
		int y = e.getY();

		Tower newTower;

		if(SwingUtilities.isRightMouseButton(e)){
			newTower = new TowerAttractor(x, y, 500);
		} else if(SwingUtilities.isLeftMouseButton(e)){
			newTower = new TowerRepulsor(x, y, 500);
		} else {
			return;
		}

		if(towers.size() == 0) {
			towers.add(newTower);
		} else {
			towers.set(0, newTower);
		}
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(towers.size() != 0) {
			towers.remove(0);			
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
