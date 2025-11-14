package com.example;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glMatrixMode;


import java.nio.IntBuffer;

import com.example.gamestate.*;

public class Main {

	// The window handle
	private long window;	
	public ArrayList<Mesh> meshObjects;	
	public Matrix4f projectionMatrix;
	public Matrix4f worldMatrix;
	public inputHandler inputHandler;
	public World world;
	public myImGui myImGui;

	public GameState currentGameState;
	public GameState game;
	public GameState menu;
	public Camera camera;
	public Uniforms uniforms;
	
	public int WIDTH = 1000;
	public int HEIGHT = 1000;
	private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float Z_NEAR = 0.01f;
	private static final float Z_FAR = 1000.f;
	private static final float PITCH = 0f;
	private static final float YAW = 0f;
	private static final float ROLL = 0f;

	
	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	public void run() throws Exception {
		init_window();		
		uniforms = new Uniforms();
		myImGui = new myImGui(this, window);

		game = new GameStateGame();
		menu = new GameStateMenu();
		currentGameState = game;

		world = new World();
		inputHandler = new inputHandler(this, world);
		gameLoop();
		cleanup();
	}

	private void gameLoop() {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {		
			GL32.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GL32.glClear(GL32.GL_COLOR_BUFFER_BIT);
			
			currentGameState.update(inputHandler);

			currentGameState.render(this);

			myImGui.update(inputHandler);

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	
	private void init_window() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			keyCallBack(key, action);
		});
		glfwSetMouseButtonCallback(window, (window, button, action, mods) ->  { mouseButtonCallback(window, button, action, mods); } );
		glfwSetCursorPosCallback(window, (window, xpos, ypos) ->  { mousePositionCallback(window, xpos, ypos); } );

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		camera = new Camera(new Vector3f(0, 0, 8), new Vector3f(0f,0f,0f), new Vector3f(0f,1f,0f), WIDTH, HEIGHT, Z_NEAR, Z_FAR, FOV, PITCH, YAW, ROLL);		

		// Make the window visible
		glfwShowWindow(window);
		GL.createCapabilities();
		
		glMatrixMode(GL_MODELVIEW);
		glClearColor( 0.0F, 0.0F, 0.0F, 1 );
		glEnable(GL_DEPTH_TEST);
	}

	public void cleanup() {
		world.cleanUpObjects();
		myImGui.cleanup();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
}

	public void keyCallBack(int key, int action) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop				
		}

		for (int i=0; i<350; i++) {
			if (key == i && action == GLFW_PRESS) inputHandler.pressKey(i);
			else if (key == i && action == GLFW_RELEASE) inputHandler.releaseKey(i);
		}
	}

	public void mouseButtonCallback(long window, int button, int action, int mods){
		if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
			inputHandler.setMouseLeftButton(true);
		}
		else if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
			inputHandler.setMouseLeftButton(false);
		}
	}

	public void mousePositionCallback(long window, double xpos,double ypos){
		inputHandler.setMousePosition(xpos, ypos);
	}		
}