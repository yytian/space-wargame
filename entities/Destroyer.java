package game.entities;

import game.FileIO;
import game.logic.Team;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.ImageIcon;

public class Destroyer extends Ship
{
	public static final int IMG_WIDTH = 100, IMG_HEIGHT = 100;
	private static final BufferedImage scaledImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	static
	{
		Graphics2D g2D = scaledImage.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.drawImage(FileIO.getImage("destroyer.png"), 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g2D.dispose();
	}
	
	public static final Path2D SHAPE = new Path2D.Float();
	static
	{
		SHAPE.moveTo(4, 4);
		SHAPE.lineTo(-4, 4);
		SHAPE.lineTo(-4, -4);
		SHAPE.lineTo(4, -4);
		SHAPE.closePath();
	}
	
	private static final java.util.List<Map<Integer, ShipAction>> DESTROYER_INPUTS = new java.util.ArrayList<Map<Integer, ShipAction>>(DEFAULT_INPUTS);
	static
	{
		 DESTROYER_INPUTS.get(0).put(KeyEvent.VK_SPACE, ShipAction.FIRE_BLASTER);
		 DESTROYER_INPUTS.get(1).put(KeyEvent.VK_F, ShipAction.FIRE_BLASTER);
	}
	
	public static final int HP = 50;
	public static final double ACCEL = 0.35, DECEL = 0.993, MAX_SPEED = 8.5, TURNING = (Math.PI / 75);
	
	private java.util.List<Weapon> weaponList = new java.util.ArrayList<Weapon>();

	public Destroyer(Team team, Point loc)
	{
		super(team, loc);
		for(int i = 0; i < 3; i++)
			weaponList.add(new Blaster());
		weaponList.add(new Seeker());
	}
	
	public Path2D getShape()
	{
		return SHAPE;
	}

	public double getAcceleration()
	{
		return ACCEL;
	}
	
	public double getDeceleration()
	{
		return DECEL;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	public double getMaxSpeed()
	{
		return MAX_SPEED;
	}
	
	public double getTurnSpeed()
	{
		return TURNING;
	}
	
	public BufferedImage getImage()
	{
		return scaledImage;
	}
	
	public java.util.List<Map<Integer, ShipAction>> getInput()
	{
		return DESTROYER_INPUTS;
	}
	
	public String getName()
	{
		return "destroyer";
	}
	
	public java.util.List<Weapon> getWeaponList()
	{
		return weaponList;
	}
}