import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities 
{
	public static <T> boolean ArrayContains (T[] p_Array, T p_Value)
	{
		boolean contains = false;
		
		for(int i = 0; i < p_Array.length && !contains; i++) 
		{
			if(p_Array[i].equals(p_Value)) {
				contains = true;
			}
		}
		
		return contains;
	}
	
	public static int StringToInt(String p_String)
	{
		int n = -1;
		
		try {
			n = Integer.valueOf(p_String);
		}
		catch (Exception e) {
			System.out.println("Warning: could not convert " + p_String + " to an integer, defaulting to -1");
		}
		
		return n;
	}
	
	public static String TimeToString (long p_MS)
	{
		int s = (int) ((p_MS / 1000) % 60);
		int m = (int) ((p_MS / (1000 * 60)) % 60);
		int h = (int) ((p_MS / (1000 * 60 * 60)));
		
		String time = (s < 10 && m > 0 ? "0" : "") + s + "s";
		
		if(m > 0) {
			time = (m < 10 && h > 0 ? "0" : "") + m + "m, " + time;
		}
		if (h > 0) {
			time = h + "h, " + time;
		}
		
		return time;
	}
	
	public static Date StringToDate(String p_Date)
	{
		DateFormat f = new SimpleDateFormat(ConfigVars.DATE_FORMAT);
		Date d = null;
		
		try {
			d = f.parse(p_Date);
		}
		catch(Exception e) 
		{
			System.out.println(e);
		}
		
		return d;
	}
}
