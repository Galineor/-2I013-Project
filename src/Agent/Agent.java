package Agent;


import java.awt.Graphics2D;

import javax.swing.JFrame;

import Environnement.*;
import Sprite.*;

public abstract class Agent {
	
	protected boolean isPred;
	protected boolean isAlive;
	
	protected int posX;
	protected int posY;
	protected int prevPosX;
	protected int prevPosY;
	
	protected int spritePosX;
	protected int spritePosY;
	
	protected boolean belongPack;

	protected int direction;
	protected Map world;
	
	protected int age;
	protected Agent parent;
	
	protected int reprodTime;
	protected int hungerTime;
	protected int ht, rt; //Compteur de faim et de reproduction
	
	protected boolean isOnFire;

	public Agent(Map world){
		this.direction = 0;
		this.isAlive = true;
		this.world = world;
		this.spritePosX = this.spritePosY = -1;
		this.belongPack = false; // De base aucun agent n'appartiennent a un groupe
		this.isOnFire = false;
	}
	
	public abstract void Step();
	
	public void updatePrevPos(){
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		spritePosX = this.prevPosX*16;
		spritePosY = this.prevPosY*16;
	}
	
	public void StepSprite(){
		//TODO Gerer les animations de deplacement torique
		if(prevPosX != -1 && prevPosY != -1){
			if(prevPosX != this.posX || prevPosY != this.posY){
				if(prevPosX < posX){
					if(direction == 1)
						spritePosX += posX - prevPosX;
				} 
				if(prevPosX > posX){
					if(direction == 3)
						spritePosX -= prevPosX - posX;
				} 
				if(prevPosY < posY){
					if(direction == 2)
						spritePosY += posY - prevPosY;
				} 
				if(prevPosY > posY){
					if(direction == 0)
						spritePosY -= prevPosY - posY;
				}
			}else{
				spritePosX = this.posX*SpriteDemo.spriteLength;
				spritePosY = this.posY*SpriteDemo.spriteLength;
			}
		}
	}
	
	public double distanceFrom(Agent a){
		return Math.sqrt(Math.pow(this.posX - a.posX, 2) + Math.pow(this.posY - a.posY, 2));
	}
	
	public void deplacementAleatoire(){
		//Si pas de direction, on en choisie une aleatoire
		if(direction == -1){
			direction = (int)(Math.random()*4);
			return;
		}
		
		if(Math.random() < 0.3){
			if ( Math.random() > 0.5 ) // au hasard
				direction = (direction+1) %4;
			else
				direction = (direction-1+4) %4;
		}
	}
	
	public void move(int direction, int distance){
		if(direction == -1){
			return;
		}
		 switch ( direction ) 
		 {
         	case 0: // nord
         		posY = ( posY - distance + world.getHeight() ) % world.getHeight();
         		break;
         	case 1:	// est
         		posX = ( posX + distance + world.getWidth() ) % world.getWidth();
 				break;
         	case 2:	// sud
         		posY = ( posY + distance + world.getHeight() ) % world.getHeight();
 				break;
         	case 3:	// ouest
         		posX = ( posX - distance + world.getWidth() ) % world.getWidth();
 				break;
		 }
		
	}
	
	public void moveToward(Agent a){
		if(a.posX > this.posX){
			direction = 1;
		}else if(a.posX < this.posX){
			direction = 3;
		}else if(a.posY > this.posY){
			direction = 2;
		}else if(a.posY < this.posY){
			direction = 0;
		}else{
			direction = -1;
		}
		correctDirection();
		move(direction, 1);
	}
	
	public void correctDirection(){
		//Si l'agent ne peut pas se deplacer dans la direction actuelle, on essaie les autres directions
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
			
			if(isWaterDirection(direction)){
				//L'agent est entouré d'eau, il ne peut pas se deplacer
				direction = -1;
			}
		}
	}
	
	public boolean isWaterDirection(int d){
		if(d == -1){
			return false;
		}
		
		switch(d){
		case 0:
			if(world.getTerrain()[posX][(posY - 1 + world.getHeight()) % world.getHeight()].type == 2){
				return true;
			}
			break;
		case 1:
			if(world.getTerrain()[ (posX + 1 + world.getWidth()) % world.getWidth()][posY].type== 2){
				return true;
			}
			break;
		case 2:
			if(world.getTerrain()[posX][(posY + 1 + world.getHeight()) % world.getHeight()].type == 2){
				return true;
			}
			break;
		case 3:
			if(world.getTerrain()[(posX - 1 + world.getWidth()) % world.getWidth()][posY].type == 2){
				return true;
			}
			break;
		}
		
		return false;
	}
	
	public void setOnFire(){
		this.isOnFire = true;
		//TODO init compteur de feu pour mort
	}
	
	public void interactEnvironment(){
		Terrain terrain = world.getTerrain()[posX][posY];
		
		//Si l'agent se trouve sur un arbre en feu, il prend feu
		if(terrain.type == 4){
			this.setOnFire();
			return;
		}
		
		//Si l'agent se trouve sur de la lave, il meurt
		else if(terrain.type == 0 && terrain.isTree && terrain.getAFA()==1){
			this.mourir();
			return;
		}
		
		//On verifie si les cases autour sont de la lave, si oui, l'agent prend feu
		terrain = world.getTerrain()[posX+1][posY];
		if(terrain.type == 0 && terrain.isTree && terrain.getAFA()==1){
			this.setOnFire();
		}
		terrain = world.getTerrain()[posX-1][posY];
		if(terrain.type == 0 && terrain.isTree && terrain.getAFA()==1){
			this.setOnFire();
		}
		terrain = world.getTerrain()[posX][posY+1];
		if(terrain.type == 0 && terrain.isTree && terrain.getAFA()==1){
			this.setOnFire();
		}
		terrain = world.getTerrain()[posX][posY-1];
		if(terrain.type == 0 && terrain.isTree && terrain.getAFA()==1){
			this.setOnFire();
		}
	}
	
	public abstract void afficher(Graphics2D g2, JFrame frame);
	
	public abstract void mourir();

	public int getPrevPosX() {
		return prevPosX;
	}

	public void setPrevPosX(int prevPosX) {
		this.prevPosX = prevPosX;
	}

	public int getPrevPosY() {
		return prevPosY;
	}

	public void setPrevPosY(int prevPosY) {
		this.prevPosY = prevPosY;
	}

	public boolean isPred() {
		return isPred;
	}


	public void setPred(boolean isPred) {
		this.isPred = isPred;
	}


	public boolean isAlive() {
		return isAlive;
	}


	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}


	public int getPosX() {
		return posX;
	}


	public void setPosX(int posX) {
		this.posX = posX;
	}


	public int getPosY() {
		return posY;
	}


	public void setPosY(int posY) {
		this.posY = posY;
	}


	public int getDirection() {
		return direction;
	}


	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getHt() {
		return ht;
	}

	public void setHt(int ht) {
		this.ht = ht;
	}

	public int getRt() {
		return rt;
	}

	public void setRt(int rt) {
		this.rt = rt;
	}
	
	public Agent getParent() {
		return parent;
	}

	public void setParent(Agent parent) {
		this.parent = parent;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public int getSpritePosX() {
		return spritePosX;
	}

	public int getSpritePosY() {
		return spritePosY;
	}

	public abstract void comportementJeune();
	
	public abstract void comportementAdulte();
	
	public abstract void comportementVieux();
	
	

}
