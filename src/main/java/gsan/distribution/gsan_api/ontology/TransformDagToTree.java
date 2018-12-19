package gsan.distribution.gsan_api.ontology;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.json.simple.JSONObject;

import gsan.distribution.gsan_api.annotation.Annotation;

/*
 * Proposed class to transforme the dag structure to a simple tree. For a given set of terms,
 * I recover the subgraph containing the set and I apply 2 things:
 * 	.. I choose the most informative parents (applying a IC)
 * 	.. I remove the terms with 1 childs (except the leaf terms). Instead the childs of this parents
 * 	   will be the child of the parent of parent.
 */
public class TransformDagToTree {

	private  GlobalOntology go;

	public TransformDagToTree(GlobalOntology go) {
		// TODO Auto-generated constructor stub
		this.go = go;
	}
	public Map<String,Set<String>> testGene(Set<String> stockTerm){
		Map<String,Set<String>> map = new HashMap<>();
		for(String t : stockTerm) {
			Set<String> genes = new HashSet<String>(go.allStringtoInfoTerm.get(t).geneSet);
			List<String> childs = new ArrayList<String>(go.allStringtoInfoTerm.get(t).is_a.descendants);
			childs.retainAll(stockTerm);
			for(String c : childs) {
				genes.removeAll(go.allStringtoInfoTerm.get(c).geneSet);
			}
			map.put(t, genes);
			
		}
		
		return map;
	}
	
	public  Map<String,List<String>>  GetHierarchy(Annotation GOA, List<String> terminos, int ic) {
		

		
		try {
			
			Map<String,List<String>> hierarchy = new HashMap<>();
			hierarchy.putAll(getHierarchy(terminos, ic));
			
//			for(String key : hierarchy.keySet()) {
//				System.out.println(key + " " +hierarchy.get(key));
//			}
		return hierarchy;	
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

//			System.out.println("que pasa?");
			return null;
		}
		}
		@SuppressWarnings("unchecked")
		public  JSONObject transform(Map<String,List<String>> hierarchy, int ic,Map<String, Set<String>> mapTerm2genes ) {
			
			try {
			List<JSONObject> listJO = new ArrayList<>();

			for(String top : go.subontology.keySet()) {

				if(hierarchy.containsKey(top)) {

					JSONObject jo = new JSONObject();
					jo.put("id", top);
					if(hierarchy.get(top).size()>0){

						List<JSONObject> childs = jsonMethod(hierarchy.get(top), hierarchy,mapTerm2genes);
						jo.put("children", childs);
						for(String g : mapTerm2genes.get(top)) {
							JSONObject geneJO = new JSONObject();
							geneJO.put("id",g);
							geneJO.put("size",1);
							childs.add(geneJO);
						}
					}
					else {
						List<JSONObject> childs = new ArrayList<>();
						for(String g: mapTerm2genes.get(top)) {
							JSONObject geneJO = new JSONObject();
							geneJO.put("id",g);
							geneJO.put("size",1);
							childs.add(geneJO);
						}
						jo.put("children", childs );
					}
					//System.out.println(jo.toJSONString());
					listJO.add(jo);
				}
			}

			if(listJO.size()>1) {
				JSONObject jo = new JSONObject();
				jo.put("id", "GO");
				jo.put("name","Gene Ontology");
				jo.put("children", listJO);
				return jo;
			}else {
				return listJO.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

//			System.out.println("que pasa?");
			return null;
		}


	}

	@SuppressWarnings("unchecked")
	private  List<JSONObject>  jsonMethod(List<String> mod, Map<String,List<String>> hierarchy,Map<String, Set<String>> mapTerm2genes) {



		List<JSONObject> jolist = new ArrayList<>();
		for(String t : mod) {
			JSONObject jo = new JSONObject();
			jo.put("id", t);
			if(!hierarchy.get(t).isEmpty()){

				List<JSONObject> childs = jsonMethod(hierarchy.get(t), hierarchy,mapTerm2genes);
				for(String g : mapTerm2genes.get(t)) {
					JSONObject geneJO = new JSONObject();
					geneJO.put("id",g);
					geneJO.put("size",1);
					childs.add(geneJO);
				}
				
				jo.put("children", childs);
			}
			else {
				List<JSONObject> childs = new ArrayList<>();
				for(String g: mapTerm2genes.get(t)) {
					JSONObject geneJO = new JSONObject();
					geneJO.put("id",g);
					geneJO.put("size",1);
					childs.add(geneJO);
				}
				jo.put("children", childs );
			}

			jolist.add(jo);
		}
		return jolist;

	}


	private  Map<String,List<String>> getHierarchy(List<String> term, int ic) {

		Stack<String> pile = new Stack<String>();
		Set<String> stockage = new HashSet<String>();

		Map<String,Set<String>> hierarchy = new HashMap<>();
		Map<String,String> map = new HashMap<>();
		for(int i = 0; i<term.size();i++){
			String St = term.get(i);
			hierarchy.put(St, new HashSet<>());
			pile.push(St);
			InfoTerm t = go.allStringtoInfoTerm.get(St); 
			t.bits = new BitSet();
			t.bits.set(i);
			stockage.addAll(go.allStringtoInfoTerm.get(St).is_a.ancestors);

		}
		/*
		 * Charger le stockage
		 */
		stockage.removeAll(term);

		for(String s :stockage) {
			go.allStringtoInfoTerm.get(s).bits = new BitSet();
		}
		
		stockage.addAll(term);
		
		/*
		 * Charger les bitSet dans la taxonomie is_a 
		 */
		while(pile.size()>0){
			String x = (String) pile.pop();
			if(!go.subontology.containsKey(x)) {
				InfoTerm tx = go.allStringtoInfoTerm.get(x);
				if(!tx.id.equals(tx.top)) {
				String a = mica(tx,term,ic); // get the parent whit the best IC. 
				map.put(x, a);
				InfoTerm ta = go.allStringtoInfoTerm.get(a);
				ta.bits.or(tx.bits);
				pile.push(a);

				}
				

			}
		}
		
		
		Queue<String> queue = new LinkedList<>(term);
		
		
		while(!queue.isEmpty()) {
			
			String t = queue.poll();
			Stack<String> stack = new Stack<>(); // Cargo una pila
			stack.push(t);
			InfoTerm it = go.allStringtoInfoTerm.get(t);
			while(stack.size()>0) {
				String a = map.get(stack.pop());
				if(a!=null) {
				InfoTerm mica = go.allStringtoInfoTerm.get(a);
				
				if(it.bits.equals(mica.bits)&&!mica.id.equals(it.top)) {
					stack.push(mica.id);
				}
				else{
					if(!hierarchy.containsKey(mica.id)) {
						hierarchy.put(mica.id, new HashSet<>());
						hierarchy.get(mica.id).add(it.id);
						queue.add(mica.id);
					}else {
						hierarchy.get(mica.id).add(it.id);
					}
				}
				
			}
			}
			
			
		}
		Map<String,List<String>> res_hierarchy = new HashMap<>();
		for(String t : hierarchy.keySet()) {
			res_hierarchy.put(t, new ArrayList<>());
			res_hierarchy.get(t).addAll(hierarchy.get(t));
		}
		
		return res_hierarchy;
	}
	
////	private  Map<String,List<String>> getHierarchyFullTest(List<String> term, int ic) {
////
////		Stack<String> pile = new Stack<String>();
////		Set<String> stockage = new HashSet<String>();
////
////		Map<String,Set<String>> hierarchy = new HashMap<>();
////		Map<String,List<String>> map = new HashMap<>();
////		for(int i = 0; i<term.size();i++){
////			String St = term.get(i);
////			hierarchy.put(St, new HashSet<>());
////			pile.push(St);
////			InfoTerm t = go.allStringtoInfoTerm.get(St); 
////			t.bits = new BitSet();
////			t.bits.set(i);
////			stockage.addAll(go.allStringtoInfoTerm.get(St).is_a.ancestors);
////
////		}
//		/*
//		 * Charger le stockage
//		 */
//		stockage.removeAll(term);
//
//		for(String s :stockage) {
//			go.allStringtoInfoTerm.get(s).bits = new BitSet();
//		}
//		
//		stockage.addAll(term);
//		
//		/*
//		 * Charger les bitSet dans la taxonomie is_a 
//		 */
////		while(pile.size()>0){
////			String x = (String) pile.pop();
////			if(!go.subontology.containsKey(x)) {
////				InfoTerm tx = go.allStringtoInfoTerm.get(x);
////				if(!tx.id.equals(tx.top)) {
////				map.put(x, new ArrayList<>());
////				for(String a: tx.is_a.parents) {
////				map.get(x).add(a);
////				InfoTerm ta = go.allStringtoInfoTerm.get(a);
////				ta.bits.or(tx.bits);
////				pile.push(a);
////				}
////				}
////				
////
////			}
////		}
//		
//		for(String s :term) {
//			for(String anc:go.allStringtoInfoTerm.get(s).is_a.ancestors) {
//				go.allStringtoInfoTerm.get(anc).bits.or(go.allStringtoInfoTerm.get(s).bits);
//			}
//		}
//	
//		
//		Queue<String> queue = new LinkedList<>(term);
//		
//		
//		while(!queue.isEmpty()) {
//			
//			String t = queue.poll();
//			if(t.equals("GO:0045321")) {
//				System.out.println(go.allStringtoInfoTerm.get(t).name);
//				System.out.println(go.allStringtoInfoTerm.get(t).bits);
//			}
//			Stack<String> stack = new Stack<>(); // Cargo una pila
//			stack.push(t);
//			InfoTerm it = go.allStringtoInfoTerm.get(t);
//			while(stack.size()>0) {
//				String els = stack.pop();
//				List<String> la = go.allStringtoInfoTerm.get(els).is_a.parents;//map.get(stack.pop());
//				
//				if(t.equals("GO:0045321")) {
//					System.out.println(go.allStringtoInfoTerm.get(els).name);
//					System.out.println(go.allStringtoInfoTerm.get(els).bits);
//					System.out.println(la);
//				}
//				if(la!=null) {
//				for(String a : la) {
//				InfoTerm mica = go.allStringtoInfoTerm.get(a);
//				
//				if(it.bits.equals(mica.bits)&&!mica.id.equals(it.top)) {
//					stack.push(mica.id);
//				}
//				else{
//					if(!hierarchy.containsKey(mica.id)) {
//						hierarchy.put(mica.id, new HashSet<>());
//						hierarchy.get(mica.id).add(it.id);
//						queue.add(mica.id);
//					}else {
//						hierarchy.get(mica.id).add(it.id);
//					}
//				}
//				}
//			}
//			}
//			
//			
//		}
//		Map<String,List<String>> res_hierarchy = new HashMap<>();
//		for(String t : hierarchy.keySet()) {
//			res_hierarchy.put(t, new ArrayList<>());
//			res_hierarchy.get(t).addAll(hierarchy.get(t));
//		}
//		
//		return res_hierarchy;
//	}
//	
	
//	private  Map<String,List<String>> getHierarchyTest(List<String> term, int ic) {
//		
//		Set<String> stockTerm = new HashSet<String>();
//		for(String t : term) {
//			stockTerm.add(t);
//			stockTerm.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);
//		}
//		
//		for(String s :stockTerm) {
//			go.allStringtoInfoTerm.get(s).bits = new BitSet();
//		}
//		
//		for(int i = 0; i<term.size();i++){
//			InfoTerm it = go.allStringtoInfoTerm.get(term.get(i));
//			
//			it.bits.set(i);
//			
//			for(String anc : it.is_a.ancestors) {
//				go.allStringtoInfoTerm.get(anc).bits.set(i);
//			}
//			
//		}
//		
//		
//		
//		Map<String,List<String>> map = new HashMap<>();
//		
//		Stack<String> stack = new Stack<>();
//		stack.push("GO:0008150");
//		while(!stack.isEmpty()) {
//			String s = stack.pop();
//			List<String> childs = new ArrayList<String>(go.allStringtoInfoTerm.get(s).is_a.childrens);
//			childs.retainAll(stockTerm);
//			Set<String> finalChilds = new HashSet<String>();
//			for(String c:childs) {
//				InfoTerm itc = go.allStringtoInfoTerm.get(c);
//				List<String> cc = new ArrayList<String>(go.allStringtoInfoTerm.get(c).is_a.childrens);
//				cc.retainAll(stockTerm);
//				if(cc.size()!=0 && itc.bits.cardinality()==1 ) {
//				while(cc.size()==1 && itc.bits.cardinality()==1) {
//					c = cc.get(0);
//					itc = go.allStringtoInfoTerm.get(c);
//					cc = new ArrayList<String>(go.allStringtoInfoTerm.get(c).is_a.childrens);
//					cc.retainAll(stockTerm);
//					
//				}
//				}
//
//				stack.push(c);
//				finalChilds.add(c);
//	
//			}
//			map.put(s, new ArrayList<>(finalChilds));
//		}
//		
//		return map;
//	}
	/*
	 * Là on coupe seulement les intermediaires
	 */
//	private  Map<String,List<String>> getHierarchyTest2(List<String> term, int ic) {
//
//		Set<String> stockTerm = new HashSet<String>();
//		for(String t : term) {
//			stockTerm.add(t);
//			stockTerm.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);
//		}
//		Map<String,List<String>> map = new HashMap<>();
//		
//		Stack<String> stack = new Stack<>();
//		stack.push("GO:0008150");
//		while(!stack.isEmpty()) {
//			String s = stack.pop();
//			List<String> childs = new ArrayList<String>(go.allStringtoInfoTerm.get(s).is_a.childrens);
//			childs.retainAll(stockTerm);
//			Set<String> finalChilds = new HashSet<String>();
//			for(String c:childs) {
//				List<String> cc = new ArrayList<String>(go.allStringtoInfoTerm.get(c).is_a.descendants);
//				cc.retainAll(stockTerm);
////				if(cc.size()==1) {
////				while(cc.size()==1) {
////					c = cc.get(0);
////					cc = new ArrayList<String>(go.allStringtoInfoTerm.get(c).is_a.childrens);
////					cc.retainAll(stockTerm);
////					
////				}
////				}
//
//				stack.push(c);
//				finalChilds.add(c);
//	
//			}
//			map.put(s, new ArrayList<>(finalChilds));
//		}
//		
//		return map;
//	}
	
//	private  Map<String,List<String>> getHierarchyTest3(List<String> term, int ic) {
//
//		Set<String> stockTerm = new HashSet<String>();
//		for(String t : term) {
//			stockTerm.add(t);
//			stockTerm.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);
//		}
//		Map<String,Set<String>> map = new HashMap<>();
//		
//		Stack<String> stack = new Stack<>();
//		stack.push("GO:0008150");
//		while(!stack.isEmpty()) {
//			String s = stack.pop();
//			List<String> childs = new ArrayList<String>(go.allStringtoInfoTerm.get(s).is_a.childrens);
//			childs.retainAll(stockTerm);
//			Set<String> finalChilds = new HashSet<String>();
//			for(String c:childs) {
//				List<String> cc = new ArrayList<String>(go.allStringtoInfoTerm.get(c).is_a.childrens);
//				cc.retainAll(stockTerm);
//				if(cc.size()==1) {
//				while(cc.size()==1) {
//					c = cc.get(0);
//					cc = new ArrayList<String>(go.allStringtoInfoTerm.get(c).is_a.childrens);
//					cc.retainAll(stockTerm);
//					
//				}
//				}
//
//				stack.push(c);
//				finalChilds.add(c);
//	
//			}
//			map.put(s, new HashSet<>(finalChilds));
//		}
//		
//		
//		Map<String,List<String>> map2 = new HashMap<>();
//		
//		for(String k : map.keySet()) {
//			map2.put(k, new ArrayList<>(map.get(k)));
//		}
//		
//		return map2;
//	}
	
	private  String mica(InfoTerm t,List<String> term,int ic) {


		double icMax = 0;
		String parentICMax = t.top;

		for(String a : t.is_a.parents){
			InfoTerm ta = go.allStringtoInfoTerm.get(a);
			if(term.contains(a)) {
				parentICMax = ta.id; // Only when exist a element in the bag of term.
				break;
			}
			if(ta.ICs.get(ic)>icMax) {
				icMax = ta.ICs.get(ic);
				parentICMax = ta.id;
			}

		}
		return parentICMax;


	}

	
	
	
	
	
	
	

}
