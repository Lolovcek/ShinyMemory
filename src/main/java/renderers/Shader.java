package renderers;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIAABB;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Shader {

    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;


    /**
     *
      * @param filepath - specifies the location of the shader file which is then separated
     */
    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                this.vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                this.fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                this.vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                this.fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch(IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file '" + filepath + "'";
        }

    }

    /**
     * ============================================================
     * Compile and link shaders
     * ============================================================
     */
    public void compile() {

        int vertexID, fragmentID;
        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, this.vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + this.filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // First load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, this.fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + this.filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors
        this.shaderProgramID = glCreateProgram();
        glAttachShader(this.shaderProgramID, vertexID);
        glAttachShader(this.shaderProgramID, fragmentID);
        glLinkProgram(this.shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(this.shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(this.shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + this.filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(this.shaderProgramID, len));
            assert false : "";
        }

    }

    /**
     *
     * @param vertexArray               - position and color information of all vertices... (XYZ, RGBA) - per vertex
     * @param elementArray              - vertices that make up a shape
     * @param positionsSize             - amount of position information for 1 vertex
     * @param colorSize                 - amount of color information for 1 vertex
     * @param firstAttributePointer     - from defaultShader.glsl...    the location of position
     * @param secondAttributePointer    - from defaultShader.glsl...   the location of color
     * @return                          - VAO address in memory
     */
    public int prepare(float[] vertexArray, int[] elementArray, int positionsSize, int colorSize, int firstAttributePointer, int secondAttributePointer) {
        // Generate VAO (vertices attribute object), VBO (vertices buffer object), and EBO (element buffer object) buffer objects, and send to GPU
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;
        glVertexAttribPointer(firstAttributePointer, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(firstAttributePointer);

        glVertexAttribPointer(secondAttributePointer, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(secondAttributePointer);

        return vaoID;
    }

    /**
     *
     * @param vertexArray               - position and color information of all vertices... (XYZ, RGBA) - per vertex
     * @param elementArray              - vertices that make up a shape
     * @param positionsSize             - amount of position information for 1 vertex
     * @param colorSize                 - amount of color information for 1 vertex
     * @param firstAttributePointer     - from defaultShader.glsl...    the location of position
     * @param secondAttributePointer    - from defaultShader.glsl...   the location of color
     * @param thirdAttributePointer     - from defaultShader.glsl...    future possible attribute pointer
     * @return - VAO address in memory
     */
    public int prepare(float[] vertexArray, int[] elementArray, int positionsSize, int colorSize, int tempSize, int firstAttributePointer, int secondAttributePointer, int thirdAttributePointer) {
        // Generate VAO (vertices attribute object), VBO (vertices buffer object), and EBO (element buffer object) buffer objects, and send to GPU
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;

        glVertexAttribPointer(firstAttributePointer, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(firstAttributePointer);

        glVertexAttribPointer(secondAttributePointer, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(secondAttributePointer);

        glVertexAttribPointer(secondAttributePointer, tempSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(thirdAttributePointer);

        return vaoID;
    }

    /**
     *
     * @param vaoID                     - VAO address in memory
     * @param elementArrayLength        - amount of vertices
     * @param firstAttributePointer     - from defaultShader.glsl...    the location of position
     * @param secondAttributePointer    - from defaultShader.glsl...   the location of color
     */
    public void draw(int vaoID, int elementArrayLength, int firstAttributePointer, int secondAttributePointer) {
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(firstAttributePointer);
        glEnableVertexAttribArray(secondAttributePointer);

        // Draw
        glDrawElements(GL_TRIANGLES, elementArrayLength, GL_UNSIGNED_INT, 0);

        // Disable the vertex attribute pointers
        glDisableVertexAttribArray(firstAttributePointer);
        glDisableVertexAttribArray(secondAttributePointer);

        glBindVertexArray(0);
    }

    /**
     *
     * @param vaoID                     - VAO address in memory
     * @param elementArrayLength        - amount of vertices
     * @param firstAttributePointer     - from defaultShader.glsl...    the location of position
     * @param secondAttributePointer    - from defaultShader.glsl...    the location of color
     * @param thirdAttributePointer     - from defaultShader.glsl...    future possible attribute pointer
     */
    public void draw(int vaoID, int elementArrayLength, int firstAttributePointer, int secondAttributePointer, int thirdAttributePointer) {
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(firstAttributePointer);
        glEnableVertexAttribArray(secondAttributePointer);
        glEnableVertexAttribArray(thirdAttributePointer);

        // Draw
        glDrawElements(GL_TRIANGLES, elementArrayLength, GL_UNSIGNED_INT, 0);

        // Disable the vertex attribute pointers
        glDisableVertexAttribArray(firstAttributePointer);
        glDisableVertexAttribArray(secondAttributePointer);
        glDisableVertexAttribArray(thirdAttributePointer);


        glBindVertexArray(0);
    }

    public void use() {
        // Bind shader program
        glUseProgram(this.shaderProgramID);

    }

    public void detach() {
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

}
