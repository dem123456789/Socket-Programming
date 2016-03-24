import java.io.File;

public class FileChecker {

public static boolean Exists( String file ) 
{ 
    System.out.println("File being checked: " + file);
    return( (file.length()) > 0 && (new File(file).exists()) );
}

public static void main( String[] args ) 
{
    File dir = new File("src");

    System.out.println("List of files in source directory: ");
    if( dir.isDirectory()){
        File[] filenames = dir.listFiles();
        for( Object file : filenames ) {
            System.out.println(file.toString());
        }

    }
    else
        System.out.println("Directory does not exist.");

    if(FileChecker.Exists("3251.jpg"))
        System.out.println("File exists");
    else
        System.out.println("File does not exist");
}

}