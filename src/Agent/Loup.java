package Agent;

import Environnement.World;

public class Loup extends Pred {

	public Loup(World world) {
		super(world, 100, 150);
		this.posX = (int)(Math.random()*world.getWidth());
		this.posY = (int)(Math.random()*world.getHeight());
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
	}
	
	public Loup(World world, int x, int y) {
		super(world, 100, 150);
		this.posX = x;
		this.posY = y;
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
	}
	
	public boolean manger(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.getPosX() == this.posX && a.getPosY() == this.posY){
				a.setAlive(false);
				return true;
			}
		}
		return false;
	}
	
	public void reproduire(){
		for(Agent a : world.getAgents()){
			if(a.isPred && !a.isAlive){
				a.setAlive(true);
				a.setPosX(this.posX);
				a.setPosY(this.posY);
				((Pred)a).setRt(reprodTime);
				((Pred)a).setHt(hungerTime);
				return;
			}
		}	
		world.getToAdd().add(new Loup(world, this.posX, this.posY));
	}
	
	@Override
	public void Step() {
		if(manger()){
			ht = hungerTime;
		}
		if(ht == 0){
			this.setAlive(false);
		}
		
		if(rt == 0){
			reproduire();
			rt = reprodTime;
		}
		// TODO Auto-generated method stub
		if ( Math.random() > 0.5 ) // au hasard
			direction = (direction+1) %4;
		else
			direction = (direction-1+4) %4;

		
		// met a jour: la position de l'agent (depend de l'orientation)
		 switch ( direction ) 
		 {
         	case 0: // nord	
         		posY = ( posY - 1 + world.getHeight() ) % world.getHeight();
         		break;
         	case 1:	// est
         		posX = ( posX + 1 + world.getWidth() ) % world.getWidth();
 				break;
         	case 2:	// sud
         		posY = ( posY + 1 + world.getHeight() ) % world.getHeight();
 				break;
         	case 3:	// ouest
         		posX = ( posX - 1 + world.getWidth() ) % world.getWidth();
 				break;
		 }
		 
		 ht--;
		 rt--;
	}
}
