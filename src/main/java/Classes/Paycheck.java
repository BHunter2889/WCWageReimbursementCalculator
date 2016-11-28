package Classes;

import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;


public class Paycheck implements Comparable<Calendar> {
	//fields
	protected BigDecimal grossAmount;
	protected Calendar paymentDate;
	protected Calendar payPeriodStart;
	protected Calendar payPeriodEnd;

	//null constructor
	public Paycheck(){
		this.grossAmount = null;
		this.paymentDate = null;
		this.payPeriodStart = null;
		this.payPeriodEnd = null;
	}
	
	//standard constructor
	public Paycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay) {
		BigDecimal gA = new BigDecimal(grossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
		
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		this.paymentDate = new GregorianCalendar(iYear, iMonth-1, iDay);
		this.paymentDate.setTimeZone(tz1);
		
		this.payPeriodStart = new GregorianCalendar(sYear, sMonth-1, sDay);
		this.payPeriodStart.setTimeZone(tz1);

		this.payPeriodEnd = new GregorianCalendar (iYear, iMonth-1, iDay);
		this.payPeriodEnd.setTimeZone(tz1);
		
	}

	//constructor to use if payment date is not the same as end date
	public Paycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay, int eYear, int eMonth, int eDay) {
		BigDecimal gA = new BigDecimal(grossAmount);
		this.grossAmount = gA.setScale(2, RoundingMode.HALF_EVEN);
		
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		this.paymentDate = new GregorianCalendar(iYear, iMonth-1, iDay);
		this.paymentDate.setTimeZone(tz1);
		
		this.payPeriodStart = new GregorianCalendar(sYear, sMonth-1, sDay);
		this.payPeriodStart.setTimeZone(tz1);

		this.payPeriodEnd = new GregorianCalendar (eYear, eMonth-1, eDay);
		this.payPeriodEnd.setTimeZone(tz1);
		
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
	public Paycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart, GregorianCalendar payPeriodEnd) {
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
		
		Date payD = (Date) this.paymentDate.getTime();
		Date payPS = (Date) this.payPeriodStart.getTime();
		Date payPE = (Date) this.payPeriodEnd.getTime();
		
		return formatter.format(payPS) + " - " + formatter.format(payPE) + ": $" + this.getGrossAmount() + " paid on " + formatter.format(payD) + ".";
		
	}

	public void setGrossAmount(BigDecimal grossAmnt) {
		if (grossAmnt.scale() != 2){
			this.grossAmount = grossAmnt.setScale(2, RoundingMode.HALF_EVEN);
		}else {
			this.grossAmount = grossAmnt;
		}
	}

	public void setPaymentDate(Date payDate) {
		this.paymentDate = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		this.paymentDate.setTime(payDate);
	}
	
	public void setPayPeriodStart(Date payPS) {
		this.payPeriodStart = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		this.payPeriodStart.setTime(payPS);
	}
	
	public void setPayPeriodEnd(Date payPE) {
		this.payPeriodEnd = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		this.payPeriodEnd.setTime(payPE);
	}
}
