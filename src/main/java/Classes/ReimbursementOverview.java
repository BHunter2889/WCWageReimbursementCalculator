package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.SimpleTimeZone;

public class ReimbursementOverview {
	
	protected Claimant claimant;
	protected TTDReimbursementSummary ttdRSumm;
	protected TPDReimbursementSummary tpdRSumm;
	protected Calendar fullDutyReturnDate;
	protected Calendar lightDutyStartDate;
	protected boolean anyLatePay;
	

	public ReimbursementOverview(Claimant clmnt, TTDReimbursementSummary ttdRSumm, TPDReimbursementSummary tpdRSumm, Calendar fullDutyReturnDate, Calendar lightDutyReturnDate) {
		this.claimant = clmnt;
		this.ttdRSumm = ttdRSumm;
		this.tpdRSumm = tpdRSumm;
		this.fullDutyReturnDate = fullDutyReturnDate;
		this.lightDutyStartDate = lightDutyReturnDate;
		this.anyLatePay = false;
	}
	
	public ReimbursementOverview(){
		this.claimant = null;
		this.ttdRSumm = null;
		this.tpdRSumm = null;
		this.fullDutyReturnDate = null;
		this.lightDutyStartDate = null;
		this.anyLatePay = false;
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
		if (!this.tpdRSumm.determineAnyLatePay()) this.tpdRSumm.computeAmountNotPaidAndAnyLateCompensation();
		if (this.containsTTD()) this.computeTTDaNPNoLatePayCalculation();
		for (TPDPaycheck p : this.tpdRSumm.getReceivedWorkPayments()){
			System.out.println(p.toWCPayString());
		}
	}
	
	public boolean determineAnyLatePay(){
		if(this.containsTPD() && this.containsTTD())this.anyLatePay = this.ttdRSumm.determineAnyLatePay() || this.tpdRSumm.determineAnyLatePay();
		else if(this.containsTTD())this.anyLatePay = this.ttdRSumm.determineAnyLatePay();
		else if(this.containsTPD())this.anyLatePay = this.tpdRSumm.determineAnyLatePay();
		else this.anyLatePay = false;
			
		return anyLatePay;
	}
	
	public void setFullDutyReturnDate(Calendar fullDutyReturnDate){
		if (fullDutyReturnDate == null) return;
		this.fullDutyReturnDate = fullDutyReturnDate;
		this.computeTTDaNPNoLatePayCalculation();
		System.out.println("Full Duty Return Date: "+this.toStringFullDutyReturn());
	}
	
	public void setLightDutyStartDate(Calendar lightDutyStartDate){
		if (lightDutyStartDate == null) return;
		this.lightDutyStartDate = lightDutyStartDate;
		this.computeTTDaNPNoLatePayCalculation();
		System.out.println("Light Duty Return Date: "+this.toStringLightDutyStart());
	}
	
	public void computeDaysAndWeeksInjured(){
		if (this.fullDutyReturnDate == null) return;
		LocalDate end = this.fullDutyReturnDate.getTime().toInstant().atZone(new SimpleTimeZone(0, "UTC").toZoneId()).toLocalDate();
		LocalDate start = null;
		if (this.containsTTD()){
			if(this.ttdRSumm.containsCompClaim()) start = this.ttdRSumm.claimSummary.getDateInjured().getTime().toInstant().atZone(new SimpleTimeZone(0, "UTC").toZoneId()).toLocalDate();
			else return;
		}
		else if (this.containsTPD()){
			if(this.tpdRSumm.containsCompClaim()) start = this.tpdRSumm.claimSummary.getDateInjured().getTime().toInstant().atZone(new SimpleTimeZone(0, "UTC").toZoneId()).toLocalDate();
			else return;
		}
		else return;
		
		
		long days = ChronoUnit.DAYS.between(start, end) + 1;
		long weeks = (long) Math.ceil(days/7);
		if(this.containsTTD()) this.ttdRSumm.claimSummary.setDaysAndWeeksInjuredByFullDutyReturn(days, weeks);
		if(this.containsTPD()) this.tpdRSumm.claimSummary.setDaysAndWeeksInjuredByFullDutyReturn(days, weeks);
	}
	
	public long getDaysTTD(){
		if (this.hasLightDuty()){
			LocalDate start = this.ttdRSumm.claimSummary.dateInjured.getTime().toInstant().atZone(new SimpleTimeZone(0, "UTC").toZoneId()).toLocalDate();
			LocalDate end = this.lightDutyStartDate.getTime().toInstant().atZone(new SimpleTimeZone(0, "UTC").toZoneId()).toLocalDate();
			return ChronoUnit.DAYS.between(start, end) + 1;
		}
		else return 0;
				

	}
	//accounts for any days not covered by a return to TTD Status for more than a pay period.
	public long getNumDaysNotInTPD(){
		this.computeDaysAndWeeksInjured();
		this.checkForLDOverlap();
		if (!this.containsTPD()){
			if (this.containsTTD()){
				
				return this.hasLightDuty() ? this.getDaysTTD(): this.ttdRSumm.claimSummary.daysInjured;
			}
			else try{
				throw new NullPointerException("Neither ReimbursementSummary contains a CompClaim with a valid Date of Injury.");
			} catch (NullPointerException e){
				e.printStackTrace();
				return -1;
			}
		}
		ArrayList<TPDPaycheck> tpd = this.getTPDRSumm().getReceivedWorkPayments();
		if (tpd.isEmpty()){
			if (this.hasLightDuty()) return this.getDaysTTD();
					
			return this.ttdRSumm.claimSummary.daysInjured;
		}
		long daysTPD = 0;
		if (tpd.size() > 1){
	  label:for(int i = 0, j=tpd.size()-1; i<j; i++, j--){
				daysTPD += tpd.get(i).getDaysInPayPeriod() + tpd.get(j).getDaysInPayPeriod();
				if(i+2 == j){
					i++;
					daysTPD += tpd.get(i).getDaysInPayPeriod();
					break label;
				}
			}
		}
		else {
			daysTPD += tpd.get(0).getDaysInPayPeriod();
		}
		ReimbursementSummary rs = (this.containsTTD()) ? this.getTTDRSumm():this.getTPDRSumm();
		System.out.println("Days TPD: "+daysTPD);
		long daysNotTPD = Math.max(this.getDaysTTD(), rs.getClaimSummary().getDaysInjured() - daysTPD);

		System.out.println("Days TTD: "+daysNotTPD);
		return daysNotTPD;
	}
	
	public void computeTTDaNPNoLatePayCalculation(){
		if (!this.containsTTD()) return;
		BigDecimal totalCalcPay = this.getTotalTTDCalcOwed();
		System.out.println("TTD Total Calculated Pay no Late Pay: $"+totalCalcPay.toPlainString());
		BigDecimal amountNotPaid = new BigDecimal("0.00");
		BigDecimal wcPAID = this.ttdRSumm.getWCPayToDate(); 
		
		amountNotPaid = totalCalcPay.subtract(wcPAID).setScale(2, RoundingMode.HALF_EVEN);
		if (this.ttdRSumm.amountNotPaid == null) this.ttdRSumm.setAmountNotPaid(amountNotPaid);
		else {
			amountNotPaid = (!this.determineAnyLatePay()) ? amountNotPaid: this.ttdRSumm.getAmountNotPaid();
			this.ttdRSumm.setAmountNotPaid(amountNotPaid);
		}
		System.out.println("TTD AmountNotPaid: $"+this.ttdRSumm.getAmountNotPaid().toPlainString());
	}
	
	public BigDecimal getTotalTTDCalcOwed(){
		//this.computeDaysAndWeeksInjured();
		long nonTPDInjDays = this.getNumDaysNotInTPD();
		if (nonTPDInjDays <= 0) return new BigDecimal("0.00");
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
	public Calendar getLightDutyStartDate(){
		return this.lightDutyStartDate;
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
	
	public boolean hasLightDuty(){
		return this.lightDutyStartDate != null;
	}
	
	public boolean checkForLDOverlap(){
		boolean trimmed = false;
		if (!this.hasLightDuty() || !this.containsTPD()) return trimmed;
		if (this.checkForTPDLightDutyPayPeriodStart()) return trimmed;
		ArrayList<TPDPaycheck> tpd = this.tpdRSumm.receivedWorkPayments;
		if(tpd.isEmpty()) return false;
		if (tpd.size() > 1){
	  label:for(int i = 0, j=tpd.size()-1; i<j; i++, j--){
				if(tpd.get(i).getPayPeriodStart().before(lightDutyStartDate)){
					tpd.get(i).setPayPeriodStart(lightDutyStartDate);
					tpd.get(i).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
					trimmed = true;
					break label;
				}
				if(tpd.get(j).getPayPeriodStart().before(lightDutyStartDate)){
					tpd.get(j).setPayPeriodStart(lightDutyStartDate);
					tpd.get(j).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
					trimmed = true;
					break label;
				}
				if(i+2 == j){
					i++;
					if(tpd.get(i).getPayPeriodStart().before(lightDutyStartDate)){
						tpd.get(i).setPayPeriodStart(lightDutyStartDate);
						tpd.get(i).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
						trimmed = true;
						break label;
					}
				}
			}
		}
		else {
			if(tpd.get(0).getPayPeriodStart().before(lightDutyStartDate)){
				tpd.get(0).setPayPeriodStart(lightDutyStartDate);
				tpd.get(0).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
				trimmed = true;
			}
		}
		if (trimmed){
			this.tpdRSumm.setReceivedWorkPayments(tpd);
			if (this.containsTTD()) this.computeTTDaNPNoLatePayCalculation();
		}
		return trimmed;
	}
	
	public boolean checkForTPDLightDutyPayPeriodStart(){
		boolean lDPPS = false;
		if (!this.hasLightDuty() || !this.containsTPD()) return lDPPS;
		ArrayList<TPDPaycheck> tpd = this.tpdRSumm.receivedWorkPayments;
		if(tpd.isEmpty()) return false;
		if (tpd.size() > 1){
			for(int i = 0, j=tpd.size()-1; i<j; i++, j--){
				if(tpd.get(i).getPayPeriodStart().compareTo(lightDutyStartDate) == 0){
					lDPPS = true;
					tpd.get(i).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
					this.tpdRSumm.setReceivedWorkPayments(tpd);
					return lDPPS;
				}
				if(tpd.get(j).getPayPeriodStart().compareTo(lightDutyStartDate) == 0){			
					tpd.get(j).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
					lDPPS = true;
					this.tpdRSumm.setReceivedWorkPayments(tpd);
					return lDPPS;
				}
				if(i+2 == j){
					i++;
					if(tpd.get(i).getPayPeriodStart().compareTo(lightDutyStartDate) == 0){
						tpd.get(i).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
						lDPPS = true;
						this.tpdRSumm.setReceivedWorkPayments(tpd);
						return lDPPS;
					}
				}
			}
		}
		else {
			if(tpd.get(0).getPayPeriodStart().compareTo(lightDutyStartDate) == 0){
				lDPPS = true;
				tpd.get(0).computeWCCalcPay(this.tpdRSumm.claimSummary.getAvgPriorGrossWeeklyPayment());
				this.tpdRSumm.setReceivedWorkPayments(tpd);
				return lDPPS;
			}
		}
		
		return lDPPS;
	}
	
	public String toStringFullDutyReturn(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		java.util.Date fDR = this.fullDutyReturnDate.getTime();
		return formatter.format(fDR);
	}
	
	public String toStringLightDutyStart(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		java.util.Date lDR = this.lightDutyStartDate.getTime();
		return formatter.format(lDR);
	}
	
	public String getTotalString(){
		return "Total Not Paid: $"+this.getTotalNotPaid().toPlainString()+" | Total Work Comp Pay-To-Date: $"+this.getTotalWCPayToDate().toPlainString();
	}
	
	@Override
	public String toString(){
		return String.valueOf(claimant.id) +" "+ claimant.firstName +" "+ claimant.middleName +" "+ claimant.lastName +" "+ claimant.workPlace +" "+ claimant.state;
	}
	

}
