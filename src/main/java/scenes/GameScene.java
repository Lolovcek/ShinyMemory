package scenes;
import org.lwjgl.BufferUtils;
import renderers.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GameScene extends AbstractScene {

    private float[] vertexArray = {
            // position               // color
             0.5f, -0.5f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
            -0.5f,  0.5f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left     1
             0.5f,  0.5f, 0.0f ,      0.0f, 0.0f, 1.0f, 1.0f, // Top right    2
            -0.5f, -0.5f, 0.0f,       0.0f, 0.0f, 0.0f, 1.0f, // Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // bottom left triangle
    };

    private int vaoID;

    private Shader defaultShader;

    public GameScene() {

    }

    @Override
    public void init() {
        defaultShader = new Shader("assets/shaders/defaultShader.glsl");
        defaultShader.compile();

        this.vaoID = defaultShader.prepare(this.vertexArray, this.elementArray, 3, 4, 0, 1);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        defaultShader.draw(this.vaoID, this.elementArray.length, 0, 1);

        defaultShader.detach();
    }
}