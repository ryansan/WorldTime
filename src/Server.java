import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
               // server.setSoTimeout(20000000);

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

                //Get from user
                String s = in.readUTF();
                System.out.println("Get from this place: " + s);

                //Get time
                String time = getTime(s);

                //Send to client
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(time);
                System.out.println(time);

                //Close server
                server.close();
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
            } catch (IOException e) {
                e.printStackTrace();
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

    public static String getTime(String inputString){
        //Set to chrome to not get 403 error
        System.setProperty("http.agent", "Chrome");

        //clean up user input in case of leading whitespaces, and replace spaces
        //with underscores for URL to be valid
        inputString.trim();
        inputString = inputString.replaceAll(" ","-");

        String userURL = "http://google.com/search?&rls=en&q="+inputString+"+time";

        StringBuilder stringBuilderForHTML = new StringBuilder();

        URL url = null;
        try {
            url = new URL(userURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        BufferedReader input = null;

        try{
            input = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            while ((line=input.readLine()) != null) {
                stringBuilderForHTML.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Regex to be used to find the div box containing the time from Google search
        String regex = "<div class=\"SFt5jb uRIxYb\">";


        //Pattern object to be used with matcher to search throuhgout the entire HTML
        //to find match
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(stringBuilderForHTML.toString());


        //Check for match
        matcher.find();


        //System.out.println("Funnet " + matcher.group());
        //System.out.println("Index; " + matcher.start() + " - "+ matcher.end());

        //String to store time
        String time = "";

        //Add next 5 chars after end of match to save time in variable time
        for(int i = matcher.end(); i < matcher.end()+5;i++){
            time += stringBuilderForHTML.charAt(i);
        }

        return time;
    }

    public static String getTimeJSoup(String s){

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


        Element contentDiv = doc.select("div[class=gsrt vk_bk dDoNo]").first();

        if(contentDiv == null){
            System.out.println("funka ikke");
            return "fant ikke";
        }

        String text = contentDiv.getElementsByTag("div").text();
        System.out.println("t†");
        System.out.println(text);



        return text;
    }
}