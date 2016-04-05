package edu.hyu.findpositiveset;

import java.util.ArrayList;
import java.util.HashSet;

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
  public static ArrayList<String> findFullyTrypticPeptideSequences(String proteinSeq, int missCleavageSize, int minPeptideLength){
    
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
          if (endIndex - startIndex >= minPeptideLength){
            peptideSequences.add(proteinSeq.substring(startIndex, endIndex + 1));
          }
        }
        return peptideSequences;
      }
       
      //Tryptic-enzymes cut the right next of K or R
      if (proteinSeq.charAt(i) == 'K' || proteinSeq.charAt(i) == 'R'){
        endIndex = i;
        
        if (missedCleavageCount == missCleavageSize){
          if ( endIndex - startIndex >= minPeptideLength){
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

}
