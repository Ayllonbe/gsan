package gsan.distribution.gsan_api.run.representative;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rcaller.datatypes.DataFrame;
import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;

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

	
	public static  Communication  comunication(Map<String, Object> fileSS, String method) 
	{  
		 RCaller caller = RCaller.create();
	      RCode code = RCode.create();
	      
	      Object[][] o = (Object[][]) fileSS.get("table");
	      String[] s = (String[]) fileSS.get("names");
	      
		
		  DataFrame df = DataFrame.create(o, s);
		  code.addRCode("library(\"fastcluster\")");
	      code.addDataFrame("df", df);
	      
	      code.addRCode("d <- as.dist(1-df)");
	      code.addRCode("h <- hclust(d,\"average\")");
	      
	      code.addRCode("silClus <- function(hc.obj,dist.obj,nc){\n" + 
	      		"  require(cluster)\n" + 
	      		"  \n" + 
	      		"  asw <-c()\n" + 
	      		"  for( k in 2 : nc){\n" + 
	      		"    sil <- silhouette(cutree(hc.obj,k = k), dist.obj)\n" + 
	      		"    asw <- c(asw,summary(sil)$avg.width)\n" + 
	      		"  }\n" + 
	      		"  \n" + 
	      		"  \n" + 
	      		"  #  print(which.max(unlist(l))+1)\n" + 
	      		"  #print(which.max(asw)+1)\n" + 
	      		"  # return(which.max(unlist(l))+1)\n" + 
	      		"  return(which.max(asw)+1)\n" + 
	      		"}");
	      
	      code.addRCode("cl <- silClus(h,d, nrow(df) -1)");
	      code.addRCode("cutree.obj <- cutree(h,k=cl)\n"
	      		+ "sil.obj <- summary(silhouette(cutree.obj, d))$clus.avg.widths\n" + 
	      		"");
	      code.addRCode("clusters <- c()\n" + 
		      		"\n" + 
		      		"termCl <- c()\n" + 
		      		"\n" + 
		      		"for(x in 1:cl){\n" + 
		      		"  namesCluster <- names(cutree.obj[cutree.obj==x])\n" + 
<<<<<<< HEAD
		      		"  termCl<-c(termCl,length(namesCluster))\n"+
		      		"collapseCluster <- \"\"\n"+
		      		" if(any(i <- grep(\"HSA\",namesCluster))){\n"+
		      		"  collapseCluster <- gsub(\"[.]\", \"-\",paste(namesCluster,collapse=\";\"))\n" + 
		      		"}else{"+
		      		 "collapseCluster <- gsub(\"[.]\", \":\",paste(namesCluster,collapse=\";\"))\n"+
		      		"}\n"+
=======
		      		"  termCl<-c(termCl,length(namesCluster))\n"
		      		+ "collapseCluster <- gsub(\"[.]\", \":\",paste(namesCluster,collapse=\";\"))\n" + 
>>>>>>> Release_1.0.1
		      		"  clusters <- c(clusters,paste(x,sil.obj[x],collapseCluster,sep=\"\\t\"))\n" + 
		      		"}");
	      code.addRCode("Tcl <- length(termCl)\n" + 
	      		"Tmax <- max(termCl)\n" + 
	      		"Tmin <- min(termCl)\n" + 
	      		"Tmean <- mean(termCl)");
	      code.addRCode("result <- as.list(.GlobalEnv)");

	      caller.setRCode(code);
	      
		
		
		
		
		
//		Process p = Runtime.getRuntime().exec("python clust.py " + file + " "+method);
		Communication com = new Communication();
		
		
		
		try {
			
			caller.runAndReturnResult("result");
		      
		      
//		System.out.println("[Communication] Exit of R program");
		
		com = readFileClustering(caller);
		
		
		
		}
			catch (Exception e) {
				
				com.error = 4;
				com.log.error(e.getLocalizedMessage());
				com.log.error("[R comand] Communication failed");
			}
		return com;

	}
	
	@SuppressWarnings("resource")
	public static Communication readFileClustering(RCaller caller) throws NumberFormatException{
		Communication com = new Communication();
		Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
		Hashtable<Integer,Double> quality = new Hashtable<Integer,Double>();
		Hashtable<String,Double> statTable = new Hashtable<String,Double>();
		String[] cl = caller.getParser().getAsStringArray("clusters");
	     
		for(String s : cl) {
					String[] A = s.split("\t");
					int n = Integer.parseInt(A[0]);
					
					List<String> B = Arrays.asList(A[2].split(";"));
					clusters.put(n, B);
					quality.put(n, Double.parseDouble(A[1]));
					//System.out.println(s);
				

			}
		
		
		Double Tcl = format.round(Double.parseDouble(caller.getParser().getAsStringArray("Tcl")[0]));
		statTable.put("Cluster Number",Tcl); 
		
		Double Tmax = format.round(Double.parseDouble(caller.getParser().getAsStringArray("Tmax")[0]));
		statTable.put("Terms Number max in a cluster",Tmax); 
		Double Tmin = format.round(Double.parseDouble(caller.getParser().getAsStringArray("Tmin")[0]));
		statTable.put("Terms Number min in a cluster",Tmin); 
		Double Tmean = format.round(Double.parseDouble(caller.getParser().getAsStringArray("Tmean")[0]));
		statTable.put("Terms Number mean in a cluster",Tmean); 
		
		
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
    