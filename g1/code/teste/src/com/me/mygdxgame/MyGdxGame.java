package com.me.mygdxgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.SimpleTileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;



public class MyGdxGame implements ApplicationListener {
	private OrthographicCamera cam;
	private SpriteBatch batch;
	
	TileMapRenderer  tilemapRenderer;
	TiledMap map;
	TileAtlas atlas;
	TileMapRenderer tileMapRenderer;
	int w ,h;
	float zoomlvl = 0.5f;
	float pos_x = 0.0f;
	float pos_y = 0.0f;
	float walk_dir = 0;
	boolean running = false;
	
	public static final float WALK_SPEED = 1;
	public static final float RUN_SPEED = 1.5f;
		
	SpriteManager sprites;
	SpriteBatch spriteBatch;
	TextureRegion currentFrame;
	float stateTime;
	private Sound shoot;
	
	DatagramSocket socket;
	ByteBuffer sendBuf;
    long packetSendPeriod;
	
    OtherPlayers otherPlayers;
	
	@Override
	public void create() {
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		cam = new OrthographicCamera(w, h);
		batch = new SpriteBatch();
		
		map = TiledLoader.createMap(Gdx.files.internal("data/teste 2.tmx"));
		atlas = new SimpleTileAtlas(map, Gdx.files.internal("data/"));
		
		
		shoot = Gdx.audio.newSound(Gdx.files.internal("data/shoot1.wav"));
		
		sprites = SpriteManager.getInstance();
		spriteBatch = new SpriteBatch();
		stateTime = 0f;
	    tileMapRenderer = new TileMapRenderer(map,atlas,32,32,16,16);
	    currentFrame = sprites.downAnime.getKeyFrame(stateTime,true);// Starts up
		
	    packetSendPeriod = (long) 1000.0 / Constants.MAXPACKETS;
	    sendBuf = ByteBuffer.allocate(64);
	    try {
			InetAddress serverAddr = InetAddress.getByName("localhost");
			socket = new DatagramSocket();
			socket.connect(serverAddr, 5005);
			otherPlayers = new OtherPlayers(this, socket);
			new Thread(otherPlayers).start();
	    } catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void render() 
	{		
		
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			// TURBO 
			running = true;
		}
		
		
		if(Gdx.input.isKeyPressed(Keys.SPACE))
		{
			shoot.play();	
		}
		
		stateTime += Gdx.graphics.getDeltaTime(); 
		//System.out.println(stateTime);
		
		
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			walk_dir = (float) Math.toRadians(90);
			currentFrame = sprites.upAnime.getKeyFrame(stateTime,true);
		}
		else if(Gdx.input.isKeyPressed(Keys.DOWN))
		{
			walk_dir = (float) Math.toRadians(-90);
			currentFrame = sprites.downAnime.getKeyFrame(stateTime,true);
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT))
		{
			if (walk_dir == -1)
				walk_dir = (float) Math.toRadians(120);
			walk_dir *= 1.5;
			currentFrame = sprites.leftAnime.getKeyFrame(stateTime,true);
		}
		else if(Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			if (walk_dir == -1)
				walk_dir = 0;
			walk_dir /= 2;
			currentFrame = sprites.rightAnime.getKeyFrame(stateTime,true);
		}
		
		if (walk_dir != -1) {
			pos_x += (float)(Math.cos(walk_dir) * WALK_SPEED);
			pos_y += (float)(Math.sin(walk_dir) * WALK_SPEED);
		}
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		cam.position.set((zoomlvl * w/2.0f)+pos_x, (zoomlvl * h/2.0f) + pos_y,0);
		
		cam.zoom = zoomlvl;
		cam.update();
		
		tileMapRenderer.render(cam);
		
		spriteBatch.begin();
		otherPlayers.render(spriteBatch, stateTime);
		spriteBatch.draw(currentFrame,w/2.0f, h/2.0f);
		spriteBatch.end();
		
		walk_dir = -1;
		running = false;
		
		sendBuf.putInt(0, UDPMessageTypes.MOVE);
		sendBuf.putFloat(4, pos_x);
		sendBuf.putFloat(8, pos_y);
		DatagramPacket p = new DatagramPacket(sendBuf.array(), 12);

		try {
			socket.send(p);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		//texture.dispose();
		
	}



	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	
}
