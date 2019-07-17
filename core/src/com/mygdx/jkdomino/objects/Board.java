package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons.Tweens;
import com.mygdx.jkdomino.commons._Stage;
import com.mygdx.jkdomino.interfaces.IBoardEventListener;

public class Board extends _Stage {
    class Tuple<X, Y, Z> {
        X value; Y tile; Z vector;
        Tuple(X v, Y t, Z vec){ value = v; tile = t; vector = vec;}
    }

    private BoardConfig cfg;
    private IBoardEventListener listener;
    private Array<Actor> dominos;
    private Tuple<Integer, Tile, Vector2> head;
    private Tuple<Integer, Tile, Vector2> tail;
    private float scaleFactor;
    private Vector2 turn;

    public Board(IBoardEventListener listener) {
        cfg = new BoardConfig();
        this.listener = listener;
        this.dominos = getActors();
        head = new Tuple<>(0, null, null);
        tail = new Tuple<>(0, null, null);
        turn = new Vector2(0, 1);
    }

    public void addCard(int row, int col, int value) {
        //try {
            Tile tile = new Tile(row, col);
            if (head.tile == null && tail.tile == null) { //very first one
                tile.rotateBy(90);
                tile.setPosition(cfg.INITIAL_X, cfg.INITIAL_Y);
                Vector2[] vecs = tile.getInitialVectors();
                head.value = row;
                head.tile = tile;
                head.vector = vecs[0];
                tail.value = col;
                tail.tile = tile;
                tail.vector = vecs[1];
                addActor(tile);
            }
            else {
                int degree = 0;
                Vector2 position;
                if (value == head.value) {
                    degree = head.tile.evalConnectRotation(value, tile, head.vector);
                    position = head.tile.evalConnectPosition(value, head.vector);

                    head.value = tile.getAnotherSideValue(value);
                    head.tile = tile;
                    addActor(tile);
                    tile.rotateBy(degree);
                    tile.setPosition(position.x, position.y);
                    Vector2 dv = tile.getDirectionVector(value);
                    head.vector = tile.getAnotherSideVector(dv);
                }
                else if (value == tail.value) {
                    degree = tail.tile.evalConnectRotation(value, tile, tail.vector);
                    position = tail.tile.evalConnectPosition(value, tail.vector);

                    tail.value = tile.getAnotherSideValue(value);
                    tail.tile = tile;
                    dominos.insert(0, tile);
                    tile.rotateBy(degree);
                    tile.setPosition(position.x, position.y);
                    Vector2 dv = tile.getDirectionVector(value);
                    tail.vector = tile.getAnotherSideVector(dv);
                }
                else
                    return;


                if (dominos.size != 0 && dominos.size %5 == 0) {
                    head.tile.turn(head.value, turn);
                    turn.x = (turn.x != 0) ? 0 : 1;
                    turn.y = (turn.y !=0) ? 0 : 1;
                }
            }

            tile.setVisible(false);
            updateViewPort(tile, row, col);
        //}
        /*catch (Exception e) {
            Gdx.app.log("", e.printStackTrace());
        }*/
    }

    public float isConnectable(Vector2 tile) {
        if (head.tile == null && tail.tile == null)
            return tile.x;
        Vector2 current = new Vector2(head.value, tail.value);
        if (tile.x == current.x || tile.x == current.y)
            return tile.x;
        if (tile.y == current.x || tile.y == current.y)
            return tile.y;
        return -1;
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