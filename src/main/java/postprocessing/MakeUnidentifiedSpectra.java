package postprocessing;

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
    
    result.SearchTool searchResult = null;
    if (resultFormat.equalsIgnoreCase("spikein")) {
      searchResult = new result.SpikeIn();
    } else {
      System.err.println("other format is not implemented yet");
      System.exit(-1);
    }
    
    // 1. read result file and store the information
    searchResult.scanResultFile(resultFileName);
    // 2. read spectrum file and check if the spectrum is identified or not. if not, write the
    // spectrum to new out file (concatenated _IdRemoved.mgf)
    searchResult.writeUnidentifiedSpectrum(spectrumFileName);
    System.out.println("done");
  }
}
