import play.Logger;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebServer implements Runnable
{
    ServerSocketChannel _ss;
    private List<RealmClient> _clients;

	public WebServer()
	{
        _clients = new ArrayList<>();
    }

    public void run()
    {
        try
        {
            _ss = ServerSocketChannel.open();
            _ss.socket().bind(new InetSocketAddress(20000));
            _ss.configureBlocking(false);

            Logger.info("webserver started");
            while (true)
            {
                SocketChannel socketChannel = _ss.accept();
                if(socketChannel != null)
                {
                    socketChannel.configureBlocking(false);
                    RealmSession s = new RealmSession(socketChannel);
                    RealmClient client = new RealmClient(s);
                    s.setRealm(client);

                    if (!_clients.contains(client))
                        _clients.add(client);

                    Logger.info("new realm");
                }
            }
        }
		catch (IOException e)
		{
			e.printStackTrace();
		}
        finally
        {
            try
            {
                _ss.close();
            }
            catch (IOException e){ }
        }
	}
}
