package result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class PinResult implements SearchResult {

  private static BufferedReader percolResult;
  private static HashSet<String> scanKeySet;
  private BufferedReader specReader;
  private BufferedWriter unidSpecWriter;

  @Override
  public void loadResultFile(String resultFileName) throws IOException {

    // Read FDR filtered PSM file, and put the scanNumber (key) to a HashSet
    percolResult = new BufferedReader(new FileReader(resultFileName));

    String psmLine = "";
    // Skip the header
    psmLine = percolResult.readLine();

    String scanKey = "";
    scanKeySet = new HashSet<String>();
    while ((psmLine = percolResult.readLine()) != null) {
      scanKey = getScanKey(psmLine);
      scanKeySet.add(scanKey);
    }
  }

  @Override
  public void writeUnidentifiedSpectrum(String spectrumFileName) throws IOException {
    specReader = new BufferedReader(new FileReader(spectrumFileName));

    String unidentieidSpectrumFileName =
        spectrumFileName.substring(0, spectrumFileName.lastIndexOf('.')) + "_IdRemoved.mgf";
    unidSpecWriter = new BufferedWriter(new FileWriter(unidentieidSpectrumFileName));

    String specLine = "";
    StringBuffer sb = new StringBuffer();

    int spectrumCount = 0;
    int unIdentifiedCount = 0;
    String scanKey = "";
    String spectrumTitle = "";
    String spectrumCharge = "";
    
    while ((specLine = specReader.readLine()) != null) {

      if (specLine.startsWith("BEGIN IONS")) {
        spectrumCount++;
        // initialize spectrum buffer
        sb.setLength(0);
        sb.append(specLine + "\n");
      } else if (specLine.startsWith("TITLE")) {
        spectrumTitle = specLine.trim().split("\\=")[1];
        scanKey = spectrumTitle; // In pin file, scanKey is the spectrumTitle
        sb.append(specLine + "\n");
      } else if (specLine.startsWith("CHARGE")) {
        spectrumCharge = specLine.trim().split("\\=")[1];
        sb.append(specLine + "\n");
      } else if (specLine.startsWith("END IONS")) {
        sb.append(specLine + "\n");

        // if it's not existed in the result file, write the spectrum to the unidentified spectrum file.
        if (!scanKeySet.contains(scanKey)) {
          unIdentifiedCount++;
          unidSpecWriter.write(new String(sb)); //write the spectrum
        }
        else {
//          System.out.println("It is a matched spectra " + scanKey);
        }
      } else //when it is just a peak (mz intensity)
        sb.append(specLine + "\n");
    }
    
    specReader.close();
    unidSpecWriter.close();
    
    System.out.println("Total Spectrum Count: " + spectrumCount);
    System.out.println("UnIdentified Spectrum Count: " + unIdentifiedCount);

  }

  private static String getScanKeyFromSpecTitle(String spectrumTitle) {
    // Title example)
    // TITLE=UPS1_5000amol_R1.5.5.2
    return spectrumTitle.split("\\.")[1];
  }


  public static String getScanKey(String psmLine) {
    // Pin file example
    // UPS1_5000amol_R1_5_2_1
    String[] splited = psmLine.split("\t");
    String psmId = splited[0];
    String[] splitedPsmId = psmId.split("_");
    
    String scanNum = splitedPsmId[3];
    String charge = splitedPsmId[4];
    
    String scanKey = splitedPsmId[0] + "_" + splitedPsmId[1] + "_"+ splitedPsmId[2]
                     + "." + scanNum + "." + scanNum
                     + "." + charge;
    return scanKey;
  }

  private static ArrayList<File> getFdrFilteredFiles(String searchSource) {

    File dir = new File(searchSource);
    File[] fileList = dir.listFiles();
    ArrayList<File> fdrFilteredFileList = new ArrayList<File>();

    for (File f : fileList) {
      if (f.getName().endsWith("FDR")) {
        fdrFilteredFileList.add(f);
      }
    }

    return fdrFilteredFileList;
  }

  private static File getSpectrumFile(String searchSource) {

    File dir = new File(searchSource);
    File[] fileList = dir.listFiles();
    File spectrumFile = null;

    for (File f : fileList) {
      if (f.getName().endsWith("mgf")) {
        if (spectrumFile == null) {
          spectrumFile = f;
        } else {
          System.err.println("There are two spectrum files in the source " + searchSource);
          System.err.println("Program exit");
          System.exit(1);

        }
      }
    }

    return spectrumFile;
  }

}


