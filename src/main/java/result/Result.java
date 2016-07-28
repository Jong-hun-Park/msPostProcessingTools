package result;

public class Result {
	
	private String fileName;
	private int index;
	private String title;
	private String charge;
	
	public Result (String fileName, int index){
		this.fileName = fileName;
		this.index = index;
	}
	public Result (String fileName, int index, String title){
		this.fileName = fileName;
		this.index= index;
		this.title = title;
	}
	public Result (String fileName, int index, String title, String charge){
		this.fileName = fileName;
		this.index= index;
		this.title = title;
		this.charge = charge;
	}

}
