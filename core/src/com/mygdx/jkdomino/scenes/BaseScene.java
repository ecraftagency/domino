package com.mygdx.jkdomino.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons._Stage;

public class BaseScene implements Screen {
    private Game game;
    protected _Stage uiStage;

    public BaseScene(Game game){
        Domino.SH = Domino.SW* Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
        this.game=  game;
        uiStage = new _Stage();
        uiStage.setViewport(new ExtendViewport(Domino.SW, Domino.SH, new OrthographicCamera()));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void hide() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1/255f, 78/255f, 121/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }
}