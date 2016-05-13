package format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * pDB header change
 * from B41 to N41+T42
 * 
 * Jonghun Park
 * 2016.05.12
 * 
 */
public class Pdb {
  static final String PREFIX_OF_HEADER = ">";
  static final String HEADER_DELIMITER = ";";
  
  public static void main (String[] args){
    String pdbFileName = "./repo/CompositeDB/CompositeDB_43N+62T_v1.41.revCat.fasta";

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
   */
  public static void convertPdbHeader (String pdbFileName) throws IOException {
    String outputFileName = pdbFileName.substring(0, pdbFileName.lastIndexOf(".")) + ".typeDivided.tsv";
    
    // Load File
    BufferedReader pdbFile = new BufferedReader(new FileReader(pdbFileName));
    BufferedWriter outputFile = new BufferedWriter( new PrintWriter(outputFileName));
    
    //Initialization
    String line = "";
    
    while ((line = pdbFile.readLine()) != null) {
      
      if (line.startsWith(">A2AB89")){
        System.out.println("Www");
      }
      
      
      if (isHeader(line)){
        String pdbHeader = line;
        StringBuilder sb = new StringBuilder(line. length() * 2);
        String convertedHeader = "";
        
        if (line.contains("Contaminant")){
          outputFile.write(line + "\n");
          continue;
        }
        
        if (hasMoreThanTwoHeader(pdbHeader)) {
          String[] headers = pdbHeader.split(HEADER_DELIMITER);
          
          for (int i = 0; i < headers.length; i++){
            if (isFromUniprot(headers[i])) { //don't have to convert
              convertedHeader = headers[i]; //not convert
            } else {
              convertedHeader = divideBothType(headers[i]);
            }
            
            if (i == headers.length) { // the last header
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
  
  // Example
  // >XXX_ENST00000559605$SNV_S42F(chr15#101910550,0,G,A)*Germ*B43+B61&CodeN_g
  private static String divideBothType(String header) {
    assert header.startsWith(PREFIX_OF_HEADER) == false : "should start with >";
    
    String convertedHeader = "";
    String[] splitedHeader = header.split("\\*");
    
    StringBuilder sb = new StringBuilder(header.length() * 2);
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
