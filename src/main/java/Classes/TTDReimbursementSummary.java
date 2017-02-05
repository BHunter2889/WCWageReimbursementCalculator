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
	
	public String toString(){
		String eol = System.getProperty("line.separator");
		return (this.claimSummary.priorWagesIsComplete()) ? "Calculated Weekly Payment: $"+this.calculatedWeeklyPayment.toPlainString()+" | Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString()+eol+
				" | Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()
				: "Not Yet Completed.";
	}
}
