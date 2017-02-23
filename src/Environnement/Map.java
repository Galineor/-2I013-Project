package Environnement;

import Agent.Agent;

public class Map extends World {
	
	int terrain [][][];
	
	
	public Map(int dx, int dy) {
		super (dx , dy);
		terrain = new int [dx][dy][3];
		
		for (int  x = 0; x < dx; x++){
			for(int y = 0; y < dy; y++){
				terrain[x][y][0] = (int)(Math.random()*3); //definie le type de terrain
				terrain[x][y][1] = 0; //definie l'altitude du terrain
				terrain[x][y][2] = 0; //definie les obstacles du terrain
			}
		}
	}
	
	//creer un deuxieme constructeur qui prends un nuage de point pour creer la map
	
	public void Step (){
		//StepWorld();
		StepAgent();
	}
	
	public void StepAgent(){
		
		for(Agent a : agents){
			a.Step();
		}
	}
	
	public int[][][] getTerrain(){
		return this.terrain;
	}
	
}
