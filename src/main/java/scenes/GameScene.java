package scenes;
import camera.Camera;
import components.SpriteRenderer;
import entities.GameObject;
import entities.Transform;
import org.joml.Vector2f;
import utils.AssetPool;

import static org.lwjgl.opengl.GL11.*;


public class GameScene extends AbstractScene {

    private final float AMOUNT = 200.0f;
    public GameScene() {

    }

    @Override
    public void init() {
        setCamera(new Camera(new Vector2f()));

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/goomba.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/player.png")));
        this.addGameObjectToScene(obj2);

        loadResources();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/defaultShader.glsl");
    }

    @Override
    public void update(float dt) {
        for (GameObject gameObject : getGameObjects()) {
            gameObject.update(dt);
        }

        getRenderer().render();
    }
}