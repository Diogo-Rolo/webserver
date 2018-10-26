import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private final String ROOT = "www";

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    public static void main(String[] args) {
        WebServer webServer = new WebServer();
        webServer.start();
    }

    private void start() {
        BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("In which port do you wish to run this server?");
            serverSocket = new ServerSocket(Integer.parseInt(fromConsole.readLine()));

            while (true) {

                acceptConnection(serverSocket);

                String fromClient = in.readLine();

                if (fromClient == null || fromClient.isEmpty()) {
                    continue;
                }

                File resource = new File(ROOT + processRequest(fromClient));

                if (!resource.exists()) {
                    resource = new File(ROOT + "/404.html");
                }

                out.writeBytes(generateHeader(resource));

                out.write(readFromFile(resource));

                socket.close();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void acceptConnection(ServerSocket serverSocket) throws IOException {
        socket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
    }

    private byte[] readFromFile(File resource) {
        byte[] readFile = new byte[(int) resource.length()];
        try {
            FileInputStream fromFile = new FileInputStream(resource);
            fromFile.read(readFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readFile;
    }

    private String processRequest(String fromClient) throws IOException {


        String request = fromClient.split(" ")[1];
        System.out.println("request: " + request);

        if (request.equals("/")) {
            System.out.println("this did happen");
            request = "/idx.html";
        }
        return request;
    }

    private String generateHeader(File resource) {

        int statusCode = getStatusCode(resource); //for now, needs changing if I want to deal with other statuses
        String contentType = getMimeType(resource.getAbsolutePath());
        long fileLength = resource.length();


        String header = "HTTP/1.0 " + statusCode + " Document Follows\r\n" +
                "Content-Type: " + contentType + "; charset=UTF-8\r\n" +
                "Content-Length: " + fileLength + " \r\n" +
                "\r\n";

        return header;
    }

    private int getStatusCode(File resource) {
        int statusCode;
        if(resource.getName().equals("404.html")){
            statusCode = 404;
        } else {
            statusCode = 200;
        }
        return statusCode;

    }

    private String getMimeType(String extension) {
        String mimeType = "";

        int lastDot = extension.lastIndexOf('.');
        String ext = extension.substring(lastDot + 1);
        System.out.println(ext);

        switch (ext) {
            case "html":
                mimeType = "text/html";
                break;
            case "jpeg":
                mimeType = "image/jpeg";
                break;
        }
        return mimeType;
    }
}
