package Agent;

import java.util.ArrayList;

public class Groupe<T extends Agent> {
	
	public ArrayList<T> groupe;
	public T leader;
	
	public Groupe(T leader){
		this.leader = leader;
		this.groupe = new ArrayList<T>();
		this.add(leader);
	}
	
	public void add(T member){
		this.groupe.add(member);
	}
	
	//Verifie si le leader est vivant et le change s'il est mort
	public T updateLeader(){
		if(leader.isAlive){
			return leader;
		}
		if(groupe.size() == 1){
			//Il ne reste qu'un membre donc il n'est plus en meute
			groupe.get(0).quitterGroupe();
			return null;
		}
		if(groupe.size() == 0){
			return null;
		}
		
		//Si le leader est mort, on le change
		leader = groupe.get(0);
		return leader;
	}
	
	
	//Quand un membre du groupe mange, il reparti la nourriture entre tous les membres
	public void repartitionNourriture(){
		for(T agent : groupe){
			agent.setHt(agent.hungerTime/4); 
		}
	}
}
