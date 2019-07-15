package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons._Stage;
import com.mygdx.jkdomino.interfaces.AddingPosition;
import com.mygdx.jkdomino.interfaces.CardLayout;
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

    private Vector2 getNextPosition(AddingPosition addPos, CardLayout layout) {
        int count = getActors().size;
        float pad, shift;

        if (count == 0)
            return new Vector2(cfg.INITIAL_X, cfg.INITIAL_Y);
        else {
            TileCard last = (addPos == AddingPosition.END) ? (TileCard) dominos.get(count - 1) : (TileCard) dominos.get(0);

            pad = (last.layout == CardLayout.VERTICAL) ? cfg.TW : - 4;
            pad = (layout == CardLayout.VERTICAL) ? pad : pad + 4;
            shift = (layout == CardLayout.VERTICAL) ? 0 : cfg.TH;

            if (addPos == AddingPosition.START){
                pad = -1*pad;
                shift = -1*shift;
            }

            return new Vector2(
                    last.getX() + pad + shift ,
                    cfg.INITIAL_Y
            );
        }
    }

    public void addTile(AddingPosition addPos) {
        int coin = (new Random()).nextInt(2);
        CardLayout layout = (coin%2 == 0) ? CardLayout.HORIZON : CardLayout.VERTICAL;
        Vector2 nextPos = getNextPosition(addPos, layout);
        TileCard card = new TileCard(3, 4, nextPos.x, nextPos.y, layout);

        if (addPos == AddingPosition.END)
            dominos.add(card);
        else
            dominos.insert(0, card);

        float[] mid = getBoardWidth();

        float scale = mid[0] /(Domino.SW - cfg.HPADDING*2);
        scale = (scale >= 1) ? scale : 1;

        zoom(scale, 0.4f, Interpolation.fastSlow, () -> {
            listener.zoomComplete();
        });

        move(new Vector2(mid[1], getCamera().position.y), 0.4f, Interpolation.fastSlow, () -> {
            listener.moveComplete();
        });
    }

    public float getBoardHeight() {
        return 0;
    }

    public float[] getBoardWidth() {
        float minX, maxX;
        minX = Domino.SW;
        maxX = 0;

        for (Actor a : dominos) {
            TileCard card = (TileCard)a;
            float minx = card.getCardX();
            float maxx = card.getCardX() + card.getCardWidth();
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