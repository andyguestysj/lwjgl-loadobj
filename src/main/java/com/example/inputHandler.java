package com.example;

import static org.lwjgl.glfw.GLFW.*;

import com.example.commands.*;
import com.example.gamestate.GameState;

public class inputHandler {
  
	private CommandQueue commandQueue = new CommandQueue();
  private Command[] commands = new Command[350];
	private Command[] mouseCommands = new Command[5];
	
	private boolean[] keyState = new boolean[350]; // Array to hold button states
	private boolean[] mouseButtonState = new boolean[5]; // Array to hold mouse button states

	private double mouseX, mouseY;
	private double deltaX, deltaY;
	private boolean firstMouse = true;
	private float mouseSensitivity = 0.1f;

	private Main main;

  public inputHandler(Main main, World world){
		// rotate world

		this.main = main;

		commands[GLFW_KEY_W] = setButton(new MoveCameraCommand(main, Command.Z, true));
		commands[GLFW_KEY_S] = setButton(new MoveCameraCommand(main, Command.Z, false));
		commands[GLFW_KEY_A] = setButton(new MoveCameraCommand(main, Command.X, false));
		commands[GLFW_KEY_D] = setButton(new MoveCameraCommand(main, Command.X, true));
		commands[GLFW_KEY_Z] = setButton(new MoveCameraCommand(main, Command.Y, true));
		commands[GLFW_KEY_X] = setButton(new MoveCameraCommand(main, Command.Y, false));


		// mouse commands
		mouseCommands[GLFW_MOUSE_BUTTON_LEFT] = setButton(new ExitMenuCommand(main));
	}

	public void processInput(){

		for (int i=0; i<commands.length; i++){
			if (commands[i] != null && isPressed(i)){
				commandQueue.addCommand(commands[i]);
			}
		}
		for (int i=0; i<mouseCommands.length; i++){
			if (mouseCommands[i] != null && mouseButtonState[i]){
				commandQueue.addCommand(mouseCommands[i]);
			}
		}

		if (main.currentGameState.getGameStateType() != GameState.GameStateType.GAME){			
			main.camera.setYaw(main.camera.getYaw()+(float)deltaX*mouseSensitivity);
			main.camera.setPitch(main.camera.getPitch()+(float)deltaY*mouseSensitivity);

			if (main.camera.getPitch()>89.0f) main.camera.setPitch(89.0f);
			if (main.camera.getPitch()<-89.0f) main.camera.setPitch(-89.0f);

			deltaX = 0f; deltaY = 0f;
		}
	}

  // Methods to bind commands...

  public Command setButton(Command aCommand){   
      return(aCommand);
  }

	public boolean isPressed(int button){
		return keyState[button];
	}

	public void pressKey(int button){
		keyState[button] = true;
	}

	public void releaseKey(int button){
		keyState[button] = false;
	}

	public void executeCommands() {
		commandQueue.executeCommands();
	}

	public void setMouseLeftButton(boolean state) {		
		mouseButtonState[GLFW_MOUSE_BUTTON_LEFT] = state;
	}

	public void setMousePosition(double x, double y) {
		if (firstMouse){
			this.mouseX = x;
			this.mouseY = y;
			firstMouse = false;
		}

		deltaX = x - this.mouseX; 
		deltaY = y - this.mouseY; 

		this.mouseX = x;
		this.mouseY = y;
	}

	public double getMouseX() { return mouseX; 	}	
	public double getMouseY() { return mouseY;	}
	public double getDeltaX() { return deltaX; }
	public double getDeltaY() { return deltaY; }

}
