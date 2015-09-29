import java.io.File;

/**
 * This class is responsible for truncating a directory
 */
public class TruncateDirHandler 
{
	/**
	 * Truncate a directory
	 * 
	 * @param p_Directory The directory to be truncated
	 * @param p_PrintProgress If printing progress is wanted
	 */
	public static void RemoveFilesinDir(String p_Directory, boolean p_PrintProgress)
	{
		File dir = new File(p_Directory);
		
		if(p_PrintProgress)
		{
			if(dir.listFiles().length > 0) {
				System.out.println("Removing files:");
			}
			else {
				System.out.println("No files to remove");
			}
		}

		// Iterate all files in the directory and delete them
		for(File file: dir.listFiles())
		{
			if(p_PrintProgress) {
				System.out.println("- " + file.getName());
			}
			
			file.delete();
		}
	}
}
