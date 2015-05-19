package com.aj.particle.entity;

import android.graphics.Color;
import android.util.Log;

import com.aj.particle.ParticleView;

import java.util.Random;

/**
 * Created by Andrew on 14/05/2015.
 */
public class Particle {

    private final static String TAG = Particle.class.getSimpleName();

    private float posX;
    private float posY;
    private float dx;
    private float dy;
    private float width;
    private float accX;
    private float accY;

    private int r;
    private int g;
    private int b;

    Random rand = new Random();

    public Particle(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        width = 3.0f;

        dy = (float) rand.nextGaussian() * rand.nextFloat();
        dx = (float) rand.nextGaussian() * rand.nextFloat();

        accX = 0;
        accY = 0;
//        Log.d(TAG, "dx : " + dx + " dy : "  + dy + " dz : " + dz);

        r = 51;
        g = 16;
        b = 0;

    }

    public void update() {
        posX += dx;
        posY += dy;
    }

    public void render(ParticleView view) {
        for (int y = (int) (posY - width); y < (posY + width); y++) {
            if (y < 0 || y >= view.getHeight()) break;
            for (int x = (int) (posX - width); x < (posX + width); x++) {
                if (x < 0 || x >= view.getWidth()) break;
                // add the particle colour to overlapping particles
                view.pixels[x + y*view.getWidth()] += Color.rgb(r, g, b);
            }
        }
    }

    public float getAccX() {
        return accX;
    }

    public float getAccY() {
        return accY;
    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public void setdX(float dx) {
        this.dx = dx;
        accX = dx;
    }

    public void setdY(float dy) {
        this.dy = dy;
        accY = dy;
    }

}
