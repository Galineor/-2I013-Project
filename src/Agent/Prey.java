package Agent;
import Environnement.*;

public abstract class Prey extends Agent {	
	public Prey(Map world, int hungerTime, int reprodTime) {
		super(world, hungerTime, reprodTime);
		this.isPred = false;
	}
}
