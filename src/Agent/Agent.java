package Agent;


import Environnement.*;

public abstract class Agent {
	
	protected boolean isPred;
	protected boolean isAlive;
	
	protected int posX;
	protected int posY;
	
	protected int direction;
	protected World world;
	
	public Agent(boolean isPred, World world){
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
	
	
	public boolean isObstacleDirection(int d){
		switch(d){
		case 0:
			if(((Map)world).getTerrain()[posX][(posY - 1 + world.getHeight()) % world.getHeight()][2] == 0){
				return false;
			}
			break;
		case 1:
			if(((Map)world).getTerrain()[ (posX + 1 + world.getWidth()) % world.getWidth()][posY][2] == 0){
				return false;
			}
			break;
		case 2:
			if(((Map)world).getTerrain()[posX][(posY + 1 + world.getHeight()) % world.getHeight()][2] == 0){
				return false;
			}
			break;
		case 3:
			if(((Map)world).getTerrain()[(posX - 1 + world.getWidth()) % world.getWidth()][posY][2] == 0){
				return false;
			}
			break;
		}
		
		return true;
	}



}
