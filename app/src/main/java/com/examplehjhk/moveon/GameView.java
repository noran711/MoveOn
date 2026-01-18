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
import androidx.core.content.ContextCompat;

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
    private float armAngle = 0; 
    private final List<Obstacle> obstacles;
    private int score = 0;
    private int obstaclesSpawned = 0;
    private final int MAX_OBSTACLES = 30;
    private final Random random;
    private final Path heartPath = new Path();

    private float gameSpeed = 15f;
    private float heartScale = 1.0f;
    private final float baseHeartSize = 60f;
    
    private int currentROM = 90; 

    // Farben aus Ressourcen
    private int colorBackground;
    private int colorObstacle;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);
        random = new Random();
        obstacles = new ArrayList<>();
        
        // Farben initialisieren
        colorBackground = ContextCompat.getColor(context, R.color.moveon_background);
        colorObstacle = ContextCompat.getColor(context, R.color.obstacle_color);
        
        initializeHeartPath(baseHeartSize);
    }

    private void initializeHeartPath(float size) {
        heartPath.reset();
        float offset = size * 2;
        heartPath.moveTo(0, size / 4 - offset);
        heartPath.cubicTo(0, -size / 2 - offset, -size, -size / 2 - offset, -size, size / 2 - offset);
        heartPath.cubicTo(-size, size - offset, 0, size * 1.5f - offset, 0, size * 2 - offset); 
        heartPath.cubicTo(0, size * 1.5f - offset, size, size - offset, size, size / 2 - offset);
        heartPath.cubicTo(size, -size / 2 - offset, 0, -size / 2 - offset, 0, size / 4 - offset);
    }

    public void setROM(int rom) {
        this.currentROM = rom;
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
        float scaledHeight = baseHeartSize * 2 * heartScale;
        float minY = scaledHeight; 
        float maxY = screenY;      
        float range = maxY - minY;
        
        float targetY = maxY - (armAngle / 90f * range);
        
        if (targetY < minY) targetY = minY;
        if (targetY > maxY) targetY = maxY;

        heartY += (targetY - heartY) * 0.1f;

        if (!isGameStarted) return;

        if (obstaclesSpawned < MAX_OBSTACLES) {
            if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < screenX - (500 + gameSpeed * 10)) {
                obstacles.add(new Obstacle(screenX, screenY, currentROM, scaledHeight));
                obstaclesSpawned++;
            }
        } else if (obstacles.isEmpty()) {
            isGameStarted = false;
            if (onGameOverListener != null) {
                onGameOverListener.onGameOver(true);
            }
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            o.x -= gameSpeed;

            if (o.collides(screenX / 4f, heartY, heartScale)) {
                isGameStarted = false;
                if (onGameOverListener != null) {
                    onGameOverListener.onGameOver(false);
                }
                break;
            }

            if (!o.passed && (o.x + o.width) < screenX / 4f) {
                o.passed = true;
                score++;
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
            
            canvas.drawColor(colorBackground);
            drawHeart(canvas, screenX / 4f, heartY);
            
            paint.setColor(colorObstacle);
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
        heartY = h; 
    }

    public void setArmAngle(float angle) {
        this.armAngle = angle;
    }

    private void resetGame() {
        score = 0;
        obstaclesSpawned = 0;
        if (onScoreChangeListener != null) {
            onScoreChangeListener.onScoreChanged(score);
        }
        obstacles.clear();
        heartY = screenY; 
        armAngle = 0;
    }

    private class Obstacle {
        float x, width = 150;
        float gapY, gapSize = 400;
        boolean passed = false;

        Obstacle(int screenX, int screenY, int rom, float heartHeight) {
            this.x = screenX;
            float maxY = screenY; 
            float minY_at_ROM = screenY - (rom / 90f * (screenY - heartHeight));
            float targetHeartTipY = minY_at_ROM + random.nextFloat() * (maxY - minY_at_ROM);
            float heartCenterY = targetHeartTipY - (heartHeight / 2f);
            this.gapY = heartCenterY - (gapSize / 2f);
            if (gapY < 0) gapY = 0;
            if (gapY + gapSize > screenY) gapY = screenY - gapSize;
        }

        boolean collides(float hX, float hY, float scale) {
            float scaledHeight = baseHeartSize * 2 * scale;
            float hitBoxWidth = baseHeartSize * scale; 
            if (hX + hitBoxWidth > x && hX - hitBoxWidth < x + width) {
                if (hY - scaledHeight < gapY) return true;
                if (hY > gapY + gapSize) return true;
            }
            return false;
        }
    }

    public interface OnGameOverListener {
        void onGameOver(boolean success);
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
