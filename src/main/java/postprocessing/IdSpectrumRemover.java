package postprocessing;

import java.io.IOException;

import parameter.Arguments;
import parameter.ArgumentsParser;
import result.SearchResult;
import result.SearchResultFactory;

/*
 * Remove Identified Spectrum from the given spectrum file and Make an unidentified spectrum
 * file(.mgf format) Generalized version of the old version of MakeUnidentifiedSpectra.
 * 
 * 2016-03-28
 * 
 * @author Jonghun Park
 **/

// TODO: 2016.11.11 charge까지 key로 사용해서 지울 것.
public class IdSpectrumRemover {

  public static void main(String[] args) throws IOException {
    ArgumentsParser argsParser = new ArgumentsParser(args);
    argsParser.readIdRemoverOptionList();
    Arguments arguments = new Arguments(argsParser);
    
    SearchResult searchResult = SearchResultFactory.create(arguments.getResultFormat());
    
    System.out.println("Make unidentified spectra file from the given MS2 spectrum file (mgf format)");

    // 1. read result file and store the information
    System.out.println("Reading the reulst files...");
    searchResult.loadResultFile(arguments.getResultFileName());
    
    // 2. read spectrum file and check if the spectrum is identified or not. if not, write the
    // spectrum to new out file (concatenated _IdRemoved.mgf)
    System.out.println("Writing unidentified spectra...");
    searchResult.writeUnidentifiedSpectrum(arguments.getSpectrumFileName());

    System.out.println("New unidentified spectra file is created");
  }
}
