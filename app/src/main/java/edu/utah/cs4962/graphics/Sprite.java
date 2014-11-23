package edu.utah.cs4962.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.FloatMath;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zbynek on 11/22/2014.
 */
public class Sprite
{
    static int _Program = -1;
    static final int POSITION_ATTRIBUTE_ID = 0;
    static final int TEXTURE_COORDINATE_ATTRIBUTE_ID = 1;
    static FloatBuffer _QuadPointsBuffer = null;
    static FloatBuffer _QuadTextureBuffer = null;

    static void init()
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

        _Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(_Program, vertexShader);
        GLES20.glAttachShader(_Program, fragmentShader);
        // Bind variables
        GLES20.glBindAttribLocation(_Program, POSITION_ATTRIBUTE_ID, "position");
        GLES20.glBindAttribLocation(_Program, TEXTURE_COORDINATE_ATTRIBUTE_ID, "textureCoordinate");
        // link it and use it
        GLES20.glLinkProgram(_Program);
        GLES20.glUseProgram(_Program);
        // trace linker output
        String programLinkLog = GLES20.glGetProgramInfoLog(_Program);
        Log.i("Program Link", programLinkLog);

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
        _QuadPointsBuffer = quadPointsByteBuffer.asFloatBuffer();
        _QuadPointsBuffer.put(quadPoints);
        _QuadPointsBuffer.rewind();

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
        _QuadTextureBuffer = quadTextureByteBuffer.asFloatBuffer();
        _QuadTextureBuffer.put(quadTextureCoordinates);
        _QuadTextureBuffer.rewind();
    }

    static int loadTexture(Resources resourcers, int resourceIdentifier)
    {
        Bitmap texture = BitmapFactory.decodeResource(resourcers, resourceIdentifier);
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        int textureId = textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);

        return textureId;
    }

    float _centerX;
    float _centerY;
    float _width;
    float _height;
    float _rotation;
    float _textureIdentifier;

    public Sprite()
    {
        _width = 1.0f;
        _height = 1.0f;
    }

    public float getCenterX()
    {
        return _centerX;
    }

    public void setCenterX(float centerX)
    {
        _centerX = centerX;
    }

    public float getCenterY()
    {
        return _centerY;
    }

    public void setCenterY(float centerY)
    {
        _centerY = centerY;
    }

    public float getWidth()
    {
        return _width;
    }

    public void setWidth(float width)
    {
        _width = width;
        //updateModelViewMatrix();
    }

    public float getHeight()
    {
        return _height;
    }

    public void setHeight(float height)
    {
        _height = height;
    }

    public float getRotation()
    {
        return _rotation;
    }

    public void setRotation(float rotation)
    {
        _rotation = rotation;
    }

    public float getTextureIdentifier()
    {
        return _textureIdentifier;
    }

    public void setTextureIdentifier(float textureIdentifier)
    {
        _textureIdentifier = textureIdentifier;
    }

    public void draw()
    {
        if(_Program < 0)
            init();

        float[] modelView = new float[]
                {
                        _width * FloatMath.cos(_rotation), _height * FloatMath.sin(_rotation), 0.0f, 0.0f,
                        -_width * FloatMath.sin(_rotation), _height * FloatMath.cos(_rotation), 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f,
                        _centerX, _centerY, 0.0f, 1.0f,
                };

        int modelViewLocation = GLES20.glGetUniformLocation(_Program, "modelView");
        GLES20.glUniformMatrix4fv(modelViewLocation, 1, false, modelView, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glEnableVertexAttribArray(POSITION_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, _QuadPointsBuffer);
        GLES20.glEnableVertexAttribArray(TEXTURE_COORDINATE_ATTRIBUTE_ID);
        GLES20.glVertexAttribPointer(TEXTURE_COORDINATE_ATTRIBUTE_ID, 2, GLES20.GL_FLOAT, false, 2 * 4, _QuadTextureBuffer);
    }
}
