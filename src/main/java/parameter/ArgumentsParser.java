package parameter;

import java.io.FileReader;
import java.util.ArrayList;

// open file, and parse parameters.
public class ArgumentsParser {
  ArrayList<Option> optionList;
  String[] args;

  public ArgumentsParser(String[] args) {
    this.args = args;
  }
  
  
  //TODO: argument validation check?
  //TODO: this is just a reading method. create a method checking validity of the options
  public void readOptionList() {

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
                return;
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

    this.optionList = optsList;
  }

  public boolean hasInvalidOption() {
    return (this.optionList == null);
  }

  public void printErrorMessage() {
    System.out.println("Please check the arguments");

    System.out.println("java -jar MakeUnidentifiedSpectra.jar -i -s -f");
    System.out.println("-i : input Result File");
    System.out.println("-s : Spectrum File compared with");
    System.out.println("-f : Mode ( Spike-in, MSGF, ModPlus )");
    System.out.println(
        "Example java -jar MakeUnidentifiedSpectra.jar -i result.tsv -s test.mgf -f SpikeIn");
  }

  public ArrayList<Option> getOptionList() {
    return this.optionList;
  }
}
