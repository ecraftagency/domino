package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.interfaces.CardLayout;
import com.mygdx.jkdomino.interfaces.Direction;

public class TileCard extends Image {
    private static TextureRegion[][] textures;
    private static float CW = 66;
    private static float CH = 128;

    private int lValue;
    private int rValue;

    public CardLayout layout;

    public TileCard(int row, int col, float x, float y, CardLayout layout) {
        super(textures[row][col]);
        setPosition(x, y, Align.left);
        lValue = row; rValue = col;
        this.layout = layout;
        if (layout == CardLayout.HORIZON) {
            setRotation(-90);
        }
    }

    public float getCardWidth() {
        return (layout == CardLayout.VERTICAL) ? TileCard.CW : TileCard.CH;
    }

    public float getCardHeight() {
        return (layout == CardLayout.VERTICAL) ? TileCard.CH : TileCard.CW;
    }

    public float getCardX() {
        return (layout == CardLayout.VERTICAL) ? getX() : getX() - TileCard.CH;
    }

    public float getCardY() {
        return 0;
    }

    public Vector2 getNextPosition(Direction d) {

        return new Vector2(0,0);
    }

    public static void initAssets() {
        textures = new TextureRegion(Domino.assetManager.get("domino.png", Texture.class)).split(69,132);
        for (TextureRegion[] texture : textures)
            for (int j = 0; j < textures[0].length; j++)
                texture[j].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
}