package game.entities;

import game.FileIO;
import game.logic.Team;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.ImageIcon;

public class Fighter extends Ship
{
	public static final int IMG_WIDTH = 60, IMG_HEIGHT = 40;
	private static final BufferedImage scaledImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	static
	{
		Graphics2D g2D = scaledImage.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.drawImage(FileIO.getImage("fighter.png"), 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g2D.dispose();
	}
	public static final Path2D SHAPE = new Path2D.Float();
	static
	{
		SHAPE.moveTo(2, 0);
		SHAPE.lineTo(1, 1);
		SHAPE.lineTo(-1, 1);
		SHAPE.lineTo(-1, -1);
		SHAPE.lineTo(1, -1);
		SHAPE.closePath();
	}
	public static final int HP = 35;
	public static final double ACCEL = 0.6, DECEL = 0.990, MAX_SPEED = 15.0, TURNING = (Math.PI / 30);
	
	private static final java.util.List<Map<Integer, ShipAction>> FIGHTER_INPUTS = new java.util.ArrayList<Map<Integer, ShipAction>>(DEFAULT_INPUTS);
	static
	{
		FIGHTER_INPUTS.get(0).put(KeyEvent.VK_SPACE, ShipAction.FIRE_BLASTER);
		FIGHTER_INPUTS.get(1).put(KeyEvent.VK_F, ShipAction.FIRE_BLASTER);
	}
	
	private java.util.List<Weapon> weaponList = new java.util.ArrayList<Weapon>();

	public Fighter(Team team, Point loc)
	{
		super(team, loc);
		weaponList.add(new Blaster());
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
		return FIGHTER_INPUTS;
	}
	
	public String getName()
	{
		return "fighter";
	}
	
	public java.util.List<Weapon> getWeaponList()
	{
		return weaponList;
	}
}