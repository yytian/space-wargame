package game.logic;

import game.entities.Ship;
import java.util.*;

public class Squad
{
	public static final short MAXSHIPS = 30;
	private final List<Ship> shipList = new ArrayList<Ship>(MAXSHIPS);
	
	public boolean addAll(Ship[] list)
	{
		for(Ship ship : list)
		{
			if(shipList.size() <= MAXSHIPS)
				shipList.add(ship);
			else
				return false;
		}
		return true;
	}
}