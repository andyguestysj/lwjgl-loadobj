package com.example;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class Uniforms {

  public Map<String, Integer> uniforms;
  public int programID;

  
  public Uniforms() throws Exception {
 		uniforms = new HashMap<>();
		programID = Shaders.makeShaders();		
		createUniform("projectionMatrix");
		createUniform("viewMatrix");
		createUniform("modelMatrix");    
		createUniform("txtSampler");
    createUniform("material.diffuse");
  }

	public void createUniform(String uniformName) throws Exception {
		int uniformLocation = glGetUniformLocation(programID, uniformName);
		if (uniformLocation < 0) {
			System.out.println("createUniform error");
			throw new Exception("Could not find uniform:" + uniformName);				
		}
		uniforms.put(uniformName, uniformLocation);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		// Dump the matrix into a float buffer
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
		}
	}

		public void setUniform(String uniformName, Vector3f value) {
			glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
	}

		public void setUniform(String uniformName, float value) {
			glUniform1f(uniforms.get(uniformName), value);
	}
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }
	
}