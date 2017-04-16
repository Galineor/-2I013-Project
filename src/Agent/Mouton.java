package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.*;

public class Mouton extends Prey {
	private Image moutonSprite;
	private Groupe<Mouton> troupeau;
	
	public Mouton(Map world) {
		super(world, 125, 60);
		boolean goodPlacement = false;
		
		//Tant qu'il y a de l'eau sur le spawn ou de l'eau qui va se propager a proximite, on change de spawn
		while(!goodPlacement){
			posX = (int)(Math.random()*world.getWidth());
			posY = (int)(Math.random()*world.getHeight());
			
			goodPlacement = true;
			
			// S'il y a de l'eau a proximite, on considere que c'est une mauvaise position de spawn
			
			if(world.getTerrain()[posX][posY].type == 2 
					|| (!isOutBoundsDirection(EST) && world.getTerrain()[posX+1][posY].type == 2) 
					|| (!isOutBoundsDirection(OUEST) && world.getTerrain()[posX-1][posY].type == 2) ||
					(!isOutBoundsDirection(SUD) && world.getTerrain()[posX][posY+1].type == 2) ||
					(!isOutBoundsDirection(NORD) &&  world.getTerrain()[posX][posY-1].type == 2 )){
				goodPlacement = false;
			}
			
		}
		initAttributes(this, posX, posY, null);
		
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
		a.setRt(reprodTime);
		a.setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
		a.isOnFire = false;
		a.belongPack = false;
		((Mouton)a).troupeau = null;
	}
	
	public void afficher(Graphics2D g2, JFrame frame, int spriteLength){
		if(getSpritePosX() == -1 || getSpritePosY() == -1){
			this.spritePosX = this.posX * spriteLength;
			this.spritePosY = this.posY * spriteLength;
		}
		g2.drawImage(moutonSprite, this.getSpritePosX(), this.getSpritePosY(), spriteLength, spriteLength, frame);
		if(isOnFire){
			g2.drawImage(fireSprite, this.getSpritePosX(), this.getSpritePosY(), spriteLength, spriteLength, frame);
		}
	}
	
	
	public void manger(){
		int pousse;
		
		//Si le mouton a faim
		if(ht <= hungerTime/4){
			//Alors il mange s'il y a de l'herbe sous ses pieds
			pousse = world.getTerrain()[posX][posY].getPousse();
			if(pousse>=5){
				world.getTerrain()[posX][posY].setPousse(pousse-5);
				this.setHt(hungerTime/2);
			}
		}
	}
	
	public void gestionPack(){
		for(Agent a : world.getAgents()){
			if(a instanceof Mouton && a.isAlive && !a.equals(this)){
				//Si un autre Mouton se trouve a proximite
				if(a.getPosX() >= this.posX - 2 && a.getPosX() <= this.posX + 2 &&
						a.getPosY() >= this.posY - 2 && a.getPosY() <= this.posY + 2){
					if(!this.belongPack){
						if(a.belongPack && ((Mouton)a).troupeau.groupe.size() < 5){
							((Mouton)a).troupeau.add(this);
							this.troupeau = ((Mouton)a).troupeau;
							this.belongPack = true;
						}else if(!a.belongPack){
							this.troupeau = new Groupe<Mouton>(this);
							this.troupeau.add((Mouton)a);
							this.belongPack = true;
							((Mouton)a).troupeau = this.troupeau;
							a.belongPack = true;
						}
					}
					//Si il y a 2 petites meutes a proximite, elles fusionnent
					else if(this.belongPack && a.belongPack && this.troupeau != ((Mouton)a).troupeau){
						if(this.troupeau.groupe.size() + ((Mouton)a).troupeau.groupe.size() < 5){
							for(Mouton l : this.troupeau.groupe){
								l.troupeau = ((Mouton)a).troupeau;
								((Mouton)a).troupeau.add(l);
							}
						}
					}
				}
			}
		}
	}
	
	public void quitterGroupe(){
		if(!belongPack){
			return;
		}
		
		this.belongPack = false;
		this.troupeau.groupe.remove(this);
		this.troupeau.updateLeader();
		this.troupeau = null;
	}
	
	
	//Regarde les alentours de la proie et engage la fuite de la proe vers une zone (peut etre) safe
	// Retourne true si un predateur visible se trouve a proximite
	public boolean choixDirectionAvecFuite(int champDeVision){
		//Algo de fuite
		
		//On utilise un tableau de boolean pour connaitres les directions de fuites possibles
		//On parcourt la liste des agents afin de voir ou sont les predateurs et de bloquer ces directions dans le tableau
		boolean danger = false;
		
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
				danger = true;
			}
			
			//Bloc droit
			if(a.getPosX() >= this.posX +1 && a.getPosX() <= this.posX+champDeVision && a.getPosY() >= this.posY - champDeVision
					&& a.getPosY() <= this.posY + champDeVision){
				directionPossible[1] = false;
				danger = true;
			}
			
			//Bloc haut
			if(a.getPosX() >= this.posX - champDeVision && a.getPosX() <= this.posX+champDeVision && a.getPosY() <= this.posY - 1
					&& a.getPosY() >= this.posY - champDeVision){
				directionPossible[0] = false;
				danger = true;
			}
			
			//Bloc bas
			if(a.getPosX() >= this.posX - champDeVision && a.getPosX() <= this.posX+champDeVision && a.getPosY() >= this.posY + 1
					&& a.getPosY() <= this.posY + champDeVision){
				directionPossible[2] = false;
				danger = true;
			}
		}
		
		//Tirage aleatoire de la direction a prendre parmis les valeurs true du tableau
		int alea = (int)(Math.random()*4);
		int cpt=0;
		while(cpt < 4){
			if(directionPossible[alea]){
				this.direction = alea;
				return danger;
			}
			alea = (alea+1)%4;
			cpt++;
		}
		
		//Le mouton ne peut aller nul part...
		this.direction = 0;
		return danger;
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
		if(this.belongPack){
			//On supprime les agents qui meurent de la meute
			this.troupeau.groupe.remove(this);
			this.troupeau.updateLeader();

		}
	}

	@Override
	public void Step() {
		if(ht == 0 || age == ageMort){
			this.mourir();
			return;
		}
		
		updatePrevPos();
		interactEnvironment();
		manger();
		gestionPack();
		
		if(age < ageAdulte){
			comportementJeune();
		}else if(age < ageVieux){
			comportementAdulte();
		}else{
			comportementVieux();
		}
		
		
		ht--;
		age++;
		
	}


	@Override
	public void comportementJeune() {
		int champDeVisionFuite = 3;
		if(parent != null && parent.isAlive() && !parent.belongPack){
			if(!choixDirectionAvecFuite(champDeVisionFuite)){
				//Si le mouton n'est pas en danger, il se deplace vers son parent
				if(distanceFrom(parent) > 2){
					moveToward(parent);
				}else{
					deplacementAleatoire();
					correctDirection();
					move(direction, 1);
				}
			}else{
				correctDirection();
				move(direction, 1);
			}
		}else if(belongPack && this.troupeau.leader != this){
			if(choixDirectionAvecFuite(champDeVisionFuite)){
				correctDirection();
				move(direction, 1);
			}
			else if(distanceFrom(troupeau.leader) > 2){
				moveToward(troupeau.leader);
			}
		}else{
			choixDirectionAvecFuite(champDeVisionFuite);
			correctDirection();
			move(direction, 1);
		}
		
		//Si le parent est mort on le passe a null
		if(parent != null && !parent.isAlive()){
			parent = null;
		}
	}

	@Override
	public void comportementAdulte() {
		int champDeVisionFuite = 3;
		int champDeVisionEau = 4;
		
		if(rt == 0 && belongPack){
			reproduire();
			reproduire();
			this.setRt(reprodTime);
		}
		
		if(!belongPack || this.troupeau.leader == this){
			boolean fuite = choixDirectionAvecFuite(champDeVisionFuite); //Fuis si besoin
			
			//Si on est pas en phase de fuite, on a une chance de s'approcher de l'eau s'il y en a a proximite
			if(!fuite && Math.random() < 0.3){
				int dirEau = eauAProximite(champDeVisionEau);
				//S'il y a de l'eau a proximite
				if(dirEau >= 0){
					this.direction = dirEau;
				}
			}
			correctDirection();		
			move(direction, 1);
		}
		
		else{
			if(choixDirectionAvecFuite(champDeVisionFuite)){
				correctDirection();
				move(direction, 1);
			}else{
				if(distanceFrom(troupeau.leader) > 2){
					moveToward(troupeau.leader);
				}
			}
		}
	

		if(belongPack){
			rt--;
		}
		
	}

	@Override
	public void comportementVieux() {
		
		int champDeVisionFuite = (int)(Math.random()*2)+1;
		int champDeVisionEau = 3;
		
		if(rt == 0 && belongPack){
			reproduire();
			this.setRt(reprodTime);
		}
		
		if(!belongPack || this.troupeau.leader == this){
			boolean fuite = choixDirectionAvecFuite(champDeVisionFuite); //Fuis si besoin
			
			//Si on est pas en phase de fuite, on a une chance de s'approcher de l'eau s'il y en a a proximite
			if(!fuite && Math.random() < 0.5){
				int dirEau = eauAProximite(champDeVisionEau);
				//S'il y a de l'eau a proximite
				if(dirEau >= 0){
					this.direction = dirEau;
				}
			}
			correctDirection();		
			move(direction, 1);
		}
		
		else{
			if(choixDirectionAvecFuite(champDeVisionFuite)){
				correctDirection();
				move(direction, 1);
			}else{
				if(distanceFrom(troupeau.leader) > 2){
					moveToward(troupeau.leader);
				}
			}
		}
	

		if(belongPack){
			rt--;
		}
	}
}
