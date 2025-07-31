import java.net.*;
import java.nio.ByteBuffer;

public class UDPServer {
  public static void main(String[] args) throws Exception {
    DatagramSocket socket = new DatagramSocket(9876);
    byte[] receiveBuffer = new byte[1024];

    while (true) {
      DatagramPacket request = new DatagramPacket(receiveBuffer, receiveBuffer.length);
      socket.receive(request);

      ByteBuffer buffer = ByteBuffer.wrap(request.getData());
      int numElements = request.getLength() / 16; // 8 byte per double, 2 vektorer
      double dotProduct = 0.0;

      for (int i = 0; i < numElements; i++) {
        double a = buffer.getDouble();
        double b = buffer.getDouble();
        dotProduct += a * b;
      }

      // Send svar tilbake
      byte[] responseData = ByteBuffer.allocate(8).putDouble(dotProduct).array();
      DatagramPacket response = new DatagramPacket(responseData, responseData.length, request.getAddress(), request.getPort());
      socket.send(response);
    }
  }
}
