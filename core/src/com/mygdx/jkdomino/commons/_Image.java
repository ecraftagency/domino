package com.mygdx.jkdomino.commons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.jkdomino.Domino;

public class _Image extends Image {
    TextureRegionDrawable texture;
    Stage stage;
    public _Image(Stage stage, float x, float y, String key, int align){
        super(new TextureRegionDrawable(Domino.assetManager.get(key, Texture.class)));
        this.stage = stage;
        texture = new TextureRegionDrawable(Domino.assetManager.get(key, Texture.class));
        setDrawable(texture);

        setPosition(x,y, align);
        setOrigin(align);
        stage.addActor(this);
    }

    public void dispose() {
        stage.getActors().removeValue(this, true);
    }
}
