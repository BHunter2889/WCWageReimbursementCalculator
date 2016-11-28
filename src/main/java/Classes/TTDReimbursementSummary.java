package Classes;

import java.math.BigDecimal;
import java.util.ArrayList;

import Interfaces.StateLawCalculable;

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
	
	public String toString(){
		return "Calculated Weekly Payment: $"+this.calculatedWeeklyPayment.toPlainString()+" | Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()+
				" | Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString();
	}
}
