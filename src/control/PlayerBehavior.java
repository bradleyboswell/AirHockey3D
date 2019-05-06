package control;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;

import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Vector3f;

import model.Paddle;
import model.Puck;
import model.Rink;
import view.Game;

public class PlayerBehavior extends Behavior{
	public WakeupCriterion frames;
	
	public boolean player1;
	
	public static float horizontalV = 0.0015f;
	public static float verticalV = 0.0015f;
	
	//	public static float horizontalV = 0.009f;		//for laptop
	//	public static float verticalV = 0.009f;
	public Vector3f pLocation;
	
	public Transform3D move;
	public Vector3f mover;
	
	public float p1nextX, p1nextZ, p2nextX, p2nextZ;
	
	
	public PlayerBehavior(Bounds bounds, Vector3f pLocation, boolean player1) {
		this.setSchedulingBounds(bounds);
		this.pLocation = pLocation;
		this.player1 = player1;
	}
	
	
	@Override
	public void initialize() {
		frames = new WakeupOnElapsedFrames(5);
	//	frames = new WakeupOnElapsedFrames(1);				//for laptop
		wakeupOn(frames);
		
	}

	@Override
	public void processStimulus(Enumeration trigger) {
		while(trigger.hasMoreElements()) {
			WakeupCriterion criteria = (WakeupCriterion) trigger.nextElement();
			
			if(criteria instanceof WakeupOnElapsedFrames) {
				if(player1) {
					pLocation = new Vector3f();
					Game.player1Pos.get(pLocation);
					translationHandler(pLocation);
				}else if(!player1) {
					pLocation = new Vector3f();
					Game.player2Pos.get(pLocation);
					translationHandler(pLocation);
				}		
			}
		}
		wakeupOn(frames);
	}
	
	//Look ahead to make sure paddles wont collide with wall to prevent sticking.
	private void predict(boolean p1isMoving,Vector3f increment) {
		if(p1isMoving) {
			p1nextX = pLocation.x + increment.x*horizontalV;
			p1nextZ = pLocation.z + increment.z*verticalV;
		}
		else if (!p1isMoving) {
			p2nextX = pLocation.x + increment.x*horizontalV;
			p2nextZ = pLocation.z + increment.z*verticalV;
		}
	}

	//Handle movement of paddles
	public void translationHandler(Vector3f player) {
		Vector3f increment;
		float p1Angle = 0;
		boolean p1isMoving = false;
		
		float p2Angle = 0;
		boolean p2isMoving = false;
		
		//Player1 Controls
		if(player1) {
			if(Game.p1Up) {
				p1Angle = 90;
				if(Game.p1Right) p1Angle = 135;
				else if(Game.p1Left) p1Angle = 45;
				p1isMoving = true;
			}else if (Game.p1Down) {
				p1Angle = 270;
				if(Game.p1Right) p1Angle = 225;
				else if(Game.p1Left) p1Angle = 315;
				p1isMoving = true;
			}else if(Game.p1Right) {
				p1Angle = 180;
				p1isMoving = true;
			}else if(Game.p1Left) {
				p1Angle = 0;
	 			p1isMoving = true;
			}
		}
		
		//Player 2 controls
		if(!player1) {
			if(Game.p2Down) {
				p2Angle = 90;
				if(Game.p2Left) p2Angle = 135;
				else if(Game.p2Right) p2Angle = 45;
				p2isMoving = true;
			}else if (Game.p2Up) {
				p2Angle = 270;
				if(Game.p2Left) p2Angle = 225;
				else if(Game.p2Right) p2Angle = 315;
				p2isMoving = true;
			}else if(Game.p2Left) {
				p2Angle = 180;
				p2isMoving = true;
			}else if(Game.p2Right) {
				p2Angle = 0;
	 			p2isMoving = true;
			}
		}
		
		if(p1isMoving&&player1) {
			increment = getXY(p1Angle);
			predict(p1isMoving,increment);
			if(!wallCollisions(p1isMoving,increment)) moveP1Paddle(increment.x*horizontalV, increment.z*verticalV);
		}else if(p2isMoving&&!player1) {
			increment = getXY(p2Angle);
			predict(p1isMoving, increment);
			if(!wallCollisions(p1isMoving,increment)) moveP2Paddle(increment.x*horizontalV, increment.z*verticalV);
		}
		
	}
	
	//Check for wall collisions
	public boolean wallCollisions(boolean p1isMoving, Vector3f increment) {
		boolean isColliding = false;
		if(p1isMoving && player1) {
			if(p1nextX + Paddle.radius > Rink.width-(Rink.depth/2)) {		//P1 Left Wall Collision
				p1nextX = (Rink.width - Rink.depth) - Paddle.radius;
				isColliding = true;
			}else if(p1nextX - Paddle.radius < (Rink.depth/2)) {		//P1 Right Wall collision
				p1nextX = Puck.radius;
				isColliding = true;
			}else if(p1nextZ+ Paddle.radius > Rink.length-(Rink.depth/2)) {		//P1 Far Wall collision
				p1nextZ = (Rink.length-Rink.depth) - Puck.radius;
				isColliding = true;
			}else if(p1nextZ - Paddle.radius < (Rink.depth/2)) {			//P1 Near Wall Collision
				p1nextZ = Puck.radius;
				isColliding = true;
			}
		}else if(!p1isMoving && !player1) {
			if(p2nextX + Paddle.radius > Rink.width-(Rink.depth/2)) {		//P2 Left Wall Collision
				p2nextX = (Rink.width - Rink.depth) - Paddle.radius;
				isColliding = true;
			}else if(p2nextX - Paddle.radius < (Rink.depth/2)) {		//P2 Right Wall Collision
				p2nextX = Puck.radius;
				isColliding = true;
			}else if(p2nextZ+ Paddle.radius > Rink.length-(Rink.depth/2)) {			//P2 Far Wall Collision
				p2nextZ = (Rink.length-Rink.depth) - Puck.radius;
				isColliding = true;
			}else if(p2nextZ - Paddle.radius < (Rink.depth/2)) {				//P2 Near Wall Collision
				p2nextZ = Puck.radius;
				isColliding = true;
			}
		}
		return isColliding;
	}

	//Move player2 paddle
	private void moveP2Paddle(float dx, float dz) {
		pLocation.setX(pLocation.x + dx);
		pLocation.setZ(pLocation.z + dz);
		
		move = new Transform3D();
		mover = new Vector3f(dx, 0f, dz);
		move.setTranslation(mover);
		Game.player2Pos.add(move);
		
		move = new Transform3D();
		mover = new Vector3f();
		Game.player2Pos.get(mover);
		move.setTranslation(mover);
		Game.player2XfmGrp.setTransform(move);
	}
	
	//Move player1 paddle
	private void moveP1Paddle(float dx, float dz) {
		pLocation.setX(pLocation.x + dx);
		pLocation.setZ(pLocation.z + dz);
		
		move = new Transform3D();
		mover = new Vector3f(dx, 0f, dz);
		move.setTranslation(mover);
		Game.player1Pos.add(move);
		
		
		move = new Transform3D();
		mover = new Vector3f();
		Game.player1Pos.get(mover);
		move.setTranslation(mover);
		Game.player1XfmGrp.setTransform(move);

	}

	//Calculate new x and Y based on angle
	private Vector3f getXY(float angle) {
		Vector3f move = new Vector3f();
		float rad = (float) Math.toRadians(angle);
		float x = (float) Math.cos(rad);
		float z = (float) Math.sin(rad);
		move.setX(x);
		move.setZ(z);
		return move;
	}

}
