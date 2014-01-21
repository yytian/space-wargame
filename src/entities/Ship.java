package game.entities;

import game.ai.*;
import game.logic.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

public abstract class Ship extends Entity
{
	public static final int FIGHTER = 0, DESTROYER = 1, MOTHERSHIP = 2;	// reference codes
	private static final int SPAWN_PRECISION = 500;	// Radius around spawn point for spawning purposes
	
	private Team team;
	private int hp = getHP();
	
	private boolean active = true;
	
	protected static final List<Map<Integer, ShipAction>> DEFAULT_INPUTS = new ArrayList<Map<Integer, ShipAction>>();
	static
	{
		Map<Integer, ShipAction> input0 = new HashMap<Integer, ShipAction>();	// if controlled by first player
		input0.put(KeyEvent.VK_W, ShipAction.ACCELERATE);
		input0.put(KeyEvent.VK_A, ShipAction.TURN_CCW);
		input0.put(KeyEvent.VK_D, ShipAction.TURN_CW);
		
		Map<Integer, ShipAction> input1 = new HashMap<Integer, ShipAction>();	// if controlled by second player (unused)
		input1.put(KeyEvent.VK_UP, ShipAction.ACCELERATE);
		input1.put(KeyEvent.VK_LEFT, ShipAction.TURN_CCW);
		input1.put(KeyEvent.VK_RIGHT, ShipAction.TURN_CW);
		DEFAULT_INPUTS.add(input0);
		DEFAULT_INPUTS.add(input1);
	}
	
	private CopyOnWriteArrayList<Weapon> curWeapons = new CopyOnWriteArrayList<Weapon>();	// weapons currently in play
	
	private Tactics tactics = new Tactics(this, Math.PI/9, 50, 50, 750);
	
	public Ship(Team team, Point loc)
	{
		super((int) (loc.x + (Math.random() - 0.5)*SPAWN_PRECISION),
				(int) (loc.y + (Math.random() - 0.5)*SPAWN_PRECISION),
				new game.logic.Vector(0, 0));
		this.team = team;
	}
	
	public void process()	// AI processing
	{
		for(Weapon w : curWeapons)	// lasers only last one frame (are fired continuously)
			if(w instanceof Laser)
				curWeapons.remove(w);
		team.getStrategy().decide(this);
		tactics.decide();
		tactics.fireControl(getWeaponList(), Strategy.nearest(this));
	}
	
	public void process(List<Integer> keyInputs, List<MouseEvent> mouseInputs, int player)	// Player input processing
	{
		for(Integer keyCode : keyInputs)
		{
			ShipAction action = getInput().get(player).get(keyCode);
			if(action != null)
				action.act(this);
		}
		
		for(Weapon w : curWeapons)
			if(w instanceof Laser)
				curWeapons.remove(w);
				
		Dimension dimensions = BattleIO.getDimensions();
		for(MouseEvent e : mouseInputs)
		{
			Point point = e.getPoint();
			switch(e.getButton())
			{
				case MouseEvent.BUTTON1:	// left button controls seeker missiles
					for(Weapon w : getWeaponList())
						if(w instanceof Seeker)
							fire(w.set(this,
									  new Point((int)xC, (int)yC),
									  new Point((int)(point.x - dimensions.getWidth()/2 + xC), (int)(point.y - dimensions.getHeight()/2 + yC))));
					break;
				case MouseEvent.BUTTON3:	// right button controls lasers
					for(Weapon w : getWeaponList())
						if(w instanceof Laser)
							fire(((Laser)w).set(team,
												new Point((int)xC, (int)yC),
												new Point((int)(point.x - dimensions.getWidth()/2 + xC), (int)(point.y - dimensions.getHeight()/2 + yC))));
					break;
			}
		}
	}
	
	public void addDamage(double damage)	// if ship is hit by a weapon
	{
		hp -= damage;
		if(hp <= 0)	// ship is destroyed
			active = false;
	}
	
	public void fire(Weapon weapon)
	{
		if(weapon != null)
			curWeapons.add(weapon);
	}
	
	public List<Map<Integer, ShipAction>> getInput()
	{
		return DEFAULT_INPUTS;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public List<Weapon> getWeapons()
	{
		return curWeapons;
	}
	
	public Tactics getTactics()
	{
		return tactics;
	}
	
	public static Ship getInstance(int code, Team team, Point loc)
	{
		switch(code)
		{
			case 0:
				return new Fighter(team, loc);
			case 1:
				return new Destroyer(team, loc);
			case 2:
				return new Mothership(team, loc);
			default:
				return null;
		}
	}
	
	public abstract int getHP();
	public abstract List<Weapon> getWeaponList();	// weapons that the ship possesses (has the capability to fire)
	
}