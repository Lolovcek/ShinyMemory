package scenes;
import camera.Camera;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderers.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GameScene extends AbstractScene {

    private float[] vertexArray = {
            // position               // color
             100.5f, -0.5f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
            -0.5f,  100.5f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left     1
             100.5f,  100.5f, 0.0f ,      0.0f, 0.0f, 1.0f, 1.0f, // Top right    2
            -0.5f, -0.5f, 0.0f,       0.0f, 0.0f, 0.0f, 1.0f, // Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // bottom left triangle
    };

    private int vaoID;

    private Shader defaultShader;

    private boolean touchedLeftSide = true, touchedRightSide = false;

    public GameScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        this.defaultShader = new Shader("assets/shaders/defaultShader.glsl");
        this.defaultShader.compile();

        this.vaoID = this.defaultShader.prepare(this.vertexArray, this.elementArray, 3, 4, 0, 1);
    }

    @Override
    public void update(float dt) {
        float temp = dt * 1000.0f;
        if (touchedLeftSide) {
            if ((this.camera.position.x - temp) <= -10230) {
                this.camera.position.x = -10230;
                touchedLeftSide = false;
                touchedRightSide = true;
                System.out.println("Touched right side at " + this.camera.position.x + " and temp is " + temp);
            } else {
                this.camera.position.x -= temp;
            }
        }
        if (touchedRightSide) {
            if ((this.camera.position.x + temp) >= 0) {
                this.camera.position.x = 0;
                touchedLeftSide = true;
                touchedRightSide = false;
                System.out.println("Touched left side at " + this.camera.position.x + " and temp is " + temp);
            }
            this.camera.position.x += temp;
        }
        this.defaultShader.use();
        this.defaultShader.uploadMat4f("uProjectionMatrix", camera.getProjectionMatrix());
        this.defaultShader.uploadMat4f("uViewMatrix", camera.getViewMatrix());

        this.defaultShader.draw(this.vaoID, this.elementArray.length, 0, 1);

        this.defaultShader.detach();
    }
}