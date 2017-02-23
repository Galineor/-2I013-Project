package Agent;

import Environnement.World;

public class Mouton extends Prey {

	public Mouton(World world) {
		super(world);
		this.posX = (int)(Math.random()*world.getWidth());
		this.posY = (int)(Math.random()*world.getHeight());
		this.direction = (int)(Math.random()*4);
	}
	

	@Override
	public void Step() {
		// TODO Auto-generated method stub
		if ( Math.random() > 0.5 ) // au hasard
			direction = (direction+1) %4;
		else
			direction = (direction-1+4) %4;

		
		// met a jour: la position de l'agent (dpend de l'orientation)
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
	}
}
