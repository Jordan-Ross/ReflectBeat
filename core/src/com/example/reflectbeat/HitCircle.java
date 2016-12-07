package com.example.reflectbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

import java.util.Locale;

/**
 * Created by Jordan on 11/3/2016.
 */

public class HitCircle extends Sprite implements Pool.Poolable {

    private float xspeed, yspeed;
    private int x_direction; // -1 is left
    public boolean alive;
    private final float hitbox_diff = 32;

    int HIT_SPRITE_SIZE = Graphics.HIT_SPRITE_SIZE;
    int RENDER_WIDTH = Graphics.RENDER_WIDTH;
    int RENDER_HEIGHT = Graphics.RENDER_HEIGHT;

    public HitCircle(boolean fail, float x, float y, int xdir, float xspeed, float yspeed) {
        //super(fail ? "hitcircle_fail.png" : "hitcircle.png");
        super(fail ? Graphics.hitcircle_fail_texture : Graphics.hitcircle_texture);

        this.setPosition(x, y);

        // TODO: Make this not suck
        if (this.getX() < 2)
            x_direction = 1;
        else if (this.getX() > RENDER_WIDTH - HIT_SPRITE_SIZE - 2)
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
        float yAmount = yspeed * deltaTime;
        if (getX() + xAmount > RENDER_WIDTH - HIT_SPRITE_SIZE
                || getX() - xAmount < 0) {
            x_direction = -1 * x_direction;
        }
        // Warning: don't use this it's dumb
        //setRotation(getRotation() + x_direction * -4);
        super.translate((float) x_direction * xAmount, yAmount);
    }

    public void init(boolean fail, float xspeed, float yspeed) {
        this.setTexture(fail ? Graphics.hitcircle_fail_texture : Graphics.hitcircle_texture);
        setPosition(RENDER_WIDTH/2, RENDER_HEIGHT);
        this.xspeed = xspeed;
        this.yspeed = yspeed;
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
}
