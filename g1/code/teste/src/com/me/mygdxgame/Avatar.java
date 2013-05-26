package com.me.mygdxgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Avatar {
	public int id;
	public float x;
	public float y;
	public int direction;
	public boolean moving;
	
	private SpriteManager sprites;
	private TextureRegion currentFrame;
	private MyGdxGame game;
	
	
	public Avatar(MyGdxGame game, int id, float x, float y) {
		this.game = game;
		sprites = SpriteManager.getInstance();
		this.id = id;
		this.x = x;
		this.y = y;
	}
	

	public void update(float x, float y) {
		if (x == this.x && y == this.y) {
			moving = false;
		}
		else {
			this.x = x;
			this.y = y;
		}
	}
	
	
	public void render (SpriteBatch spriteBatch, float stateTime) {
		currentFrame = sprites.leftAnime.getKeyFrame(stateTime,true);
		spriteBatch.draw(currentFrame, (game.w/2.0f) + (x - game.pos_x)/game.zoomlvl, (game.h/2.0f) + (y - game.pos_y)/game.zoomlvl);
	}
}
