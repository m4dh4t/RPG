package ch.epfl.cs107.play.game.tutos.actor;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;

public class SimpleGhost extends Entity{
    private Sprite sprite;
    private float hp;
    private TextGraphics hpText;

    public SimpleGhost(Vector position, String spriteName){
        super(position);
        sprite = new Sprite(spriteName, 1.f, 1.f, this);
        hp = 10;
        hpText = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);

        hpText.setParent(this);
        this.hpText.setAnchor(new Vector(-0.3f, 0.1f));
    }

    @Override
    public void draw(Canvas canvas) {
        hpText.draw(canvas);
        sprite.draw(canvas);
    }

    @Override
    public void update(float deltaTime){
        if(!isWeak()){
            hp -= deltaTime;
            hpText.setText(Integer.toString((int)hp));
        } else {
            hp = 0.f;
        }
    }

    public boolean isWeak(){
        return (hp <= 0.f);
    }

    public void strenghten(){
        hp = 10;
    }

    public void moveUp(){
        setCurrentPosition(getPosition().add(0.f, 0.05f));
    }

    public void moveDown(){
        setCurrentPosition(getPosition().add(0.f, -0.05f));
    }

    public void moveRight(){
        setCurrentPosition(getPosition().add(0.05f, 0.f));
    }

    public void moveLeft(){
        setCurrentPosition(getPosition().add(-0.05f, 0.f));
    }
}