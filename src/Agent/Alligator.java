package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.Map;
import Sprite.SpriteDemo;

public class Alligator extends Pred {
	
	private Image alligatorSprite;
	private boolean returningToWater;
	private int posAvantChasseX;
	private int posAvantChasseY;
	
	public Alligator(Map world) {
		//this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()) );
		super(world, 125, 200);
		initAttributes(this, 0, 0, null);
		placementDansEau();
		try{
			alligatorSprite = ImageIO.read(new File("src/alligator.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Alligator(Map world, int x, int y){
		this(world);
		this.setPosX(x);
		this.setPosY(y);
	}
	
	public void initAttributes(Agent a, int x, int y, Agent parent){
		a.setAlive(true);
		a.setPosX(x);
		a.setPosY(y);
		a.champDeVision = 2;
		((Pred)a).setRt(reprodTime);
		((Pred)a).setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		a.estCache = true;
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
		((Alligator)a).returningToWater = false;
	}
	
	public void placementDansEau(){
		int x = (int)(Math.random()*world.getWidth());
		int y = (int)(Math.random()*world.getHeight());
		
		//Les alligator doivent spawner dans l'eau
		while(world.getTerrain()[x][y].type != 2){
			x = (int)(Math.random()*world.getWidth());
			y = (int)(Math.random()*world.getHeight());
		}
		this.setPosX(x);
		this.setPosY(y);
	}
	
	public void afficher(Graphics2D g2, JFrame frame){
		if(getSpritePosX() == -1 || getSpritePosY() == -1){
			this.spritePosX = this.posX * SpriteDemo.spriteLength;
			this.spritePosY = this.posY * SpriteDemo.spriteLength;
		}
		g2.drawImage(alligatorSprite, this.getSpritePosX(), this.getSpritePosY(), SpriteDemo.spriteLength, SpriteDemo.spriteLength, frame);
	}

	@Override
	public void Step() {
		updatePrevPos();
		
		if(rt == 0){
			reproduire();
			rt = reprodTime;
		}
		if(ht == 0 || age == ageMort){
			this.mourir();
			return;
		}
		
		//TODO Comportement des loups : 
		//	Restent pres de l'eau
		// 	Mange les animaux qui s'approche
		// 	Longue duree de vie
		
		
		if(age<ageAdulte){
			comportementJeune();
		}else if(age<ageVieux){
			comportementAdulte();
		}else{
			comportementVieux();
		}
		
		age++;
		rt--;
		ht--;
	}
	
	public void reproduire(){
		rt = reprodTime;
		for(Agent a : world.getAgents()){
			if(a instanceof Alligator && !a.isAlive){
				initAttributes(a, this.posX, this.posY, this);
				return;
			}
		}
		world.toAdd.add(new Alligator(world, this.posX, this.posY));
	}

	@Override
	public void mourir() {
		// TODO Auto-generated method stub
		this.setAlive(false);
		
	}
	
	public void chasse(){
		//Si proie proche, l'alligator se precipite dessus
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && !a.estCache){
				if(a.posX <= this.posX+champDeVision && a.posX >= this.posX - champDeVision
						&& a.posY <= this.posY + champDeVision && a.posY >= this.posY-champDeVision){
					
					//On sauvegarde la position
					this.posAvantChasseX = this.posX;
					this.posAvantChasseY = this.posY;
					
					moveTo(a.posX, a.posY);
					manger();
					
					this.returningToWater = true;
					return;
				}
			}
		}
	}
	
	//Changement de la methode de verification de direction pour rester dans l'eau
	@Override
	public void correctDirection(){
		if(isChasing){
			return;
		}
		
		if(isOutBoundsDirection(direction) || !isWaterDirection(direction)){
			if ( Math.random() > 0.5 ){ // au hasard
				for(int i=0; i<3; i++){
					direction = (direction+1) %4;
					if(!isOutBoundsDirection(direction) && isWaterDirection(direction)){
						return;
					}
				}
			}
			else{
				for(int i=0; i<3; i++){
					direction = (direction-1+4) %4;
					if(!isOutBoundsDirection(direction) && isWaterDirection(direction)){
						return;
					}
				}
			}
			
			direction = -1;
		}
		//Ne pas sortir de l'eau
	}
	
	public boolean RiveProche(){
		for(int i=0; i<4; i++){
			if(!isOutBoundsDirection(i) && !isWaterDirection(i)){
				return true;
			}
		}	
		return false;
	}

	@Override
	public void comportementJeune() {
		//Quand ils sont jeunes, ils se deplacent partout et ne chasse pas
		deplacementAleatoire();
		correctDirection();
		move(direction, 1);
	}

	@Override
	public void comportementAdulte() {
		// Quand ils sont adultes, ils cherhent une rive sur laquelle attendre pour chasser
		
		if(this.returningToWater){
			//Si on est de retour a la position d'avant chasse, on peut reprendre le cycle normal
			if(this.posAvantChasseX == this.posX && this.posAvantChasseY == this.posY){
				returningToWater = false;
			}
			//Sinon on continue de se deplacer vers cette position
			else{
				moveToward(posAvantChasseX, posAvantChasseY, false);
			}
		}
		
		
		if(!returningToWater){
			chasse();
		}
		
		//Si on est a cote d'une rive, on ne bouge pas
		if(!returningToWater && !RiveProche()){
			correctDirection();
			move(direction, 1);
		}
		
	}

	@Override
	public void comportementVieux() {
		// TODO Auto-generated method stub
		
	}

}
