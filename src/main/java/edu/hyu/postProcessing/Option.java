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
		this.paramValue = paramValue;
		
	}
}
