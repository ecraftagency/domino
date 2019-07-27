package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons._Stage;
import com.mygdx.jkdomino.interfaces.IBoardEventListener;

import javax.rmi.CORBA.Tie;

public class Board extends _Stage {
    class Tuple <X,Y> {
        X tile; Y vector;
        Tuple(X tile, Y vector) {this.tile = tile; this.vector = vector;}
    }

    private BoardConfig cfg;
    private IBoardEventListener listener;
    private Array<Actor> dominos;
    private Tuple<Tile, Vector3> head = null;
    private Tuple<Tile, Vector3> tail = null;
    private Tile first;

    public Board(IBoardEventListener listener) {
        Tile.initAssets();
        this.listener = listener;
        cfg = new BoardConfig();
        dominos = getActors();
    }

    private void initPosition(Tile card, Vector2 placeDirection) {
        float placeX = 0;float placeY = 0;
        OrthographicCamera camera = (OrthographicCamera) getCamera();
        float scale = camera.zoom;
        Vector3 pos = camera.position;

        if (placeDirection.x == 1)
            placeX = (Domino.SW/2)*scale + pos.x;
        else if (placeDirection.x == -1)
            placeX = pos.x - (Domino.SW/2)*scale;

        else if (placeDirection.y == 1)
            placeY = (Domino.SH/2)*scale + pos.y;
        else if (placeDirection.y == -1)
            placeY = pos.y - (Domino.SH/2)*scale;

        card.setPosition(placeX, placeY);
    }

    private void addFirst(int row, int col, Vector2 placeDirection) {
        Tile tile = new Tile(row, col, cfg.INITIAL_X, cfg.INITIAL_Y, cfg.INITIAL_ROTATION);
        initPosition(tile, placeDirection);
        addActor(tile);
        head = new Tuple<>(tile, tile.vectors[0]);
        tail = new Tuple<>(tile, tile.vectors[1]);
        tile.commit();
        first = tile;
    }

    private void addNext(Tuple<Tile, Vector3> chain, int row, int col, int value,int direction, Vector2 placeDirection) {
        Tile tile = new Tile(row, col, 0, 0, 0);
        initPosition(tile, placeDirection);
        if (direction == 1)
            dominos.add(tile);
        else if (direction == -1)
            dominos.insert(0, tile);

        Vector3 next = chain.tile.connect(chain.vector, tile, direction);
        chain.tile = tile;
        chain.vector = next;
        if (dominos.size == 6) {
            head.tile.addTurn(head.vector, -1);
            tail.tile.addTurn(tail.vector, 1);
        }
        if ( dominos.size == 18) {
            head.tile.addTurn(head.vector, -1);
            tail.tile.addTurn(tail.vector, 1);
        }
        tile.commit();
    }

    public void addCard(int row, int col, int value, Vector2 placeDirection) throws NullPointerException {
        if (head == null || tail == null)
            addFirst(row, col, placeDirection);
        else {
            if (value == head.vector.z)
                addNext(head, row, col, value, 1, placeDirection);
            else if (value == tail.vector.z)
                addNext(tail, row, col, value, -1, placeDirection);
        }

        updateViewPort(() -> {
            listener.moveComplete();
        });
    }

    private void updateViewPort(Runnable onComplete) {
        Vector2[] mids = getMidInfo();

        float scaleX = mids[0].x /(Domino.SW - cfg.HPADDING*2);
        float scaleY = mids[0].y/ (Domino.SH - cfg.VPADDING*2);
        float scale = (scaleX > scaleY) ? scaleX : scaleY;
        scale = (scale >= 1) ? scale : 1;

        zoom(scale, 0.4f, Interpolation.fastSlow, listener::zoomComplete);

        move(mids[1], 0.41f, Interpolation.fastSlow, onComplete);
    }

    private Vector2[] getMidInfo() {
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

    public float isConnectable(float row, float col) {
        Vector2 tile = new Vector2(row, col);
        if (head == null && tail == null)
            return tile.x;
        Vector2 current = new Vector2(head.vector.z, tail.vector.z);
        if (tile.x == current.x || tile.x == current.y)
            return tile.x;
        if (tile.y == current.x || tile.y == current.y)
            return tile.y;
        return -1;
    }
}