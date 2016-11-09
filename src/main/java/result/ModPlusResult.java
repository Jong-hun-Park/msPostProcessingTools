package result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

// TODO: make a abstract class result file scan data

public class ModPlusResult {
  public String spectrumFile;
  public String index;
  public String observedMW;
  public String charge;
  public String calculatedMW;
  public String deltaMass;
  public String score;
  public String probability;
  public String peptideSequence;
  public String protein;
  public String modification;
  public String scanNum;
  public String deltaScore; //luciphor deltaScore
  public String title;
  
  public String luciphorSeq1;
  public String luciphorSeq2;
  
  public static String phosphoMass = "+79.966";

  public ModPlusResult(String spectrumFile, String index, String observedMW, String charge,
      String calculatedMW, String deltaMass, String score, String probability, String peptideSequence,
      String protein, String modification, String scanNum, String deltaScore, String title) {
    this.spectrumFile = spectrumFile;
    this.index = index;
    this.observedMW = observedMW;
    this.charge = charge;
    this.calculatedMW = calculatedMW;
    this.deltaMass = deltaMass;
    this.score = score;
    this.probability = probability;
    this.peptideSequence = peptideSequence;
    this.protein = protein;
    this.modification = modification;
    this.scanNum = scanNum;
    this.deltaScore = deltaScore;
    this.title = title;
    
    this.luciphorSeq1 = "-";
    this.luciphorSeq2 = "-";
    
  }


  // Change modplusResult variables.
  // change only the peptide and Modification column.
  public static ModPlusResult changePhosphoSite(ModPlusResult       modpResult, 
                                                ArrayList<Integer>  luciphorPhosphoSite){

    ModPlusResult result = modpResult; //same object but different naming
    
    System.out.println("before  : " + modpResult.peptideSequence);
    System.out.println("before  : " + modpResult.modification);
    
    /*
     * CHANGE PEPTIDE COLUMN
     */
   
    // get the phospho modification mass from modp result.
    String pepSeq = result.peptideSequence;
    int luciphorIndex = 0;
    
    StringBuilder SB = new StringBuilder(pepSeq.length());
    int AAIndex = 0; //amino acid index. (that is the same as strip sequence index)
    boolean isPhosphoMass = false;
    
    for (int i = 2; i < pepSeq.length() - 2; i ++){ // start from 2, cuz it's modplus result
                                                    // e.g.) R.S+79.966VIDPVPAPVGDSHVDGAAK.S
      if (Character.isLetter(pepSeq.charAt(i))){
        isPhosphoMass = false;
        AAIndex++;
        
        if (pepSeq.charAt(i) == 'S'
         || pepSeq.charAt(i) == 'T'
         || pepSeq.charAt(i) == 'Y'){
          
          isPhosphoMass = true;
          if (luciphorPhosphoSite.size() != luciphorIndex){
            if (AAIndex == luciphorPhosphoSite.get(luciphorIndex)){
              luciphorIndex++;
              SB.append(pepSeq.charAt(i))
              .append(phosphoMass);
            }
            else{
              SB.append(pepSeq.charAt(i));
            }
          }
          else{
            SB.append(pepSeq.charAt(i));
          }
          
        }
        else{
          SB.append(pepSeq.charAt(i));
        }
      }
      else{ // +, number, punctuation.
       if (!isPhosphoMass){
         SB.append(pepSeq.charAt(i));
       }
      }
    }
    
    result.peptideSequence = pepSeq.substring(0, 2) 
                           + SB.toString()
                           + pepSeq.substring(pepSeq.lastIndexOf(".") , pepSeq.length()); 
    
    System.out.println("changed : " + result.peptideSequence);
    
    /*
     * CHANGE MODIFICATIN COLUMN
     */
    
    String[] mods = result.modification.split(" "); // modification column delimeter.
    ArrayList<String> changedMods = new ArrayList<String>();
    ArrayList<Integer> siteOfChangedMod = new ArrayList<Integer>(); // sorting chagedMods 
                                                                         // using phospho site with increasing order.
    
    String modName = "";
    String modSite = "";
    
    luciphorIndex = 0;
    
    for (String mod : mods){
      String[] splited = mod.split("\\(");
      
      modName = splited[0]; // e.g.) "Phospho"
      modSite = splited[1].substring(1, splited[1].length() - 1); // e.g.) S, T, Y
      
      if (modName.equals("Phospho")){ // get the modSite from luciphor result cuz it can be changed.
        modSite = luciphorPhosphoSite.get(luciphorIndex).toString();
        luciphorIndex++;
      }
      
      String changedMod = modName
                        + "("
                        + getStripSeq(result.peptideSequence).charAt(Integer.parseInt(modSite) - 1)
                        + modSite
                        + ")";
      
      siteOfChangedMod.add(Integer.parseInt(modSite));
      changedMods.add(changedMod);
      
    }
    
    /**
     * To sort the changedMod by it's modSite,
     * sort the site of site of changed mod and write output by the order
     */
    Collections.sort(siteOfChangedMod);
    
    String changedModiciationCol = "";
    /*
     * there is a bug. (if N-term modification and first residue modification
     * occurs simultaneously, it doesn't work)
     */
    boolean hasNtermMod = false;
    for (int site : siteOfChangedMod){
      if (hasNtermMod && site == 1) {
        continue;
      }
      if (site == 1) { // N-Term
        hasNtermMod = true;
      }
      for(String m : changedMods){
        String[] splited = m.split("\\(");
        modSite = splited[1].substring(1, splited[1].length() - 1);
        int index = Integer.parseInt(modSite);
        
        if (site == index){
          changedModiciationCol += (m + " ");
        }
      }
    }
    
    result.modification = changedModiciationCol;
//    result.modification = changedModiciationCol.substring(0, changedModiciationCol.length() -1);
    
    System.out.println("changed : " + result.modification);
    System.out.println("");

    return result;
  }


  private static String getStripSeq(String peptide) {
    
    StringBuilder SB = new StringBuilder(peptide.length());
    for (int i = 2; i < peptide.length() -2; i++){
      if (Character.isLetter(peptide.charAt(i))){
        SB.append(peptide.charAt(i));
      }
    }
    
    return SB.toString();
  }


  public static ArrayList<Integer> getPhosphoSite(ModPlusResult modPlusResult) {
    ArrayList<Integer> result = new ArrayList<Integer>();

    String[] mods = modPlusResult.modification.split(" "); // modification column delimeter.

    String modName = "";
    String modSite = "";

    // e.g) Phospho(S1)
    for (String mod : mods) {
      String[] splited = mod.split("\\(");

      modName = splited[0];
      modSite = splited[1].substring(1, splited[1].length() - 1); // check

      if (modName.equals("Phospho")) {
        result.add(Integer.parseInt(modSite));
      }
    }

    return result;
  }
}
