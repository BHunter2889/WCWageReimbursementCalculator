package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class TPDPaycheck extends Paycheck {
	protected BigDecimal wcCalcPay;
	protected StateLawCalculable sLC;

	public TPDPaycheck() {
		super();
		this.wcCalcPay = new BigDecimal("0");
		
	}

	@Deprecated
	public TPDPaycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay) {
		super(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay);
	}
	@Deprecated
	public TPDPaycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay, int eYear,
			int eMonth, int eDay) {
		super(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay, eYear, eMonth, eDay);
	}
	@Deprecated
	public TPDPaycheck(String grossAmount, GregorianCalendar paymentDate, GregorianCalendar payPeriodStart) {
		super(grossAmount, paymentDate, payPeriodStart);
		
	}

	public TPDPaycheck(String grossAmount, Calendar paymentDate, Calendar payPeriodStart, Calendar payPeriodEnd, StateLawCalculable sLC) {
		super(grossAmount, paymentDate, payPeriodStart, payPeriodEnd);
		this.sLC = sLC;
		this.wcCalcPay = new BigDecimal("0.00");
	}
	
	public void setWCCalcPay(String bdWCCalcPay){
		this.wcCalcPay = new BigDecimal(bdWCCalcPay);
		this.wcCalcPay = this.wcCalcPay.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public void setWCCalcPay(BigDecimal wcCalcPay){
		if (wcCalcPay.scale() != 2){
			this.wcCalcPay = wcCalcPay.setScale(2, RoundingMode.HALF_EVEN);
		}else {
			this.wcCalcPay = wcCalcPay;
		}
		
	}
	
	@Override
	public void setPayPeriodEnd(Date payPE){
		SimpleTimeZone tZ = new SimpleTimeZone(0, "Standard");
		GregorianCalendar pPE = new GregorianCalendar(tZ);
		pPE.setTime(payPE);
		this.payPeriodEnd = new MissouriCalculation().normalizeCalendarTime(pPE);
		this.setDaysInPayPeriod();
	}
	
	public void setStateLawCalculation(StateLawCalculable sLC){
		this.sLC = sLC;
	}
	
	public void computeWCCalcPay(BigDecimal aPGWP){
		this.wcCalcPay = sLC.computeWCSupplementalPayment(this, aPGWP);
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		System.out.println(this.wcCalcPay.toPlainString());
		
		String calc = "Calculated Supplemental Payment: ";
		long mPP = this.payPeriodEnd.getTimeInMillis() - this.payPeriodStart.getTimeInMillis();
		BigDecimal payPWeeks = (new BigDecimal(String.valueOf(mPP)).divide(new BigDecimal(String.valueOf(mWeek)), 8, RoundingMode.HALF_UP));
		BigDecimal two3 = new BigDecimal("2").divide(new BigDecimal("3"), 8, RoundingMode.HALF_UP);
		calc += "{((Avg. Gross Prior Weekly Wage) "+aPGWP+" x (Weeks in Pay Period) "+payPWeeks.toPlainString()+") - "+this.grossAmount.toPlainString()+") x "+two3.toPlainString();
		calc += " = "+this.wcCalcPay.toPlainString();
		this.mathLog.put(1, calc);
	}
	
	public BigDecimal getWCCalcPay(){
		return this.wcCalcPay;
	}
	
	public String toWCPayString(){
		String eol = System.lineSeparator();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy");
		formatter.setLenient(false);
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		
		java.util.Date payD = this.paymentDate.getTime();
		java.util.Date payPS = this.payPeriodStart.getTime();
		java.util.Date payPE = this.payPeriodEnd.getTime();
		
		return formatter.format(payPS) + " - " + formatter.format(payPE) + ": $" + this.getGrossAmount().toPlainString() + " paid on " + formatter.format(payD) + "."+eol+
				"Computed Work Comp Supplemental Payment: $" + this.wcCalcPay.toPlainString();
		
	}

}
