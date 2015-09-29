public class Main 
{
    public static void main(String[] args) 
    {
    	long startTime = System.currentTimeMillis();
          	
    	ConfigVars.Load();
    	
    	/* Empty temp folder */
    	if(ConfigVars.TRUNCATE_TEMP_BEGINNING) 
    	{
	    	System.out.println("Removing old temp files...");
	    	//RemoveFilesInDirManager.RemoveFilesinDir(ConfigVars.LOCAL_TEMP_DIR, ConfigVars.PRINT_PROGRESS);
    	}
    	
    	/* Download dblp file */
   		System.out.println("\nDownloading DBLP XML file...");
   		//DownloadManager.DownloadFile(ConfigVars.REMOTE_DBLP_FILE, ConfigVars.LOCAL_DBLP_FILE_GZ, ConfigVars.PRINT_PROGRESS);

   		/* Unzip dblp file */
   		System.out.println("\nUnzipping DBLP XML file...");
   		//UnzipManager.UnzipGZipFile(ConfigVars.LOCAL_DBLP_FILE_GZ, ConfigVars.PRINT_PROGRESS);

   		/* Read dblp file */
   		System.out.println("\nReading DBLP XML file...");
   		DBLPReadFileHandler.ReadDBLPFile(ConfigVars.LOCAL_DBLP_FILE, ConfigVars.PRINT_PROGRESS);
   		
    	/* Empty temp folder */
    	if(ConfigVars.TRUNCATE_TEMP_END) 
    	{
    		System.out.println("\nRemoving new temp files...");
   			//RemoveFilesInDirManager.RemoveFilesinDir(ConfigVars.LOCAL_TEMP_DIR, ConfigVars.PRINT_PROGRESS);
    	}
    	
    	long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("\nExecution time: " + Utilities.TimeToString	(elapsedTime));
    }
}