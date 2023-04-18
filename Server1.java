import java.util.*;
import java.net.*;
import java.io.*;
public class Server1 {
    int PORT = 5106;
    ServerSocket sSocket;
	Socket connection = null;
    public void serverConnect(){
        try {
            //create a serversocket
			sSocket = new ServerSocket(PORT);
			//Wait for connection
			System.out.println("Waiting for connection");
			//accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());         //stream write to the socket
 	        ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());

             try{
				while(true)
				{
                    String functionality = (String)inputStream.readObject();
                    if(functionality.equals("get")){
                        String fileName = (String)inputStream.readObject();
                        System.out.println("Received fileName: " + fileName);
                        File file = new File(fileName);

                        // if (file.exists() && !file.isDirectory()) {
                        //     outputStream.writeObject("Yes_File");
                        //     try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        //         int bytesRead;
                        //         byte[] buffer = new byte[1024];
                        //         while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                        //             outputStream.write(buffer, 0, bytesRead);
                        //             outputStream.flush();
                        //         }
                        //         System.out.println("File Sent to client");
                        //     } catch (Exception e) {
                        //         System.err.println(e);
                        //     }
                        // }
                        // if (file.exists() && !file.isDirectory()) {
                        //     outputStream.writeObject("Yes_File");
                        //     try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        //         int bytesRead;
                        //         byte[] buffer = new byte[1024];
                        //         long totalSize = file.length();
                        //         outputStream.writeLong(totalSize);
                        //         outputStream.flush();
                        //         while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                        //             outputStream.write(buffer, 0, bytesRead);
                        //             outputStream.flush();
                        //         }
                        //         System.out.println("File Sent to client");
                        //     } catch (Exception e) {
                        //         System.err.println(e);
                        //     }
                        // }
                        if (file.exists() && !file.isDirectory()) {
                            outputStream.writeObject("Yes_File");
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                int bytesRead;
                                byte[] buffer = new byte[1024];
                                long totalSize = file.length();
                                outputStream.writeLong(totalSize);
                                outputStream.flush();
                                long bytesSent = 0;
                                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, bytesRead);
                                    outputStream.flush();
                                    bytesSent += bytesRead;
                                    if (bytesSent == totalSize) {
                                        break;
                                    }
                                }
                                System.out.println("File Sent to client");
                            }catch (EOFException e) {
                                System.err.println("Connection closed by client after file transfer was complete" + e.getMessage()); }
                            // catch (Exception e) {
                            //     System.err.println(e);
                            // }
                        }                        
                        else {
                            outputStream.writeObject("No_File");
                            System.err.println("File does not exist");
                        }
                    }else {
                        String fileYn = (String)inputStream.readObject();
                        if(fileYn.equals("Yes_File")){
                            System.out.println("upload fileName: " + functionality);
                            DataInputStream dataInputStream = new DataInputStream(inputStream);
                            FileOutputStream fileOutputStream = new FileOutputStream("newUploadTestFile.pptx.");
                            byte[] buffer = new byte[1024];
                            int read = 0;
                            while((read = dataInputStream.read(buffer)) != -1){
                                fileOutputStream.write(buffer, 0, read);
                            }
                            try {
                                fileOutputStream.close();
                            }catch(IOException e) {
                                System.err.println("An error occurred while closing the FileOutputStream: " + e.getMessage());
                            }
                            try {
                                dataInputStream.close();
                            } catch (IOException e) {
                                System.err.println("An error occurred while closing the DataInputStream: " + e.getMessage());
                            }
                            System.out.println("File downloaded");
                        }else{
                            System.out.println("No File to Upload");
                        }
                    }
				}
			}catch (EOFException e) {
                System.err.println("Connection closed after file transfer was complete" + e.getMessage()); 
            }
			catch(Exception e){
                System.err.println("Catch nested..");
				System.err.println(e);
				}
        } catch (Exception e) {
            System.err.println(e);
    }
}
public static void main(String[] args){
    Server1 server = new Server1();
    server.serverConnect();
}
}