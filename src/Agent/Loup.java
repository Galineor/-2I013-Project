package Agent;

import Environnement.*;

public class Loup extends Pred {
	
	private boolean isChasing;

	public Loup(Map world) {
		super(world, 100, 150);
		this.posX = (int)(Math.random()*world.getWidth());
		this.posY = (int)(Math.random()*world.getHeight());
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
		this.directionPrec = -1;
		this.isChasing = false;
	}
	
	public Loup(Map world, int x, int y) {
		super(world, 100, 150);
		this.posX = x;
		this.posY = y;
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
		this.directionPrec = -1;
		this.isChasing = false;
	}
	
	public boolean manger(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && a.getPosX() == this.posX && a.getPosY() == this.posY){
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
				a.directionPrec = -1;
				((Loup)a).isChasing = false;
				return;
			}
		}	
		(world).getToAdd().add(new Loup(world, this.posX, this.posY));
	}
		
	public void chasser(){
		for (Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive()){
				if(a.posY >= this.posY - 4 && a.posY<=this.posY && a.posX == this.posX){
					this.direction = 0;
					isChasing = true;
				}
				if(a.posX <= this.posX + 4 && a.posX>=this.posX && a.posY == this.posY){
					this.direction = 1;
					isChasing = true;
				}
				if(a.posY <= this.posY + 4 && a.posY>=this.posY && a.posX == this.posX){
					this.direction = 2;			
					isChasing = true;
				}
				if(a.posX >= this.posX - 4 && a.posX<=this.posX && a.posY == this.posY){
					this.direction = 3;
					isChasing = true;
				}
			}
		}
	}
	
	@Override
	public void Step() {
		//Si le loup a trop faim, il meurt
		if(ht <= 0){
			this.setAlive(false);
			return;
		}
		//On mange avant de se deplacer
		if(manger()){
			ht = hungerTime;
		}

		
		//Le loup se reproduit apres reprodTime iterations
		if(rt == 0){
			reproduire();
			rt = reprodTime;
		}

		
		isChasing = false; //A chaque debut de tour, on ne sait pas si le loup est entrain de chasser
		chasser();
		
		//Permet d'�viter les deplacements avant/arriere en boucle, rendant la simulation plus realiste
		if(!isChasing && directionPrec>0 && direction == (directionPrec+2)%4){
			if ( Math.random() > 0.5 ){ // au hasard
				for(int i=0; i<3; i++){
					direction = (direction+1) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}
			else{
				for(int i=0; i<3; i++){
					direction = (direction-1+4) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}
		}
		
		//Si le loup ne peut pas se deplacer dans la direction actuelle, on essaie les autres directions
		if(isWaterDirection(direction)){
			if ( Math.random() > 0.5 ){ // au hasard
				for(int i=0; i<3; i++){
					direction = (direction+1) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}
			else{
				for(int i=0; i<3; i++){
					direction = (direction-1+4) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}	
		}
		
		// met a jour: la position de l'agent (depend de l'orientation)
		if(!isWaterDirection(direction)){
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
	 			default:
	 				System.out.println("PROBLEME");
			 }
		}
		
		
		 if(manger()){
				ht = hungerTime;
			}
		 
		 ht--;
		 rt--;
		 
		 this.directionPrec = this.direction;
	}
}
