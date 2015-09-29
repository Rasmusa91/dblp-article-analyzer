import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is responsible for parsing the DBLP XML file with SAX
 */
public class DBLPParseFileHandler extends DefaultHandler 
{
	private Article m_CurrentArticle;
	private Map<String, Boolean> m_FetchingAdditionalAttribute;
	
	/* Debugging */
	private boolean m_PrintProgress;
	private Locator m_Locator;
	private long m_TotalLineNumbers;
	private int m_CurrProgPerc;
	private int m_ArticlesFound;
	private int m_ArticlesAdded;
	private int m_ArticlesUpdated;
	private Map<String, Integer> m_ArticlesPerJournalAdded;
	private Map<String, Integer> m_ArticlesPerJournalUpdated;

	/**
	 * Default constructor
	 */
	public DBLPParseFileHandler()
	{
		m_CurrentArticle = null;
		m_FetchingAdditionalAttribute = new HashMap<String, Boolean>();		
		
		m_PrintProgress = false;
	}
	
	/**
	 * Constructor to initialize debugging
	 * 
	 * @param p_TotalLineNumbers The number of lines in the file to be read
	 */
	public DBLPParseFileHandler(long p_TotalLineNumbers) 
	{
		this();
		
		/* Init progress vars */
		m_PrintProgress = true;
		m_TotalLineNumbers = p_TotalLineNumbers;
		m_CurrProgPerc = -1;
		m_ArticlesFound = 0;
		m_ArticlesAdded = 0;
		m_ArticlesPerJournalAdded = new HashMap<String, Integer> ();
		m_ArticlesPerJournalUpdated = new HashMap<String, Integer> ();
	}
	
	/**
	 * Handle the start element of a XML block
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		// Check if the start element is an article
		if(qName.equals("article")) 
		{
			m_CurrentArticle = new Article ();
			
			// Handle all attributs in the start tag
			for(int i = 0; i < attributes.getLength(); i++) {
				HandleAttribute(attributes.getQName(i), attributes.getValue(i));				
			}
		}
		// If an article is already being parsed, we're inside it and want to extract these attributes to the article
		// To get the value within tags like <a>b</a>, b in this case, which is afterwards being processed by the characters method.
		// But to remember the tag name, its saved
		else if (m_CurrentArticle != null) {
			m_FetchingAdditionalAttribute.put(qName, true);
		}
		
		if(m_PrintProgress) {
			PrintProgress ();
		}
	}
	
	/**
	 * When we reach the end of an article block, its ready to be processed
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if(qName.equals("article")) 
		{
			if(m_CurrentArticle != null)
			{
				// Process the article and get the result flags
				// The flags are only relevant here for printing progress
				int flags = ProcessArticleHandler.ProcessArticle(m_CurrentArticle, ConfigVars.UPDATE_EXISTING_ARTICLES);		
				
				if(m_PrintProgress) {
					HandleProccessedArticleOutput(flags);
				}
				 
				m_CurrentArticle = null;
			}
			else {
				System.out.println("No article instantiated");
			}
		}

		if(m_PrintProgress) {
			PrintProgress ();
		}
	}
	
	/**
	 * Get the value inside an tag <a>b</a>, b will be extracted here
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException 
	{		
		// Iterate all names that's wanted for extraction (found in start element)
		for (Map.Entry<String, Boolean> entry : m_FetchingAdditionalAttribute.entrySet()) 
		{
			// Get all tags names witch is currently being parsed
			if(entry.getValue()) 
			{
				// Save the tag value with the tag name
				HandleAttribute(entry.getKey(), new String (ch, start, length));
				
				// Result is now found for this tag name and is currenty disabled again
				entry.setValue(false);
			}
		}
	}
	
	/**
	 * Get the document locator to keep track of the position in the document, for debugging
	 */
	@Override
	public void setDocumentLocator(Locator locator)
	{
		m_Locator = locator;
	}
	
	/**
	 * Save the value from a xml attribute to the article object, the name is matched with a method in the article
	 * 
	 * @param p_Name
	 * @param p_Value
	 */
	private void HandleAttribute(String p_Name, String p_Value)
	{
		if(m_CurrentArticle != null)
		{
			switch(p_Name)
			{
			case "mdate":
				m_CurrentArticle.SetLastModified(p_Value); 
				break;
			case "key":
				m_CurrentArticle.SetDBLPKey(p_Value); 
				break;
			case "author":
				m_CurrentArticle.AddAuthor(p_Value); 
				break;
			case "title":
				m_CurrentArticle.SetTitle(p_Value); 
				break;
			case "pages":
				m_CurrentArticle.SetPages(p_Value); 
				break;
			case "year":
				m_CurrentArticle.SetYear(Utilities.StringToInt(p_Value)); 
				break;
			case "volume":
				m_CurrentArticle.SetVolume(p_Value); 
				break;
			case "journal":
				m_CurrentArticle.SetJournal(p_Value); 
				break;
			case "number":
				m_CurrentArticle.SetNumber(p_Value); 
				break;
			case "ee":
				m_CurrentArticle.SetElectronicEdition(p_Value); 
				break;
			case "url":
				m_CurrentArticle.SetLocalURL(p_Value); 
				break;
			}
		}
		else {
			System.out.println("No article instantiated");
		}
	}
	
	/**
	 * Store the result of a processed article.  
	 * 
	 * @param p_Flags The result flags of the process
	 */
	private void HandleProccessedArticleOutput (int p_Flags)
	{
		// Count articles matched with the wanted journals
		if((p_Flags & ProcessArticleHandler.FLAG_IN_JOURNAL) == ProcessArticleHandler.FLAG_IN_JOURNAL) {
			m_ArticlesFound++;
		}
		
		// Count the total added articles and added articles per journal
		if((p_Flags & ProcessArticleHandler.FLAG_ADDED) == ProcessArticleHandler.FLAG_ADDED) 
		{
			m_ArticlesAdded++;

			int counter = 0;
			
			if(m_ArticlesPerJournalAdded.containsKey(m_CurrentArticle.GetJournal())) {
				counter = m_ArticlesPerJournalAdded.get(m_CurrentArticle.GetJournal());
			}
			
			m_ArticlesPerJournalAdded.put(m_CurrentArticle.GetJournal(), counter + 1);					
		}		
		
		// Count the total updated articles and updated articles per journal
		if((p_Flags & ProcessArticleHandler.FLAG_UPDATED) == ProcessArticleHandler.FLAG_UPDATED) 
		{
			m_ArticlesUpdated++;

			int counter = 0;
			
			if(m_ArticlesPerJournalUpdated.containsKey(m_CurrentArticle.GetJournal())) {
				counter = m_ArticlesPerJournalUpdated.get(m_CurrentArticle.GetJournal());
			}
			
			m_ArticlesPerJournalUpdated.put(m_CurrentArticle.GetJournal(), counter + 1);					
		}		
	}
	
	/**
	 * Print the progress of the parsing
	 * The progress is displayed by percentage of the current position in the document
	 *  and the total number of lines in the document
	 */
	public void PrintProgress ()
	{
		int progPerc = (int) ((m_Locator.getLineNumber() / (float) m_TotalLineNumbers) * 100);
		
		if(m_CurrProgPerc != progPerc) 
		{
			System.out.print("Progress: " + progPerc + "%" + (progPerc < 100 ? "\r" : "\n"));
			m_CurrProgPerc = progPerc;
		}
	}
	
	/**
	 * Print the result of the parsing
	 */
	public void PrintResult ()
	{
		System.out.println("Articles found: " + m_ArticlesFound);
		System.out.println("Articles added: " + m_ArticlesAdded);
		System.out.println("Articles updated: " + m_ArticlesUpdated);
		System.out.println("Articles per journal added/updated: ");
		
		for (Map.Entry<String, Integer> entry : m_ArticlesPerJournalAdded.entrySet()) 
		{
			String addedS = "Added: " + entry.getValue();
			String updatedS = "Updated: 0";
			
			if(m_ArticlesPerJournalUpdated.containsKey(entry.getKey())) {
				updatedS = "Updated: " + m_ArticlesPerJournalUpdated.get(entry.getKey());
			}
			
			System.out.println("- " + entry.getKey() + ": " + addedS + ", " + updatedS);
		}

		for (Map.Entry<String, Integer> entry : m_ArticlesPerJournalUpdated.entrySet()) 
		{
			if(!m_ArticlesPerJournalAdded.containsKey(entry.getKey()))
			{
				String addedS = "Added: 0";
				String updatedS = "Updated: " + entry.getValue();
				
				System.out.println("- " + entry.getKey() + ": " + addedS + ", " + updatedS);
			}
		}
	}
}

