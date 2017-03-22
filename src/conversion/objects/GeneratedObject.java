package conversion.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the geometry of a newly generated object.
 * @author michael
 *
 */
public class GeneratedObject {
	List<Face> faces = new ArrayList<GeneratedObject.Face>();
	
	public GeneratedObject() {
		
	}
	
	public void addFace(Face face) {
		faces.add(face);
	}
	
	public List<Face> getFaces() {
	    return faces;
    }
	
	public class Face {
		private final Vector[] points;
		private final TextureLink texture;

		public Face(Vector[] points, TextureLink texture) {
			this.points = points;
			this.texture = texture;
		}
	}
	
	public class TextureLink {
		
	}
	
	public class Vector {
		private final float x;
		private final float y;
		private final float z;
		private final float u;
		private final float v;

		public Vector(float x, float y, float z, float u, float v) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = u;
			this.v = v;
		}
		
		public float getX() {
	        return x;
        }
		public float getY() {
	        return y;
        }
		public float getZ() {
	        return z;
        }
		public float getU() {
	        return u;
        }
		public float getV() {
	        return v;
        }
	}
}
