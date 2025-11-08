package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;

public class World {

  private Vector3f translation;
  private Vector3f rotation;
  private float scale;

  public ArrayList<Object> objects;
  private TextureCache textureCache;
  private Map<String, Model> modelMap;
  private Entity cubeEntity;

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
    cubeEntity.setPosition(0, 0, -2);
    addEntity(cubeEntity);

    objects = new ArrayList<Object>();

    objects.add(new Object("Cube1", "Cube", 1f, new Vector3f(0f, 0.5f, 0f), new Vector3f(0f, 0.0f, 0f),
        new Vector3f(1f, 1f, 1f)));
    objects.add(new Object("Cube2", "Cube", 0.5f, new Vector3f(2f, 0.25f, 0f), new Vector3f(0f, 0.0f, 0f),
        new Vector3f(1f, 1f, 1f)));

    int count = 0;

    for (int x = 0; x < 11; x++) {
      for (int z = 0; z < 11; z++) {
        Vector3f translate = new Vector3f(0f, 0f, 0f);
        translate.x = (float) x - 4.5f;
        translate.z = (float) z - 4.5f;
        if (count % 2 == 0) {
          objects
              .add(new Object("col1", "Square", 1f, translate, new Vector3f(0f, 0.0f, 0f), new Vector3f(1f, 1f, 1f)));
        } else {
          objects
              .add(new Object("col2", "Square", 1f, translate, new Vector3f(0f, 0.0f, 0f), new Vector3f(1f, 1f, 1f)));
        }
        count++;
      }
    }
  }

  public Object getObject(String name) {
    for (Object obj : objects) {
      if (obj.getName().equals(name)) {
        return obj;
      }
    }
    return null;
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
    for (Object anObject : objects) {
      anObject.cleanup();
    }
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
