package edu.utah.cs4962.graphics;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
    static final int COLOR_ATTRIBUTE_ID = 1;

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
                "attribute vec4 color; \n" +
                "uniform vec2 translate; \n" +
                "varying vec4 colorVarying; \n" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_Position = vec4(position.x + translate.x, position.y + translate.y, position.z, position.w); \n" +
                "  colorVarying = color;\n" +
                "} \n" +
                " \n";

        String fragmentShaderSource = "" +
                "varying highp vec4 colorVarying; \n" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_FragColor = colorVarying; \n" +
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
        GLES20.glLinkProgram(_program);
        GLES20.glUseProgram(_program);
        String programLinkLog = GLES20.glGetProgramInfoLog(_program);
        Log.i("Program Link", programLinkLog);
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 0.8f);

        float[] trianglePoints =
                {
                        -0.5f, -0.5f, 0.0f, 1.0f,
                        0.5f, -0.5f, 0.0f, 1.0f,
                        0.0f,  0.5f, 0.0f, 1.0f,
                };

        ByteBuffer trianglePointsByteBuffer = ByteBuffer.allocateDirect(trianglePoints.length * 4);
        trianglePointsByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer trianglePointsBuffer = trianglePointsByteBuffer.asFloatBuffer();
        trianglePointsBuffer.put(trianglePoints);
        trianglePointsBuffer.rewind();

        GLES20.glEnableVertexAttribArray(POSITION_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, trianglePointsBuffer);

        float[] triangleColors =
                {
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f,  0.0f, 1.0f, 1.0f,
                };

        ByteBuffer triangleColorsByteBuffer = ByteBuffer.allocateDirect(triangleColors.length * 4);
        triangleColorsByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer triangleColorsBuffer = triangleColorsByteBuffer.asFloatBuffer();
        triangleColorsBuffer.put(triangleColors);
        triangleColorsBuffer.rewind();

        GLES20.glEnableVertexAttribArray(COLOR_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(COLOR_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, triangleColorsBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10)
    {
        _translateX += 0.001f;
        _translateY += 0.0005f;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        int translateLocation = GLES20.glGetUniformLocation(_program, "translate");
        GLES20.glUniform2f(translateLocation, _translateX, _translateY);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
