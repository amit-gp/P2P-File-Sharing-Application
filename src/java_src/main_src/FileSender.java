package java_src.main_src;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Amit Kumar on 06-08-2017.
 */
public class FileSender implements Runnable
{
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private File file;
    private final CallBack callBack;

    public interface CallBack
    {
        public void fileSent();
    }

    public FileSender(Socket socket, File file, CallBack callBack)
    {
        this.socket = socket;
        this.file = file;
        this.callBack = callBack;
    }

    @Override
    public void run()
    {
        try
        {
            out = socket.getOutputStream();
            in = new FileInputStream(file);

            byte[] bytes = new byte[16 * 1024];

            int count;
            while ((count = in.read(bytes)) > 0)
            {
                out.write(bytes, 0, count);
                System.out.println("Sending " + bytes);
            }

            callBack.fileSent();

        }catch (Exception e){e.printStackTrace();}
    }
}
