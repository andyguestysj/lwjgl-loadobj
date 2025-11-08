package com.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;


public class Object {

private String name;
private String type;
private float size;
private	Vector3f translation;
private	Vector3f rotation;
private	float scale;
public ArrayList<Mesh> meshObjects;	


  public Object(String name, String type, float size, Vector3f translation, Vector3f rotation, Vector3f scale ) {
    this.name = name;
    this.type = type;
    this.size = size;
    this.translation = translation;
    this.rotation = rotation;
    this.scale = size;  
		meshObjects = new ArrayList<Mesh>();

    if (this.type.equals("Cube")) {
      meshObjects.add(makeCube(this.size));
    } else if (this.type.equals("Square")) {
			float[] col1 = {0.5f,0.0f,0.0f};
			float[] col2 = {0.0f,0.5f,0.0f};

			if (name.equals("col1"))
				meshObjects.add(makeSquare(this.size, col1));
			else
				meshObjects.add(makeSquare(this.size, col2));
    }
    
  }

public Matrix4f getTransforms(){
    Matrix4f myMatrix = new Matrix4f();

		if (!myImGui.getFlipRotation()){
    myMatrix.identity().translate(translation).
		  rotateX((float)Math.toRadians(rotation.x)).
      rotateY((float)Math.toRadians(rotation.y)).
      rotateZ((float)Math.toRadians(rotation.z)).
      
			scale(scale);
		} else {

			myMatrix.identity().rotateX((float)Math.toRadians(rotation.x)).
      rotateY((float)Math.toRadians(rotation.y)).
      rotateZ((float)Math.toRadians(rotation.z)).
      translate(translation).
			scale(scale);
		}
  return myMatrix;
}

public ArrayList<Mesh> getMeshes() {
    return meshObjects;
  }

public Mesh makeCube(float size){

		float offset = size / 2f;

		float[] positions = new float[]{
			// VO
			-offset,  offset,  offset,
			// V1
			-offset, -offset,  offset,
			// V2
			offset, -offset,  offset,
			// V3
			offset,  offset,  offset,
			// V4
			-offset,  offset, -offset,
			// V5
			offset,  offset, -offset,
			// V6
			-offset, -offset, -offset,
			// V7
			offset, -offset, -offset,
};

		float[] colors = new float[]{
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
		};
		int[] indices = new int[]{
		 // Front face
		 0, 1, 3, 3, 1, 2,
		 // Top Face
		 4, 0, 3, 5, 4, 3,
		 // Right face
		 3, 2, 7, 5, 3, 7,
		 // Left face
		 6, 1, 0, 6, 0, 4,
		 // Bottom face
		 2, 1, 6, 2, 6, 7,
		 // Back face
		 7, 6, 4, 7, 4, 5,
		};

    //System.out.println("Creating cube mesh with "+positions.length/3+" vertices and "+indices.length/3+" triangles.");
		return new Mesh(positions, colors, indices);
   

	}

	

	public Mesh makeSquare(float size, float[] col){
    float offset = size / 2f;
		float[] positions = new float[]{
			-offset, 0f, -offset,
			offset, 0f, -offset,
			offset, 0f, offset,
			-offset, 0f, offset
		};

		float[] colors = new float[]{
				col[0], col[1], col[2],
				col[0], col[1], col[2],
				col[0], col[1], col[2],
				col[0], col[1], col[2]
		};

		
		int[] indices = new int[]{
			0, 1, 2, // first triangle
			0, 2, 3  // second triangle
		};

		return new Mesh(positions, colors, indices);
	}


  public Vector3f getRotation() {
    return rotation;
  }
  public void setRotation(Vector3f rotation) {
    this.rotation = rotation;
  }
  public Vector3f getTranslation() {
    return translation;
  } 
  public void setTranslation(Vector3f translation) {
    this.translation = translation;
  } 
  public float getScale() {
    return scale;
  }
  public void setScale(float scale) {
    this.scale = scale;
  }

	public String getName() {
		return name;
	}

	public void cleanup(){
		
	}

}
