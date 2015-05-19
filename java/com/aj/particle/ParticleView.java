package com.aj.particle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aj.particle.entity.GravityWell;
import com.aj.particle.entity.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ParticleView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = ParticleView.class.getSimpleName();

    private Bitmap bmp;
    private SurfaceHolder holder;
    private ParticleThread particleThread;
    private Paint paint;

    public int[] pixels;

    private int posX;
    private int posY;
    private int emmitted_particles;
    private int particle_size;
    private Random rand = new Random();

    private List<Particle> particle_list = new ArrayList<>();
    private List<GravityWell> gravity_list = new ArrayList<>();

    final GestureDetector gestureDetector;

    public ParticleView(Context context) {
        super(context);

        particleThread = new ParticleThread(this);
        holder = getHolder();
        holder.addCallback(this);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

//        posX = 0;
//        posY = 0;

        emmitted_particles = 200;

        // add gesture for long click
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
//                Log.e(TAG, "Longpress detected: " + e.getX());
                addGravityWell((int) e.getX(), (int) e.getY());
            }
        });

        // allow user to click on surfaceview
        setFocusable(true);

    }

    public void update() {

        particle_size = particle_list.size();
        for (int i = 0; i < particle_size; i++) {
            Particle particle = particle_list.get(i);
            if (gravity_list.size() > 0) {
                // calculate new dx and dy for each particle
                setParticlePos(particle);
            }
            particle.update();
        }

//        posX += 1;
//        posY += 1;
    }

    protected void render(Canvas canvas) {

        // if bitmap has not been created yet then create it using the size of the canvas
        if (bmp == null) {
            bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            pixels = new int[bmp.getWidth() * bmp.getHeight()];
        }

        // set background to black
        canvas.drawColor(Color.BLACK);

        // clear the canvas
        clear();

        particle_size = particle_list.size();
        for (int i = 0; i < particle_size; i++) {
            particle_list.get(i).render(this);
        }

//        canvas.drawRect(posX, posY, posX+10, posY+10, paint);
//        for (int y = posY; y < posY + 10; y++) {
//            if (y < 0 || y > bmp.getHeight()) break;
//            for (int x = posX; x < posX + 10; x++) {
//                if (x < 0 || x > bmp.getWidth()) break;
//                pixels[y + x*bmp.getWidth()] = Color.rgb(255,0,0);
//            }
//        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.drawBitmap(bmp, 0, 0, null);

        canvas.drawText("Test", 10, 10, paint);

    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
    }

    public void addParticles(int x, int y) {
        if (particle_list.size() < 5000) {
            for (int i = 0; i < emmitted_particles; i++) {
//                int xx = 400 - rand.nextInt(800);
//                int yy = 400 - rand.nextInt(800);
                particle_list.add(new Particle(x, y ));
            }
        }
    }

    public void addGravityWell(int x, int y) {
        gravity_list.add(new GravityWell(x, y));
    }

    public void setParticlePos(Particle particle) {
        // amount each particle will travel -- increases with every pull towards gravity well
        float accX = particle.getAccX();
        float accY = particle.getAccY();

        // particle position
        float px = particle.getX();
        float py = particle.getY();

        // find the amount of pull each gravity well has on a particle
        for (int j = 0; j < gravity_list.size(); j++) {
            GravityWell grav = gravity_list.get(j);

            // gravity well position
            float gx = grav.getX();
            float gy = grav.getY();

            // calculate the direction between the particle and gravity hole
            float dx = gx - px;
            float dy = gy - py;

            // normalize the direction and divide by a factor to mimic the strength of pull
            float length = (float) Math.sqrt(dx*dx + dy*dy);
            float ux = dx / (length*7);
            float uy = dy / (length*7);

            // accumulate the pull of each gravity hole
            accX += ux;
            accY += uy;

        }

        // set the particles change in position
        particle.setdX(accX);
        particle.setdY(accY);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        particleThread.setRunning(true);
        particleThread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        particleThread.setRunning(false);
        while (retry) {
            try {
                particleThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                addParticles((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }
        return true;
//        return super.onTouchEvent(event);
    }

}