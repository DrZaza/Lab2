package socialdistancing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Control {
		Frame view;
		String title = "Social Distance Simulation";
		//Model and View
		ArrayList<Person> model; //the community of Person objects	
		
		// counters for "this" simulation instance
		
		public int numInfected = 0;
		public int numDied= 0;
		
		//adding dimensions to create the walls 
		protected Wall Vertical1 = new Wall(600, 0, "src/SocialDistancingImages/wall2.png", true);
		protected Wall Vertical2 = new Wall(200, 0, "src/SocialDistancingImages/wall2.png", true);
		protected Wall Vertical3 = new Wall(550, 400, "src/SocialDistancingImages/wall2.png", true);
		protected Wall Vertical4 = new Wall(200, 400, "src/SocialDistancingImages/wall2.png", true);
		protected Wall Horizontal1 = new Wall(620, 160, "src/SocialDistancingImages/wall1.png", false);
		protected Wall Horizontal2 = new Wall(-25, 160, "src/SocialDistancingImages/wall1.png", false);
		protected Wall Horizontal3 = new Wall(620, 400, "src/SocialDistancingImages/wall1.png", false);
		protected Wall Horizontal4 = new Wall(-25, 400, "src/SocialDistancingImages/wall1.png", false);
		
		private Wall[] w = {Vertical1, Vertical2, Vertical3, Vertical4, Horizontal1, Horizontal2, Horizontal3, Horizontal4};
		
		//simulation control values
		public int  numPeople;			
		public double toRoam;			    
		public double toBeInfected;		
		public double toDie;				
		public int sickTimeLow;			
		public int sickTimeMax;
		
		//frame extents
		public int frameX;
		public int frameY;
		
		//position extents, keep objects away from the edges
		public int xExt;
		public int yExt;
		
		//oval size, represents person in frame
		public int OvalW;	//Height
		public int OvalH;	//Width
		
		//refresh timer, also used to calculate time/age of infection
		public int timerValue;
	
		/*
		 * Default constructor uses Static/Default simulation values
		 */
		public Control() {
			//This sets defaults in case run with default constructor
			// simulation control starting values
			numPeople = Settings.sNumPeople;			
			toRoam = Settings.sToRoam;			    
			toBeInfected = Settings.sToBeInfected;		
			toDie = Settings.sToDie;				
			sickTimeLow = Settings.sSickTimeLow;			
			sickTimeMax = Settings.sSickTimeMax;
			
			//frame extents
			frameX = Settings.sFrameX;
			frameY = Settings.sFrameY;
			//position extents, keep objects away from the edges
			xExt = Settings.sXExt;
			yExt = Settings.sYExt;
			//oval size, represents person in frame
			OvalW = Settings.sOvalW;	//Height
			OvalH = Settings.sOvalH;	//Width
			//refresh timer, also used to calculate time/age of infection
			timerValue = Settings.sTimerValue;
		}

		/*
		 * This constructor uses user defined simulation Settings
		 */
		public Control(Settings sets) {
			// health settings
			numPeople = sets.numPeople;
			toRoam = sets.toRoam;
			toBeInfected = sets.toBeInfected;
			toDie = sets.toDie;
			sickTimeLow = sets.sickTimeLow;
			sickTimeMax = sets.sickTimeMax;
			// simulator settings
			frameX = sets.frameX;
			frameY = sets.frameY;
			yExt = sets.yExt;
			xExt = sets.xExt;
			OvalW = sets.OvalW;
			OvalH = sets.OvalH;
			timerValue = sets.timerValue;
		}
		
		/*
		 * Tester method to run simulation
		 */
		public static void main (String[] args) {
			Control c = new Control();
			c.runSimulation();
		}
		
		/* 
		 * This method coordinates MVC for Simulation
		 * - The Simulation is managing People in a Graphics frame to simulate a virus outbreak
		 * - Prerequisite: Control values from constructor are ready
		 */
		public void runSimulation() {
			//Setup to the Simulation Panel/Frame
			Frame view = new Frame(this, title);
			
			//Setup the People
			model = new ArrayList<Person>();
			for(int i = 0; i < numPeople; i++) {
				//instantiate Person object and add it to the ArrayList
				model.add(new Person(this));
			}
			
			// Start the Simulation
			view.activate();
		}
		
		/*
		 * Call Back method for View
		 * paints/repaints model of graphic objects repressing person objects in the frame 
		 */
		public void paintPersons(Graphics graphicsPeople1) {
			
			//find the Person in the Model!
			int index = 0;
			for(Person Dot1: model) {
				for(Person Dot2: model) {
					//for each unique pair invoke the collision detection code
					Dot1.collisionDetector(Dot2);
				}
				checkWallCollision(Dot1);
				Dot1.healthManager(); //manage health values of the Person
				Dot1.velocityManager(); //manage social distancing and/or roaming values of the Person
				
				//set the color of the for the person oval based on the health status of person object
				switch(Dot1.state) {
					case candidate:
						graphicsPeople1.setColor(Color.LIGHT_GRAY);
						break;
					case infected:
						graphicsPeople1.setColor(Color.red);
						break;
					case recovered:
						graphicsPeople1.setColor(Color.green);
						break;
					case died:
						graphicsPeople1.setColor(Color.black);
						
				}
				
				//draw the person oval in the simulation frame
				graphicsPeople1.fillOval(Dot1.x, Dot1.y, OvalW, OvalH);
				
				// draw the person oval in meter/bar indicator
				graphicsPeople1.fillOval((frameX-(int)(frameX*.02)), (int)(frameY-((numPeople-index)*OvalH)/1.67), OvalW, OvalH);
				index++;
				
			}
		}
		
		public void paintWalls(Graphics g)
		{
			
			for(Wall w1 : w)
				g.drawImage(w1.getImage(), w1.getX(), w1.getY(), view);
			
			//sets text color
			g.setColor(Color.BLACK);
			g.setFont(new Font("Roboto", Font.BOLD, 25));	
			
			g.drawString("Sprouts", 610, 50);
			g.drawString("Scripps Medical", 5, 50);
			g.drawString("Board and Brew", 5, 440);
			g.drawString("Mr. M's House", 590, 440);
		}
		
		//added new collision method 
		public void checkWallCollision(Person p) {
			Wall[] walls = {Vertical1, Horizontal1, Vertical2, Horizontal2, Vertical3, Horizontal3, Vertical4, Horizontal4};
			Rectangle[] r = {Vertical1.getBounds(), Horizontal1.getBounds(), Vertical2.getBounds(), Horizontal2.getBounds(),
					Vertical3.getBounds(), Horizontal3.getBounds(), Vertical4.getBounds(), Horizontal4.getBounds()};
			Rectangle rect1 = new Rectangle(p.x,p.y, p.width, p.height);
			for(int i = 0; i < walls.length;i++)
			{
				if(r[i].intersects(rect1))
					if(walls[i].vertical)
					{
						p.vx *= -1;
					}
					else
						p.vy *= -1;
			}
		}
		
}