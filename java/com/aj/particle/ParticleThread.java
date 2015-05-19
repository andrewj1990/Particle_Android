package com.aj.particle;

import android.graphics.Canvas;

public class ParticleThread extends Thread {

    private ParticleView view;
    private boolean running = false;

    public ParticleThread(ParticleView view) {
        this.view = view;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {

        Canvas c = null;
        long prevTime = System.nanoTime();
        double ns = 1000000000.0 / 60.0;
        double delta = 0.0;
        long timer = System.currentTimeMillis();
        while (running) {
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {

                    long now = System.nanoTime();
                    delta += (now - prevTime) / ns;
                    prevTime = now;
                    while (delta >= 1.0) {
                        view.update();
                        delta--;
                    }

                    view.render(c);

                    if (System.currentTimeMillis() - timer > 1000) {
                        timer += 1000;
                    }
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
        }

    }

}
