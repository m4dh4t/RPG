package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGPlayerStatusGUI implements Graphics {
    private static final int MAX_HEART = 5;

    private float width;
    private float height;
    private Vector anchor;

    private ImageGraphics[] heartDisplay;
    private ImageGraphics[] gearDisplay;
    private ImageGraphics[] coinsDisplay;

    public void drawGUI(Canvas canvas, float hp, ARPGItem item, int money){
        width = canvas.getScaledWidth();
        height = canvas.getScaledHeight();
        anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

        heartDisplay(hp);
        gearDisplay(item);
        coinsDisplay(money);

        draw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        for(int i = 0; i < gearDisplay.length; i++){
            gearDisplay[i].draw(canvas);
        }

        for(int i = 0; i < heartDisplay.length; i++){
            heartDisplay[i].draw(canvas);
        }

        for(int i = 0; i < coinsDisplay.length; i++){
            coinsDisplay[i].draw(canvas);
        }
    }

    private void gearDisplay(ARPGItem item){
        gearDisplay = new ImageGraphics[2];

        //BACKGROUND
        gearDisplay[0] = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"), 1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(new Vector(0, height - 1.5f)), 1, 2000);
        //ITEM
        if (item != null) {
            if (item == ARPGItem.WINGS) {
                gearDisplay[1] = new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()), 1f, 1f, null, anchor.add(new Vector(0.25f, height - 1.25f)), 1, 2001);
            }
            gearDisplay[1] = new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()), 1f, 1f, new RegionOfInterest(0, 0, 16, 16), anchor.add(new Vector(0.25f, height - 1.25f)), 1, 2001);
        } else {
            gearDisplay[1] = new ImageGraphics(null, 1.f, 1.f, null);
        }
    }

    private void heartDisplay(float hp){
        heartDisplay = new ImageGraphics[MAX_HEART];
        int fullHearts = (int)(hp / 2);

        //FULL HEARTS
        for(int i = 0; i < fullHearts; i++){
            heartDisplay[i] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1f, 1f, new RegionOfInterest(32, 0, 16, 16), anchor.add(new Vector(1.75f+i, height - 1.25f)), 1, 2000);
        }

        //HALF-HEART
        if(hp % 2 != 0){
            heartDisplay[fullHearts] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1f, 1f, new RegionOfInterest(16, 0, 16, 16), anchor.add(new Vector(1.75f+fullHearts, height - 1.25f)), 1, 2000);
            //EMPTY-HEARTS W/ OFFSET
            for(int i = fullHearts+1; i < MAX_HEART; i++){
                heartDisplay[i] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1f, 1f, new RegionOfInterest(0, 0, 16, 16), anchor.add(new Vector(1.75f+i, height - 1.25f)), 1, 2000);
            }
        } else {
            //EMPTY-HEARTS W/O OFFSET
            for(int i = fullHearts; i < MAX_HEART; i++){
                heartDisplay[i] = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 1f, 1f, new RegionOfInterest(0, 0, 16, 16), anchor.add(new Vector(1.75f+i, height - 1.25f)), 1, 2000);
            }
        }
    }

    private void coinsDisplay(int money){
        String number = Integer.toString(money);
        coinsDisplay = new ImageGraphics[1 + number.length()];
        int[] digits = new int[number.length()];

        for(int i = 0; i < number.length(); i++){
            digits[i] = number.charAt(i) - 48;
        }

        //BACKGROUND
        coinsDisplay[number.length()] = new ImageGraphics(ResourcePath.getSprite("zelda/coinsDisplay"), 4f, 2f, new RegionOfInterest(0, 0, 64, 32), anchor.add(new Vector(0, 0)), 1, 2000);

        //DIGITS
        for(int i = 0; i < digits.length; i++){
            int x;
            int y;

            if (digits[i] == 0) {
                x = 1;
                y = 2;
            } else {
                x = (digits[i] - 1) % 4;
                y = digits[i] / 4;
            }

            coinsDisplay[i] = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.9f, 0.9f, new RegionOfInterest(x * 16, y * 16, 16, 16), anchor.add(new Vector(1.5f+(i*0.7f), 0.6f)), 1, 2001);
        }
    }
}
