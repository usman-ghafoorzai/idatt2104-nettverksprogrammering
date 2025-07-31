import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UDPClient {
  public static void main(String[] args) throws Exception {
    DatagramSocket socket = new DatagramSocket();
    InetAddress serverAddress = InetAddress.getByName("localhost");
    int port = 9876;

    // Eksempeldata (to vektorer)
    double[] vec1 = {1.0, 2.0, 3.0};
    double[] vec2 = {4.0, 5.0, 6.0};

    // Serialiserer vektorene til bytes
    ByteBuffer buffer = ByteBuffer.allocate(8 * vec1.length * 2);
    for (int i = 0; i < vec1.length; i++) {
      buffer.putDouble(vec1[i]);
      buffer.putDouble(vec2[i]);
    }

    // Sender forespørsel
    byte[] data = buffer.array();
    DatagramPacket request = new DatagramPacket(data, data.length, serverAddress, port);
    socket.send(request);

    // Mottar svar
    byte[] responseBuffer = new byte[8];
    DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
    socket.receive(response);

    double dotProduct = ByteBuffer.wrap(response.getData()).getDouble();
    System.out.println("Dot-produkt: " + dotProduct);
    socket.close();
  }
}
