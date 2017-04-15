package Environnement;



public class Terrain {
	
	public int type; //0=Plaine 1=Desert 2=Eau 3=Terre 4=Lava 5=obsidienne
	public int altitude; //altitude de la case
	public int water; //quantite d eau
	public boolean isTree; //contient un arbre ??
	private int AFA = 0; //0= Alive  1= Fire 2=Ashes
	private int pousse = 0; //taille de l'herbe 
						//peut etre mange a 5
						//terrain devient terre si pousse == 0
	public int cptLAVA = -1; //compteur durciucement de la lave (lave => obsi)
	
	private boolean foudre = false; //presence d'un eclair
	private boolean pluie = false; //presence de pluie
	private int evap = -1; //compteur avant evaporation de l'eau de la case
	private int tmpPluie = -1; //compteur de duree de la pluie
	
	//constructeur des case du terrain
	public Terrain(int type,int water, int alt, boolean tree){
		
		this.water = water;
		this.altitude = alt;
		this.isTree = tree;
		
		if (type == 0)
			this.pousse = (int)(Math.random() * 7);
		if (type == 0 && pousse < 3)
			this.type = 3;
		else 
			this.type = type;
		
		if (type == 2){
			evap = 5;
		}
		
	}
	
	//constructeur de terrain rapide
	public Terrain(){
		this(0,0,0,false);
	}

	public int getEvap() {
		return evap;
	}

	public void setEvap(int evap) {
		this.evap = evap;
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

	public boolean isFoudre() {
		return foudre;
	}

	public void setFoudre(boolean foudre) {
		this.foudre = foudre;
	}

	
	public boolean isPluie() {
		return pluie;
	}

	public void setPluie(boolean pluie) {
		this.pluie = pluie;
	}

	public int getTmpPluie() {
		return tmpPluie;
	}

	public void setTmpPluie(int tmpPluie) {
		this.tmpPluie = tmpPluie;
	}
	
}
