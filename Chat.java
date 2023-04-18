import java.util.*;
import java.net.*;
import java.io.*;

public class Chat {
    // private static final int PORT = 4007;
    // ServerSocket sSocket;
    private static Socket connection;
    private static ObjectOutputStream outputStream; //stream write to the socket
    private static ObjectInputStream inputStream;

    public static void main(String[] args) throws Exception {
        // System.out.println("The server is running.");
        // ServerSocket listener = new ServerSocket(PORT);
        ServerSocket serverSocket = new ServerSocket(0);
        int bobPort = serverSocket.getLocalPort();
        int clientNum = 1;
        System.out.println("Person"+clientNum+" is listening on port " + bobPort);
        try {
        // while (true) {
          System.out.println("0");
            new WritingThread().start();
            System.out.println("1");

            connection = serverSocket.accept();

            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream());
            System.out.println("Connection received from client ");
            // PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
            try {
                String messageFromClient;
                while (true) {
                    messageFromClient = (String) inputStream.readObject();
                    String[] messageArr = messageFromClient.split(" ");
                    System.out.println(messageFromClient);
                    if (messageArr[0].equals("transfer")) {
                      // System.out.println("message1: " + messageArr[1]);
                          //receive()
                            DataInputStream dataInputStream = new DataInputStream(inputStream);
                            FileOutputStream fileOutputStream = new FileOutputStream("new"+ messageArr[1]);
                            long fileSize = dataInputStream.readLong();
                            System.out.println("fileSize:");
                            System.out.println(fileSize);
                            byte[] buffer = new byte[1024];
                            long totalBytesRead = 0;
                            int read = 0;
                            while (totalBytesRead < fileSize && (read = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                                // if (new String(buffer, 0, read).contains("EOF")) {
                                //     break;
                                // }
                              fileOutputStream.write(buffer, 0, read);
                              fileOutputStream.flush();
                              totalBytesRead += read;
                            }
                            try {
                              System.out.println("closing fileoutputstream...");
                              fileOutputStream.close();
                            } catch (IOException e) {
                              System.err.println("An error occurred while closing the FileOutputStream: " + e.getMessage());
                            }
                    }
        

                }
            } catch (Exception e) {
              System.err.println("Connection closed after file transfer was complete" + e.getMessage());
            } 
            // new ReadingThread(serverSocket.accept(), clientNum).start();
            System.out.println("Here before writing start...");
            
            clientNum++;
        // }
        } finally {
            serverSocket.close();
        }
    }
    
      private static class WritingThread extends Thread {
        private ObjectOutputStream outputStream; //stream write to the socket
        private ObjectInputStream inputStream;
        private int no;
        // public WritingThread(int no) {
        //   this.no = no;
        // }

        
        public void run() {
          try {
            System.out.println("3");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter port number to connect to the other Person: ");
            int AlicePort = Integer.parseInt(reader.readLine());
            Socket AliceSocket = new Socket("localhost", AlicePort);
            System.out.println("Connected to client on port4 " + AlicePort);
            outputStream = new ObjectOutputStream(AliceSocket.getOutputStream()); 
            outputStream.flush();
            // PrintWriter out = new PrintWriter(AliceSocket.getOutputStream(), true);
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                  String[] messageArr = message.split(" ");
                  // System.out.println("message: " + messageArr[0]);
                  if (messageArr[0].equals("transfer")) {
                    // System.out.println("message1: " + messageArr[1]);
                    outputStream.writeObject(messageArr[1]);
                    //sendFile();
                    File file = new File(messageArr[1]);
                    if (file.exists() && !file.isDirectory()) {
                      System.out.println("Here 3");
                      long fileSize = file.length();
                      outputStream.writeLong(fileSize);
                      try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        System.out.println("Here 4");
                        int bytesRead;
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                          outputStream.write(buffer, 0, bytesRead);
                          outputStream.flush();
                        }
                        System.out.println("Here 5");
                        System.out.println("File Sent to client");
                        // outputStream.write("EOF".getBytes());
                        // outputStream.flush();
                      } catch (Exception e) {
                          System.err.println(e);
                      }
                }

                  }
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

    // private static class ReadingThread extends Thread {
    //     private ObjectOutputStream outputStream; //stream write to the socket
    //     private ObjectInputStream inputStream;
    //     private int no;
    //     private Socket connection;
    //     public ReadingThread(Socket connection, int no) {
    //       this.connection = connection;
    //       this.no = no;
    //     }
    //     public void run() {
    //       try {
    //         System.out.println("Connected to Person on port " + connection.getPort());
    //         outputStream = new ObjectOutputStream(connection.getOutputStream());
    //         outputStream.flush();
    //         inputStream = new ObjectInputStream(connection.getInputStream());
    //         System.out.println("Connection received from client " + no);
    //         PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
    //         try {
    //             String messageFromClient;
    //             while (true) {
    //                 messageFromClient = (String) inputStream.readObject();
    //                 if (messageFromClient.contains("transfer")) {
    //                     // file saving...
    //                 }
    //                 System.out.println(messageFromClient);

    //             }
    //         } catch (Exception e) {
    //           System.err.println("Connection closed after file transfer was complete" + e.getMessage());
    //         } 
    //       } catch (Exception e) {
    //         System.out.println("Disconnect with Client " + no);
    //       }
    //     }
    //   }
}
