package model;

import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

public class Walls extends Shape3D{
	public static final float length = 0.96f;
	public static final float width = 0.72f;
	public static final float depth = 0.010f;
	public static enum Side{LEFT, RIGHT, FAR, NEAR}
	
	public Walls(Side wall) {
		GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
		Point3d[] vertices = {
				new Point3d(0,0,0), new Point3d(0,0,length), new Point3d(width,0,length), new Point3d(width,0,0),	//Bottom face Outer
				new Point3d(depth,0,depth), new Point3d(depth,0,length-depth), 										//Bottom face inner
					new Point3d(width-depth,0,length-depth), new Point3d(width-depth,0,depth),										
				
				new Point3d(0,depth,0), new Point3d(0,depth,length), new Point3d(width,depth,length), new Point3d(width,depth,0),	//Top face outer
				new Point3d(depth,depth,depth), new Point3d(depth,depth,length-depth), 												//Top face inner
					new Point3d(width-depth,depth,length-depth), new Point3d(width-depth,depth,depth)
				};
		int[] stripCounts = {4,4,4,4,4,4};
		int[] normalIndices = { 0,0,0,0, 1,1,1,1, 2,2,2,2, 3,3,3,3, 4,4,4,4, 5,5,5,5};
		
		switch(wall) {
			case RIGHT:
				int[] rIndices = {0,1,5,4,  8,9,13,12, 0,4,12,8, 0,8,9,1, 1,9,13,5, 5,13,12,4};   //Bottom, Top, Near, Right, Far, Left
				Vector3f[] rNormals = {
						new Vector3f(0.0f,-1.0f,0.0f),
						new Vector3f(0.0f,1.0f, 0.0f),
						new Vector3f(0.0f,0.0f,1.0f),
						new Vector3f(1.0f,0.0f,0.0f),
						new Vector3f(0.0f,0.0f,1.0f),
						new Vector3f(-1.0f,0.0f,0.0f)
				};
				setGeo(vertices, rIndices, stripCounts, rNormals, normalIndices, gi);
				this.setName("rWall");
				this.setUserData("User Data: Right Wall");
				break;
			case LEFT:
				int[] lIndices = {3,2,6,7,  11,10,14,15, 3,11,15,7, 7,15,14,6, 6,14,10,2, 2,10,11,3};					//Bottom, Top, Near, Right, Far, Left
				Vector3f[] lNormals = {
						new Vector3f(0.0f,-1.0f,0.0f),
						new Vector3f(0.0f,1.0f, 0.0f),
						new Vector3f(0.0f,0.0f,1.0f),
						new Vector3f(1.0f,0.0f,0.0f),
						new Vector3f(0.0f,0.0f,1.0f),
						new Vector3f(-1.0f,0.0f,0.0f)
				};
				setGeo(vertices, lIndices, stripCounts, lNormals,normalIndices, gi);
				this.setName("lWall");
				this.setUserData("User Data: Left Wall");
				break;
			case FAR:
				int[] fIndices = {1,2,6,5, 9,10,14,13, 5,6,14,13, 5,13,9,1, 1,9,10,2,  2,10,14,6};		//Bottom, Top, Near, Right, Far, Left
				Vector3f[] fNormals = {
						new Vector3f(0.0f,-1.0f,0.0f),
						new Vector3f(0.0f,1.0f, 0.0f),
						new Vector3f(0.0f,0.0f,1.0f),
						new Vector3f(1.0f,0.0f,0.0f),
						new Vector3f(0.0f,0.0f,-1.0f),
						new Vector3f(-1.0f,0.0f,0.0f)
				};
				setGeo(vertices, fIndices, stripCounts, fNormals,normalIndices, gi);
				this.setName("fWall");
				this.setUserData("User Data: Far Wall");
				break;
			case NEAR:
				int[] nIndices = {0,3,7,4, 8,11,15,12,  0,3,11,8,  0,8,12,4,  4,12,15,7, 7,15,11,3};		//Bottom, Top, Near, Right, Far, Left
				Vector3f[] nNormals = {
						new Vector3f(0.0f,-1.0f,0.0f),
						new Vector3f(0.0f,1.0f, 0.0f),
						new Vector3f(0.0f,0.0f,1.0f),
						new Vector3f(1.0f,0.0f,0.0f),
						new Vector3f(0.0f,0.0f,-1.0f),
						new Vector3f(-1.0f,0.0f,0.0f)
				};
				setGeo(vertices, nIndices, stripCounts,nNormals, normalIndices, gi);
				this.setName("nWall");
				this.setUserData("User Data: Near Wall");
				break;
		}
	}
	public void setGeo(Point3d[] vertices, int[] indices, int[] stripCounts, Vector3f[] normals, int[] normalIndices, GeometryInfo gi) {
		gi.setCoordinates(vertices);
		gi.setCoordinateIndices(indices);
		gi.setStripCounts(stripCounts);
		gi.setNormals(normals);
		gi.setNormalIndices(normalIndices);
	    this.setGeometry(gi.getGeometryArray());
	}
}
