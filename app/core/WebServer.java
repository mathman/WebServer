import play.Logger;
import java.net.ServerSocket;
import java.io.IOException;

public class WebServer implements Runnable
{
    private Thread _t;
    ServerSocket _ss;

	public WebServer(ServerSocket s)
	{
        _ss = s;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                new WebThread(_ss.accept());
                Logger.info("New client");
            }
        }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
