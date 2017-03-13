package result;

public enum ResultFormat {
  SPIKEIN,
  MODPLUS,
  MSGF,
  PIN,
  PINMSGF;
  
  
  public boolean isSpikeIn() {
    return this == SPIKEIN;
  }
  
  public boolean isMsgf() {
    return this == MSGF;
  }

  public boolean isPin() {
    return this == PIN;
  }

  public boolean isPinMsgf() {
    return this == PINMSGF;
  }
  
}
