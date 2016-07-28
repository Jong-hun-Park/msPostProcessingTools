package result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class SpikeInResult implements SearchResult{
  private Map<String, Result> fileAneResultMap = new HashMap<>();

  @Override
  public void loadResultFile(String resultFileName) throws IOException {
    BufferedReader resultFileReader = new BufferedReader(new FileReader(resultFileName));

    String resultLine = "";
    // Read header
    resultLine = resultFileReader.readLine();
    // TODO: header checker

    if (resultFileName.substring(resultFileName.lastIndexOf('.'), resultFileName.length())
        .equals(".tsv")) {

      while ((resultLine = resultFileReader.readLine()) != null) {
        String columns[] = resultLine.split("\t"); // because the reulst file is tsv file.
        String fileName = columns[0];
        int index = 0;
        String charge = "";
        Result searchResult;

        if (isInteger(columns[1])) {
          index = Integer.parseInt(columns[1]);
        } else {
          /* get only integer from the given string. */
          index = Integer.parseInt(columns[1].replaceAll("[\\D]", ""));
        }

        searchResult = new Result(fileName, index);

        /* A HACK, this is only for the case that (fileName = TITLE in the formated result) */
        fileAneResultMap.put(fileName, searchResult);
      }
    } else {
      resultFileReader.close();
      throw new IOException("Please check the result file extension(required .tsv).");
    }
    resultFileReader.close();
  }

  @Override
  public void writeUnidentifiedSpectrum(String spectrumFileName) throws IOException {
    
    // DOUBLE CHECK!
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

    while ((specLine = spectrumFile.readLine()) != null) {
      if (specLine.startsWith("BEGIN")) {
        // initialize spectrum buffer
        spectrumCount++;
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
        if (!fileAneResultMap.containsKey(spectrumTitle)) {
          unidentifiedSpectrumFile.print(new String(spectrum));
        } else {
          // debug
          // TODO: this is a bug. no data in resultList objects. It's because it's a hashmap. other
          // option is using Set map?? 0329.

          //          SearchResult value = resultList.get(spectrumTitle);
          //          String charge = value.charge;
          //          System.out.println(charge);
          //          
          //          System.out.println("this spectrum is identified.");
          //          System.out.println(spectrumCharge);
          //          System.out.println( resultList.get(spectrumTitle));
          //          System.out.println(spectrumTitle);
          //          System.out.println( ((SearchResult)resultList.get(spectrumTitle)).title );

          continue;
        }
      } else
        spectrum.append(specLine + "\n");
    }

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

}
