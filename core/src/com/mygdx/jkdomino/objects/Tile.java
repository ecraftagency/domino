package com.mygdx.jkdomino.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.jkdomino.Domino;
import com.mygdx.jkdomino.commons.Tweens;

final class Tile extends Image {
    class Triple<X, Y, Z> {
        X tile; Y connected; Z connector;
        Triple(X tile, Y connected, Z connector){
            this.tile = tile;
            this.connected = connected;
            this.connector = connector;
        }
    }

    private static TextureRegion[][] textures;
    private static float CW = 69;
    private static float CH = 132;

    Vector3[] vectors; //direction vectors
    private Vector3 turn;

    private Triple<Tile, Vector3, Vector3> next = null;
    private Triple<Tile, Vector3, Vector3> prev = null;

    private float _width = CW;
    private float _height = CH;
    private float shiftX = 0;
    private float shiftY = 0;
    private float _x = 0;
    private float _y = 0;
    private float _rotation = 0;

    Tile(int row, int col, float x, float y, float rotation) {
        super(textures[row][col]);
        setOrigin(Align.bottomLeft);
        vectors = new Vector3[2];
        vectors[0] = new Vector3(0,1,row);
        vectors[1] = new Vector3(0,1,col);
        turn = null;
        _rotate(rotation);
        _setPosition(x, y);
    }

    private void _rotate(float amountInDegrees) {
        _rotation = getRotation() + amountInDegrees;
        double s = Math.sin(_rotation*Math.PI/180);
        double c = Math.cos(_rotation*Math.PI/180);
        if (Math.abs((int)s) == 1){
            shiftX = (s == 1) ? CH : 0;
            shiftY = (s == 1) ? 0 : CW;
            vectors[0].x = (s == 1) ? -1 : 1;
            vectors[1].x = (s == 1) ? 1 : -1;
            vectors[0].y = vectors[1].y = 0;
        }
        if (Math.abs((int)c) == 1) {
            shiftX = (c == 1) ? 0 : CW;
            shiftY = (c == 1) ? 0 : CH;
            vectors[0].y = (c == 1) ? 1 : -1;
            vectors[1].y = (c == 1) ? -1 : 1;
            vectors[0].x = vectors[1].x = 0;
        }
        _height = CH*(int)Math.abs(c) + CW*(int)Math.abs(s);
        _width = CH*(int)Math.abs(s) + CW*(int)Math.abs(c);
    }

    private void _setPosition(float x, float y) { _x = x + shiftX;_y = y + shiftY; }
    float _getX() { return _x + -1*shiftX; }
    float _getY() { return _y + -1*shiftY; }
    float _getHeight() {return _height;}
    float _getWidth() {return _width;}

    Vector3 connect(Vector3 connected, Tile connectTo, int direction) throws NullPointerException {
        Vector3 connector = connectTo.getConnectionVector(connected.z);
        int degree = evalConnectRotation(connected, connector);
        Vector2 position = evalConnectPosition(connected);
        connectTo._rotate(degree);
        connectTo._setPosition(position.x, position.y);


        if (direction == -1) { // add prev
            prev = new Triple<>(connectTo, connected, connector);
            connectTo.next = new Triple<>(this, connector, connected);
        }
        else { // add next
            next = new Triple<>(connectTo, connected, connector);
            connectTo.prev = new Triple<>(this, connector, connected);
        }
        return connectTo.getAnotherSideVector(connector);
    }

    void commit() { //final commit
        Tweens.action(this, Actions.rotateTo(_rotation, 0.4f), null);
        Tweens.action(this, Actions.moveTo(_x, _y, 0.4f), null);
    }

    void addTurn(Vector3 vector, int direction) {
        turn = new Vector3(vector.x, vector.y, vector.z);
        vector.x = (vector.x == 0) ? direction : 0;
        vector.y = (vector.y == 0) ? direction : 0;
    }

    void addTurn(boolean head, int direction) {
        Vector3 tv = (head) ? this.next.connected : this.prev.connected;
        turn = new Vector3(tv.x, tv.y, tv.z);
        tv.x = (tv.x == 0) ? direction : 0;
        tv.y = (tv.y == 0) ? direction : 0;
    }

    private Vector3 getAnotherSideVector(Vector3 v) throws NullPointerException {
        for (int i = 0; i < 2; i++)
            if (v.x != vectors[i].x || v.y != vectors[i].y || v.z != vectors[i].z)
                return vectors[i];
        throw new NullPointerException();
    }

    private Vector3 getConnectionVector(float value) throws NullPointerException {
        for (int i = 0; i < 2; i++)
            if (vectors[i].z == value)
                return vectors[i];
        throw new NullPointerException();
    }

    private int evalConnectRotation(Vector3 connected, Vector3 connector) throws NullPointerException {
        if ( connected.x != 0 && Math.abs(connected.x) == Math.abs(connector.x))
            return (connected.x*connector.x < 0) ? 0 : 180;
        else if (connected.y != 0 && Math.abs(connected.y) == Math.abs(connector.y))
            return (connected.y*connector.y < 0) ? 0 : 180;
        else {
            float __x = (connected.x == 0) ? connected.y : connected.x;
            float __y = (connector.y == 0) ? connector.x : connector.y;

            if (connector.x != 0)
                return ((__x*__y) > 0) ? -90 : 90;
            else
                return ((__x*__y) > 0) ? 90 : -90;
        }
    }

    private Vector2 evalConnectPosition(Vector3 cv) throws NullPointerException {
        Vector2 r;
        if (turn != null){
            float padx = (cv.x > 0) ? _getWidth() : _getHeight();
            float pady = (cv.y > 0) ? _getHeight() : _getWidth();

            float shift = (turn.x > 0 || turn.y > 0) ? CH - CW : 0;
            r = new Vector2(_getX() + cv.x*padx + turn.x*shift, _getY() + cv.y*pady + turn.y*shift);
        }
        else
            r = new Vector2(_getX() + cv.x*_getWidth(), _getY() + cv.y*_getHeight());
        return r;
    }

    void removeTurn(int loopDirection) {
        if (loopDirection == -1 && this.prev != null) {
            if (turn != null) {
                this.prev.connected.x = turn.x;
                this.prev.connected.y = turn.y;
                turn = null;
                this.connect(this.prev.connected, this.prev.tile, loopDirection);
            }
            this.prev.tile.removeTurn(loopDirection);
        }
        else if (loopDirection == 1 && this.next != null){
            if (turn != null) {
                this.next.connected.x = turn.x;
                this.next.connected.y = turn.y;
                turn = null;
                this.connect(this.next.connected, this.next.tile, loopDirection);
            }
            this.next.tile.removeTurn(loopDirection);
        }
    }

    void chainUpdate(int direction) {
        if (direction == 1 && this.next != null) {
            this.connect(this.next.connected, this.next.tile, direction);
            this.next.tile.chainUpdate(direction);
        }
        else if (direction == -1 && this.prev != null){
            this.connect(this.prev.connected, this.prev.tile, direction);
            this.prev.tile.chainCommit(direction);
        }
    }

    void chainCommit(int direction) {
        if (direction == 1) {
            this.commit();
            if (this.next != null)
                this.next.tile.chainCommit(direction);
        }
        else if (direction == -1){
            this.commit();
            if (this.prev != null)
                this.prev.tile.chainCommit(direction);
        }
    }

    static void initAssets() {
        textures = new TextureRegion(Domino.assetManager.get("domino.png", Texture.class)).split(69,132);
        for (TextureRegion[] texture : textures)
            for (int j = 0; j < textures[0].length; j++)
                texture[j].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public String toString() {
        int x0 = (int)vectors[0].x;
        int x1 = (int)vectors[1].x;
        int y0 = (int)vectors[0].y;
        int y1 = (int)vectors[1].y;
        int z0 = (int)vectors[0].z;
        int z1 = (int)vectors[1].z;
        return String.format("[%d %d,%d %d,%d %d]", z0, x0, y0, x1, y1, z1);
    }

    String printChain() {
        if (this.next == null)
            return this.toString();
        return this.toString() + " " + this.next.tile.printChain();
    }
}