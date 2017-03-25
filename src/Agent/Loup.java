package Agent;

import java.util.Iterator;

import Environnement.*;

public class Loup extends Pred {

	private boolean isChasing;
	public final int ageAdulte = 40;
	public final int ageVieux = 100;
	public final int deplacement = 20;
	public final int repos = 5;
	public final int chasse = 15;

	private int cptDeplacement;
	private int cptRepos;
	private int cptChasse;

	public Loup(Map world) {
		this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()) );
	}

	public Loup(Map world, int x, int y) {
		super(world, 100, 180);
		initAttributes(this, x, y, null);
	}

	public void initAttributes(Agent a, int x, int y, Agent parent){
		a.setAlive(true);
		a.setPosX(x);
		a.setPosY(y);
		((Pred)a).setRt(reprodTime);
		((Pred)a).setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		((Loup)a).isChasing = false;
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
		((Loup)a).cptDeplacement = deplacement;
		((Loup)a).cptRepos = 0;
		((Loup)a).cptChasse = 0;
	}

	public void reproduire(){
		rt = reprodTime;
		for(Agent a : world.getAgents()){
			if(a.isPred && !a.isAlive){
				initAttributes(a, this.posX, this.posY, this);
				return;
			}
		}	
		(world).toAdd.add(new Loup(world, this.posX, this.posY));
	}

	public void chasser(){
		isChasing = false;
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive()){
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


	@Override
	public void Step() {
		updatePrevPos();
		//Si le loup a trop faim, il meurt

		if(ht <= 0){
			this.setAlive(false);
			return;
		}
		if(age<40){
			comportementJeune();
		}else{
			comportementAdulte();
		}
		age++;
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
