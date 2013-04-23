import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("all")
public class MRC_Sample
{
    private Component[] components;

	private int getValue() {
		return 1;
	}

	private String getStringValue() {
		return "Hello";
	}

	public float getFP()
	{
		return 0.0f;
	}

	private double getTwoValuesFP(boolean b)
	{
		if (b)
			return 1.0;
		else
			return 0.0;
	}

	private String getTwoDupsBySwitch(int i)
	{
		switch (i)
		{
		case 1:
			return "Hello";

		case 2:
			return "Hello";

		default:
			return "Hello";
		}
	}


	private String fpStringBuilder()
	{
		StringBuilder sb = new StringBuilder();
		fooIt(sb);

	    return sb.toString();
	}

	private void fooIt(StringBuilder sb)
	{
		sb.append("Foo");
	}

	private int getCount(List<String> l)
	{
	  	int count = 0;
	  	Iterator<String> it = l.iterator();
	  	while(it.hasNext())
	  	{
	  		if("Foo".equals(it.next()))
	  		{
	  			count += 1;
	  		}
	  	}

	  	return count;
	}

	private int fpInc()
	{
		int i = 0;
		i++;
		return i;
	}

    private long fpCountChars(Collection<String> c)
    {
        long totLength = 0;
        for (String s : c)
        {
            totLength += s.length();
        }
        return totLength;
    }

    private int getFPLoopVar(List<String> c) {
        for (int i = 0; i < c.size(); i++) {
            if (c.get(i) == null) {
                return i;
            }
        }

        throw new RuntimeException();
    }

    private int fpFindComponent(Component component) {
        int index = 0;
        while (this.components[index] != component) {
            index++;
            if (index >= this.components.length) {
                return 0;
            }
        }
        return index;
    }
    
    public int findIndex() {
        boolean matches = true;
        int[] data = new int[] { 0, 1, 2, 3, 4, 5, };
        for (int i = 0; i < data.length; i++) {
            if (i != data[i]) {
                matches = false;
            }
            if (matches) {
                return i;
            }
        }
        return -1;
    }
}
