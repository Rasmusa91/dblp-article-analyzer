import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * This class does everything the ReadableByteChannel does, except that it adds logic to the already existing methods to determine the progress
 */
public final class RBCWrapper implements ReadableByteChannel
{
	private ReadableByteChannel m_Reader;
	private int m_TotalBytes;
	private int m_DownloadedBytes;
	private int m_CurrProgPerc;
	private boolean m_PrintProgress;
	
	public RBCWrapper(ReadableByteChannel p_Reader, int p_TotalBytes, boolean p_PrintProgress) 
	{
		m_Reader = p_Reader;
		m_TotalBytes = p_TotalBytes;
		m_PrintProgress = p_PrintProgress;
		m_CurrProgPerc = -1;
	}
	
	@Override
	public void close() throws IOException 
	{
		m_Reader.close();
	}

	@Override
	public boolean isOpen() 
	{
		return m_Reader.isOpen();
	}

	@Override
	public int read(ByteBuffer dst) throws IOException 
	{
		int bytesRead = m_Reader.read(dst);
		
		// Save the amount of bytes read
		m_DownloadedBytes += bytesRead;
		
		if(m_PrintProgress) {
			PrintProgress();
		}
		
		return bytesRead;
	}
	
	/**
	 * Print the progress of the read (bytes read / total bytes)
	 */
	public void PrintProgress ()
	{
		int progPerc = (int) ((m_DownloadedBytes / (float) m_TotalBytes) * 100);
		
		if(m_CurrProgPerc != progPerc) 
		{
			System.out.print("Progress: " + progPerc + "%" + (progPerc < 100 ? "\r" : "\n"));
			m_CurrProgPerc = progPerc;
		}
	}
}