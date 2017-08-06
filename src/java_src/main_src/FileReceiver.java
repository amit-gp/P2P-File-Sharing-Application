package java_src.main_src;

import java.io.*;
import java.net.Socket;

/**
 * Created by Amit Kumar on 06-08-2017.
 */
public class FileReceiver implements Runnable
{
    private final CallBack callBack;
    private Socket socket;
    private long fileSize;
    private String fileName;
    private File file;
    private int bufferSize;

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
        bufferSize = bytes.length;
        try
        {
        //InputStream inputStream = socket.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        //System.out.println("File size: " + fileSize);

        int length = 0;
        int fileRead = 0;
        while ((length = bufferedInputStream.read(bytes)) > 0)
        {
            try
            {
                bufferedOutputStream.write(bytes, 0, length);
            }catch (IOException e)
            {
                System.out.println("Stream finished.");
                callBack.downloadComplete();
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                fileOutputStream.close();
            }
            //System.out.println(bytes);
            fileRead += bufferSize/1000;
            System.out.println(fileRead);

            if(bufferSize > fileSize)
            {
                callBack.downloadComplete();
            }

            if(fileRead >= fileSize * 2)
            {
                callBack.downloadComplete();
            }

        }
        System.out.println("Outside");
        //int bytesRead = inputStream.read(bytes, 0, bytes.length);
        //bufferedOutputStream.write(bytes, 0, bytesRead);
        //System.out.println("Download complete");
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        fileOutputStream.close();
        //inputStream.close();
        //System.out.println("Download complete");
        }catch (Exception e){e.printStackTrace();}

        //System.out.println("Download complete");
    }

    public interface CallBack
    {
        void downloadComplete();
    }
}
