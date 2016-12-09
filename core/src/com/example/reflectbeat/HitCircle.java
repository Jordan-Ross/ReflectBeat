package com.example.reflectbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

import java.util.Locale;

/**
 * Created by Jordan on 11/3/2016.
 * Acts as a single hit object on the screen
 */

public class HitCircle extends Sprite implements Pool.Poolable {

    private float xspeed, yspeed;
    private int x_direction; // 1 is right, -1 is left
    public boolean alive;
    private final float hitbox_diff = 32;   // HitCircle touch tolerance

    int HIT_SPRITE_SIZE = GraphicsController.HIT_SPRITE_SIZE;
    int RENDER_WIDTH = GraphicsController.RENDER_WIDTH;
    int WIDTH_RIGHT_SPRITE = RENDER_WIDTH - HIT_SPRITE_SIZE;    // Max x pos a circle can spawn
    int RENDER_HEIGHT = GraphicsController.RENDER_HEIGHT;

    public HitCircle(boolean fail, int xdir) {
        super(fail ? GraphicsController.hitcircleFailTexture : GraphicsController.hitcircleTexture);
        x_direction = xdir;
    }

    public HitCircle(boolean fail, float x, float y, int xdir, float xspeed, float yspeed) {
        this(fail, xdir);
        this.setPosition(x, y);

        // Reverses direction if too close to one edge on spawn
        if (this.getX() < 2)
            x_direction = 1;
        else if (this.getX() > WIDTH_RIGHT_SPRITE - 2)
            x_direction = -1;
        else
            x_direction = xdir;

        this.xspeed = xspeed;
        this.yspeed = yspeed;

        this.alive = false;

        Gdx.app.log("HitCircle constructor", String.format(Locale.US, "x: %f   y: %f    xdir: %d",
                getX(), getY(), x_direction));
    }

    public HitCircle(boolean fail, float x, float y, int xdir) {
        this(fail, x, y, xdir, 0, -300);
    }

    public void moveCircle(float deltaTime) {
        float xAmount = xspeed * deltaTime;

        // *boing*
        if (getX() + xAmount > WIDTH_RIGHT_SPRITE
                || getX() - xAmount < 0) {
            x_direction *= -1;
        }
        // Warning: don't use this it's dumb (but looks interesting)
        //setRotation(getRotation() + x_direction * -4);
        super.translate((float) x_direction * xAmount, yspeed * deltaTime);
    }

    public void init(boolean fail, float xspeed, float yspeed, float xpos, float ypos) {
        this.setTexture(fail ? GraphicsController.hitcircleFailTexture : GraphicsController.hitcircleTexture);
        setPosition(RENDER_WIDTH/2, RENDER_HEIGHT);
        this.xspeed = xspeed;
        this.yspeed = yspeed;
        this.setX(xpos);
        this.setY(ypos);
        alive = true;
    }

    public boolean checkTouched(float x, float y) {
        return x < this.getX() + HIT_SPRITE_SIZE + hitbox_diff &&
                y < this.getY() + HIT_SPRITE_SIZE + hitbox_diff &&
                x > this.getX() - hitbox_diff &&
                y > this.getY() - hitbox_diff;
    }

    @Override
    public void reset() {
        setPosition(0,0);
        alive = false;
    }

    public void update (float delta) {

    }
}
