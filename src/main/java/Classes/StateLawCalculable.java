package Classes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public interface StateLawCalculable {
	
	public ArrayList<Paycheck> addAndTrimToPriorWages(Paycheck pc, ArrayList<Paycheck> pchecks, Calendar priorWeekStart); //CompClaim
	public ArrayList<Paycheck> addTPDWorkPaycheck(Paycheck pc, ArrayList<Paycheck> pchecks, Calendar priorWeekStart) throws Exception; //TPDRSummary
	public ArrayList<WorkCompPaycheck> addWCPaycheck(WorkCompPaycheck wcPC, ArrayList<WorkCompPaycheck> wcPayments, Calendar priorWeekStart); //ReimbursementSummary
	public BigDecimal computeAnyLatePaymentCompensation(BigDecimal grossAmnt, BigDecimal calculatedWeeklyPayment); //WCPaycheck
	public BigDecimal computeAvgPriorGrossWeeklyPayment(ArrayList<Paycheck> priorWages); //CompClaim
	//public BigDecimal computeAmountNotPaid(ArrayList<WorkCompPaycheck> wcPayments, BigDecimal calcWP);	//ReimbursementSummary
	public BigDecimal computeCalculatedWeeklyPayment(BigDecimal avgPGrossWeekPay); //ReimbursementSummary
	public Calendar computeEarliestPriorWageDate(Calendar priorWeekStart); //CompClaim
	public BigDecimal computeWCSupplementalPayment(Paycheck workPayment, BigDecimal avgPriorGrossWeeklyPayment);
	public boolean determineAndSetIsLate(Calendar payPeriodEnd, Calendar payReceived); //WCPaycheck
	public String getStateAbbrv();
	public String getStateName();
	public TimeZone getTimeZone();
	public boolean priorWagesIsComplete(ArrayList<Paycheck> priorWages); //CompClaim
}
