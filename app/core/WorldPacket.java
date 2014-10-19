import play.Logger;

import org.apache.http.util.EncodingUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;

public class WorldPacket implements WebPacket {
    private ByteBuffer data;

    public int GetOpcode() { return data.getInt(0); }
    public int GetDataSize() { return data.getInt(4); }
    public int GetSize() { return data.limit(); }

    /*
     * Constructs a new WorldPacket.
     */
    public WorldPacket(Opcodes opcode, int size)
    {
        data = ByteBuffer.allocate(size + 4 + 4);
        data.order(ByteOrder.LITTLE_ENDIAN);
        WriteInt(opcode.ordinal());
        WriteInt(size);
    }

    public WorldPacket(ByteBuffer buffer)
    {
        data = buffer.duplicate();
        data.order(ByteOrder.LITTLE_ENDIAN);
        data.getInt();
        data.getInt();
    }

    public WorldPacket(byte[] bdata)
    {
        data = ByteBuffer.wrap(bdata);
        data.order(ByteOrder.LITTLE_ENDIAN);
        data.getInt();
        data.getInt();
    }

    public void WriteInt(int number)   { data.putInt(number); }
    public void WriteShort(short number) { data.putShort(number); }
    public void WriteFloat(float number)  { data.putFloat(number);}
    public void WriteByte(byte number)    { data.put(number); }
    public void WriteBytes(byte[] ip)     { data.put(ip); }

    public int GetWritePos() { return data.position(); }
    public void WriteByte(int writePos, byte value)
    {
        if (writePos <= data.capacity())
            data.put(writePos, value);
    }

    public void WriteString(String value)
    {
        try {
            data.put(EncodingUtils.getAsciiBytes(value));
            data.put((byte)0);
        }
        catch (BufferOverflowException e) {
            e.printStackTrace();
        }
    }

    public byte ReadByte()
    {
        byte retVal = data.get();
        return retVal;
    }

    public byte ReadByte(int offset)
    {
        return data.get(offset);
    }

    public byte[] ReadBytes(int count)
    {
        byte[] retVal = new byte[count];
        data.get(retVal);
        return retVal;
    }

    public short ReadShort()
    {
        return data.getShort();
    }

    public int ReadInt()
    {
        return data.getInt();
    }

    public float ReadFloat()
    {
        return data.getFloat();
    }

    public String ReadString()
    {
        int position = data.position();

        while (data.get() != 0);

        byte[] stringBytes = new byte[data.position() - position - 1];
        data.position(position); // reset to position
        data.get(stringBytes); // read correct amount of bytes
        data.get(); // Skip terminary 0
        return EncodingUtils.getAsciiString(stringBytes, 0, stringBytes.length);
    }

    /**
     * NOTE: This function can only be used if you are DONE with writing your packet!
     */
    public ByteBuffer ToByteBuffer()
    {
        ByteBuffer copy = data.duplicate();
        copy.order(ByteOrder.LITTLE_ENDIAN);
        copy.flip();
        return copy;
    }
}