package edu.hyu.postprocessing;

public class SearchResult {
	
	//TODO: add other values if you need to store.
	String fileName;
	int index;
	String title;
	String charge;
	
	public SearchResult (String fileName, int index){
		this.fileName = fileName;
		this.index = index;
	}
	public SearchResult (String fileName, int index, String title){
		this.fileName = fileName;
		this.index= index;
		this.title = title;
	}
	public SearchResult (String fileName, int index, String title, String charge){
		this.fileName = fileName;
		this.index= index;
		this.title = title;
		this.charge = charge;
	}
	

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void setIndex(int index) {
		this.index = index;
	}

	private void setTitle(String title) {
		this.title = title;
	}
	
	private void setCharge(String charge) {
		this.charge = charge;
	}

	private String getFileName() {
		return this.fileName;
	}

	private String getIndex() {
		return this.fileName;
	}

	private String getTitle() {
		return this.fileName;
	}
	
	private String getCharge() {
		return this.charge;
	}

}
