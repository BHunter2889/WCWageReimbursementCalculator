package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
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
		if (ttdRSumm == null) return;
		this.ttdRSumm = ttdRSumm;
		this.computeTTDaNPNoLatePayCalculation();
	}
	
	public void setTPDRSumm(TPDReimbursementSummary tpdRSumm){
		if (tpdRSumm == null) return;
		this.tpdRSumm = tpdRSumm;
	}
	
	public void setFullDutyReturnDate(Calendar fullDutyReturnDate){
		if (this.fullDutyReturnDate == null) return;
		this.fullDutyReturnDate = fullDutyReturnDate;
		computeDaysAndWeeksInjured();
	}
	
	public void computeDaysAndWeeksInjured(){
		if (this.fullDutyReturnDate == null) return;
		LocalDate end = this.fullDutyReturnDate.getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
		LocalDate start = null;
		if (this.containsTTD()){
			if(this.ttdRSumm.containsCompClaim()) start = this.ttdRSumm.claimSummary.getDateInjured().getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
			else return;
		}
		else if (this.containsTPD()){
			if(this.tpdRSumm.containsCompClaim()) start = this.tpdRSumm.claimSummary.getDateInjured().getTime().toInstant().atZone(new SimpleTimeZone(0, "Standard").toZoneId()).toLocalDate();
			else return;
		}
		Period timeInj = Period.between( start, end);
		long days = timeInj.getDays() + 1;
		long weeks = (long) Math.ceil(days/7);
		this.ttdRSumm.claimSummary.setDaysAndWeeksInjuredByFullDutyReturn(days, weeks);
	}
	
	public long getNumDaysNotInTPD(){
		this.computeDaysAndWeeksInjured();
		if (!this.containsTPD()){
			if (this.containsTTD()) return this.ttdRSumm.claimSummary.daysInjured;
			else try{
				throw new NullPointerException("Neither ReimbursementSummary contains a CompClaim with a valid Date of Injury.");
			} catch (NullPointerException e){
				e.printStackTrace();
				return -1;
			}
		}
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
		ReimbursementSummary rs = (this.containsTTD()) ? this.getTTDRSumm():this.getTPDRSumm();
		long daysNotTPD = rs.claimSummary.getDaysInjured() - daysTPD;
		return daysNotTPD;
	}
	
	public void computeTTDaNPNoLatePayCalculation(){
		BigDecimal totalCalcPay = this.getTotalTTDCalcOwed();
		BigDecimal amountNotPaid = new BigDecimal("0.00");
		BigDecimal wcPAID = this.ttdRSumm.getWCPayToDate(); 
		
		amountNotPaid = totalCalcPay.subtract(wcPAID).setScale(2, RoundingMode.HALF_EVEN);
		amountNotPaid = (this.ttdRSumm.amountNotPaid.compareTo(amountNotPaid) <= 0) ? amountNotPaid: this.ttdRSumm.getAmountNotPaid();
		this.ttdRSumm.setAmountNotPaid(amountNotPaid);
	}
	
	public BigDecimal getTotalTTDCalcOwed(){
		this.computeDaysAndWeeksInjured();
		long nonTPDInjDays = this.getNumDaysNotInTPD();
		if (nonTPDInjDays < 0) return new BigDecimal("0.00");
		if (this.ttdRSumm.calculatedWeeklyPayment.compareTo(new BigDecimal("0")) <= 0) this.ttdRSumm.calculateAndSetWeeklyPayment();
		BigDecimal dailyPay = this.ttdRSumm.calculatedWeeklyPayment.divide(new BigDecimal("7"), 3, RoundingMode.HALF_EVEN);
		return dailyPay.multiply(new BigDecimal(String.valueOf(nonTPDInjDays))).setScale(2, RoundingMode.HALF_EVEN);
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
