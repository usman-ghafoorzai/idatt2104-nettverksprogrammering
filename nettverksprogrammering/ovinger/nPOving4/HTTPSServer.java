import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class HTTPSServer {
  public static void main(String[] args) {
    int port = 8443;  // HTTPS-standardporten er 443, men vi bruker 8443 for testing

    try {
      // Last inn KeyStore (SSL-sertifikat)
      char[] password = "password".toCharArray(); // Passord for keystore
      KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream("keystore.jks"), password);

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, password);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kmf.getKeyManagers(), null, null);

      SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
      SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);
      System.out.println("Server running on https://localhost:" + port);

      while (true) {
        SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
        new ClientHandler(clientSocket).start();
      }
    } catch (Exception e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }
}

class ClientHandler extends Thread {
  private SSLSocket clientSocket;

  public ClientHandler(SSLSocket socket) {
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
