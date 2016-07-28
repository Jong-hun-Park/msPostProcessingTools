package result;

public class PSM {
	
	private String fileName;
	private int index;
	private String title;
	private String charge;
	
	public PSM (String fileName, int index){
		this.fileName = fileName;
		this.index = index;
	}
	public PSM (String fileName, int index, String title){
		this.fileName = fileName;
		this.index= index;
		this.title = title;
	}
	public PSM (String fileName, int index, String title, String charge){
		this.fileName = fileName;
		this.index= index;
		this.title = title;
		this.charge = charge;
	}

}
