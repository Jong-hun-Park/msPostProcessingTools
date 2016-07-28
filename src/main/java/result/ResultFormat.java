package result;

public enum ResultFormat {
  SPIKEIN,
  MODPLUS,
  MSGF;
  
  public boolean isSpikeIn() {
    return this == SPIKEIN;
  }
  
  public boolean isMsgf() {
    return this == MSGF;
  }
  
}
