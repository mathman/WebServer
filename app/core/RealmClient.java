import play.Logger;

public class RealmClient
{
    private RealmSession _ss;
    private String _name;

	public RealmClient(RealmSession s)
	{
        _ss = s;
        _name = "";

        _ss.sendConnectOk();
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getName()
    {
        return _name;
    }
}
