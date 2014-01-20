package game.logic;

public class Vector	//various vector operations
{
	private double x, y;
	
	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void add(double x, double y)
	{
		this.x += x;
		this.y += y;
		x = Math.max(0, x);
		y = Math.max(0, y);
	}
	
	public void scale(double f)
	{
		x *= f;
		y *= f;
	}
	
	public double magnitude()
	{
		return Math.sqrt(x * x + y * y);
	}
	
	public static int quadrant(Vector v)
	{
		return quadrant(Math.atan2(v.x, v.y));
	}
	
	public static int quadrant(double angle)
	{
		while(angle < 0)
			angle += 2*Math.PI;
		if(angle < Math.PI/2)
			return 1;
		else if(angle < Math.PI)
			return 2;
		else if(angle < 3*Math.PI/2)
			return 3;
		else
			return 4;
	}
	
	public String toString()
	{
		return x + " " + y;
	}
}