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

    @Override
    public void run()
    {
        try
        {
            byte[] bytes = new byte[(int) file.length()];
            int bufferSize = bytes.length;
            FileInputStream fileInputStream = new FileInputStream(file);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());

            int length = 0;
            int fileRead = 0;

            while ((length = bufferedInputStream.read(bytes)) > 0)
            {
                bufferedOutputStream.write(bytes, 0, length);
                fileRead += bufferSize/1000;
                System.out.println(bufferSize + "   " + file.length());

                if(bufferSize >= file.length())
                {
                    System.out.println("File Completele sent");
                    callBack.fileSent();
                }

                if(fileRead >= file.length() * 2)
                {
                    //System.out.println("File Completele sent");
                    callBack.fileSent();
                    System.out.println("Closing the streams....");
                    bufferedOutputStream.flush();
                    fileInputStream.close();
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                }
                //System.out.println(bytes);
            }

            //bufferedInputStream.read(bytes, 0, bytes.length);
            //OutputStream outputStream = socket.getOutputStream();

            //outputStream.write(bytes, 0, bytes.length);
            bufferedOutputStream.flush();
            fileInputStream.close();
            bufferedInputStream.close();
            //outputStream.close();


        }catch (Exception e){e.printStackTrace();}
    }

    public FileSender(Socket socket, File file, CallBack callBack)
    {
        this.socket = socket;
        this.file = file;
        this.callBack = callBack;
    }

    public interface CallBack
    {
        void fileSent();
    }
}
