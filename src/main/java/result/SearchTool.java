package result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import postprocessing.SearchResult;

//search tools
public interface SearchTool {

  public void scanResultFile           (String resultFileName) throws IOException;
  public void writeUnidentifiedSpectrum(String spectrumFileName) throws IOException;
}
