require 'socket'

s = UDPSocket.new
s.bind("192.168.1.67", 0)
s.connect("127.0.0.1", 5005)

data = [1.23, 3.14, 6.28]
net_data = data.pack("f*")

s.send(net_data, 0)
