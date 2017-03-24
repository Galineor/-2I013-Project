package Agent;

import Environnement.*;

public class Mouton extends Prey {

	public Mouton(Map world) {
		this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()));
	}
	
	public Mouton(Map world, int posx, int posy){
		super(world, 100, 120);
		this.posX = posx;
		this.posY = posy;
		this.direction = (int)(Math.random()*4);
		this.champDeVision = 3;
		this.ht = hungerTime;
		this.rt = reprodTime;
		this.parent=null;
		this.prevPosX = -1;
		this.prevPosY = -1;
	}
	
	//Regarde les alentours de la proie et engage la fuite de la proe vers une zone (peut etre) safe
	public void fuite(){
		for(Agent a: world.getAgents()){
			if(a.isPred){
				//Si le predateur se trouve dans le champ de vision
				if(a.getPosX() >= this.posX - champDeVision && a.getPosX() <= this.posX + champDeVision &&
						a.getPosY() >= this.posY - this.champDeVision && a.getPosY() <= this.posY + this.champDeVision){
					//TODO
					//Faire un algo intéressant pour la fuite avec une part d'aleatoire
					
					//Pour l'instant le mouton prend juste la direction du predateur proche pour l'eviter
					if(a.getPosX() < this.posX){
						this.direction = 1;
					}else if(a.getPosX() > this.posX){
						this.direction = 3;
					}else if(a.getPosY() < this.posY){
						this.direction = 2;
					}else if(a.getPosY() > this.posY){
						this.direction = 0;
					}
				}
			}
		}
	}
	
	public void reproduire(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && !a.isAlive){
				a.setAlive(true);
				a.setPosX(this.posX);
				a.setPosY(this.posY);
				((Prey)a).setRt(reprodTime);
				((Prey)a).setHt(hungerTime);
				a.directionPrec = -1;
				a.setAge(0);
				a.setParent(this);
				a.setPrevPosX(-1);
				a.setPrevPosY(-1);
				return;
			}
		}	
		(world).toAdd.add(new Mouton(world, this.posX, this.posY));
		
	}

	@Override
	public void Step() {
		 updatePrevPos();
		if ( Math.random() > 0.5 ) // au hasard
			direction = (direction+1) %4;
		else
			direction = (direction-1+4) %4;
		
		if(rt == 0){
			rt = reprodTime;
			reproduire();
		}
		fuite();
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
			 }
		}
		rt--;
		//ht--;
		
	}

	@Override
	public void comportementJeune() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comportementAdulte() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comportementVieux() {
		// TODO Auto-generated method stub
		
	}
}
