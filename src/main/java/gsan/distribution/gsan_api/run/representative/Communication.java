package gsan.distribution.gsan_api.run.representative;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gsan.distribution.gsan_api.read_write.Format;



public class Communication {
	public Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
	Hashtable<String,Double> statTable = new Hashtable<String,Double>();
	public Hashtable<Integer,Double> qualityTable = new Hashtable<Integer,Double>();
	public int error = 0;
	static Format format = new Format(3);
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public Communication(Hashtable<Integer,List<String>> cl, Hashtable<String,Double> stat ,
			Hashtable<Integer,Double> quality){
		clusters.putAll(cl);
		statTable.putAll(stat);
		qualityTable.putAll(quality);
	}
	public Communication(){
	}
	
	public void putClusters(Hashtable<Integer,List<String>> cl) {
		this.clusters.putAll(cl);
	}
	public void putStat(Hashtable<String,Double> stat) {
		this.statTable.putAll(stat);
	}
	public void putQuality(Hashtable<Integer,Double> quality) {
		this.qualityTable.putAll(quality);
	}	

	
	public static  Communication  comunication(String file, String method) 
	{  
//		Process p = Runtime.getRuntime().exec("python clust.py " + file + " "+method);
		Communication com = new Communication();
		float random = Math.round(Math.random()* 1000000000);
		File fR = new File(file);
		File ffolder = new File("src/main/tmp/");
		String out = random +".txt";
		File path = new File(ffolder.getAbsolutePath()+"/" +out);

//		System.out.println("[Communication] Enter to R program");
		
		
		File Rfile = new File("Scripts/clusteranalisis.R");
		String line = "Rscript "+Rfile+ " --file " + new File(file).getAbsolutePath() + " -m "+method+" --outFolder " +new File("src/main/tmp/").getAbsolutePath()+" -o "+out;
		//com.log.debug("Rscript "+Rfile+ " --file " + new File(file).getAbsolutePath() + " -m "+method+" --outFolder " +new File("src/main/tmp/").getAbsolutePath()+" -o "+out);
		Process p;
		try {
			p = Runtime.getRuntime().exec("Rscript "+Rfile+ " --file " + new File(file).getAbsolutePath() + " -m "+method+" --outFolder " +new File("src/main/tmp/").getAbsolutePath()+" -o "+out);
			

		   //BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
			p.waitFor();
		
//		System.out.println("[Communication] Exit of R program");
		
		com = readFileClustering(path);
		
			if(Files.exists(path.toPath()))
				Files.delete(path.toPath());
			if(Files.exists(fR.toPath()))
				Files.delete(fR.toPath());
		}
			catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				try {
					if(Files.exists(path.toPath()))
						Files.delete(path.toPath());
					if(Files.exists(fR.toPath()))
						Files.delete(fR.toPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				com.error = 3;
				com.log.error(e.getLocalizedMessage());
				com.log.error("[R comand] "+line);
			}
		return com;

	}
	
	@SuppressWarnings("resource")
	public static Communication readFileClustering(File path) throws NumberFormatException, IOException {
		Communication com = new Communication();
		Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
		Hashtable<Integer,Double> quality = new Hashtable<Integer,Double>();
		Hashtable<String,Double> statTable = new Hashtable<String,Double>();
		String info = new String();
		InputStream f = new FileInputStream(path);
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(f));
		String s = null;
		
			while ((s = stdInput.readLine()) != null) {
//				System.out.println(s);
				if(!s.contains("#")){
					String[] A = s.split("\t");
					int n = Integer.parseInt(A[0]);
					
					List<String> B = Arrays.asList(A[2].split(";"));
					clusters.put(n, B);
					quality.put(n, Double.parseDouble(A[1]));
					//System.out.println(s);
				}
				else{
					info = info + s + "\n";
					
				}

			}
		
		info = info.replace("#" ,"");
		for(String i : info.split("\n")){
//			System.out.println(i);
			String[] sp = i.split(":");
			Double v = format.round(Double.parseDouble(sp[1]));
			statTable.put(sp[0],v);
		}
		
		com.putClusters(clusters);
		com.putStat(statTable);
		com.putQuality(quality);
		
		
		return com;

	}

	public static Hashtable<Integer,List<String>>  getCluster(String ss, String method, String nc) throws IOException, InterruptedException
	{  


		
//		System.out.println("[Communication] Enter to R program");
		Process p = Runtime.getRuntime().exec("Rscript getCluster.R --semantic " + ss + " -m "+method+" -c " + nc);
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		p.waitFor();
//		System.out.println("[Communication] Exit of R program");
		File path = new File("/tmp/out.txt");
		Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
		if(!path.exists()){
			
//		System.err.println("No path");	
		}else{
		
		FileReader f = new FileReader(path);
	
		stdInput = new BufferedReader(f);
		String s = null;
		while ((s = stdInput.readLine()) != null) {
				String[] A = s.split("\t");
				int n = Integer.parseInt(A[0]);
				
				List<String> B = Arrays.asList(A[1].split(";"));
				clusters.put(n, B);
				//System.out.println(s);
			
		}
		path.delete();
		
		}
		return clusters;

	}
}
    