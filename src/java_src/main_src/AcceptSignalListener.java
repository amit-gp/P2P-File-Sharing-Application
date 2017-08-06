package java_src.main_src;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Amit Kumar on 05-08-2017.
 */
public class AcceptSignalListener implements Runnable
{
    Socket socket;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private final CallBack callBack;

    public interface CallBack
    {
        public void requestAccepted();
    }

    public AcceptSignalListener(Socket socket, CallBack callBack)
    {
        this.callBack = callBack;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                System.out.println("InSignalreciver before accept");
                //System.out.println(dataInputStream.readUTF());
                JSONObject acceptObject = new JSONObject(dataInputStream.readUTF());
                System.out.println("InSignalreciver after accept");
                //if (acceptObject.getString("accept").equals("true"))
                {
                    System.out.println("User Accepted !!!");
                    callBack.requestAccepted();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
