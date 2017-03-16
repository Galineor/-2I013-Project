package Agent;
import Environnement.*;

public abstract class Prey extends Agent {
	protected int champDeVision; //Distance a laquelle la proie peut detecter un predateur
	protected int reprodTime;
	protected int hungerTime;
	protected int ht, rt; //Compteur de faim et de reproduction
	
	public Prey(Map world, int hungerTime, int reprodTime) {
		super(false, world);
		this.reprodTime = reprodTime;
		this.hungerTime = hungerTime;
	}

	public int getRt() {
		return reprodTime;
	}

	public void setRt(int reprodTime) {
		this.reprodTime = reprodTime;
	}

	public int getHt() {
		return hungerTime;
	}

	public void setHt(int hungerTime) {
		this.hungerTime = hungerTime;
	}
	
	
}
