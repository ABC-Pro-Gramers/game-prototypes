module UDPMessageTypes
  ### --- Server to Client --- ###

  # World refresh message. Updates the client with new position data for other players.
  #  => 32-bit for WORLD_REFRESH ID
  #  => up to 126 32-bit values, where each pair of values
  WORLD_REFRESH  = 0

  # Handshake message containing the new client's ID
  YOUR_ID        = 1


  ### --- Client to Server --- ###
  MOVE           = 2
  KEEP_ALIVE     = 3
end


module Constants
  MAXPACKETS     = 30
  CLIENT_TIMEOUT = 10
end
