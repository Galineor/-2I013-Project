package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.*;
import Sprite.SpriteDemo;

public class Mouton extends Prey {
	private Image moutonSprite;
	
	
	public Mouton(Map world) {
		//this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()));
		super(world, 500, 200);
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
		
		try{
			moutonSprite = ImageIO.read(new File("src/sheep.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public Mouton(Map world, int x, int y){
		this(world);
		this.setPosX(x);
		this.setPosY(y);
	}
	
	public void initAttributes(Agent a, int x, int y, Agent parent){
		a.setAlive(true);
		a.setPosX(x);
		a.setPosY(y);
		a.champDeVision = 3;
		((Prey)a).setRt(reprodTime);
		((Prey)a).setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
	}
	
	public void afficher(Graphics2D g2, JFrame frame){
		if(getSpritePosX() == -1 || getSpritePosY() == -1){
			this.spritePosX = this.posX * SpriteDemo.spriteLength;
			this.spritePosY = this.posY * SpriteDemo.spriteLength;
		}
		g2.drawImage(moutonSprite, this.getSpritePosX(), this.getSpritePosY(), SpriteDemo.spriteLength, SpriteDemo.spriteLength, frame);
	}
	
	//Regarde les alentours de la proie et engage la fuite de la proe vers une zone (peut etre) safe
	public void fuite(){
		//Algo de fuite
		
		//On utilise un tableau de boolean pour connaitres les directions de fuites possibles
		//On parcourt la liste des agents afin de voir ou sont les predateurs et de bloquer ces directions dans le tableau
		
		boolean directionPossible[] = new boolean[4];
		for(int i=0; i<4; i++){
			directionPossible[i] = true;
		}
		
		
		//Parcourt de la liste
		for(Agent a : world.getAgents()){
			if(!a.isAlive || !a.isPred || a.estCache){
				continue;
			}
			
			
			//Bloc gauche
			if(a.getPosX() <= this.posX - 1 && a.getPosX() >= this.posX-champDeVision && a.getPosY() >= this.posY - champDeVision
					&& a.getPosY() <= this.posY + champDeVision){
				directionPossible[3] = false;
			}
			
			//Bloc droit
			if(a.getPosX() >= this.posX +1 && a.getPosX() <= this.posX+champDeVision && a.getPosY() >= this.posY - champDeVision
					&& a.getPosY() <= this.posY + champDeVision){
				directionPossible[1] = false;
			}
			
			//Bloc haut
			if(a.getPosX() >= this.posX - champDeVision && a.getPosX() <= this.posX+champDeVision && a.getPosY() <= this.posY - 1
					&& a.getPosY() >= this.posY - champDeVision){
				directionPossible[0] = false;
			}
			
			//Bloc bas
			if(a.getPosX() >= this.posX - champDeVision && a.getPosX() <= this.posX+champDeVision && a.getPosY() >= this.posY + 1
					&& a.getPosY() <= this.posY + champDeVision){
				directionPossible[2] = false;
			}
		}
		
		//Tirage aleatoire de la direction a prendre parmis les valeurs true du tableau
		int alea = (int)(Math.random()*4);
		int cpt=0;
		while(cpt < 4){
			if(directionPossible[alea]){
				this.direction = alea;
				return;
			}
			alea = (alea+1)%4;
			cpt++;
		}
		
		//Le mouton ne peut aller nul part...
		this.direction = 0;
	}
	
	public void reproduire(){
		rt = reprodTime;
		for(Agent a : world.getAgents()){
			if(a instanceof Mouton && !a.isAlive){
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
			this.mourir();
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
