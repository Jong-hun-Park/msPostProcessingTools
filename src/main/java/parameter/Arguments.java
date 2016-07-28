package parameter;

import result.ResultFormat;

public class Arguments {
  private String resultFileName;
  private String spectrumFileName;
  private ResultFormat resultFormat;
  
  public Arguments(ArgumentsParser argsParser) {
    //TODO: format checker?
    //TODO: possible option list.
    for (Option opt : argsParser.getOptionList()) {

      switch (opt.flag) {
        case "-i":
          this.resultFileName = opt.paramValue;
          break;
        case "-s":
          this.spectrumFileName = opt.paramValue;
          break;
        case "-f":
          this.resultFormat = ResultFormat.valueOf(opt.paramValue);
          break;
        default:
          System.out.println("This option" + opt + "is not registered.");
          argsParser.printErrorMessage();
          break;
      }
    }
  }
  
  
  public String getResultFileName() {
    return resultFileName;
  }
  public String getSpectrumFileName() {
    return spectrumFileName;
  }
  public ResultFormat getResultFormat() {
    return resultFormat;
  }
  
}
