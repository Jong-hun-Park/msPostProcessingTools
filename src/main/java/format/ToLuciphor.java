package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Change MODplus search result format to LuciPHOr input format.
 * 
 * Format Information : http://luciphor2.sourceforge.net/luciphorInfo.html
 *
 * Jonghun Park
 * 2016.04.28
 */

public class ToLuciphor {
  
  static final boolean isMODplusFormat = true; //this only effect to the peptide format Pre.Seq.Post
                                              //in GBM, it's always true.
  static final String RESULT_FILE_DELIMITER = "\t";
  static boolean isMsgfResult;
  static boolean hasNtermTMT = true; //if this variable is true, add the TMT as a variable
                                     //since luciphor2 doensn't support n-term fixed modification.
  
  public static void main(String[] args) {
    String resultFileName = "[set7]MODplus_1stSelected"
                             + ".txt";
    System.out.println("resultFile: " + resultFileName);
    
    try {
      changeFormatForLuciphor(resultFileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println("done");
  }

  /*
   * Change format to LuciPHOr Format
   * 
   * MODplus Format is like
   * SpectrumFile \t Index \t ObservedMW \t Charge \t CalculatedMW \t DeltaMass \t Score \t
   * Probability(EValue) \t Peptide \t Protein \t Modification \t ScanNum
   * 
   * LuciPHOr Format is like
   * srcFile \t scanNum \t charge \t PSMscore \t peptide \t modSites
   * 
   * @param resultFileName
   */
  private static void changeFormatForLuciphor(String resultFileName) throws IOException {
    String outputFileName = resultFileName.substring(0, resultFileName.lastIndexOf(".")) + "_Luciphor_formated.tsv";
    
    // Load File
    BufferedReader resultFile = new BufferedReader(new FileReader(resultFileName));
    BufferedWriter outputFile = new BufferedWriter( new PrintWriter(outputFileName));
    
    //Initialization
    String spectrumFile     = "";
    int index               = -1;
    float observedMW        = -1;
    int charge              = -1;
    float calculatedMW      = -1;
    float deltaMass         = -1;
    int score               = -1;
    double probability      = -1;
    String peptideSequence  = "";
    String protein          = "";
    String modification     = "";
    String scanNum          = "";
    
    double minusLogEvalue = -1;
    
    // Result File Header
    String psmLine = resultFile.readLine();
    // Write Header
    outputFile.write("srcFile" + "\t" + 
                     "scanNum" + "\t" +
                     "charge"  + "\t" + 
                     "PSMscore"+ "\t" +
                     "peptide" + "\t" +
                     "modSites"+ "\n");
    
    String[] psmColumn = null;
    String stipPeptideSeq = "";
    String modSite = "";
    
    while ((psmLine = resultFile.readLine()) != null) {
      
      psmColumn = psmLine.trim().split(RESULT_FILE_DELIMITER);
      
      String[] splitedSpectrumFile = psmColumn[0].split("/"); // in case of psmColumn has a file
                                                              // path not just a file name
      spectrumFile = splitedSpectrumFile[splitedSpectrumFile.length - 1]; // last index should be
                                                                          // the file name
      index            = Integer.parseInt(psmColumn[1]);
      observedMW       = Float.parseFloat(psmColumn[2]);
      charge           = Integer.parseInt(psmColumn[3]);
      calculatedMW     = Float.parseFloat(psmColumn[4]);
      deltaMass        = Float.parseFloat(psmColumn[5]);
      score            = Integer.parseInt(psmColumn[6]);
      probability      = Double.parseDouble(psmColumn[7]);
      
      minusLogEvalue = -Math.log(probability);
      
      peptideSequence  = psmColumn[8];
      protein          = psmColumn[9];
      modification     = psmColumn[10];
      scanNum          = psmColumn[11];
      
      stipPeptideSeq = getStripPeptideSeq(peptideSequence);
      modSite = getModSite(peptideSequence, modification);
      
      // When scanNum is written like the TITLE = .xxx.xxx.
      String[] splitedScanNum = scanNum.split("\\.");
      scanNum = splitedScanNum[1];
      
      if (isMsgfResult(resultFileName)) {        
        outputFile.write(spectrumFile   + "\t" +
            scanNum        + "\t" +
            charge         + "\t" +
            minusLogEvalue    + "\t" +
            stipPeptideSeq + "\t" +
            modSite        + "\n");
      }
      else { //modplus result
        outputFile.write(spectrumFile   + "\t" +
            scanNum        + "\t" +
            charge         + "\t" +
            probability    + "\t" +
            stipPeptideSeq + "\t" +
            modSite        + "\n");
      }
    }
    
    resultFile.close();
    outputFile.close();
  }

  private static boolean isMsgfResult(String resultFileName) {
    return resultFileName.toLowerCase().contains("msgf");
  }

  private static String getStripPeptideSeq(String peptideSequence) {
    StringBuilder stripPeptideSequence = new StringBuilder(peptideSequence.length());
    
    /* 
     * MODplus peptide report
     * index from 2 to (length - 2)
     * Except pre and post (K.SEQUENCE.K)
     */
    if (isMODplusFormat) {
      for (int i = 2; i < peptideSequence.length() - 2; i++) {
        if (Character.isLetter(peptideSequence.charAt(i))) {
          stripPeptideSequence.append(peptideSequence.charAt(i));
        }
      }
    }
    //TODO: other format
    
    return stripPeptideSequence.toString();
  }
  
  private static String getModSite(String peptideSequence, String modification) {
    // When no modification
    if (modification.equals("")) {
      if (hasNtermTMT) {
        //TODO: this is a hack
        return "-100=+229.162932"; //apply N-term TMT fixed modification
      }
      return "";
    }
    
    /* MODplus Modification Column is like
     * Oxidation(M1) Phospho(S10), which has space delimiter
     */
    if (isMODplusFormat) {
      peptideSequence = peptideSequence.substring(2, peptideSequence.length() -2); //K. SEQUENCE . R
    }
    String[] mods = modification.split(" "); 
    
    ArrayList<String> modNameArray = new ArrayList<String>();
    for (String mod : mods){
      String modName = mod.split("\\(")[0];
      modNameArray.add(modName);
    }
    
    String modSiteString = modification.replaceAll("[^0-9]+", " ");
    List<String> modSiteArray = Arrays.asList(modSiteString.trim().split(" "));
        
    String modMassString = peptideSequence.replaceAll("[^0-9\\.\\-\\+]+", " ");
    List<String> modMassArray = Arrays.asList(modMassString.trim().split(" "));
    
    assert modSiteArray.size() == modMassArray.size() : "should be the same modification size";
    assert modSiteArray.size() == modNameArray.size() : "should be the same modification size";
    
    /*
     * LuciPHOr requires comma-delimited. The first residue in a peptide is position 0 and the rest
     * increment from there. For N-terminal or C-terminal modifications use -100 or 100 respectively
     * for the value of 'pos' and then list the mass shift of the terminal modification.
     * 
     * only N-term -100=42.010565 
     * other site 2=160.030649,4=160.030649
     */
    StringBuilder sb = new StringBuilder(6 * modSiteArray.size()); // 6 is for modification digit
                                                                   // (15.995)
    
    if (hasNtermTMT) {
      sb.append("-100=+229.162932,"); //apply N-term fixed modification 
    }
    
    for (int i = 0; i < modSiteArray.size(); i++) {
      int modSite;
      
      if (modNameArray.get(i).equals("De-TMT")) { 
        continue;
      }
      if (mods[i].contains("Nterm")) {
        modSite = -100;
      }
      else{
        modSite = Integer.parseInt(modSiteArray.get(i)) - 1;
      }
      
      sb.append(modSite).append("=").append(modMassArray.get(i));
      
      if (modSiteArray.size() > 1) {
        if (i < modSiteArray.size() - 1) {
          sb.append(",");
        }
      }
    }
    return sb.toString();
  }
    
}
