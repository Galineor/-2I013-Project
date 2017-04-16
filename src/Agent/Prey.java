package Agent;
import Environnement.*;

public abstract class Prey extends Agent {	
	protected int soif;
	
	public Prey(Map world, int hungerTime, int reprodTime) {
		super(world, hungerTime, reprodTime);
		this.isPred = false;
	}
	
	//Retourne une direction dans laquelle se trouve de l'eau proche, retourne -1 si pas d'eau trouvee
	protected int eauAProximite(int champDeVision){
		int dir = (int)(Math.random()*4); // Choix de la premiere direction a tester aleatoirement
		
		//On teste les 4 directions
		for(int i=0; i<4; i++){
			//On teste chaque case dans le champ de vision dans la direction actuelle
			for(int distance = 1; distance<=champDeVision; distance++){
				//S'il y a de l'eau dans la direction actuelle
				if(isWater(dir, distance)){
					return dir;
				}
				dir = (dir+1)%4;
			}
		}
		
		return -1;
		
	}
	
}
