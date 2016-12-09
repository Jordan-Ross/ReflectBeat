package com.example.reflectbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

/**
 * Created by Jordan on 12/7/2016.
 * Handles all Graphics of the application
 */
public class GraphicsController {

    private Random random;

    public Viewport viewport;
    private OrthographicCamera camera;

    private Pool<HitCircle> hitCirclePool;
    public Array<HitCircle> activeHitCircles;

    public static final int RENDER_WIDTH = 540;
    public static final int RENDER_HEIGHT = 960;
    public static final int LINE_HEIGHT = 150;
    public static final int LINE_WIDTH = 40;
    public static final int HIT_SPRITE_SIZE = 64;

    private SpriteBatch batch;
    private Sprite hitLine;
    public static final int hitLine_tolerance = 40;

    public static Texture hitcircleTexture;
    public static Texture hitcircleFailTexture;
    private Texture hitLineTexture;

    private Texture explosion;
    private static final int FRAME_COLS = 8;
    private static final int FRAME_ROWS = 1;
    TextureRegion[] explosionFrames;
    private Animation explosionAnimation;
    public Array<Explosion> activeExplosions;

    private TextureRegion currentFrame;

    BitmapFont font;

    GraphicsController() {
        batch = new SpriteBatch();
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(RENDER_WIDTH, RENDER_HEIGHT, camera);

        hitcircleTexture = new Texture("hitcircle.png");
        hitcircleFailTexture = new Texture("hitcircle_fail.png");
        hitLineTexture = new Texture("hitline.png");

        // EXPLOSION
        explosion = new Texture("sanicnew2x.png");
        TextureRegion[][] temp = TextureRegion.split(explosion, explosion.getWidth() / FRAME_COLS, explosion.getHeight() / FRAME_ROWS);
        explosionFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                explosionFrames[index++] = temp[i][j];
            }
        }
        explosionAnimation = new Animation(0.025f, explosionFrames);
        activeExplosions = new Array<Explosion>();

        hitLine = new Sprite(hitLineTexture);
        hitLine.setPosition(0, LINE_HEIGHT);

        // The pool is better on memory or something
        hitCirclePool = new Pool<HitCircle>() {
            @Override
            protected HitCircle newObject() {
                return new  HitCircle(false, random.nextBoolean() ? 1 : -1);
            }
        };

        activeHitCircles = new Array<HitCircle>();

        random = new Random();
        font = new BitmapFont();
        // TODO: Font could probably look a bit better
        font = new BitmapFont(Gdx.files.internal("gothic.fnt"), false);
        font.getData().setScale(1.5f);
    }

    public void processGraphics() {
        camera.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveHitcircles();

        // Process Graphics
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
            font.draw(batch,
                    ReflectBeat.scoreStr,
                    0,
                    camera.viewportHeight / 2,
                    RENDER_WIDTH,
                    Align.center,
                    false);

            batch.draw(hitLine, 0, LINE_HEIGHT);

            for (HitCircle hit : activeHitCircles) {
                hit.draw(batch);
            }

            for (Explosion exp : activeExplosions) {
                float stateTime = exp.getCurrentFrame(Gdx.graphics.getDeltaTime());
                if (explosionAnimation.isAnimationFinished(stateTime)) {
                    activeExplosions.removeValue(exp, true);
                }
                else {
                    batch.draw(explosionAnimation.getKeyFrame(stateTime), exp.xpos, LINE_HEIGHT);
                }
            }
        batch.end();

        removeHitcircles();
    }

    public void spawnHitcircle(HitObject note) {
        HitCircle hit = hitCirclePool.obtain();
        hit.init(false, note.x_vel, note.y_vel, note.x_pos, note.y_pos);
        activeHitCircles.add(hit);
    }


    public boolean checkInLineHitbox(float x, float y) {
        return y < hitLine.getY() + hitLine.getHeight() + hitLine_tolerance &&
                    y > hitLine.getY() - hitLine_tolerance;
    }

    private void removeHitcircles() {
        for (HitCircle hit : activeHitCircles) {
            if (!hit.alive) {
                activeHitCircles.removeValue(hit, true);
                hitCirclePool.free(hit);
            }
        }
    }

    //Handle Hitcircle movement
    private void moveHitcircles() {
        for (HitCircle hit : activeHitCircles) {
            //long test = System.nanoTime();
            hit.moveCircle(Gdx.graphics.getDeltaTime());
            //test -= System.nanoTime();
            //Gdx.app.log("moveHitcircles", Long.toString(test));

            // If hitcircle passed below line
            if (hit.getY() < LINE_HEIGHT - HIT_SPRITE_SIZE) {
                if (hit.getY() < -HIT_SPRITE_SIZE) {
                    // Below Screen (Remove hitcircle)
                    hit.alive = false;
                    //spawnHitcircle(0, -speed);
                }
                else {
                    // Just below line (Hit fail)
                    hit.setTexture(hitcircleFailTexture);
                    ReflectBeat.resetScore();
                }
            }
        }
    }

    public void resize(int width, int height) {
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
    }

    public void dispose() {
        batch.dispose();
        hitcircleFailTexture.dispose();
        hitcircleTexture.dispose();
        hitLineTexture.dispose();
        hitCirclePool.freeAll(activeHitCircles);
        ReflectBeat.score = 0;
    }
}
