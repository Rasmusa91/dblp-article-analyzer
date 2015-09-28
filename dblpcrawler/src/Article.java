import java.util.ArrayList;

public class Article 
{
	private String m_DBLPKey;
	private String m_LastModified;
	private ArrayList<String> m_Authors;
	private String m_Title;
	private String m_Pages;
	private int m_Year;
	private String m_Volume;
	private String m_Journal;
	private String m_Number;
	private String m_ElectronicEdition;
	private String m_LocalURL;
	
	public Article()
	{
		m_Authors = new ArrayList<String> ();
	}
	
	public String GetDBLPKey() 
	{
		return m_DBLPKey;
	}

	public void SetDBLPKey(String p_DBLPKey) 
	{
		m_DBLPKey = p_DBLPKey;
	}

	public String GetLastModified() 
	{
		return m_LastModified;
	}

	public void SetLastModified(String p_LastModified) 
	{
		m_LastModified = p_LastModified;
	}

	public String[] GetAuthors() 
	{
		return m_Authors.toArray(new String[m_Authors.size()]);
	}

	public void AddAuthor(String p_Author) 
	{
		m_Authors.add(p_Author);
	}

	public String GetTitle() 
	{
		return m_Title;
	}

	public void SetTitle(String p_Title) 
	{
		m_Title = p_Title;
	}

	public String GetPages() 
	{
		return m_Pages;
	}

	public void SetPages(String p_Pages) 
	{
		m_Pages = p_Pages;
	}

	public int GetYear() 
	{
		return m_Year;
	}

	public void SetYear(int p_Year) 
	{
		m_Year = p_Year;
	}

	public String GetVolume() 
	{
		return m_Volume;
	}

	public void SetVolume(String p_Volume) 
	{
		m_Volume = p_Volume;
	}

	public String GetJournal() 
	{
		return m_Journal;
	}

	public void SetJournal(String p_Journal) 
	{
		m_Journal = p_Journal;
	}

	public String GetNumber() 
	{
		return m_Number;
	}

	public void SetNumber(String p_Number) 
	{
		m_Number = p_Number;
	}

	public String GetElectronicEdition() 
	{
		return m_ElectronicEdition;
	}

	public void SetElectronicEdition(String p_ElectronicEdition) 
	{
		m_ElectronicEdition = p_ElectronicEdition;
	}

	public String GetLocalURL() 
	{
		return m_LocalURL;
	}

	public void SetLocalURL(String p_LocalURL) {
		m_LocalURL = p_LocalURL;
	}
}
