package Agent;

import Environnement.*;

public class Mouton extends Prey {

	public Mouton(Map world) {
		//this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()));
		super(world, 200, 125);
		int x = -1 ,y = -1;
		boolean goodPlacement = false;
		
		//Tant qu'il y a de l'eau sur le spawn ou de l'eau qui va se propager a proximite, on change de spawn
		while(!goodPlacement){
			x = (int)(Math.random()*world.getWidth());
			y = (int)(Math.random()*world.getHeight());
			
			goodPlacement = true;
			
			// S'il y a de l'eau a proximite, on considere que c'est une mauvaise position de spawn
			if(world.getTerrain()[x][y].type == 2 || world.getTerrain()[x+1][y].type == 2 || world.getTerrain()[x-1][y].type == 2 ||
					world.getTerrain()[x][y+1].type == 2|| world.getTerrain()[x][y-1].type == 2 ){
				goodPlacement = false;
			}
			
		}
		initAttributes(this, x, y, null);
		
	}
	
	public Mouton(Map world, int x, int y){
		super(world, 200, 125);
		initAttributes(this, x, y, null);
	}
	
	public void initAttributes(Agent a, int x, int y, Agent parent){
		a.setAlive(true);
		a.setPosX(x);
		a.setPosY(y);
		((Prey)a).champDeVision = 3;
		((Prey)a).setRt(reprodTime);
		((Prey)a).setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
	}
	
	//Regarde les alentours de la proie et engage la fuite de la proe vers une zone (peut etre) safe
	public void fuite(){
		for(Agent a: world.getAgents()){
			if(a.isPred && a.isAlive){
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
		rt = reprodTime;
		for(Agent a : world.getAgents()){
			if(!a.isPred && !a.isAlive){
				initAttributes(a, this.posX, this.posY, this);
				return;
			}
		}	
		world.toAdd.add(new Mouton(world, this.posX, this.posY));
		
	}
	
	public void mourir(){
		this.setAlive(false);
	}

	@Override
	public void Step() {
		updatePrevPos();
		deplacementAleatoire();
		
		if(rt == 0){
			reproduire();
		}
		
		if(ht == 0){
			this.setAlive(false);
			return;
		}
		fuite(); //Fuis si besoin
		correctDirection();		
		move(direction, 1);
		rt--;
		ht--;
		
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
