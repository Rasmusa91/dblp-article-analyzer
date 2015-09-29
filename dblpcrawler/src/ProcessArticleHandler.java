import java.util.Date;

/**
 * This class is responsible for processing an article object.
 * It checks if an article has the required requirements to be added to the database. 
 */
public class ProcessArticleHandler 
{
	// Result flags
	public static int FLAG_IN_JOURNAL = 1;
	public static int FLAG_ADDED = 2;
	public static int FLAG_UPDATED = 4;
	
	/**
	 * Process an article, check if it meets the requirements, insert or update it to the database
	 * 
	 * @param p_Article The article to be processed
	 * @param p_Update If you want the article to be updated if it's not unique

	 * @return The result flags of the process (ArticleProcesser.FLAG_IN_JOURNAL, ArticleProcesser.FLAG_ADDED, ArticleProcesser.FLAG_UPDATED)
	 */
	public static int ProcessArticle(Article p_Article, boolean p_Update)
	{
		int flags = 0;
		
		// Check if the article is contained in a journal we're interested in
		if(Utilities.ArrayContains(ConfigVars.JOURNALS, p_Article.GetJournal())) {
			flags = FLAG_IN_JOURNAL;
		}
		
		if ((flags & FLAG_IN_JOURNAL) == FLAG_IN_JOURNAL) 
		{
			// Try to insert the article into the database, if failed, it's not unique
			boolean added = DatabaseHandler.GetInstance().InsertArticle(p_Article);
			
			if(added) {
				flags = FLAG_IN_JOURNAL | FLAG_ADDED;
			}
			else 
			{
				// If the insertion of the article failed, update it (if wanted)
				if(p_Update)
				{
					// Compare the date of the old article and the new article
					Date newArticleDate = Utilities.StringToDate(p_Article.GetLastModified());
					Date oldArticleDate = Utilities.StringToDate(DatabaseHandler.GetInstance().GetArticleLastModified(p_Article.GetLastModified()));
					
					// If the new article was modified after the old one, update it
					if(newArticleDate.after(oldArticleDate)) 
					{
						boolean updated = DatabaseHandler.GetInstance().UpdateArticle(p_Article);
						
						if(updated) {
							flags = FLAG_IN_JOURNAL | FLAG_UPDATED;
						}
					}
				}
			}
		}
		
		return flags;
	}
}
