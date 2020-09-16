    #type vertex
    #version 330 core
    layout (location=0) in vec3 attributePos;
    layout (location=1) in vec4 attributeColor;
    layout (location=2) in vec2 attributeTextureCoordinates;
    layout (location=3) in float attributeTextureId;

    uniform mat4 uProjectionMatrix;
    uniform mat4 uViewMatrix;

    out vec4 fragmentColor;
    out vec2 fragmentTextureCoordinates;
    out float fragmentTextureId;

    void main() {
        fragmentColor = attributeColor;
        fragmentTextureCoordinates = attributeTextureCoordinates;
        fragmentTextureId = attributeTextureId;
        gl_Position = uProjectionMatrix * uViewMatrix * vec4(attributePos, 1.0);
    }
    #type fragment
    #version 330 core

    in vec4 fragmentColor;
    in vec2 fragmentTextureCoordinates;
    in float fragmentTextureId;

    uniform sampler2D uTextures[8];

    out vec4 color;

    void main() {
        if (fragmentTextureId > 0) {
            int id = int(fragmentTextureId);
            color = fragmentColor * texture(uTextures[id], fragmentTextureCoordinates);
        } else {
            color = fragmentColor;
        }
    }