package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.*;
import Sprite.SpriteDemo;

public class Loup extends Pred {

	public final int deplacement = 20;
	public final int repos = 5;
	public final int chasse = 15;

	private int cptDeplacement;
	private int cptRepos;
	private int cptChasse;
	protected Groupe<Loup> pack;

	private Image loupSprite;

	public Loup(Map world) {
		//this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()) );
		super(world, 125, 200);
		int x = (int)(Math.random()*world.getWidth());
		int y = (int)(Math.random()*world.getHeight());

		//Tant qu'il y a de l'eau sur le spawn, on change de spawn
		while(world.getTerrain()[x][y].type == 2){
			x = (int)(Math.random()*world.getWidth());
			y = (int)(Math.random()*world.getHeight());
		}
		initAttributes(this, x, y, null);

		try{
			loupSprite = ImageIO.read(new File("src/wolf.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Loup(Map world, int x, int y) {
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
		a.belongPack = false;
		a.isOnFire = false;
		((Loup)a).cptDeplacement = deplacement;
		((Loup)a).cptRepos = 0;
		((Loup)a).cptChasse = 0;
		((Loup)a).pack = null;
		((Loup)a).isChasing = false;
		
	}

	public void afficher(Graphics2D g2, JFrame frame){
		if(getSpritePosX() == -1 || getSpritePosY() == -1){
			this.spritePosX = this.posX * SpriteDemo.spriteLength;
			this.spritePosY = this.posY * SpriteDemo.spriteLength;
		}
		g2.drawImage(loupSprite, this.getSpritePosX(), this.getSpritePosY(), SpriteDemo.spriteLength, SpriteDemo.spriteLength, frame);
		if(isOnFire){
			g2.drawImage(fireSprite, this.getSpritePosX(), this.getSpritePosY(), SpriteDemo.spriteLength, SpriteDemo.spriteLength, frame);
		}
	}

	public void reproduire(){
		rt = reprodTime;
		for(Agent a : world.getAgents()){
			if(a instanceof Loup && !a.isAlive){
				initAttributes(a, this.posX, this.posY, this);
				return;
			}
		}	
		world.toAdd.add(new Loup(world, this.posX, this.posY));
	}

	public void chasser(){
		isChasing = false;
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && !a.estCache){
				if(a.posY >= this.posY - 4 && a.posY<=this.posY && a.posX == this.posX){
					this.direction = 0;
					isChasing = true;
				}
				if(a.posX <= this.posX + 4 && a.posX>=this.posX && a.posY == this.posY){
					this.direction = 1;
					isChasing = true;
				}
				if(a.posY <= this.posY + 4 && a.posY>=this.posY && a.posX == this.posX){
					this.direction = 2;			
					isChasing = true;
				}
				if(a.posX >= this.posX - 4 && a.posX<=this.posX && a.posY == this.posY){
					this.direction = 3;
					isChasing = true;
				}
			}
		}
	}

	public void mourir(){
		this.setAlive(false);
		if(this.belongPack){
			//On supprime les agents qui meurent de la meute
			this.pack.groupe.remove(this);
			if(this.pack.groupe.size() == 1){
				this.pack.groupe.get(0).belongPack = false;
				this.pack.groupe.get(0).pack = null;
				return;
			}
			this.pack.updateLeader();

		}
	}


	@Override
	public void Step() {
		updatePrevPos();
		interactEnvironment();
		
		//Si le loup a trop faim, il meurt
		if(ht <= 0 || age == ageMort){
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
	}


	public void gestionPack(){
		for(Agent a : world.getAgents()){
			if(a instanceof Loup && a.isAlive && !a.equals(this)){
				//Si un autre Loup se trouve a proximite
				if(a.getPosX() >= this.posX - 2 && a.getPosX() <= this.posX + 2 &&
						a.getPosY() >= this.posY - 2 && a.getPosY() <= this.posY + 2){
					if(!this.belongPack){
						if(a.belongPack){
							this.pack = ((Loup)a).pack;
							this.belongPack = true;
						}else if(!a.belongPack){
							this.pack = new Groupe<Loup>(this);
							this.pack.add((Loup)a);
							this.belongPack = true;
							((Loup)a).pack = this.pack;
							a.belongPack = true;
						}
					}
					//Si il y a 2 petites meutes a proximite, elles fusionnent
					else if(this.belongPack && a.belongPack && this.pack != ((Loup)a).pack){
						if(this.pack.groupe.size() + ((Loup)a).pack.groupe.size() < 10){
							for(Loup l : this.pack.groupe){
								l.pack = ((Loup)a).pack;
								((Loup)a).pack.add(l);
							}
						}
					}
				}
			}
		}
	}


	@Override
	public void comportementJeune() {

		/* 
		 * Suit le parent
		 * Pas necessaire de manger
		 * Pas de reproduction
		 * Endurance forte
		 */

		//S'il y a un parent : on le suit
		if(parent != null && parent.isAlive()){
			moveToward(parent);
		}
		//Si pas de parents, le louveteau devient adulte
		else{
			age = ageAdulte;
			comportementAdulte();
			this.cptDeplacement = deplacement;
		}


	}



	@Override
	public void comportementAdulte() {
		/*
		 * Chasse
		 * Reproduction
		 * Comportement en meute
		 * Endurance normale
		 */

		//On essaie de manger avant de se deplacer
		manger();

		//Le loup se reproduit apres reprodTime iterations
		if(rt == 0){
			reproduire();
		}

		gestionPack();

		if(!belongPack || (this.belongPack && this.pack.leader == this)){
			//On verifie si on peut creer une meute

			//Si on n'appatient pas a une meute, on regarde si on peut en creer une


			//Arbre de comportement
			if(cptDeplacement > 0){
				cptDeplacement--;
				chasser();
				if(!isChasing){
					deplacementAleatoire();
					if(cptDeplacement == 0){
						cptRepos = repos;
					}
				}else{
					cptChasse = chasse;
					cptDeplacement = 0;
				}
			}else if(cptRepos > 0){
				cptRepos--;
				chasser();
				if(!isChasing){
					direction = -1;
					if(cptRepos == 0){
						cptDeplacement = deplacement;
					}
				}else{
					cptChasse = chasse;
					cptRepos = 0;
				}
			}
			if(isChasing && cptChasse > 0){
				cptChasse--;
			}else if(isChasing && cptChasse == 0){
				isChasing = false;
				cptDeplacement = deplacement;
			}else if(!isChasing && cptChasse > 0){
				cptChasse = 0;
				cptDeplacement = deplacement;
			}

			chasser();
			correctDirection(); //Permet de corriger la direction si le deplacement n'est pas possible dans la direction actuelle
			move(direction, 1);
		}
		else if(belongPack){
			if(distanceFrom(this.pack.leader) > 2){
				moveToward(this.pack.leader);
			}
		}




		//On essaie de manger apres le deplacemnt
		manger();

		ht--;
		rt--;
	}


	@Override
	public void comportementVieux() {

		/*
		 * Moins d'endurance
		 * Plus petit champ de vision
		 * 
		 */
	}
}
