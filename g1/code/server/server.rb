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
    disconnect_loop
    threads.each { |t| t.join }
  end


  def receive_loop
    loop do
      packet, sender = @s.recvfrom(16)
      data = packet.unpack("l>1g*")

      c = nil
      @clients_mutex.synchronize { c = @clients[sender] }
      c = handshake(sender) if c.nil?
      c[:x] = data[1]
      c[:y] = data[2]
      c[:last_seen] = Time.now
      @clients_mutex.synchronize { @clients[sender] = c }
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
        @clients.each_value { |v| data << v[:id] << v[:x] << v[:y] if !v[:x].nil? }
        packet = data.pack("l>1" + "l>1g2" * (data.length / 3))
        @clients.keys.each { |k| @s.send(packet, 0, k[3], k[1]) }
      end
    end
  end


  def disconnect_loop
    puts "Hello!"
    loop do
      sleep 1
      now = Time.now
      @clients_mutex.synchronize do
        @clients.delete_if { |_, client|
          if now - client[:last_seen] > Constants::CLIENT_TIMEOUT
            puts "Disconnected client #{client[:id]}"
            true
          else
            false
          end
        }
      end
    end
  end


  def handshake addr
    @cur_id += 1
    puts "New client connection from #{addr[2]}, ID = #{@cur_id}"
    c = { id: @cur_id }
    @clients_mutex.synchronize do
      @clients[addr] = c
    end
    data = [UDPMessageTypes::YOUR_ID, @cur_id]
    packet = data.pack("l>2")
    @s.send(packet, 0, addr[3], addr[1])
    return c
  end
end



if __FILE__ == $0
  d = Daemon.new
  d.run
end
