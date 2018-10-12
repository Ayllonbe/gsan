package gsan.distribution.gsan_api.read_write;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadFile {
	
	

	public static List<String> readTextFileByLines(String fileName) throws IOException {
		Charset charset = Charset.forName("UTF-8");
	    List<String> lines = Files.readAllLines(Paths.get(fileName),charset);
	    return lines;
	  }
	
	
	public static String readFileJSON(String filename) {
	    String result = "";
	    try {
	        @SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(filename));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        result = sb.toString();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	public static List<List<String>> ReadAnnotation(String goa) throws IOException{
		
		Charset charset = Charset.forName("UTF-8");
		
		
	    List<String> lines = Files.readAllLines(Paths.get(goa),charset);
	    
	    
	    List<List<String>> list = new ArrayList<List<String>>();
//	 
//	    while(pat.matcher(lines.get(0)).find()){
//	    	lines.remove(0);
//	    	
//	    }
	    for(String l : lines){
	    	if(!l.contains("!"))
	    		list.add(Arrays.asList(l.split("\t"))); // !! 3.6 sec. le procesus pour human
//	    	else {
//	    		System.out.println(l);
//	    	}
	    }
		
		return list;
		}
	
public static List<List<String>> Readcsv(String goa,String sep) throws IOException{
		
		Charset charset = Charset.forName("UTF-8");
		
		
	    List<String> lines = Files.readAllLines(Paths.get(goa),charset);
	    
	    
	    List<List<String>> list = new ArrayList<List<String>>();
	    
	    for(String l : lines){
	    	list.add(Arrays.asList(l.replace("\"", "").split(sep))); // !! 3.6 sec. le procesus pour human
	    }
	 
	   
		
		
		return list;
		}
	
	
	
	
	
	
}
