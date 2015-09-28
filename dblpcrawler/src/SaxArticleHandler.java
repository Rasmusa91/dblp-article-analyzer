import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxArticleHandler extends DefaultHandler 
{
	private Article m_CurrentArticle;
	private Map<String, Boolean> m_FetchingAdditionalAttribute;
	
	/* Progress */
	private boolean m_PrintProgress;
	private Locator m_Locator;
	private long m_TotalLineNumbers;
	private int m_CurrProgPerc;
	private int m_ArticlesFound;
	private int m_ArticlesAdded;
	private int m_ArticlesUpdated;
	private Map<String, Integer> m_ArticlesPerJournalAdded;
	private Map<String, Integer> m_ArticlesPerJournalUpdated;

	public SaxArticleHandler()
	{
		m_CurrentArticle = null;
		m_FetchingAdditionalAttribute = new HashMap<String, Boolean>();		
		
		m_PrintProgress = false;
	}
	
	public SaxArticleHandler(long p_TotalLineNumbers) 
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
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		if(qName.equals("article")) 
		{
			m_CurrentArticle = new Article ();
			
			for(int i = 0; i < attributes.getLength(); i++) 
			{
				HandleAttribute(attributes.getQName(i), attributes.getValue(i));				
			}
		}
		else if (m_CurrentArticle != null)
		{
			m_FetchingAdditionalAttribute.put(qName, true);
		}
		
		if(m_PrintProgress) {
			PrintProgress ();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if(qName.equals("article")) 
		{
			if(m_CurrentArticle != null)
			{
				int flags = ArticleProcesser.ProcessArticle(m_CurrentArticle, ConfigVars.UPDATE_EXISTING_ARTICLES);		
				
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
	
	@Override
	public void characters(char ch[], int start, int length) throws SAXException 
	{		
		for (Map.Entry<String, Boolean> entry : m_FetchingAdditionalAttribute.entrySet()) 
		{
			if(entry.getValue()) 
			{
				HandleAttribute(entry.getKey(), new String (ch, start, length));
				entry.setValue(false);
			}
		}
	}
	
	@Override
	public void setDocumentLocator(Locator locator)
	{
		m_Locator = locator;
	}
	
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
	
	private void HandleProccessedArticleOutput (int p_Flags)
	{
		if((p_Flags & ArticleProcesser.FLAG_IN_JOURNAL) == ArticleProcesser.FLAG_IN_JOURNAL) {
			m_ArticlesFound++;
		}
		
		if((p_Flags & ArticleProcesser.FLAG_ADDED) == ArticleProcesser.FLAG_ADDED) 
		{
			m_ArticlesAdded++;

			int counter = 0;
			
			if(m_ArticlesPerJournalAdded.containsKey(m_CurrentArticle.GetJournal())) {
				counter = m_ArticlesPerJournalAdded.get(m_CurrentArticle.GetJournal());
			}
			
			m_ArticlesPerJournalAdded.put(m_CurrentArticle.GetJournal(), counter + 1);					
		}		
		
		if((p_Flags & ArticleProcesser.FLAG_UPDATED) == ArticleProcesser.FLAG_UPDATED) 
		{
			m_ArticlesUpdated++;

			int counter = 0;
			
			if(m_ArticlesPerJournalUpdated.containsKey(m_CurrentArticle.GetJournal())) {
				counter = m_ArticlesPerJournalUpdated.get(m_CurrentArticle.GetJournal());
			}
			
			m_ArticlesPerJournalUpdated.put(m_CurrentArticle.GetJournal(), counter + 1);					
		}		
	}
	
	public void PrintProgress ()
	{
		int progPerc = (int) ((m_Locator.getLineNumber() / (float) m_TotalLineNumbers) * 100);
		
		if(m_CurrProgPerc != progPerc) 
		{
			System.out.print("Progress: " + progPerc + "%" + (progPerc < 100 ? "\r" : "\n"));
			m_CurrProgPerc = progPerc;
		}
	}
	
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

