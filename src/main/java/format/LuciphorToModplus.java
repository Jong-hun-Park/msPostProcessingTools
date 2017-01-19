package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import result.ModPlusResult;
import com.google.common.hash.*;
/*
 * assign luciphor result to modplus search result
 * 
 * @author Jonghun Park 2016.05.20
 */

public class LuciphorToModplus {
  // result file delimeters
  static final String MODPLUS_RESULT_DELIMETER = "\t";
  static final String LUCIPHOR_RESULT_DELIMETER = "\t";
  private static final boolean OVERWRITE_ONLY_NON_ZERO_DELTA_SCORE = false;
  static int changedSiteCount = 0;
  static int changedSiteAndZeroDeltaScoreCount = 0;
  static int zeroDeltaScoreCount = 0;
  static int mightBeChangedSiteCount = 0;

  public static void main(String[] args) {
    
    String rootPath = "./repo/LuciphorToModplus/modplus/GBM_1109_set6_fixed/";

    String luciphorResultFile = rootPath + "luciphor_results.201611월10-11_20_37_set6_nTermFixed"
        + ".tsv";
    String modoplusResultFile = rootPath + "[set6]3rd_MODplus_TMT_Title"
        + ".txt";

    try {
      assignLuciphorToModplus(luciphorResultFile, modoplusResultFile);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }

  }


  /*
   * compare luciphor result and modplus result, and if any change has occured in phospho
   * modification site, assign the newly localized site to modplus result. In other words, just
   * apply luciphor result to modplus result.
   * 
   * @param luciphor result file name
   * 
   * @param modplus result file name
   * 
   * @throw IOException
   * 
   * @throw IllegalArgumentException
   */
  private static void assignLuciphorToModplus(String luciphorResultFile, String modplusResultFile)
      throws IOException, IllegalArgumentException {

//    String outputFile = modplusResultFile.substring(0, modplusResultFile.lastIndexOf("."))
//        + "_phosphoLocalized_notZeroDelta.tsv";
    String outputFile = modplusResultFile.substring(0, modplusResultFile.lastIndexOf("."))
        + "_localizedByLuciphor.tsv";

    BufferedReader luciphorReader = new BufferedReader(new FileReader(luciphorResultFile));
    BufferedReader modplusReader = new BufferedReader(new FileReader(modplusResultFile));

    BufferedWriter modplusWriter = new BufferedWriter(new FileWriter(outputFile));
    PrintWriter log = new PrintWriter(outputFile + "_log_notZeroDelta" + ".txt");
    
    PrintWriter nonZeroDeltaPsmWriter = new PrintWriter(modplusResultFile.substring(0, modplusResultFile.lastIndexOf(".")) + "_nonZeroDeltaPsms.tsv");

    // Load ModPlus result File
    String line = modplusReader.readLine(); // Read header
    String deltaScoreAddedHeader = line + "\t" + 
                                   "Luciphor1" + "\t" +
                                   "Luciphor2" + "\t" +
                                   "DeltaScore"+ "\n"; // new result column for luciphor
                                                               // delta score.
    modplusWriter.write(deltaScoreAddedHeader); // Write header

    // Modplus result file columns
    String spectrumFile = "";
    String index = "";
    String observedMW = "";
    String charge = "";
    String calculatedMW = "";
    String deltaMass = "";
    String score = "";
    String probability = "";
    String peptideSequence = "";
    String protein = "";
    String modification = "";
    String scanNum = "";
    String deltaScore = ""; // luciphor delta score.
    String title = "";

    ArrayList<String> modplusFileNameList = new ArrayList<String>(); // to keep order
    HashMap<String, ModPlusResult> modplusResultHM = new HashMap<String, ModPlusResult>();

    while ((line = modplusReader.readLine()) != null) {
      String[] splitedResult = line.split(MODPLUS_RESULT_DELIMETER);

      assert (splitedResult.length == 12) : "mod plus reulst format has 12 columns";

      spectrumFile = splitedResult[0];
      index = splitedResult[1];
      observedMW = splitedResult[2];
      charge = splitedResult[3];
      calculatedMW = splitedResult[4];
      deltaMass = splitedResult[5];
      score = splitedResult[6];
      probability = splitedResult[7];
      peptideSequence = splitedResult[8];
      protein = splitedResult[9];
      modification = splitedResult[10];
//      scanNum = splitedResult[11]; // msgf
//      scanNum = splitedResult[11].split("=")[1]; //modplus
//      scanNum = splitedResult[11].split(" ")[0].split("=")[0]; //msgf merged
//      scanNum = splitedResult[11].split(" ")[0].split("=")[1]; //modplus merged
      scanNum = splitedResult[11].split(" ")[0]; // modplus

      title = splitedResult[11];

      deltaScore = "-";


      ModPlusResult modReulstRow =
          new ModPlusResult(spectrumFile, index, observedMW, charge, calculatedMW, deltaMass, score,
              probability, peptideSequence, protein, modification, scanNum, deltaScore, title);

      modplusFileNameList.add(scanNum); // to keep order
      modplusResultHM.put(scanNum, modReulstRow); // to find result
    }

    /*
     * Load luciphor result file luciphor result column count can differ from the variation of
     * parameters, so first check the count of result column
     */

    // luciphor result columns
    String specId = "";
    String predictedPep1 = "";
    String predictedPep2 = "";
    String luciphorDeltaScore = "";

    line = luciphorReader.readLine(); // header
    nonZeroDeltaPsmWriter.write(line + "\n");
    
    while ((line = luciphorReader.readLine()) != null) {
      String[] splitedResult = line.split(LUCIPHOR_RESULT_DELIMETER);

      assert splitedResult.length == 12 : "should be 12 columns";
      if (splitedResult.length != 12) {
        throw new IllegalArgumentException("Unappropriate Luciphor Result Format");
      }

      specId = splitedResult[0];
      predictedPep1 = splitedResult[2];
      predictedPep2 = splitedResult[3];
      luciphorDeltaScore = splitedResult[7];


      // modplus scanNum is equal to luciphor result specId column
      if (modplusResultHM.containsKey(specId)) {
        //Format changed 16.11.08. modplus features, luciphor1, luciphor2, deltaScore
        {
          ModPlusResult modpResult = modplusResultHM.get(specId);
          
          modpResult.luciphorSeq1 = getModFormatFromLuciphorPep(predictedPep1); 
          modpResult.luciphorSeq2 = getModFormatFromLuciphorPep(predictedPep2);
          modpResult.deltaScore = luciphorDeltaScore;
        }
        
        
        //Overwrite mopdlus peptide result to luciphor 1st ranked peptide, acrroding to its deltaScore (optional)
        overwriteModplusResult(nonZeroDeltaPsmWriter, line, modplusResultHM, specId, predictedPep1, luciphorDeltaScore);
        
      } else {
        System.err.println("there is a not matched luciphor result");
        System.err.println(specId);
        System.exit(-1);
      }
    } // luciphor loading and change phospho site.

    // write changed output.
    for (String orderedFileName : modplusFileNameList) {
      ModPlusResult result = modplusResultHM.get(orderedFileName);

      modplusWriter.write(result.spectrumFile + "\t" + result.index + "\t" + result.observedMW
          + "\t" + result.charge + "\t" + result.calculatedMW + "\t" + result.deltaMass + "\t"
          + result.score + "\t" + result.probability + "\t" + result.peptideSequence + "\t"
          + result.protein + "\t" + result.modification + "\t" + result.title + "\t"
          + result.luciphorSeq1 + "\t" + result.luciphorSeq2 + "\t"
          + result.deltaScore + "\n");
    }

    System.out.println("The number of changed phospho site among not zero delta score : " + changedSiteCount);
    System.out.println("The number of zero delta score count among phospho site changed PSMs  : "
                + changedSiteAndZeroDeltaScoreCount);
    
    System.out.println("The number of zero delta score count : " + zeroDeltaScoreCount);
    System.out.println("The number of (might have been changed) phospho site among zero delta score : " + mightBeChangedSiteCount);
    
    log.println("The number of changed phospho site among not zero delta score : " + changedSiteCount);
    log.println("The number of zero delta score count among phospho site changed PSMs  : "
                + changedSiteAndZeroDeltaScoreCount);
    
    log.println("The number of zero delta score count : " + zeroDeltaScoreCount);
    log.println("The number of (might have been changed) phospho site among zero delta score : " + mightBeChangedSiteCount);

    luciphorReader.close();
    modplusReader.close();
    modplusWriter.close();
    log.close();
    nonZeroDeltaPsmWriter.close();
  }

  private static void overwriteModplusResult(PrintWriter nonZeroDeltaPsmWriter, String line,
      HashMap<String, ModPlusResult> modplusResultHM, String specId, String predictedPep1,
      String luciphorDeltaScore) {
    
    if (OVERWRITE_ONLY_NON_ZERO_DELTA_SCORE) {
      if (Float.parseFloat(luciphorDeltaScore) != 0.0) {
        changeModSite(nonZeroDeltaPsmWriter, line, modplusResultHM, specId, predictedPep1,
            luciphorDeltaScore);
      }
      else{ //if delta score is zero, do not change the site, only write deltaScore
        zeroDeltaScoreCount++;
        ModPlusResult modpResult = modplusResultHM.get(specId);
        
        // check if the phospho location is changed or not.
        ArrayList<Integer> modplusPhosphoSite  = ModPlusResult.getPhosphoSite(modpResult);
        ArrayList<Integer> luciphorPhosphoSite = getPhosphoSite(predictedPep1);
        
        assert modplusPhosphoSite.size() == luciphorPhosphoSite.size() : "shold be same";
        
        int sameSiteCount = countSamePhosphoSite(modplusPhosphoSite, luciphorPhosphoSite);
        
        // at least one site is changed, count possible change count
        if (sameSiteCount != modplusPhosphoSite.size()) {
          mightBeChangedSiteCount++;
        }
        
        modpResult.deltaScore = luciphorDeltaScore;
        modplusResultHM.put(specId, modpResult);
      }
    }
    // always apply the luciphor's localization result 
    else {
      changeModSite(nonZeroDeltaPsmWriter, line, modplusResultHM, specId, predictedPep1,
          luciphorDeltaScore);      
    }
  }


  private static void changeModSite(PrintWriter nonZeroDeltaPsmWriter, String line,
      HashMap<String, ModPlusResult> modplusResultHM, String specId, String predictedPep1,
      String luciphorDeltaScore) {
    ModPlusResult modpResult = modplusResultHM.get(specId);
    
    // check if the phospho location is changed or not.
    ArrayList<Integer> modplusPhosphoSite  = ModPlusResult.getPhosphoSite(modpResult);
    ArrayList<Integer> luciphorPhosphoSite = getPhosphoSite(predictedPep1);
    
    assert modplusPhosphoSite.size() == luciphorPhosphoSite.size() : "shold be same";
    
    int sameSiteCount = countSamePhosphoSite(modplusPhosphoSite, luciphorPhosphoSite);
    
    // at least one site is changed, so change phospho site
    if (sameSiteCount != modplusPhosphoSite.size()) {
      modpResult = ModPlusResult.changePhosphoSite(modpResult, luciphorPhosphoSite);
      changedSiteCount++;
      
      //write the changed psm to another file.
      nonZeroDeltaPsmWriter.write(line + "\n");
      
    }
    modpResult.deltaScore = luciphorDeltaScore;
    modplusResultHM.put(specId, modpResult);
  }

  private static String getModFormatFromLuciphorPep(String predictedPep1) {
    String modResidueAndIndexDictionary = "";
    for (int i = 0; i < predictedPep1.length(); i++) {
      char AA = predictedPep1.charAt(i);
      if (AA == 's' || AA == 't' || AA == 'y') { //phosphorylated site
        modResidueAndIndexDictionary += Character.toUpperCase(AA) + Integer.toString(i) + " "; //one base.
      }
    }
        
    return modResidueAndIndexDictionary.substring(0, modResidueAndIndexDictionary.length() - 1); // delete the last space
  }

  private static int countSamePhosphoSite(ArrayList<Integer> modplusPhosphoSite,
      ArrayList<Integer> luciphorPhosphoSite) {
    int sameSiteCount = 0;
    // count the number of same phospho site
    for (int i = 0; i < modplusPhosphoSite.size(); i++) {
      if (modplusPhosphoSite.get(i) == luciphorPhosphoSite.get(i)) {
        sameSiteCount++;
      }
    }
    return sameSiteCount;
  }

  private static ArrayList<Integer> getPhosphoSite(String luciphorPepSeq) {
    ArrayList<Integer> result = new ArrayList<Integer>();
    // phospho count.
    for (int i = 0; i < luciphorPepSeq.length(); i++) {
      if (   (luciphorPepSeq.charAt(i) == 's') 
          || (luciphorPepSeq.charAt(i) == 't')
          || (luciphorPepSeq.charAt(i) == 'y')) {
        result.add(i); 
      }
    }
    return result;
  }
}
