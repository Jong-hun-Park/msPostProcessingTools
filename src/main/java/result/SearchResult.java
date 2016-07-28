package result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface SearchResult {

  public abstract void loadResultFile            (String resultFileName) throws IOException;
  public abstract void writeUnidentifiedSpectrum (String spectrumFileName) throws IOException;

  
}
