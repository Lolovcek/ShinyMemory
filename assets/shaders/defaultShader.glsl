    #type vertex
    #version 330 core
    layout (location=0) in vec3 attributePos;
    layout (location=1) in vec4 attributeColor;
    layout (location=2) in vec2 attributeTextureCoordinates;

    uniform mat4 uProjectionMatrix;
    uniform mat4 uViewMatrix;

    out vec4 fragmentColor;
    out vec2 fragmentTextureCoordinates;

    void main() {
        fragmentColor = attributeColor;
        fragmentTextureCoordinates = attributeTextureCoordinates;
        gl_Position = uProjectionMatrix * uViewMatrix * vec4(attributePos, 1.0);
    }
    #type fragment
    #version 330 core

    uniform float uTime;
    uniform sampler2D TEXTURE_SAMPLER;

    in vec4 fragmentColor;
    in vec2 fragmentTextureCoordinates;

    out vec4 color;

    void main() {

        //float noise = fract(sin(dot(fragmentColor.xy, vec2(12.9898, 78.233))) * 43758.5453);
        //float avg = (fragmentColor.r + fragmentColor.g + fragmentColor.b) / 3;
        color = texture(TEXTURE_SAMPLER, fragmentTextureCoordinates); // * noise
    }