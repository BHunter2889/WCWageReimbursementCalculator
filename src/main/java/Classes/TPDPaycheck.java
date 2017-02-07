package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class TPDPaycheck extends Paycheck {
	protected BigDecimal wcCalcPay;

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

	public TPDPaycheck(String grossAmount, Calendar paymentDate, Calendar payPeriodStart, Calendar payPeriodEnd, String bdWCCalcPay) {
		super(grossAmount, paymentDate, payPeriodStart, payPeriodEnd);
		this.wcCalcPay = new BigDecimal(bdWCCalcPay);
		this.wcCalcPay = this.wcCalcPay.setScale(2, RoundingMode.HALF_EVEN);
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
	
	public void computeWCCalcPay(StateLawCalculable sLC, BigDecimal aPGWP){
		this.wcCalcPay = sLC.computeWCSupplementalPayment(this, aPGWP);
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
