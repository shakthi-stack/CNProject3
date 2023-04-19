import java.util.*;
import java.net.*;
import java.io.*;

public class Chat {
    private static Socket connection;
    private static ObjectOutputStream outputStream; //stream write to the socket
    private static ObjectInputStream inputStream;


    private static void receiveFile(String fileName){
			try{
				int bytes = 0;
				FileOutputStream fileOutputStream= new FileOutputStream(fileName);
				long size=inputStream.readLong();
				byte[] buffer = new byte[1000];
				while (size > 0 && (bytes = inputStream.read(buffer, 0,(int)Math.min(buffer.length, size)))!= -1) {
					fileOutputStream.write(buffer, 0, bytes);
					size = size-bytes; 
				}
				fileOutputStream.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
	
		}
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(0);
        int bobPort = serverSocket.getLocalPort();
        int clientNum = 1;
        System.out.println("Program is listening on port " + bobPort);
        try {
            new WritingThread().start();

            connection = serverSocket.accept();

            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream());
            System.out.println("Connection received from client ");
            try {
                String messageFromClient;
                while (true) {
                    messageFromClient = (String) inputStream.readObject();
                    String[] messageArr = messageFromClient.split(" ");
                    System.out.println(messageFromClient);
                    if (messageArr[0].equals("transfer")) {
                      //receive
                      receiveFile("new" + messageArr[1]);
                    }
        

                }
            } catch (Exception e) {
              System.err.println("Connection closed after file transfer was complete" + e.getMessage());
            } 
            System.out.println("Here before writing start...");
            clientNum++;
        } finally {
            serverSocket.close();
        }
    }
    
      private static class WritingThread extends Thread {
        private static ObjectOutputStream outputStream; //stream write to the socket
        private ObjectInputStream inputStream;
        private int no;
  
        // sendFile function define here
        private static void sendFile(String path){
          try{
            int bytes = 0;
            File file = new File(path);
            
            try{
              if(file.exists()){
                System.out.println("File Found");
                FileInputStream fileInputStream = new FileInputStream(file);
          
                outputStream.writeLong(file.length());
                byte[] buffer = new byte[1000];
                while ((bytes = fileInputStream.read(buffer))!= -1) {
                  outputStream.write(buffer, 0, bytes);
                  outputStream.flush();
                }
                fileInputStream.close();
                System.out.println("File Sent to the Server");

              }
              else{
                System.out.println("File not Found");
                throw new FileNotFoundException();
              }
            }
            catch (FileNotFoundException e) {
              System.out.println("Error: File not found.");
            }
          }
          catch(IOException ioException){
            ioException.printStackTrace();
          } 

        }
        
        public void run() {
          try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter port number to connect to the other Person: ");
            int AlicePort = Integer.parseInt(reader.readLine());
            Socket AliceSocket = new Socket("localhost", AlicePort);
            System.out.println("Connected to client on port" + AlicePort);
            outputStream = new ObjectOutputStream(AliceSocket.getOutputStream()); 
            outputStream.flush();
            // PrintWriter out = new PrintWriter(AliceSocket.getOutputStream(), true);
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                  String[] messageArr = message.split(" ");
                  // System.out.println("message: " + messageArr[0]);
                  outputStream.writeObject(message);
                  if (messageArr[0].equals("transfer")) {     
                    //send   
                    sendFile(messageArr[1]);
                          
                  }
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
