package scenes;
import camera.Camera;
import org.joml.Vector2f;
import renderers.Shader;
import renderers.Texture;
import utils.Time;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;


public class GameScene extends AbstractScene {

    private float[] vertexArray = {
            // position               // color                          // UV coordinates
             100.0f, 0.0f,   0.0f,       1.0f, 0.0f, 0.0f, 1.0f,        1, 1,// Bottom right 0
             0.0f,   100.0f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f,        0, 0,// Top left     1
             100.0f, 100.0f, 0.0f ,      0.0f, 0.0f, 1.0f, 1.0f,        1, 0,// Top right    2
             0.0f,   0.0f,   0.0f,       0.0f, 0.0f, 0.0f, 1.0f,        0, 1// Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // bottom left triangle
    };

    private int vaoID;

    private Shader defaultShader;
    private Texture testTexture;

    private boolean touchedLeftSide = false, touchedRightSide = false, touchedUpSide = false, touchedBottomSide = true;

    public GameScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        this.defaultShader = new Shader("assets/shaders/defaultShader.glsl");
        this.defaultShader.compile();
        this.testTexture = new Texture("assets/images/player.png");

        //this.vaoID = this.defaultShader.prepare(this.vertexArray, this.elementArray, 3, 4, 0, 1);
        this.vaoID = this.defaultShader.prepare(this.vertexArray, this.elementArray, 3, 4, 2, 0, 1, 2);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void update(float dt) {
        // Upload texture to shader
        defaultShader.uploadTexture("TEXTURE_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();


        float temp = dt * 100.0f;

        if (touchedBottomSide) {
            if ((this.camera.position.x - temp) <= -540) {
                this.camera.position.x = -540;
                touchedBottomSide = false;
                touchedRightSide = true;
                System.out.println("Touched right side at " + this.camera.position.x + " and temp is " + temp);
            } else {
                this.camera.position.x -= temp;
            }
        } else if (touchedRightSide) {
            if ((this.camera.position.y - temp) <= -236) {
                this.camera.position.y = -236;
                touchedRightSide = false;
                touchedUpSide = true;
                System.out.println("Touched up side at " + this.camera.position.y + " and temp is " + temp);
            }
            this.camera.position.y -= temp;
        } else if (touchedUpSide) {
            if ((this.camera.position.x + temp) >= 0) {
                this.camera.position.x = 0;
                touchedUpSide = false;
                touchedLeftSide = true;
                System.out.println("Touched left side at " + this.camera.position.x + " and temp is " + temp);
            }
            this.camera.position.x += temp;
        } else if (touchedLeftSide) {
            if ((this.camera.position.y + temp) >= 0) {
                this.camera.position.y = 0;
                touchedLeftSide = false;
                touchedBottomSide = true;
                System.out.println("Touched bottom side at " + this.camera.position.y + " and temp is " + temp);
            }
            this.camera.position.y += temp;
        }

        this.defaultShader.use();

        //upload variables to the shaders ... must be after using the shader so it's uploaded in the current used shader
        this.defaultShader.uploadMat4f("uProjectionMatrix", camera.getProjectionMatrix());
        this.defaultShader.uploadMat4f("uViewMatrix", camera.getViewMatrix());
        this.defaultShader.uploadFloat("uTime", Time.getTime());
        this.defaultShader.draw(this.vaoID, this.elementArray.length, 0, 1, 2);

        this.testTexture.unbind();
        this.defaultShader.detach();
    }
}