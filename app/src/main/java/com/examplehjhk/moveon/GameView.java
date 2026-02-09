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

/**
 * Custom SurfaceView that handles the game rendering and logic.
 * This class implements a standard game loop using a separate Thread.
 */
public class GameView extends SurfaceView implements Runnable {

    private static final String TAG = "GameView";

    // Game loop control
    private Thread gameThread;
    private boolean isPlaying = false;
    private boolean isGameStarted = false;

    private final SurfaceHolder surfaceHolder;
    private final Paint paint;

    // Screen dimensions initialized in onSizeChanged
    private int screenX, screenY;

    // Domain Objects
    private final Bird bird = new Bird();
    private final List<Obstacle> obstacles = new ArrayList<>();

    // Hardware integration
    private final ThumbSlider thumbSlider = new ThumbSlider();

    // Input state
    private volatile float armAngle = 0f;   // User's arm angle (0 to 90 degrees)
    private static final int POTI_MAX = 330; // Max raw value from the hardware potentiometer

    // Game state
    private int score = 0;
    private int obstaclesSpawned = 0;

    // Level settings
    private int maxObstacles = 30; // Number of obstacles to clear for victory
    private float gameSpeed = 15f;
    private int currentROM = 90;   // ROM

    private final Random random = new Random();
    private final Path heartPath = new Path(); // Path for drawing the heart-shaped heart

    private int colorBackground;
    private int colorObstacle;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Load theme colors from resources
        colorBackground = ContextCompat.getColor(context, R.color.moveon_background);
        colorObstacle = ContextCompat.getColor(context, R.color.obstacle_color);

        bird.baseSize = 60f;
        bird.scale = 1.0f;

        initializeHeartPath(bird.baseSize);
    }

    /**
     * Defines the vector path for the heart shape used as the player character.
     */
    private void initializeHeartPath(float size) {
        heartPath.reset();
        float o = size * 2;
        heartPath.moveTo(0, size / 4 - o);
        heartPath.cubicTo(0, -size / 2 - o, -size, -size / 2 - o, -size, size / 2 - o);
        heartPath.cubicTo(-size, size - o, 0, size * 1.5f - o, 0, size * 2 - o);
        heartPath.cubicTo(0, size * 1.5f - o, size, size - o, size, size / 2 - o);
        heartPath.cubicTo(size, -size / 2 - o, 0, -size / 2 - o, 0, size / 4 - o);
    }

    // external api

    public void setROM(int rom) {
        this.currentROM = rom;
    }

    public void setGameStarted(boolean started) {
        isGameStarted = started;
        if (started) resetGame();
    }

    public void setArmAngle(float angle) {
        // Clamp the angle between 0 and 90 degrees
        armAngle = Math.max(0f, Math.min(90f, angle));
    }

    /** Sets hardware raw value from MQTT */
    public void setPotiRaw(int raw) {
        thumbSlider.setFromRaw(raw, POTI_MAX);
    }

    /** Set slider value manually */
    public void setThumbSlider01(float value01) {
        thumbSlider.setValue01(value01);
    }

    /** Configures how many obstacles must be cleared to finish the level */
    public void setMaxObstacles(int count) {
        if (count < 1) count = 1;
        this.maxObstacles = count;
    }

    public float getBirdX() { return bird.x; }
    public float getBirdY() { return bird.y; }

    // game loop logic

    @Override
    public void run() {
        while (isPlaying) {
            update();     // Calculate positions and logic
            drawFrame();  // Render visuals
            sleep();      // Maintain ~60 FPS
        }
    }

    /**
     * Logic update: Handles movement, collision, and spawning.
     */
    private void update() {
        // ThumbSlider: Influences size and speed
        float s = thumbSlider.getValue01(); // Normalized value 0 to 1

        // Smaller slider value makes the heart bigger
        float maxScale = 1.0f;
        float minScale = 0.40f;
        bird.scale = maxScale - s * (maxScale - minScale);

        // Larger slider value increases game speed
        float minSpeed = 8f;
        float maxSpeed = 35f;
        gameSpeed = minSpeed + s * (maxSpeed - minSpeed);

        // Heart Vertical Movement based on Arm Angle
        float h = bird.getScaledHeight();
        float minY = h;
        float maxY = screenY;

        // Map 0-90 degrees to vertical screen coordinates
        float targetY = maxY - (armAngle / 90f) * (maxY - minY);
        // Linear interpolation for smooth heart movement
        bird.y += (targetY - bird.y) * 0.10f;

        if (!isGameStarted) return;

        // Spawning Obstacles
        if (obstaclesSpawned < maxObstacles) {
            // Spawn a new obstacle if the previous one has traveled far enough
            if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < screenX - 500) {
                obstacles.add(new Obstacle(screenX, screenY, currentROM, h, random));
                obstaclesSpawned++;
            }
        } else if (obstacles.isEmpty()) {
            // Victory: No more obstacles and list is empty
            isGameStarted = false;
            if (onGameOverListener != null) onGameOverListener.onGameOver(true);
        }

        // Obstacle Logic: Move, Collision, Score and Cleanup
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            o.x -= gameSpeed;

            // Check for collision with the heart
            if (o.collides(bird)) {
                isGameStarted = false;
                if (onGameOverListener != null) onGameOverListener.onGameOver(false);
                break;
            }

            // Update score if obstacle is passed successfully
            if (!o.passed && o.x + o.width < bird.x) {
                o.passed = true;
                score++;
                if (onScoreChangeListener != null) onScoreChangeListener.onScoreChanged(score);
            }

            // Remove obstacle if it moves off-screen
            if (o.x + o.width < 0) {
                obstacles.remove(i--);
            }
        }
    }

    /**
     * Rendering logic: Draws the background, bird, and obstacles to the canvas.
     */
    private void drawFrame() {
        if (!surfaceHolder.getSurface().isValid()) return;

        Canvas c = surfaceHolder.lockCanvas();
        if (c == null) return;

        // Clear screen
        c.drawColor(colorBackground);

        // Draw Player (Bird/Heart)
        drawHeart(c);

        // Draw Obstacles
        paint.setColor(colorObstacle);
        for (Obstacle o : obstacles) {
            // Top pipe
            c.drawRect(o.x, 0, o.x + o.width, o.gapY, paint);
            // Bottom pipe
            c.drawRect(o.x, o.gapY + o.gapSize, o.x + o.width, screenY, paint);
        }

        surfaceHolder.unlockCanvasAndPost(c);
    }

    /**
     * Draws the player character heart using the predefined path and current coordinates.
     */
    private void drawHeart(Canvas c) {
        paint.setColor(Color.RED);
        c.save();
        c.translate(bird.x, bird.y);
        c.scale(bird.scale, bird.scale);
        c.drawPath(heartPath, paint);
        c.restore();
    }

    /**
     * Utility method to regulate the game frame rate.
     */
    private void sleep() {
        try { Thread.sleep(17); } // Approx. 60 FPS
        catch (InterruptedException e) { Log.e(TAG, "sleep", e); }
    }

    /**
     * Starts the game thread.
     */
    public void resume() {
        if (isPlaying) return;
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Stops the game thread safely.
     */
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

        // Initial heart position
        bird.x = screenX / 4f;
        bird.y = screenY;
    }

    /**
     * Resets the game state for a new level or retry.
     */
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

    // callback interfaces

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