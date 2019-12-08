package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGPlayerStatusGUI implements Graphics {
    private final static int DEPTH = 1001;
    private int playerMoney;
    private float playerHealth;
    private ARPGItem playerCurrentItem;

    protected ARPGPlayerStatusGUI(int startingMoney, float healthPoints, ARPGItem currentItem) {
        playerMoney = startingMoney;
        playerHealth = healthPoints;
        playerCurrentItem = currentItem;
    }

    @Override
    public void draw(Canvas canvas) {
        drawItem(canvas,drawGearDisplay(canvas));
        drawDigits(canvas, drawCoinsDisplay(canvas));
        drawHearts(canvas);
    }

    private ImageGraphics drawGearDisplay(Canvas canvas) {
        float width = canvas.getScaledWidth();
        float height = canvas.getScaledHeight();
        Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

        ImageGraphics gearDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"), 1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(0, height - 1.75f), 1, DEPTH);

        gearDisplay.draw(canvas);
        return gearDisplay;
    }

    private void drawItem(Canvas canvas, ImageGraphics gearDisplay) {
        Vector anchor = gearDisplay.getAnchor();

        if (playerCurrentItem != null) {
            ImageGraphics item = new ImageGraphics(ResourcePath.getSprite(playerCurrentItem.getSpriteName()), 1.f, 1.f, new RegionOfInterest(0, 0, 16, 16), anchor.add(0.25f, 0.3f), 1, DEPTH);

            item.draw(canvas);
        }
    }

    private ImageGraphics drawCoinsDisplay(Canvas canvas) {
        float width = canvas.getScaledWidth();
        float height = canvas.getScaledHeight();
        Vector anchor = canvas.getTransform().getOrigin().sub(width/2, height/2);

        ImageGraphics coinsDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/coinsDisplay"), 5.f,2.5f, new RegionOfInterest(0,0,64,32),anchor,1,DEPTH);

        coinsDisplay.draw(canvas);
        return coinsDisplay;
    }

    private void drawDigits(Canvas canvas, ImageGraphics coinsDisplay) {
        Vector anchor = coinsDisplay.getAnchor();
        int moneyCalculation = playerMoney;

        for (int i = 0; i < 3; ++i) {
            ImageGraphics digit = new ImageGraphics(ResourcePath.getSprite("zelda/digits"),0.9f,0.9f, calculateROI(moneyCalculation % 10),anchor.add(3.8f - 0.9f * i,0.85f),1,DEPTH);
            digit.draw(canvas);
            moneyCalculation /= 10;
        }
    }

    private void drawHearts(Canvas canvas) {
        float width = canvas.getScaledWidth();
        float height = canvas.getScaledHeight();
        Vector anchor = canvas.getTransform().getOrigin().add(-width/2, height/2);

        float healthCalculation = playerHealth;
        for (int i = 0; i < 5; ++i) {
            ImageGraphics heart = calculateHeart(healthCalculation, anchor.add(1.75f + i, -1.5f));
            heart.draw(canvas);
            healthCalculation -= 1;
        }
    }

    private ImageGraphics calculateHeart(float health, Vector anchor) {
        ImageGraphics heart;
        if (health >= 1) {
            heart = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1.f,1.f, new RegionOfInterest(32,0,16,16),anchor,1,DEPTH);
        } else if (health > 0) {
            heart = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1.f,1.f, new RegionOfInterest(16,0,16,16),anchor,1,DEPTH);
        } else {
            heart = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1.f,1.f, new RegionOfInterest(0,0,16,16),anchor,1,DEPTH);
        }

        return heart;
    }


    private RegionOfInterest calculateROI(int digit) {
        int x;
        int y;

        if (digit < 0 || digit > 9) {
            return null;
        }

        if (digit == 0) {
            x = 1;
            y = 2;
        } else {
            x = (digit - 1) % 4;
            y = (digit-1)/4;
        }

        x *= 16;
        y *= 16;
        return new RegionOfInterest(x,y,16,16);
    }

    public void update(int playerMoney, float playerHealth, ARPGItem playerCurrentItem) {
        this.playerMoney = playerMoney;
        this.playerHealth = playerHealth;
        this.playerCurrentItem = playerCurrentItem;
    }
}
