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
	protected static final Comparator<WorkCompPaycheck> WC_COMPARATOR = new Comparator<WorkCompPaycheck>(){
		@Override
		public int compare(WorkCompPaycheck p1, WorkCompPaycheck p2) {
			GregorianCalendar epoch = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
			epoch.setTimeInMillis(1000*60*60*24);
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
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
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
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
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
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
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
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
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
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
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
	public WorkCompPaycheck(String grossAmount, Calendar pRD, Calendar pPS, Calendar pPE, boolean isContested, StateLawCalculable sLC, Calendar pD) {
		super(grossAmount, pD, pPS, pPE);
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
		this.stateLawCalculation = sLC;
		GregorianCalendar epoch = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		this.payReceivedDate = pRD;
		this.isContested = isContested;
		determineAndSetIsLate();
		this.fullTimeHours = false;
		this.isLate = false;
		this.amountStillOwed = new BigDecimal("0");
		this.contestResolvedDate = (pPE.compareTo(epoch) >= 0) ? pPE: pD;
	}
	
	public WorkCompPaycheck() {
		super();
		this.mathLog.put(1, "N/A: Pay Period Dates Not Provided.");
		this.mathLog.put(3, "N/A: Pay Period Dates Not Provided.");
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
			Object[] bM = this.stateLawCalculation.determineAndSetIsLate(this.contestResolvedDate, this.payReceivedDate);
			this.isLate = (boolean) bM[0];
			BigDecimal[] bD = {new BigDecimal(String.valueOf(bM[1]))};
			logMath(1, bD);
		}
		else if(this.isContested && this.contestResolvedDate == null){
			System.out.println("No Resolution Date for the Contested paycheck is set (date contest was settled or resolved in court.");
			System.out.println("Defaults to assuming contest has not been resolved. Pay cannot be considered late until 30 days after resolution of contest.");
			this.isLate = false;
		}
		else{
			Object[] bM = stateLawCalculation.determineAndSetIsLate(this.payPeriodEnd, this.payReceivedDate);
			this.isLate = (boolean) bM[0];
			BigDecimal[] bD = {new BigDecimal(String.valueOf(bM[1]))};
			logMath(1, bD);
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
		BigDecimal[] bD = {calculatedWeeklyPayment};
		logMath(2, bD);
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
		BigDecimal[] bD = {lPC, calculatedWeeklyPayment};
		logMath(3, bD);
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
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		//String eol = System.getProperty("line.separator");
		
		java.util.Date payD = this.paymentDate.getTime();		
		java.util.Date payRD = this.payReceivedDate.getTime();
				
		return "Payment Received on: " + formatter.format(payRD) + ": $" + this.getGrossAmount() + " paid on " + formatter.format(payD);
	}
	
	public String toFullString(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy");
		formatter.setLenient(false);
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		String eol = System.getProperty("line.separator");
		
		java.util.Date payD = this.paymentDate.getTime();
		java.util.Date payPS = this.payPeriodStart.getTime();
		java.util.Date payPE = this.payPeriodEnd.getTime();
		java.util.Date payRD = this.payReceivedDate.getTime();
		
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
	
	@Override
	public void setPayPeriodStart(Date pPS){
		GregorianCalendar payPS = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		payPS.setTime(pPS);
		this.payPeriodStart = this.stateLawCalculation.normalizeCalendarTime(payPS);
	}
	
	@Override
	public void setPayPeriodEnd(Date pPE){
		GregorianCalendar payPE = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		payPE.setTime(pPE);
		this.payPeriodEnd = this.stateLawCalculation.normalizeCalendarTime(payPE);
	}
	
	@Override
	public void setPaymentDate(Date pD){
		GregorianCalendar payD = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		payD.setTime(pD);
		this.paymentDate = this.stateLawCalculation.normalizeCalendarTime(payD);
	}

	public void setPayRecievedDate(Date payReceived) {
		GregorianCalendar pRD = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		pRD.setTime(payReceived);
		this.payReceivedDate = this.stateLawCalculation.normalizeCalendarTime(pRD);
	}
	
	public void setContestResolutionDate(Calendar contestRslvdDate){
		this.contestResolvedDate = this.stateLawCalculation.normalizeCalendarTime(contestRslvdDate);
	}
	
	public void setContestResolutionDate(Date contestResolved) {
		GregorianCalendar cRD = new GregorianCalendar(this.stateLawCalculation.getTimeZone());
		cRD.setTime(contestResolved);
		this.contestResolvedDate = this.stateLawCalculation.normalizeCalendarTime(cRD); //Does not rely on any TimeZone or locale information, so Missouri was used arbitrarily
		
	}
	
	public Calendar getContestResolutionDate(){
		return this.contestResolvedDate;
	}
	
	public void logMath(int num, BigDecimal[] bD){
		switch(num){
			case 1: String late = "Is Payment More that 30 days late?: ";
				SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
				formatter.setLenient(false);
				formatter.setTimeZone(this.stateLawCalculation.getTimeZone());
				
				late += "(Pay Period End) "+formatter.format(this.payPeriodEnd.getTime()+" - ");
				late += "(Pay Received Date) "+formatter.format(this.payReceivedDate.getTime()+" = ");
				late += this.isLate ? bD[0].toBigInteger().toString()+" (Late)": bD[0].toBigInteger().toString()+" (Not Late)";
				
				mathLog.put(1, late);
			
			case 2: String aSO = "Amount Still Owed: ";
				GregorianCalendar epoch = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
				epoch.setTimeInMillis(1000*60*60*24);
				boolean pPS = this.getPayPeriodStart().compareTo(epoch) > 0;
				if(pPS) aSO += "(Calc. WeeklyPayment) "+bD[0].toPlainString()+" - (Payment Amnt.) "+this.grossAmount.toPlainString()+" = "+this.amountStillOwed.toPlainString();
				else aSO += "Amount Still Owed could not be computed per Payment due to unknown Pay Period Dates.";
			
				mathLog.put(2, aSO);
				
			case 3: String latePay = "Late Payment Added: ";
				latePay+= "(10% of Calculated Weekly Payment Added) ((0.10 x "+bD[1].toPlainString()+") + "+bD[1].toPlainString()+") - ";
				latePay+= "(Amount Paid) "+this.grossAmount.toPlainString()+" = "+bD[0].toPlainString()+"(Amount added to Amount Still Owed)";
				
				mathLog.put(3, latePay);
		}
	}
}
