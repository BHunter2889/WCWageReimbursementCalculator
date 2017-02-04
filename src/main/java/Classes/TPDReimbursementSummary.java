package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class TPDReimbursementSummary extends ReimbursementSummary {
	//additional fields
	protected ArrayList<Paycheck> receivedWorkPayments;
	//unnecessary (this varies week to week based on hours worked) protected BigDecimal computedWCSupplementalPayment;
	
	public TPDReimbursementSummary(){
		super();
		this.receivedWorkPayments = new ArrayList<Paycheck>();
	}
	
	public TPDReimbursementSummary(CompClaim claim, StateLawCalculable stateLawCalc) {
		super(claim, stateLawCalc);
		this.receivedWorkPayments = null;
	}
	
	public TPDReimbursementSummary(CompClaim claim, StateLawCalculable stateLawCalc, ArrayList<Paycheck> receivedWorkPayments) {
		super(claim, stateLawCalc);
		this.receivedWorkPayments = receivedWorkPayments;
	}
	
	public TPDReimbursementSummary(ReimbursementSummary rsumm){
		super(rsumm);
		this.receivedWorkPayments = null;
	}
	
	public TPDReimbursementSummary(ReimbursementSummary rsumm, ArrayList<Paycheck> receivedWorkPayments){
		super(rsumm);
		this.receivedWorkPayments = receivedWorkPayments;
	}
	public TPDReimbursementSummary(BigDecimal calculatedWeeklyPayment, CompClaim claimSummary, BigDecimal amountNotPaid, ArrayList<WorkCompPaycheck> wcPayments, ArrayList<Paycheck> receivedWorkPayments) {
		super(calculatedWeeklyPayment, claimSummary, amountNotPaid, wcPayments);
		this.receivedWorkPayments = receivedWorkPayments;
		this.sortPaychecksByDate();
	}
	
	@Override
	//calculates and sets amountNotPaid total and sets the amountStillOwed for each pay period entered in wcPayments
	//also determines if wcPC was late and adds and late payment owed to wcPC grossAmnt
	public void computeAmountNotPaidAndAnyLateCompensation(){
		String aNP = "0.00";
		BigDecimal amountNotPaid = new BigDecimal(aNP);
		Paycheck pcheck = null;
		for (WorkCompPaycheck p : this.wcPayments){
			label:for (Paycheck pc : this.receivedWorkPayments){
				if (p.getPayPeriodEnd().compareTo(pc.getPayPeriodEnd()) == 0){
					pcheck = pc;
					break label;
				}
			}
			BigDecimal calcSuppPayment = this.stateLawCalculation.computeWCSupplementalPayment(pcheck, this.claimSummary.getAvgPriorGrossWeeklyPayment());
			p.computeAnyAddtionalLatePaymentCompensation(calcSuppPayment);
			BigDecimal aSO = p.getAmountStillOwed();
			amountNotPaid = amountNotPaid.add(aSO);
		}
		amountNotPaid = amountNotPaid.setScale(2, RoundingMode.HALF_EVEN);
		this.amountNotPaid = amountNotPaid;
	}
	
	public void sortPaychecksByDate(){
		Collections.sort(this.receivedWorkPayments, Paycheck.PPS_COMPARATOR);
	}
	
	public void addPaycheck(Paycheck pc){
		this.receivedWorkPayments.add(pc);
		this.sortPaychecksByDate();
		this.computeAmountNotPaidAndAnyLateCompensation();
	}
	
	public Paycheck trimFirstWorkPayment(Paycheck pc, String bdTotalHrsWorked, String bdWeekInjHrsWorked){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		BigDecimal totalHrs = new BigDecimal(bdTotalHrsWorked);
		BigDecimal weekInjHrs = new BigDecimal(bdWeekInjHrsWorked);
		BigDecimal wkInjHrsPrcnt = totalHrs.divide(totalHrs.subtract(totalHrs.subtract(weekInjHrs)));
		BigDecimal trimmedGA = pc.getGrossAmount().multiply(wkInjHrsPrcnt);
		trimmedGA = trimmedGA.setScale(2, RoundingMode.HALF_EVEN);
		
		long mPPS = this.claimSummary.getPriorWeekStart().getTimeInMillis() + mWeek;
		Calendar pcPPS = this.claimSummary.getPriorWeekStart();
		pcPPS.setTimeInMillis(mPPS);
		pc.setPayPeriodStart(pcPPS);;
		pc.setGrossAmount(trimmedGA);
		return pc;
	}
	
	public void updateReceivedWorkPayments(ArrayList<Paycheck> pchecks){
		this.receivedWorkPayments = pchecks;
		this.sortPaychecksByDate();
		this.computeAmountNotPaidAndAnyLateCompensation();
	}
	
	public BigDecimal getWorkPayToDate(){
		String wPTD = "0.00";
		BigDecimal workPTD = new BigDecimal(wPTD);
		for(Paycheck p : this.receivedWorkPayments){
			BigDecimal pay = p.getGrossAmount();
			workPTD = workPTD.add(pay);
		}
		workPTD = workPTD.setScale(2, RoundingMode.HALF_EVEN);
		return workPTD;
	}
	
	public ArrayList<Paycheck> getReceivedWorkPayments(){
		return this.receivedWorkPayments;
	}
	
	public void setReceivedWorkPayments(ArrayList<Paycheck> rWP){
		this.receivedWorkPayments = rWP;
	}
	
	public String toString(){
		return "Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()+" | Light Duty Pay-To-Date: $"+this.getWorkPayToDate().toPlainString()+
				" | Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString();
	}
	/*public void setReceivedWorkPayments(){
		boolean added = false;
		System.out.println("Please Select an Option:");
		System.out.println("1) Add Paycheck received from Work Place for actual hours worked or vacation/sick days used (NOT from Work Comp Insurer)");
		System.out.println("2) Done adding Prior Wages");
		int selected = s.nextInt();
		while(selected < 1 || selected > 2){
			System.out.println("Invalid selection.");
			System.out.println("Please Select an Option:");
			System.out.println("1) Add Paycheck received from Work Place for actual hours worked or vacation/sick days used (NOT from Work Comp Insurer)");
			System.out.println("2) Done adding Prior Wages");
			selected = s.nextInt();
		}
		while (selected != 2){
			
			System.out.println("Enter gross amount (before taxes and deductions) of Paycheck: ");
			String grossAmount = s.next();
			System.out.println("Year of payment date (format: yyyy): ");
			int iYear = s.nextInt();
			System.out.println("Month of payment date (format: MM): ");
			int iMonth = s.nextInt();
			System.out.println("Day of the month of payment date (format: dd): ");
			int iDay = s.nextInt();
			
			System.out.println("Year of Pay Period Start date (format: yyyy): ");
			int sYear = s.nextInt();
			System.out.println("Month of Pay Period Start date (format: MM): ");
			int sMonth = s.nextInt();
			System.out.println("Day of the month of Pay Period Start date (format: dd): ");
			int sDay = s.nextInt();

			System.out.println("Is the Pay Period End Date different from the date of Payment? (1 = Yes, 2 = No)");
			int isDiff = s.nextInt();
			while (isDiff < 1 || isDiff > 2){
				System.out.println("Invalid input. (1 = Yes, 2 = No)");
				System.out.println("Is the Pay Period End Date different from the date of Payment? (1 = Yes, 2 = No)");
				isDiff = s.nextInt();
			}
	
			if(isDiff == 1){
				System.out.println("Year of Pay Period End date (format: yyyy): ");
				int eYear = s.nextInt();
				System.out.println("Month of Pay Period End date (format: mm): ");
				int eMonth = s.nextInt();
				System.out.println("Day of the month of Pay Period End date (format: dd): ");
				int eDay = s.nextInt();
				added = addPaycheck(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay, eYear, eMonth, eDay);	
			}
			else{
				added = addPaycheck(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay);
			}
			
			if (added){
				System.out.println("Paycheck Added.");
			}
			else{
				System.out.println("Error adding paycheck. Please ensure all inputs follow correct format.");
			}
			selected = 0;
			while(selected < 1 || selected > 2){
				System.out.println("Please Select an Option:");
				System.out.println("1) Add Paycheck received from Work Place for actual hours worked or vacation/sick days used (NOT from Work Comp Insurer)");
				System.out.println("2) Done adding Prior Wages");
				selected = s.nextInt();
			}
		}
	}*/

}
