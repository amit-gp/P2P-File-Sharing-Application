package java_src.main_src;

import java.io.*;
import java.net.Socket;

/**
 * Created by Amit Kumar on 06-08-2017.
 */
public class FileReceiver implements Runnable
{
    private Socket socket;
    private long fileSize;
    private String fileName;
    private File file;
    private final CallBack callBack;

    public interface CallBack
    {
        public void downloadComplete();
    }

    public FileReceiver(Socket socket, long fileSize, String fileName, CallBack callBack)
    {
        this.fileName = fileName;
        this.socket = socket;
        this.fileSize = fileSize;
        try
        {
            file = new File("C:\\P2P_FileSharing\\" + fileName);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);

        }catch (Exception e){e.printStackTrace();}

        this.callBack = callBack;
    }

    @Override
    public void run()
    {

        byte[] bytes = new byte[((int)fileSize+1) * 1024]; //buffer
        try
        {
        //InputStream inputStream = socket.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        //System.out.println("File size: " + fileSize);

        int length = 0;

        while ((length = bufferedInputStream.read(bytes)) > 0)
        {
            bufferedOutputStream.write(bytes, 0, length);
            System.out.println(bytes);
        }

        //int bytesRead = inputStream.read(bytes, 0, bytes.length);
        //bufferedOutputStream.write(bytes, 0, bytesRead);

        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        fileOutputStream.close();
        //inputStream.close();
        }catch (Exception e){e.printStackTrace();}

        callBack.downloadComplete();
    }
}
