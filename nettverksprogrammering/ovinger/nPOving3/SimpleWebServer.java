import java.io.*;
import java.net.*;

public class SimpleWebServer {
  public static void main(String[] args) {
    int port = 8080;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server running on http://localhost:" + port);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        new ClientHandler(clientSocket).start();
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }
}

class ClientHandler extends Thread {
  private Socket clientSocket;

  public ClientHandler(Socket socket) {
    this.clientSocket = socket;
  }

  @Override
  public void run() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

      String requestLine = reader.readLine();
      if (requestLine == null) return;

      System.out.println("Received: " + requestLine);

      // Hent forespurt URL
      String[] requestParts = requestLine.split(" ");
      if (requestParts.length < 2) return;

      String response;
      String content;

      switch (requestParts[1]) {
        case "/":
          content = "<html><h1>Velkommen til hovedsiden!</h1></html>";
          break;
        case "/page1":
          content = "<html><h1>Dette er side 1.</h1></html>";
          break;
        case "/page2":
          content = "<html><h1>Dette er side 2.</h1></html>";
          break;
        default:
          content = "<html><h1>404 - Side ikke funnet</h1></html>";
          break;
      }

      response = "HTTP/1.1 200 OK\r\n" +
          "Content-Type: text/html\r\n" +
          "Content-Length: " + content.length() + "\r\n" +
          "Connection: close\r\n\r\n" +
          content;

      writer.write(response);
      writer.flush();

    } catch (IOException e) {
      System.err.println("Client error: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("Error closing socket: " + e.getMessage());
      }
    }
  }
}
