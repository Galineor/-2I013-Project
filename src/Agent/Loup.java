package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.*;

public class Loup extends Pred {

	private int cptDeplacement;
	private int cptRepos;
	private int cptChasse;
	protected Groupe<Loup> pack;

	private Image loupSprite;
	

	public Loup(Map world) {
		super(world, 250, 251);
		
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
		((Loup)a).cptDeplacement = 0;
		((Loup)a).cptRepos = 0;
		((Loup)a).cptChasse = 0;
		((Loup)a).pack = null;
		((Loup)a).isChasing = false;
		
	}

	public void afficher(Graphics2D g2, JFrame frame, int spriteLength){
		if(getSpritePosX() == -1 || getSpritePosY() == -1){
			this.spritePosX = this.posX * spriteLength;
			this.spritePosY = this.posY * spriteLength;
		}
		
		g2.drawImage(loupSprite, this.getSpritePosX(), this.getSpritePosY(), spriteLength, spriteLength, frame);
		if(isOnFire){
			g2.drawImage(fireSprite, this.getSpritePosX(), this.getSpritePosY(),spriteLength, spriteLength, frame);
		}
		
		//Si on est en phase de repos
		if(cptRepos > 0){
			g2.drawImage(sleepSprite, this.getSpritePosX(), this.getSpritePosY(), spriteLength, spriteLength, frame);
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

	public void chasser(int champDeVision){
		isChasing = false;
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && !a.estCache){
				if(a.posY >= this.posY - champDeVision && a.posY<=this.posY && a.posX == this.posX){
					this.direction = 0;
					isChasing = true;
				}
				if(a.posX <= this.posX + champDeVision && a.posX>=this.posX && a.posY == this.posY){
					this.direction = 1;
					isChasing = true;
				}
				if(a.posY <= this.posY + champDeVision && a.posY>=this.posY && a.posX == this.posX){
					this.direction = 2;			
					isChasing = true;
				}
				if(a.posX >= this.posX - champDeVision && a.posX<=this.posX && a.posY == this.posY){
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
			this.pack.updateLeader();

		}
	}
	
	public void gestionPack(){
		for(Agent a : world.getAgents()){
			if(a instanceof Loup && a.isAlive && !a.equals(this)){
				//Si un autre Loup se trouve a proximite
				if(a.getPosX() >= this.posX - 3 && a.getPosX() <= this.posX + 3 &&
						a.getPosY() >= this.posY - 3 && a.getPosY() <= this.posY + 3){
					if(!this.belongPack){
						//S'il l'autre Loup appartient a une meute et qu'elle n'est pas trop grande, on la rejoint
						if(a.belongPack && ((Loup)a).pack.groupe.size() < 5){
							((Loup)a).pack.add(this);
							this.pack = ((Loup)a).pack;
							this.belongPack = true;
						}
						//Sinon si l'autre loup n'appartient pas a une meute, on en cree une nouvelle
						else if(!a.belongPack){
							this.pack = new Groupe<Loup>(this);
							this.pack.add((Loup)a);
							this.belongPack = true;
							((Loup)a).pack = this.pack;
							a.belongPack = true;
						}
					}
					//Si il y a 2 petites meutes a proximite, elles fusionnent
					else if(this.belongPack && a.belongPack && this.pack != ((Loup)a).pack){
						if(this.pack.groupe.size() + ((Loup)a).pack.groupe.size() < 5){
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


	public void quitterGroupe(){
		if(!belongPack){
			return;
		}
		
		this.belongPack = false;
		this.pack.groupe.remove(this);
		this.pack.updateLeader();
		this.pack = null;
	}
	
	
	//Permet de decider quelle comportement le loup doit utiliser
	public void arbreDeComportement(int tempsDeplacement, int tempsRepos, int tempsChasse){
		//Si l'arbre de comportement n'est initialise a aucune valeur, on lui indique de commencer la phase de deplacement
		if(cptDeplacement == 0 && cptRepos == 0 && cptChasse == 0){
			cptDeplacement = tempsDeplacement;
			return;
		}
		
		
		//Si l'agent phase deplacement
		if(cptDeplacement > 0){
			cptDeplacement--;
			//S'il est entrain de chasser
			if(isChasing){
				//On passe en phase de chasse
				cptDeplacement = 0;
				cptChasse = tempsChasse;
			}else{
				//S'il n'est pas entrain de chasser et que la phase de deplacement est finie, on passe en repos
				if(cptDeplacement == 0){
					cptRepos = tempsRepos;
				}
			}
		}
		//Si l'agent est en phase de repos
		else if(cptRepos > 0){
			cptRepos--;
			if(cptRepos == 0){
				cptDeplacement = tempsDeplacement;
			}
		}
		//Si s'il en phase de chasse et qu'il a toujours une proie en vue
		else if(cptChasse > 0 && isChasing){
			cptChasse--;
			//Si la phase de chasse est fini, l'agent doit se reposer
			if(cptChasse == 0){
				cptRepos = tempsRepos;
			}
		}
		
		//Si l'agent est en phase de chasse mais qu'il n'a plus de proie en vue
		else if(cptChasse > 0 && !isChasing){
			//On arrete la chasse et on relance une phase de deplacement
			cptChasse = 0;
			cptDeplacement = tempsDeplacement;
		}
	}
	
	@Override
	public void Step() {
		updatePrevPos();
		interactEnvironment();
		gestionPack();
		
	
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


	@Override
	public void comportementJeune() {

		//S'il y a un parent : on le suit
		if(parent != null && parent.isAlive()){
			moveToward(parent);
		}
		//Si pas de parents, le louveteau devient adulte
		else{
			parent = null;
			age = ageAdulte;
			comportementAdulte();
		}


	}



	@Override
	public void comportementAdulte() {
		int champDeVisionChasse = 5;
		
		int tempsDeplacement = 12;
		int tempsRepos = 5;
		int tempsChasse = 7;

		//Le loup se reproduit apres reprodTime iterations
		if(rt == 0 && belongPack){
			reproduire();
			reproduire();
			reproduire();
			rt = reprodTime;
		}


		if(!belongPack || this.pack.leader == this){
			//On verifie si on peut creer une meute

		
			arbreDeComportement(tempsDeplacement, tempsRepos, tempsChasse);
			
			
			//On regarde dans quelle phase on est et on agit en fonction
			if(cptDeplacement > 0){
				//On essaie de chasser
				chasser(champDeVisionChasse);
				
				//Si on a pas trouve de proie, on se deplace aleatoirement
				if(!isChasing){
					deplacementAleatoire();
				}
			}else if(cptRepos > 0){
				direction = NO_MOVE;
			}else if(cptChasse > 0){
				chasser(champDeVisionChasse);
				
				//Si l'agent a perdu la proie de vue, il se deplace aleatoirement
				if(!isChasing){
					deplacementAleatoire();
				}
			}

			correctDirection(); //Permet de corriger la direction si le deplacement n'est pas possible dans la direction actuelle
			move(direction, 1);
			
			//Si l'agent est entrain de chasser, on essaie de manger apres le deplacement
			if(isChasing){
				manger(); //Si l'a proie n'est pas sur la case actuelle, rien ne se passe
			}
		}
		else if(belongPack){
			if(distanceFrom(this.pack.leader) > 2){
				moveToward(this.pack.leader);
			}
		}

		ht--; //Compteur de faim
		if(belongPack){
			rt--; //Compteur de reproduction
		}
	}


	@Override
	public void comportementVieux() {
		int champDeVisionChasse = 2;
		
		int tempsDeplacement = 10;
		int tempsRepos = 10;
		int tempsChasse = 4;
		
		
		//Le loup se reproduit apres reprodTime iterations
		if(rt == 0 && belongPack){
			reproduire();
			rt = reprodTime;
		}

		if(!belongPack || this.pack.leader == this){
			//On verifie si on peut creer une meute

		
			arbreDeComportement(tempsDeplacement, tempsRepos, tempsChasse);
			
			if(cptDeplacement > 0){
				//On essaie de chasser
				chasser(champDeVisionChasse);
				
				//Si on a pas trouve de proie, on se deplace aleatoirement
				if(!isChasing){
					deplacementAleatoire();
				}
			}else if(cptRepos > 0){
				direction = NO_MOVE;
			}else if(cptChasse > 0){
				chasser(champDeVisionChasse);
				
				//Si l'agent a perdu la proie de vue, il se deplace aleatoirement
				if(!isChasing){
					deplacementAleatoire();
				}
			}

			correctDirection(); //Permet de corriger la direction si le deplacement n'est pas possible dans la direction actuelle
			move(direction, 1);
			
			//Si l'agent est entrain de chasser, on essaie de manger apres le deplacement
			if(isChasing){
				manger(); //Si l'a proie n'est pas sur la case actuelle, rien ne se passe
			}
		}
		else if(belongPack){
			if(distanceFrom(this.pack.leader) > 2){
				moveToward(this.pack.leader);
			}
		}

		ht--;
		if(belongPack){
			rt--;
		}
	}
}
