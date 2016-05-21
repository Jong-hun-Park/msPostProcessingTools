package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
  static final boolean isMODplusFormat = true;
  static final String RESULT_FILE_DELIMITER = "\t";
  
  public static void main(String[] args) {
    String resultFileName = "TMT_3rd_MODPLUS_MERGE_TITLE.txt";
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
    String outputFileName = resultFileName.substring(0, resultFileName.lastIndexOf(".")) + ".Luciphor_formated.tsv";
    
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
      peptideSequence  = psmColumn[8];
      protein          = psmColumn[9];
      modification     = psmColumn[10];
      scanNum          = psmColumn[11];
      
      stipPeptideSeq = getStripPeptideSeq(peptideSequence);
      modSite = getModSite(peptideSequence, modification);
      
      String[] splitedScanNum = scanNum.split("\\.");
      scanNum = splitedScanNum[1];
      
      outputFile.write(spectrumFile   + "\t" +
                       scanNum        + "\t" +
                       charge         + "\t" +
                       probability    + "\t" +
                       stipPeptideSeq + "\t" +
                       modSite        + "\n");
    }
    
    resultFile.close();
    outputFile.close();
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
      return "";
    }
    
    /* MODplus Modification Column is like
     * Oxidation(M1) Phospho(S10), which has space delimiter
     */
    if (isMODplusFormat) {
      peptideSequence = peptideSequence.substring(2, peptideSequence.length() -2); //K. SEQUENCE . R
    }
    String modSiteString = modification.replaceAll("[^0-9]+", " ");
    List<String> modSiteArray = Arrays.asList(modSiteString.trim().split(" "));
        
    String modMassString = peptideSequence.replaceAll("[^0-9\\.\\-\\+]+", " ");
    List<String> modMassArray = Arrays.asList(modMassString.trim().split(" "));
    
    assert modSiteArray.size() == modMassArray.size() : "should be the same modification size";
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
    for (int i = 0; i < modSiteArray.size(); i++){
      int modSite;
      modSite = Integer.parseInt(modSiteArray.get(i)) - 1;
      
      sb.append(modSite).append("=").append(modMassArray.get(i));
      
      if (modSiteArray.size() > 1){
        if (i < modSiteArray.size() - 1){
          sb.append(",");
        }
      }
    }
    return sb.toString();
  }
    
}
