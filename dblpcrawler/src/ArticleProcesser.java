import java.util.Date;

public class ArticleProcesser 
{
	public static int FLAG_IN_JOURNAL = 1;
	public static int FLAG_ADDED = 2;
	public static int FLAG_UPDATED = 4;
	
	public static int ProcessArticle(Article p_Article, boolean p_Update)
	{
		int flags = 0;
		
		if(Utilities.ArrayContains(ConfigVars.JOURNALS, p_Article.GetJournal())) {
			flags = FLAG_IN_JOURNAL;
		}
		
		if ((flags & FLAG_IN_JOURNAL) == FLAG_IN_JOURNAL) 
		{
			boolean added = DatabaseHandler.GetInstance().InsertArticle(p_Article);
			
			if(added) {
				flags = FLAG_IN_JOURNAL | FLAG_ADDED;
			}
			else 
			{
				if(p_Update)
				{
					Date newArticleDate = Utilities.StringToDate(p_Article.GetLastModified());
					Date oldArticleDate = Utilities.StringToDate(DatabaseHandler.GetInstance().GetArticleLastModified(p_Article.GetLastModified()));
					
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
