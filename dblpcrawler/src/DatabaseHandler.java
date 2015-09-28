import java.net.UnknownHostException;

import com.mongodb.*;

public class DatabaseHandler 
{	
	private static DatabaseHandler INSTANCE;
		
	private MongoClient m_MongoClient;
	private DB m_MongoDatabase;
	
	protected DatabaseHandler ()
	{
		try {
			if(ConfigVars.DATABASE_HOST != null && ConfigVars.DATABASE_PORT != -1) {				
				m_MongoClient = new MongoClient(ConfigVars.DATABASE_HOST, ConfigVars.DATABASE_PORT);
			}
			else if(ConfigVars.DATABASE_HOST != null) {
				m_MongoClient = new MongoClient(ConfigVars.DATABASE_HOST);				
			}
			else {
				m_MongoClient = new MongoClient();				
			}
			
			m_MongoDatabase = m_MongoClient.getDB(ConfigVars.DATABASE_NAME);
			CheckArticleCollectionConstraints();
		} 
		catch (UnknownHostException e) {
			System.out.println("A database connection could not be established");
		}
	}
	
	public static DatabaseHandler GetInstance()
	{
		if(INSTANCE == null) {
			INSTANCE = new DatabaseHandler ();
		}
		
		return INSTANCE;
	}
	
	public boolean InsertArticle(Article p_Article)
	{
		boolean added = true;
		
		try {
			m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION).insert(
				GetArticleDBObject(p_Article)
			);
		}
		catch(Exception e) {
			added = false;
		}

		return added;
	}
	
	public boolean UpdateArticle(Article p_Article)
	{
		boolean updated = true;
		
		try {
			m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION).update(
				new BasicDBObject("DBLPKey", p_Article.GetDBLPKey()),
				GetArticleDBObject(p_Article)
			);
		}
		catch(Exception e) {
			updated = false;
		}

		return updated;		
	}
	
	private void CheckArticleCollectionConstraints()
	{
		if(!m_MongoDatabase.collectionExists(ConfigVars.DATABASE_ARTICLE_COLLECTION))
		{
			m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION)
				.createIndex(
					new BasicDBObject("DBLPKey", 1),
					new BasicDBObject("unique", true)					
				);
		}
	}
	
	private DBObject GetArticleDBObject (Article p_Article)
	{
		return BasicDBObjectBuilder.start()
			.add("DBLPKey", p_Article.GetDBLPKey())
			.add("LastModified", p_Article.GetLastModified())
			.get();
	}
	
	private DBObject GetArticleData (String p_DBLPKey)
	{
		return m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION).findOne(
				new BasicDBObject("DBLPKey", p_DBLPKey)
			);
	}
	
	public String GetArticleLastModified (String p_DBLPKey)
	{
		return (String) GetArticleData(p_DBLPKey).get("LastModified");
	}
}
