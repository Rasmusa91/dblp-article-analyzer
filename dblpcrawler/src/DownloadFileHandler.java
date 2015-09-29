import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;

/**
 * This class is responsible for downloading files
 */
public class DownloadFileHandler
{
	/**
	 * Download a remote file
	 * 
	 * @param p_RemoteFile The URL of the file
	 * @param p_LocalFile The location and name of where the file should be downloaded to
	 * @param p_PrintProgress If debugging is wanted
	 */
	public static void DownloadFile(String p_RemoteFile, String p_LocalFile, boolean p_PrintProgress)
	{   	 
   		URL url;
		RBCWrapper rbcw;
    	FileOutputStream fos;

    	try {   		
			url = new URL(p_RemoteFile);
			
	    	rbcw = new RBCWrapper(Channels.newChannel(url.openStream()), GetURLSize(url), p_PrintProgress);
	    	fos = new FileOutputStream(p_LocalFile);
	    	fos.getChannel().transferFrom(rbcw, 0, Long.MAX_VALUE);    	
    	}
    	catch (Exception e) {
    		System.out.println("Could not download the file (" + e.getMessage() + ")");
    	}
	}

	private static int GetURLSize (URL p_URL)
	{
		int size = -1;
		HttpURLConnection conn = null;

		try {
			conn = (HttpURLConnection) p_URL.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			size = conn.getContentLength();
		} 
		catch (IOException e) {
			size = -1;
		} 
		finally {
			conn.disconnect();
		}
		
		return size;
	}		
}
