package Agent;


import Environnement.*;

public abstract class Agent {
	
	protected boolean isPred;
	protected boolean isAlive;
	
	protected int posX;
	protected int posY;
	protected int prevPosX;
	protected int prevPosY;
	
	protected int spritePosX;
	protected int spritePosY;
	
	public int getSpritePosX() {
		return spritePosX;
	}

	public int getSpritePosY() {
		return spritePosY;
	}

	protected int direction;
	protected int directionPrec; //On save la direction precedente pour ne pas retourner sur pas;
	protected Map world;
	
	protected int age;
	protected Agent parent;

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

	public Agent(boolean isPred, Map world){
		this.direction = 0;
		this.isAlive = true;
		this.world = world;
		this.isPred = isPred;
		this.spritePosX = this.spritePosY = -1;
	}
	
	public abstract void Step();
	
	public void updatePrevPos(){
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		spritePosX = this.prevPosX*16;
		spritePosY = this.prevPosY*16;
	}
	
	//g2.drawImage(loupSprite,spriteLength*a.getPrevPosX()+a.getSpritePosX(),spriteLength*a.getPrevPosY() + a.getSpritePosY(),spriteLength,spriteLength, frame);
	public void StepSprite(){		
		if(prevPosX != -1 && prevPosY != -1){
			if(prevPosX != this.posX || prevPosY != this.posY){
				if(prevPosX == posX-1){
					spritePosX++;
				}else if(prevPosX == posX+1){
					spritePosX--;
				}else if(prevPosY == posY-1){
					spritePosY++;
				}else if(prevPosY == posY+1){
					spritePosY--;
				}
			}else{
				spritePosX = this.posX*16;
				spritePosY = this.posY*16;
			}
		}
	}


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

	public abstract void comportementJeune();
	
	public abstract void comportementAdulte();
	
	public abstract void comportementVieux();
	
	

}
