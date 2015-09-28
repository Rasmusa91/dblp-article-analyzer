import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigVars 
{
	public static String REMOTE_DBLP_FILE = "http://dblp.uni-trier.de/xml/dblp.xml.gz";
	public static String LOCAL_TEMP_DIR = "temp";
	public static String LOCAL_DBLP_FILE_GZ = "temp/dblp.xml.gz";
	public static String LOCAL_DBLP_FILE = "temp/dblp.xml";

	public static boolean TRUNCATE_TEMP_BEGINNING = true;
	public static boolean TRUNCATE_TEMP_END = false;

	public static boolean PRINT_PROGRESS = true;
	public static boolean UPDATE_EXISTING_ARTICLES = false;
	public static String DATE_FORMAT = "dd-MM-yyyy";
	
	public static String 	DATABASE_HOST = null;
	public static int 		DATABASE_PORT = -1;
	public static String 	DATABASE_NAME = "IndivProj";
	public static String	DATABASE_ARTICLE_COLLECTION = "Articles";

	public static String[] JOURNALS = {
			"IEEE Trans. Software Eng.",												// TSE
			"Commun. ACM",																// CACM
			"IEEE Computer",															// COMPUTER, not Comp
			"IEEE Software",															// SOFTWARE, not SW
			"ACM Trans. Softw. Eng. Methodol.",											// TOSEM
			"Information", //(?)														// not "Information & Software Technology", INFSOF, not IST
			"Autom. Softw. Eng.",														// ASE
			"Journal of Software: Evolution and Process", // (?)						// not SW Maintenance & Evolution - Research & Practice, SMR, not JSEP / SMRP
			"Software and System Modeling",	 											// SOSYM
			"Empirical Software Engineering",											// ESE, not ESEJ
			"Journal of Systems and Software",											// JSS
			"Requir. Eng.",																// RE, not REJ
			"Softw. Test., Verif. Reliab.",												// STVR
			"Softw., Pract. Exper.",													// SPE
			"Software Quality Journal",													// SQJ
			"IBM Journal of Research and Development",									// IBMRD, not IBM JRD
			"IET Software",																// IEE, not IET SW
			"International Journal of Software Engineering and Knowledge Engineering" 	// IJSEKE
		};		
	
	public static void Load ()
	{
		File f = new File("config.properties");
		
		if(f.exists()) {
			ReadPropeties(f);
		}		
		else {
			WritePropeties(f);				
		}
	}
	
	private static void ReadPropeties(File p_File) 
	{
		Properties p = new Properties();
		FileInputStream fis;		
	}

	private static void WritePropeties(File p_File) 
	{
		Properties p = new Properties();
		FileOutputStream fos;		
	}
}
