package listeners;

import org.lwjgl.glfw.GLFW;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[GLFW.GLFW_KEY_LAST];

    private KeyListener() {

    }

    public static KeyListener getKeyListener() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }

        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            getKeyListener().keyPressed[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            getKeyListener().keyPressed[key] = false ;

        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return getKeyListener().keyPressed[keyCode];
    }
}
