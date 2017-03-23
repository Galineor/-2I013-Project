package Environnement;

public class Terrain {
	
	public int type; //0= Plaine 1= Desert 2= Eau
	public int altitude; //altitude de la case
	public int water; //quantite d eau
	public boolean isTree; //contient un arbre ??
	private int AFA = 0; //0= Alive  1= Fire 2=Ashes
	private boolean PheroPrey = false; //une proie est passée par la ??
	private boolean PheroPred = false; //un pred est passsé par la ???
	
	public Terrain(int type,int water, int alt, boolean tree){
		this.type = type;
		this.water = water;
		this.altitude = alt;
		this.isTree = tree;
	}
	
	public Terrain(){
		this(0,0,0,false);
	}

	public boolean isPheroPrey() {
		return PheroPrey;
	}

	public boolean isPheroPred() {
		return PheroPred;
	}

	public int getAFA() {
		return AFA;
	}

	public void setAFA(int aFA) {
		AFA = aFA;
	}
	
}
