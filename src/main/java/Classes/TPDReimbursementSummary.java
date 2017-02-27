package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class TPDReimbursementSummary extends ReimbursementSummary {
	//additional fields
	protected ArrayList<TPDPaycheck> receivedWorkPayments;
	//unnecessary (this varies week to week based on hours worked) protected BigDecimal computedWCSupplementalPayment;
	
	public TPDReimbursementSummary(){
		super();
		this.receivedWorkPayments = new ArrayList<TPDPaycheck>();
	}
	
	public TPDReimbursementSummary(CompClaim claim, StateLawCalculable stateLawCalc) {
		super(claim, stateLawCalc);
		this.receivedWorkPayments = new ArrayList<TPDPaycheck>();
	}
	
	public TPDReimbursementSummary(CompClaim claim, StateLawCalculable stateLawCalc, ArrayList<TPDPaycheck> receivedWorkPayments) {
		super(claim, stateLawCalc);
		this.receivedWorkPayments = receivedWorkPayments;
	}
	
	public TPDReimbursementSummary(ReimbursementSummary rsumm){
		super(rsumm);
		this.receivedWorkPayments = new ArrayList<TPDPaycheck>();
	}
	
	public TPDReimbursementSummary(ReimbursementSummary rsumm, ArrayList<TPDPaycheck> receivedWorkPayments){
		super(rsumm);
		this.receivedWorkPayments = receivedWorkPayments;
	}
	public TPDReimbursementSummary(BigDecimal calculatedWeeklyPayment, CompClaim claimSummary, BigDecimal amountNotPaid, ArrayList<WorkCompPaycheck> wcPayments, ArrayList<TPDPaycheck> receivedWorkPayments) {
		super(calculatedWeeklyPayment, claimSummary, amountNotPaid, wcPayments);
		this.receivedWorkPayments = receivedWorkPayments;
		this.sortPaychecksByDate();
	}
	@Override
	public void setWCPayments(ArrayList<WorkCompPaycheck> wcP){
		this.wcPayments = wcP;
		if(this.determineAnyLatePay()) this.computeAmountNotPaidAndAnyLateCompensationByWCPC();
		else this.computeAmountNotPaidAndAnyLateCompensation();
	}
	
	@Override
	public void computeAmountNotPaidAndAnyLateCompensation(){
		String aNP = "0.00";
		BigDecimal amountNotPaid = new BigDecimal(aNP);
		this.amountNotPaid = amountNotPaid;
		BigDecimal wcTotalPay = new BigDecimal(this.getWCPayToDate().toString());
		BigDecimal wcCalcTotalPay = this.getWCCalcPayToDate();
		amountNotPaid = wcCalcTotalPay.subtract(wcTotalPay);
		amountNotPaid = amountNotPaid.setScale(2, RoundingMode.HALF_EVEN);
		//TODO Implement LatePayCalc for noKnownPP
		this.amountNotPaid = amountNotPaid;
	}
	//calculates and sets amountNotPaid total and sets the amountStillOwed for each pay period entered in wcPayments
	//also determines if wcPC was late and adds and late payment owed to wcPC grossAmnt
	public void computeAmountNotPaidAndAnyLateCompensationByWCPC(){
		String aNP = "0.00";
		BigDecimal amountNotPaid = new BigDecimal(aNP);
		TPDPaycheck pcheck = null;
		for (WorkCompPaycheck p : this.wcPayments){
			label:for (TPDPaycheck pc : this.receivedWorkPayments){
				if (p.getPayPeriodEnd().compareTo(pc.getPayPeriodEnd()) == 0){
					pcheck = pc;
					break label;
				}
			}
			WorkCompPaycheck p2 = null;
			if (!this.arePayPeriodsEqual(p, pcheck)){
				label:for (WorkCompPaycheck wc : this.wcPayments){
					if (wc.getPayPeriodStart().compareTo(pcheck.getPayPeriodStart()) == 0){
						p2 = wc;
						break label;
					}
				}
			}
			BigDecimal calcSuppPayment = this.stateLawCalculation.computeWCSupplementalPayment(pcheck, this.claimSummary.getAvgPriorGrossWeeklyPayment());
			p.computeAnyAddtionalLatePaymentCompensation(calcSuppPayment);
			if(p2 != null) p2.computeAnyAddtionalLatePaymentCompensation(calcSuppPayment); //TODO maybe ensure that these together match the PP length for the Paycheck 
			BigDecimal aSO = p.getAmountStillOwed();
			if (p2 != null) aSO.add(p2.getAmountStillOwed());
			amountNotPaid = amountNotPaid.add(aSO);
		}
		amountNotPaid = amountNotPaid.setScale(2, RoundingMode.HALF_EVEN);
		this.amountNotPaid = amountNotPaid;
	}
	
	public boolean arePayPeriodsEqual(WorkCompPaycheck wc, Paycheck p){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long wcEnd = wc.getPayPeriodEnd().getTimeInMillis() + mDay;
		long wcStart = wc.getPayPeriodStart().getTimeInMillis();
		long wcPP = wcEnd - wcStart;
		
		long pEnd = p.getPayPeriodEnd().getTimeInMillis() + mDay;
		long pStart = p.getPayPeriodStart().getTimeInMillis();
		long pPP = pEnd - pStart;
		
		return wcPP == pPP;
	}
	
	public void sortPaychecksByDate(){
		Collections.sort(this.receivedWorkPayments, Paycheck.PPS_COMPARATOR);
	}
	
	public void addPaycheck(TPDPaycheck pc){
		//if(pc.getWCCalcPay().compareTo(new BigDecimal("0")) <=0 ) pc.computeWCCalcPay(stateLawCalculation, this.claimSummary.getAvgPriorGrossWeeklyPayment());
		this.receivedWorkPayments.add(pc);
		this.sortPaychecksByDate();
		this.computeAmountNotPaidAndAnyLateCompensation();
	}
	
	public TPDPaycheck trimFirstWorkPayment(TPDPaycheck pc, String bdTotalHrsWorked, String bdWeekInjHrsWorked){
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
	
	public void updateReceivedWorkPayments(ArrayList<TPDPaycheck> pchecks){
		this.receivedWorkPayments = pchecks;
		this.sortPaychecksByDate();
		this.computeAmountNotPaidAndAnyLateCompensation();
	}
	
	public BigDecimal getWorkPayToDate(){
		String wPTD = "0.00";
		BigDecimal workPTD = new BigDecimal(wPTD);
		if(this.receivedWorkPayments.isEmpty()) return workPTD;
		if(this.receivedWorkPayments.size() < 2) return this.receivedWorkPayments.get(0).getGrossAmount();
		
		for (int i=0, j=this.receivedWorkPayments.size()-1; i<j; i++, j--){
			TPDPaycheck p1 = this.receivedWorkPayments.get(i);
			TPDPaycheck p2 = this.receivedWorkPayments.get(j);
			workPTD = workPTD.add(p1.getGrossAmount()).add(p2.getGrossAmount());
			if(i+2 == j){
				p1 = this.receivedWorkPayments.get(i+1);
				workPTD = workPTD.add(p1.getGrossAmount());
				break;
			}
		}
		workPTD = workPTD.setScale(2, RoundingMode.HALF_EVEN);
		return workPTD;
	}
	
	public BigDecimal getWCCalcPayToDate(){
		BigDecimal wcCalcPTD = new BigDecimal("0.00");
		if (this.receivedWorkPayments.isEmpty()) return wcCalcPTD;
		if(this.receivedWorkPayments.size() < 2) wcCalcPTD = wcCalcPTD.add(this.receivedWorkPayments.get(0).getWCCalcPay());
		else{
			for (int i=0, j=this.receivedWorkPayments.size()-1; i<j; i++, j--){
				TPDPaycheck p1 = this.receivedWorkPayments.get(i);
				TPDPaycheck p2 = this.receivedWorkPayments.get(j);
				if (p1.getWCCalcPay().compareTo(new BigDecimal("0")) <= 0) p1.computeWCCalcPay(this.claimSummary.getAvgPriorGrossWeeklyPayment());
				if (p2.getWCCalcPay().compareTo(new BigDecimal("0")) <= 0) p2.computeWCCalcPay(this.claimSummary.getAvgPriorGrossWeeklyPayment());
				wcCalcPTD = wcCalcPTD.add(p1.getWCCalcPay()).add(p2.getWCCalcPay());
				if(i+2 == j){
					p1 = this.receivedWorkPayments.get(i+1);
					if (p1.getWCCalcPay().compareTo(new BigDecimal("0")) <= 0) p1.computeWCCalcPay(this.claimSummary.getAvgPriorGrossWeeklyPayment());
					wcCalcPTD = wcCalcPTD.add(p1.getWCCalcPay());
					break;
				}
			}
		}
		wcCalcPTD = wcCalcPTD.setScale(2, RoundingMode.HALF_EVEN);
		return wcCalcPTD;
	}	
	
	public ArrayList<TPDPaycheck> getReceivedWorkPayments(){
		return this.receivedWorkPayments;
	}
	
	public void setReceivedWorkPayments(ArrayList<TPDPaycheck> rWP){
		this.receivedWorkPayments = rWP;
		this.computeAmountNotPaidAndAnyLateCompensation();
	}
	
	public String toString(){
		return "Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()+" | Light Duty Pay-To-Date: $"+this.getWorkPayToDate().toPlainString()+
				" | Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString();
	}
	
	public String toTableString(){
		String eol = System.getProperty("line.separator");
		return "Amount Not Yet Paid: $"+this.amountNotPaid.toPlainString()+eol+
				"Light Duty Pay-To-Date: $"+this.getWorkPayToDate().toPlainString()+eol+
				"Work Comp Pay-To-Date: $"+this.getWCPayToDate().toPlainString()+eol+
				"Work Comp Calculated Total Owed: $"+this.getWCCalcPayToDate();
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
