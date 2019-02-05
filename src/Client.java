import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args){
        String serverName = "10.253.15.229";
        int port = 5555;
        try{
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            System.out.println("Type in city or country: ");
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();

            //send to server
            out.writeUTF(s);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            //Get from server
            System.out.println("Server says " + in.readUTF());


            //Close port
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
