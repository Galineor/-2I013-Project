package Agent;

import Environnement.*;

public abstract class Pred extends Agent {
	public Pred(Map world, int hungerTime, int reprodTime) {
		super(world);
		this.isPred = true;
		this.hungerTime = hungerTime;
		this.reprodTime = reprodTime;
	}
	
	public boolean manger(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && a.getPosX() == this.posX && a.getPosY() == this.posY){
				a.setAlive(false);
				this.ht = this.hungerTime;
				return true;
			}
		}
		return false;
	}
}
