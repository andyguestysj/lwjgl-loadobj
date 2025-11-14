package com.example.gamestate;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import org.joml.Vector3f;

import com.example.inputHandler;
import com.example.Main;

public class GameStateMenu extends GameState {

  public GameStateType state;

  
  

  public GameStateMenu(){
    state = GameStateType.MENU;    
    initialise();
  }  

  public void initialise(){
    
  }

  public void update(inputHandler inputHandler){    
    // read input
    inputHandler.processInput();
    // update
    inputHandler.executeCommands();
  }  

  public void render(Main main){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		glUseProgram(main.uniforms.programID);
		if (glGetProgrami(main.uniforms.programID, GL_LINK_STATUS) == 0) {			
			throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(main.uniforms.programID, 1024));
		}

    main.uniforms.setUniform("viewMatrix", main.camera.getViewMatrix());
    main.uniforms.setUniform("projectionMatrix", main.camera.getPerspectiveProjectionMatrix());

		glBindVertexArray(0);
		glUseProgram(0);
  }


  
}

