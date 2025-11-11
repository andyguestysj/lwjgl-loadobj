package com.example.gamestate;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import com.example.*;

import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class GameStateGame extends GameState {

  public GameStateType state;

  public GameStateGame() {
    state = GameStateType.GAME;
  }

  public void initialise() {
  }

  public void update(inputHandler inputHandler) {
    // read input
    inputHandler.processInput();
    // update
    inputHandler.executeCommands();
  }

  public void render(Main main) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

    glUseProgram(main.uniforms.programID);
    if (glGetProgrami(main.uniforms.programID, GL_LINK_STATUS) == 0) {
      throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(main.uniforms.programID, 1024));
    }

    main.uniforms.setUniform("viewMatrix", main.camera.getViewMatrixFPS());
    main.uniforms.setUniform("projectionMatrix", main.camera.getPerspectiveProjectionMatrix());

    main.uniforms.setUniform("txtSampler", 0);

    Collection<Model> models = main.world.getModelMap().values();
    TextureCache textureCache = main.world.getTextureCache();

    int count=0;

    for (Model model : models) {
      List<Entity> entities = model.getEntitiesList();

      for (Material material : model.getMaterialList()) {
        main.uniforms.setUniform("material.diffuse", material.getDiffuseColor());
        Texture texture = textureCache.getTexture(material.getTexturePath());
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        for (Mesh mesh : material.getMeshList()) {          
          glBindVertexArray(mesh.getVaoId());
          for (Entity entity : entities) {
            main.uniforms.setUniform("modelMatrix", entity.getModelMatrix());            
            glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
          }
        }
      }
    }

    glBindVertexArray(0);
    glUseProgram(0);
  }

}
