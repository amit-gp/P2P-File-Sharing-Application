package java_src.main_src;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Amit Kumar on 02-08-2017.
 */
public class ConnectionListener implements Runnable
{
    private static ServerSocket serverSocket;
    private static int PORT;
    private InputStream inputStream;
    private DataInputStream dataInputStream;

    public ConnectionListener(ServerSocket socket, int givenPORT)
    {
       serverSocket = socket;
       PORT = givenPORT;
    }

    public void run()
    {
        while (true)
        {
            System.out.println("Listening at PORT: " + PORT);
            try
            {
                Socket peerSocket = serverSocket.accept();
                inputStream = peerSocket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                JSONObject peerDetailsJson = new JSONObject(dataInputStream.readUTF());
                //Updating GUI
                //System.out.println(peerDetailsJson.getString("peerDetails"));
                P2P_Client.updateObservalbeList(new PeerConnectionDetails(peerDetailsJson.getString("peerName"), peerDetailsJson.getString("fileName"), peerSocket));
                System.out.println("Connection to a peer from: " + peerSocket.getInetAddress());
                //Sending a push notification to the user.
                Notifications notificationBuilder = Notifications.create().title("A peer want's to Send a File !").graphic(null).hideAfter(Duration.seconds(10)).position(Pos.BOTTOM_RIGHT).onAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        Main.requestApplicationFocus();
                    }
                });

                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        notificationBuilder.showInformation();
                    }
                });

            }catch (Exception e){e.printStackTrace();}
        }
    }
}
