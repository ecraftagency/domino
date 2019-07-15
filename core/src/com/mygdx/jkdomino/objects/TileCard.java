package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.interfaces.Orientation;
import com.mygdx.jkdomino.interfaces.Direction;
import com.mygdx.jkdomino.interfaces.Position;

public class TileCard extends Image {
    private static TextureRegion[][] textures;
    private static float CW = 69;
    private static float CH = 132;

    public Orientation layout;
    public float _width = 0;
    public float _height = 0;
    public int align;
    private float shiftX = 0;
    private float shiftY = 0;

    public TileCard(int row, int col, float x, float y, Orientation layout) {
        super(textures[row][col]);

        setOrigin(Align.bottomLeft);

        if (layout == Orientation.HORIZON) {
            setRotation(90);
            shiftX = CH;
        }

        _width = (layout == Orientation.VERTICAL) ? CW : CH;
        _height = (layout == Orientation.VERTICAL) ? CH : CW;
        this.layout = layout;
        setPosition(x + shiftX, y + 0);
    }

    public float _getX() {
        return super.getX() + -1*shiftX;
    }

    public float _getWidth() {
        return _width;
    }

    public float _getNextHorizonPos(Position nextPosition, Orientation nextCardLayout) {
        if (nextPosition == Position.NEXT)
            return _getX() + _getWidth();
        else
            return _getX() - ((nextCardLayout == Orientation.HORIZON) ? CH : CW);
    }

    public static void initAssets() {
        textures = new TextureRegion(Domino.assetManager.get("domino.png", Texture.class)).split(69,132);
        for (TextureRegion[] texture : textures)
            for (int j = 0; j < textures[0].length; j++)
                texture[j].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
}