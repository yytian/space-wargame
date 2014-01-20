package game.entities;

import java.awt.Point;

public interface ShipAction	// encapsulates an action due to a key input
{
	public void act(Ship s);
	
	public static final ShipAction 
		ACCELERATE = new ShipAction()
			{
				public void act(Ship s)
				{
					s.accelerate();
				}
			},
		TURN_CCW = new ShipAction()
			{
				public void act(Ship s)
				{
					s.turn(-1);
				}
			},
		TURN_CW = new ShipAction()
			{
				public void act(Ship s)
				{
					s.turn(1);
				}
			},
		FIRE_BLASTER = new ShipAction()
			{
				public void act(Ship s)
				{
					for(Weapon w : s.getWeaponList())
						if(w instanceof Blaster)
						{
							Weapon copy = w.set(s, new Point((int)s.xC, (int)s.yC), s.dir);
							if(copy != null)
							{
								s.fire(copy);
								return;
							}
						}
				}
			};
}