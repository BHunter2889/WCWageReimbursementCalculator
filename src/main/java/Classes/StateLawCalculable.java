package Classes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;

public interface StateLawCalculable {
	
	public ArrayList<Paycheck> addAndTrimToPriorWages(Paycheck pc, ArrayList<Paycheck> pchecks, CompClaim cHist); //CompClaim
	public ArrayList<TPDPaycheck> addTPDWorkPaycheck(TPDPaycheck pc, ArrayList<TPDPaycheck> workPayments, Calendar priorWeekStart) throws Exception; //TPDRSummary
	public ArrayList<WorkCompPaycheck> addWCPaycheck(WorkCompPaycheck wcPC, ArrayList<WorkCompPaycheck> wcPayments, Calendar priorWeekStart); //ReimbursementSummary
	public Paycheck[] splitDateInjuredPayPeriodChecks(Paycheck pc, CompClaim cHist);
	public BigDecimal computeAnyLatePaymentCompensation(BigDecimal grossAmnt, BigDecimal calculatedWeeklyPayment); //WCPaycheck
	public BigDecimal computeAvgPriorGrossWeeklyPayment(ArrayList<Paycheck> priorWages); //CompClaim
	//public BigDecimal computeAmountNotPaid(ArrayList<WorkCompPaycheck> wcPayments, BigDecimal calcWP);	//ReimbursementSummary
	public BigDecimal computeCalculatedWeeklyPayment(BigDecimal avgPGrossWeekPay); //ReimbursementSummary
	public Calendar computeEarliestPriorWageDate(Calendar priorWeekStart); //CompClaim
	public BigDecimal computeWCSupplementalPayment(TPDPaycheck workPayment, BigDecimal avgPriorGrossWeeklyPayment);
	public boolean determineAndSetIsLate(Calendar payPeriodEnd, Calendar payReceived); //WCPaycheck
	public GregorianCalendar normalizeCalendarTime(GregorianCalendar calendar);
	public String getStateAbbrv();
	public String getStateName();
	public TimeZone getTimeZone();
	public SortedMap<Paycheck, Integer> sortPCHashMapByDate(Map<Paycheck, Integer> pcList);
	public SortedMap<WorkCompPaycheck, Integer> sortWCPCHashMapByDate(Map<WorkCompPaycheck, Integer> wcpcList);
	public boolean priorWagesIsComplete(ArrayList<Paycheck> priorWages); //CompClaim
}
