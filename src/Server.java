import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.io.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private final ExecutorService pool;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000000);
        pool = Executors.newFixedThreadPool(20);
    }

    class Handler implements Runnable {
        private final Socket socket;
        Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            // read and service request on socket
            try {
                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");


                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(socket.getInputStream());

                //Get from user
                String s = in.readUTF();
                System.out.println("Get from this place: " + s);

                if(s.equals("oslo")){
                    Thread.sleep(5000);
                }

                //Get time
                String time = getTimeJSoup(s);



                //Send to client
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(time);
                System.out.println(time);

                //Close server
                socket.close();
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        while(true) {
            try {
                pool.execute(new Handler(serverSocket.accept()));
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
                System.out.println(line);
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
        try {
            for (int i = matcher.end()+38; i < matcher.end()+49; i++) {
                if (stringBuilderForHTML.charAt(i) == ',') {
                    break;
                } else {
                    time += stringBuilderForHTML.charAt(i);
                }
            }

            time += ", ";


            for (int i = matcher.end()+79; i < matcher.end()+96; i++) {
                if (stringBuilderForHTML.charAt(i) == '<') {
                    break;
                } else {
                    time += stringBuilderForHTML.charAt(i);
                }
            }

            time += " ";

            for(int i = matcher.end(); i < matcher.end()+5;i++){
                time += stringBuilderForHTML.charAt(i);
            }
        } catch (IllegalStateException e){
            return "Couldn't find time for " + inputString;
        }

        return time;
    }

    public static String getTimeJSoup(String s){

        String userURL = "http://google.com/search?&rls=en&q="+s+"+time";


        Document doc = null;
        try {
            doc = Jsoup.connect(userURL).get();

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
        //System.out.println(text);

        Element contentDiv2 = doc.select("div[class=vk_gy vk_sh]").first();

        String text2 = contentDiv2.getElementsByTag("div").text();

        System.out.println(text + " " + text2 + " ");


        return text;
    }
}

