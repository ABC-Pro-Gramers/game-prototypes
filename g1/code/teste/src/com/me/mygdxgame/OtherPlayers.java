package com.me.mygdxgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class OtherPlayers implements Runnable {
	private MyGdxGame game;
	private DatagramSocket socket;

	private DatagramPacket packet;
	private ByteBuffer buf;
	private byte[] bufArray;

	private int myID;

	// Double-buffer for updating player positions
	private HashMap<Integer, Avatar> frontBuffer;
	private HashMap<Integer, Avatar> backBuffer;


	public OtherPlayers(MyGdxGame game, DatagramSocket socket) {
		this.game = game;
		this.socket = socket;

		frontBuffer = new HashMap<Integer, Avatar>();
		backBuffer = new HashMap<Integer, Avatar>();

		buf = ByteBuffer.allocate(1024);
		bufArray = buf.array();
		packet = new DatagramPacket(bufArray, bufArray.length);
	}


	public void run() {
		int msgType;

		while (true) {
			try {
				socket.receive(packet);	// TODO/FIXME: Add spinlock to ignore old packets for the case where framerate < packets/second		
				msgType = buf.getInt();

				switch (msgType) {
				case UDPMessageTypes.WORLD_REFRESH:
					updateBuffers();
					break;
				case UDPMessageTypes.YOUR_ID:
					myID = buf.getInt();
					System.out.println("Got my ID = " + myID);
					break;
				default:
					System.out.println("Received message of unknown type: " + msgType);
					System.out.println("Size = " + packet.getLength());
					break;
				}

				buf.clear();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private void updateBuffers() {
		int numPlayers = (packet.getLength() - 4) / 4 / 3;

		for (int i = 0; i < numPlayers; i ++) {
			int id = buf.getInt();
			float x = buf.getFloat();
			float y = buf.getFloat();
			if (id != myID) {
				Avatar player = backBuffer.get(id);
				if (player == null) {
					player = new Avatar(game, id, x, y);
					backBuffer.put(id, player);
				}
				else {
					player.update(x, y);
				}
				System.out.println("Player " + id + " moved to " + x + ", " + y);
			}
			else {
				System.out.println("Got myself... Fuck it!");
			}
		}
		
		// Swap buffers :)
		synchronized (frontBuffer) {
			HashMap<Integer, Avatar> tmp = frontBuffer;
			frontBuffer = backBuffer;
			backBuffer = tmp;
		}
	}


	public void render(SpriteBatch spriteBatch, float stateTime) {
		synchronized (frontBuffer) {
			for (Avatar p: frontBuffer.values()) {
				p.render(spriteBatch, stateTime);
			}
		}
	}
}
