package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class WorkCompPaycheck extends Paycheck {
	//additional fields
	protected boolean isContested;
	//moved to StateLawCalulable classes: protected int stateDaysToLate;
	protected boolean isLate;
	protected Calendar payReceivedDate;
	protected BigDecimal amountStillOwed;
	protected boolean fullTimeHours;
	protected Calendar contestResolvedDate = null;
	protected StateLawCalculable stateLawCalculation;//defaults to false
	protected static final Comparator<WorkCompPaycheck> PPS_COMPARATOR = new Comparator<WorkCompPaycheck>(){
		@Override
		public int compare(WorkCompPaycheck p1, WorkCompPaycheck p2) {
			GregorianCalendar epoch = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
			epoch.setTimeInMillis(0);
			boolean pPS = p1.getPayPeriodStart().compareTo(epoch) > 0 && p2.getPayPeriodStart().compareTo(epoch) > 0;
			return pPS ? p1.compareTo(p2.getPayPeriodStart()): p1.getPaymentDate().compareTo(p2.getPaymentDate());
		}
	};
	
/* NOT using these two constructors at the moment due to unwieldy parameters, using Calendar constructors instead.
 * Will either delete or implement later...
 * 
 	/* constructor if payment date is same as pay period end date (if employer or WC Insurer has contested payment at 
 	any point, isContested should be true, otherwise false.
 	public WorkCompPaycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay) {
		super(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay);
		//  Auto-generated constructor stub
	}
	
	/* constructor if payment date and pay period date are different (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.)
	public WorkCompPaycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay,
			int eYear, int eMonth, int eDay) {
		super(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay, eYear, eMonth, eDay);
		//  Auto-generated constructor stub
	}
*/	
	/*CONSTRUCTOR: Calendar constructor, pD and ppE are the same, payReceived is same as payDate (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.*/
	public WorkCompPaycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart, boolean isContested) {
		super(grossAmount, paymentDate, payPeriodStart);
		this.payReceivedDate = paymentDate;
		this.isContested = isContested;
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
	}
	
	/*CONSTRUCTOR: Calendar constructor, pPE and pD are different, payReceived is same as payDate (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.)*/
	public WorkCompPaycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart, GregorianCalendar payPeriodEnd, boolean isContested) {
		super(grossAmount, paymentDate, payPeriodStart, payPeriodEnd);
		this.payReceivedDate = paymentDate;
		this.isContested = isContested;
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
	}

	/*CONSTRUCTOR: (to set stateDaystoLate) Calendar constructor, pD and ppE are the same, payReceived is same as payDate (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.*/
	public WorkCompPaycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart, boolean isContested, StateLawCalculable sLC) {
		super(grossAmount, paymentDate, payPeriodStart);
		this.payReceivedDate = paymentDate;
		this.isContested = isContested;
		this.stateLawCalculation = sLC;
		determineAndSetIsLate();
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
	}
	
	/*CONSTRUCTOR: (to set stateDaystoLate) Calendar constructor, pPE and pD are different, payReceived is same as payDate (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.)*/
	public WorkCompPaycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart, GregorianCalendar payPeriodEnd, boolean isContested, StateLawCalculable sLC) {
		super(grossAmount, paymentDate, payPeriodStart, payPeriodEnd);
		this.payReceivedDate = paymentDate;
		this.isContested = isContested;
		this.stateLawCalculation = sLC;
		determineAndSetIsLate();
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
	}
	
	/*CONSTRUCTOR: (to set stateDaystoLate) Calendar constructor, pD and ppE are the same, payReceived IS NOT payDate (payDate becomes irrelevant) (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.*/
	public WorkCompPaycheck(String grossAmount, GregorianCalendar payReceivedDate, GregorianCalendar payPeriodStart, boolean isContested, StateLawCalculable sLC, GregorianCalendar paymentDate) {
		super(grossAmount, paymentDate, payPeriodStart);
		this.payReceivedDate = payReceivedDate;
		this.isContested = isContested;
		this.stateLawCalculation = sLC;
		determineAndSetIsLate();
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
	}
	
	/*CONSTRUCTOR: (to set stateDaystoLate) Calendar constructor, pPE and pD are different, payReceived IS NOT payDate (payDate becomes irrelevant) (if employer or WC Insurer has contested payment at 
	any point, isContested should be true, otherwise false.)*/
	public WorkCompPaycheck(String grossAmount, GregorianCalendar payReceivedDate, GregorianCalendar payPeriodStart, GregorianCalendar payPeriodEnd, boolean isContested, StateLawCalculable sLC, GregorianCalendar paymentDate) {
		super(grossAmount, paymentDate, payPeriodStart, payPeriodEnd);
		GregorianCalendar epoch = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		this.payReceivedDate = payReceivedDate;
		this.isContested = isContested;
		this.stateLawCalculation = sLC;
		determineAndSetIsLate();
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
		this.contestResolvedDate = (payPeriodEnd.compareTo(epoch) >= 0) ? payPeriodEnd: paymentDate;
	}
	
	public WorkCompPaycheck() {
		super();
		this.isContested = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
		this.fullTimeHours = false;
		this.payReceivedDate = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
		this.stateLawCalculation = null;
		this.contestResolvedDate = this.payPeriodEnd;
		
	}

	//methods
	public void setStateLawCalculation(StateLawCalculable sLC){
		this.stateLawCalculation = sLC;
	}
	
	//if set, uses isContested and contestResolvedDate to determine if the date on which pay was received was late, otherwise uses payPE and payRD.
	public void determineAndSetIsLate(){
		
		if (this.isContested && this.contestResolvedDate != null){
			this.isLate = this.stateLawCalculation.determineAndSetIsLate(this.contestResolvedDate, this.payReceivedDate);
		}
		else if(this.isContested && this.contestResolvedDate == null){
			System.out.println("No Resolution Date for the Contested paycheck is set (date contest was settled or resolved in court.");
			System.out.println("Defaults to assuming contest has not been resolved. Pay cannot be considered late until 30 days after resolution of contest.");
			this.isLate = false;
		}
		else{
			this.isLate = stateLawCalculation.determineAndSetIsLate(this.payPeriodEnd, this.payReceivedDate);
		}
	}
	
	public void computeAmountStillOwed(BigDecimal calculatedWeeklyPayment){
		BigDecimal aSO = calculatedWeeklyPayment.subtract(this.grossAmount);
		BigDecimal zero = new BigDecimal("0");
		if(aSO.compareTo(zero) <= 0){
			return;
		}
		aSO = aSO.setScale(2, RoundingMode.HALF_EVEN);
		this.amountStillOwed = this.amountStillOwed.add(aSO);
	}
	
	public void computeAnyAddtionalLatePaymentCompensation(BigDecimal calculatedWeeklyPayment){
		this.computeAmountStillOwed(calculatedWeeklyPayment);
		this.determineAndSetIsLate();
		if(!this.isLate){
			return;
		}
		BigDecimal lPC = this.stateLawCalculation.computeAnyLatePaymentCompensation(this.grossAmount, calculatedWeeklyPayment);
		BigDecimal zero = new BigDecimal("0");
		if(lPC.compareTo(zero) <= 0){
			return;
		}
		
		BigDecimal lateAdjustment = this.amountStillOwed.add(lPC);
		this.amountStillOwed = lateAdjustment.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public void setIsContested(boolean isContested){
		this.isContested = isContested;
	}
	
	public void setPayRecievedDate(GregorianCalendar payReceivedDate){
		this.payReceivedDate = payReceivedDate;
	}
	
	//defaults to false. if fullTimeHours is set to true, computedAmount will be 0.00;
	public void setFullTimeHours(boolean fTH){
		this.fullTimeHours = fTH;
		/* if(this.fullTimeHours){
			String z = "0.00";
			BigDecimal zero = new BigDecimal(z);
			this.paidAmount = zero.setScale(2, RoundingMode.UNNECESSARY);
		} */
	}
	
	protected void setAmountStillOwed(BigDecimal aSO){
		if (aSO.compareTo(new BigDecimal("-1")) == 0) this.amountStillOwed = new BigDecimal("0");
		else this.amountStillOwed = aSO;
	}
	
	public boolean getIsContested(){
		return this.isContested;
	}
	
	public StateLawCalculable getStateLawCalulationClass(){
		return this.stateLawCalculation;
	}
	
	public boolean getIsLate(){
		return this.isLate;
	}
	
	public Calendar getPayReceivedDate(){
		return this.payReceivedDate;
	}
	
	public boolean getFullTimeHours(){
		return this.fullTimeHours;
	}
	public BigDecimal getAmountStillOwed(){
		return this.amountStillOwed;
	}
	
	@Override
	public String toString(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy");
		formatter.setLenient(false);
		String eol = System.getProperty("line.separator");
		
		Date payD = (Date) this.paymentDate.getTime();
		Date payPS = (Date) this.payPeriodStart.getTime();
		Date payPE = (Date) this.payPeriodEnd.getTime();
		Date payRD = (Date) this.payReceivedDate.getTime();
		
		String contested = "";
		if(this.isContested){
			contested = "Yes. ";
		}
		else{
			contested = "No. ";
		}
		String late = "";
		if(this.isLate){
			late = "Yes. ";
		}
		else{
			late = "No. ";
		}
		String fTH = "";
		if(this.fullTimeHours){
			fTH = "Yes. ";
		}
		else{
			fTH = "No. ";
		}
		
		return formatter.format(payPS) + " - " + formatter.format(payPE) + ": $" + this.getGrossAmount() + " paid on " + formatter.format(payD) + "." + eol + 
				"Payment Received on: " + formatter.format(payRD) + ". Payment Contested: " + contested + "Late Payment: " + late + "Worked Regular Status Hours: " + fTH;
		
	}

	public void setIsLate(boolean isLate) {
		this.isLate = isLate;
		
	}

	public void setPayRecievedDate(Date payReceived) {
		SimpleTimeZone tZ = new SimpleTimeZone(0, "Standard");
		tZ.setDSTSavings(0);
		GregorianCalendar pRD = new GregorianCalendar(tZ);
		pRD.setTime(payReceived);
		this.payReceivedDate = new MissouriCalculation().normalizeCalendarTime(pRD);
	}
	
	public void setContestResolutionDate(GregorianCalendar contestResolved){
		this.contestResolvedDate = contestResolved;
	}
	
	public void setContestResolutionDate(Date contestResolved) {
		SimpleTimeZone tZ = new SimpleTimeZone(0, "Standard");
		tZ.setDSTSavings(0);
		GregorianCalendar pRD = new GregorianCalendar(tZ);
		pRD.setTime(contestResolved);
		this.payReceivedDate = new MissouriCalculation().normalizeCalendarTime(pRD); //Does not rely on any TimeZone or locale information, so Missouri was used arbitrarily
		
	}
	
	public Calendar getContestResolutionDate(){
		return this.contestResolvedDate;
	}
}
