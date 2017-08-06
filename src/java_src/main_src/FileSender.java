package java_src.main_src;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Amit Kumar on 06-08-2017.
 */
public class FileSender implements Runnable
{
    private Socket socket;
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
            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedInputStream.read(bytes, 0, bytes.length);
            OutputStream outputStream = socket.getOutputStream();
            System.out.println(bytes);
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            callBack.fileSent();

        }catch (Exception e){e.printStackTrace();}
    }
}
