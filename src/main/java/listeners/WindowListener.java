package listeners;

import window.GameWindow;

public class WindowListener {
    private static WindowListener instance;

    private WindowListener() {

    }

    public static WindowListener getWindowListener() {
        if (WindowListener.instance == null) {
            WindowListener.instance = new WindowListener();
        }

        return WindowListener.instance;
    }

    public static void windowCallback(long window, int width, int height) {
        getWindowListener();
        GameWindow.getGameWindow().setWidth(width);
        GameWindow.getGameWindow().setHeight(height);
        GameWindow.getGameWindow().setIsResized(true);
    }
}
