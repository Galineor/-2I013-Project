package Sprite;


import Environnement.*;
import Agent.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SpriteDemo extends JPanel {


	private JFrame frame;

	private Image waterSprite;
	private Image grassSprite;
	private Image desertSprite;
	private Image treeSprite;
	private Image fireSprite;
	private Image ashesSprite;
	private Image rockSprite;
	private Image loupSprite;
	private Image moutonSprite;
	private Image lavaSprite;
	
	public static int spriteLength = 16;
	private static int delai = 1;
	
	private int NbDepartLoup = 20;
	private int NbDepartMouton = 65;
	
	private int tailleX =40, tailleY = 40;
	private Map myMap;

	
	private ArrayList<Agent> cloneAgent;
	public SpriteDemo()
	{
		try
		{
			//TODO Gestion des sprites dans les classes pour essayer d'avoir des sprites qui "evoluent"
			waterSprite = ImageIO.read(new File("src/water.png"));
			treeSprite = ImageIO.read(new File("src/tree.png"));
			fireSprite = ImageIO.read(new File("src/tree_fire.png"));
			ashesSprite = ImageIO.read(new File("src/tree_ashes.png"));
			rockSprite = ImageIO.read(new File("src/rock.png"));
			grassSprite = ImageIO.read(new File("src/grass.png"));
			desertSprite = ImageIO.read(new File("src/desert.png"));
			loupSprite = ImageIO.read(new File("src/wolf.png"));
			moutonSprite = ImageIO.read(new File("src/sheep.png"));
			lavaSprite = ImageIO.read(new File ("src/Lava.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		myMap = new Map(tailleX, tailleY);
//		for(int i=0; i< NbDepartLoup; i++){
//			myMap.getAgents().add(new Loup(myMap));
//		}
//		for(int i=0; i< NbDepartMouton; i++){
//			myMap.getAgents().add(new Mouton(myMap));
//		}
		
		frame = new JFrame("World of Sprite");
		frame.add(this);
		frame.setSize(tailleX*spriteLength+16, tailleY*spriteLength+40);
		frame.setVisible(true);
		
		
	}

	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < myMap.getTerrain().length ; i++ ){
			for ( int j = 0 ; j < myMap.getTerrain()[0].length ; j++ ){
				
				if ( myMap.getTerrain()[i][j].type == 0){
					//affiche plaine
					g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
					
				}else if(myMap.getTerrain()[i][j].type == 1){
					//affiche desert
					g2.drawImage(desertSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}else if(myMap.getTerrain()[i][j].type == 2){
					//affiche l eau
					g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}else if(myMap.getTerrain()[i][j].type == 4){
					g2.drawImage(lavaSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
				
				if (myMap.getTerrain()[i][j].isTree){
					if (myMap.getTerrain()[i][j].type == 0){
						//affichage arbre
						if(myMap.getTerrain()[i][j].getAFA()==0){
							g2.drawImage(treeSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
						}
						//affichage arbre en feu
						if (myMap.getTerrain()[i][j].getAFA()==1){
							g2.drawImage(fireSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
						}
						
					}else{
						//affichage des rochers dans le desert
						if (myMap.getTerrain()[i][j].type == 1){
							g2.drawImage(rockSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
						}
					}
				}
				//affichage cendres
				if (myMap.getTerrain()[i][j].getAFA()==2){
					g2.drawImage(ashesSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
			}
		}
		
		//Clone pour eviter les ConcurrentModificationException...
		cloneAgent = new ArrayList<Agent>(myMap.getAgents());
		for(Agent a : cloneAgent){
			if(a.isAlive()){
				if(a instanceof Loup){
					g2.drawImage(loupSprite,a.getSpritePosX(),a.getSpritePosY(),spriteLength,spriteLength, frame);
				}else if(a instanceof Mouton){
					g2.drawImage(moutonSprite,a.getSpritePosX(),a.getSpritePosY(),spriteLength,spriteLength, frame);
				}
			}
		}
	}
	
	public void Step(){
		myMap.Step();
		for(int i=0; i<=spriteLength; i++){
			for(Agent a : myMap.getAgents()){
				if(a.isAlive()){
					a.StepSprite();
				}
			}
			repaint();
			try {
				Thread.sleep(delai);
			} catch (InterruptedException e) {}
		}
	}

	public static void main(String[] args) {
		SpriteDemo monSpriteDemo = new SpriteDemo();
		while (true){
			monSpriteDemo.Step();
		}
		
	}
}