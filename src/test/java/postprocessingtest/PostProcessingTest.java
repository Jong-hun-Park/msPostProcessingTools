package postprocessingtest;

import java.io.IOException;

import postprocessing.MakeUnidentifiedSpectra;

public class PostProcessingTest {

  public static void main(String[] args) {
    
    testArgumentsParser();
  }

  private static void testArgumentsParser() {
    
    MakeUnidentifiedSpectra tester = new MakeUnidentifiedSpectra();
    String[] args = new String[6];
    
    // -i, -s, -f -n, -h
    args[0] = "-i";
    args[1] = "C-25fmol-R1_TEST.tsv";
    args[2] = "-s";
    args[3] = "C-25fmol-R1_QEx2_000090.mgf";
    args[4] = "-f";
//    args[5] = "Spike"; //should be spikein
    args[5] = "spikein";
    
    try {
      tester.main(args);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
