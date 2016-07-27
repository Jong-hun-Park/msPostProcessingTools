package parameter;

/*
 * Parameterized Options
 */
public class Option {
	
	public final String flag;
	public final String paramValue;

	public Option(String flag, String paramValue) {
		
	  this.flag = flag;
	  //TODO: Option error check. (enum check)
	  this.paramValue = paramValue;
		
	}
}
