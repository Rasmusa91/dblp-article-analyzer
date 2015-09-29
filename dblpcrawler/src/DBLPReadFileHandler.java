import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * This class is responsible for reading the dblp xml file
 */
public class DBLPReadFileHandler 
{
	/**
	 * Read the file
	 * 
	 * @param p_File The location and name of the file
	 * @param p_PrintProgress If debugging is enabled
	 */
	public static void ReadDBLPFile (String p_File, boolean p_PrintProgress)
	{
		SAXParserFactory factory;
		SAXParser parser;
		LineNumberReader lineReader;
		DBLPParseFileHandler handler;
		
        try 
        {
        	// Warning: Slow
        	// If debugging is enabled, find the total number of lines in the file
        	// Open the file, skip to the end and check which line the reader is at
        	if(p_PrintProgress) 
        	{
        		System.out.print("Preparing...\r");
        		
            	lineReader = new LineNumberReader(new FileReader(new File(p_File)));
            	lineReader.skip(Long.MAX_VALUE);
        		
            	handler = new DBLPParseFileHandler(lineReader.getLineNumber());
            	
            	lineReader.close();
        	}
        	else {
        		handler = new DBLPParseFileHandler();        		
        	}
        	        	
        	// Using sax to parse the xml
        	factory = SAXParserFactory.newInstance();
        	
        	// Make sure to not validate the file
    		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    		
    		// Parse the file the custom handler (DBLPParseFileHandler)
            parser = factory.newSAXParser();
            parser.parse(p_File, handler);    
            
            if(p_PrintProgress) {
            	handler.PrintResult();
            }
            
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
}
