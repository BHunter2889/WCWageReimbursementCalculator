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
	protected boolean anyLatePay;
	

	public ReimbursementOverview(Claimant clmnt, TTDReimbursementSummary ttdRSumm, TPDReimbursementSummary tpdRSumm, Calendar fullDutyReturnDate) {
		this.claimant = clmnt;
		this.ttdRSumm = ttdRSumm;
		this.tpdRSumm = tpdRSumm;
		this.fullDutyReturnDate = fullDutyReturnDate;
		this.anyLatePay = false;
	}
	
	public ReimbursementOverview(){
		this.claimant = null;
		this.ttdRSumm = null;
		this.tpdRSumm = null;
		this.fullDutyReturnDate = null;
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
	}
	
	public boolean determineAnyLatePay(){
		ArrayList<WorkCompPaycheck> wcTTD = new ArrayList<WorkCompPaycheck>();
		ArrayList<WorkCompPaycheck> wcTPD = new ArrayList<WorkCompPaycheck>();
		if (this.containsTTD()) wcTTD = this.ttdRSumm.getWCPayments();
		if (this.containsTPD()) wcTPD = this.tpdRSumm.getWCPayments();
		if (wcTTD.isEmpty() && wcTPD.isEmpty()){
			this.anyLatePay = false;
			return false;
		}
		else if(wcTPD.isEmpty()){
			if (wcTTD.size() > 1){
				label:for(int i = 0, j=wcTTD.size()-1; i<j; i++, j--){
						if (wcTTD.get(i).getIsLate() || wcTTD.get(j).getIsLate()){
							this.anyLatePay = true;
							return true;
						}
						if(j - i == 2){
							i++;
							if(wcTTD.get(i).getIsLate()){
								this.anyLatePay = true;
								return true;
							}
							break label;
						}
					}
			}
			else {
				if(wcTTD.get(0).getIsLate()){
					this.anyLatePay = true;
					return true;
				}
			}
		}
		else if(wcTTD.isEmpty()){
			if (wcTPD.size() > 1){
				label:for(int i = 0, j=wcTPD.size()-1; i<j; i++, j--){
						if (wcTPD.get(i).getIsLate() || wcTPD.get(j).getIsLate()){
							this.anyLatePay = true;
							return true;
						}
						if(j - i == 2){
							i++;
							if(wcTPD.get(i).getIsLate()){
								this.anyLatePay = true;
								return true;
							}
							break label;
						}
					}
			}
			else {
				if(wcTPD.get(0).getIsLate()){
					this.anyLatePay = true;
					return true;
				}
			}
		}
		else{
			if (wcTPD.size() > 1){
				label:for(int i = 0, j=wcTPD.size()-1; i<j; i++, j--){
						if (wcTPD.get(i).getIsLate() || wcTPD.get(j).getIsLate()){
							this.anyLatePay = true;
							return true;
						}
						if(j - i == 2){
							i++;
							if(wcTPD.get(i).getIsLate()){
								this.anyLatePay = true;
								return true;
							}
							break label;
						}
					}
			}
			else {
				if(wcTPD.get(0).getIsLate()){
					this.anyLatePay = true;
					return true;
				}
			}
			
			if (wcTTD.size() > 1){
				label:for(int i = 0, j=wcTTD.size()-1; i<j; i++, j--){
						if (wcTTD.get(i).getIsLate() || wcTTD.get(j).getIsLate()){
							this.anyLatePay = true;
							return true;
						}
						if(j - i == 2){
							i++;
							if(wcTTD.get(i).getIsLate()){
								this.anyLatePay = true;
								return true;
							}
							break label;
						}
					}
			}
			else {
				if(wcTTD.get(0).getIsLate()){
					this.anyLatePay = true;
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setFullDutyReturnDate(Calendar fullDutyReturnDate){
		if (fullDutyReturnDate == null) return;
		this.fullDutyReturnDate = fullDutyReturnDate;
		this.computeTTDaNPNoLatePayCalculation();
		System.out.println("Full Duty Return Date: "+this.toStringFullDutyReturn());
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
		if (tpd.isEmpty()) return this.ttdRSumm.claimSummary.daysInjured;
		long daysTPD = 0;
		if (tpd.size() > 1){
		label:for(int i = 0, j=tpd.size()-1; i<j; i++, j--){
				daysTPD += tpd.get(i).getDaysInPayPeriod() + tpd.get(j).getDaysInPayPeriod();
				if(j - i == 2){
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
		long daysNotTPD = rs.claimSummary.getDaysInjured() - daysTPD;
		return daysNotTPD;
	}
	
	public void computeTTDaNPNoLatePayCalculation(){
		if (!this.containsTTD()) return;
		BigDecimal totalCalcPay = this.getTotalTTDCalcOwed();
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
	
	public String toStringFullDutyReturn(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		formatter.setTimeZone(new SimpleTimeZone(0, "Standard"));
		java.util.Date fDR = this.fullDutyReturnDate.getTime();
		return formatter.format(fDR);
	}
	
	public String getTotalString(){
		return "Total Not Paid: $"+this.getTotalNotPaid().toPlainString()+" | Total Work Comp Pay-To-Date: $"+this.getTotalWCPayToDate().toPlainString();
	}
	
	@Override
	public String toString(){
		return String.valueOf(claimant.id) +" "+ claimant.firstName +" "+ claimant.middleName +" "+ claimant.lastName +" "+ claimant.workPlace +" "+ claimant.state;
	}
	

}
