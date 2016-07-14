package findset;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class ProteinCutterTest {
  ProteinCutter proCutter;
  
  @Before
  public void setUp() throws Exception {
    proCutter = new ProteinCutter();
  }

  @Test
  public void testFindFullyTrypticPeptideSequences_minLength() {
    HashSet<String> peptideSequenceSet = new HashSet<String>();
    
    int missCleavageSize = 2;
    int minPeptideLength = 8;
    String proteinSeq  = "AAAAAAAR"
                       + "ABCDDDDR"
                       + "R";
    
    for (int i = 0; i <= missCleavageSize; i ++){
      peptideSequenceSet.addAll(proCutter.findFullyTrypticPeptideSequences(proteinSeq, i, minPeptideLength));
      System.out.println(peptideSequenceSet);
    }
    
    
    int expected = 5;
    int actual = peptideSequenceSet.size();
    
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testFindFullyTrypticPeptideSequences_allTryptic() {
    HashSet<String> peptideSequenceSet = new HashSet<String>();
    
    int missCleavageSize = 2;
    int minPeptideLength = 1;
    String proteinSeq  = "KRKRKR";
    
    for (int i = 0; i <= missCleavageSize; i ++){
      peptideSequenceSet.addAll(proCutter.findFullyTrypticPeptideSequences(proteinSeq, i, minPeptideLength));
      System.out.println(peptideSequenceSet);
    }
    
    
    int expected = 6;
    int actual = peptideSequenceSet.size();
    
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testFindFullyTrypticPeptideSequences_Cterm() {
    HashSet<String> peptideSequenceSet = new HashSet<String>();
    
    int missCleavageSize = 2;
    int minPeptideLength = 1;
    String proteinSeq  = "ARBRC";
    
    for (int i = 0; i <= missCleavageSize; i ++){
      peptideSequenceSet.addAll(proCutter.findFullyTrypticPeptideSequences(proteinSeq, i, minPeptideLength));
      System.out.println(peptideSequenceSet);
    }
    
    
    int expected = 6;
    int actual = peptideSequenceSet.size();
    
    Assert.assertEquals(expected, actual);
  }

}
