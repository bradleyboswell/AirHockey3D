package control;

import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;

import model.Paddle;
import model.Puck;
import model.Rink;
import view.Game;

public class PuckBehavior extends Behavior{
	int renderFrame=1;
	
	private WakeupCriterion frames = new WakeupOnElapsedFrames(5);		//for desktop 
	private static float horizontal = -0.0015f;
	private static float vertical = -0.0009f;
	
//	private WakeupCriterion frames = new WakeupOnElapsedFrames(1);		//for laptop different frame rate	
//	private static float horizontal = -0.009f;
//	private static float vertical = -0.009f;

	private Vector3f location;
	private Transform3D move = new Transform3D();
	private Vector3f mover;
	
	private static float nextX,nextZ;
	
	public PuckBehavior(Bounds bounds, Vector3f location) {
		this.setSchedulingBounds(bounds);
		this.setUserData("User Data: PuckBehavior");
		this.setName("PuckBehavior");
		this.location = location;
	}
	
	@Override
	public void initialize() {
		wakeupOn(frames);
	}

	@Override
	public void processStimulus(Enumeration trigger) {
		
		while(trigger.hasMoreElements()) {
			WakeupCriterion criteria = (WakeupCriterion) trigger.nextElement();
			if(Game.gameRunning) {
				//Only render first frame when launching game or after reset
				if(renderFrame == 1) {
					if(criteria instanceof WakeupOnElapsedFrames) {
						step();
					}
					//System.out.println("finished loading frame: " +renderFrame);
					renderFrame++;
					Game.gameRunning=false;
				}else {
					if(criteria instanceof WakeupOnElapsedFrames) {
						Vector3f p1 = new Vector3f();
						Game.player1Pos.get(p1);
							
						Vector3f p2 = new Vector3f();
						Game.player2Pos.get(p2);
							
						location = new Vector3f();
						Game.puckPos.get(location);
							
						predict(location);
						wallCollisions();
						goalCollisions();
						controlPaddleCollision(p1,p2);
						step();
						}
				}
			}
			wakeupOn(frames);
		}
	}

	//Predict next location of puck
	private void predict(Vector3f puck) {
		nextX = puck.x + horizontal;
		nextZ = puck.z + vertical;
	}
	
	//Check for wall collisions with puck
	private void wallCollisions() {
		if(nextX + Puck.radius > Rink.width-(Rink.depth/2f)) {  				//Left wall collide
			horizontal = -horizontal;
			nextX = (Rink.width - Rink.depth/2f) - Puck.radius;
			Game.wallSound.setEnable(true);
		}else if(nextX - Puck.radius < (Rink.depth/2f)) {				//Right wall collide
			horizontal = -horizontal;
			nextX = (Rink.depth/2f)+Puck.radius;
			Game.wallSound.setEnable(true);
		}else if(nextZ + Puck.radius > Rink.length-(Rink.depth/2f)) { 	// Far wall collide
			vertical = -vertical;
			nextZ = (Rink.length-Rink.depth/2f) - Puck.radius;
			Game.wallSound.setEnable(true);
		}else if(nextZ - Puck.radius < (Rink.depth/2f)) {				//Near wall collide
			vertical = -vertical;
			nextZ = (Rink.depth/2f)+Puck.radius;
			Game.wallSound.setEnable(true);
		}
	}
	
	//Check for goal collisions with puck
	private void goalCollisions() {
		if(location.x >= 0.24f && location.x <= 0.48f) {
			if(location.z + Puck.radius > Rink.length-(Rink.depth)-0.0001f) {		//Player1 Scored
				Game.scoreboard.setPlayer1score(Game.scoreboard.getPlayer1score()+1);
				System.out.println("Player 1 Scored a goal!");
				System.out.println("Score: " + Game.scoreboard.getPlayer1score() + " : " + Game.scoreboard.getPlayer2score());
				Game.scoreboardText.setString("Score: " + Game.scoreboard.getPlayer1score() + " : " + Game.scoreboard.getPlayer2score());
				Game.g2Sound.setEnable(true);
				reset();
			}else if(location.z - Puck.radius < (Rink.depth)+0.0001f) {					//Player2 Scored
				Game.scoreboard.setPlayer2score(Game.scoreboard.getPlayer2score()+1);
				System.out.println("Player 2 Scored a goal!");
				System.out.println("Score: " + Game.scoreboard.getPlayer1score() + " : " + Game.scoreboard.getPlayer2score());
				Game.scoreboardText.setString("Score: " + Game.scoreboard.getPlayer1score() + " : " + Game.scoreboard.getPlayer2score());
				Game.g1Sound.setEnable(true);
				reset();
			}
		}	
	}
	
	//Reset puck position only and render first frame if a goal has been made
	private void reset() {
		renderFrame=1;
		location = new Vector3f(0.36f,0.0075f,0.48f);
		move.setTranslation(location);
		Game.puckPos.set(move);
		
		move = new Transform3D();
		mover = new Vector3f();
		Game.puckPos.get(mover);
		move.setTranslation(mover);
		
		Game.puckXfmGrp.setTransform(move);
	}
	
	//Control method for collisions with paddle
	private void controlPaddleCollision(Vector3f p1, Vector3f p2) {				
		if(testP1Collision(p1)) {
			Game.p1Sound.setEnable(true);
			executeP1Collision(p1);
		}
		if(testP1Collision(p2)) {
			Game.p2Sound.setEnable(true);
			executeP1Collision(p2);
		}
	}
	
	//Check if puck is colliding with player
	private boolean testP1Collision(Vector3f p1) {
		boolean colliding = false;		
		float dxP1 = nextX - p1.x;
		float dzP1 = nextZ - p1.z;
		float d = (dxP1*dxP1 + dzP1*dzP1);
		if(d <= (Puck.radius + Paddle.radius)*( Puck.radius + Paddle.radius)) {
			colliding = true;
		}	
		return colliding;
	}
	
	//Calculate new puck trajectory after collision
	private void executeP1Collision(Vector3f p1) {
		float dx = nextX - p1.x;
		float dz = nextZ - p1.z;
		float angle = (float) Math.atan2(dz, dx);	
		float vel = (float) Math.sqrt((horizontal*horizontal) + (vertical*vertical));
		float direction = (float) Math.atan2(dz, dx);		
		float xVelocity = (float) (vel*Math.cos(direction - angle));
		float zVelocity = (float)(vel *Math.sin(direction - angle));		
		horizontal = (float) (Math.cos(angle)*xVelocity + Math.cos(angle+Math.PI/2)*zVelocity);
		vertical = (float) (Math.sin(angle)*xVelocity+Math.cos(angle+Math.PI/2)*zVelocity);
		nextX = nextX + horizontal;
		nextZ = nextZ + vertical;
	}
	
	//Step puck
	private void step() {
		location.setX(location.x + horizontal);
		location.setZ(location.z + vertical);
		
		mover = new Vector3f(horizontal, 0f, vertical);
		move.setTranslation(mover);
		Game.puckPos.add(move);
		
		move = new Transform3D();
		mover = new Vector3f();
		Game.puckPos.get(mover);
		move.setTranslation(mover);
		
		Game.puckXfmGrp.setTransform(move);
	}
}
