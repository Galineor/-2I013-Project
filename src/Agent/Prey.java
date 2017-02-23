package Agent;
import Environnement.*;

public abstract class Prey extends Agent {
	
	protected int reprodTime;

	public Prey(World world) {
		super(false, world);
	}
}
