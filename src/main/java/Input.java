import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input {
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX;
    private static double mouseY;

    private static Input input = null;

    public Input() {
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
            }
        };

        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtons[button] = (action != GLFW.GLFW_RELEASE);
            }
        };
    }

    public static Input getInput() {
        if (input == null) {
            input = new Input();
        }
        return Input.input;
    }

    public static boolean isKeyDown(int key) {
        return keys[key];
    }

    public static boolean isMouseButtonDown(int button) {
        return mouseButtons[button];
    }

    public void destroy() {
        keyCallback.free();
        cursorPosCallback.free();
        mouseButtonCallback.free();
    }

    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    public GLFWCursorPosCallback getCursorPosCallback() {
        return cursorPosCallback;
    }

    public GLFWMouseButtonCallback getMouseButtonCallback() {
        return mouseButtonCallback;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }
}
