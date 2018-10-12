package gsan.distribution.gsan_api.run.representative;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Combination {
 
public static Set<List<String>> combination(Object[]  elements, int K){
	
		Set<List<String>> lint = new HashSet<List<String>>();
        // get the length of the array
        // e.g. for {"A","B","C","D"} => N = 4 
        int N = elements.length;
         
        if(K > N){
            //ystem.out.println("Invalid input, K > N");
            return lint;
        }
        // calculate the possible combinations
        // e.g. c(4,2)
       // c(N,K);
         
        // get the combination by index 
        // e.g. 01 --> AB , 23 --> CD
        int combination[] = new int[K];
         
        // position of current index
        //  if (r = 1)              r*
        //  index ==>        0   |   1   |   2
        //  element ==>      A   |   B   |   C
        int r = 0;      
        int index = 0;
         
        while(r >= 0){
            // possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
            // possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"
             
            // for r = 0 ==> index < (4+ (0 - 2)) = 2
            if(index <= (N + (r - K))){
                    combination[r] = index;
                     
                // if we are at the last position print and increase the index
                if(r == K-1){
 
                    //do something with the combination e.g. add to list or print
                   // print(combination, elements);
                	List<String> li = new ArrayList<String>();
                	for(int c : combination){
                		
                		li.add((String) elements[c]);
                		
                	}
                	lint.add(li);
                	
                	
                    index++;                
                }
                else{
                    // select index for next position
                    index = combination[r]+1;
                    r++;                                        
                }
            }
            else{
                r--;
                if(r > 0)
                    index = combination[r]+1;
                else
                    index = combination[0]+1;   
            }           
        }
        
        return lint;
    }

//public static void main(String[] args){
//    Object[] elements = new Object[] {"1","2","3","4","5","6","7","8","9","10"}; 
//
//    System.out.println(Combination.combination(elements,7));
//}

public static Set<List<String>> ncombination(List<String> children, int ncombi){
    Object[] elements = new Object[children.size()];
    
    for(int i = 0 ; i<children.size();i++)
    	elements[i] = children.get(i);
    
    

   return Combination.combination(elements,ncombi);
}

}