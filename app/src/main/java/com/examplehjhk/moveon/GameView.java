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

import com.examplehjhk.moveon.domain.Bird;
import com.examplehjhk.moveon.domain.Obstacle;
import com.examplehjhk.moveon.hardware.ThumbSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private static final String TAG = "GameView";

    private Thread gameThread;
    private boolean isPlaying = false;
    private boolean isGameStarted = false;

    private final SurfaceHolder surfaceHolder;
    private final Paint paint;

    private int screenX, screenY;

    // ===== DOMAIN =====
    private final Bird bird = new Bird();
    private final List<Obstacle> obstacles = new ArrayList<>();

    // ===== HARDWARE =====
    private final ThumbSlider thumbSlider = new ThumbSlider();

    // ===== INPUT =====
    private volatile float armAngle = 0f;   // 0..90
    private static final int POTI_MAX = 330;

    // ===== GAME =====
    private int score = 0;
    private int obstaclesSpawned = 0;

    // ✅ Level steuert Hindernisanzahl
    private int maxObstacles = 30;

    private float gameSpeed = 15f;
    private int currentROM = 90;

    private final Random random = new Random();
    private final Path heartPath = new Path();

    private int colorBackground;
    private int colorObstacle;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        colorBackground = ContextCompat.getColor(context, R.color.moveon_background);
        colorObstacle = ContextCompat.getColor(context, R.color.obstacle_color);

        bird.baseSize = 60f;
        bird.scale = 1.0f;

        initializeHeartPath(bird.baseSize);
    }

    private void initializeHeartPath(float size) {
        heartPath.reset();
        float o = size * 2;
        heartPath.moveTo(0, size / 4 - o);
        heartPath.cubicTo(0, -size / 2 - o, -size, -size / 2 - o, -size, size / 2 - o);
        heartPath.cubicTo(-size, size - o, 0, size * 1.5f - o, 0, size * 2 - o);
        heartPath.cubicTo(0, size * 1.5f - o, size, size - o, size, size / 2 - o);
        heartPath.cubicTo(size, -size / 2 - o, 0, -size / 2 - o, 0, size / 4 - o);
    }

    // ===== API von außen =====

    public void setROM(int rom) {
        this.currentROM = rom;
    }

    public void setGameStarted(boolean started) {
        isGameStarted = started;
        if (started) resetGame();
    }

    public void setArmAngle(float angle) {
        armAngle = Math.max(0f, Math.min(90f, angle));
    }

    /** MQTT / Hardware → Rohwert */
    public void setPotiRaw(int raw) {
        thumbSlider.setFromRaw(raw, POTI_MAX);
    }

    /** Optional: UI-Slider 0..1 */
    public void setThumbSlider01(float value01) {
        thumbSlider.setValue01(value01);
    }

    /** ✅ Level → Anzahl Hindernisse */
    public void setMaxObstacles(int count) {
        if (count < 1) count = 1;
        this.maxObstacles = count;
    }

    // Für Logging/Session
    public float getBirdX() { return bird.x; }
    public float getBirdY() { return bird.y; }

    // ===== GAME LOOP =====

    @Override
    public void run() {
        while (isPlaying) {
            update();
            drawFrame();
            sleep();
        }
    }

    private void update() {
        // ===== ThumbSlider steuert Größe + Speed =====
        float s = thumbSlider.getValue01(); // 0..1

        float maxScale = 1.0f;
        float minScale = 0.40f;
        bird.scale = maxScale - s * (maxScale - minScale);

        float minSpeed = 8f;
        float maxSpeed = 35f;
        gameSpeed = minSpeed + s * (maxSpeed - minSpeed);

        // ===== Winkel → Bird.y =====
        float h = bird.getScaledHeight();
        float minY = h;
        float maxY = screenY;

        float targetY = maxY - (armAngle / 90f) * (maxY - minY);
        bird.y += (targetY - bird.y) * 0.10f;

        if (!isGameStarted) return;

        // ===== Obstacles spawnen =====
        if (obstaclesSpawned < maxObstacles) {
            if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < screenX - 500) {
                obstacles.add(new Obstacle(screenX, screenY, currentROM, h, random));
                obstaclesSpawned++;
            }
        } else if (obstacles.isEmpty()) {
            // Level geschafft
            isGameStarted = false;
            if (onGameOverListener != null) onGameOverListener.onGameOver(true);
        }

        // ===== Obstacles bewegen / Collision / Score =====
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            o.x -= gameSpeed;

            if (o.collides(bird)) {
                isGameStarted = false;
                if (onGameOverListener != null) onGameOverListener.onGameOver(false);
                break;
            }

            if (!o.passed && o.x + o.width < bird.x) {
                o.passed = true;
                score++;
                if (onScoreChangeListener != null) onScoreChangeListener.onScoreChanged(score);
            }

            if (o.x + o.width < 0) {
                obstacles.remove(i--);
            }
        }
    }

    private void drawFrame() {
        if (!surfaceHolder.getSurface().isValid()) return;

        Canvas c = surfaceHolder.lockCanvas();
        if (c == null) return;

        c.drawColor(colorBackground);

        // Bird (Herz)
        drawHeart(c);

        // Obstacles
        paint.setColor(colorObstacle);
        for (Obstacle o : obstacles) {
            c.drawRect(o.x, 0, o.x + o.width, o.gapY, paint);
            c.drawRect(o.x, o.gapY + o.gapSize, o.x + o.width, screenY, paint);
        }

        surfaceHolder.unlockCanvasAndPost(c);
    }

    private void drawHeart(Canvas c) {
        paint.setColor(Color.RED);
        c.save();
        c.translate(bird.x, bird.y);
        c.scale(bird.scale, bird.scale);
        c.drawPath(heartPath, paint);
        c.restore();
    }

    private void sleep() {
        try { Thread.sleep(17); }
        catch (InterruptedException e) { Log.e(TAG, "sleep", e); }
    }

    public void resume() {
        if (isPlaying) return;
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            if (gameThread != null) gameThread.join();
        } catch (InterruptedException ignored) {}
        gameThread = null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        screenX = w;
        screenY = h;

        bird.x = screenX / 4f;
        bird.y = screenY;
    }

    private void resetGame() {
        score = 0;
        obstaclesSpawned = 0;
        obstacles.clear();

        bird.x = screenX / 4f;
        bird.y = screenY;

        armAngle = 0f;
        thumbSlider.setValue01(0f);

        if (onScoreChangeListener != null) onScoreChangeListener.onScoreChanged(score);
    }

    // ===== CALLBACKS =====
    public interface OnGameOverListener {
        void onGameOver(boolean success);
    }

    public interface OnScoreChangeListener {
        void onScoreChanged(int score);
    }

    private OnGameOverListener onGameOverListener;
    private OnScoreChangeListener onScoreChangeListener;

    public void setOnGameOverListener(OnGameOverListener l) { onGameOverListener = l; }
    public void setOnScoreChangeListener(OnScoreChangeListener l) { onScoreChangeListener = l; }
}
