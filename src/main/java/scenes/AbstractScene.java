package scenes;

import camera.Camera;

public abstract class AbstractScene {

    protected Camera camera;

    public AbstractScene() {

    }

    public abstract void init();

    public abstract void update(float dt);
}
