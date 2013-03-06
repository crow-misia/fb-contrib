import java.util.ArrayList;
import java.util.List;


public class SEC_Sample 
{
	public SEC_Sample(List<SEC_Sample> l) 
	{
		l.add(this);
	}
	
	public static void main(String[] args) 
	{
		List<SEC_Sample> l = new ArrayList<SEC_Sample>();
		new SEC_Sample(l);
	}
	
	public void test() 
	{
		List<SEC_Sample> l = new ArrayList<SEC_Sample>();
		new SEC_Sample(l);
		main(new String[0]);
	}

	public void test2() 
	{
		List<SEC_Sample> l = new ArrayList<SEC_Sample>();
		new SEC_Sample(l);
		new SEC_Sample(l);
	}

	public void test3() 
	{
		List<SEC_Sample> l = new ArrayList<SEC_Sample>();
		new SEC_Sample(l);
		System.out.println(new SEC_Sample(l));
	}
}
