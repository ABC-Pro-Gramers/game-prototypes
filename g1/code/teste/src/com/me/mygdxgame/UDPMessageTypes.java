package com.me.mygdxgame;

public class UDPMessageTypes {
//    ### --- Server to Client --- ###

//	  # World refresh message. Updates the client with new position data for other players.
//	  #  => 32-bit for WORLD_REFRESH ID
//	  #  => up to 126 32-bit values, where each pair of values
	public static final int WORLD_REFRESH  = 0;

//	  # Handshake message containing the new client's ID
	public static final int YOUR_ID        = 1;


//	  ### --- Client to Server --- ###
	public static final int MOVE           = 2;
	public static final int KEEP_ALIVE     = 3;
}
