package model;

import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;

public class Rink extends Shape3D{
	public static final float length = 0.96f;
	public static final float width = 0.72f;
	public static final float depth = 0.025f;
	
	//720x960
	public Rink() {	
		IndexedQuadArray rink = new IndexedQuadArray(8, IndexedQuadArray.COORDINATES| IndexedQuadArray.NORMALS | IndexedQuadArray.TEXTURE_COORDINATE_2,24);
		Point3f[] vertices = {
				new Point3f(0,0,0), new Point3f(0,0,length), new Point3f(width,0,length), new Point3f(width,0,0),				//Top face
				new Point3f(0,-depth,0), new Point3f(0,-depth,length), new Point3f(width,-depth,length), new Point3f(width,-depth,0)		//Bottom face
			};
		int[] indices = {0,1,2,3,  0,4,5,1,  1,5,6,2,  2,6,7,3,  3,7,4,0,  4,5,6,7};	//Top,  Left,  Back,  Right,  Front,  Bottom 
	    TexCoord2f[] textCoord = { new TexCoord2f(1.0f, 1.0f),
	            new TexCoord2f(0.0f, 1.0f), new TexCoord2f(0.0f, 0.0f),
	            new TexCoord2f(1.0f, 0.0f) };
		int[] texIndices = {3,0,1,2};
	    Vector3f[] normals = { new Vector3f(0.0f, 1.0f, 0.0f),
	        new Vector3f(-1.0f, 0.0f, 0.0f),
	        new Vector3f(0.0f, 0.0f, -1.0f),
	        new Vector3f(1.0f, 0.0f, 0.0f),
	        new Vector3f(0.0f, 0.0f, 1.0f), 
	        new Vector3f(0.0f, -1.0f, 0.0f) };
	    int normalIndices[] = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
	            4, 4, 4, 4, 5, 5, 5, 5 };
		rink.setCoordinates(0,vertices);
		rink.setCoordinateIndices(0,indices);
		rink.setTextureCoordinates(0, 0,textCoord);
		rink.setTextureCoordinateIndices(0,0,texIndices);
		rink.setNormals(0, normals);
		rink.setNormalIndices(0, normalIndices);
		GeometryInfo info = new GeometryInfo(rink);
		this.setGeometry(info.getGeometryArray());
	}
	
}
