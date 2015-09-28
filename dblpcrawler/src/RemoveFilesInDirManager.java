import java.io.File;

public class RemoveFilesInDirManager 
{
	public static void RemoveFilesinDir(String p_Folder, boolean p_PrintProgress)
	{
		File dir = new File(p_Folder);
		
		if(p_PrintProgress)
		{
			if(dir.listFiles().length > 0) {
				System.out.println("Removing files:");
			}
			else {
				System.out.println("No files to remove");
			}
		}

		for(File file: dir.listFiles())
		{
			if(p_PrintProgress) {
				System.out.println("- " + file.getName());
			}
			
			file.delete();
		}
	}
}
