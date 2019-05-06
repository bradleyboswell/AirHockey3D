package model;

import javax.media.j3d.Appearance;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;

public class Paddle extends Cylinder{
	public static float radius = 0.03f;
	
	public Paddle(boolean player1, Appearance ap) {
		super(radius,0.03f,Primitive.ENABLE_APPEARANCE_MODIFY 
				| Primitive.GENERATE_NORMALS ,ap);
		if(player1) {
			this.setName("Paddle1");
			this.setUserData(this);	
		}else {
			this.setName("Paddle2");
			this.setUserData(this);	
		}
	}
}
