package Environnement;

public class Terrain {
	
	public int type;
	public int altitude;
	public int water;
	public boolean isTree;
	private boolean isOnFire = false;
	private boolean PheroPrey = false;
	private boolean PheroPred = false;
	
	public Terrain(int type,int water, int alt, boolean tree){
		this.type = type;
		this.water = water;
		this.altitude = alt;
		this.isTree = tree;
	}
	
	public Terrain(){
		this(0,0,0,false);
	}

	public boolean isOnFire() {
		return isOnFire;
	}

	public boolean isPheroPrey() {
		return PheroPrey;
	}

	public boolean isPheroPred() {
		return PheroPred;
	}
	
}
