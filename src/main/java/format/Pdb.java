package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * pDB header change
 * e.g.) from B41 to N+T42
 * 
 * Jonghun Park
 * 2016.05.12
 * 
 */
public class Pdb {
  static final String PREFIX_OF_HEADER = ">";
  static final String HEADER_DELIMITER = ";";
  
  public static void main (String[] args){
    String pdbFileName = "./repo/compositeDB/new_30sample_DB/"
                       + "CompositeDB_5381N+5760T_v1.43"
                       + ".fasta";

    try {
      convertPdbHeader(pdbFileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * personalized database (PDB) is a composite DB of four different DB:
   * Expressed DB, Variant DB, Fusion Gene DB, and Contaminants.
   * 
   * @param pdb file name
   * @throw IOException 
   */
  public static void convertPdbHeader (String pdbFileName) throws IOException {
    String outputFileName = pdbFileName.substring(0, pdbFileName.lastIndexOf(".")) + ".typeDivided.fasta";
    
    // Load File
    BufferedReader pdbFile = new BufferedReader(new FileReader(pdbFileName));
    BufferedWriter outputFile = new BufferedWriter( new PrintWriter(outputFileName));
    
    //Initialization
    String line = "";
    
    while ((line = pdbFile.readLine()) != null) {
      
      if (isHeader(line)){
        String pdbHeader = line;
        StringBuilder sb = new StringBuilder(line. length() * 2);
        String convertedHeader = "";
        
        if (line.contains("Contaminant")){
          // Both Contam and Uniprot matching case.
          if(line.contains(HEADER_DELIMITER)){
            String[] contamSplited = line.split(HEADER_DELIMITER);
            
            if (contamSplited.length != 2){
              System.err.println("contam size error :" + contamSplited.length);
            }
            
            sb.append(contamSplited[0])
              .append(";");
            
            // case: P05783_B*B43+B61
            convertedHeader = divideBothType(contamSplited[1]);
            
            sb.append(convertedHeader);
            outputFile.write(sb.toString() + "\n");
            continue;
          }
          else{ //only contaminant case.
            outputFile.write(line + "\n");
            continue;
          }
        }
        
        if (hasMoreThanTwoHeader(pdbHeader)) {
          String[] headers = pdbHeader.split(HEADER_DELIMITER);
          
          for (int i = 0; i < headers.length; i++){
            if (isFromUniprot(headers[i])) { //don't have to convert
              convertedHeader = headers[i]; //not convert
            } else {
              convertedHeader = divideBothType(headers[i]);
            }
            
            if (i == (headers.length - 1)) { // the last header
              sb.append(convertedHeader).append("\n");
            } else {
              sb.append(convertedHeader).append(";");
            }
            
          }
        } 
        else{ // only one header
          if (isFromUniprot(pdbHeader)) { //don't have to convert
            sb.append(pdbHeader).append("\n");
          } else {
            convertedHeader = divideBothType(pdbHeader);
            sb.append(convertedHeader).append("\n");
          }
        }
        outputFile.write(sb.toString());
      }
      else {
        outputFile.write(line + "\n");
      }          
    }
    
    pdbFile.close();
    outputFile.close();
    
    System.out.println("done");
  }
  
  /*
   * Possible cases for a header
   * 
   * case 1) header has just only one variant case
   * >ENST00000013222$SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61 (okay)
   * 
   * case 2) header has one variant and *Code
   * >ENST00000013222$SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61&CodeN_xx (okay)
   * 
   * case 3) header has other mutation
   * >ENST00000013222$SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61&SNV_F54C(chr7#30795436,0,T,G)*Germ*B43+B61
   * 
   * case 4) header has other mutation and &CodeN_xx
   * >ENST00000013222$SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61&SNV_F54C(chr7#30795436,0,T,G)*Germ*B43+B61&CodeN_xx
   * 
   * 
   * @param header 
   */
  private static String divideBothType(String header) {
    assert header.startsWith(PREFIX_OF_HEADER) == false : "should start with >";
    
    StringBuilder sb = new StringBuilder(header.length() * 4);
    
    if (hasOtherMutation(header)) {
            
      String[] separatedHeader = header.split("\\$");
      
      sb.append(separatedHeader[0])
        .append("$");                 //>ENST00000013222$
      
      if (hasAmbiguityCode(header)) {
        // SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61&SNV_F54C(chr7#30795436,0,T,G)*Germ*B43+B61&CodeN_xx
        // now separted like this.
        // SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61
        // SNV_F54C(chr7#30795436,0,T,G)*Germ*B43+B61
        // CodeN_xx
        
        String[] splitedMutation = separatedHeader[1].split("&");
        String[] splitedLastMutation = splitedMutation[splitedMutation.length - 2].split("\\*");
        String lastMutationPair = splitedLastMutation[splitedLastMutation.length - 1];
        String sampleNumber = lastMutationPair.substring(1, splitedLastMutation[splitedLastMutation.length - 1].length());
        
        if (lastMutationPair.substring(0).startsWith("T")
            && !sampleNumber.contains("+")) {
          sampleNumber = String.valueOf(Integer.parseInt(sampleNumber) - 1 );
        }
        
        String[] variantMods = header.substring(header.indexOf("$") + 1, header.length())
                                     .split("&");
        
        //Note that i < variantMods.length - 1 !
        //because the last variantMods[i] is &CODE
        boolean exceptionFlag = false;
        for (int i = 0; i < variantMods.length - 1; i ++){
          String[] sepHeader = variantMods[i].split("\\*");
          String[] tsPairs = sepHeader[sepHeader.length - 1].split("\\+");
          
          /*
           * *******************************************************
           * EXCEPTIONAL CASE (SHOULD BE DELEATED LATER)
           */
          if (tsPairs[0].equals("B") ||
              tsPairs[0].equals("T") ||
              tsPairs[0].equals("N")
              ){
            //append to header except last type sample pairs 
            for (int j = 0 ; j < sepHeader.length - 1; j++){
              sb.append(sepHeader[j] + "*");
            }
            
            if (tsPairs[0].equals("B")){
              tsPairs[0] = tsPairs[0] + sampleNumber; // B43
            }
            else if(tsPairs[0].equals("T")){
              tsPairs[0] = tsPairs[0] + (Integer.parseInt(sampleNumber) + 1); //T44
            }
            else {
              tsPairs[0] = tsPairs[0] + sampleNumber; // N43
            }
                        
            sb.append(convertPair(tsPairs))
              .append("&");
              
            
//            exceptionFlag = true;
            continue;
          }
          
          if (exceptionFlag == true) {
            StringBuilder sb_exception = new StringBuilder(header.length() * 2);
            //append to header except last type sample pairs 
            for (int j = 0 ; j < sepHeader.length - 1; j++){
              sb_exception.append(sepHeader[j] + "*");
            }
            String stringToConcatLater = sb_exception.toString();
            
            String convertedPair = convertPair(tsPairs);
            
            sb.append(convertedPair)
              .append("&")
              .append(stringToConcatLater)
              .append(convertedPair);
            
            break;
          }
          /*
           * *******************************************************
           */
          
          //append to header except last type sample pairs 
          for (int j = 0 ; j < sepHeader.length - 1; j++){
            sb.append(sepHeader[j] + "*");
          }
          
          //now we have
          //>ENST00000013222$SNV_E19G(chr7#30795331,0,A,G)*Germ*
          sb.append(convertPair(tsPairs))
            .append("&");
        }
        sb.append(variantMods[variantMods.length - 1]); //append &Code
        
        
      }
      else { //no ambiguity code case.
        
        String[] splitedMutation = separatedHeader[1].split("&");
        String[] splitedLastMutation = splitedMutation[splitedMutation.length - 1].split("\\*");
        String lastMutationPair = splitedLastMutation[splitedLastMutation.length - 1];
        
        String sampleNumber = lastMutationPair.substring(1, splitedLastMutation[splitedLastMutation.length - 1].length());;
        
        System.out.println(sampleNumber);
        if (lastMutationPair.substring(0).startsWith("T")
            && !sampleNumber.contains("+")) {
          sampleNumber = String.valueOf(Integer.parseInt(sampleNumber) - 1 );
        }
        
        // an example header we have had is like
        // SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61&SNV_F54C(chr7#30795436,0,T,G)*Germ*B43+B61
        
        // now the header has changed like this.
        // SNV_E19G(chr7#30795331,0,A,G)*Germ*B43+B61
        // SNV_F54C(chr7#30795436,0,T,G)*Germ*B43+B61
        String[] variantMods = header.substring(header.indexOf("$") + 1, header.length())
                                     .split("&");
        
        boolean exceptionFlag = false;
        for (int i = 0; i < variantMods.length; i ++){
          String[] sepHeader = variantMods[i].split("\\*");
          String[] tsPairs = sepHeader[sepHeader.length - 1].split("\\+");
          
          /*
           * *******************************************************
           * EXCEPTIONAL CASE (SHOULD BE DELEATED LATER)
           */
          if (tsPairs[0].equals("B") ||
              tsPairs[0].equals("T") ||
              tsPairs[0].equals("N")
              ){
            //append to header except last type sample pairs 
            for (int j = 0 ; j < sepHeader.length - 1; j++){
              sb.append(sepHeader[j] + "*");
            }
            
            if (tsPairs[0].equals("B")){
              tsPairs[0] = tsPairs[0] + sampleNumber; // B43
            }
            else if(tsPairs[0].equals("T")){
              tsPairs[0] = tsPairs[0] + (Integer.parseInt(sampleNumber) + 1); //T44
            }
            else {
              tsPairs[0] = tsPairs[0] + sampleNumber; // N43
            }
            
            sb.append(convertPair(tsPairs))
            .append("&");
            
//            exceptionFlag = true;
            continue;
          }
          
          if (exceptionFlag == true) {
            StringBuilder sb_exception = new StringBuilder(header.length() * 2);
            //append to header except last type sample pairs 
            for (int j = 0 ; j < sepHeader.length - 1; j++){
              sb_exception.append(sepHeader[j] + "*");
            }
            String stringToConcatLater = sb_exception.toString();
            
            String convertedPair = convertPair(tsPairs);
            
            sb.append(convertedPair)
              .append("&")
              .append(stringToConcatLater)
              .append(convertedPair);
            
            break;
          }
          /*
           * *******************************************************
           */
          
          //append to header except last type sample pairs 
          for (int j = 0 ; j < sepHeader.length - 1; j++){
            sb.append(sepHeader[j] + "*");
          }
          
          //now we have
          //>ENST00000013222$SNV_E19G(chr7#30795331,0,A,G)*Germ*
          if (i == (variantMods.length - 1)) { //last so no need $
            sb.append(convertPair(tsPairs));
          }
          else{
            sb.append(convertPair(tsPairs))
              .append("&");
          }
        }
        
      }//else
      return sb.toString();
    } //has other mutations.
    
    String convertedHeader = "";
    String[] splitedHeader = header.split("\\*");
    
    
    //make header except last type sample pairs 
    for (int i = 0 ; i < splitedHeader.length - 1; i++){
      sb.append(splitedHeader[i]).append("*");
    }
    
    String typeSamplePair = "";
    String[] typeSamplePairs = null;
    String codeInfo = "";
    
    if (hasAmbiguityCode(header)) {
      typeSamplePair = splitedHeader[splitedHeader.length - 1].split("&")[0];
      codeInfo = splitedHeader[splitedHeader.length - 1].split("&")[1];
      typeSamplePairs = typeSamplePair.split("\\+");
      
      System.out.println(header);
      
      convertedHeader = sb.append(convertPair(typeSamplePairs))
                          .append("&")
                          .append(codeInfo)
                          .toString();
    }
    else{
      typeSamplePairs = splitedHeader[splitedHeader.length - 1].split("\\+");
      convertedHeader = sb.append(convertPair(typeSamplePairs))
                          .toString();
    }
    
    return convertedHeader;
  }
  
  private static boolean hasOtherMutation(String header) {
    // if & contains, return true
    boolean result = false;
    if (header.contains("&SNV") || 
        header.contains("&INS") ||
        header.contains("&DEL") ){
      result = true;
    }
    
    return result;
  }

  private static String convertPair(String[] typeSamplePairs) {
    StringBuilder sb = new StringBuilder(20);
    int pairCount = 0;
    
    for ( String pair : typeSamplePairs) {
      pairCount += 1;
      
      char type = pair.charAt(0);
      String sampleNum = pair.substring(1, pair.length());
      
      //last sample, no "+" at the end of the pair
      if (pairCount == typeSamplePairs.length) {
        if (type == 'B') {
          sb.append("N")
            .append(sampleNum)
            .append("+");
          
          sb.append("T")
            .append(Integer.parseInt(sampleNum) + 1);
        }
        else{
          sb.append(pair);
        }
      }
      //not the last sample
      else{
        if (type == 'B') {
          sb.append("N")
            .append(sampleNum)
            .append("+");
          
          sb.append("T")
            .append(Integer.parseInt(sampleNum) + 1)
            .append("+");
        }
        else{
          sb.append(pair).append("+");
        }
      }
      
    }
    return sb.toString();
  }

  private static boolean isFromUniprot(String pdbHeader) {
    return !(pdbHeader.contains("*"));
  }

  private static boolean hasAmbiguityCode(String header) {
    return header.contains("&Code");
  }

  private static boolean isVariantDb(String header) {
    boolean isVarinatDb = false;
    if (header.contains("SNV") || 
        header.contains("INS") ||
        header.contains("DEL")) {
      isVarinatDb = true;
    }  
    return isVarinatDb; 
  }

  private static boolean hasMoreThanTwoHeader(String pdbHeader) {
    return (pdbHeader.split(HEADER_DELIMITER).length) > 1 ? true : false;
  }

  public static boolean isHeader(String line){
    return line.startsWith(PREFIX_OF_HEADER);
  }
}
