package result;

//TODO: make a abstract class result file scan data

public class ModPlusResult {
  String spectrumFile;
  int index;
  float observedMW;
  int charge;
  float calculatedMW;
  float deltaMass;
  int score;
  double probability;
  String peptideSequence;
  String protein;
  String modification;
  String scanNum;
  
  ModPlusResult( String spectrumFile
             ,int index
             ,float observedMW
             ,int charge
             ,float calculatedMW
             ,float deltaMass
             ,int score
             ,double probability
             ,String peptideSequence
             ,String protein
             ,String modification
             ,String scanNum
             )
  {
    this.spectrumFile = spectrumFile;
    this.index = index;
    this.observedMW = observedMW;
    this.charge = charge;
    this.calculatedMW = calculatedMW;
    this.deltaMass = deltaMass;
    this.score = score;
    this.probability = probability;
    this.peptideSequence = peptideSequence;
    this.protein = protein;
    this.modification = modification;
    this.scanNum = scanNum;
  }
}
