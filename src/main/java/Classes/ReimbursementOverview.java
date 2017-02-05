package Classes;

import java.math.BigDecimal;
import java.util.Calendar;

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
