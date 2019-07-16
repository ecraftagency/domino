package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons.Tweens;
import com.mygdx.jkdomino.commons._Stage;
import com.mygdx.jkdomino.interfaces.IBoardEventListener;

public class Board extends _Stage {
    class Tuple<X, Y> {
        X value; Y tile;
        Tuple(X v, Y t){ value = v; tile = t;}
    }

    private BoardConfig cfg;
    private IBoardEventListener listener;
    private Array<Actor> dominos;
    private Tuple<Integer, Tile> head;
    private Tuple<Integer, Tile> tail;
    private float scaleFactor;
    private Vector2 turn;

    public Board(IBoardEventListener listener) {
        cfg = new BoardConfig();
        this.listener = listener;
        this.dominos = getActors();
        head = new Tuple<>(0, null);
        tail = new Tuple<>(0, null);
        turn = new Vector2(-1, 0);
    }

    public int addCard(int row, int col, int value) {
        Tile tile = new Tile(row, col);
        if (head.tile == null && tail.tile == null) { //very first one
            tile.rotateBy(180);
            tile.setPosition(cfg.INITIAL_X, cfg.INITIAL_Y);
            head.value = row;
            head.tile = tile;
            tail.value = col;
            tail.tile = tile;
            addActor(tile);
        }
        else {
            int degree = 0;
            Vector2 position;
            if (value == head.value) {
                degree = head.tile.evalConnectRotation(value, tile);
                position = head.tile.evalConnectPosition(value);
                head.value = tile.getAnotherSideValue(value);
                head.tile = tile;
                addActor(tile);
            }
            else if (value == tail.value) {
                degree = tail.tile.evalConnectRotation(value, tile);
                position = tail.tile.evalConnectPosition(value);
                tail.value = tile.getAnotherSideValue(value);
                tail.tile = tile;
                addActor(tile);
                //dominos.insert(0, tile);
            }
            else
                return -1;

            tile.rotateBy(degree);
            tile.setPosition(position.x, position.y);
            if (dominos.size != 0 && dominos.size %5 == 0) {
                head.tile.turn(head.value, turn);
                turn.x = (turn.x != 0) ? 0 : 1;
                turn.y = (turn.y !=0) ? 0 : 1;
            }
        }

        tile.setVisible(false);
        updateViewPort(tile, row, col);
        return 1;
    }

    private void ani(int row, int col, Vector2 pos, float rotation, float scale) {
        Tile mimic = new Tile(row, col);
        mimic.setPosition(0 - 0.1f*Domino.SW*scale,0 - 0.1f*Domino.SH*scale);
        this.addActor(mimic);
        mimic.setScale(scale);
        Tweens.palActions(mimic, () -> {
            dominos.removeValue(mimic, true);
                },
                Actions.moveTo(pos.x, pos.y, 0.4f),
                Actions.rotateBy(rotation, 0.4f),
                Actions.scaleTo(1, 1, 0.4f));
    }

    private void updateViewPort(Tile card, int row, int col) {
        Vector2[] mids = getMidInfo();

        float scaleX = mids[0].x /(Domino.SW - cfg.HPADDING*2);
        float scaleY = mids[0].y/ (Domino.SH - cfg.VPADDING*2);
        float scale = (scaleX > scaleY) ? scaleX : scaleY;
        scale = (scale >= 1) ? scale : 1;
        this.scaleFactor = scale;

        ani(row, col, new Vector2(card.getX(), card.getY()), card.getRotation(), scale);

        zoom(scale, 0.4f, Interpolation.fastSlow, listener::zoomComplete);

        move(mids[1], 0.4f, Interpolation.fastSlow, () -> {
            listener.moveComplete();
            card.setVisible(true);
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