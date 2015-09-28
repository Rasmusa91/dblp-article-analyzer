import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DBLPParser 
{
	public static void ReadDBLPFile (String p_File, boolean p_PrintProgress)
	{
		SAXParserFactory factory;
		SAXParser parser;
		LineNumberReader lineReader;
		SaxArticleHandler handler;
		
        try 
        {
        	if(p_PrintProgress) 
        	{
        		System.out.print("Preparing...\r");
        		
            	lineReader = new LineNumberReader(new FileReader(new File(p_File)));
            	lineReader.skip(Long.MAX_VALUE);
        		
            	handler = new SaxArticleHandler(lineReader.getLineNumber());
            	
            	lineReader.close();
        	}
        	else {
        		handler = new SaxArticleHandler();        		
        	}
        	        	
        	factory = SAXParserFactory.newInstance();
    		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    		
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
