package java_src.main_src;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Amit Kumar on 01-08-2017.
 */
public class P2P_Client
{
    private static Socket socket; //Used while sending a file to another peer and then closed.
    private static ServerSocket serverSocket; // Always listening for an incoming connection.
    private static final int PORT = 8080;
    private static ConnectionListener connectionListener;
    private static OutputStream outputStream;
    private static DataOutputStream dataOutputStream;

    public static void startListening()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
            connectionListener = new ConnectionListener(serverSocket, PORT);
            startThreads();
        }catch (Exception e){e.printStackTrace(); connectionListener = new ConnectionListener(serverSocket, PORT);
            startThreads();}
    }

    public static void sendFile(File file, String IPaddress, String peerName)
    {
        try
        {

            socket = new Socket(IPaddress, PORT);
            System.out.println("Inside");
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            JSONObject peerDetailsJsonObject = new JSONObject();
            peerDetailsJsonObject.put("peerDetails", "peerDetails").put("peerName", peerName).put("fileName", file.getAbsoluteFile());
            //First sending login info to server
            dataOutputStream.writeUTF(peerDetailsJsonObject.toString());

        }catch (Exception e){e.printStackTrace();}
    }

    private static void startThreads()
    {
        new Thread(connectionListener).start();
    }
}
