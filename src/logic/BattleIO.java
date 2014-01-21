/*
	http://helpdesk.objects.com.au/java/changing-the-colormodel-of-a-bufferedimage	Changes interpretation of image
	http://stackoverflow.com/questions/23763/colorizing-images-in-java	I think this changes the image itself and is for other purposes
*/

package game.logic;

import game.Game;
import game.ai.Strategy;
import game.FileIO;
import game.entities.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

public class BattleIO extends JFrame
{
	public static final int DISPLAY_FREQUENCY = 60, LOGIC_FREQUENCY = 30;	// Framerate (hertz)
	public static final int BORDER = 25;	// Size of border
	public static final Color BORDER_COLOR = Color.YELLOW;	// obvious
	public static final int BG_RATIO = 10;	// determines the size of the background relative to the screen
	public static final float MINIMAP_RATIO = 1/6f;	// How large the minimap is relative to the screen
	public static final int MINIMAP_DOTS = 10;	// Size of dots on the minimap

	private static BufferStrategy bufferStrategy;	// for getting Graphics to draw on
	private static GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice graphDevice = graphEnv.getDefaultScreenDevice();
	private static GraphicsConfiguration graphicConf = graphDevice.getDefaultConfiguration();
	
	private static int width = (int)graphicConf.getBounds().getWidth(), height = (int)graphicConf.getBounds().getHeight();	//size of screen
	private static Dimension dimensions = new Dimension(width, height);	//encapsulates size into single variable
	private static int mmX = (int)(width * (1 - MINIMAP_RATIO)), mmY = (int)(height * (1 - MINIMAP_RATIO)),
					mmWidth = (int)(width*MINIMAP_RATIO), mmHeight = (int)(height * MINIMAP_RATIO);	// minimap variables
	public final BufferedImage bg = FileIO.getImage("bg.jpg");	//background
	
	private BattleLogic logic;	//obvious
	private int difficulty = 0;	//obvious
	private int logicWidth, logicHeight;	//size of game map

	private List<Integer> keyInputs = new CopyOnWriteArrayList<Integer>();	//obvious
	private List<MouseEvent> mouseInputs = new CopyOnWriteArrayList<MouseEvent>();
	
	private final Action logicFrame = new AbstractAction()	// action taken by logic each frame
		{
			public void actionPerformed(ActionEvent e)
			{
				if(logic.getVictorious() != null)
					return;
				logic.frame(keyInputs, mouseInputs);
			}
		};
		
	private final Action displayFrame = new AbstractAction()	// action taken by display each frame
		{
			public void actionPerformed(ActionEvent e)
			{
				Graphics2D g = null;
				try
				{
					g = (Graphics2D)bufferStrategy.getDrawGraphics();
					if(!paused)
						render(g);
					else
					{
						g.setColor(Color.RED);
						g.clearRect(0, 0, width, height);
						g.drawString("Paused", 100, 20);
						String[] instr = instructions();
						for(int i = 0; i < instr.length; i++)
							g.drawString(instr[i], 100, 200 + i*20);
					}
				} finally { g.dispose(); }
				bufferStrategy.show();
			}
		};
		
	private javax.swing.Timer logicTimer = new javax.swing.Timer((int)(1000/LOGIC_FREQUENCY), logicFrame),	// timers
									  displayTimer = new javax.swing.Timer((int)(1000/DISPLAY_FREQUENCY), displayFrame);
	
	private boolean paused = false;	//obvious
	
	public BattleIO(CopyOnWriteArrayList<Team> teams, int gridWidth, int gridHeight)
	{
		Graphics2D graphics2D = bg.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	
		logic = new BattleLogic(teams, gridWidth, gridHeight);
		logicWidth = gridWidth * BattleLogic.CELL_WIDTH;
		logicHeight = gridHeight * BattleLogic.CELL_HEIGHT;
		
		setUndecorated(true);
		setResizable(false);
		setIgnoreRepaint(true);
		setVisible(true);
		setSize(width, height);
		
		addKeyListener(new KeyHandler());
		MouseHandler temp = new MouseHandler();
		addMouseListener(temp);
		addMouseMotionListener(temp);
		
		initiate();
	}
	
	private void initiate()
	{
		// Setting up double buffering
		createBufferStrategy(2);
		bufferStrategy = getBufferStrategy();
		try
		{
			// Changing to full-screen
	   	graphDevice.setFullScreenWindow(this);
		}
		catch(Exception e)
		{
			graphDevice.setFullScreenWindow(null);
		}
		logicTimer.start();
		displayTimer.start();
	}
	
	public void render(Graphics2D g)
	{
		if(logic.getVictorious() != null)
		{
			g.setColor(Color.WHITE);
			g.drawString("Team " + logic.getVictorious().toString() + " is victorious!", 300, 300);
			return;
		}
		g.clearRect(0, 0, width, height);
		Ship currentShip = logic.getCurrentShip();
		// Current ship should be in center of screen
		int xOffset = width/2 - (int)currentShip.xC, yOffset = height/2 - (int)currentShip.yC;	// offsetting so that current ship is at the center
		g.drawImage(bg,
						0, 0, width, height,
						width/2-xOffset/BG_RATIO, height/2-yOffset/BG_RATIO, 3/2*width-xOffset/BG_RATIO, 3/2*height-yOffset/BG_RATIO, this);
		g.setColor(BORDER_COLOR);
		g.fillRect(xOffset - BORDER, yOffset - BORDER, BORDER, logicHeight + BORDER);
		g.fillRect(xOffset - BORDER, yOffset - BORDER, logicWidth + BORDER, BORDER);
		g.fillRect(xOffset + logicWidth, yOffset - BORDER, BORDER, logicHeight + (BORDER/2));	
		g.fillRect(xOffset - BORDER, yOffset + logicHeight, logicWidth + (BORDER/2), BORDER);
		
		for(Ship s : logic.getShips())
		{
			Team team = s.getTeam();
			s.draw(g, team, xOffset, yOffset);
	
		/*	Show hitboxes:
			Path2D shape = s.getCurShape();
			g.setColor(Color.BLUE);
			shape.transform(AffineTransform.getScaleInstance(BattleLogic.CELL_WIDTH, BattleLogic.CELL_HEIGHT));
			shape.transform(AffineTransform.getTranslateInstance(s.xC + xOffset, s.yC + yOffset));
			g.fill(shape); */
			
			for(Weapon w : s.getWeapons())
				w.draw(g, team, xOffset, yOffset);
		}
		
		g.setColor(Color.BLACK);
		g.fillRect(mmX, mmY, mmWidth, mmHeight);
		g.setColor(BORDER_COLOR);
		g.drawRect(mmX, mmY, mmWidth, mmHeight);
		g.setColor(Color.GREEN);
		for(Ship s : logic.getShips())
		{
			Point2D.Double ratio = logic.getRatio(s);
			if(s == currentShip)
				g.setColor(Color.WHITE);
			else
				g.setColor(s.getTeam().getColor());
			g.fillOval((int)(mmX + mmWidth * ratio.getX()) - MINIMAP_DOTS/2, (int)(mmY + mmHeight * ratio.getY()) - MINIMAP_DOTS/2,
							MINIMAP_DOTS/2, MINIMAP_DOTS/2);
		}
	}

	private class KeyHandler extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			int code = e.getKeyCode();
			
			if(code == '9')
				logic.getTeam(0).getStrategy().setState(Strategy.DEFEND, null, logic.getPlayer());
			else if(code == '0')
				logic.getTeam(0).getStrategy().setState(Strategy.ROAM, null, null);
			else if(code >= '1' && code <= '5')
				logic.getTeam(0).getStrategy().setState(Strategy.ATTACK, logic.getTeam(code - '1'), null);
			switch(code)
			{
				case KeyEvent.VK_ESCAPE:
					close();
					System.exit(0);
					return;
					
				case KeyEvent.VK_P:
					pause();
					return;
				
				case KeyEvent.VK_HOME:
					CopyOnWriteArrayList<Team> teams = Game.generate(0, difficulty);
					logic = new BattleLogic(teams, logicWidth / BattleLogic.CELL_WIDTH, logicHeight / BattleLogic.CELL_HEIGHT);
				
				case KeyEvent.VK_PAGE_DOWN:
					if(difficulty > 0)
						difficulty--;
						
				case KeyEvent.VK_PAGE_UP:
					difficulty++;
				
				default:
					if(!keyInputs.contains(code))	// If list of keyInputs does not already contain this key adds it to the list
						keyInputs.add(code);
					return;
			}
		}
		@Override
		public void keyReleased(KeyEvent e)
		{
			int code = e.getKeyCode();
			if(keyInputs.contains(code))
				keyInputs.remove(new Integer(code));	// Takes the input out of the input list
		}
	}
	
	private class MouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			mouseInputs.add(e);
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			for(MouseEvent listed : mouseInputs)
				if(listed.getButton() == e.getButton())
					mouseInputs.remove(listed);
		}
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			for(MouseEvent listed : mouseInputs)
				if(listed.getButton() == MouseEvent.BUTTON3)	// only matters for lasers, which are controlled by right click
				{
					listed.translatePoint(-listed.getX(), -listed.getY());	// moves point back to origin; no Java method to do this! dumb
					listed.translatePoint(e.getX(), e.getY());
				}
		}
	}
	
	public java.util.List<Integer> getInputs()
	{
		return keyInputs;
	}
	
	public static Dimension getDimensions()
	{
		return dimensions;
	}
	
	public void close()
	{
		logicTimer.stop();
		displayTimer.stop();
		logicTimer = null;
		displayTimer = null;
		logic = null;
	}
	
	public String[] instructions()
	{
		String[] temp =
		{
			"In this game, your team of ships attempt to be the last team standing. Use your piloting skill and tactics to come out on top.",
			"To pause, press p. W accelerates, A and D turn. In all ships, pressing space will shoot small blaster bullets.",
			"In the medium and large ships, pressing the left mouse button will (if you're dexterous) launch a missile homing at your target.",
			"In the large ships, pressing and holding the right mouse button will fire a laser.",
			"Pressing 1-5 commands your team to attack the corresponding team, while pressing 9 commands it to defend you. 0 sets it to roam freely.",
			"Pressing escape ends the game, while pressing home restarts.",
			"PgUp increases difficulty, PgDown decreases it (must restart)."
		};
		return temp;
	}
	
	public void pause()
	{
		if(paused)
			logicTimer.restart();
		else
			logicTimer.stop();
		paused = !paused;
	}
}