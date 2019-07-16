package com.mygdx.jkdomino.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.interfaces.Position;
import com.mygdx.jkdomino.interfaces.IBoardEventListener;
import com.mygdx.jkdomino.objects.Board;

public class GameScene extends BaseScene implements IBoardEventListener {
    private Board board;

    public GameScene(Game game) {
        super(game);
        board = new Board(this);
        board.setViewport(new ExtendViewport(Domino.SW, Domino.SH, new OrthographicCamera()));
    }

    @Override
    public void show() {
        super.show();
        uiStage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Gdx.input.setInputProcessor(null);
            if (board.addCard(2,4, 4) == -1)
                board.addCard(2,4,2);
            return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        board.act(delta);
        board.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        board.getViewport().update(width, height,true);
    }

    @Override
    public void dispose() {
        super.dispose();
        board.dispose();
    }

    @Override
    public void moveComplete() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void zoomComplete() {

    }
}
