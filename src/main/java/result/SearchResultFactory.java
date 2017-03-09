package result;

public class SearchResultFactory {

  public static SearchResult create(ResultFormat resultFormat) {
    
    if (resultFormat.isSpikeIn()) {
      return new SpikeInResult();
    }
    else if (resultFormat.isMsgf()) {
      return new MsgfPlusResult();
    } 
    else if (resultFormat.isPin()) {
      return new PinResult();
    }
    else {
      System.err.println("Other format is not avaialble");
      System.exit(-1);
    }
    
    return null;
  }
  
}
