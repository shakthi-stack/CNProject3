import java.util.*;
import java.net.*;
import java.io.*;
public class Client2 {
    Socket requestSocket;
    public void clientConnect(int PORT){
        try {
            requestSocket = new Socket("localhost", PORT);
            System.out.println("Connected to localhost in port "+PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(requestSocket.getOutputStream());         //stream write to the socket
 	        ObjectInputStream inputStream = new ObjectInputStream(requestSocket.getInputStream());

             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
             while(true)
             {
                System.out.println("Here client 1");
                 String message = bufferedReader.readLine();
                 String[] messageArr = message.split(" ");
                 System.out.println("message: " + messageArr[0]);
                 if (messageArr[0].equals("get")) {
                    try{
                        //stream write the message
                        System.out.println("Here client 2");
                        outputStream.writeObject(messageArr[0]);
                        outputStream.writeObject(messageArr[1]);
                        outputStream.flush();
                        System.out.println("Send message: " + messageArr[1]);
                        String fileYn = (String)inputStream.readObject();
                        if(fileYn.equals("Yes_File")){
                            // System.out.println("upload fileName: " + functionality);
                
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String[] flname = messageArr[1].split("\\.(?=[^\\.]+$)");
                FileOutputStream fileOutputStream = new FileOutputStream("new"+flname[0]+".pptx.");
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
                // try {
                //   System.out.println("closing datainputstream...");
                //   dataInputStream.close();
                // } catch (IOException e) {
                //   System.err.println("An error occurred while closing the DataInputStream: " + e.getMessage());
                // }
                System.out.println("File downloaded");
                            // System.out.println("Here client 3");
                            // DataInputStream dataInputStream = new DataInputStream(inputStream);
                            // long totalBytes = dataInputStream.readLong();
                            // FileOutputStream fileOutputStream = new FileOutputStream("new"+messageArr[1]);
    
                            // byte[] buffer = new byte[1024];
                            // int read = 0;
                            // long bytesReceived = 0;
                            // while (bytesReceived < totalBytes) {
                            //     read = dataInputStream.read(buffer);
                            //     fileOutputStream.write(buffer, 0, read);
                            //     bytesReceived += read;
                            // }
                            // try {
                            //     fileOutputStream.close();
                            // }catch(IOException e) {
                            //     System.err.println("An error occurred while closing the FileOutputStream: " + e.getMessage());
                            // }
                            // try {
                            //     dataInputStream.close();
                            // } catch (IOException e) {
                            //     System.err.println("An error occurred while closing the DataInputStream: " + e.getMessage());
                            // }
                            // System.out.println("File downloaded");
                        }else{
                            System.out.println("File not found!");
                        }
                    }catch (EOFException e) {
                        System.err.println("Connection closed after file transfer was complete" + e.getMessage()); }
                    catch(Exception e){
                        System.err.println(e);
                    }
                 } else if(messageArr[0].equals("upload")) {
                    System.out.println("upload "+messageArr[1]);
                    outputStream.writeObject(messageArr[0]);
                    File file = new File(messageArr[1]);

                    if (file.exists() && !file.isDirectory()) {
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
                            System.out.println("File Sent to server");
                            // outputStream.write("EOF".getBytes());
                            // outputStream.flush();
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                        // outputStream.writeObject("Yes_File");
                        // try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        //     int bytesRead;
                        //     byte[] buffer = new byte[1024];
                        //     while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                        //         outputStream.write(buffer, 0, bytesRead);
                        //         outputStream.flush();
                        //         outputStream.flush();
                        //     }
                        //     System.out.println("File Sent to server");
                        // } catch (Exception e) {
                        //     System.err.println(e);
                        // }
                    } else {
                        outputStream.writeObject("No_File");
                        System.err.println("File does not exist");
                    }
                 } else{
                    System.out.println("Cannot process command. Only upload or get");
                 }
             }
        } catch (ConnectException e) {
            System.out.println("Connection refused: connect to an available server");
        } catch (EOFException e) {
            System.err.println("Connection closed after file transfer was complete" + e.getMessage()); 
            try{
				requestSocket.close();
			}
			catch(Exception e_){
				System.err.println(e_);
			}
        
        }
            catch (Exception e) {
            System.err.println(e);
        }
        finally{
			//Close connections
			try{
				if (requestSocket != null) {
					requestSocket.close();
				}
			}
			catch(Exception e){
				System.err.println(e);
			}
}
    }
    public static void main(String[] args){
        Client2 client = new Client2();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String message;
        try {
            message = bufferedReader.readLine();
            String[] messageArr = message.split(" ");
            if (messageArr[0].equals("ftpclient")){
                client.clientConnect(Integer.parseInt(messageArr[1]));
            }else{
                System.out.println(messageArr[0]+" operation not possible");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}