package result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PinMsgfResult implements SearchResult {

  private BufferedReader tableReader;
  private Map<String, String> scanNumAndTitleMap;
  
  private BufferedReader percolResult;
  private HashSet<String> scanKeySet;
  private BufferedReader specReader;
  private BufferedWriter unidSpecWriter;

  @Override
  public void loadResultFile(String resultFileName) throws IOException {
    // Read the scanNum and Title Map
    // TODO: THIS SHOULD BE CHANGED!
    String tableFileName = "5fmol_msgf_id_title_table.tsv";
    loadScanNumAndTitleTable(tableFileName);
    
    // Read FDR filtered PSM file, and put the scanNumber (key) to a HashSet
    percolResult = new BufferedReader(new FileReader(resultFileName));

    String psmLine = "";
    // Skip the header
    psmLine = percolResult.readLine();

    String scanKey = "";
    scanKeySet = new HashSet<String>();
    while ((psmLine = percolResult.readLine()) != null) {
      scanKey = getScanKey(psmLine);
      if (scanKey != null) {
        scanKeySet.add(scanKey);
      }
      else {
        System.err.println("There is no such key, check the scanKey");
        System.exit(-1);
      }
    }
  }

  private String getScanKey(String psmLine) {
    String[] tabSplitedPsmLine = psmLine.split("\t");
    String psmId = tabSplitedPsmLine[0];
    String[] splitedPsmId = psmId.split("_");
    String scanNumKey = splitedPsmId[0] + "_" + 
                        splitedPsmId[1] + "_" + 
                        splitedPsmId[2] + "_" + 
                        splitedPsmId[3] + "_" + 
                        "SIR" + "_" + 
                        splitedPsmId[5];
    
    return scanNumAndTitleMap.get(scanNumKey);
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
  
  public void loadScanNumAndTitleTable(String tableFileName) throws IOException {
    try {
      tableReader = new BufferedReader(new FileReader(tableFileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("There should be a scan num table file.");
      System.exit(-1);
    }
    
    scanNumAndTitleMap = new HashMap<String, String>();
    String line = "";
    while ((line = tableReader.readLine()) != null ) {
      String[] splited = line.split("\t");
      
      if (splited.length != 2) {
        System.err.println("Parsing error, should be length 2");
        System.exit(-1);
      }
      String scanNum = splited[0];
      String scanTitle = splited[1];
      
      scanNumAndTitleMap.put(scanNum, scanTitle);
    }
  }

}
