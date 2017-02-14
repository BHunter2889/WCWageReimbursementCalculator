package Classes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class ReimbursementOverview {
	
	protected Claimant claimant;
	protected TTDReimbursementSummary ttdRSumm;
	protected TPDReimbursementSummary tpdRSumm;
	protected Calendar fullDutyReturnDate;
	

	public ReimbursementOverview(Claimant clmnt, TTDReimbursementSummary ttdRSumm, TPDReimbursementSummary tpdRSumm, Calendar fullDutyReturnDate) {
		this.claimant = clmnt;
		this.ttdRSumm = ttdRSumm;
		this.tpdRSumm = tpdRSumm;
		this.fullDutyReturnDate = fullDutyReturnDate;
	}
	
	public ReimbursementOverview(){
		this.claimant = null;
		this.ttdRSumm = null;
		this.tpdRSumm = null;
		this.fullDutyReturnDate = null;
	}
	
	public void setClaimant(Claimant clmnt){
		this.claimant = clmnt;
	}
	
	public void setTTDRSumm(TTDReimbursementSummary ttdRSumm){
		this.ttdRSumm = ttdRSumm;
	}
	
	public void setTPDRSumm(TPDReimbursementSummary tpdRSumm){
		this.tpdRSumm = tpdRSumm;
	}
	
	public void setFullDutyReturnDate(Calendar fullDutyReturnDate){
		this.fullDutyReturnDate = fullDutyReturnDate;
		if (this.fullDutyReturnDate != null) computeDaysAndWeeksInjured();
	}
	
	public void computeDaysAndWeeksInjured(){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		Calendar dateInjured = new GregorianCalendar(this.ttdRSumm.getClaimSummary().stateLawCalculation.getTimeZone());
		dateInjured.setTimeInMillis(this.ttdRSumm.claimSummary.getDateInjured().getTimeInMillis());
		long milliDays = (this.fullDutyReturnDate.getTimeInMillis() - dateInjured.getTimeInMillis()) + mDay;
		long days = (long) Math.floor((milliDays) / mDay);
		long weeks = (long) Math.floor((milliDays) / mWeek);
		this.ttdRSumm.claimSummary.setDaysAndWeeksInjuredByFullDutyReturn(days, weeks);
	}
	
	public long getNumDaysNotInTPD(){
		ArrayList<TPDPaycheck> tpd = this.getTPDRSumm().getReceivedWorkPayments();
		long daysTPD = 0;
		for(int i = 0, j=tpd.size()-1; i<j; i++, j--){
			daysTPD += tpd.get(i).getDaysInPayPeriod() + tpd.get(j).getDaysInPayPeriod();
			if(j - i == 2){
				i++;
				daysTPD += tpd.get(i).getDaysInPayPeriod();
				return daysTPD;
			}
		}
		
		LocalDate end = this.fullDutyReturnDate.getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
		LocalDate start = this.ttdRSumm.claimSummary.getDateInjured().getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
		long days = (long) Period.between( start, end).getDays() + 1;
		long daysNotTPD = days - daysTPD;
		return daysNotTPD;
	}
	
	public Claimant getClaimant(){
		return this.claimant;
	}
	
	public TTDReimbursementSummary getTTDRSumm(){
		return this.ttdRSumm;
	}
	
	public TPDReimbursementSummary getTPDRSumm(){
		return this.tpdRSumm;
	}
	
	public BigDecimal getTotalNotPaid(){
		return this.ttdRSumm.getAmountNotPaid().add(this.getTPDRSumm().getAmountNotPaid());
	}
	
	public BigDecimal getTotalWCPayToDate(){
		return this.getTTDRSumm().getWCPayToDate().add(this.getTPDRSumm().getWCPayToDate());
	}
	
	public Calendar getFullDutyReturnDate(){
		return this.fullDutyReturnDate;
	}
	
	public boolean containsTTD(){
		return this.ttdRSumm != null;
	}
	
	public boolean containsTPD(){
		return this.tpdRSumm != null;
	}
	
	public boolean isFullDuty(){
		return this.fullDutyReturnDate != null;
	}
	
	public String getTotalString(){
		return "Total Not Paid: $"+this.getTotalNotPaid().toPlainString()+" | Total Work Comp Pay-To-Date: $"+this.getTotalWCPayToDate().toPlainString();
	}
	
	@Override
	public String toString(){
		return String.valueOf(claimant.id) +" "+ claimant.firstName +" "+ claimant.middleName +" "+ claimant.lastName +" "+ claimant.workPlace +" "+ claimant.state;
	}

}
