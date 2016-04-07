package edu.hyu.postprocessing;

/*
 * Parameterized Options
 */
public class Option {
	
	enum ResultFormat {spikein, MSGFplus, MODplus}
	
	final String flag;
	final String paramValue;

	public Option(String flag, String paramValue) {
		
	  this.flag = flag;
	  //TODO: Option error check. (enum check)
	  this.paramValue = paramValue;
		
	}
}
