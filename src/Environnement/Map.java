package Environnement;

import java.util.ArrayList;
import Agent.Agent;

public class Map {
	
	private Terrain[][] terrain;
	private final int dx, dy;
	protected int water [][];
	private ArrayList<Agent> toAdd;
	private ArrayList<Agent> agents;
	
	
	public Map(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
		
		terrain = new Terrain[dx][dy];

		agents = new ArrayList<Agent>();
		toAdd = new ArrayList<Agent>();
		
		//for (int  x = 0; x < dx; x++){
		//	for(int y = 0; y < dy; y++){
		//		terrain[x][y][0] = (int)(Math.random()*2); //definie le type de terrain
		//		terrain[x][y][1] = 0; //definie l'altitude du terrain
		//		terrain[x][y][2] = 0; //definie les obstacles du terrain
		//	}
		//}
		
		boolean tree;
		
		for (int x = 0; x < terrain.length; x++) {
			for (int y = 0; y < terrain.length; y++) {
				if (Math.random() < 0.2)
					tree = true;
				else
					tree = false;
					
				//initialisation du terrain
				if (x > terrain.length/2 && y > terrain.length/2)
					//cree une case de desert
					terrain[x][y] = new Terrain(1, 0, 0, tree); 
				else if (x > terrain.length/2 - 2 && y > terrain.length/2 - 2)
					//cree une transition entre le desert et la foret
					terrain[x][y] = new Terrain ((int)(Math.random() * 2), 0 ,0 , false);
				else if (Math.random() < 0.05)
					//initialisation de la foret avec de l eau
					terrain[x][y] = new Terrain (2, (int)(Math.random() * 5), 0, false);
				else
					//initialisation foret
					terrain[x][y] = new Terrain(0, 0, 0, tree);
			}
		}
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
		for(int x = 0; x < dx; x++){
			for (int y = 0; y < dy; y++){
				if (!terrain[x][y].isTree){
					if (terrain[(x - 1 + dx)% dx][y].water > terrain[x][y].water && terrain[(x - 1 + dx)% dx][y].water > 1){
						terrain[x][y].water ++;
						terrain[(x - 1 + dx)% dx][y].water --;
						terrain[x][y].type = 2;
						
					}if (terrain[(x + 1)% dx][y].water > terrain[x][y].water && terrain[(x + 1)% dx][y].water > 1){
						terrain[x][y].water ++;
						terrain[(x + 1)% dx][y].water --;
						terrain[x][y].type = 2;
						
					}if(terrain[x][(y - 1 + dy)% dy].water > terrain[x][y].water && terrain[x][(y - 1 + dx)% dx].water > 1){
						terrain[x][y].water ++;
						terrain[x][(y - 1 + dy)% dy].water --;
						terrain[x][y].type = 2;
					
					}if(terrain[x][(y + 1)% dy].water > terrain[x][y].water && terrain[x][(y + 1)% dy].water > 1){
						terrain[x][y].water ++;
						terrain[x][(y + 1)% dy].water --;
						terrain[x][y].type = 2;
					}
				}
			}
		}
	}
	
	public Terrain[][] getTerrain(){
		return this.terrain;
	}
	
	public int[][] getWater(){
		return this.water;
	}
	
	public int getHeight(){
		return this.dy;
	}
	
	public int getWidth(){
		return this.dx;
	}
	
	public ArrayList<Agent> getAgents(){
		return this.agents;
	}
	
	public ArrayList<Agent> getToAdd(){
		return this.toAdd;
	}
}
