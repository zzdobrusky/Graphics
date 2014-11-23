package edu.utah.cs4962.graphics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
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
                "uniform vec2 translate; \n" +
                "attribute vec2 textureCoordinate; \n" +
                "varying vec2 textureCoordinateVarying; \n" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_Position = vec4(position.x + translate.x, position.y + translate.y, position.z, position.w); \n" +
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

        // triangle vertices coordinates
        float[] trianglePoints =
                {
                        -0.5f, -0.5f, 0.0f, 1.0f,
                        0.5f, -0.5f, 0.0f, 1.0f,
                        0.0f,  0.5f, 0.0f, 1.0f
                };

        ByteBuffer trianglePointsByteBuffer = ByteBuffer.allocateDirect(trianglePoints.length * 4);
        trianglePointsByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer trianglePointsBuffer = trianglePointsByteBuffer.asFloatBuffer();
        trianglePointsBuffer.put(trianglePoints);
        trianglePointsBuffer.rewind();

        // triangle texture coordinates
        float[] triangleTextureCoordinates =
                {
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        0.5f, 0.0f
                };

        ByteBuffer triangleTextureByteBuffer = ByteBuffer.allocateDirect(triangleTextureCoordinates.length * 4);
        triangleTextureByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer triangleTextureBuffer = triangleTextureByteBuffer.asFloatBuffer();
        triangleTextureBuffer.put(triangleTextureCoordinates);
        triangleTextureBuffer.rewind();

        GLES20.glEnableVertexAttribArray(POSITION_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, trianglePointsBuffer);
        GLES20.glEnableVertexAttribArray(TEXTURE_COORDINATE_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(TEXTURE_COORDINATE_ATTRIBUTE_ID, 2, GLES20.GL_FLOAT, false, 2 * 4, triangleTextureBuffer);


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
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10)
    {
        _translateX += 0.0f;//01f;
        _translateY += 0.0f;//005f;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int translateLocation = GLES20.glGetUniformLocation(_program, "translate");
        GLES20.glUniform2f(translateLocation, _translateX, _translateY);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
