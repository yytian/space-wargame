package game.ai;

import game.entities.*;
import game.logic.*;
import java.awt.Point;
import java.util.List;

public class Tactics
{
	private double anglePrecision = Math.PI/9;	// how close to precise angle the ship needs to come
	private int borderSafety = 50;	// number of ticks to hit border
	private int speedGoal = 50;	// number of ticks to hit target
	private int fireRange = 750;	// Range at which ship starts firing

	private static int logicHeight, logicWidth;	// in pixels
	private Entity owner, target;	//obvious
	private int destX, destY;	//coordinates to move to (frame, not grid)
	
	public Tactics(Entity owner, double angleP, int borderS, int speedG, int fireR)
	{
		this.owner = owner;
		anglePrecision = angleP;
		borderSafety = borderS;
		speedGoal = speedG;
		fireRange = fireR;
	}
	
	public void setTarget(Entity target)
	{
		this.target = target;
		destX = (int)target.xC;
		destY = (int)target.yC;
	}
	
	public void setTarget(Point target)
	{
		destX = target.x;
		destY = target.y;
	}
	
	public void decide()	// acts for the turn
	{
		if(!checkBorders() && target != null)
		{
			destX = (int)target.xC;
			destY = (int)target.yC;
		}
		moveTo();
	}
	
	public void fireControl(List<Weapon> weapons, Entity fireTarget)	// decides whether to fire at a target
	{
		Ship shipOwner = (Ship)owner;
		if(fireTarget != null && Strategy.distTo(shipOwner, fireTarget) < fireRange)
			for(Weapon w : weapons)
			{
				if(w instanceof Laser)
					shipOwner.fire(((Laser)w).set(shipOwner.getTeam(), new Point((int)shipOwner.xC, (int)shipOwner.yC), fireTarget.getCoords()));
				else
					shipOwner.fire(w.set((Ship)shipOwner, new Point((int)shipOwner.xC, (int)shipOwner.yC), fireTarget));
			}
	}
	
	public void moveTo()
	{
		owner.turn(changeDir());
		if(distTo() / owner.velocity.magnitude() > speedGoal)
			owner.accelerate();
	}
	
	public int changeDir()
	{
		double angle = Math.atan2(owner.yC - destY, destX - owner.xC);	// angle necessary
		if(angle < 0)	// make it 0 <= angle <= 2pi
			angle += 2*Math.PI;
		
		double tempDir = owner.dir;	// current direction of entity

		if(angle > tempDir)
		{
			if(angle - tempDir < anglePrecision)	// close enough
				return (int)((Math.random() - 0.5)*5);
			else if(angle - tempDir < Math.PI)	// counterclockwise is better
				return -1;
			return 1;	// clockwise is better
		}
		else
		{
			if(tempDir - angle < anglePrecision)	// close enough
				return (int)((Math.random() - 0.5)*5);
			else if(tempDir - angle < Math.PI)	// cw is better
				return 1;
			return -1;	// ccw is better
		}
	}
	
	public boolean checkBorders()
	{
		double up = -owner.yC / owner.velocity.getY(), left = -owner.xC / owner.velocity.getX();	// danger of respective border
		double down = (logicHeight - owner.yC) / owner.velocity.getY(), right = (logicWidth - owner.xC) / owner.velocity.getX();
		
		boolean acted = false;	// whether anything was changed (return value for this method)
		
		if(Math.abs(up) < borderSafety)
		{
			destY = logicHeight;
			acted = true;
		}
		else if(Math.abs(down) < borderSafety)
		{
			destY = 0;
			acted = true;
		}
		if(Math.abs(left) < borderSafety)
		{
			destX = logicWidth;
			acted = true;
		}
		else if(Math.abs(right) < borderSafety)
		{
			destX = 0;
			acted = true;
		}
		return acted;
	}
	
	public double distTo()	// distance to destination
	{
		return Math.sqrt(Math.pow(owner.yC - destY, 2) + Math.pow(owner.xC - destX, 2));
	}
	
	public static void update()	// updates Tactics on logic sizes
	{
		logicHeight = BattleLogic.curLogic().getHeight() * BattleLogic.CELL_HEIGHT;
		logicWidth = BattleLogic.curLogic().getWidth() * BattleLogic.CELL_WIDTH;
	}
}