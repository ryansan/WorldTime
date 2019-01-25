import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.io.*;

public class Server extends Thread {
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000);
    }

    public void run() {
        while(true) {
            try {
                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                server.setSoTimeout(20000000);

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

                //Get from user
                String s = in.readUTF();
                System.out.println("Get from this place: " + s);

                String time = getTime(s);

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(time);

                server.close();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String [] args) {
        int port = 5555;
        try {
            Thread t = new Server(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getTime(String s){

        String s1 = "http://google.com/search?&rls=en&q=";
        String user = s + " time";

        String urlDenne = s1+user;

        Document doc = null;
        try {
            doc = Jsoup.connect(urlDenne).get();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(doc.title());

        Elements elements = doc.select("div.gsrt vk_bk dDoNo");
        System.out.println(elements.toString());

        Element contentDiv = doc.select("div[class=gsrt vk_bk dDoNo]").first();
        String text=contentDiv.getElementsByTag("div").text();
        System.out.println("t†");
        System.out.println(text);

        return text;
    }
}