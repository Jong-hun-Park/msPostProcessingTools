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

  @Override
  public void loadResultFile(String resultFileName) throws IOException {

    // Read FDR filtered PSM file, and put the scanNumber (key) to a HashSet
    percolResult = new BufferedReader(new FileReader(resultFileName));

    String psmLine = "";
    // Skip the header
    psmLine = percolResult.readLine();

    String scanKey = "";
    while ((psmLine = percolResult.readLine()) != null) {
      scanKey = getScanKey(psmLine);
      scanKeySet.add(scanKey);
    }
  }

  @Override
  public void writeUnidentifiedSpectrum(String spectrumFileName) throws IOException {
    // Read Spectrum file (mgf format), and get the scanNumber from them
    // If the scanNumber is in the HashSet, skip the spectrum, otherwise write the spectrum.
    BufferedReader specReader = new BufferedReader(new FileReader(spectrumFileName));

    String unidentieidSpectrumFileName =
        spectrumFileName.substring(0, spectrumFileName.lastIndexOf('.')) + "_IdRemoved.mgf";
    PrintWriter unidentifiedSpectrumFile =
        new PrintWriter(new BufferedWriter(new FileWriter(unidentieidSpectrumFileName)));

    String specLine = "";
    StringBuffer sb = new StringBuffer();

    int spectrumCount = 0;
    String scanKey = null;
    String spectrumTitle;
    String spectrumCharge;
    while ((specLine = specReader.readLine()) != null) {

      if (specLine.startsWith("BEGIN")) {
        spectrumCount++;
        // initialize spectrum buffer
        sb.setLength(0);
        sb.append(specLine + "\n");
      } else if (specLine.startsWith("TITLE")) {
        spectrumTitle = specLine.trim().split("\\=")[1];
        scanKey = getScanKeyFromSpecTitle(spectrumTitle);
        sb.append(specLine + "\n");
      } else if (specLine.startsWith("CHARGE")) {
        spectrumCharge = specLine.trim().split("\\=")[1];
        sb.append(specLine + "\n");
      } else if (specLine.startsWith("END")) {
        sb.append(specLine + "\n");

        // if it's not existed in the result file, write the spectrum to the unidentified spectrum file.
        if (!scanKeySet.contains(scanKey)) {
          System.out.println(spectrumCount);
          unidentifiedSpectrumFile.print(new String(sb)); //write the spectrum
        }
        else {
          System.out.println("It is a matched spectra" + scanKey);
        }
      } else //when it is just a peak (mz intensity)
        sb.append(specLine + "\n");
    }

  }

  private static String getScanKeyFromSpecTitle(String spectrumTitle) {
    // TITLE=UPS1_5000amol_R1.5.5.2
    return spectrumTitle.split(".")[1];
  }


  public static String getScanKey(String psmLine) {
    String[] splited = psmLine.split("\t");
    String psmId = splited[0];
    String scanKey = psmId.split("_")[3];

    System.out.println(scanKey);

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


