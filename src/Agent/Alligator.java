package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.Map;

public class Alligator extends Pred {
	
	private Image alligatorSprite;
	private boolean returningToWater; 
	private int posAvantChasseX;
	private int posAvantChasseY;
	
	private int cptDeplacement; // Compteur pour la phase de deplacement
	private int tempsAttenteProie; // Temps pendant lequel l'agent attendra une proie, s'il mange, le compteur est reinitialise
	
	public Alligator(Map world) {
		super(world, 300, 301);
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
		a.setRt(reprodTime);
		a.setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		a.estCache = true;
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
		a.isOnFire = false;
		((Alligator)a).returningToWater = false;
		((Alligator)a).tempsAttenteProie = 0;
		((Alligator)a).cptDeplacement = 0;
		
	}
	
	//Permet de definir des positions de l'alligator dans l'eau
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
	
	//Permet d'afficher les sprites
	public void afficher(Graphics2D g2, JFrame frame, int spriteLength){
		if(getSpritePosX() == -1 || getSpritePosY() == -1){
			this.spritePosX = this.posX * spriteLength;
			this.spritePosY = this.posY * spriteLength;
		}
		g2.drawImage(alligatorSprite, this.getSpritePosX(), this.getSpritePosY(), spriteLength, spriteLength, frame);
		if(isOnFire){
			g2.drawImage(fireSprite, this.getSpritePosX(), this.getSpritePosY(), spriteLength, spriteLength, frame);
		}
	}

	@Override
	public void Step() {
		updatePrevPos();
		interactEnvironment();
		
		if(rt == 0){
			reproduire();
			rt = reprodTime;
		}
		if(ht == 0 || age == ageMort){
			this.mourir();
			return;
		}
		
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
	
	@Override
	public void comportementJeune() {
		//Quand ils sont jeunes, ils se deplacent partout et ne chasse pas
		deplacementAleatoire();
		correctDirection();
		move(direction, 1);
		
		//Permet de varier le temps pendant lequel chaque alligator restera jeune
		age+= (int)(Math.random()*3);
	}

	@Override
	public void comportementAdulte() {
		int champDeVisionChasse = 3;
		int tempsDeplacement = 5;
		int tempsAttente = 20; //Temps d'attente avant de changer de position de chasse
		
		// Quand ils sont adultes, ils cherhent une rive sur laquelle attendre pour chasser
		
		
		//Si l'agent est entrain de retourner a sa position de chasse
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
		
		//Si l'alligator a depasser le temps d'attente maximum, il change de position
		if(tempsAttenteProie > tempsAttente){
			cptDeplacement = tempsDeplacement;
			tempsAttenteProie = 0;
		}	
		
		//Quand on est pret a chasser, on essaie de chasser
		if(!returningToWater){
			tempsAttenteProie++;
			chasse(champDeVisionChasse);
		}
		
		//Si on est a cote d'une rive, on ne bouge pas
		if((!returningToWater && !RiveProche()) || cptDeplacement > 0){
			deplacementAleatoire();
			correctDirection();
			move(direction, 1);
		}
		
		//Permet de gerer le temps pendant lequel l'alligator se deplace
		if(cptDeplacement > 0){
			cptDeplacement--;
		}
		
	}

	@Override
	public void comportementVieux() {
		int champDeVisionChasse = 2;
		
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
			chasse(champDeVisionChasse);
		}
		
		//Si on est a cote d'une rive, on ne bouge pas
		if(!returningToWater && !RiveProche()){
			correctDirection();
			move(direction, 1);
		}
		
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
		this.setAlive(false);
		
	}
	
	public void chasse(int champDeVision){
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
					
					tempsAttenteProie = 0;
					this.returningToWater = true;
					return;
				}
			}
		}
	}
	
	//Changement de la methode de verification de direction afin de rester dans l'eau
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
	
	//Detecte si une rive est dans le voisinage de Von Neumann de l'agent
	public boolean RiveProche(){
		for(int i=0; i<4; i++){
			if(!isOutBoundsDirection(i) && !isWaterDirection(i)){
				return true;
			}
		}	
		return false;
	}

}
