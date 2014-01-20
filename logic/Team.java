package game.logic;

import game.ai.Strategy;
import game.entities.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.*;
import java.util.*;

public class Team
{
/* unimplemented ideas

	public static final short MAXSQUADS = 10;
	private final List<Squad> squadList = new ArrayList<Squad>(MAXSQUADS);
	private boolean active = false;
	
	public boolean addSquad(Squad squad)
	{
		if(squadList.size() <= MAXSQUADS)
		{
			squadList.add(squad);
		}
		return false;
	}
	
	public void setActive(boolean change)
	{
		active = change;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public Squad getSquad(int number)
	{
		return squadList.get(number);
	}*/
	
	private String name;
	private List<Ship> ships;
	private Point spawnLoc;		//ships belonging to this team spawn around here
	private Color color;
	private LookupOp palette;	//http://stackoverflow.com/questions/23763/colorizing-images-in-java
	
	private Strategy strategy = new Strategy(this);	// team strategy
	
	public Team(String name, Color color, int num, Point spawnLoc)
	{
		this.name = name;
		ships = new ArrayList<Ship>();
		this.color = color;
		this.spawnLoc = spawnLoc;
		
		short[] alpha = new short[256];	// handles palettization for this team's ships; turns white areas of entity image into team color,
													// black into transparency
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		int redVal = color.getRed(), greenVal = color.getGreen(), blueVal = color.getBlue();
		
		for(short i = 0; i < 256; i++)
		{
			int val = i/255;
			alpha[i] = i;
			red[i] = (short)(val*redVal);
			green[i] = (short)(val*greenVal);
			blue[i] = (short)(val*blueVal);
		}
		
		short[][] data = new short[][]
		{
			red, green, blue, alpha
		};
		
		LookupTable lookupTable = new ShortLookupTable(0, data);
		palette = new LookupOp(lookupTable, null);
	}
	
	public void add(int code, int num)
	{
		for(int i = 0; i < num; i++)
			ships.add(Ship.getInstance(code, this, spawnLoc));
	}
	
	public void remove(Ship ship)
	{
		ships.remove(ship);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public LookupOp getPalette()
	{
		return palette;		
	}
	
	public List<Ship> getShips()
	{
		return ships;
	}
	
	public Strategy getStrategy()
	{
		return strategy;
	}
	
	public String toString()
	{
		return name;
	}
}