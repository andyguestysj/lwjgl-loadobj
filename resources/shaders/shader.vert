#version 330

layout (location=0) in vec3 position;
layout (location=2) in vec3 normals;
layout (location=1) in vec2 texCoord;

out vec3 fragPos;
out vec3 fragNormal;
out vec2 outTextCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main()
{
    vec4 worldPos = modelMatrix * vec4(position, 1.0);
    fragPos = worldPos.xyz;
    fragNormal = mat3(transpose(inverse(modelMatrix))) * normals;
    outTextCoord = texCoord;

    gl_Position = projectionMatrix * viewMatrix * worldPos;
}