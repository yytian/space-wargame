package game.entities;

import game.FileIO;
import game.logic.Team;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class Blaster extends Weapon
{
	public static final int IMG_WIDTH = 10, IMG_HEIGHT = 10;
	public static final BufferedImage scaledImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	static
	{
		Graphics2D g2D = scaledImage.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.drawImage(FileIO.getImage("blaster.png"), 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g2D.dispose();
	}
	
	private static final double RANGE = 1000, VELOCITY = 30.0, MAX_SPEED = 60.0;
	private static final int DAMAGE = 2;
	private static final long COOLDOWN = 500;
	
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
		return "blaster";
	}
	
	public Path2D getShape()
	{
		return Weapon.DEFAULT_SHAPE; 
	}
	
	public double getAcceleration()
	{
		return 0;
	}
	public double getDeceleration()
	{
		return 1;
	}
	public Weapon getBlank()
	{
		return new Blaster();
	}
	public int getDamage()
	{
		return DAMAGE;
	}
	public double getMaxSpeed()
	{
		return MAX_SPEED;
	}
	public double getRange()
	{
		return RANGE;
	}
	public double getVelocity()
	{
		return VELOCITY;
	}
	public double getTurnSpeed()
	{
		return 0;
	}
}