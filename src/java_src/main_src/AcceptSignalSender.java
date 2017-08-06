package java_src.main_src;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Amit Kumar on 05-08-2017.
 */
public class AcceptSignalSender implements Runnable
{
    private Socket socket;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;

    public AcceptSignalSender(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("InSignalSender");
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            JSONObject acceptObject = new JSONObject().put("accept", "true");
            dataOutputStream.writeUTF(acceptObject.toString());
            System.out.println("Signal Sent !!");

        }catch (Exception e){e.printStackTrace();}
    }
}
