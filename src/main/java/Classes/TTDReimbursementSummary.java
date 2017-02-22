package Classes;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TTDReimbursementSummary extends ReimbursementSummary {
	
	public TTDReimbursementSummary() {
		super();
	}
	public TTDReimbursementSummary(CompClaim claim, StateLawCalculable stateLawCalc) {
		super(claim, stateLawCalc);
	}
	
	public TTDReimbursementSummary(BigDecimal calculatedWeeklyPayment, CompClaim claimSummary, BigDecimal amountNotPaid, ArrayList<WorkCompPaycheck> wcPayments) {
		super(calculatedWeeklyPayment, claimSummary, amountNotPaid, wcPayments);
	}
	
	public TTDReimbursementSummary(ReimbursementSummary rsumm) {
		super(rsumm);
	}
	
	public String listWCPCs(){
		if (this.wcPayments.isEmpty()) return "No WC Paychecks Set.";
		if (this.wcPayments.size() == 1) return "1) " + this.wcPayments.get(this.wcPayments.size()-1).toString();
		
		this.sortWCPaymentsByDate();
		String eol = System.getProperty("line.separator");
		String list = "";
		int num = 1;
		for(WorkCompPaycheck p : this.wcPayments){
			list += num + ")" + p.toString() + eol;
			num++;
		}
		
		return list;
	}
	
	public String toString(){
		String eol = System.getProperty("line.separator");
		
		return (this.claimSummary.priorWagesIsComplete()) ? "Calculated Weekly Payment: $"+this.calculatedWeeklyPayment.toPlainString()+" | Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString()+eol+
				" | Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()
				: "Not Yet Completed.";
	}
	
	public String toTableString(BigDecimal calcOwed){
		String eol = System.getProperty("line.separator");
		
		if(this.containsCompClaim()){
			if (this.claimSummary.priorWagesIsComplete()){
				return "Calculated Weekly Payment: $"+this.calculatedWeeklyPayment.toPlainString()+eol+
						"Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString()+eol+
						"Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()+eol+
						"Work Comp Calculated Total Owed: $"+calcOwed.toPlainString();
			}
			else{
				return "Not ready to compute. Prior Wages are not complete.";
			}
		}
		else{
			return "Not Yet Completed.";
		}
	}
}
