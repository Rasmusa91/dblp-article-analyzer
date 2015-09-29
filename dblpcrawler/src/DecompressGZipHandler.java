import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.util.zip.GZIPInputStream;

/**
 * This class is responsible for decompressing GZip files.
 */
public class DecompressGZipHandler 
{
	/**
	 * Decompress the file
	 * 
	 * @param p_LocalFile The location and name of the file
	 * @param p_PrintProgress If debugging is enabled
	 */
	public static void Decompress(String p_LocalFile, boolean p_PrintProgress)
	{
		GZIPInputStream gis;
		RBCWrapper rbc;
		FileOutputStream fos;
		
		try {
			// Use premade libray gzipinput stream decompress
			gis = new GZIPInputStream(new FileInputStream(p_LocalFile));
			
			// Use the custom RBCWrapper to read the file, so progress can be printed
			rbc = new RBCWrapper(Channels.newChannel(gis), GetFileSize(p_LocalFile), p_PrintProgress);
			
			// Write the file to the same location and the same name (except the .gz extension)
			fos = new FileOutputStream(p_LocalFile.replace(".gz", ""));
			
			// Write the new file
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		catch (Exception e) {
			System.out.println("Could not unzip file");
		}
	}
	
	/*
	 * Warning: Unreliable
	 * Read the last 4 bytes of the GZip file to determine the size of the uncompressed file
	 * */
	private static int GetFileSize(String p_LocalFile)
	{
		int fileSize = -1;
		byte[] bytes = new byte[4];
		RandomAccessFile raf = null;
		
		try {
			raf = new RandomAccessFile(p_LocalFile, "r");		
			raf.seek(raf.length() - 4);
			raf.read(bytes);
			fileSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			fileSize = -1;
		}
		finally 
		{
			try {
				raf.close();
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		return fileSize;
	}
}
