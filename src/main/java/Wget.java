
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class Wget {

  public static void main(String[] args) throws IOException, ParseException {
	  File srcPath = new File("src/main/resources/static/AnnotationTAB");
		if(!srcPath.exists()) {
			srcPath.mkdirs();
		}
	String file = "ecocyc.gaf.gz";	
	
	  String annotationFile = "src/main/resources/static/AnnotationTAB/"+file.replace(".gz", "");
	  File created_file = new File(annotationFile);
		long localLong = created_file.exists()? created_file.lastModified():0 ;

	  URL url = new URL("http://current.geneontology.org/annotations/" + file);

	  URLConnection urlConnection = url.openConnection();
	  
	  Map<String, List<String>> headers = urlConnection.getHeaderFields();
	  SimpleDateFormat formatter=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
	  Date date = formatter.parse(headers.get("Last-Modified").get(0).replaceAll(" GMT", ""));
	  Timestamp ts_ftp = new Timestamp(date.getTime());
		Timestamp ts_local = new Timestamp(localLong);
		if(ts_ftp.after(ts_local)) {
			System.out.println("Downloading the new Gene Ontology Annotation version for " +file+".");
			
			 StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				GZIPInputStream in = new GZIPInputStream(urlConnection.getInputStream());
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {	
					while ((line = bufferedReader.readLine()) != null) {
						if(!line.contains("!"))
							stringBuilder.append(line+"\n");
					}
				}
			 
				String s = stringBuilder.toString();
				PrintWriter pw = new PrintWriter( annotationFile);
				pw.print(s);
				pw.close();
		}
		else {
			System.out.println("no");
		}
//	 
		
  }
  
  static void DownloadGO(String go, String urlS, String dossier) throws IOException, ParseException {
	  File srcPath = new File(dossier);
		if(!srcPath.exists()) {
			srcPath.mkdirs();
		}



			//System.out.println(file);

			String annotationFile = dossier+go;
			File created_file = new File(annotationFile);
			long localLong = created_file.exists()? created_file.lastModified():0 ;
	  
	  
	  URL url = new URL(urlS + go);

	  URLConnection urlConnection = url.openConnection();
	  
	  Map<String, List<String>> headers = urlConnection.getHeaderFields();
	  SimpleDateFormat formatter=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
	  Date date = formatter.parse(headers.get("Last-Modified").get(0).replaceAll(" GMT", ""));
	  Timestamp ts_ftp = new Timestamp(date.getTime());
		Timestamp ts_local = new Timestamp(localLong);
		if(ts_ftp.after(ts_local)) {
			System.out.println("Downloading the new Gene Ontology version.");
			 StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				InputStream inputStream = urlConnection.getInputStream();
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {	
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line+"\n");
					}
				}
			 
				String s = stringBuilder.toString();
				PrintWriter pw = new PrintWriter( annotationFile);
				pw.print(s);
				pw.close();
		}
		else {
			System.out.println("no");
		}
  }

}