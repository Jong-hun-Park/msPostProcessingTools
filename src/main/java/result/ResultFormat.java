package result;

public enum ResultFormat {
  SPIKEIN,
  MODPLUS,
  MSGF,
  PIN;
  
  public boolean isSpikeIn() {
    return this == SPIKEIN;
  }
  
  public boolean isMsgf() {
    return this == MSGF;
  }

  public boolean isPin() {
    return this == PIN;
  }
  
}
