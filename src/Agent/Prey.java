package Agent;
import Environnement.*;

public abstract class Prey extends Agent {
	protected int champDeVision; //Distance a laquelle la proie peut detecter un predateur
	protected int reprodTime;

	public Prey(Map world) {
		super(false, world);
	}
}
