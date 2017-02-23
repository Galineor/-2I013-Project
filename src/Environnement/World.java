package Environnement;

import Agent.*;
import java.util.ArrayList;

public abstract class World {
	
	protected ArrayList<Agent> agents;
	int sizeX, sizeY;
	
	public World(int sizeX, int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		
		agents = new ArrayList<Agent>();
	}
	
	public int getHeight(){
		return this.sizeY;
	}
	
	public int getWidth(){
		return this.sizeX;
	}
	
	public ArrayList<Agent> getAgents(){
		return this.agents;
	}
	
	public abstract void Step();
}
