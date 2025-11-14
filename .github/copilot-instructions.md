# Copilot Instructions for `lwjgl-loadobj`

These notes teach AI coding agents how to work effectively in this LWJGL + OpenGL + ImGui Java project.

## Big Picture
- Java 21 desktop app using LWJGL 3, JOML, STB, Assimp, and ImGui.
- Entry point: `com.example.Main` starts GLFW, creates the GL context, sets up `Camera`, `Uniforms` (compiles shaders), `myImGui` overlay, and a `World` of models/entities. Game loop: input → update → render.
- Rendering is driven by game states: `gamestate/GameStateGame` (draw world) and `gamestate/GameStateMenu` (UI-only scene). Switch via ImGui buttons or `ExitMenuCommand` (LMB).

## Build & Run
- Dependencies via Maven; natives are pulled automatically.
- Build (compile only):
```powershell
mvn -q clean compile
```
- Run (IDE): debug `Main.main()`.
- Run (CLI): uses exec plugin and unpacks natives:
```powershell
mvn -q clean package exec:java
```

## Rendering Pipeline
- Shaders: `resources/shaders/shader.vert`, `shader.frag`. Program is built in `Shaders.makeShaders()` and stored in `Uniforms.programID`.
  - Attributes: `layout(location=0) vec3 position`, `layout(location=1) vec2 texCoord`.
  - Uniforms created in `Uniforms`: `projectionMatrix`, `viewMatrix`, `modelMatrix`, `txtSampler`, `material.diffuse`.
- Per-frame (`GameStateGame.render`):
  - Set view/projection from `Camera` (`getViewMatrixFPS`) and bind texture unit 0.
  - Iterate `World.getModelMap()` → each `Model` → each `Material` → each `Mesh` → each `Entity`:
    - Bind texture (`TextureCache.getTexture(material.getTexturePath())`).
    - Bind VAO (`Mesh.getVaoId()`), set `modelMatrix` from `Entity.getModelMatrix()`, `glDrawElements` with mesh index count.
- Mesh layout (`Mesh`): creates a VAO with two VBOs: positions (loc 0, 3 floats) and texture coords (loc 1, 2 floats), plus an index EBO. `getNumVertices()` returns index count.

Tip: Shaders live in `resources/shaders`. To add a new uniform:
- Add it in GLSL, then in `Uniforms` call `createUniform("name")` and set via `setUniform(...)` before drawing.

## Assets & Model Loading
- Models live under `resources/models`. Load with `ModelLoader.loadModel(id, path, textureCache)`.
  - Assimp flags: triangulate, gen normals, fix normals, pre-transform, etc.
  - `Material` reads diffuse color and optional diffuse texture; when a texture exists, `material.diffuse` is set to `DEFAULT_COLOR` and the texture path is recorded.
  - `TextureCache` provides a shared texture map; default fallback: `resources/models/default_texture.png`.
- Example (see `World.makeObjects`): load `cube.obj`, `skull.obj`, then create `Entity`s, set position/scale, call `updateModelMatrix()`, and `addEntity` to the model.

## Input & Commands
- Input uses a command queue (`commands` package). Keys are mapped in `inputHandler`:
  - `W/S` move forward/back; `A/D` strafe; `Z/X` vertical. Movement is FPS-style using yaw.
  - Mouse: left button triggers `ExitMenuCommand` (switches to GAME when not already).
  - Mouse-look (yaw/pitch) updates when NOT in GAME state (menu/cursor mode), per current code.
- To add input, implement `Command`, then assign it in `inputHandler.commands[key]` or `mouseCommands[button]`.

## Game State Flow
- `GameStateGame`: clears buffers, `glUseProgram`, sets `viewMatrix`/`projectionMatrix`, renders world.
- `GameStateMenu`: sets matrices but renders no meshes; ImGui shows controls (buttons to switch states, live data).

## Common Tasks
- Add a model + entity (pattern from `World.makeObjects`):
  1) `Model m = ModelLoader.loadModel("id", "resources/models/my.obj", textureCache); addModel(m);`
  2) `Entity e = new Entity("id-entity", m.getId()); e.setPosition(...); e.updateModelMatrix(); addEntity(e);`
- Bind a new key: in `inputHandler`, set `commands[GLFW_KEY_…] = setButton(new MyCommand(...));`.
- New mesh from code: construct `new Mesh(positions, texCoords, indices)` matching the shader attribute layout.

## Conventions & Notes
- Math: JOML everywhere (`Matrix4f`, `Vector3f`, `Quaternionf`). After changing an `Entity`, call `updateModelMatrix()`.
- Asset paths are read from the filesystem (not classpath). Keep relative paths like `resources/...`.
- Cleanup: `Mesh.cleanup()` deletes VBOs/VAO; `Material.cleanup()` cleans meshes; `Model.cleanup()` cleans materials. `World.cleanUpObjects()` is currently a no-op.
- Shader compile/link logs are printed to stdout for quick debugging.

## Normals & Lighting
- Current status: Assimp is invoked with `GenSmoothNormals` and `CalcTangentSpace`, but normals are not read or uploaded; shaders don’t declare normals and no lighting is computed.
- Attribute plan if adding lighting: use `location=2` for `vec3 normal`.
- Shader changes:
  - Vertex: `layout(location=2) in vec3 normal;` compute a normal in view/model space using a normal matrix: `mat3 normalMatrix = transpose(inverse(mat3(viewMatrix * modelMatrix))); out vec3 vNormal = normalize(normalMatrix * normal);` pass along position if needed.
  - Fragment: add light uniforms (e.g., `uniform vec3 lightDir; uniform vec3 lightColor; uniform float ambient;`) and compute Lambert/Blinn-Phong using `normalize(vNormal)` and your chosen model; combine with texture sample and `material.diffuse`.
- Java changes:
  - `Mesh`: accept a `float[] normals` parameter; create a VBO, `glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0); glEnableVertexAttribArray(2);`.
  - `ModelLoader`: extract normals from `aiMesh.mNormals()`; if null, fallback to zeroes or rely on Assimp’s generated normals. Keep arrays aligned with positions.
  - `GameStateGame`: set light uniforms prior to draw; keep depth test enabled.
- OBJ note: Many OBJ files omit normals; Assimp’s smooth normal generation covers that. Tangents/bitangents are available for normal mapping if extended later.

## Troubleshooting
- If CLI run can’t find natives: ensure `mvn clean package exec:java` (unpacks to `target/natives`).
- If models don’t appear: verify OBJ/MTL/texture filenames under `resources/models` and that `material.diffuse`/textures match the shader.
- Mouse-look only updates outside GAME by design (see `inputHandler` + `GameState`).

If anything above is unclear or you need additional workflow details (e.g., adding a Maven exec/shade plugin for CLI runs), ask and we’ll refine this document.
