import java.util.*;
import java.net.*;
import java.io.*;

public class Chat {
    // private static final int PORT = 4007;
    // ServerSocket sSocket;
    public static void main(String[] args) throws Exception {
        // System.out.println("The server is running.");
        // ServerSocket listener = new ServerSocket(PORT);
        ServerSocket serverSocket = new ServerSocket(0);
        int bobPort = serverSocket.getLocalPort();
        int clientNum = 1;
        System.out.println("Person"+clientNum+" is listening on port " + bobPort);
        try {
        while (true) {
          new WritingThread(clientNum).start();
            new ReadingThread(serverSocket.accept(), clientNum).start();
            System.out.println("Here before writing start...");
            
            clientNum++;
        }
        } finally {
            serverSocket.close();
        }
    }
    private static class ReadingThread extends Thread {
        private ObjectOutputStream outputStream; //stream write to the socket
        private ObjectInputStream inputStream;
        private int no;
        private Socket connection;
        public ReadingThread(Socket connection, int no) {
          this.connection = connection;
          this.no = no;
        }
        public void run() {
          try {
            System.out.println("Connected to Person on port " + connection.getPort());
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream());
            System.out.println("Connection received from client " + no);
            PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
            try {
                String messageFromClient;
                while (true) {
                    messageFromClient = (String) inputStream.readObject();
                    if (messageFromClient.contains("transfer")) {
                        // file saving...
                    }
                    out.println(messageFromClient);

                }
            } catch (Exception e) {
              System.err.println("Connection closed after file transfer was complete" + e.getMessage());
            } 
          } catch (Exception e) {
            System.out.println("Disconnect with Client " + no);
          }
        }
      }
      private static class WritingThread extends Thread {
        private ObjectOutputStream outputStream; //stream write to the socket
        private ObjectInputStream inputStream;
        private int no;
        public WritingThread(int no) {
          this.no = no;
        }
        public void run() {
          try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter port number to connect to the other Person: ");
            int AlicePort = Integer.parseInt(reader.readLine());
            Socket AliceSocket = new Socket("localhost", AlicePort);
            System.out.println("Connected to client on port " + AlicePort);
            outputStream = new ObjectOutputStream(AliceSocket.getOutputStream()); 
            outputStream.flush();
            // PrintWriter out = new PrintWriter(AliceSocket.getOutputStream(), true);
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    outputStream.writeObject(message);
                }
            } catch (Exception e) {
              System.err.println("Connection closed after file transfer was complete" + e.getMessage());
            } 
          } catch (Exception Exception) {
            System.out.println("Disconnect with Client " + no);
          }
        }
      }
}
