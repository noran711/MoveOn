package com.examplehjhk.moveon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying;
    private boolean isGameStarted = false;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private int screenX, screenY;
    private float heartY;
    private float armAngle = 90; // Default angle (middle)
    private List<Obstacle> obstacles;
    private int score = 0;
    private Random random;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        paint = new Paint();
        random = new Random();
        obstacles = new ArrayList<>();
    }

    public void setGameStarted(boolean started) {
        this.isGameStarted = started;
        if (started) {
            resetGame();
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        // Map arm angle (e.g., 0-180) to screen height
        // 0 degrees = bottom, 180 degrees = top
        float targetY = screenY - (armAngle / 180f * screenY);
        
        // Smooth transition for the heart
        heartY += (targetY - heartY) * 0.1f;

        if (!isGameStarted) return;

        // Update obstacles
        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < screenX - 500) {
            obstacles.add(new Obstacle(screenX, screenY));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            o.x -= 15; // Speed

            // Collision check
            if (o.collides(screenX / 4f, heartY)) {
                // Game Over - we can reset and stop the game movement
                isGameStarted = false;
                // Notify Activity if needed (via a callback) to show the start button again
                if (onGameOverListener != null) {
                    onGameOverListener.onGameOver();
                }
                break;
            }

            // Score update
            if (!o.passed && o.x < screenX / 4f) {
                o.passed = true;
                score++;
            }

            if (o.x + o.width < 0) {
                obstacles.remove(i);
                i--;
            }
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            
            canvas.drawColor(Color.parseColor("#DFF3FF")); // Background

            // Draw Heart (Player)
            drawHeart(canvas, screenX / 4f, heartY, 60);

            // Draw Obstacles
            paint.setColor(Color.parseColor("#9370DB")); // Purple
            for (Obstacle o : obstacles) {
                canvas.drawRect(o.x, 0, o.x + o.width, o.gapY, paint);
                canvas.drawRect(o.x, o.gapY + o.gapSize, o.x + o.width, screenY, paint);
            }

            // Draw Score
            paint.setColor(Color.BLACK);
            paint.setTextSize(64);
            canvas.drawText("Score: " + score, 50, 100, paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawHeart(Canvas canvas, float x, float y, float size) {
        paint.setColor(Color.RED);
        Path path = new Path();
        // Simple heart path
        path.moveTo(x, y + size / 4);
        path.cubicTo(x, y - size / 2, x - size, y - size / 2, x - size, y + size / 2);
        path.cubicTo(x - size, y + size, x, y + size * 1.5f, x, y + size * 2);
        path.cubicTo(x, y + size * 1.5f, x + size, y + size, x + size, y + size / 2);
        path.cubicTo(x + size, y - size / 2, x, y - size / 2, x, y + size / 4);
        canvas.drawPath(path, paint);
    }

    private void sleep() {
        try {
            Thread.sleep(17); // ~60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenX = w;
        screenY = h;
        heartY = h / 2f;
    }

    public void setArmAngle(float angle) {
        if (angle < 0) angle = 0;
        if (angle > 180) angle = 180;
        this.armAngle = angle;
    }

    private void resetGame() {
        score = 0;
        obstacles.clear();
        heartY = screenY / 2f;
    }

    private class Obstacle {
        float x, width = 150;
        float gapY, gapSize = 400;
        boolean passed = false;

        Obstacle(int screenX, int screenY) {
            this.x = screenX;
            this.gapY = 100 + random.nextInt(screenY - 600);
        }

        boolean collides(float hX, float hY) {
            // Check if heart is within obstacle's x-range
            if (hX + 30 > x && hX - 30 < x + width) {
                // Check if heart is outside the gap (vertical collision)
                return (hY < gapY || hY + 40 > gapY + gapSize);
            }
            return false;
        }
    }

    // Interface for Game Over callback
    public interface OnGameOverListener {
        void onGameOver();
    }

    private OnGameOverListener onGameOverListener;

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.onGameOverListener = listener;
    }
}
