package edu.utah.cs4962.graphics;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GraphicsActivity extends Activity implements GLSurfaceView.Renderer
{
    Sprite _sprite = null;
    Sprite _sprite2 = null;

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
        // make background gray
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 0.8f);

        _sprite = new Sprite();
        _sprite.setTextureIdentifier(Sprite.loadTexture(getResources(), R.drawable.astronomy));
        _sprite.setWidth(0.5f);
        _sprite.setHeight(0.5f);

        _sprite2 = new Sprite();
        _sprite2.setTextureIdentifier(Sprite.loadTexture(getResources(), R.drawable.astronomy));
        _sprite2.setWidth(0.1f);
        _sprite2.setHeight(0.1f);
        _sprite2.setCenterX(-0.9f);
        _sprite2.setCenterY(0.8f);
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        _sprite.setRotation(_sprite.getRotation() + 0.005f);
        _sprite.draw();

        _sprite2.setCenterX(_sprite2.getCenterX() + 0.005f);
        _sprite2.draw();
    }
}
