package edu.hyu.findset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FindPeptideSet {
  
  static int maxMissCleavage = 2;
  static int minPeptideLength = 8;
  
  public static void main(String[] args) throws IOException {

    String proteinSeq = "";
    int proteinCount = 0; //need to test.
    Set<String> peptideSequenceSet = new HashSet();
    ProteinCutter proCutter = new ProteinCutter();
    
    FileReader fr;
    BufferedReader br;
    String line = "";
    String header = "";
    
    ArrayList<ProteinRef> proteins = new ArrayList<ProteinRef>();
    
    fr = new FileReader("repo/ups48.fasta");
    br = new BufferedReader(fr);
    
    while ((line = br.readLine()) != null){
      if (line.startsWith(">")){
        header = line;
        
        line = br.readLine();
        proteinSeq = line;
        
        proteins.add(new ProteinRef(header, proteinSeq));
      }
    }
    proteinCount = proteins.size();
    
    
    /*
     * read protein sequences from input file.
     */
    
    //TODO: protein seq check? only alphabet. assert
    //String[] proteins = new String[2];
    //proteins[1] = "ABCDASDFKASDFKQSDSADFFKRAFDFASDFKASDFASDFEREASDFRASDF";

    //for each protein sequence
    for (int proteinIndex = 0; proteinIndex < proteinCount; proteinIndex++){
      proteinSeq = proteins.get(proteinIndex).proteinSequence;

      /*
       * for each possible miss cleavage.
       * if maxMissCleavege = 2, we consider all possible cases: missCleavageSize = 0, 1, 2
       */
      for (int missCleavageSize = 0; missCleavageSize <= maxMissCleavage; missCleavageSize++){
        peptideSequenceSet.addAll(proCutter.findFullyTrypticPeptideSequences(proteinSeq, missCleavageSize, minPeptideLength));
      }
    }

    System.out.println("peptide lists: ");
    System.out.println(peptideSequenceSet);
    System.out.println("the number of elements in the Peptide Sequence Set: " + peptideSequenceSet.size());

  }

}
