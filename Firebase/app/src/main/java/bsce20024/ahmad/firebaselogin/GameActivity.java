package bsce20024.ahmad.firebaselogin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this));
    }
}

class GameView extends View {
    private List<Bitmap> birdFrames;       // My Bird
    private List<Bitmap> enemyFrames;       // Enemy
    private Bitmap background, gameover;
    private Bitmap[] tubes;
    private Bitmap ground;
    private Bitmap bulletBitmap;
    private int birdX, birdY;
    private int birdFrameIndex;
    private int screenWidth, screenHeight;
    private int tubeVelocity = 30; // Speed at which tubes move
    private int tubeX, groundX;
    private int birdVelocity, birdGravity = 3;
    private long lastFrameTime;
    private int score = 0;
    private boolean tubePassed = false;
    private boolean gameOver = false;
    private int enemyX, enemyY;
    private int enemyVelocity = 20;
    private int enemyFrameIndex;
    private boolean enemyVisible = true;
    private List<Bullet> bullets = new ArrayList<>();

    private int enemyWidth, enemyHeight; // New variables to store enemy dimensions

    public GameView(Context context) {
        super(context);
        birdFrames = new ArrayList<>();
        birdFrames.add(BitmapFactory.decodeResource(getResources(), R.drawable.bird));
        birdFrames.add(BitmapFactory.decodeResource(getResources(), R.drawable.bird2));
        enemyFrames = new ArrayList<>();
        // Load and resize enemy frames
        Bitmap enemy1 = BitmapFactory.decodeResource(getResources(), R.drawable.sprite1);
        Bitmap enemy2 = BitmapFactory.decodeResource(getResources(), R.drawable.sprite2);
        // Scale the enemy bitmaps to a smaller size
        enemyWidth = enemy1.getWidth() / 2; // Adjust these values as needed
        enemyHeight = enemy1.getHeight() / 2;
        enemyFrames.add(Bitmap.createScaledBitmap(enemy1, enemyWidth, enemyHeight, false));
        enemyFrames.add(Bitmap.createScaledBitmap(enemy2, enemyWidth, enemyHeight, false));
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
        tubes = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.toptube),
                BitmapFactory.decodeResource(getResources(), R.drawable.bottomtube)
        };
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground2);

        // Load the bullet bitmap and scale it down
        Bitmap originalBulletBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bullets);
        int bulletWidth = originalBulletBitmap.getWidth() / 5; // Scale down to a fifth of original size
        int bulletHeight = originalBulletBitmap.getHeight() / 5; // Scale down to a fifth of original size
        bulletBitmap = Bitmap.createScaledBitmap(originalBulletBitmap, bulletWidth, bulletHeight, false);

        birdFrameIndex = 0;
        tubeX = screenWidth;
        groundX = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long currentTime = System.currentTimeMillis();
        if (currentTime > lastFrameTime + 30) {
            updateGameState();
            lastFrameTime = currentTime;
        }
        drawGameElements(canvas);
        invalidate(); // Continue the game loop
    }

    private void updateGameState() {
        if (!gameOver) {
            // Update bird's position
            birdY += birdVelocity;
            birdVelocity += birdGravity;

            // Update tubes' position
            tubeX -= tubeVelocity;
            if (tubeX < -tubes[0].getWidth()) { // Reset tube position
                tubeX = screenWidth;
                tubePassed = false; // Reset tubePassed when the tube resets
            }

            // Update ground's position for a scrolling effect
            groundX -= tubeVelocity;
            if (groundX < -ground.getWidth()) {
                groundX = 0;
            }

            if (checkCollision()) {
                gameOver();
            } else {
                updateScore();
            }

            // Cycle through bird frames for animation
            birdFrameIndex = (birdFrameIndex + 1) % birdFrames.size();

            // Update bullets' positions and check for collisions with the enemy
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                bullet.x += bullet.velocityX;
                if (bullet.x > screenWidth) {
                    iterator.remove();
                } else if (enemyVisible && checkBulletCollision(bullet)) {
                    iterator.remove();
                    enemyVisible = false;
                }
            }
        }
        if (enemyVisible) {
            enemyX -= enemyVelocity;
            if (enemyX < -enemyWidth) {
                enemyX = screenWidth;
                enemyVisible = false;
            }
        } else {
            if (Math.random() < 0.01) {
                enemyX = screenWidth;
                enemyVisible = true;
                enemyY = screenHeight / 3;
            }
        }
    }

    private boolean checkBulletCollision(Bullet bullet) {
        Rect bulletRect = new Rect(bullet.x, bullet.y, bullet.x + bulletBitmap.getWidth(), bullet.y + bulletBitmap.getHeight());
        Rect enemyRect = new Rect(enemyX, enemyY, enemyX + enemyWidth, enemyY + enemyHeight);
        return Rect.intersects(bulletRect, enemyRect);
    }

    private void drawGameElements(Canvas canvas) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        // Draw background
        Rect destRect = new Rect(0, 0, screenWidth, screenHeight);
        canvas.drawBitmap(background, null, destRect, null);

        // Calculate tube positions
        int tubeTopY = -(screenHeight / 2);
        int tubeBottomY = screenHeight / 2;

        // Draw tubes
        canvas.drawBitmap(tubes[0], tubeX, tubeTopY, null); // Top tube
        canvas.drawBitmap(tubes[1], tubeX, tubeBottomY, null); // Bottom tube

        // Draw bird
        Bitmap currentFrame = birdFrames.get(birdFrameIndex);
        canvas.drawBitmap(currentFrame, birdX, birdY, null);

        // Draw ground
        canvas.drawBitmap(ground, groundX, screenHeight - ground.getHeight(), null);
        canvas.drawBitmap(ground, groundX + screenWidth, screenHeight - ground.getHeight(), null);

        // Draw bullets
        for (Bullet bullet : bullets) {
            canvas.drawBitmap(bulletBitmap, bullet.x, bullet.y, null);
        }

        // Draw score
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Score: " + score, 20, 80, paint);

        if (gameOver) {
            gameover = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
            int centerX = screenWidth / 2;
            int centerY = screenHeight / 2;

            int left = centerX - gameover.getWidth() / 2;
            int top = centerY - gameover.getHeight() / 2;

            int right = left + gameover.getWidth();
            int bottom = top + gameover.getHeight();
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(gameover, null, rect, null);
        }

        if (enemyVisible) {
            Bitmap enemyFrame = enemyFrames.get(enemyFrameIndex);
            canvas.drawBitmap(enemyFrame, enemyX, enemyY, null);
            enemyFrameIndex = (enemyFrameIndex + 1) % enemyFrames.size();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() < screenWidth / 2) {
                birdVelocity = -30; // Makes the bird jump
            } else {
                shootBullet();
            }
            score++;
        } else if (gameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            restartGame();
        }
        return true;
    }

    private boolean checkCollision() {
        // Check collision with ground
        if (birdY + birdFrames.get(birdFrameIndex).getHeight() >= screenHeight - ground.getHeight()) {
            return true; // Collision occurred
        }

        // Rectangle around the bird
        Rect birdRect = new Rect(birdX, birdY, birdX + birdFrames.get(birdFrameIndex).getWidth(), birdY + birdFrames.get(birdFrameIndex).getHeight());

        // Rectangles around the tubes
        int tubeTopY = -(screenHeight / 2);
        int tubeBottomY = screenHeight / 2;
        Rect topTubeRect = new Rect(tubeX, tubeTopY, tubeX + tubes[0].getWidth(), tubeTopY + tubes[0].getHeight());
        Rect bottomTubeRect = new Rect(tubeX, tubeBottomY, tubeX + tubes[1].getWidth(), tubeBottomY + tubes[1].getHeight());

        if (enemyVisible) {
            Rect enemyRect = new Rect(enemyX, enemyY, enemyX + enemyWidth, enemyY + enemyHeight);
            if (birdRect.intersect(enemyRect)) {
                return true;
            }
        }
        // Check if the bird collides with either tube
        return birdRect.intersect(topTubeRect) || birdRect.intersect(bottomTubeRect);
    }

    private void updateScore() {
        if (tubeX + tubes[0].getWidth() < birdX && !tubePassed) {
            score++;
            tubePassed = true; // Mark the tube as passed to avoid incrementing the score again for the same tube
        }
    }

    private void gameOver() {
        gameOver = true;
    }

    private void restartGame() {
        score = 0;
        gameOver = false;
        birdY = screenHeight / 2;
        tubeX = screenWidth;
        birdVelocity = 0;
        bullets.clear();
        enemyVisible = true;
    }

    private void shootBullet() {
        int bulletX = birdX + birdFrames.get(birdFrameIndex).getWidth();
        int bulletY = birdY + birdFrames.get(birdFrameIndex).getHeight() / 2;
        Bullet bullet = new Bullet(bulletX, bulletY, 20); // Adjust the bullet velocity if needed
        bullets.add(bullet);
    }

    private class Bullet {
        int x, y, velocityX;

        Bullet(int x, int y, int velocityX) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
        }
    }
}
