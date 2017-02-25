package Environnement;

import java.util.ArrayList;
import Agent.Agent;

public class Map extends World {
	
	int terrain [][][];
	/*terrain[x][y][0] : 
	 * */
	
	
	protected int water [][];
	private ArrayList<Agent> toAdd;
	
	
	public Map(int dx, int dy) {
		super (dx , dy);
		terrain = new int [dx][dy][3];
		water = new int[dx][dy];

		toAdd = new ArrayList<Agent>();
		
		//for (int  x = 0; x < dx; x++){
		//	for(int y = 0; y < dy; y++){
		//		terrain[x][y][0] = (int)(Math.random()*2); //definie le type de terrain
		//		terrain[x][y][1] = 0; //definie l'altitude du terrain
		//		terrain[x][y][2] = 0; //definie les obstacles du terrain
		//	}
		//}
		for (int x = 0; x < terrain.length; x++) {
			for (int y = 0; y < terrain.length; y++) {
				if (x > terrain.length/2 && y > terrain.length/2)
					terrain[x][y][0] = 1; //definie le type de terrain
				else if (x > terrain.length/2 - 2 && y > terrain.length/2 - 2)
					terrain[x][y][0] = (int) (Math.random() * 2);
				else
					terrain[x][y][0] = 0;
				
				terrain[x][y][1] = 0; //definie l'altitude du terrain
				
			}
		}
		
		for (int x = 0; x < sizeX; x ++){
			for (int y = 0; y < water.length; y++) {
				if (Math.random() > 0.90){
					water[x][y] = (int)(Math.random() * 5) + 1; //cree de l'eau d'une profondeur aleatoire 
					terrain[x][y][2] = 1; //met de l'eau dans le terrain (condidere comme un obstacle
				}else if(Math.random() > 0.80)
					terrain[x][y][2] = 2; //definie les obstacles du terrain
				else
					terrain[x][y][2] = 0;
			}
		}
	}

	public ArrayList<Agent> getToAdd(){
		return this.toAdd;
	}
	
	//creer un deuxieme constructeur qui prends un nuage de point pour creer la map
	
	public void Step (){
		StepWorld();
		StepAgent();
	}
	
	public void StepAgent(){
		
		for(Agent a : agents){
			if (a.isAlive())
				a.Step();
		}

		agents.addAll(toAdd);
		toAdd = new ArrayList<Agent>();
	}
	
	public void StepWorld(){
		for (int x = 0; x < water.length; x++) {
			for (int y = 0; y < water[0].length; y++) {
				if (terrain[x][y][0] == 1)
					continue;
				if(water[(x - 1 + water.length) % water.length][y] > water[x][y]+1){
					terrain[x][y][2] = 1;
					water[x][y] ++;
					water[(x - 1 + water.length) % water.length][y] --;
				}else if(water[(x + 1) % water.length][y] > water[x][y]+1){
					terrain[x][y][2] = 1;
					water[x][y] ++;
					water[(x + 1) % water.length][y] --;
				}else if(water[x][(y - 1 + water.length) % water.length] > water[x][y]+1){
					terrain[x][y][2] = 1;
					water[x][y] ++;
					water[x][(y - 1 + water.length) % water.length] --;
				}else if(water[x][(y - 1 + water.length) % water.length] > water[x][y]+1){
					terrain[x][y][2] = 1;
					water[x][y] ++;
					water[x][(y - 1 + water.length) % water.length] --;
				}
			}
		}
	}
	
	public int[][][] getTerrain(){
		return this.terrain;
	}
	
	public int[][] getWater(){
		return this.water;
	}
}
