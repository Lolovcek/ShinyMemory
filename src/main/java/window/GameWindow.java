package window;

import scene.*;
import listeners.*;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import utils.Time;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow {

    private int width;
    private int height;
    private int[] windowPosX = new int[1];
    private int[] windowPosY = new int[1];

    private long glfwWindow;

    private float r = 1f;
    private float g = 1f;
    private float b = 1f;
    private float alpha = 1f;
    private final String title;
    private boolean isResized;
    private boolean isFullscreen = false;


    private static GameWindow instance;
    private static AbstractScene currentScene;

    private GameWindow() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Shiny Memory";


    }

    public static GameWindow getGameWindow() {
            if (GameWindow.instance == null) {
                GameWindow.instance = new GameWindow();
            }
            return GameWindow.instance;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        loop();
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(this.glfwWindow);
        glfwDestroyWindow(this.glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        this.glfwWindow = glfwCreateWindow(this.width, this.height, this.title, this.isFullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
        if ( this.glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*


            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(this.glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            this.windowPosX[0] = (vidmode.width() - pWidth.get(0)) / 2;
            this.windowPosY[0] = (vidmode.height() - pHeight.get(0)) / 2;
            // Center the window
            glfwSetWindowPos(this.glfwWindow, this.windowPosX[0], this.windowPosY[0]);
        } // the stack frame is popped automatically

        //create callbacks
        createCallbacks();

        // Make the OpenGL context current
        glfwMakeContextCurrent(this.glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(this.glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Change to MenuScene on init
        changeScene(1);
    }

    private void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1;

        while (!glfwWindowShouldClose(this.glfwWindow) && !KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            if (KeyListener.isKeyPressed(GLFW_KEY_F11)) {
                setFullscreen(!this.isFullscreen);
                setIsResized(true);
                System.out.println("Setting to fullscreen is " + this.isFullscreen);
            }

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            // Set the clear color
            glClearColor(this.r, this.g, this.b, this.alpha);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(this.glfwWindow); // swap the color buffers

            if (this.isResized) {
                glViewport(0, 0, getWidth(), getHeight());
                if (dt > 0) {
                    System.out.println("FPS is " + Math.round(1/dt));
                }
                System.out.println("Window resolution is " + getWidth() + "x" + getHeight());
                this.isResized = false;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                System.out.println("Scene changed to GameScene: 1");

                changeScene(1);
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                System.out.println("Scene changed to MenuScene: 0");
                changeScene(0);
            }
            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new MenuScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new GameScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
        }
    }

    public void setFullscreen(boolean fullscreen) {
        this.isFullscreen = fullscreen;
        if (this.isFullscreen) {
            glfwGetWindowPos(this.glfwWindow, this.windowPosX, this.windowPosY);
            glfwSetWindowMonitor(this.glfwWindow, glfwGetPrimaryMonitor(), 0, 0, getWidth(), getHeight(), 0);
        }
        else {
            glfwSetWindowMonitor(this.glfwWindow, 0, this.windowPosX[0], this.windowPosY[0], getWidth(), getHeight(), 0);
        }
    }

    private void createCallbacks() {
        glfwSetWindowSizeCallback(this.glfwWindow, WindowListener::windowCallback);
        glfwSetKeyCallback(this.glfwWindow, KeyListener::keyCallback);
        glfwSetCursorPosCallback(this.glfwWindow, MouseListener::mousePosCallBack);
        glfwSetMouseButtonCallback(this.glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(this.glfwWindow, MouseListener::mouseScrollCallback);
    }

    public void setIsResized(boolean value) {
        this.isResized = value;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return this.title;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

}
