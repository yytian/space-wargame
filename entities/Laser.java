package game.entities;

import game.ai.Tactics;
import game.FileIO;
import game.logic.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.*;

public class Laser extends Weapon
{
	public static final int IMG_WIDTH = 10, IMG_HEIGHT = 10;
	public static final BufferedImage image = FileIO.getImage("seeker.png");
	
	public static final Path2D SHAPE = new Path2D.Float();
	static
	{
		SHAPE.moveTo(0, 2);
		SHAPE.lineTo(-10, 10);
		SHAPE.lineTo(-10, -10);
		SHAPE.lineTo(10, -10);
		SHAPE.closePath();
	}
	
	private static final double DAMAGE = 0.05;
	
	private Point target;
	
	@Override
	public void draw(Graphics2D g, Team team, int xOffset, int yOffset)
	{
		int distance = (int)Math.sqrt(Math.pow(yC - target.y, 2) + Math.pow(xC - target.x, 2));
		AffineTransform memory = g.getTransform();
		g.rotate(-dir, xC + xOffset, yC + yOffset);	// rotates laser about center of ship
		g.setColor(Color.WHITE);
		g.fillRect((int)xC + xOffset, (int)yC + yOffset, distance, 4);
		g.setTransform(memory);
	}
	
	@Override
	public boolean contact(Ship s)
	{
		if(s.contains(BattleLogic.getLocation(target)))
			s.addDamage(DAMAGE);
		return false;
	}
	
	public Weapon set(Team team, Point loc, Point target)
	{
		this.team = team;
		xC = loc.x;
		yC = loc.y;
		this.target = target;
		dir = Math.atan2(loc.y - target.y, target.x - loc.x);
		velocity = new Vector(0, 0);
		return this;
	}
	
	public long getCooldown()
	{
		return 0;
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public String getName()
	{
		return "laser";
	}
	
	public Path2D getShape()
	{
		return Weapon.DEFAULT_SHAPE;
	}
	
	public double getAcceleration()
	{
		return 0;
	}
	
	public Weapon getBlank()
	{
		return new Laser();
	}
	
	public int getDamage()
	{
		return (int)DAMAGE;
	}
	public double getDeceleration()
	{
		return 1;
	}
	public double getMaxSpeed()
	{
		return 0;
	}
	public double getRange()
	{
		return 1;
	}
	public double getTurnSpeed()
	{
		return 0;
	}
	public double getVelocity()
	{
		return 0;
	}
}