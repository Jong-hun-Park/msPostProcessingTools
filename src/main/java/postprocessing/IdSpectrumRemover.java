package postprocessing;

import java.io.IOException;

import parameter.Arguments;
import parameter.ArgumentsParser;
import result.MsgfPlusResult;
import result.SearchResult;
import result.SpikeInResult;

/*
 * Remove Identified Spectrum from the given spectrum file and Make an unidentified spectrum
 * file(.mgf format) Generalized version of the old version of MakeUnidentifiedSpectra. 
 * 
 * 2016-03-28
 * 
 * @author Jonghun Park
 **/

//TODO: change this class name, not a verb form, but a Noun form.
public class IdSpectrumRemover {

  public static void main(String[] args) throws IOException {
    ArgumentsParser argsParser = new ArgumentsParser(args);
    argsParser.readOptionList();
    
    Arguments arguments = new Arguments(argsParser);

    SearchResult searchResult = null;
    if (arguments.getResultFormat().isSpikeIn()) {
      searchResult = new SpikeInResult();
    } else if (arguments.getResultFormat().isMsgf()) {
      searchResult = new MsgfPlusResult();
    }
    else {
      System.err.println("Other format is not avaialble yet");
      System.exit(-1);
    }

    // 1. read result file and store the information
    searchResult.loadResultFile(arguments.getResultFileName());
    // 2. read spectrum file and check if the spectrum is identified or not. if not, write the
    // spectrum to new out file (concatenated _IdRemoved.mgf)
    searchResult.writeUnidentifiedSpectrum(arguments.getSpectrumFileName());
    
    System.out.println("Removed Identified spectra from the spectrum file");
  }
}
