package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SplashImage extends Actor {
    private String imgFile;
    private Texture imgTexture;
    private Sprite imgSprite;

    public SplashImage(String fileName, float width, float height, float posX, float posY, float maxW, float maxH) {
        super();
        imgFile = fileName;
        imgTexture = new Texture(Gdx.files.internal(imgFile));
        imgSprite = new Sprite(imgTexture);


        float scaledW = maxW;
        float scaledH = maxH;
        scaledH = maxH;
        scaledW = (maxH / height) * width;
//        if (width > height) {
//            scaledW = maxW;
//            scaledH = (maxW / width) * height;
//        } else {
//            scaledH = maxH;
//            scaledW = (maxH / height) * width;
//        }

        imgSprite.setSize(scaledW, scaledH);
        imgSprite.setPosition( posX, posY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        imgSprite.draw(batch);
    }
}
