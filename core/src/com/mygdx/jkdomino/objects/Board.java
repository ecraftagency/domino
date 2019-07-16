package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons.Tweens;
import com.mygdx.jkdomino.commons._Stage;
import com.mygdx.jkdomino.interfaces.Position;
import com.mygdx.jkdomino.interfaces.Orientation;
import com.mygdx.jkdomino.interfaces.IBoardEventListener;

import java.util.Random;

public class Board extends _Stage {
    private BoardConfig cfg;
    private IBoardEventListener listener;
    private Array<Actor> dominos;

    public Board(IBoardEventListener listener) {
        cfg = new BoardConfig();
        this.listener = listener;
        this.dominos = getActors();
    }

    private Vector2 getNextHorizonPos(Position nextPos, Orientation layout) {
        int count = dominos.size;
        if (count == 0)
            return new Vector2(cfg.INITIAL_X, cfg.INITIAL_Y);
        TileCard seed = (nextPos == Position.NEXT) ? (TileCard) dominos.get(count - 1) : (TileCard) dominos.get(0);
        float nextX = seed._getNextHorizonPos(nextPos, layout);
        return new Vector2(nextX, seed._getY());
    }

    private Vector2 getNextVerticalPos(Position nextPos, Orientation layout) {
        int count = dominos.size;
        if (count == 0)
            return new Vector2(cfg.INITIAL_X, cfg.INITIAL_Y);
        TileCard seed = (nextPos == Position.NEXT) ? (TileCard) dominos.get(count - 1) : (TileCard) dominos.get(0);
        float nextY = seed._getNextVerticalPos(nextPos, layout);
        return new Vector2(seed._getX(), nextY);
    }

    public void addTile(Position addPos) {
        int coin = (new Random()).nextInt(31);
        Orientation layout = (coin%2 == 0) ? Orientation.FV : Orientation.FH;
        addPos = (coin%3 == 0) ? Position.NEXT : Position.PREVIOUS;

        Vector2 nextPos;
        if (coin%4 == 0)
            nextPos = getNextVerticalPos(addPos, layout);
        else
            nextPos = getNextHorizonPos(addPos, layout);

        TileCard card = new TileCard(2,4, nextPos.x,nextPos.y, layout);
        card.setVisible(false);

        if (addPos == Position.NEXT)
            dominos.add(card);
        else
            dominos.insert(0, card);

        updateViewPort(card);
    }

    public void addCard() {
        Tile card = new Tile(2,4);
        Tile card1 = new Tile(2,5);
        Tile card3 = new Tile(1, 5);

        //card.rotateBy(180);
        card.setPosition(300,500);
        addActor(card);

        card1.setPosition(0,0);
        int degree = card.evalConnectRotation(2, card1);
        Vector2 position = card.evalConnectPosition(2);
        card1.rotateBy(degree);
        card1.setPosition(position.x, position.y);
        card1.turn(5, new Vector2(1, 0));

        card3.setPosition(0,0);
        degree = card1.evalConnectRotation(5, card3);
        position = card1.evalConnectPosition(5);
        card3.rotateBy(degree);
        card3.setPosition(position.x, position.y);

        Tile card2 = new Tile(2, 5);
        addActor(card2);
        card2.addAction(Actions.rotateBy(card1.getRotation(), 0.4f));
        Tweens.action(card2, Actions.moveTo(card1.getX(), card1.getY(), 0.4f), () -> {
            getActors().removeValue(card2, true);
            addActor(card1);
            addActor(card3);
            updateViewPort(null);
        });
    }

    private void updateViewPort(TileCard card) {
        Vector2[] mids = getMidInfo();

        float scaleX = mids[0].x /(Domino.SW - cfg.HPADDING*2);
        float scaleY = mids[0].y/ (Domino.SH - cfg.VPADDING*2);
        float scale = (scaleX > scaleY) ? scaleX : scaleY;
        scale = (scale >= 1) ? scale : 1;

        zoom(scale, 0.4f, Interpolation.fastSlow, listener::zoomComplete);

        move(mids[1], 0.4f, Interpolation.fastSlow, () -> {
            listener.moveComplete();
            //card.setVisible(true);
        });
    }

    public Vector2[] getMidInfo() {
        float minX, maxX, minY, maxY;
        minY = Domino.SH; minX = Domino.SW;
        maxX = 0; maxY = 0;

        for (Actor a : dominos) {
            Tile card = (Tile)a;
            float minx = card._getX();
            float maxx = card._getX() + card._getWidth();
            float miny = card._getY();
            float maxy = card._getY() + card._getHeight();
            minX = (minX < minx) ? minX : minx;
            maxX = (maxX > maxx) ? maxX : maxx;
            minY = (minY < miny) ? minY : miny;
            maxY = (maxY > maxy) ? maxY : maxy;
        }

        float boardWidth = maxX - minX;
        float boardWMid = (minX + maxX)/2;
        float boardHeight = maxY - minY;
        float boardHMid = (minY + maxY)/2;
        return new Vector2[] {
            new Vector2(boardWidth, boardHeight),
            new Vector2(boardWMid, boardHMid)
        };
    }

    @Override
    public void dispose() {
        int count = getActors().size;
        getActors().removeRange(0, count - 1);
        super.dispose();
    }
}