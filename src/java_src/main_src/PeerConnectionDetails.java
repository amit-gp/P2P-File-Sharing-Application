package java_src.main_src;

import java.net.Socket;

/**
 * Created by Amit Kumar on 04-08-2017.
 */
public class PeerConnectionDetails
{
    private String peerName;
    private Socket socket;
    private String fileName;
    private long fileSize;

    //Temporary constructor to populate a test case (a list )
    public PeerConnectionDetails(String peerName, String fileName, Socket socket, long fileSize)
    {
        this.peerName = peerName;
        this.socket = socket;
        this.fileName = fileName;
        this.fileSize = fileSize;

    }

    public long getFileSize()
    {
        return fileSize;
    }

    public String getPeername()
    {
        return peerName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void sendAcceptSignal()
    {
        System.out.println();
        AcceptSignalSender acceptSignalSender = new AcceptSignalSender(socket);
        new Thread(acceptSignalSender).start();
    }
}
