import java.util.*;
import java.net.*;
import java.io.*;
public class Server2 {
  private static final int PORT = 4007;
  ServerSocket sSocket;
  public static void main(String[] args) throws Exception {
    System.out.println("The server is running.");
    ServerSocket listener = new ServerSocket(PORT);
    int clientNum = 1;
    try {
      while (true) {
        new Handler(listener.accept(), clientNum).start();
        System.out.println("Client " + clientNum + " is connected!");
        clientNum++;
      }
    } finally {
      listener.close();
    }
  }
  private static class Handler extends Thread {
    private ObjectOutputStream outputStream; //stream write to the socket
    private ObjectInputStream inputStream;
    private int no;
    private Socket connection;
    public Handler(Socket connection, int no) {
      this.connection = connection;
      this.no = no;
    }
    public void run() {
      try {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        System.out.println("Connection received from client " + no);
        // ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());
        // ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());

        try {
          while (true) {
            System.out.println("Here 1");
            String functionality = (String) inputStream.readObject();
            System.out.println("functionality: "+functionality);
            if (functionality.equals("get")) {
              System.out.println("Here 2");
              String fileName = (String) inputStream.readObject();
              System.out.println("Received fileName: " + fileName);
              File file = new File(fileName);
              if (file.exists() && !file.isDirectory()) {
                System.out.println("Here 3");
                outputStream.writeObject("Yes_File");
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
                // System.out.println("Here 3");
                // outputStream.writeObject("Yes_File");
                // try (FileInputStream fileInputStream = new FileInputStream(file)) {
                //   int bytesRead;
                //   byte[] buffer = new byte[1024];
                //   long totalSize = file.length();
                //   outputStream.writeLong(totalSize);
                //   outputStream.flush();
                //   long bytesSent = 0;
                //   while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                //     outputStream.write(buffer, 0, bytesRead);
                //     outputStream.flush();
                //     bytesSent += bytesRead;
                //     if (bytesSent == totalSize) {
                //       break;
                //     }
                //   }
                //   System.out.println("File Sent to client");
                // } catch (EOFException e) {
                //   System.err.println("Connection closed by client after file transfer was complete" + e.getMessage());
                // }

              } else {
                outputStream.writeObject("No_File");
                System.err.println("File does not exist");
              }
            } else {
              String fileYn = (String) inputStream.readObject();
              if (fileYn.equals("Yes_File")) {
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream("newUploadTestFile"+no+".pptx.");
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
                // System.out.println("upload fileName: " + functionality);
                // DataInputStream dataInputStream = new DataInputStream(inputStream);
                // FileOutputStream fileOutputStream1 = new FileOutputStream("newUploadTestFile"+no+".pptx.");
                // byte[] buffer = new byte[1024];
                // int read = 0;
                // while ((read = dataInputStream.read(buffer)) != -1) {
                //   fileOutputStream1.write(buffer, 0, read);
                // }
                // try {
                //   fileOutputStream1.flush();
                //   System.out.println("closing fileoutputstream...");
                //   fileOutputStream1.close();
                // } catch (IOException e) {
                //   System.err.println("An error occurred while closing the FileOutputStream: " + e.getMessage());
                // }
                // try {
                //   System.out.println("closing datainputstream...");
                //   dataInputStream.close();
                // } catch (IOException e) {
                //   System.err.println("An error occurred while closing the DataInputStream: " + e.getMessage());
                // }
                System.out.println("File uploaded");
              } else {
                System.out.println("No File to Upload");
              }
            }
          }
        } catch (EOFException e) {
          System.err.println("Connection closed after file transfer was complete" + e.getMessage());
        } 
      } catch (IOException ioException) {
        System.out.println("Disconnect with Client " + no);
      }
      catch (ClassNotFoundException classnot) {
        System.err.println("Data received in unknown format");
      }
    }
  }
}