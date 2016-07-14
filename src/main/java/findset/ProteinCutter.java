package findset;

import java.util.ArrayList;

/*
 * Calculate the number of peptide, where the constraints are given.
 * 
 * 2016-04-05
 * 
 * @author : Jonghun Park
 * 
 */
public class ProteinCutter {
  
  /*
   * find fully tryptic peptide sequences
   * 
   * @param a protein sequence
   * @param size of miss cleavage
   * @param minimum peptide length
   *
   * @return a set of peptide sequences (considered the given constraints)
   */
  public ArrayList<String> findFullyTrypticPeptideSequences(String proteinSeq, int missCleavageSize, int minPeptideLength){
    
    ArrayList<String> peptideSequences = new ArrayList<String>();
    int missedCleavageCount = 0;
    int startIndex = 0;
    int endIndex   = 0;
  
    //for each index of the given peptide sequence
    for (int i = 0; i < proteinSeq.length(); i++){
      
      //boundary
      if ( i == proteinSeq.length() - 1 ){
        endIndex = i;
        
        if (missedCleavageCount == missCleavageSize){
          if (endIndex - startIndex + 1 >= minPeptideLength){
            peptideSequences.add(proteinSeq.substring(startIndex, endIndex + 1));
          }
        }
        return peptideSequences;
      }
       
      //Tryptic-enzymes cut the right next of K or R
      if (proteinSeq.charAt(i) == 'K' || proteinSeq.charAt(i) == 'R'){
        endIndex = i;
        
        if (missedCleavageCount == missCleavageSize){
          if ( endIndex - startIndex + 1 >= minPeptideLength){
            peptideSequences.add(proteinSeq.substring(startIndex, endIndex + 1));
          }
          missedCleavageCount = 0;
          startIndex = endIndex + 1;
        }
        else{
          missedCleavageCount++;
        }
      }
    }
    
    return peptideSequences;
  }
  
  public ArrayList<String> findFullyTrypticPeptideSequences_new(String proteinSeq, int missCleavageSize, int minPeptideLength){
    ArrayList<String> fullyTrypticSequences = new ArrayList<String>();
    ArrayList<Integer> trypticIndices = new ArrayList<Integer>();
    char[] proteinSequence = proteinSeq.toCharArray();
    
    //Scan all tryptic indices to add their index.
    trypticIndices.add(0); //Protein N-term
    for (int i = 0; i < proteinSequence.length; i++) {
      if (proteinSequence[i] == 'K' || proteinSequence[i] == 'R'){
        trypticIndices.add(i + 1);
      }
    }
    trypticIndices.add(proteinSequence.length); //Protein C-term
    
    String peptideSeq = "";
    for (int begin = 0; begin < trypticIndices.size() - 1 - missCleavageSize; begin++) {
      int end = begin + 1 + missCleavageSize; //begin + 1 means no missCleavage allowed

      int beginIndex = trypticIndices.get(begin);
      int endIndex = trypticIndices.get(end);
        
      peptideSeq = proteinSeq.substring(beginIndex, endIndex);
      
      if (peptideSeq.length() >= minPeptideLength) {
        fullyTrypticSequences.add(peptideSeq);
      }
    }
    
    return fullyTrypticSequences;
  }
}
  
