package scenes;

import camera.Camera;
import entities.GameObject;
import renderers.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScene {

    private Renderer renderer = new Renderer();
    private Camera camera;
    private boolean isRunning = false;
    private List<GameObject> gameObjects = new ArrayList<>();

    public AbstractScene() {

    }

    public Camera getCamera() {
        return this.camera;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    public abstract void init();

    public void start() {
        for (GameObject gameObject : this.gameObjects) {
            gameObject.start();
            this.renderer.add(gameObject);
        }
        this.isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        if (!this.isRunning) {
            this.gameObjects.add(gameObject);
        } else {
            this.gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public abstract void update(float dt);
}
