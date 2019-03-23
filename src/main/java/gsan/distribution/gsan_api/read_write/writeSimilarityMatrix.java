package gsan.distribution.gsan_api.read_write;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import gsan.distribution.gsan_api.semantic_similarity.*;

public class writeSimilarityMatrix {
	
	private String similarity;
	private Map<String,SemanticSimilarity> mapSS;

	
	public writeSimilarityMatrix(String s) {
//		System.out.println(s);
		this.mapSS = new HashMap<>();
		this.mapSS.put("simrel", new SimRel());
		this.mapSS.put("lin", new Lin());
		this.mapSS.put("resnik", new Resnik());
		this.mapSS.put("distanceFunction", new DF());
		this.mapSS.put("ganesan", new Ganesan());
		this.mapSS.put("pekarStaab", new PS());
		this.mapSS.put("aic", new AIC());
		this.mapSS.put("nUnivers", new NUnivers());
		this.mapSS.put("NUM", new NUnivers_Mod());
		//this.mapSS.put("wuPalmer", new WP());
		//this.mapSS.put("wang", new Wang()); // For instance Wang is not appliable.
		this.similarity = s;
	}
	
	public Map<String, Object> similarityMethod(GlobalOntology go,List<String> terminos) {
		Double[][] matrixSS = new Double[terminos.size()][terminos.size()];
		String[] names = new String[terminos.size()];
		for(int i = 0; i<terminos.size();i++){
			String t1 = terminos.get(i);
			names[i] = t1;
			InfoTerm it1 = go.allStringtoInfoTerm.get(t1);
			matrixSS[i][i] = 1.;
			for(int j = i+1; j<terminos.size();j++){
				String t2 = terminos.get(j);
				InfoTerm it2 = go.allStringtoInfoTerm.get(t2);
				if(it1.top.equals(it2.top)) {
				matrixSS[i][j] = this.mapSS.get(similarity).method(t1, t2, go);
				}else {
					matrixSS[i][j] = 0.;	
				}
				matrixSS[j][i] = matrixSS[i][j];
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("table", matrixSS);
		map.put("names", names);
		
		return map;
		
//StringBuffer sb = new StringBuffer();
//		
//		sb.append(";");
//		for(String t : terminos){
//			sb.append(t+";");
//		}
//	sb.deleteCharAt(sb.length()-1);
//	sb.append("\n");
//
//	for(int i=0; i<terminos.size();i++){
//		
//		sb.append(terminos.get(i)+";");
//		for(int j=0; j<terminos.size();j++){
//			sb.append(matrixSS[i][j]+";");
//		
//		}
//		
//		sb.deleteCharAt(sb.length()-1);
//		
//		sb.append("\n");
//	}
//		
//		sb.deleteCharAt(sb.length()-1);
//		float random = Math.round(Math.random()* 10000000);
//		this.file = "src/main/tmp/"+random+".csv";
//		try {
//			PrintWriter writer = new PrintWriter(this.file, "UTF-8");
//			writer.println(sb);
//			writer.close();
//			writer = new PrintWriter(this.file, "UTF-8");
//			writer.println(sb);
//			writer.close();
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
		
	}

}
