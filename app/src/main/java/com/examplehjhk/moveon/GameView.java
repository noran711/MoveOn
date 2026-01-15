package com.examplehjhk.moveon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private static final String TAG = "GameView";
    private Thread gameThread;
    private boolean isPlaying;
    private boolean isGameStarted = false;
    private final SurfaceHolder surfaceHolder;
    private final Paint paint;
    private int screenX, screenY;
    private float heartY;
    private float armAngle = 90;
    private final List<Obstacle> obstacles;
    private int score = 0;
    private final Random random;
    private final Path heartPath = new Path();

    private float gameSpeed = 15f;
    private float heartScale = 1.0f;
    private final float baseHeartSize = 60f;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);
        random = new Random();
        obstacles = new ArrayList<>();
        initializeHeartPath(baseHeartSize);
    }

    private void initializeHeartPath(float size) {
        heartPath.reset();
        heartPath.moveTo(0, size / 4);
        heartPath.cubicTo(0, -size / 2, -size, -size / 2, -size, size / 2);
        heartPath.cubicTo(-size, size, 0, size * 1.5f, 0, size * 2);
        heartPath.cubicTo(0, size * 1.5f, size, size, size, size / 2);
        heartPath.cubicTo(size, -size / 2, 0, -size / 2, 0, size / 4);
    }

    public void updateDifficulty(float factor) {
        this.gameSpeed = 15f + (factor * 20f);
        this.heartScale = 1.0f - (factor * 0.6f);
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
        float targetY = screenY - (armAngle / 180f * screenY);
        heartY += (targetY - heartY) * 0.1f;

        if (!isGameStarted) return;

        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < screenX - (500 + gameSpeed * 10)) {
            obstacles.add(new Obstacle(screenX, screenY));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            o.x -= gameSpeed;

            if (o.collides(screenX / 4f, heartY, heartScale)) {
                isGameStarted = false;
                if (onGameOverListener != null) {
                    onGameOverListener.onGameOver();
                }
                break;
            }

            if (!o.passed && (o.x + o.width) < screenX / 4f) {
                o.passed = true;
                score++;
                // Callback an Activity, um UI-Score zu aktualisieren
                if (onScoreChangeListener != null) {
                    onScoreChangeListener.onScoreChanged(score);
                }
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
            
            canvas.drawColor(Color.parseColor("#DFF3FF"));

            drawHeart(canvas, screenX / 4f, heartY);

            paint.setColor(Color.parseColor("#9370DB"));
            for (Obstacle o : obstacles) {
                canvas.drawRect(o.x, 0, o.x + o.width, o.gapY, paint);
                canvas.drawRect(o.x, o.gapY + o.gapSize, o.x + o.width, screenY, paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawHeart(Canvas canvas, float x, float y) {
        paint.setColor(Color.RED);
        canvas.save();
        canvas.translate(x, y);
        canvas.scale(heartScale, heartScale);
        canvas.drawPath(heartPath, paint);
        canvas.restore();
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted in sleep", e);
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
            Log.e(TAG, "Error joining thread", e);
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
        if (onScoreChangeListener != null) {
            onScoreChangeListener.onScoreChanged(score);
        }
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

        boolean collides(float hX, float hY, float scale) {
            float hitBoxSize = 40 * scale; 
            if (hX + hitBoxSize > x && hX - hitBoxSize < x + width) {
                return (hY - hitBoxSize < gapY || hY + hitBoxSize > gapY + gapSize);
            }
            return false;
        }
    }

    public interface OnGameOverListener {
        void onGameOver();
    }

    public interface OnScoreChangeListener {
        void onScoreChanged(int score);
    }

    private OnGameOverListener onGameOverListener;
    private OnScoreChangeListener onScoreChangeListener;

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.onGameOverListener = listener;
    }

    public void setOnScoreChangeListener(OnScoreChangeListener listener) {
        this.onScoreChangeListener = listener;
    }
}
