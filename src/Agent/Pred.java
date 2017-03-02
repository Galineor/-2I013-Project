package Agent;

import Environnement.*;

public abstract class Pred extends Agent {
	protected final int hungerTime;
	protected final int reprodTime;
	
	protected int ht, rt; // Compteur de faim et de reprod
	
	public int getHt() {
		return ht;
	}

	public void setHt(int ht) {
		this.ht = ht;
	}

	public int getRt() {
		return rt;
	}

	public void setRt(int rt) {
		this.rt = rt;
	}

	public Pred(Map world, int hungerTime, int reprodTime) {
		super(true, world);
		this.hungerTime = hungerTime;
		this.reprodTime = reprodTime;
	}
	
	public abstract boolean manger();
}
