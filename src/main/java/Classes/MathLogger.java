package Classes;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class MathLogger extends TreeMap<Integer, String>{

	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString(){
		Set<Entry<Integer, String>> set = this.entrySet();
		String eol = System.lineSeparator();
		String mL = "";
		for (Entry<Integer, String> pair : set){
			mL += pair.getKey()+") "+pair.getValue()+eol;
		}
		
		return mL;
	}
	
}