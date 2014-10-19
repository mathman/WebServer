import play.Logger;

import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Queue;

public class RealmSession implements Runnable
{
	private Thread _t;
	private SocketChannel _s;
    private volatile Queue<WorldPacket> _writeQueue;
    private RealmClient _client;

	RealmSession(SocketChannel s)
    {
        _s = s;
        _writeQueue = new LinkedList<WorldPacket>();
        _t = new Thread(this);
        _t.start();
    }

    public void sendPacket(WebPacket packet)
    {
        _writeQueue.add((WorldPacket)packet);
    }

    public void setRealm(RealmClient client) { _client = client; }

    public final void close()
    {
        Logger.info("close");
        try {
            _s.close();
            _t.interrupt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void run()
    {
        try
        {
            internalRun();
        }
        catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }

    private void internalRun() throws Exception
    {
        try
        {
            while (!_t.isInterrupted())
            {
                PushPacket();

                ByteBuffer buffer = ByteBuffer.allocate(4096);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                int packetSize = 0;
                int readSize = 0;
                while ((readSize = _s.read(buffer)) > 0) // Returns the number of bytes read
                    packetSize += readSize;

                // End of stream
                if (readSize == -1)
                    break;

                // Nothing read yet (readSize is always == 0 here)
                if (packetSize == 0)
                    continue;

                buffer.flip(); // Make ready for reading
                WorldPacket packet = new WorldPacket(buffer);

                ReadHandler(packet);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            close();
        }
	}

    private void PushPacket() throws IOException
    {
        if (_writeQueue.isEmpty())
            return;

        WorldPacket pkt = _writeQueue.poll();
        if (pkt == null || pkt.GetSize() == 0)
            return;

        _s.write(pkt.ToByteBuffer());
    }

    private void ReadHandler(WorldPacket packet)
    {
        Logger.info("packet receive");

        int opcode = packet.GetOpcode();
        switch (Opcodes.values()[opcode])
        {
            case CMSG_WEB_NAME_RESPONSE:
                HandleWebNameResponse(packet);
                break;
        }
    }

    public void HandleWebNameResponse(WorldPacket packet)
    {
        String name = packet.ReadString();
        _client.setName(name);
    }

    public void sendConnectOk()
    {
        WorldPacket buffer = new WorldPacket(Opcodes.SMSG_WEB_NAME_REQUEST, 0);
        sendPacket(buffer);
    }
}
