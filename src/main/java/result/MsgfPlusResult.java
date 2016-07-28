package result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MsgfPlusResult implements SearchResult{
  private Map<String, PSM> titleAndPsmMap = new HashMap<>();
  private static final String TSV_DELIMITER = "\t";
  
  @Override
  public void loadResultFile(String resultFileName) throws IOException {
    BufferedReader resultFileReader = new BufferedReader(new FileReader(resultFileName));

    String resultLine = "";
    // Read header. #SpecFile
    checkHeader(resultFileReader, resultLine);

    if (isTsv(resultFileName)) {
      
      while ((resultLine = resultFileReader.readLine()) != null) {
        String columns[] = resultLine.split(TSV_DELIMITER); // tsv format
        
        String specFile = columns[0];
        int index = 0;
        String title = columns[3];
        String charge = columns[8];
        PSM psm;

        if (isInteger(columns[1])) {
          index = Integer.parseInt(columns[1]);
        } else {
          /* get only integer from the given string. */
          index = Integer.parseInt(columns[1].replaceAll("[\\D]", ""));
        }
        psm = new PSM(specFile, index, title, charge);

        titleAndPsmMap.put(title, psm);
      }
    } else {
      resultFileReader.close();
      throw new IOException("Please check the result file extension(required .tsv).");
    }
    resultFileReader.close();
  }

  @Override
  public void writeUnidentifiedSpectrum(String spectrumFileName) throws IOException {
    String unidentieidSpectrumFileName =
        spectrumFileName.substring(0, spectrumFileName.lastIndexOf('.')) + "_IdRemoved.mgf";

    BufferedReader spectrumFile = new BufferedReader(new FileReader(spectrumFileName));
    PrintWriter unidentifiedSpectrumFile =
        new PrintWriter(new BufferedWriter(new FileWriter(unidentieidSpectrumFileName)));

    String specLine = "";
    String spectrumTitle = "";
    String spectrumCharge = "";
    StringBuffer spectrum = null;
    int spectrumCount = 0;
    int idSpectrumCount = 0;

    while ((specLine = spectrumFile.readLine()) != null) {
      if (specLine.startsWith("BEGIN")) {
        spectrumCount++;
        // initialize spectrum buffer
        spectrum = new StringBuffer();
        spectrum.append(specLine + "\n");

      }
      // TODO: can be check using TITLE, not index. optional.
      // new method which compare the TITLE of result file and the spectrum file
      else if (specLine.startsWith("TITLE")) {
        spectrumTitle = specLine.trim().split("\\=")[1];
        spectrum.append(specLine + "\n");
      }
      else if (specLine.startsWith("CHARGE")) {
        spectrumCharge = specLine.trim().split("\\=")[1];
        spectrum.append(specLine + "\n");
      }
      else if (specLine.startsWith("END")) {
        spectrum.append(specLine + "\n");

        // if it's not existed in the result file, write the spectrum to the unidentified spectrum
        // file.
        if (!titleAndPsmMap.containsKey(spectrumTitle)) {
          unidentifiedSpectrumFile.print(new String(spectrum));
        } else {
          idSpectrumCount++;
        }
      } 
      else{
        spectrum.append(specLine + "\n");
      }
    }
    
    System.out.println("id Spectrum Count:" + idSpectrumCount);
    System.out.println("spectrum Count: " + spectrumCount);
    unidentifiedSpectrumFile.close();
    spectrumFile.close();
    
  }
  
  
  /*
   * Check given string is Integer or not
   * 
   * @input : A string
   * @output: boolean value(True or False)
   */
  public static boolean isInteger(String str) {
    if (str == null) {
      return false;
    }
    int length = str.length();
    if (length == 0) {
      return false;
    }
    int i = 0;
    if (str.charAt(0) == '-') {
      if (length == 1) {
        return false;
      }
      i = 1;
    }
    for (; i < length; i++) {
      char c = str.charAt(i);
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }

  private boolean isTsv(String resultFileName) {
    return resultFileName.substring(resultFileName.lastIndexOf('.'), resultFileName.length())
        .equals(".tsv");
  }

  private void checkHeader(BufferedReader resultFileReader, String line) throws IOException {
    if (line.startsWith("#")) {
      resultFileReader.readLine();
    }
  }


}
