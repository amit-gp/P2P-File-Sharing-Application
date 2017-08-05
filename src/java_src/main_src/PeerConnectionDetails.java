package java_src.main_src;

import java.net.Socket;

/**
 * Created by Amit Kumar on 04-08-2017.
 */
public class PeerConnectionDetails
{
    private String peerName;
    private Socket socket;
    private String file;

    //Temporary constructor to populate a test case (a list )
    public PeerConnectionDetails(String peerName, String file, Socket socket)
    {
        this.peerName = peerName;
        this.socket = socket;
        this.file = file;
    }

    public String getPeername()
    {
        return peerName;
    }

    public String getFile()
    {
        return file;
    }

    public Socket getSocket()
    {
        return socket;
    }
}
