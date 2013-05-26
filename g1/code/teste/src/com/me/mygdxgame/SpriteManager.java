package com.me.mygdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteManager {
	private static final SpriteManager theInstance = new SpriteManager();
	private static final int ANIME_FRAMES = 4;
	private static final int ANIME_DIRECTIONS = 4;

	private TextureRegion [] upFrames;
	private TextureRegion [] downFrames;
	private TextureRegion [] leftFrames;
	private TextureRegion [] rightFrames;

	public Animation upAnime;
	public Animation downAnime;
	public Animation leftAnime;
	public Animation rightAnime;

	Texture wSheet;


	private SpriteManager() {
		wSheet = new Texture(Gdx.files.internal("data/character1.png"));

		TextureRegion[][] tmp = TextureRegion.split(wSheet, wSheet.getWidth()/ANIME_FRAMES, wSheet.getHeight()/ANIME_DIRECTIONS);
		int index = 0;
		upFrames = new TextureRegion[ANIME_FRAMES];
		downFrames = new TextureRegion[ANIME_FRAMES];
		leftFrames = new TextureRegion[ANIME_FRAMES];
		rightFrames = new TextureRegion[ANIME_FRAMES];

		for(int i = 0 ; i < ANIME_FRAMES ; i++)
		{
			upFrames[index] = tmp[0][i];
			rightFrames[index] = tmp[1][i];
			downFrames[index] = tmp[2][i];//FIXME: tira esta merda...
			leftFrames[index] = tmp[3][i];
			index++;
		}

		upAnime = new Animation(0.05f, upFrames);
		downAnime = new Animation(0.05f, downFrames);
		leftAnime = new Animation(0.05f, leftFrames);
		rightAnime = new Animation(0.05f, rightFrames);
	}

	
	public static SpriteManager getInstance() {
		return theInstance;
	}
}
