package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class World {

  private Vector3f translation;
  private Vector3f rotation;
  private float scale;

  public ArrayList<Object> objects;
  private TextureCache textureCache;
  private Map<String, Model> modelMap;
  private Entity cubeEntity;
  private Entity skullEntity;

  public World() {
    rotation = new Vector3f(0, 0, 0);
    translation = new Vector3f(0, 0, -5f);
    scale = 1;

    textureCache = new TextureCache();
    modelMap = new HashMap<>();

    makeObjects();
  }

  public void makeObjects() {

    Model testModel = ModelLoader.loadModel("cube", "resources/models/cube.obj", textureCache);
    addModel(testModel);

    cubeEntity = new Entity("cube-entity", testModel.getId());
    cubeEntity.setPosition(0f, 0f, 0f);
    cubeEntity.updateModelMatrix();
    addEntity(cubeEntity);

    Model testModel2 = ModelLoader.loadModel("skull", "resources/models/skull.obj", textureCache);
    addModel(testModel2);
    
    skullEntity = new Entity("cube-entity2", testModel2.getId());
    skullEntity.setPosition(1.5f, 0f, 0f);    
    skullEntity.updateModelMatrix();
    addEntity(skullEntity);

  }



  public Matrix4f getWorldMatrix() {
    Matrix4f worldMatrix = new Matrix4f();
    worldMatrix.identity().translate(translation).rotateX((float) Math.toRadians(rotation.x))
        .rotateY((float) Math.toRadians(rotation.y)).rotateZ((float) Math.toRadians(rotation.z)).scale(scale);
    return worldMatrix;
  }

  public Vector3f getTranslation() {
    return translation;
  }

  public void setTranslation(Vector3f translation) {
    this.translation = translation;
  }

  public Vector3f getRotation() {
    return rotation;
  }

  public void setRotation(Vector3f rotation) {
    this.rotation = rotation;

    if (rotation.x > 360f)
      rotation.x -= 360f;
    if (rotation.x < 0f)
      rotation.x += 360f;
    if (rotation.y > 360f)
      rotation.y -= 360f;
    if (rotation.y < 0f)
      rotation.y += 360f;
    if (rotation.z > 360f)
      rotation.z -= 360f;
    if (rotation.z < 0f)
      rotation.z += 360f;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  public void cleanUpObjects() {
  }

  public TextureCache getTextureCache() {
    return textureCache;
  }

  public void addModel(Model model) {
    modelMap.put(model.getId(), model);
  }

  public Map<String, Model> getModelMap() {
    return modelMap;
  }

  public void addEntity(Entity entity) {
    String modelId = entity.getModelId();
    Model model = modelMap.get(modelId);
    if (model == null) {
      throw new RuntimeException("Could not find model [" + modelId + "]");
    }
    model.getEntitiesList().add(entity);
  }
}
