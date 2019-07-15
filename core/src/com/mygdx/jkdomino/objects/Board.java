package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons._Stage;
import com.mygdx.jkdomino.interfaces.Position;
import com.mygdx.jkdomino.interfaces.Orientation;
import com.mygdx.jkdomino.interfaces.IBoardEventListener;

import org.omg.PortableServer.POA;

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
        return new Vector2(nextX, cfg.INITIAL_Y);
    }

    public void addTile(Position addPos) {
        int coin = (new Random()).nextInt(31);
        Orientation layout = (coin%2 == 0) ? Orientation.VERTICAL : Orientation.HORIZON;
        addPos = (coin%3 == 0) ? Position.NEXT : Position.PREVIOUS;

        Vector2 nextPos = getNextHorizonPos(addPos, layout);
        TileCard card = new TileCard(2,4, nextPos.x,nextPos.y, layout);
        card.setVisible(false);

        if (addPos == Position.NEXT)
            dominos.add(card);
        else
            dominos.insert(0, card);

        updateViewPort(card);
    }

    private void updateViewPort(TileCard card) {
        float[] mid = getBoardWidth();

        float scale = mid[0] /(Domino.SW - cfg.HPADDING*2);
        scale = (scale >= 1) ? scale : 1;

        zoom(scale, 0.4f, Interpolation.fastSlow, () -> {
            listener.zoomComplete();
        });

        move(new Vector2(mid[1], getCamera().position.y), 0.4f, Interpolation.fastSlow, () -> {
            listener.moveComplete();
            card.setVisible(true);
        });
    }

    public float[] getBoardWidth() {
        float minX, maxX;
        minX = Domino.SW;
        maxX = 0;

        for (Actor a : dominos) {
            TileCard card = (TileCard)a;
            float minx = card._getX();
            float maxx = card._getX() + card._getWidth();
            minX = (minX < minx) ? minX : minx;
            maxX = (maxX > maxx) ? maxX : maxx;
        }

        float boardWidth = maxX - minX;
        float boardMid = (minX + maxX)/2;
        return new float[] {boardWidth, boardMid};
    }

    @Override
    public void dispose() {
        int count = getActors().size;
        getActors().removeRange(0, count - 1);
        super.dispose();
    }
}