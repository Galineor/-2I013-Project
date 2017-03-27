package Environnement;

public class Terrain {
	
	public int type; //0= Plaine 1= Desert 2= Eau 3= Terre 4= Lava 5= lave seche
	public int altitude; //altitude de la case
	public int water; //quantite d eau
	public boolean isTree; //contient un arbre ??
	private int AFA = 0; //0= Alive  1= Fire 2=Ashes
	private int pousse; //taille de l'herbe 
						//peut etre mange a 5
						//terrain devient terre si pousse == 0
	public int cptLAVA = -1;
	
	public Terrain(int type,int water, int alt, boolean tree){
		this.type = type;
		this.water = water;
		this.altitude = alt;
		this.isTree = tree;
		this.pousse = (int)(Math.random() * 5);
	}
	
	public Terrain(){
		this(0,0,0,false);
	}

	public int getAFA() {
		return AFA;
	}

	public void setAFA(int aFA) {
		AFA = aFA;
	}
	
	public int getPousse(){
		return pousse;
	}
	
	public void setPousse (int pousse){
		this.pousse = pousse;
	}
	
}
