package edu.hyu.postProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Remove Identified Spectrum from the given spectrum file and Make an unidentified spectrum
 * file(.mgf format) Generalized version of the old version of MakeUnidentifiedSpectra. 
 * 
 * 2016-03-28
 * 
 * @author Jonghun Park
 **/

public class MakeUnidentifiedSpectra {

  public static void main(String[] args) throws IOException {

    ArrayList<Option> optsList = new ArrayList<Option>();
    ArgumentsParser argsParser = new ArgumentsParser(args);
    optsList = argsParser.getOptionList();

    /* when the user call -h option. (print available options and terminate) */
    if (optsList == null) {
      System.exit(0);
    }

    String resultFileName = "";
    String spectrumFileName = "";
    String resultFormat = "";

    // Assign the parameters
    // TODO: right arguments? coding! argument check! e.g.) -i a;sldk;alsfj.tsv --> nofile. -f MGGF+
    // ??
    for (Option opt : optsList) {

      switch (opt.flag) {
        case "-i":
          resultFileName = opt.paramValue;
          break;
        case "-s":
          spectrumFileName = opt.paramValue;
          break;
        case "-f":
          resultFormat = opt.paramValue;
          break;
        default:
      }
    }
    //TODO: format checker?

    // 1. read result file and store the information
    HashMap<String, SearchResult> resultList = scanResultFile(resultFileName, resultFormat);

    // 2. read spectrum file and check if the spectrum is identified or not. if not, write the
    // spectrum to new out file (concatenated _IdRemoved.mgf)
    writeUnidentifiedSpectrum(spectrumFileName, resultList);

    System.out.println("done");

  }

  /*
   * 
   */
  public static void writeUnidentifiedSpectrum(String spectrumFileName,
      HashMap<String, SearchResult> resultList) throws IOException {

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
        if (!resultList.containsKey(spectrumTitle)) {
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
   * Scan result File and store the information(TITLE) into SearchResult class(data structure)
   * @input: resultFileName, resultFormat
   * @output:
   */
  public static HashMap<String, SearchResult> scanResultFile(String resultFileName,
      String resultFormat) throws IOException {

    HashMap<String, SearchResult> resultList = new HashMap<String, SearchResult>();

    // format check
    if (resultFormat.equalsIgnoreCase("spikein")) {
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
          SearchResult searchResult;

          if (isInteger(columns[1])) {
            index = Integer.parseInt(columns[1]);
          } else {
            /* get only integer from the given string. */
            index = Integer.parseInt(columns[1].replaceAll("[\\D]", ""));
          }

          searchResult = new SearchResult(fileName, index);

          /* A HACK, this is only for the case that (fileName = TITLE in the formated result) */
          resultList.put(fileName, searchResult);
        }
      } else {
        resultFileReader.close();
        throw new IOException("Please check the result file extension(required .tsv).");
      }
      resultFileReader.close();
      return resultList;
    }
    // TODO: other format can be handled. 
    // format is not spike-in?
    else if (resultFormat.equalsIgnoreCase("msgfPlus")) {
      System.out.println("format error, msgfPlus is not supported yet.");
      System.exit(0);
      return null;
    }
    else if (resultFormat.equalsIgnoreCase("modPlus")) {
      System.out.println("format error, modPlus is not supported yet.");
      System.exit(0);
      return null;
    }
    else {
      System.out.println("format error, should be spikein");
      System.exit(0);
      return null;
    }

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
