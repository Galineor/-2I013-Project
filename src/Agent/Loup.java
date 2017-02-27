package Agent;

import Environnement.*;

public class Loup extends Pred {

	public Loup(World world) {
		super(world, 100, 150);
		this.posX = (int)(Math.random()*world.getWidth());
		this.posY = (int)(Math.random()*world.getHeight());
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
	}
	
	public Loup(World world, int x, int y) {
		super(world, 100, 150);
		this.posX = x;
		this.posY = y;
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
	}
	
	public boolean manger(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.getPosX() == this.posX && a.getPosY() == this.posY){
				a.setAlive(false);
				return true;
			}
		}
		return false;
	}
	
	public void reproduire(){
		for(Agent a : world.getAgents()){
			if(a.isPred && !a.isAlive){
				a.setAlive(true);
				a.setPosX(this.posX);
				a.setPosY(this.posY);
				((Pred)a).setRt(reprodTime);
				((Pred)a).setHt(hungerTime);
				return;
			}
		}	
		((Map)world).getToAdd().add(new Loup(world, this.posX, this.posY));
	}
	
	public int chasser(){
		
		for (Agent a : world.getAgents()){
			if(!a.isPred){
				if(a.posY <= this.posY - 2)
					return 0;
				if(a.posX <= this.posX + 2)
					return 1;
				if(a.posY <= this.posY + 2)
					return 2;
				if(a.posX <= this.posX - 2)
					return 3;
			}
		}
		//Le loup se deplace au hasard
		if ( Math.random() > 0.5 ) // au hasard
			return (direction+1) %4;
		else
			return (direction-1+4) %4;
	}
	
	@Override
	public void Step() {
		//On mange avant de se deplacer
		if(manger()){
			ht = hungerTime;
		}
		
		//Si le loup a trop faim, il meurt
		if(ht == 0){
			this.setAlive(false);
		}
		
		//Le loup se reproduit apres reprodTime iterations
		if(rt == 0){
			reproduire();
			rt = reprodTime;
		}
		
		direction = chasser();
		
		//Si le loup ne peut pas se deplacer dans la direction actuelle, on essaie les autres directions
		if(isObstacleDirection(direction)){
			if ( Math.random() > 0.5 ){ // au hasard
				for(int i=0; i<3; i++){
					direction = (direction+1) %4;
					if(!isObstacleDirection(direction)){
						break;
					}
				}
			}
			else{
				for(int i=0; i<3; i++){
					direction = (direction-1+4) %4;
					if(!isObstacleDirection(direction)){
						break;
					}
				}
			}	
		}
		
		// met a jour: la position de l'agent (depend de l'orientation)
		if(!isObstacleDirection(direction)){
			 switch ( direction ) 
			 {
	         	case 0: // nord
	         		posY = ( posY - 1 + world.getHeight() ) % world.getHeight();
	         		break;
	         	case 1:	// est
	         		posX = ( posX + 1 + world.getWidth() ) % world.getWidth();
	 				break;
	         	case 2:	// sud
	         		posY = ( posY + 1 + world.getHeight() ) % world.getHeight();
	 				break;
	         	case 3:	// ouest
	         		posX = ( posX - 1 + world.getWidth() ) % world.getWidth();
	 				break;
			 }
		}
		
		
		 if(manger()){
				ht = hungerTime;
			}
		 
		 ht--;
		 rt--;
	}
}
