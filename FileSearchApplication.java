/**
 * file > new > java project > add a new class > run as > run configurations
 * Esegui app da console con 2 param in input:
 * run a java application passing two or more arguments to the command line:
 * project > build project : disable
 * tx dx sulla classe / progetto : run as > run configuration > arguments: nella box, inserire i param separati da spazi.
 * tali param verranno passati al main() nell'array args[]. es args[0], args[1]
 * 
 * corso by Julian Robichaux - LinkedIn
 * 
 * 
 * 
 * + java.io.UncheckedIOException: java.nio.charset.MalformedInputException: Input length = 1
 * + java.nio.charset.MalformedInputException: Input length = 1 : binary file
 * 
 * Pattern class precompile the regularexpression to optimize memory & use over and over againg without compile it every time that you use it.
 * 
 * */



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSearchApplication {
	
	String path;
	String regex;
	String zipFileName;
	List <File> zipFiles = new ArrayList <File>() ; 
	Pattern pattern ;

	

	

	public static void main(String[] args) {
		
		FileSearchApplication app = new FileSearchApplication();
		System.out.println("args - lenght : " + args.length);	
		
		int valutazione = Math.min(args.length, 3);
		System.out.println("valutazione : " + valutazione);
		
		/**
		 *  l�esecuzione letteralmente salta alla riga di codice etichettata con il valore corrispondente al valore dell�argomento di switch e da quella posizione continua eseguendo tutte le istruzioni senza tener conto di eventuali case.
		 *  Ad esempio:
			int c = ...;
			switch (c) {
			case 1:
			System.out.print("1 ");
			case 2:
			System.out.print("2 ");
			break;
			case 3:
			System.out.println("3 ");
			case 4:
			System.out.println("4 ");
			default:
			System.out.println("4+");
			}
			Copy
			Facciamo una tabella in cui mettiamo i diversi risultati stampati sulla console al cambiare del valore di c:
			
			Valore di �c�	Stampa sulla console
			3	: 3 4 4+
			1	: 1 2
			
			return or break ? 
			
			salta a quello specifico case, eseguendo tutto quello che c'e' dopo e 
			FREGANDOSENE di tutti i CASE, quindi a cascata, a meno di non incontrare BREAK / RETURN 
		 * */
		
		switch (valutazione) { // only 3 command arguments
	
		case 0: // no arguments at all
			System.out.println("USAGE: FileSearchApplication path [regex]");
			return ; 
		case 3: // the third argument
			app.setZipFileName(args[2]);
			System.out.println("zipfilename : [" + app.getZipFileName() + "]");
		case 2: // the second argument
			app.setRegex(args[1]);
			System.out.println("regex : [" + app.getRegex() + "]");
		case 1: // the first argument
			app.setPath(args[0]);
			System.out.println("path : [" + app.getPath()+ "]");
		default:
			System.out.println("searching...");
		}
		try {
			app.walkDirectory(app.getPath()) ;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void walkDirectory (String path) throws IOException {
		this.walkDirectoryJava8(path);
		this.zipFilesJava7();
    }

	public void addFileToZip(File file) {
		if (getZipFileName() != null) 
			zipFiles.add(file);
	}
	
	public String getRelativeFileName(File file, File baseDir) {
		
		String fileName  = file.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
		
		fileName = fileName.replace('\\', '/'); // zipentryfile use "/"
		
		while (fileName.startsWith("/")) {
			fileName = fileName.substring(1);
		}
		return fileName;
	}
	
	
	
	// using FileScanner
//	public void walkDirectoryJava6 (String path) throws IOException {
//		System.out.println("sono in walkDirectoryJava6() : " + path);
//		File directory = new File (path);
//		File [] filesInThisDirectory = directory.listFiles() ; // list both files & subdirectories that exists in this path. NOT: all files in the founded subdirectory.
//		
//		for (File eachFile : filesInThisDirectory) {
//			if (eachFile.isDirectory()) 
//					walkDirectoryJava6(eachFile.getAbsolutePath());
//				else
//					processFile(eachFile); // todo with the file
//		}
//	}
	
//	public void walkDirectoryJava7 (String path) throws IOException {
//		Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
//			@Override
//			public FileVisitResult visitFile (Path file, BasicFileAttributes a)
//				throws IOException {
//				processFile(file.toFile());
//				return FileVisitResult.CONTINUE;
//			}
//		});
//	}
	
	

	// to process a file, use Streams + Lambda functions
	public void walkDirectoryJava8 (String path) throws IOException {
		System.out.println("walkDirectoryJava8() : " + path);
		Files.walk(Paths.get(path)) // return a Stream
			.forEach( f -> processFile (f.toFile())); // LAMBDA EXPRESSION : operate on each item on the Stream 
	}

	/**
	 * this method remain the same for all versions of java.
	 * 
	 * */
	public void processFile(File file) {
		System.out.println("processFile() - file : " + file);
		try {
			if (searchFile(file)) {					
				System.out.println(" ---> file match: " + file);
				addFileToZip(file);						
			}
		} catch (IOException | UncheckedIOException e) {
			System.out.println("processFile () - " + file + " - " + e.getMessage());
		}
	}
	
	public boolean searchFile(File file) throws IOException {
		return searchFileJava8(file) ;
	}

	// open & read each file
	/**
	 * anyMatch: case insensitive, recursive
	 * */
	public boolean searchFileJava8(File file) throws IOException {
		return Files.lines(file.toPath(), StandardCharsets.UTF_8)
			.anyMatch(t -> searchText(t)); // anyMatch: short circuiting method: return true as soon as find the searched one
	}
	
	
	public boolean searchText(String text) {
			//return (this.getRegex() == null) ? true :
			//		text.toLowerCase().contains(this.getRegex().toLowerCase()); // case insensitive search
		return searchText2(text);
	}
	
	/**find if the string contains this term:
	 * here find if the fileName contains searched input regex*/
	public boolean searchText2(String text) {
		return (this.getRegex() == null) ? true :
			// 1. Java Regular Expressione Engine - slower : 
			//text.matches(this.getRegex());
			
			// 2. faster: precompile regex & use everywhere
			// looking exactly match in the string:
			//this.pattern.matcher(text).matches(); 
		
			// find a term contained in the string
			this.pattern.matcher(text).find();
		
	}
	
	public void zipFilesJava7() throws IOException {
		try (ZipOutputStream out = new ZipOutputStream(
				new FileOutputStream(getZipFileName()))){
			File baseDir = new File(getPath());
	
			for (File file : zipFiles) {

				//fileName must be a relative path
				String fileName = getRelativeFileName(file, baseDir);
				System.out.println("added to zip fileName : " + fileName);
				ZipEntry zipEntry = new ZipEntry(fileName);
				zipEntry.setTime(file.lastModified());
				out.putNextEntry(zipEntry);
				
				Files.copy(file.toPath(), out); // copy a file to zip 

				out.closeEntry();
				
			}
		}
	}
	

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}
	public String getZipFileName() {
		return zipFileName;
	}
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

}
