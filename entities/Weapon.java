package game.entities;

import game.logic.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.Timer;
import java.util.TimerTask;

/*
	Provides weapon prototypes to Ships; given a new Weapon(), a Ship can make instances of weapons
*/

public abstract class Weapon extends Entity
{
	protected static Path2D DEFAULT_SHAPE = new Path2D.Float();
	static
	{
		DEFAULT_SHAPE.moveTo(1, 0);
		DEFAULT_SHAPE.lineTo(1, 1);
		DEFAULT_SHAPE.lineTo(0, 1);
		DEFAULT_SHAPE.closePath();
	}

	protected Team team;
	private boolean cool = true;	// whether weapon can fire again
	private static Timer timer = new Timer();	// handles cooldown; one timer for all weapon prototypes
	private double distance = 0;	// how far the weapon has travelled
	
	public Weapon()
	{
		super(0, 0, null);	//Entity constructor
	}
	
	public void draw(Graphics2D g, Team team, int xOffset, int yOffset)
	{
		BufferedImage image = getImage();
		g.drawImage(image, new AffineTransformOp(	//rotates weapon according to orientation of ship
									  AffineTransform.getRotateInstance(-dir, image.getWidth()/2, image.getHeight()/2),
									  AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
					  (int)xC + xOffset, (int)yC + yOffset);
	}
	
	public Weapon set(Ship ship, Point loc)	// creates a weapon belonging to ship located at loc
	{
		if(!cool)
			return null;
		Weapon temp = getBlank();
		temp.team = ship.getTeam();
		temp.xC = loc.x;
		temp.yC = loc.y;
		cool = false;
		timer.schedule(new TimerTask()
			{
				public void run()
				{
					cool = true;
				}
			}, getCooldown());
		return temp;
	}
	
	public Weapon set(Ship ship, Point loc, double angle)	// above, but fired at angle
	{
		Weapon temp = set(ship, loc);
		if(temp != null)
		{
			double velocity = getVelocity() + ship.velocity.magnitude();
			temp.velocity = new Vector(Math.cos(angle) * velocity, -Math.sin(angle) * velocity);
		}
		return temp;
	}
	
	public Weapon set(Ship ship, Point loc, Point target)	// fired at point
	{
		return set(ship, loc, Math.atan2(loc.y - target.y, target.x - loc.x));
	}
	
	public Weapon set(Ship ship, Point loc, Entity target) // fired at Entity target
	{
		return set(ship, loc, Math.atan2(loc.y - target.yC, target.xC - loc.x));
	}
	
	@Override
	public void move()
	{
		super.move();
		distance += velocity.magnitude();
	}
	
	public boolean inRange()	// if weapon travels too far, self-destructs
	{
		return distance < getRange();
	}
	
	public boolean contact(Ship s)
	{
		if(s.getTeam() != team && s.contains(BattleLogic.getLocation(this)))
		{
			s.addDamage(getDamage());
			return true;
		}
		return false;
	}

	public abstract Weapon getBlank();	// blank copy of weapon
	public abstract long getCooldown();
	public abstract int getDamage();
	public abstract double getRange();
	public abstract double getVelocity();
}