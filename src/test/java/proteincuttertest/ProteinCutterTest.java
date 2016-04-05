package proteincuttertest;

import java.lang.Object;

import edu.hyu.findpositiveset.ProteinCutter;;

public class ProteinCutterTest {
  
  public void main (String args[]){
    
    testProteinCutter();
  }
  
  public void testProteinCutter(){
    ProteinCutter proCutter = new ProteinCutter();
    
    int missCleavageSize = 2;
    int minPeptideLength = 8;
    String proteinSeq = "AAAAAAARAAAAAAARAAAAAAARAAAAAAARAAAAAAA";
    
    proCutter.findFullyTrypticPeptideSequences(proteinSeq, missCleavageSize, minPeptideLength);
  }
}
