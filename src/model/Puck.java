package model;

import javax.media.j3d.Appearance;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;

public class Puck extends Cylinder{
		public static float radius = 0.02f;
		
		public Puck(Appearance ap) {
			super(radius,0.005f, Primitive.ENABLE_APPEARANCE_MODIFY 
					| Cylinder.GENERATE_NORMALS ,ap);
			this.setName("Puck");
		}
}
