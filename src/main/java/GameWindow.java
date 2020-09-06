import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow {

    private int width;
    private int height;
    private String title;
    private long glfwWindow;
    private int fps;
    private long currentTime;
    private Input input;

    private static GameWindow gameWindow = null;

    private GameWindow() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Shiny Memory";
    }

    public static GameWindow getGameWindow() {
        if (gameWindow == null) {
            gameWindow = new GameWindow();
        }
        return GameWindow.gameWindow;
    }


    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(this.glfwWindow)) {
            loop();
            if (Input.isKeyDown(GLFW_KEY_ESCAPE)) {
                return;
            }
        }
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(this.glfwWindow);
        glfwDestroyWindow(this.glfwWindow);

        // Destroys the input class
        destroyInput();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        glfwInitHint(GLFW_COCOA_MENUBAR, GLFW_TRUE);

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Create input singleton
        this.input = Input.getInput();

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        this.glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if ( this.glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(this.glfwWindow, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Set inputs.
        glfwSetKeyCallback(this.glfwWindow, this.input.getKeyCallback());
        glfwSetCursorPosCallback(this.glfwWindow, this.input.getCursorPosCallback());
        glfwSetMouseButtonCallback(this.glfwWindow, this.input.getMouseButtonCallback());


        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(this.glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    this.glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(this.glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(this.glfwWindow);

        this.currentTime = System.currentTimeMillis();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glfwSwapBuffers(this.glfwWindow); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        this.fps++;
        if (System.currentTimeMillis() > currentTime + 1000) {
            this.currentTime = System.currentTimeMillis();
            glfwSetWindowTitle(this.glfwWindow, getTitle() + " | FPS: " + this.fps);
            this.fps = 0;
        }

        if (Input.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            System.out.println("X: " + this.input.getMouseX() + " | Y: " + this.input.getMouseY());
        }

        if (Input.isKeyDown(GLFW_KEY_A)) {
            System.out.println("Key A pressed!");
        }

    }

    public void destroyInput() {
        this.input.destroy();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
