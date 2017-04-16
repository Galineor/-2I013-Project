package Agent;

import Environnement.*;

public abstract class Pred extends Agent {
	protected boolean isChasing;
	
	public Pred(Map world, int hungerTime, int reprodTime) {
		super(world, hungerTime, reprodTime);
		this.isPred = true;
	}
	
	public boolean manger(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && a.getPosX() == this.posX && a.getPosY() == this.posY){
				a.mourir();;
				
				//Si l'animal est un loup qui appartient a une meute, on reparti la nourriture dans la meute
				if(belongPack && this instanceof Loup){
					((Loup)this).pack.repartitionNourriture();
				}else{
					this.ht = this.hungerTime;					
				}
				return true;
			}
		}
		return false;
	}
}
