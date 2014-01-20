package game;

import game.entities.*;
import game.logic.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

public class Game extends JFrame
{
	public static final short MAXTEAMS = 4;	// not really relevant, two-player game right now
	public static final int VS_MODE = 0;		// not implemented
	public static final String[] names = {"Red", "Blue", "Green"};	// team colors (not sure why I have it as a string, perhaps unimplemented)
	public static final Color[] colors = {Color.RED, Color.BLUE, Color.GREEN}; 	// see above
	
	public Game()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setResizable(false);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int length = (int)screenSize.getWidth(), width = (int)screenSize.getHeight();
		setSize(length, width);
	}
	
	public static CopyOnWriteArrayList<Team> generate(int mode, int difficulty)
	{	// can easily add more teams, more ships, etc.
		switch(mode)
		{
			case VS_MODE:
				CopyOnWriteArrayList<Team> temp = new CopyOnWriteArrayList<Team>();
				temp.add(new Team(names[0], colors[0], 5, new Point(1000, 1000)));
				temp.add(new Team(names[1], colors[1], 5, new Point(3000, 3000)));
				Team player = temp.get(0);
				player.add(Ship.MOTHERSHIP, 1);
				player.add(Ship.DESTROYER, 3);
				player.add(Ship.FIGHTER, 10);
				
				for(int i = 1; i < temp.size(); i++)
				{
					Team t = temp.get(i);
					t.add(Ship.MOTHERSHIP, difficulty);
					t.add(Ship.DESTROYER, 1 + difficulty * 2);
					t.add(Ship.FIGHTER, 5 + difficulty * 5);
				}
				return temp;
			default:
				return null;
		}
	}
	
	public void start()
	{
		new BattleIO(generate(0, 0), 400, 400);
	}
	
	public static void main(String[] args)
	{
		Game game = new Game();
		game.start();
	}
}

// Current:
// make sure has keyboard focus?

// Future:
// make minimap icons proportional to ship sizes
// optimize graphics: http://web.archive.org/web/20080619094336/http://www.acm.org/crossroads/xrds13-3/minueto.html

// Misc:
// @Override
// sort out lasers
// import statements
// get rid of instanceof