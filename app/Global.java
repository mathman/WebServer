import play.*;

public class Global extends GlobalSettings
{
    @Override
    public void onStart(Application app)
    {
        try
        {
            Thread t = new Thread(new WebServer());
            t.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}