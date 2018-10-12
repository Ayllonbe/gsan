package gsan.distribution.algorihms.astar;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;

import gsan.distribution.gsan_api.ontology.InfoTerm;

import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;

public class AstarSearchAlgo{
	
	
	public static List<InfoTerm> printPath(InfoTerm target){
		List<InfoTerm> path = new ArrayList<InfoTerm>();

		for(InfoTerm InfoTerm = target; InfoTerm!=null; InfoTerm = InfoTerm.next){
			path.add(InfoTerm);
		}

		Collections.reverse(path);

		return path;
	}

	public static List<InfoTerm> printValor(InfoTerm target){
		List<InfoTerm> path = new ArrayList<InfoTerm>();

		for(InfoTerm InfoTerm = target; InfoTerm!=null; InfoTerm = InfoTerm.next){
			//System.out.println(InfoTerm.toString());
			path.add(InfoTerm);
		}

		Collections.reverse(path);

		return path;
	}

	public static void AstarSearch(InfoTerm source, InfoTerm goal){

		Set<InfoTerm> explored = new HashSet<InfoTerm>();

		PriorityQueue<InfoTerm> queue = new PriorityQueue<InfoTerm>(20, 
				new Comparator<InfoTerm>(){
			//override compare method
			public int compare(InfoTerm i, InfoTerm j){
				if(i.f_scores > j.f_scores){
					return 1;
				}

				else if (i.f_scores < j.f_scores){
					return -1;
				}

				else{
					return 0;
				}
			}

		}
				);

		//cost from start
		source.g_scores = 0;

		queue.add(source);

		boolean found = false;

		while((!queue.isEmpty())&&(!found)){

			//the InfoTerm in having the lowest f_score value
			InfoTerm current = queue.poll();


			explored.add(current);

			//goal found
			if(current.id.equals(goal.id)){
				found = true;
			}

			//check every child of current InfoTerm
			for(Edges e : current.adjacencies){


				InfoTerm child = e.target;
				double cost = e.cost;
				double temp_g_scores = current.g_scores + cost;
				double temp_f_scores = temp_g_scores + child.h_scores;


				/*if child InfoTerm has been evaluated and 
                                the newer f_score is higher, skip*/

				if((explored.contains(child)) && 
						(temp_f_scores >= child.f_scores)){
					continue;
				}

				/*else if child InfoTerm is not in queue or 
                                newer f_score is lower*/

				else if((!queue.contains(child)) || 
						(temp_f_scores < child.f_scores)){

					child.next = current;
					child.g_scores = temp_g_scores;
					child.f_scores = temp_f_scores;

					if(queue.contains(child)){
						queue.remove(child);
					}

					queue.add(child);

				}

			}

		}

	}


	}






