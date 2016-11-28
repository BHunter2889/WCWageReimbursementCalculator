package Classes;

import java.util.ArrayList;

public final class StatesWithCalculations extends ArrayList<StateLawCalculable> {

	private static final long serialVersionUID = 1L;
	
	public StatesWithCalculations(){
		this.add(new MissouriCalculation());
	}
	
	public ArrayList<String> getAvailableStateNamesArray(){
		ArrayList<String> states = new ArrayList<String>();
		for(StateLawCalculable sLC : this){
			states.add(sLC.getStateName());
		}
		states.sort(null);
		return states;
	}
	
	public ArrayList<String> getAvailableStateAbbrvsArray(){
		ArrayList<String> abbrvs = new ArrayList<String>();
		for(StateLawCalculable sLC : this){
			abbrvs.add(sLC.getStateAbbrv());
		}
		abbrvs.sort(null);
		return abbrvs;
	}
}
