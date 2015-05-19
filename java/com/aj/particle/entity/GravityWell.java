package com.aj.particle.entity;

/**
 * Created by Andrew on 15/05/2015.
 */
public class GravityWell {

    private float posX;
    private float posY;

    public GravityWell(int x, int y) {
        posX = x;
        posY = y;
    }

    public void render() {

    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

}
