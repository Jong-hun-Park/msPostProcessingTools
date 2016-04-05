package proteincuttertest;

import java.lang.Object;
import java.util.HashSet;

import edu.hyu.findpositiveset.ProteinCutter;;

public class ProteinCutterTest {
  
  public static void main (String[] args){
    
    testProteinCutter();
  }
  
  public static void testProteinCutter(){
    
    HashSet<String> peptideSequenceSet = new HashSet();
    
    int missCleavageSize = 2;
    int minPeptideLength = 8;
    String proteinSeq = "AAAAAAAR"
                      + "AAAAAAAR"
                      + "AAAAAAAR"
                      + "AAAAAAAR"
                      + "AAAAAAR"
                      + "AAAAAAR";
    
    ProteinCutter proCutter = new ProteinCutter();
    
    for (int i = 0; i <= missCleavageSize; i ++){
      peptideSequenceSet.addAll(proCutter.findFullyTrypticPeptideSequences(proteinSeq, i, minPeptideLength));
      
    }
    
    System.out.println(peptideSequenceSet);
  }
}
