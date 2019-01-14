package gsan.server.gsan.api;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.server.singleton.graphSingleton;

public class FTPDownloader {

	FTPClient ftp = null;

	public FTPDownloader(String host, String user, String pwd) throws Exception {
		ftp = new FTPClient();

		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		int reply;
		ftp.connect(host);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		ftp.login(user, pwd);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();

		// lists files and directories in the current working directory


	}

	public InputStream downloadFile(String remoteFilePath) {


		try  {


			return this.ftp.retrieveFileStream(remoteFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public long viewFile(String remoteFilePath,String file_choose) {
		try  {
			FTPFile[] files = ftp.listFiles(remoteFilePath);
			// iterates over the files and prints details for each
			long ts = 0;
			for (FTPFile file : files) {
				if(file.getName().equals(file_choose)) {
					ts = file.getTimestamp().getTimeInMillis();


				}
			}

			return ts;
		} catch (IOException e) {

			e.printStackTrace();
			return 0;
		}
	}



	public void disconnect() {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			} catch (IOException f) {
				// do nothing as file is already downloaded from FTP server
			}
		}
	}

	public static void DownloadGOA() {
		File srcPath = new File("src/main/resources/static/AssociationTAB/");
		if(!srcPath.exists()) {
			srcPath.mkdirs();
		}
		//DateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
		String[] listFiles = new String[] {
				//				"gene_association.ecocyc.gz",
				//				"goa_human.gaf.gz",
				//				"gene_association.mgi.gz",
				//				"gene_association.zfin.gz",
				//				"gene_association.rgd.gz",
				//				"gene_association.sgd.gz",
				//				"gene_association.tair.gz",
				//				"goa_pig.gaf.gz",
				"HUMAN",
				"ARABIDOPSIS",
				"CHICKEN",
				"COW",
				"DOG",
				"FLY",
				"MOUSE",
				"PIG",
				"RAT",
				"WORM",
				"YEAST",
				"ZEBRAFISH",
				"gene_association.ecocyc.gz",
				"gene_association.cgd.gz"

		};



		String pathGO = "go/gene-associations/";
		String pathEBI = "pub/databases/GO/goa/";

		try {
			GlobalOntology go = graphSingleton.getGraph();
			for(String file : listFiles) {
				//System.out.println(file);
				if(file.contains("gene")) {
					String annotationFile = "src/main/resources/static/AssociationTAB/"+file.replace(".gz", "");

					FTPDownloader ftpDownloader =
							new FTPDownloader("ftp.geneontology.org", "anonymous", "");
					long ftpLong = ftpDownloader.viewFile(pathGO+file,file);
					if(thereIsModif(annotationFile, ftpLong)) {
						List<List<String>> gt = new ArrayList<>();
						gt.addAll(fromFTP(file,pathGO,go,ftpDownloader,true,"\t","!"));

						StringBuffer sb = new StringBuffer();
						for(List<String> line : gt) {

							for(String c : line) {
								sb.append(c+"\t");
							}
							sb.deleteCharAt(sb.length()-1);
							sb.append("\n");
						}
						PrintWriter pw = new PrintWriter(annotationFile);
						pw.print(sb);
						pw.close();
						File f = new File(annotationFile);
						f.setLastModified(ftpLong);
					}
					else {
						System.out.println("Are there some modification in the file?\n- " + false);
					}
					ftpDownloader.disconnect();

				}else {
					String gaf = "goa_"+file.toLowerCase()+".gaf.gz";
					String isogaf = "goa_"+file.toLowerCase()+"_isoform.gaf.gz";
					String annotationFile = "src/main/resources/static/AssociationTAB/"+gaf.replace(".gz", "");

					FTPDownloader ftpDownloader =
							new FTPDownloader("ftp.ebi.ac.uk", "anonymous", "");
					//System.out.println(pathEBI+file+"/"+gaf);

					long ftpLong = ftpDownloader.viewFile(pathEBI+file+"/"+gaf,gaf);
					if(thereIsModif(annotationFile, ftpLong)) {
						String path = pathEBI+file+"/";
						List<List<String>> gt = new ArrayList<>();
						gt.addAll(fromFTP(gaf,path,go,ftpDownloader,true,"\t","!"));
						ftpDownloader =
								new FTPDownloader("ftp.ebi.ac.uk", "anonymous", "");
						//						
						gt.addAll(fromFTP(isogaf,path,go,ftpDownloader,true,"\t","!"));

						StringBuffer sb = new StringBuffer();
						for(List<String> line : gt) {

							for(String c : line) {
								sb.append(c+"\t");
							}
							sb.deleteCharAt(sb.length()-1);
							sb.append("\n");
						}
						PrintWriter pw = new PrintWriter(annotationFile);
						pw.print(sb);
						pw.close();
						File f = new File(annotationFile);
						f.setLastModified(ftpLong);

					}else {
						System.out.println("Are there some modification in the file?\n- " + false);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean thereIsModif(String annotationFile, long ftpLong) {
		File created_file = new File(annotationFile);
		long localLong = created_file.lastModified() ;

		Timestamp ts_ftp = new Timestamp(ftpLong);
		Timestamp ts_local = new Timestamp(localLong);

		return ts_ftp.after(ts_local);

	}
	public static List<List<String>> fromFTP(String file, String pathGO, GlobalOntology go,
			FTPDownloader ftpDownloader, boolean compress,String delim,String comment) throws Exception {



		//	System.out.println(pathGO+file);
		System.out.println("Are there some modification in the file?\n- " + true);
		InputStream is = ftpDownloader.downloadFile(pathGO+file);

		System.out.println("FTP File downloaded successfully");




		System.out.println("Reading GOA files");
		List<List<String>> goaTable = new ArrayList<>();
		try {
			Reader decoder = Iscompressed(is, compress);

			BufferedReader br = new BufferedReader(decoder);
			String line;

			while ((line = br.readLine()) != null) {
				if(!line.contains(comment)) {
					String[] array = line.split(delim);
					List<String> col = new ArrayList<>();
					for(String a : array) col.add(a);

					goaTable.add(col);
				}else {

					System.out.println(line);
				}

			}



		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.

		}
		ftpDownloader.disconnect();
		return goaTable;

	}

	public static Reader Iscompressed(InputStream is, boolean c) throws IOException{
		if(c) {
			GZIPInputStream in = new GZIPInputStream(is);

			return new InputStreamReader(in);
		}else {
			return new InputStreamReader(is);
		}

	}




	public static boolean DownloadGOOWL(String file) {
		File srcPath = new File("src/main/resources/static/ontology/");
		if(!srcPath.exists()) {
			srcPath.mkdirs();
		}

		String path = "go/ontology/";

		try {

			//System.out.println(file);

			String annotationFile = "src/main/resources/static/ontology/"+file;
			File created_file = new File(annotationFile);
			long localLong = created_file.exists()? created_file.lastModified():0 ;
			FTPDownloader ftpDownloader =
					new FTPDownloader("ftp.geneontology.org", "anonymous", "");
			long ftpLong = ftpDownloader.viewFile(path+file,file);
			Timestamp ts_ftp = new Timestamp(ftpLong);
			Timestamp ts_local = new Timestamp(localLong);
			if(ts_ftp.after(ts_local)) {

				System.out.println("Are there some modification in the file?\n- " + true);
				InputStream is = ftpDownloader.downloadFile(path+file);

				System.out.println("FTP File downloaded successfully");




				System.out.println("Reading GO file");

				try {

					Reader decoder = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(decoder);
					String line;
					int count=0;
					StringBuffer sb = new StringBuffer();
					while ((line = br.readLine()) != null) {
						sb.append(line+"\n");
						//System.out.println(line);
						if(count%100000==0) {
							System.out.println(count+"\t"+line);
							
						}
						count++;
					}
					PrintWriter pw = new PrintWriter(annotationFile);

					pw.print(sb);
					pw.close();


					created_file.setLastModified(ftpLong);

					// read from your scanner
				}
				catch(IOException ex) {
					// there was some connection problem, or the file did not exist on the server,
					// or your URL was not in the right format.
					// think about what to do now, and put it here.
					ex.printStackTrace(); // for now, simply output it.
				}
				ftpDownloader.disconnect();
				return true;
			}else {
				System.out.println("Are there some modification in the file?\n- " + false);
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}

