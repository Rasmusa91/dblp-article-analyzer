import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.util.zip.GZIPInputStream;

public class UnzipManager 
{
	public static void UnzipGZipFile(String p_LocalFile, boolean p_PrintProgress)
	{
		FileInputStream fis;
		GZIPInputStream gis;
		ReadableByteChannelWrapper rbc;
		FileOutputStream fos;
		
		try {
			fis = new FileInputStream(p_LocalFile);
			gis = new GZIPInputStream(fis);
			rbc = new ReadableByteChannelWrapper(Channels.newChannel(gis), GetFileSize(p_LocalFile), p_PrintProgress);
			fos = new FileOutputStream(p_LocalFile.replace(".gz", ""));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			
			GetFileSize(p_LocalFile);
		}
		catch (Exception e) {
			System.out.println("Could not unzip file");
		}
	}
	
	/*
	 * Read the last 4 bytes of the GZip file to determine the size of the uncompressed file
	 * Warning: Unreliable
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
