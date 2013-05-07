# Absolute max packet size should be 512 bytes. Smaller than that would be best.
# --- Packets coming from the client ---
#
# ::Command packet::
# => 8-bit client command/request id
# => up to 504 bit of data related to the request
#
#
# --- Packets sent to the client ---
# ::World Update::
#
# =>
#

require 'socket'

require_relative 'constants'


class Daemon
  def initialize
    @cur_id = 0
    @clients = {}
    @clients_mutex = Mutex.new
    @s = UDPSocket.new
    @s.bind(nil, 5005)
  end


  def run
    threads = []
    threads << Thread.new { send_loop }
    threads << Thread.new { receive_loop }
    threads << Thread.new { disconnect_loop }
    threads.each { |t| t.join }
  end


  def receive_loop
    loop do
      packet, sender = @s.recvfrom(16)
      data = packet.unpack("f*")

      c = @clients[sender] || handshake(sender)
      @clients_mutex.synchronize {
        c[:x] = data[1]
        c[:y] = data[2]
      }
    end
  end


  def send_loop
    sleep_time = 1.0 / Constants::MAXPACKETS

    loop do
      # TODO:
      # Subtract time spent in last cycle so that we keep
      # a constant rate independent of the time we take to send
      # packets...
      sleep(sleep_time)

      data = [UDPMessageTypes::WORLD_REFRESH]
      @clients_mutex.synchronize do
        @clients.each_value { |v| data << v[:id] << v[:x] << v[:y] }
        packet = data.pack("l1" + "l1f2" * (data.length / 3))
        @clients.keys.each { |k| @s.send(packet, 0, k[3], k[1]) }
      end
    end
  end


  def disconnect_loop
    loop do
      sleep 5
      now = Time.now
      @clients_mutex.synchronize do
        clients.delete_if { |k, v| now - v[:last_seen] > Constants::CLIENT_TIMEOUT }
      end
    end
  end


  def handshake addr
    @cur_id += 1
    c = { id: @cur_id }

    @clients_mutex.synchronize { @clients[addr] = c }

    packet = [UDPMessageTypes.YOUR_ID, cur_id].pack("l*")
    @s.send(packet, 0, addr[3], addr[1])

    return c
  end
end



if __FILE__ == $0
  d = Daemon.new
  d.run
end
