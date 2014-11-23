package edu.utah.cs4962.graphics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GraphicsActivity extends Activity implements GLSurfaceView.Renderer
{
    int _program = -1;
    static final int POSITION_ATTRIBUTE_ID = 0;
    static final int TEXTURE_COORDINATE_ATTRIBUTE_ID = 1;

    float _translateX;
    float _translateY;
    float _rotation;
    float _scaleX;
    float _scaleY;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GLSurfaceView surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfaceView.setRenderer(this);
        setContentView(surfaceView);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig)
    {
        String vertexShaderSource = "" +
                "attribute vec4 position; \n" +
                "uniform mat4 modelView; \n" +
                "attribute vec2 textureCoordinate; \n" +
                "varying vec2 textureCoordinateVarying; \n" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_Position = modelView * position; \n" +
                "  textureCoordinateVarying = textureCoordinate;\n" +
                "} \n" +
                " \n";

        String fragmentShaderSource = "" +
                "uniform sampler2D textureUnit; \n" +
                "varying highp vec2 textureCoordinateVarying; \n" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_FragColor = texture2D(textureUnit, textureCoordinateVarying); \n" +
                "} \n" +
                " \n";

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderSource);
        GLES20.glCompileShader(vertexShader);
        String vertexShaderCompileLog = GLES20.glGetShaderInfoLog(vertexShader);
        Log.i("Vertex Shader Compile", vertexShaderCompileLog);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);
        String fragmentShaderCompileLog = GLES20.glGetShaderInfoLog(fragmentShader);
        Log.i("Fragment ShaderCompile", fragmentShaderCompileLog);

        _program = GLES20.glCreateProgram();
        GLES20.glAttachShader(_program, vertexShader);
        GLES20.glAttachShader(_program, fragmentShader);
        // Bind variables
        GLES20.glBindAttribLocation(_program, POSITION_ATTRIBUTE_ID, "position");
        GLES20.glBindAttribLocation(_program, TEXTURE_COORDINATE_ATTRIBUTE_ID, "textureCoordinate");
        // link it and use it
        GLES20.glLinkProgram(_program);
        GLES20.glUseProgram(_program);
        // trace linker output
        String programLinkLog = GLES20.glGetProgramInfoLog(_program);
        Log.i("Program Link", programLinkLog);
        // make background gray
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 0.8f);

        // quad vertices coordinates
        float[] quadPoints =
                {
                        -0.5f, -0.5f, 0.0f, 1.0f,
                        0.5f, -0.5f, 0.0f, 1.0f,
                        -0.5f, 0.5f, 0.0f, 1.0f,
                        0.5f, 0.5f, 0.0f, 1.0f,
                };

        ByteBuffer quadPointsByteBuffer = ByteBuffer.allocateDirect(quadPoints.length * 4);
        quadPointsByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer quadPointsBuffer = quadPointsByteBuffer.asFloatBuffer();
        quadPointsBuffer.put(quadPoints);
        quadPointsBuffer.rewind();

        // quad texture coordinates
        float[] quadTextureCoordinates =
                {
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                };

        ByteBuffer quadTextureByteBuffer = ByteBuffer.allocateDirect(quadTextureCoordinates.length * 4);
        quadTextureByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer quadTextureBuffer = quadTextureByteBuffer.asFloatBuffer();
        quadTextureBuffer.put(quadTextureCoordinates);
        quadTextureBuffer.rewind();

        GLES20.glEnableVertexAttribArray(POSITION_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, quadPointsBuffer);
        GLES20.glEnableVertexAttribArray(TEXTURE_COORDINATE_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(TEXTURE_COORDINATE_ATTRIBUTE_ID, 2, GLES20.GL_FLOAT, false, 2 * 4, quadTextureBuffer);


        // loading texture
        Bitmap astronomyTexture = BitmapFactory.decodeResource(getResources(), R.drawable.astronomy);
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        int textureId = textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, astronomyTexture, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height)
    {
        if (width < height)
            GLES20.glViewport((width - height) / 2, 0, height, height);
        else
            GLES20.glViewport((width - height) / 2, 0, height, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10)
    {
        _translateX += 0.0001f;
        _translateY += 0.0005f;
        _rotation += 0.001f;
        _scaleX += 0.005;
        _scaleY += 0.005;


        float[] modelView = new float[]
                {
                        _scaleX * FloatMath.cos(_rotation), _scaleY * FloatMath.sin(_rotation), 0.0f, 0.0f,
                        -_scaleX * FloatMath.sin(_rotation), _scaleY * FloatMath.cos(_rotation), 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f,
                        _translateX, _translateY, 0.0f, 1.0f,
                };

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int modelViewLocation = GLES20.glGetUniformLocation(_program, "modelView");
        GLES20.glUniformMatrix4fv(modelViewLocation, 1, false, modelView, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
