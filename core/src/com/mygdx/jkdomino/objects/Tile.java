package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.jkdomino.Domino;

public class Tile extends Image {
    private static TextureRegion[][] textures;
    private static float CW = 69;
    private static float CH = 132;

    private Vector3[] values; //direction vectors
    private Vector3 turn;

    private float _width = CW;
    private float _height = CH;
    private float shiftX = 0;
    private float shiftY = 0;
    private float originX = 0;
    private float originY = 0;

    Tile(int row, int col) {
        super(textures[row][col]);
        setOrigin(Align.bottomLeft);
        values = new Vector3[2];
        values[0] = new Vector3(0, 1, row);
        values[1] = new Vector3(0, -1, col);
        turn = null;
    }

    public int getAnotherSideValue(int value) {
        for (int i = 0; i < 2; i++)
            if (values[i].z != value)
                return Math.round(values[i].z);
        return -1;
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        float total = getRotation() + amountInDegrees;
        if (total % 90 == 0) {
            if (Math.sin(total*Math.PI/180) == 1){
                shiftX = CH;
                shiftY = 0;
                values[0].x = -1;
                values[1].x = 1;
                values[0].y = values[1].y = 0;
            }
            else {
                shiftY = CW;
                shiftX = 0;
                values[0].x = 1;
                values[1].x = -1;
                values[0].y = values[1].y = 0;
            }
            _width = CH; _height = CW;
        }

        if (total % 180 == 0) {
            if (Math.cos(total*Math.PI/180) == 1) {
                shiftX = 0;
                shiftY = 0;
                values[0].y = 1;
                values[1].y = -1;
                values[0].x = values[1].x = 0;
            }
            else {
                shiftX = CW;
                shiftY = CH;
                values[0].y = -1;
                values[1].y = 1;
                values[0].x = values[1].x = 0;
            }
            _width = CW; _height = CH;
        }
        super.rotateBy(amountInDegrees);
    }

    public float _getWidth() {
        return _width;
    }

    public float _getHeight() {
        return _height;
    }

    public float _getX() {
        return originX;
    }

    public float _getY() {
        return originY;
    }

    private Vector2 getDirectionVector(int value) {
        Vector2 r = null;
        for (int i = 0; i < 2; i++)
            if (values[i].z == value)
                r = new Vector2(values[i].x, values[i].y);
        return r;
    }

    public int evalConnectRotation(int value, Tile tile) { // chi duoc tra ve 4 gia tri: 0, 180, 90, -90
        Vector2 connected;

        if (turn != null && turn.z == value) {
            connected = new Vector2(turn.x, turn.y);
        }
        else {
             connected = this.getDirectionVector(value);
        }

        Vector2 connector = tile.getDirectionVector(value);

        if ( connected.x != 0 && Math.abs(connected.x) == Math.abs(connector.x)) { //cung phuong ngang
            return (connected.x*connector.x < 0) ? 0 : 180;
        }
        else if (connected.y != 0 && Math.abs(connected.y) == Math.abs(connector.y)) { //cung phuong dung
            return (connected.y*connector.y < 0) ? 0 : 180;
        }
        else { // khac phuong
            float _x = (connected.x == 0) ? connected.y : connected.x;
            float _y = (connector.y == 0) ? connector.x : connector.y;

            if (connector.x != 0)
                return ((_x*_y) > 0) ? -90 : 90;
            else
                return ((_x*_y) > 0) ? 90 : -90;
        }
    }

    public Vector2 evalConnectPosition(int value) {
        Vector2 d = this.getDirectionVector(value);
        Vector2 r = null;
        if (turn != null){
            float padx = (turn.x > 0) ? _getWidth() : _getHeight();
            float pady = (turn.y > 0) ? _getHeight() : _getWidth();
            float shift = (d.x > 0 || d.y > 0) ? CH - CW : 0;
            r = new Vector2(_getX() + turn.x*padx + d.x*shift, _getY() + turn.y*pady + d.y*shift);
        }
        else
            r = new Vector2(_getX() + d.x*_getWidth(), _getY() + d.y*_getHeight());
        return r;
    }

    public void turn(int value, Vector2 direction) {
        turn = new Vector3(direction.x, direction.y, value);
    }

    @Override
    public void setPosition(float x, float y) {
        originX = x;
        originY = y;
        super.setPosition(x + shiftX, y + shiftY);
    }

    @Override
    public String toString() {
        return "(" + values[0].x + "," + values[0].y + "," + values[0].z + ") " + "(" + values[1].x + "," + values[1].y + "," + values[1].z + ")";
    }

    public static void initAssets() {
        textures = new TextureRegion(Domino.assetManager.get("domino.png", Texture.class)).split(69,132);
        for (TextureRegion[] texture : textures)
            for (int j = 0; j < textures[0].length; j++)
                texture[j].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
}