package Agent;


import Environnement.*;

public abstract class Agent {
	
	protected boolean isPred;
	protected boolean isAlive;
	
	protected int posX;
	protected int posY;
	
	protected int direction;
	protected int directionPrec; //On save la direction precedente pour ne pas retourner sur pas;
	protected Map world;
	
	public Agent(boolean isPred, Map world){
		this.direction = 0;
		this.isAlive = true;
		this.world = world;
		this.isPred = isPred;
	}
	
	public abstract void Step();


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
	
	
	public boolean isWaterDirection(int d){
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



}
