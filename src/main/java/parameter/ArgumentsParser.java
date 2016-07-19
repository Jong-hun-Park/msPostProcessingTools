package parameter;

import java.io.FileReader;
import java.util.ArrayList;

// open file, and parse parameters.
public class ArgumentsParser {
  String[] args;

  public ArgumentsParser(String[] args) {
    this.args = args;
  }

  public ArrayList<Option> getOptionList() {

    ArrayList<Option> optsList = new ArrayList<Option>();

    for (int i = 0; i < args.length; i++) {
      
      if (args[i] != null){

        switch (args[i].charAt(0)) {
          
          case '-':
            if (args[i].length() < 2) {
              throw new IllegalArgumentException("Not a valid argument: " + args[i]);
            } else {
              if (args[i].charAt(1) == 'h') {
                System.out.println("java -jar MakeUnidentifiedSpectra.jar -i -s -f");
                System.out.println("-i : input Result File");
                System.out.println("-s : Spectrum File compared with");
                System.out.println("-f : Mode ( Spike-in, MSGF, ModPlus )");
                System.out.println(
                    "Example java -jar MakeUnidentifiedSpectra.jar -i result.tsv -s test.mgf -f SpikeIn");
                
                return null;
              }
              optsList.add(new Option(args[i], args[i + 1]));
              i++;
              
            }
            break;
            
          default:
            throw new IllegalArgumentException("Not a valid argument: " + args[i]);
        }
        
      }
      else{
        throw new IllegalArgumentException("null argument error");
      }
      
    } // end for

    return optsList;
  }
}
