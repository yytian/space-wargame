package game.ai;

import game.entities.*;
import game.logic.BattleLogic;
import game.logic.Team;

public class Strategy
{
	public static final int ROAM = 0, ATTACK = 1, DEFEND = 2;	// states for strategy

	private static int logicHeight, logicWidth;	// in pixels
	
	private Team owner;
	private Team attTarget;	// target for attack
	private Ship defTarget;	// target for defense
	private int state = ROAM;	// current state
	
	public Strategy(Team owner)
	{
		this.owner = owner;
	}
	
	public void decide(Ship ship)	// decide on a target to attack
	{
		switch(state)
		{
			case ATTACK:
				if(attTarget.getShips().size() < 1)	// if target to attack doesn't actually exist anymore
				{
					state = ROAM;
					break;
				}
				for(Ship s : owner.getShips())
					s.getTactics().setTarget(nearestInTeam(s, attTarget));
				break;
			case ROAM:
				for(Ship s : owner.getShips())
					s.getTactics().setTarget(nearest(s));
			case DEFEND:
				break;
		}
	}
	
	public void setState(int state, Team attTarget, Ship defTarget)
	{
		this.state = state;
		if(state == ATTACK)
			this.attTarget = attTarget;
		else if(state == DEFEND)
		{
			this.defTarget = defTarget;
			for(Ship s : owner.getShips())
				s.getTactics().setTarget(defTarget);
		}
	}
	
	public static Ship nearest(Ship ship)
	{
		Ship temp = null;
		double distance = Double.POSITIVE_INFINITY;		
	
		for(Ship s : BattleLogic.curLogic().getShips())
		{
			double tempDist = distTo(ship, s);
			if(s.getTeam() != ship.getTeam() && tempDist < distance)
			{
				temp = s;
				distance = tempDist;
			}
		}
		return temp;
	}
	
	public static Ship nearestInTeam(Ship ship, Team team)	// Team referes to the team argument, not the owner's team
	{
		Ship temp = null;
		double distance = Double.POSITIVE_INFINITY;		
	
		for(Ship s : BattleLogic.curLogic().getShips())
		{
			double tempDist = distTo(ship, s);
			if(s.getTeam() == team && tempDist < distance)
			{
				temp = s;
				distance = tempDist;
			}
		}
		return temp;
	}
	
	public static double distTo(Entity from, Entity to)
	{
		return Math.sqrt(Math.pow(from.yC - to.yC, 2) + Math.pow(from.xC - to.xC, 2));
	}
	
	public static void update()	// updates Strategy on logic sizes
	{
		logicHeight = BattleLogic.curLogic().getHeight() * BattleLogic.CELL_HEIGHT;
		logicWidth = BattleLogic.curLogic().getWidth() * BattleLogic.CELL_WIDTH;
	}
}