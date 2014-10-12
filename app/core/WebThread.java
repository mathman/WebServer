import java.net.Socket;

import play.Logger;

public class WebThread implements Runnable
{
	private Thread _t;
	private Socket _s;

	WebThread(Socket s)
    {
        _s = s;

        _t = new Thread(this);
        _t.start();
    }

	public void run()
    {
        while (true) { }
	}

}
