import java.util.*;
import java.net.*;
import java.io.*;
public class Client1 {
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
                 String message = bufferedReader.readLine();
                 String[] messageArr = message.split(" ");
                 System.out.println("message: " + messageArr[0]);
                 if (messageArr[0].equals("get")) {
                    try{
                        //stream write the message
                        outputStream.writeObject(messageArr[0]);
                        outputStream.writeObject(messageArr[1]);
                        outputStream.flush();
                        System.out.println("Send message: " + messageArr[1]);
                        String fileYn = (String)inputStream.readObject();
                        if(fileYn.equals("Yes_File")){
                            
                            DataInputStream dataInputStream = new DataInputStream(inputStream);
                            long totalBytes = dataInputStream.readLong();
                            FileOutputStream fileOutputStream = new FileOutputStream("newDownloadTestFile.pptx.");
                            // byte[] buffer = new byte[1024];
                            // int read = 0;
                            // while((read = dataInputStream.read(buffer)) != -1){
                            //     fileOutputStream.write(buffer, 0, read);
                            // }
                            byte[] buffer = new byte[1024];
                            int read = 0;
                            long bytesReceived = 0;
                            while (bytesReceived < totalBytes) {
                                read = dataInputStream.read(buffer);
                                fileOutputStream.write(buffer, 0, read);
                                bytesReceived += read;
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
                        try (FileInputStream fileInputStream = new FileInputStream(file)) {
                            int bytesRead;
                            byte[] buffer = new byte[1024];
                            while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, bytesRead);
                                outputStream.flush();
                            }
                            System.out.println("File Sent to server");
                        } catch (Exception e) {
                            System.err.println(e);
                        }
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
        Client1 client = new Client1();
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