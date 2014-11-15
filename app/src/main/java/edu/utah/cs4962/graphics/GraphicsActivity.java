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
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        String vertexShaderSource = "" +
                "attribute vec4 position; \n" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_Position = position; \n" +
                "} \n" +
                " \n";

        String fragmentShaderSource = "" +
                " \n" +
                "void main() \n" +
                "{ \n" +
                "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0); \n" +
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
        GLES20.glBindAttribLocation(_program, POSITION_ATTRIBUTE_ID, "position");
        GLES20.glLinkProgram(_program);
        String programLinkLog = GLES20.glGetProgramInfoLog(_program);
        Log.i("Program Link", programLinkLog);
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 0.8f);
        GLES20.glEnableVertexAttribArray(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] trianglePoints =
                {
                      -0.5f, -0.5f, 0.5f, 1.0f,
                       0.5f, -0.5f, 0.5f, 1.0f,
                       0.0f,  0.5f, 0.5f, 1.0f,
                };
        ByteBuffer trianglePointsByteBuffer = ByteBuffer.allocateDirect(trianglePoints.length * 4);
        trianglePointsByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer trianglePointsBuffer = trianglePointsByteBuffer.asFloatBuffer();
        trianglePointsBuffer.put(trianglePoints);
        trianglePointsBuffer.rewind();

        GLES20.glVertexAttribPointer(POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, trianglePointsBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
