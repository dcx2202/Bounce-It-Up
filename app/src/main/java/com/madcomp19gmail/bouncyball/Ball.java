package com.madcomp19gmail.bouncyball;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class Ball
{

    Vector2 position;
    Vector2 velocity;
    Vector2 acceleration;
    float angle;
    BallAttributes attributes;

    Bitmap image;
    int sound;
    int trail;

    ArrayList<Vector2> trailPositions;

    boolean dragged;

    int iteration;
    boolean reactive;


    public Ball(float x, float y, BallAttributes ba, Bitmap img, int trailHue, int sound)
    {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
        angle = 0;
        attributes = ba;
        this.sound = sound;

        if(sound!=0)
            SoundPoolManager.getInstance().loadSound(sound);

        if(img != null)
            image = img;

        trail = trailHue;

        reactive = (trail == -4);

        trailPositions = new ArrayList<>();
        trailPositions.add(new Vector2(position.x, position.y));

        dragged = false;

        iteration = 0;
    }

    public void move()
    {
        if(dragged)
            return;

        float prev_velY = velocity.y;
        float rightLimit = GameView.width - attributes.radius;
        float bottomLimit = GameView.height - attributes.radius;

        applyForce(new Vector2(-velocity.x * attributes.friction, -velocity.y * attributes.friction));

        acceleration.add(new Vector2(0, attributes.gravity));
        velocity.add(acceleration);
        position.add(velocity);


        if(angle + velocity.x > 360)
        {
            float a = 360 - angle;
            float velX = velocity.x - a;
            angle = velX;
        }
        else if(angle + velocity.x < -360)
        {
            float a = -360 - angle;
            float velX = velocity.x + a;
            angle = velX;
        }
        else
            angle += velocity.x;


        acceleration.mult(0);


        if (position.x >= rightLimit) {
            position.x = rightLimit - 1;

            if(attributes.type != 2)
                applyForce(new Vector2(velocity.x * attributes.bounce, 0));
            else
                applyForce(new Vector2(- velocity.x * attributes.bounce, 0));

            velocity.x *= -1;
            playSound();

            GameWorld.addDrop(position);

            if(reactive)
                trail = new Random().nextInt(361);
        }
        if (position.x <= attributes.radius) {
            position.x = attributes.radius + 1;

            if(attributes.type != 2)
                applyForce(new Vector2(velocity.x * attributes.bounce, 0));
            else
                applyForce(new Vector2(- velocity.x * attributes.bounce, 0));

            velocity.x *= -1;
            playSound();

            GameWorld.addDrop(position);

            if(reactive)
                trail = new Random().nextInt(361);
        }
        if (position.y >= bottomLimit) {
            position.y = bottomLimit;

            if(attributes.type != 2)
                applyForce(new Vector2(0,velocity.y * attributes.bounce));
            else
                applyForce(new Vector2(0,- velocity.y * attributes.bounce));

            velocity.y *= -1;

            if(Math.abs(prev_velY - velocity.y) > 20f)
            {
                playSound();

                GameWorld.addDrop(position);

                if(reactive)
                    trail = new Random().nextInt(361);
            }
            else if(Math.abs(prev_velY - velocity.y) > 5f)
                playSound();
            else if(Math.abs(prev_velY - velocity.y) < 1f)
                applyForce(new Vector2(-velocity.x * attributes.friction, 0));
        }
        if (position.y <= attributes.radius) {
            position.y = attributes.radius + 1;

            if(attributes.type != 2)
                applyForce(new Vector2(0,velocity.y * attributes.bounce));
            else
                applyForce(new Vector2(0,- velocity.y * attributes.bounce));

            velocity.y *= -1;
            playSound();

            GameWorld.addDrop(position);

            if(reactive)
                trail = new Random().nextInt(361);
        }

        // If there is a max velocity set and the ball would be travelling faster than that then clip it
        if(attributes.MAX_VELOCITY > 0)
        {
            if(velocity.x > attributes.MAX_VELOCITY || velocity.y > attributes.MAX_VELOCITY)
            {
                if (velocity.x > velocity.y)
                {
                    velocity.y = (velocity.y * attributes.MAX_VELOCITY) / velocity.x;
                    velocity.x = attributes.MAX_VELOCITY;
                }
                else if (velocity.y > velocity.x)
                {
                    velocity.x = (velocity.x * attributes.MAX_VELOCITY) / velocity.y;
                    velocity.y = attributes.MAX_VELOCITY;
                }
            }
        }

        // Trail -1 --> clear (no trail)
        if(trail == -1)
            return;

        trailPositions.add(new Vector2(position.x, position.y));

        if(trailPositions.size() > 20)
            trailPositions.remove(0);
    }

    public void applyForce(Vector2 force)
    {
        acceleration.add(force);
    }

    private void playSound()
    {
        if(sound != 0)
            SoundPoolManager.getInstance().playSound();
    }

    private void drawTrail(Canvas canvas, float hue, float saturation, float value)
    {
        int size = trailPositions.size();

        Paint p = new Paint();

        for(int i = 0; i < size; i++)
        {
            int alpha = 255 - (size - i) * 10;
            float[] values = {hue, saturation, value}; // hue (0-360) ; saturation (0-1) ; value (0-1)
            p.setColor(Color.HSVToColor(alpha, values));

            canvas.drawCircle(trailPositions.get(i).x, trailPositions.get(i).y, attributes.radius - 2 * (size - i), p);
        }
    }

    public void display(Canvas canvas)
    {
        if(iteration > 360)
            iteration = 0;
        else
            iteration++;

        if(image != null)
        {
            Paint p = new Paint();

            if(trail != -1 && attributes.type != 3) // clear == -1
            {
                if(trail == -2) // rainbow
                {
                    int size = trailPositions.size();

                    for(int i = 0; i < size; i++)
                    {
                        int hue = i * (360 / size);
                        int alpha = 255 - (size - i) * 10;
                        float[] values = {hue, 1, 1}; // hue (0-360) ; saturation (0-1) ; value (0-1)
                        p.setColor(Color.HSVToColor(alpha, values));

                        canvas.drawCircle(trailPositions.get(i).x, trailPositions.get(i).y, attributes.radius - 2 * (size - i), p);
                    }
                }
                else if(trail == -3) // spectrum
                    drawTrail(canvas, iteration, 1, 1);
                else if(trail == -5) // black
                    drawTrail(canvas, 0, 0, 0);
                else if(trail == -6) // gray #1
                    drawTrail(canvas, 0, 0, 0.2f);
                else if(trail == -7) // gray #2
                    drawTrail(canvas, 0, 0, 0.43f);
                else if(trail == -8) // gray #2
                    drawTrail(canvas, 0, 0, 0.68f);
                else if(trail == -9) // white
                    drawTrail(canvas, 0, 0, 1);
                else if(trail >= 0 && trail <= 360 || reactive)// color/reactive
                    drawTrail(canvas, trail, 1, 1);
            }

            if(attributes.type == 0)
            {
                canvas.save(); //Saving the canvas and later restoring it so only this image will be rotated.

                canvas.rotate(angle, position.x, position.y);

                canvas.drawBitmap(image, position.x - attributes.radius, position.y - attributes.radius, null);

                canvas.restore();
            }
            else if(attributes.type == 1)
            {
                float[] values = {iteration, 1, 1}; // hue (0-360) ; saturation (0-1) ; value (0-1)
                p.setColor(Color.HSVToColor(255, values));

                canvas.drawCircle(position.x, position.y, attributes.radius, p);
            }
            else if(attributes.type == 2)
            {
                float[] values = {trail, 1, 1}; // hue (0-360) ; saturation (0-1) ; value (0-1)

                if(values[0] < 0)
                {
                    values[0] = 115f;
                    values[1] = 0.44f;
                }

                p.setColor(Color.HSVToColor(255, values));

                canvas.drawCircle(position.x, position.y, attributes.radius, p);
            }
            else if(attributes.type == 3)
            {
                int shades = 10;
                float[] values = {0, 0, 0};

                for(int i = 0; i < shades; i++)
                {
                    p.setColor(Color.HSVToColor(i * 20, values));
                    canvas.drawCircle(position.x, position.y, attributes.radius - i, p);
                }

                switch (StorageManager.getInstance().getActiveBoost())
                {
                    case 1:
                        p.setColor(Color.argb(255, 219, 141, 33));
                        break;
                    case 2:
                        p.setColor(Color.argb(255, 255, 218, 0));
                        break;
                    case 5:
                        p.setColor(Color.argb(255, 255, 114, 0));
                        break;
                    case 10:
                        p.setColor(Color.argb(255, 251, 31, 16));
                        break;
                    case 50:
                        p.setColor(Color.argb(255, 185, 0, 112));
                        break;
                    default:
                        break;
                }

                canvas.drawCircle(position.x, position.y, attributes.radius - shades, p);

                p.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawCircle(position.x, position.y, attributes.radius - shades - GameView.getDropCount() / 12, p);
                //canvas.drawCircle(position.x, position.y, attributes.radius - shades - (float) (Math.random() * 10f), p);
            }
        }
    }
}
