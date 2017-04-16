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
	
	public static int spriteLength = 16;
	private static int delai = 10;
	
	private int NbDepartLoup = 20;
	private int NbDepartMouton = 65;
	private int NbDepartAlligator = 20;
	
	private int tailleX =40, tailleY = 40;
	private Map myMap;

	
	private ArrayList<Agent> cloneAgent;
	
	public SpriteDemo(){
		
		myMap = new Map(tailleX, tailleY);
//		for(int i=0; i< NbDepartLoup; i++){
//			myMap.getAgents().add(new Loup(myMap));
//		}
//		for(int i=0; i< NbDepartMouton; i++){
//			myMap.getAgents().add(new Mouton(myMap));
//		}
//		for(int i=0; i< NbDepartAlligator; i++){
//			myMap.getAgents().add(new Alligator(myMap));
//		}
			
		frame = new JFrame("World of Sprite");
		frame.add(this);
		frame.setSize(tailleX*spriteLength+16, tailleY*spriteLength+40);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}

	//affiche le terrain
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		myMap.afficher(g2, frame, spriteLength);
		
		//Clone pour eviter les ConcurrentModificationException...
		cloneAgent = new ArrayList<Agent>(myMap.getAgents());
		for(Agent a : cloneAgent){
			if(a.isAlive()){
				a.afficher(g2, frame, spriteLength);
			}
		}
	}
	
	//reaffiche le terrain apres avoir fait une step
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