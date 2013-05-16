package com.me.mygdxgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class NetworkReceiver implements Runnable {
	private DatagramSocket socket;
	ByteBuffer buf;
	byte[] byteArr;
	DatagramPacket packet;
	int myID;


	public NetworkReceiver(DatagramSocket socket) {
		this.socket = socket;
		buf = ByteBuffer.allocate(1024);
		byteArr = buf.array();
		packet = new DatagramPacket(byteArr, byteArr.length);
	}


	public void run() {
		int numPlayers;
		int msgType;
		HashMap<Integer, float[]> pissa = new HashMap<Integer, float[]>();

		while (true) {
//			System.out.println("FUUUCK!");
			try {
				socket.receive(packet);
				numPlayers = (packet.getLength() - 4) / 4 / 3;				
				msgType = buf.getInt();

				switch (msgType) {
				case UDPMessageTypes.WORLD_REFRESH:
					for (int i = 0; i < numPlayers; i ++) {
						int id = buf.getInt();
						float x = buf.getFloat();
						float y = buf.getFloat();
						if (id != myID) {
							pissa.put(id, new float[] {x, y});
							System.out.println("Player " + id + " moved to " + x + ", " + y);
						}
						else {
							System.out.println("Got myself... Fuck it!");
						}
					}
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
}
