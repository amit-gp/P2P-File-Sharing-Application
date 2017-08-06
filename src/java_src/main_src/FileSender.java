package java_src.main_src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
            FileInputStream fileInputStream = new FileInputStream(file);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());

            int length = 0;
            while ((length = bufferedInputStream.read(bytes)) > 0)
            {
                bufferedOutputStream.write(bytes, 0, length);
                System.out.println(bytes);
            }

            //bufferedInputStream.read(bytes, 0, bytes.length);
            //OutputStream outputStream = socket.getOutputStream();

            //outputStream.write(bytes, 0, bytes.length);
            bufferedOutputStream.flush();
            fileInputStream.close();
            bufferedInputStream.close();
            //outputStream.close();
            callBack.fileSent();

        }catch (Exception e){e.printStackTrace();}
    }
}
