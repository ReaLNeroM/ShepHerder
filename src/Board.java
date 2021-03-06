package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.*;
import java.io.File;

import javax.imageio.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Board implements ActionListener, MouseListener, KeyListener {
    private ArrayList<Sheep> sheep;
    private ArrayList<Tower> towers;
    private ArrayList<Goal> goals;
    private ArrayList<Fence> fences;
    public int score = 0;
    public int level = 0;
    public Random rand;
    public String help;
    public ImageIcon img;
    public BufferedImage image;
    public JLabel picLabel;
    
    public JFrame jframe;
    public JPanel infoPanel;
    public JLabel jlabel;
    public JButton button;
    public Renderer renderer;
    
    public boolean gameStart = false;
    public int numOfGoals = 3;
    public final static int trailLength = 10;
	public final int WIDTH = 800, HEIGHT = 800;
	public int diameter = 10;
	
	public int sideMenuWidth = 200;
	
    /**
     * above code initialized various variable
     * @param sheepSize
     */
	
    public Board(int sheepSize, int level){
    	renderer = new Renderer();
    	infoPanel = new JPanel();
    	infoPanel.setPreferredSize(new Dimension(300, 300));
    	infoPanel.setBackground(Color.GRAY);
		jframe = new JFrame();
		jframe.setIconImage(new ImageIcon("src/spaceship.png").getImage());
		jframe.add(renderer);
		button = makeStartButton(jframe);
		button.setFont(new Font("Papyrus", Font.BOLD, 35));
		button.setBackground(Color.YELLOW);
		button.setForeground(Color.BLUE);
		
		try {
			image = ImageIO.read(new File("src/rsz_11us.png"));
		} catch (Exception e) {
			System.out.println("ERROR: NO SUCH FILE");
		}
		picLabel = new JLabel(new ImageIcon(image));
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
		JLabel picLabel = new JLabel(new ImageIcon(image));
		infoPanel.add(picLabel);
		infoPanel.repaint(); 
		//this above line adding a JPanel is screwing up the display of the game somehow
		jframe.setTitle("the SHEPherd");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);
		
		if(!gameStart) {
			paintStartScreen();
		}
		
		rand = new Random();
		sheep = new ArrayList<Sheep>();
		/*for(int i = 0; i < sheepSize; i++) {
			double randX = rand.nextDouble() * (WIDTH - 50);
			double randY = rand.nextDouble() * (HEIGHT - 50);
			sheep.add(new Sheep(randX, randY));
		}*/
		//generate sheep clusters
		//generate goals
		

		goals = new ArrayList<Goal>();

		this.level = level;
		initLevel(level);
		
		towers = new ArrayList<Tower>();
		
		fences = new ArrayList<Fence>();
		fences.add(new Fence(0, 0, WIDTH, 0));
		fences.add(new Fence(0, 0, 0, HEIGHT));
		fences.add(new Fence(0, HEIGHT, WIDTH, HEIGHT));
		fences.add(new Fence(WIDTH, 0, WIDTH, HEIGHT));

		Timer timer = new Timer(17, this);
		timer.start();				
    }

    public void createHerd(int x,int y, int width, int height, int numberOfSheep) {
    	for(int i = 0; i < numberOfSheep; i++) {
    		double randX = rand.nextDouble() * (width) + x;
        	double randY = rand.nextDouble() * (height) + y;
        	sheep.add(new Sheep(randX,randY));
    	}
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
			//if sheep is in circle
				//if r < radius
			//get rid of sheep 
			//add score
		for(int i = 0; i < sheep.size(); i++) {
			for(int j = 0; j < goals.size(); j++) {
				if(sheep.get(i).isInGoal(goals.get(j))) {
					scoreUp();
					sheep.get(i).destroyed = true;
					
				}
			}
		}
	}

	private void scoreUp() {
		score++;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button) {
			gameStart = true;
			jframe.remove(infoPanel);
			jframe.add(renderer);
			renderer.revalidate();
		}
		if(gameStart) {
			this.tick();
			renderer.repaint();
		}
		if(!gameStart) {
			initLevel(level++);
			gameStart = true;
		}
	}

	public void repaint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		//paints background
		//everything else should come after this
		
		for(int i = 0; i < goals.size(); i++){
			goals.get(i).drawGoal(g);
		}
		
		g.setColor(Color.blue);
		drawSheep(g);
			
		g.setColor(Color.ORANGE);
		g.fillRect(WIDTH, 0, sideMenuWidth, HEIGHT);
		drawScore(g);
		drawFences(g);
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
	
	private void drawScore(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.RED);
		int fontY = 40;
		g.setFont(new Font("Futura", Font.PLAIN, fontY));
		g.drawString("score is: " + score, 5, fontY);
		
	}

	private void drawFences(Graphics g){
		g.setColor(Color.BLACK);
		for(int i = 0; i < fences.size(); i++){
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(5));
			g.drawLine(fences.get(i).pX1, fences.get(i).pY1, fences.get(i).pX2, fences.get(i).pY2);
		}
	}

//	private void drawGoal(Graphics g) {
//		g.setColor(Color.YELLOW);
//		g.fillOval(goalCenterX-goalRadius, goalCenterY-goalRadius, goalRadius*2, goalRadius*2);
//		g.setColor(Color.WHITE);
//		g.fillOval(goalCenterX-smallGoalRadius, goalCenterY-smallGoalRadius, smallGoalRadius*2, smallGoalRadius*2);
//	}	
	
    public void tick(){
		checkSheepGoal();
		checkLevelComplete();
		
        for(int i = 0; i < sheep.size(); i++) {
    		sheep.get(i).clearForce();
            for(int j = 0; j < towers.size(); j++){
                sheep.get(i).addForce(towers.get(j).calculateForce(sheep.get(i)));	                
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
    
	private void checkLevelComplete() {
		if(score > 50) {
			System.out.println("goal of the level completed");
			score = 0;
			sheep = new ArrayList<Sheep>();
			goals = new ArrayList<Goal>();
			gameStart = false;
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
 
    private void initLevel(int level) {
    	if(level == 1) {
			createHerd(100,600,100,100, 100); //just an example, i sum the number of sheep to be 100.
			goals.add(new Goal(WIDTH/2,HEIGHT/2,200,190));
		}
		else if(level == 2) {
			goals.add(new Goal(WIDTH/4,HEIGHT/2,100,95));
			goals.add(new Goal(3*WIDTH/4,HEIGHT/2,100,95));

			//createHerd(100,600,100,100, 100); //just an example, i sum the number of sheep to be 100.

			createHerd(350,50,100,100, 50); //just an example, i sum the number of sheep to be 100.
			createHerd(350,600,100,100, 50);
		}
		else if(level == 3) {
			goals.add(new Goal(WIDTH/2,HEIGHT/4,50,45));
			goals.add(new Goal(WIDTH/3,HEIGHT/2,50,45));
			goals.add(new Goal(2*WIDTH/3,HEIGHT/2,50,45));
			createHerd(350,350,100,100, 30); //just an example, i sum the number of sheep to be 100.
			createHerd(WIDTH/4,HEIGHT/4,100,100, 35); //just an example, i sum the number of sheep to be 100.
			createHerd((int)(2.5*WIDTH/4),HEIGHT/4,100,100, 35); //just an example, i sum the number of sheep to be 100.
		}
		else if(level == 4) {
			goals.add(new Goal(WIDTH/10,HEIGHT/4,50,45));
			goals.add(new Goal(8*WIDTH/10,HEIGHT/4,50,45));
			goals.add(new Goal(WIDTH/10,3*HEIGHT/4,50,45));
			goals.add(new Goal(8*WIDTH/10,3*HEIGHT/4,50,45));
			createHerd(350,350,100,100,100);
		}
		/* THIS IS HOW WE WOULD GENERATE DIFFERENT LEVELS!
		for(int i = 0; i < numOfGoals; i++) {
			goals.get(i) = new Goal(WIDTH/2,HEIGHT/2,100,70);	
		}*/
	}
    
}
