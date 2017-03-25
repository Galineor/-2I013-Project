package Agent;

import java.util.ArrayList;
import Environnement.*;

public class Groupe<T extends Agent> {
	
	public ArrayList<T> groupe;
	public T leader;
	
	public Groupe(T leader){
		this.leader = leader;
		this.groupe = new ArrayList<T>();
	}
	
	public void add(T member){
		this.groupe.add(member);
	}
	
	//Verifie si le leader est vivant et le change s'il est mort
	public T updateLeader(){
		if(leader.isAlive){
			return leader;
		}
		if(groupe.size() == 0){
			//La liste est vide, donc cette meute est morte
			return null;
		}
		
		//Si le leader est mort
		leader = groupe.get(0);
		return leader;
	}
}
