import play.*;
import java.net.ServerSocket;
import java.io.IOException;

public class Global extends GlobalSettings
{
    @Override
    public void onStart(Application app)
    {
        ServerSocket socket;
        try
        {
            socket = new ServerSocket(20000);
            Thread t = new Thread(new WebServer(socket));
            t.start();
            Logger.info("Start server");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}