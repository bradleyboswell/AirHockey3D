package model;


import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

public class Goal extends Shape3D{
	
	public Goal(boolean p1) {	
		IndexedQuadArray goal = new IndexedQuadArray(8, IndexedQuadArray.COORDINATES| IndexedQuadArray.NORMALS | IndexedQuadArray.TEXTURE_COORDINATE_2,24);
		
		//Generate player 1 and 2's Goals
		if(p1) {	
			Point3f[] vertices = {
					new Point3f(0.24f,Rink.depth,-0.00001f), new Point3f(0.48f,Rink.depth,-0.00001f), 
					new Point3f(0.48f,Rink.depth,Rink.depth+0.00001f), new Point3f(0.24f,Rink.depth,Rink.depth+0.00001f),				//Top face
					new Point3f(0.24f,0f,-0.00001f), new Point3f(0.48f,0f,-0.00001f), 
					new Point3f(0.48f,0f,Rink.depth+0.0001f), new Point3f(0.24f,0f,Rink.depth+0.00001f)		//Bottom face
				};
			int[] indices = {0,1,2,3,  0,4,5,1,  1,5,6,2,  2,6,7,3,  3,7,4,0,  4,5,6,7};	//Top,  Left,  Back,  Right,  Front,  Bottom 
			Vector3f[] normals = { new Vector3f(0.0f, 1.0f, 0.0f),
				       new Vector3f(1.0f, 0.0f, 0.0f),
				       new Vector3f(0.0f, 0.0f, 1.0f),
				       new Vector3f(1.0f, 0.0f, 0.0f),
				       new Vector3f(0.0f, 0.0f, -1.0f), 
				       new Vector3f(0.0f, -1.0f, 0.0f)};
			int normalIndices[] = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
				            4, 4, 4, 4, 5, 5, 5, 5 };

			goal.setCoordinates(0,vertices);
			goal.setCoordinateIndices(0,indices);
			goal.setNormals(0, normals);
			goal.setNormalIndices(0, normalIndices);
			
		}else {
			Point3f[] vertices = {
					new Point3f(0.24f,Rink.depth,Rink.length-Rink.depth-0.00001f), new Point3f(0.48f,Rink.depth,Rink.length-Rink.depth-0.00001f), 
					new Point3f(0.48f,Rink.depth,Rink.length+0.00001f), new Point3f(0.24f,Rink.depth,Rink.length+0.00001f),				//Top face
					new Point3f(0.24f,0f,Rink.length-Rink.depth-0.00001f), new Point3f(0.48f,0f,Rink.length-Rink.depth-0.00001f), 
					new Point3f(0.48f,0f,Rink.length+0.0001f), new Point3f(0.24f,0f,Rink.length+0.00001f)		//Bottom face
				};
			int[] indices = {0,1,2,3,  0,4,5,1,  1,5,6,2,  2,6,7,3,  3,7,4,0,  4,5,6,7};	//Top,  Left,  Back,  Right,  Front,  Bottom 
			Vector3f[] normals = { new Vector3f(0.0f, 1.0f, 0.0f),
				       new Vector3f(1.0f, 0.0f, 0.0f),
				       new Vector3f(0.0f, 0.0f, 1.0f),
				       new Vector3f(1.0f, 0.0f, 0.0f),
				       new Vector3f(0.0f, 0.0f, -1.0f), 
				       new Vector3f(0.0f, -1.0f, 0.0f) };
			int normalIndices[] = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
				            4, 4, 4, 4, 5, 5, 5, 5 };

			goal.setCoordinates(0,vertices);
			goal.setCoordinateIndices(0,indices);
			goal.setNormals(0, normals);
			goal.setNormalIndices(0, normalIndices);
		}
		GeometryInfo info = new GeometryInfo(goal);
		this.setGeometry(info.getGeometryArray());	
	}
}