package Sprite;


import Environnement.*;
import Agent.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

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
	private Image loupSprite;
	private Image moutonSprite;
	
	private int spriteLength = 16;
	private static int delai = 500;
	
	private int tailleX =50, tailleY = 50;
	private Map myMap;

	public SpriteDemo()
	{
		try
		{
			waterSprite = ImageIO.read(new File("src/water.png"));
			treeSprite = ImageIO.read(new File("src/tree.png"));
			grassSprite = ImageIO.read(new File("src/grass.png"));
			desertSprite = ImageIO.read(new File("src/desert.png"));
			loupSprite = ImageIO.read(new File("src/wolf.png"));
			moutonSprite = ImageIO.read(new File("src/sheep.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

		frame = new JFrame("World of Sprite");
		frame.add(this);
		frame.setSize(tailleX*spriteLength, tailleY*spriteLength+40);
		frame.setVisible(true);
		
		
		myMap = new Map(tailleX, tailleY);
		for(int i=0; i< 20; i++){
			myMap.getAgents().add(new Loup(myMap));
			myMap.getAgents().add(new Mouton(myMap));
		}
	}

	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < myMap.getTerrain().length ; i++ )
			for ( int j = 0 ; j < myMap.getTerrain()[0].length ; j++ ){
				
				if ( myMap.getTerrain()[i][j][0] == 0){
					//Affiche une plaine
					if (myMap.getWater()[i][j] > 0)
						g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
					else
						g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}else if(myMap.getTerrain()[i][j][0] == 1){
					g2.drawImage(desertSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
			}
		
		
		for(Agent a : myMap.getAgents()){
			if(a.isAlive()){
				if(a instanceof Loup){
					g2.drawImage(loupSprite,spriteLength*a.getPosX(),spriteLength*a.getPosY(),spriteLength,spriteLength, frame);
				}else if(a instanceof Mouton){
					g2.drawImage(moutonSprite,spriteLength*a.getPosX(),spriteLength*a.getPosY(),spriteLength,spriteLength, frame);
				}
			}
		}
	}
	
	public void Step(){
		myMap.Step();
		repaint();
	}

	public static void main(String[] args) {
		SpriteDemo monSpriteDemo = new SpriteDemo();
		for(int i=0; i<1000; i++){
			try {
				Thread.sleep(delai);
			} catch (InterruptedException e) 
			{
			}
			monSpriteDemo.Step();
		}
		
	}
}