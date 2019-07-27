package com.mygdx.jkdomino.interfaces;

public class Triple<X, Y, Z> {
    public X tile;
    public Y connected;
    public Z connector;
    Triple(X tile, Y connected, Z connector){
        this.tile = tile;
        this.connected = connected;
        this.connector = connector;
    }
}
