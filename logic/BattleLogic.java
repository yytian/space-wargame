package game.logic;

import game.ai.*;
import game.entities.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;

public class BattleLogic
{
	public static final int CELL_WIDTH = 10, CELL_HEIGHT = 10;	// size of each cell in the grid
	private static BattleLogic curLogic;	// logic instance currently used
	
	private int gridWidth, gridHeight;	// Size of the grid

	private CopyOnWriteArrayList<Team> teams = new CopyOnWriteArrayList<Team>();	// needs to be copyonwrite because.. will see later
	private CopyOnWriteArrayList<Ship> ships = new CopyOnWriteArrayList<Ship>();
	
	private Ship[] player = new Ship[1];	// player-controlled ship; capability for local multiplayer, but only one mouse so...
	
	private Team victory = null;	// whether game is over; who won?
	
	public BattleLogic(CopyOnWriteArrayList<Team> teams, int gridWidth, int gridHeight)
	{
		this.teams = teams;
		for(Team t : teams)
			for(Ship s : t.getShips())
				ships.add(s);
		player[0] = teams.get(0).getShips().get(0);
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;

		curLogic = this;
		Strategy.update();
		Tactics.update();
	}
	
	public void frame(List<Integer> keyInputs, List<MouseEvent> mouseInputs)
	{
		for(Ship s : ships)
		{
			if(s == player[0])
				s.process(keyInputs, mouseInputs, 0);	// processes user input
			else
				s.process();	// processes AI control
			s.move();
			for(Weapon w : s.getWeapons())	// moves weapons
			{
				w.move();
				for(Ship sh : ships)
					if(w.contact(sh))
						s.getWeapons().remove(w);
			}
		}
		for(Ship s : ships)
		{
			if(!s.isActive() || !isValid(getLocation(s)))	//removes inactive ships
			{
				ships.remove(s);
				s.getTeam().remove(s);
				if(s == player[0])	// if player's ship is inactive gives player a new ship
				{
					List<Ship> remShips = s.getTeam().getShips();
					if(remShips.size() > 0)
						player[0] = remShips.get(0);
				}
			}
			for(Weapon w : s.getWeapons())
				if(!isValid(getLocation(w)) || !w.inRange())
					s.getWeapons().remove(w);
		}
		for(Team t : teams)
			if(t.getShips().size() < 1)
				teams.remove(t);
		if(teams.size() < 2)
			victory = teams.get(0);
	}
	
	public void victory(Team team)
	{
		victory = team;
	}
	
	public List<Ship> getShips()
	{
		return ships;
	}
	
	public Ship getPlayer()
	{
		return player[0];
	}
	
	public Team getTeam(int i)
	{
		if(i >= 0 && i < teams.size())
			return teams.get(i);
		return null;
	}
	
	public Team getVictorious()
	{
		return victory;
	}	
	
	public Ship getCurrentShip()
	{
		return player[0];
	}
	
	public int getWidth()
	{
		return gridWidth;
	}

	public int getHeight()
	{
		return gridHeight;
	}
	
	public static Point getLocation(Entity e)	// returns grid location
	{
		Point p = e.getCoords();
		return getLocation(p);
	}
	
	public static Point getLocation(Point p)	// returns grid location given frame coordinates
	{
		return new Point(p.x / CELL_WIDTH, p.y / CELL_HEIGHT);
	}
	
	public Ship getOccupant(Point loc)	// frame coordinates, not grid
	{
		for(Ship s : ships)
			if(s.contains(toGrid(loc)))
				return s;
		return null;
	}
	
	public Point toFrame(Point p)	// not implemented; is here for symmetry's sake
	{
		return null;
	}
	
	public Point toGrid(Point p)	// converts from frame coordinates to logic grid
	{
		return new Point(p.x / CELL_WIDTH, p.y / CELL_HEIGHT);
	}
	
	public Point2D.Double getRatio(Entity e)	// for minimap
	{
		Point p = e.getCoords();
		return new Point2D.Double(p.getX() / (gridWidth * CELL_WIDTH), p.getY() / (gridHeight * CELL_HEIGHT));
	}
	
	public boolean isValid(Point loc)	// grid coordinates
	{
		return loc.x > 0 && loc.x < gridWidth && loc.y > 0 && loc.y < gridHeight;
	}
	
	public static BattleLogic curLogic()
	{
		return curLogic;
	}
}