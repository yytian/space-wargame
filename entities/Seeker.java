package game.entities;

import game.ai.Tactics;
import game.FileIO;
import game.logic.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class Seeker extends Weapon
{
	public static final int IMG_WIDTH = 10, IMG_HEIGHT = 10;
	public static final BufferedImage scaledImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	static
	{
		Graphics2D g2D = scaledImage.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.drawImage(FileIO.getImage("seeker.png"), 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g2D.dispose();
	}
	
	private static final double RANGE = 1500.0, TURN_SPEED = Math.PI/25, VELOCITY = 5.0, ACCEL = 0.4, DECEL = 0.9, MAX_SPEED = 7.5;
	private static final int DAMAGE = 5;
	private static final long COOLDOWN = 1000;
	
	private Tactics tactics = new Tactics(this, 0, 0, 0, 0);
	
	@Override
	public void move()
	{
		super.move();
		tactics.moveTo();
	}
	
	@Override
	public Seeker set(Ship ship, Point loc, Point target)
	{
		Ship targeted = BattleLogic.curLogic().getOccupant(target);
		if(targeted == null)
		{
			Seeker temp = (Seeker)set(ship, loc, ship.dir);
			if(temp != null)
				temp.tactics.setTarget(target);
			return temp;
		}
		else
			return set(ship, loc, targeted);
	}
	
	@Override
	public Seeker set(Ship ship, Point loc, Entity target)
	{
		Seeker temp = (Seeker)set(ship, loc, ship.dir);
		if(temp != null)
			temp.tactics.setTarget(target);
		return temp;
	}
	
	public long getCooldown()
	{
		return COOLDOWN;
	}

	public BufferedImage getImage()
	{
		return scaledImage;
	}

	public String getName()
	{
		return "seeker";
	}
	
	public Path2D getShape()
	{
		return Weapon.DEFAULT_SHAPE;
	}
	
	public double getAcceleration()
	{
		return ACCEL;
	}
	
	public Weapon getBlank()
	{
		return new Seeker();
	}
	
	public int getDamage()
	{
		return DAMAGE;
	}
	public double getDeceleration()
	{
		return DECEL;
	}
	public double getMaxSpeed()
	{
		return MAX_SPEED;
	}
	public double getRange()
	{
		return RANGE;
	}
	public double getTurnSpeed()
	{
		return TURN_SPEED;
	}
	public double getVelocity()
	{
		return VELOCITY;
	}
}