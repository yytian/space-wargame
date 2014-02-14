package game.entities;

import game.FileIO;
import game.logic.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public abstract class Entity	// everything is an entity
{
	public double xC, yC;	// coordinates
	public double dir; // Angle from straight to the right, 0 <= dir <= 2pi
	private double turnSpeed = getTurnSpeed(), acceleration = getAcceleration();	// obvious
	public Vector velocity;
	private Path2D shape;	// is a grid of squares
	
	public Entity(int xC, int yC, Vector velocity)
	{
		this.xC = xC;
		this.yC = yC;
		this.velocity = velocity;
		shape = getShape();
	}
	
	public void move()
	{
		velocity.scale(getDeceleration());	// deceleration is multiplicative
		xC += velocity.getX();
		yC += velocity.getY();
	}
	
	public void accelerate()
	{
		velocity.add(acceleration*Math.cos(dir), -acceleration*Math.sin(dir));	// acceleration is additive
		double maxSpeed = getMaxSpeed(), magnitude = velocity.magnitude();
		if(magnitude > maxSpeed)
			velocity.scale(maxSpeed / magnitude);
	}
	
	public void turn(int clockwise)
	{
		if(clockwise > 0)
			dir -= turnSpeed;
		else if(clockwise < 0)
			dir += turnSpeed;
		if(dir > 2*Math.PI)
			dir -= 2*Math.PI;
		else if(dir < 0)
			dir += 2*Math.PI;
	}
	
	public Point getCoords()
	{
		return new Point((int)xC, (int)yC);
	}
	
	public boolean contains(Point p)
	{
		return contains(p.x, p.y);
	}
	
	public boolean contains(int gridX, int gridY)
	{
		Path2D temp = getCurShape();
		Point gridLoc = BattleLogic.getLocation(this);
		temp.transform(AffineTransform.getTranslateInstance(gridLoc.x, gridLoc.y));
		return temp.contains(gridX, gridY);
	}
	
	public Path2D getCurShape()
	{
		Path2D clone = (Path2D)getShape().clone();
		clone.transform(AffineTransform.getRotateInstance(-dir));
		return clone;
	}
	
	public void draw(Graphics2D g, Team team, int xOffset, int yOffset)
	{
		BufferedImage image = team.getPalette().filter(getImage(), null);
		g.drawImage(image, new AffineTransformOp(
									  AffineTransform.getRotateInstance(-dir, image.getWidth()/2, image.getHeight()/2),
									  AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
					  (int)xC + xOffset - image.getWidth()/2, (int)yC + yOffset - image.getHeight()/2);
	}
	
	public abstract double getAcceleration();
	public abstract double getDeceleration();
	public abstract BufferedImage getImage();
	public abstract double getMaxSpeed();
	public abstract String getName();
	public abstract Path2D getShape();	// Returns the shape (facing right)
	public abstract double getTurnSpeed();
}