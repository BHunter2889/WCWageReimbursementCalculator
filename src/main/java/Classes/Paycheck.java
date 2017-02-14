package Classes;

import java.util.Calendar;
import java.util.Comparator;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.Temporal;


public class Paycheck implements Comparable<Calendar> {
	//fields
	protected BigDecimal grossAmount;
	protected Calendar paymentDate;
	protected Calendar payPeriodStart;
	protected Calendar payPeriodEnd;
	protected static final Comparator<Paycheck> PPS_COMPARATOR = new Comparator<Paycheck>(){
		@Override
		public int compare(Paycheck p1, Paycheck p2) {
	
			return p1.compareTo(p2.getPayPeriodStart());
		}
	};

	//null constructor
	public Paycheck(){
		this.grossAmount = new BigDecimal("0");
		this.paymentDate = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
		this.paymentDate.setTimeInMillis(Long.MAX_VALUE); 
		this.payPeriodStart = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
		this.payPeriodStart.setTimeInMillis(Long.MIN_VALUE);
		this.payPeriodEnd = new GregorianCalendar(new SimpleTimeZone(0, "Standard"));
		this.payPeriodEnd.setTimeInMillis(Long.MAX_VALUE);
	}
	
	//standard constructor
	public Paycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay) {
		BigDecimal gA = new BigDecimal(grossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
		
		this.paymentDate = new GregorianCalendar(iYear, iMonth-1, iDay);
		
		this.payPeriodStart = new GregorianCalendar(sYear, sMonth-1, sDay);

		this.payPeriodEnd = new GregorianCalendar (iYear, iMonth-1, iDay);
		
	}

	//constructor to use if payment date is not the same as end date
	public Paycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay, int eYear, int eMonth, int eDay) {
		BigDecimal gA = new BigDecimal(grossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
		
		this.paymentDate = new GregorianCalendar(iYear, iMonth-1, iDay);
		
		this.payPeriodStart = new GregorianCalendar(sYear, sMonth-1, sDay);

		this.payPeriodEnd = new GregorianCalendar (eYear, eMonth-1, eDay);
		
	}
	
	//Calendar constructor, pD and ppE are the same
	public Paycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart) {
		BigDecimal gA = new BigDecimal(grossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
		
		this.paymentDate = paymentDate;
		
		this.payPeriodStart = payPeriodStart;

		this.payPeriodEnd = paymentDate;
		
	}
	
	//Caldendar constructor, pPE and pD are different
	public Paycheck(String grossAmount, Calendar paymentDate, Calendar payPeriodStart, Calendar payPeriodEnd) {
		BigDecimal gA = new BigDecimal(grossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
		
		this.paymentDate = paymentDate;
		
		this.payPeriodStart = payPeriodStart;

		this.payPeriodEnd = payPeriodEnd;
		
	}
	
	@Override
	public int compareTo(Calendar p){
		if(this.payPeriodStart.compareTo(p) > 0){
			return 1;
		}
		else if(this.payPeriodStart.compareTo(p) < 0){
			return -1;
		}
		else{
			return 0;
		}
	}
	
	
	public BigDecimal getGrossAmount(){
		return this.grossAmount;
	}
	
	public String toPaymentDateString(){
		int year = this.paymentDate.get(Calendar.YEAR);
		int month = this.paymentDate.get(Calendar.MONTH);
		int day = this.paymentDate.get(Calendar.DATE);
		return month + "-" + day + "-" + year;
	}
	
	public Calendar getPaymentDate()
	{
		return this.paymentDate;
	}
	
	public Calendar getPayPeriodStart()
	{
		return this.payPeriodStart;
	}
	
	public Calendar getPayPeriodEnd()
	{
		return this.payPeriodEnd;
	}
	
	public void setGrossAmount(String newGrossAmount){
		BigDecimal gA = new BigDecimal(newGrossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public void setPaymentDate(int year, int month, int day){
		this.paymentDate.set(year, month, day);
	}
	
	public void setPayPeriodStart(int year, int month, int day){
		this.payPeriodStart.set(year, month, day);
	}
	
	public void setPayPeriodEnd(int year, int month, int day){
		this.payPeriodEnd.set(year, month, day);
	}

	public void setPaymentDate(Calendar payDate){
		this.paymentDate = payDate;
	}
	
	public void setPayPeriodStart(Calendar payPS){
		this.payPeriodStart = payPS;
	}
	
	public void setPayPeriodEnd(Calendar payPE){
		this.payPeriodStart = payPE;
	}

	public String toString(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy");
		formatter.setLenient(false);
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		
		java.util.Date payD = this.paymentDate.getTime();
		java.util.Date payPS = this.payPeriodStart.getTime();
		java.util.Date payPE = this.payPeriodEnd.getTime();
		
		return formatter.format(payPS) + " - " + formatter.format(payPE) + ": $" + this.getGrossAmount().toPlainString() + " paid on " + formatter.format(payD) + ".";
		
	}

	public void setGrossAmount(BigDecimal grossAmnt) {
		if (grossAmnt.scale() != 2){
			this.grossAmount = grossAmnt.setScale(2, RoundingMode.HALF_EVEN);
		}else {
			this.grossAmount = grossAmnt;
		}
	}

	public void setPaymentDate(Date payDate) {
		SimpleTimeZone tZ = new SimpleTimeZone(0, "Standard");
		GregorianCalendar pD = new GregorianCalendar(tZ);
		pD.setTime(payDate);
		this.paymentDate = new MissouriCalculation().normalizeCalendarTime(pD); 
	}
	
	public void setPayPeriodStart(Date payPS) {
		SimpleTimeZone tZ = new SimpleTimeZone(0, "Standard");
		GregorianCalendar pPS = new GregorianCalendar(tZ);
		pPS.setTime(payPS);
		this.payPeriodStart = new MissouriCalculation().normalizeCalendarTime(pPS);
	}
	
	public void setPayPeriodEnd(Date payPE) {
		SimpleTimeZone tZ = new SimpleTimeZone(0, "Standard");
		GregorianCalendar pPE = new GregorianCalendar(tZ);
		pPE.setTime(payPE);
		this.payPeriodEnd = new MissouriCalculation().normalizeCalendarTime(pPE);
	}
	
	public long getDaysInPayPeriod(){
		LocalDate start = this.payPeriodStart.getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
		LocalDate end = this.payPeriodEnd.getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
		long days = (long) Period.between( start, end).getDays() + 1;
		return days;
		
	}
	
	public boolean doPayPeriodsOverlap(Paycheck pc){
		boolean overlap = false;
		
		overlap = this.payPeriodStart.after(pc.getPayPeriodStart()) && this.payPeriodStart.before(pc.getPayPeriodEnd());
		
		return overlap;
	}
}
