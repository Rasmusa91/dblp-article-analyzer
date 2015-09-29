import java.net.UnknownHostException;

import com.mongodb.*;

/**
 * This singleton class is responsible for all the database
 */

public class DatabaseHandler 
{	
	private static DatabaseHandler INSTANCE;
		
	private MongoClient m_MongoClient;
	private DB m_MongoDatabase;
	
	protected DatabaseHandler ()
	{
		try {
			
			// Try to connect with host and port
			if(ConfigVars.DATABASE_HOST != null && ConfigVars.DATABASE_PORT != -1) {				
				m_MongoClient = new MongoClient(ConfigVars.DATABASE_HOST, ConfigVars.DATABASE_PORT);
			}
			// Try to connect with host
			else if(ConfigVars.DATABASE_HOST != null) {
				m_MongoClient = new MongoClient(ConfigVars.DATABASE_HOST);				
			}
			// Connect locally
			else {
				m_MongoClient = new MongoClient();				
			}
			
			// Use the wanted database
			m_MongoDatabase = m_MongoClient.getDB(ConfigVars.DATABASE_NAME);
			
			// Make sure all constraints is set (unique dblp key etc.)
			CheckArticleCollectionConstraints();
		} 
		catch (UnknownHostException e) {
			System.out.println("A database connection could not be established");
		}
	}
	
	/**
	 * Get the instance of the singleton
	 * 
	 * @return DatabaseHandler instance
	 */
	public static DatabaseHandler GetInstance()
	{
		if(INSTANCE == null) {
			INSTANCE = new DatabaseHandler ();
		}
		
		return INSTANCE;
	}
	
	/**
	 * Insert an article to the database
	 * 
	 * @param p_Article An article object
	 * @return The result of the insertion. Expect that the article was not unique if false.
	 */
	public boolean InsertArticle(Article p_Article)
	{
		boolean added = true;
		
		try {
			// Insert the article into the wanted collection
			m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION).insert(
				GetArticleDBObject(p_Article)
			);
		}
		catch(Exception e) {
			added = false;
		}

		return added;
	}
	
	/**
	 * Update an existing article
	 * 
	 * @param p_Article An article object.
	 * @return The result of the update
	 */
	public boolean UpdateArticle(Article p_Article)
	{
		boolean updated = true;
		
		try {
			// Update the article into the wanted collection,
			// Override the article in the database with the same DBLP Key
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
	
	/**
	 * Check if all required constraints of the database is available.
	 * This is necessary if someone would drop the collection without re-adding the constraints
	 */
	private void CheckArticleCollectionConstraints()
	{
		if(!m_MongoDatabase.collectionExists(ConfigVars.DATABASE_ARTICLE_COLLECTION))
		{
			// Add a unique constraint on the DBLPKey
			m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION)
				.createIndex(
					new BasicDBObject("DBLPKey", 1),
					new BasicDBObject("unique", true)					
				);
		}
	}
	
	/**
	 * Create a database article object with the values of a given article.  
	 *
	 * @param p_Article The article to be converted into a database object
	 * @return A database object with information of the given article
	 */
	private DBObject GetArticleDBObject (Article p_Article)
	{
		return BasicDBObjectBuilder.start()
			.add("DBLPKey", p_Article.GetDBLPKey())
			.add("LastModified", p_Article.GetLastModified())
			.get();
	}
	
	/**
	 * Find an article in the database
	 * 
	 * @param p_DBLPKey The key of the article to be found
	 * 
	 * @return A database object of the found article
	 */
	private DBObject GetArticleData (String p_DBLPKey)
	{
		return m_MongoDatabase.getCollection(ConfigVars.DATABASE_ARTICLE_COLLECTION).findOne(
				new BasicDBObject("DBLPKey", p_DBLPKey)
			);
	}
	
	/**
	 * Find the last modified date of the article with the matching dblp key
	 * 
	 * @param p_DBLPKey The key of the wanted article last modified date
	 * 
	 * @return The last modified date of the article with the matching key
	 */
	public String GetArticleLastModified (String p_DBLPKey)
	{
		return (String) GetArticleData(p_DBLPKey).get("LastModified");
	}
}
