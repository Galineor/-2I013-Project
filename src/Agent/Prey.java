package Agent;
import Environnement.*;

public abstract class Prey extends Agent {
	protected int champDeVision; //Distance a laquelle la proie peut detecter un predateur
	
	public Prey(Map world, int hungerTime, int reprodTime) {
		super(world);
		this.isPred = false;
		this.reprodTime = reprodTime;
		this.hungerTime = hungerTime;
	}
}
