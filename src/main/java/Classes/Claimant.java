package Classes; 

public class Claimant{
	//fields:
	protected int id;
	protected String lastName;
	protected String firstName;
	protected String middleName;
	protected String workPlace;
	protected String state;

	public Claimant() {
		this.id = -1;
		this.lastName = null;
		this.firstName = null;
		this.middleName = null;
		this.workPlace = null;
		this.state = null;
	}
	
	public Claimant(int id, String lastName, String firstName, String middleName, String workPlace, String state) {
		this.id = id;
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.workPlace = workPlace;
		this.state = state;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public void setMiddleName(String middleName){
		this.middleName = middleName;
	}
	
	public void setWorkPlace(String workPlace){
		this.workPlace = workPlace;
	}
	
	public void setState(String state){
		this.state = state;
	}
	
	public int getID(){
		return this.id;
	}
	
	public String getLastName(){
		return this.lastName;
	}
	
	public String getFirsttName(){
		return this.firstName;
	}
	
	public String getMiddleName(){
		return this.middleName;
	}
	
	public String getWorkPlace(){
		return this.workPlace;
	}
	
	public String getState(){
		return this.state;
	}
}
