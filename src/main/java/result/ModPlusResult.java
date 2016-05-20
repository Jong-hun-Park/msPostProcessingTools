package result;

import java.util.ArrayList;

// TODO: make a abstract class result file scan data

public class ModPlusResult {
  public String spectrumFile;
  public int index;
  public float observedMW;
  public int charge;
  public float calculatedMW;
  public float deltaMass;
  public int score;
  public double probability;
  public String peptideSequence;
  public String protein;
  public String modification;
  public String scanNum;
  
  public static String phosphoMass = "+79.966";

  public ModPlusResult(String spectrumFile, int index, float observedMW, int charge,
      float calculatedMW, float deltaMass, int score, double probability, String peptideSequence,
      String protein, String modification, String scanNum) {
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
    
    String modName = "";
    String modSite = "";
    
    luciphorIndex = 0;
    
    for (String mod : mods){
      String[] splited = mod.split("\\(");
      
      modName = splited[0];
      modSite = splited[1].substring(1, splited[1].length() - 1); // check
      
      if (modName.equals("Phospho")){ //change it
        modSite = luciphorPhosphoSite.get(luciphorIndex).toString();
        luciphorIndex++;
      }
      
      String changedMod = modName
                        + "("
                        + getStripSeq(result.peptideSequence).charAt(Integer.parseInt(modSite) - 1)
                        + modSite
                        + ")";
      
      changedMods.add(changedMod);
    }
    
    String changedModiciationCol = "";
    for(String m : changedMods){
      changedModiciationCol += (m + " ");
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
