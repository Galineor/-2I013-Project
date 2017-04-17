package Environnement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Agent.Agent;

public class Map {
	
	//sprite utlilise pour la carte
	private Image waterSprite;
	private Image grassSprite;
	private Image desertSprite;
	private Image treeSprite;
	private Image fireSprite;
	private Image ashesSprite;
	private Image rockSprite;
	private Image lavaSprite;
	private Image obsiSprite;
	private Image earthSprite;
	private Image foudreSprite;
	private Image rainSprite;

	private Terrain[][] terrain; //tableau des cases de la carte
	private final int dx, dy; //taille de la carte
	private ArrayList<Agent> agents; //liste des agents de la carte
	public ArrayList<Agent> toAdd = new ArrayList<Agent>();
	
	private int[] casesFoudre; //emplacement du dernier coup de foudre
	
	//modification des parametres de la carte
	private final static double OCEAN = 0.3; 		// 0=>carte remplie d'eau 			1=>carte sans eau							par default: 0.3
	private final static double FORET = 0.2;		// 0=>aucun arbre 					1=>que des arbres							par default: 0.2
	private final static double LAC = 0.01;  		// 0=>pas d'eau						1=>carte remplie d'eau						par default: 0.01
	private final static double PLUIE = 0.001; 		// 0=>pas de pluie					1=>100% pluie								par default: 0.001
	private final static double TEMPS_PLUIE = 0.05; // 0=>pluie sans fin				1=>pluie d'une iteration					par default: 0.05
	private final static double NOUV_LAC = 0.01;	// 0=>aucun nouveau lac				1=>cree nouveau lac a chaque fois			par default: 0.01
	private final static double ADD_WATER = 0.1;	// 0=>n'ajoute jamais d'eau			1=>100% ajout d'eau							par default: 0.1
	private final static double POUR_EVAP = 0.05;	// 0=>aucune evaporation			1=>le compteur est reduit a chaque fois		par default: 0.05
	private final static double ECLAIR = 0.1;		// 0=>aucun eclair					1=>un eclair a chaque fois					par default: 0.1
	
	//type de terrain
	public final static int PLAINE = 0;
	public final static int DESERT = 1;
	public final static int EAU = 2;
	public final static int TERRE = 3;
	public final static int LAVE = 4;
	public final static int OBSIDIENNE = 5;
	
	//constructeur de la carte
	public Map(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;

		terrain = new Terrain[dx][dy];
		casesFoudre = new int [2];

		agents = new ArrayList<Agent>();
		boolean tree;

		for (int x = 0; x < terrain.length; x++) {
			for (int y = 0; y < terrain.length; y++) {
				if (Math.random() < FORET)
					tree = true;
				else
					tree = false;

				// initialisation du terrain
				if (Math.random() < (1 - (x * OCEAN)) || Math.random() < (1 - (y * OCEAN))
						|| Math.random() < (1 - ((dx - x - 1) * OCEAN))
						|| Math.random() < (1 - ((dy - y - 1) * OCEAN))){
					
					// ajout de la mer autour de l ile
					terrain[x][y] = new Terrain(EAU, 2, 0, false);
					
				} else {
					if (x > terrain.length / 2 && y > terrain.length / 2) {
						// cree une case de desert
						terrain[x][y] = new Terrain(DESERT, 0, 0, tree);

					} else {
						if (x > terrain.length / 2 - 2 && y > terrain.length / 2 - 2) {
							// cree une transition entre le desert et la foret
							terrain[x][y] = new Terrain((int) (Math.random() * EAU), 0, 0, false);
							
						} else {
							if (Math.random() < LAC) {
								// initialisation de la foret avec de l eau
								terrain[x][y] = new Terrain(EAU, (int) (Math.random() * 5), 0, false);
								
							} else {
								// initialisation foret
								terrain[x][y] = new Terrain(PLAINE, 0, 0, tree);
							}
						}
					}
				}
			}
		}

		try {
			waterSprite = ImageIO.read(new File("src/water.png"));
			treeSprite = ImageIO.read(new File("src/tree.png"));
			fireSprite = ImageIO.read(new File("src/tree_fire.png"));
			ashesSprite = ImageIO.read(new File("src/tree_ashes.png"));
			rockSprite = ImageIO.read(new File("src/rock.png"));
			grassSprite = ImageIO.read(new File("src/grass.png"));
			desertSprite = ImageIO.read(new File("src/desert.png"));
			lavaSprite = ImageIO.read(new File("src/Lava.png"));
			obsiSprite = ImageIO.read(new File("src/obsidienne.png"));
			earthSprite = ImageIO.read(new File("src/terre.png"));
			foudreSprite = ImageIO.read(new File("src/foudre.png"));
			rainSprite = ImageIO.read(new File("src/rain.png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	// lance toutes les fonctions de modifications de l'environnement
	public void Step() {
		StepWorld();
		StepAgent();
	}

	// lance les modifications apportee aux agents
	public void StepAgent() {
		for (Agent a : getAgents()) {
			if (a.isAlive() && Math.random() < 0.80)
				a.Step();
			else
				a.updatePrevPos(); // Si on le met pas a jour, on modifie sa
									// position precedente ( permet l'affichage
									// d'agents immobile )
		}

		agents.addAll(toAdd);
		toAdd.clear();
	}

	// effectue les modification apporte a l'environnement
	public void StepWorld() {
		meteo();
		majForet(terrain);
		majEau();
		majHerbe();
		if (Math.random() < 0.005 && terrain[dx / 2][dy / 2].type != LAVE)
			Volcan(terrain);
		majLAVA();
	}

	// met a jour l'herbe de la carte
	public void majHerbe() {
		int x, y; // coordonnees aleatoires
		int p; // variabble de pousse de la case actuelle

		for (int i = 0; i < terrain.length / 3; i++) {
			for (int j = 0; j < terrain[0].length / 3; j++) {
				x = (int) (Math.random() * dx);
				y = (int) (Math.random() * dy);
				p = terrain[x][y].getPousse();

				if (terrain[x][y].type == TERRE || terrain[x][y].type == PLAINE) {
					
					// terrain pousse > 3 == plaines (herbe a manger)
					// sinon == terre
					if (terrain[x][y].getPousse() > 3)
						terrain[x][y].type = PLAINE;
					else
						terrain[x][y].type = TERRE;

					// l'herbe pousse
					terrain[x][y].setPousse(p + 1);
				}
			}
		}
	}

	// met a jour les arbres de la carte
	public void majForet(Terrain[][] t) {
		int x, y;
		for (int i = 0; i < (dx * dy) / 4; i++) {
			x = (int) (Math.random() * dx);
			y = (int) (Math.random() * dy);
			if (t[x][y].type == PLAINE) {
				// verifie si la case est bien un arbre
				if (!t[x][y].isTree) {

					if (Math.random() < 0.05 && t[x][y].getAFA() == 2)
						t[x][y].setAFA(0);

					// fait pousser un arbre avec chance plus elevee si il y a
					// des cendres
					if (t[x][y].getAFA() == 2 && Math.random() < 0.1) {
						t[x][y].isTree = true;
						t[x][y].setAFA(0);
					} else {
						if (t[x][y].getAFA() != 2 && Math.random() < 0.0005) {
							t[x][y].isTree = true;
							t[x][y].setAFA(0);
						}
					}

				} else {
					if (t[x][y].getAFA() == 1) {
						// les arbres en feu disparraissent si pres de lave
						if (t[(x - 1 + dx) % dx][y].type == LAVE || t[(x + 1 + dx) % dx][y].type == LAVE
								|| t[x][(y - 1 + dy) % dy].type == LAVE || t[x][(y + 1 + dy) % dy].type == LAVE) {
							t[x][y].setAFA(0);
							t[x][y].isTree = false;
						} else {
							// les arbres en feu deviennent des cendres
							t[x][y].setAFA(2);
							t[x][y].isTree = false;
						}
					}
					// propagation du feu
					if (t[x][y].getAFA() == 0 && (t[(x - 1 + dx) % dx][y].getAFA() == 1
							|| t[(x + 1 + dx) % dx][y].getAFA() == 1 || t[x][(y - 1 + dy) % dy].getAFA() == 1
							|| t[x][(y + 1 + dy) % dy].getAFA() == 1)) {
						t[x][y].setAFA(1);
					}

					if (t[x][y].getAFA() == 0 && (t[(x - 1 + dx) % dx][y].type == LAVE || t[(x + 1 + dx) % dx][y].type == LAVE
							|| t[x][(y - 1 + dy) % dy].type == LAVE || t[x][(y + 1 + dy) % dy].type == LAVE)) {
						t[x][y].setAFA(1);
					}
					
					//feu aleatoire
//					if (Math.random() < 0.01) {
//						t[x][y].setAFA(1);
//					}
				}
			}
		}
	}

	// propagation de l'eau
	public void majEau() {
		int x, y;
		for (int i = 0; i < dx / 3; i++) {
			for (int j = 0; j < dy / 3; j++) {
				x = (int) (Math.random() * dx);
				y = (int) (Math.random() * dy);
				
				if (!terrain[x][y].isTree && terrain[x][y].getAFA() != 2) {
					if (terrain[(x - 1 + dx) % dx][y].water > terrain[x][y].water
							&& terrain[(x - 1 + dx) % dx][y].water > 1 && terrain[(x - 1 + dx) % dx][y].type == EAU) {
						terrain[x][y].water++;
						terrain[(x - 1 + dx) % dx][y].water--;
						terrain[x][y].type = EAU;
						terrain[x][y].isTree = false;
						terrain[x][y].setEvap(Terrain.EVAP);

					}
					if (terrain[(x + 1) % dx][y].water > terrain[x][y].water && terrain[(x + 1) % dx][y].water > 1
							&& terrain[(x + 1) % dx][y].type == EAU) {
						terrain[x][y].water++;
						terrain[(x + 1) % dx][y].water--;
						terrain[x][y].type = EAU;
						terrain[x][y].isTree = false;
						terrain[x][y].setEvap(Terrain.EVAP);
						
					}
					if (terrain[x][(y - 1 + dy) % dy].water > terrain[x][y].water
							&& terrain[x][(y - 1 + dx) % dx].water > 1 && terrain[x][(y - 1 + dx) % dx].type == EAU) {
						terrain[x][y].water++;
						terrain[x][(y - 1 + dy) % dy].water--;
						terrain[x][y].type = EAU;
						terrain[x][y].isTree = false;
						terrain[x][y].setEvap(Terrain.EVAP);

					}
					if (terrain[x][(y + 1) % dy].water > terrain[x][y].water && terrain[x][(y + 1) % dy].water > 1
							&& terrain[x][(y + 1) % dy].type == EAU) {
						terrain[x][y].water++;
						terrain[x][(y + 1) % dy].water--;
						terrain[x][y].type = EAU;
						terrain[x][y].isTree = false;
						terrain[x][y].setEvap(Terrain.EVAP);
					}
				}
			}
		}
	}

	// lancement du volcan
	public void Volcan(Terrain[][] t) {
		t[dx / 2][dy / 2].type = LAVE; // met de la lave au niveau du volcan
		t[dx / 2][dy / 2].water = (int) (Math.random() * 30);
		t[dx / 2][dy / 2].cptLAVA = 5;
	}

	// propagation de la lave
	public void majLAVA() {
		int x, y;
		for (int i = 0; i < dx / 3; i++) {
			for (int j = 0; j < dy / 3; j++) {
				x = (int) (Math.random() * dx);
				y = (int) (Math.random() * dy);

				if (terrain[x][y].cptLAVA == 0) {
					terrain[x][y].cptLAVA = -1;
					terrain[x][y].type = OBSIDIENNE;
					terrain[x][y].water = 0;

				} else if (terrain[(x - 1 + dx) % dx][y].water > terrain[x][y].water
						&& terrain[(x - 1 + dx) % dx][y].water > 1 && terrain[(x - 1 + dx) % dx][y].type == LAVE) {

					if (terrain[x][y].type == EAU) {
						terrain[x][y].cptLAVA = -1;
						terrain[x][y].type = OBSIDIENNE;
						terrain[x][y].water = 0;
						terrain[(x - 1 + dx) % dx][y].water--;
						terrain[x][y].setEvap(-1);
						
					} else {
						terrain[x][y].water++;
						terrain[(x - 1 + dx) % dx][y].water--;
						terrain[x][y].type = LAVE;
						terrain[x][y].cptLAVA = 5;
						
					}

				} else if (terrain[(x + 1) % dx][y].water > terrain[x][y].water && terrain[(x + 1) % dx][y].water > 1
						&& terrain[(x + 1) % dx][y].type == LAVE) {
					
					if (terrain[x][y].type == EAU) {
						terrain[x][y].cptLAVA = -1;
						terrain[x][y].type = OBSIDIENNE;
						terrain[x][y].water = 0;
						terrain[(x + 1) % dx][y].water--;
						terrain[x][y].setEvap(-1);
						
					} else {
						terrain[x][y].water++;
						terrain[(x + 1) % dx][y].water--;
						terrain[x][y].type = LAVE;
						terrain[x][y].cptLAVA = 5;
					}

				} else if (terrain[x][(y - 1 + dy) % dy].water > terrain[x][y].water
						&& terrain[x][(y - 1 + dx) % dx].water > 1 && terrain[x][(y - 1 + dx) % dx].type == LAVE) {
					
					if (terrain[x][y].type == EAU) {
						terrain[x][y].cptLAVA = -1;
						terrain[x][y].type = OBSIDIENNE;
						terrain[x][y].water = 0;
						terrain[x][(y - 1 + dy) % dy].water--;
						terrain[x][y].setEvap(-1);
						
					} else {
						terrain[x][y].water++;
						terrain[x][(y - 1 + dy) % dy].water--;
						terrain[x][y].type = LAVE;
						terrain[x][y].cptLAVA = 5;
					}

				} else if (terrain[x][(y + 1) % dy].water > terrain[x][y].water && terrain[x][(y + 1) % dy].water > 1
						&& terrain[x][(y + 1) % dy].type == LAVE) {
					
					if (terrain[x][y].type == EAU) {
						terrain[x][y].cptLAVA = -1;
						terrain[x][y].type = OBSIDIENNE;
						terrain[x][y].water = 0;
						terrain[x][(y + 1) % dy].water--;
						terrain[x][y].setEvap(-1);
						
					} else {
						terrain[x][y].water++;
						terrain[x][(y + 1) % dy].water--;
						terrain[x][y].type = LAVE;
						terrain[x][y].cptLAVA = 5;
					}

				}
				if (terrain[x][y].cptLAVA > 0) {
					terrain[x][y].cptLAVA--;
				}
			}
		}
	}
	
	//lance toutes les fonction gerant la meteo
	public void meteo() {
		foudre();
		pluie();
		evaporation();
	}
	
	//a une chance de creer un eclair qui mets le feu sur la carte
	public void foudre (){
		int x,y;
		//supprime la foudre du dernier coup de foudre
		x = casesFoudre[0];
		y = casesFoudre[1];
		if (terrain[x][y].isFoudre())
			terrain[x][y].setFoudre(false);
		
		// foudre
		if (Math.random() < ECLAIR) {
			x = (int) (Math.random() * dx);
			y = (int) (Math.random() * dy);
			//mets la foudre sur la case
			terrain[x][y].setFoudre(true);
			casesFoudre[0] = x;
			casesFoudre[1] = y;
			
			//met en feu la case qui recoit l'eclair et les cases alentours
			if (terrain[x][y].isTree && terrain[x][y].type == PLAINE)
				terrain[x][y].setAFA(1);
			
			if (terrain[(x - 1 + dx) % dx][y].isTree && terrain[(x - 1 + dx) % dx][y].type == PLAINE)
				terrain[(x - 1 + dx) % dx][y].setAFA(1);
			
			if (terrain[(x + 1) % dx][y].isTree && terrain[(x + 1) % dx][y].type == PLAINE)
				terrain[(x + 1) % dx][y].setAFA(1);
			
			if (terrain[x][(y - 1 + dy) % dy].isTree && terrain[x][(y - 1 + dy) % dy].type == PLAINE)
				terrain[x][(y - 1 + dy) % dy].setAFA(1);
			
			if (terrain[x][(y + 1) % dy].isTree && terrain[x][(y + 1) % dy].type == PLAINE)
				terrain[x][(y + 1) % dy].setAFA(1);
		}
	}
	
	//gere la pluie de la carte
	public void pluie(){
		for (int x = 0; x < terrain.length; x++){
			for (int y = 0; y < terrain[0].length; y++){
				
				//la pluie ralenti l'evaporation
				if (terrain[x][y].type == EAU && terrain[x][y].isPluie())
					terrain[x][y].setEvap(Terrain.EVAP);
				
				//la pluie eteinds le feu de la case
				if (terrain[x][y].isPluie() && terrain[x][y].type == PLAINE && terrain[x][y].isTree && terrain[x][y].getAFA() == 1)
					terrain[x][y].setAFA(0);
				
				//si la pluie dure longtemps un suplment d'eau apparait
				if(terrain[x][y].type == EAU && terrain[x][y].isPluie() && Math.random() < ADD_WATER){
					terrain[x][y].water ++;
				}
				
				//si il pleut trop longtemps un lac se forme
				if(terrain[x][y].type == PLAINE && terrain[x][y].isPluie() && Math.random() < NOUV_LAC){
					terrain[x][y].water ++;
					terrain[x][y].type = EAU;
				}
				
				//continue la pluie avec proba de continuer qui diminue a chaque iteration
				if (terrain[x][y].isPluie() && Math.random() < (TEMPS_PLUIE * terrain[x][y].getTempsPluie())){
					terrain[x][y].setPluie(false);
					terrain[x][y].setTmpPluie(-1);
				}
				
				//lance la pluie avec une proba definie pas PLUIE
				if (terrain[x][y].type == PLAINE && !terrain[x][y].isPluie() && Math.random() < PLUIE){
					terrain[x][y].setPluie(true);
					terrain[x][y].setTmpPluie(0);
				}
				
				if (terrain[x][y].isPluie())
					terrain[x][y].setTmpPluie(terrain[x][y].getTempsPluie() + 1);
			}
		}		
	}
	
	//gere l'evaporation de l'eau sur la carte
	public void evaporation(){
		int x,y;
		
		for (int i = 0; i < terrain.length/10 ; i++){
			for (int j = 0; j < terrain[0].length/10 ; j ++){
				x = (int) (Math.random() * dx);
				y = (int) (Math.random() * dy);
				if (terrain[x][y].getEvap() > 0 && Math.random() < POUR_EVAP){
					terrain[x][y].setEvap(terrain[x][y].getEvap() - 1);
				}
				if (terrain[x][y].getEvap() == 0){
					//enleve une hauteur d'eau si water > 1
					if(terrain[x][y].water > 1){
						terrain[x][y].water --;
						
					}else{
						if (x > dx/2 && y > dy/2)
							terrain[x][y].type = DESERT;
						else
							terrain[x][y].type = PLAINE;
						
						terrain[x][y].water = 0;
					}
					terrain[x][y].setEvap(-1);
				}
			}
		}
	}

	//affiche les sprites du terrain
	public void afficher(Graphics2D g2, JFrame frame, int spriteLength) {
		for ( int i = 0 ; i < terrain.length ; i++ ){
			for ( int j = 0 ; j < terrain[0].length ; j++ ){
				
				if (terrain[i][j].type == PLAINE) {
					// affiche plaine
					g2.drawImage(grassSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);

				} else if (terrain[i][j].type == DESERT) {
					// affiche desert
					g2.drawImage(desertSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);

				} else if (terrain[i][j].type == EAU) {
					// affiche l eau
					g2.drawImage(waterSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);

				} else if (terrain[i][j].type == TERRE) {
					// affiche la terre
					g2.drawImage(earthSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);

				} else if (terrain[i][j].type == LAVE) {
					// affiche la lave
					g2.drawImage(lavaSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);

				} else if (terrain[i][j].type == OBSIDIENNE) {
					// affiche obsidienne
					g2.drawImage(obsiSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);

				}

				if (terrain[i][j].isTree) {
					if (terrain[i][j].type == PLAINE) {
						// affichage arbre
						if (terrain[i][j].getAFA() == 0) {
							g2.drawImage(treeSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);
						}
						// affichage arbre en feu
						if (terrain[i][j].getAFA() == 1) {
							g2.drawImage(fireSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);
						}

					} else {
						// affichage des rochers dans le desert
						if (terrain[i][j].type == DESERT) {
							g2.drawImage(rockSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);
						}
					}
				}
				// affichage cendres
				if (terrain[i][j].getAFA() == 2) {
					g2.drawImage(ashesSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);
				}
				
				//affiche la foudre
				if (terrain[i][j].isFoudre()){
					g2.drawImage(foudreSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);
				}
				
				if (terrain[i][j].isPluie()){
					g2.drawImage(rainSprite, spriteLength * i, spriteLength * j, spriteLength, spriteLength, frame);
				}
			}
		}
	}
	
	
	public Terrain[][] getTerrain() {
		return this.terrain;
	}

	
	public int getHeight() {
		return this.dy;
	}

	
	public int getWidth() {
		return this.dx;
	}

	
	public ArrayList<Agent> getAgents() {
		return this.agents;
	}
}
