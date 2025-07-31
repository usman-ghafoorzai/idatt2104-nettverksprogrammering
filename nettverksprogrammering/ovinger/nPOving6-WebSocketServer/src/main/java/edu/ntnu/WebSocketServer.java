package edu.ntnu;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.security.MessageDigest;

public class WebSocketServer {
  private static final int PORT = 3001;
  private static final String WEBSOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
  private static final Set<Socket> clients = new HashSet<>();

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("WebSocket server started on port " + PORT);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        clients.add(clientSocket);
        new Thread(() -> handleClient(clientSocket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleClient(Socket clientSocket) {
    try (InputStream inputStream = clientSocket.getInputStream();
         OutputStream outputStream = clientSocket.getOutputStream()) {

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String requestLine = reader.readLine();
      if (!requestLine.startsWith("GET")) {
        clientSocket.close();
        return;
      }

      String webSocketKey = null;
      String line;
      while (!(line = reader.readLine()).isEmpty()) {
        if (line.startsWith("Sec-WebSocket-Key: ")) {
          webSocketKey = line.substring(19);
        }
      }

      if (webSocketKey == null) {
        clientSocket.close();
        return;
      }

      // Beregner WebSocket-aksept-nøkkelen
      String acceptKey = generateWebSocketAcceptKey(webSocketKey);

      // Sender WebSocket-håndtrykkrespons
      String response =
          "HTTP/1.1 101 Switching Protocols\r\n" +
              "Upgrade: websocket\r\n" +
              "Connection: Upgrade\r\n" +
              "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
      System.out.println("Sending handshake response:\n" + response);
      outputStream.write(response.getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
      System.out.println("Handshake successful!");

      while (!clientSocket.isClosed()) {
        String message = decodeWebSocketFrame(inputStream);
        if (message == null) {
          break;
        }

        System.out.println("Received message: " + message);

        // Sender melding tilbake til alle tilkoblede klienter
        broadcastMessage("Server: " + message);
      }

    } catch (IOException e) {
      System.out.println("Client disconnected.");
    } finally {
      clients.remove(clientSocket);
      try {
        clientSocket.close();
      } catch (IOException ignored) {}
    }
  }

  private static String generateWebSocketAcceptKey(String key) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update((key + WEBSOCKET_GUID).getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(md.digest());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static String decodeWebSocketFrame(InputStream inputStream) throws IOException {
    int firstByte = inputStream.read();
    if (firstByte == -1) {
      return null;
    }

    int secondByte = inputStream.read();
    boolean masked = (secondByte & 0x80) != 0;
    int payloadLength = secondByte & 0x7F;

    if (payloadLength == 126) {
      payloadLength = (inputStream.read() << 8) | inputStream.read();
    } else if (payloadLength == 127) {
      payloadLength = (int) ((inputStream.read() << 56) | (inputStream.read() << 48) |
          (inputStream.read() << 40) | (inputStream.read() << 32) |
          (inputStream.read() << 24) | (inputStream.read() << 16) |
          (inputStream.read() << 8) | inputStream.read());
    }

    byte[] mask = new byte[4];
    if (masked) {
      inputStream.read(mask, 0, 4);
    }

    byte[] payload = new byte[payloadLength];
    inputStream.read(payload, 0, payloadLength);

    if (masked) {
      for (int i = 0; i < payloadLength; i++) {
        payload[i] ^= mask[i % 4];
      }
    }

    return new String(payload, StandardCharsets.UTF_8);
  }

  private static void broadcastMessage(String message) {
    byte[] encodedMessage = encodeWebSocketFrame(message);
    for (Socket client : clients) {
      try {
        OutputStream outputStream = client.getOutputStream();
        outputStream.write(encodedMessage);
        outputStream.flush();
      } catch (IOException ignored) {}
    }
  }

  private static byte[] encodeWebSocketFrame(String message) {
    byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
    int payloadLength = messageBytes.length;
    int frameSize = 2 + payloadLength;
    byte[] frame = new byte[frameSize];

    frame[0] = (byte) 0x81; // FIN bit + Text frame opcode
    frame[1] = (byte) payloadLength;

    System.arraycopy(messageBytes, 0, frame, 2, payloadLength);
    return frame;
  }
}
