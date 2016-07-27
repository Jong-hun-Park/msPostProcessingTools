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

import parameter.Arguments;
import parameter.ArgumentsParser;
import parameter.Option;
import result.SearchTool;
import result.SpikeIn;

/*
 * Remove Identified Spectrum from the given spectrum file and Make an unidentified spectrum
 * file(.mgf format) Generalized version of the old version of MakeUnidentifiedSpectra. 
 * 
 * 2016-03-28
 * 
 * @author Jonghun Park
 **/

//TODO: change this class name, not a verb form, but a Noun form.
public class IdRemover {

  public static void main(String[] args) throws IOException {
    ArgumentsParser argsParser = new ArgumentsParser(args);
    argsParser.readOptionList();
    
    Arguments arguments = new Arguments(argsParser);
    
    if (argsParser.hasInvalidOption()) {
      argsParser.printErrorMessage();
      System.exit(0);
    } 

    SearchTool searchResult = null;
    if (arguments.getResultFormat().isSpikeIn()) {
      searchResult = new SpikeIn();
    } else {
      System.err.println("other format is not implemented yet");
      System.exit(-1);
    }

    // 1. read result file and store the information
    searchResult.loadResultFile(arguments.getResultFileName());
    // 2. read spectrum file and check if the spectrum is identified or not. if not, write the
    // spectrum to new out file (concatenated _IdRemoved.mgf)
    searchResult.writeUnidentifiedSpectrum(arguments.getSpectrumFileName());
    System.out.println("done");
  }
}
