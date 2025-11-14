package com.example;

import org.joml.Vector3f;

public class Light {
  public Vector3f position;
  public Vector3f color;
  public float intensity;

  public Light(Vector3f position, Vector3f color, float intensity) {
    this.position = position;
    this.color = color;
    this.intensity = intensity;
  }
}
