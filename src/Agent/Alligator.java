package Agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Environnement.Map;
import Sprite.SpriteDemo;

public class Alligator extends Pred {
	
	private Image alligatorSprite;
	
	public Alligator(Map world) {
		//this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()) );
		super(world, 125, 200);
		int x = (int)(Math.random()*world.getWidth());
		int y = (int)(Math.random()*world.getHeight());
		
		//Tant qu'il y a de l'eau sur le spawn, on change de spawn
		while(world.getTerrain()[x][y].type == 2){
			x = (int)(Math.random()*world.getWidth());
			y = (int)(Math.random()*world.getHeight());
		}
		initAttributes(this, x, y, null);
		
		try{
			alligatorSprite = ImageIO.read(new File("src/alligator.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Alligator(Map world, int x, int y){
		this(world);
		this.setPosX(x);
		this.setPosY(y);
	}
	
	public void initAttributes(Agent a, int x, int y, Agent parent){
		a.setAlive(true);
		a.setPosX(x);
		a.setPosY(y);
		((Pred)a).setRt(reprodTime);
		((Pred)a).setHt(hungerTime);
		a.direction = (int)(Math.random()*4);
		a.setAge(0);
		a.setParent(parent);
		a.setPrevPosX(-1);
		a.setPrevPosY(-1);
	}
	
	public void afficher(Graphics2D g2, JFrame frame){
		g2.drawImage(alligatorSprite, this.getSpritePosX(), this.getSpritePosY(), SpriteDemo.spriteLength, SpriteDemo.spriteLength, frame);
	}

	@Override
	public void Step() {
		// TODO Auto-generated method stub
		updatePrevPos();
		deplacementAleatoire();
		
		if(rt == 0){
			reproduire();
		}
		if(ht == 0){
			this.mourir();
			return;
		}
		
		//TODO Comportement des loups : 
		//	Restent pres de l'eau
		// 	Mange les animaux qui s'approche
		// 	Longue duree de vie
		
	}
	
	public void reproduire(){
		rt = reprodTime;
		for(Agent a : world.getAgents()){
			if(!a.isPred && !a.isAlive){
				initAttributes(a, this.posX, this.posY, this);
				return;
			}
		}	
		world.toAdd.add(new Alligator(world, this.posX, this.posY));
	}

	@Override
	public void mourir() {
		// TODO Auto-generated method stub
		this.setAlive(false);
		
	}

	@Override
	public void comportementJeune() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comportementAdulte() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comportementVieux() {
		// TODO Auto-generated method stub
		
	}

}
