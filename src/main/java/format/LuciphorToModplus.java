package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import result.ModPlusResult;

/*
 * assign luciphor result to modplus search result
 * 
 * @author Jonghun Park 2016.05.20
 */

public class LuciphorToModplus {
  // result file delimeters
  static final String MODPLUS_RESULT_DELIMETER = "\t";
  static final String LUCIPHOR_RESULT_DELIMETER = "\t";
  static int siteChangedCount = 0;
  
  public static void main(String[] args) {

    String luciphorResultFile = "./repo/LuciphorToModplus/modplus/GBM0606_1set/luciphor_results_modplus_20160606.tsv";
    String modoplusResultFile = "./repo/LuciphorToModplus/modplus/GBM0606_1set/3rd_MODplus_TMT_TITLE.txt";

    try {
      assignLuciphorToModplus(luciphorResultFile, modoplusResultFile);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (IllegalArgumentException ex){
      System.err.println(ex.getMessage());
    } 
    
  }
  
  /*
   * compare luciphor result and modplus result, 
   * and if any change has occured in phospho modification site,
   * assign the newly localized site to modplus result.
   * In other words, just apply luciphor result to modplus result.
   * 
   * @param luciphor result file name
   * @param modplus result file name
   * @throw IOException
   * @throw IllegalArgumentException
   */
  private static void assignLuciphorToModplus(String luciphorResultFile,
                                              String modplusResultFile) 
                                              throws IOException, 
                                                     IllegalArgumentException 
                                                     {
    
    String outputFile = modplusResultFile.substring(0, modplusResultFile.lastIndexOf(".")) + 
                                                    "_phosphoLocalized.tsv";
    
    BufferedReader luciphorReader = new BufferedReader(new FileReader(luciphorResultFile));
    BufferedReader modplusReader = new BufferedReader(new FileReader(modplusResultFile));
    
    BufferedWriter modplusWriter = new BufferedWriter(new FileWriter(outputFile));
    PrintWriter log = new PrintWriter(outputFile + "_log" + ".txt");
    
    // Load ModPlus result File
    String line = modplusReader.readLine(); // Read header
    String deltaScoreAddedHeader = line + "\t" + "DeltaScore"; // new result column for luciphor delta score.
    modplusWriter.write(deltaScoreAddedHeader + "\n");       // Write header
    
    // Modplus result file columns
    String spectrumFile     = "";
    String index            = "";
    String observedMW       = "";
    String charge           = "";
    String calculatedMW     = "";
    String deltaMass        = "";
    String score            = "";
    String probability      = "";
    String peptideSequence  = "";
    String protein          = "";
    String modification     = "";
    String scanNum          = "";
    String deltaScore       = ""; //luciphor delta score.
    
    ArrayList<String> modplusFileNameList = new ArrayList<String>(); // to keep order
    HashMap<String, ModPlusResult> modplusResultHM = new HashMap<String, ModPlusResult>();
    
    while ((line = modplusReader.readLine()) != null){
      String[] splitedResult = line.split(MODPLUS_RESULT_DELIMETER);
      
      assert (splitedResult.length == 12): "mod plus reulst format has 12 columns";
      
      spectrumFile     = splitedResult[0];
      index            = splitedResult[1];
      observedMW       = splitedResult[2];
      charge           = splitedResult[3];
      calculatedMW     = splitedResult[4];
      deltaMass        = splitedResult[5];
      score            = splitedResult[6];
      probability      = splitedResult[7];
      peptideSequence  = splitedResult[8];
      protein          = splitedResult[9];
      modification     = splitedResult[10];
//      scanNum          = splitedResult[11]; //msgf
      scanNum          = splitedResult[11].split("=")[1]; //modplus
      deltaScore       = "";
      
      
      ModPlusResult modReulstRow = new ModPlusResult( spectrumFile, 
                                                      index, 
                                                      observedMW,
                                                      charge,
                                                      calculatedMW,
                                                      deltaMass,
                                                      score,
                                                      probability,
                                                      peptideSequence,
                                                      protein,
                                                      modification,
                                                      scanNum,
                                                      deltaScore);
                                                            
      modplusFileNameList.add(scanNum); // to keep order
      modplusResultHM.put(scanNum, modReulstRow); //to find result
    }
    
    
    /*
     * Load luciphor result file
     * luciphor result column count can differ from the variation of parameters,
     * so first check the count of result column
     */
    
    // luciphor result columns
    String specId = "";
    String predictedPep1 = "";
    String luciphorDeltaScore = "";
    
    line = luciphorReader.readLine(); //header
    
    while ((line = luciphorReader.readLine()) != null){
      String[] splitedResult = line.split(LUCIPHOR_RESULT_DELIMETER);
      
      assert splitedResult.length == 12: "should be 12 columns";
      if (splitedResult.length != 12){
        throw new IllegalArgumentException("Luciphor Result Format is different");
      }
      
      specId = splitedResult[0];
      predictedPep1 = splitedResult[2];
      luciphorDeltaScore = splitedResult[7];
      
      
      // modplus scanNum is equal to luciphor result specId column
      if (modplusResultHM.containsKey(specId)){
        ModPlusResult modpResult = modplusResultHM.get(specId);
        
        // check if the phospho location is changed or not.
        ArrayList<Integer> modplusPhosphoSite = ModPlusResult
                                                .getPhosphoSite(modpResult);
        ArrayList<Integer> luciphorPhosphoSite = getPhosphoSite(predictedPep1);
        
        assert modplusPhosphoSite.size() == luciphorPhosphoSite.size() : "shold be same";
        
        int sameSiteCount = 0;
        
        // count the number of same phospho site
        for (int i = 0; i < modplusPhosphoSite.size(); i++){
          if (modplusPhosphoSite.get(i) == luciphorPhosphoSite.get(i)){
            sameSiteCount ++;
          }
        }
        // at least one site is changed, so change phospho site
        if (sameSiteCount != modplusPhosphoSite.size()){ 
          modpResult = ModPlusResult.changePhosphoSite(modpResult, luciphorPhosphoSite);
          siteChangedCount++;
        }
        modpResult.deltaScore = luciphorDeltaScore;
        modplusResultHM.put(specId, modpResult);
      }
      else{
        System.err.println("there is a not matched luciphor result");
        System.exit(-1);
      }
    } //luciphor loading and change phospho site.
    
    // write changed output.
    for (String orderedFileName : modplusFileNameList){
      ModPlusResult result = modplusResultHM.get(orderedFileName);
      
      modplusWriter.write( result.spectrumFile + "\t"
                         + result.index  + "\t" 
                         + result.observedMW  + "\t"
                         + result.charge  + "\t"
                         + result.calculatedMW  + "\t"
                         + result.deltaMass  + "\t"
                         + result.score  + "\t"
                         + result.probability  + "\t"          
                         + result.peptideSequence  + "\t"
                         + result.protein  + "\t"
                         + result.modification  + "\t"
                         + result.scanNum  + "\t"
                         + result.deltaScore + "\n"
                         );
    }
    
    System.out.println("The number of changed phospho site  : " + siteChangedCount);
    log.println("The number of changed phospho site  : " + siteChangedCount);
    
    luciphorReader.close();
    modplusReader.close();
    modplusWriter.close();
    log.close();
  }

  private static ArrayList<Integer> getPhosphoSite(String luciphorPepSeq) {
    ArrayList<Integer> result = new ArrayList<Integer>();
    //phospho count. 
    for (int i = 0; i < luciphorPepSeq.length(); i++){
      if (  (luciphorPepSeq.charAt(i) == 's')
          ||(luciphorPepSeq.charAt(i) == 't')
          ||(luciphorPepSeq.charAt(i) == 'y') ){
        result.add(i + 1); // zero base (luciphor) to one base (modplus)
      }
    }
    return result;
  }
}
