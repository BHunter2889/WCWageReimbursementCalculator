package Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import Interfaces.StateLawCalculable;

public abstract class ReimbursementSummary {
	//fields
	protected BigDecimal calculatedWeeklyPayment; //represents the calculated payment owed to employee before taxes and deductions
	protected CompClaim claimSummary;
	protected BigDecimal amountNotPaid;
	protected ArrayList<WorkCompPaycheck> wcPayments;
	protected StateLawCalculable stateLawCalculation;
	protected Scanner s;

	//constructor
	public ReimbursementSummary(CompClaim claim, StateLawCalculable stateLawcalc) {
		this.s = new Scanner(System.in);
		this.claimSummary = claim;
		this.wcPayments = new ArrayList<WorkCompPaycheck>();
		this.stateLawCalculation = stateLawcalc;
		this.calculateAndSetWeeklyPayment();
		this.amountNotPaid = null;
	}
	
	public ReimbursementSummary() {
		this.s = new Scanner(System.in);
		this.claimSummary = null;
		this.wcPayments = new ArrayList<WorkCompPaycheck>();
		this.calculatedWeeklyPayment = null;
		this.amountNotPaid = null;
	}
	
	//for super() subclass construction
	public ReimbursementSummary(BigDecimal calculatedWeeklyPayment, CompClaim claimSummary, BigDecimal amountNotPaid, ArrayList<WorkCompPaycheck> wcPayments) {
		this.s = new Scanner(System.in);
		this.claimSummary = claimSummary;
		this.wcPayments = wcPayments;
		this.sortWCPaymentsByDate();
		this.calculatedWeeklyPayment = calculatedWeeklyPayment;
		this.amountNotPaid = amountNotPaid;
	}
	
	public ReimbursementSummary(ReimbursementSummary rsumm) {
		this.s = new Scanner(System.in);
		this.claimSummary = rsumm.claimSummary;
		this.wcPayments = rsumm.wcPayments;
		this.calculatedWeeklyPayment = rsumm.calculatedWeeklyPayment;
		this.amountNotPaid = rsumm.amountNotPaid;
	}
	/*
	public void setWCPayments(){
		boolean added = false;
		System.out.println("Please Select an Option:");
		System.out.println("1) Add Paycheck received from Work Comp insurer for hours not (Not to include any compensation from employer for vaction/sick days taken)");
		System.out.println("2) Done adding Work Comp");
		int selected = s.nextInt();
		while(selected < 1 || selected > 2){
			System.out.println("Invalid selection.");
			System.out.println("Please Select an Option:");
			System.out.println("1) Add Paycheck received from Work Comp insurer for hours not (Not to include any compensation from employer for vaction/sick days taken)");
			System.out.println("2) Done adding Prior Wages");
			selected = s.nextInt();
		}
		while (selected != 2){
			System.out.println("Was the Claim being contested at the time of Pay Period End? (1 = Yes, 2 = No)");
			int contest = s.nextInt();
			while (contest < 1 || contest > 2){
				System.out.println("Invalid input. (1 = Yes, 2 = No)");
				System.out.println("Is the Pay Period End Date different from the date pay was Received? (1 = Yes, 2 = No)");
				contest = s.nextInt();
			}
			boolean isContested = false;
			if(contest == 1){
				isContested = true;
			}
			
			System.out.println("Enter gross amount (before taxes and deductions) of Paycheck: ");
			String grossAmount = s.next();
			System.out.println("Year of date pay was Received (physically or digitally received, NOT manually deposited if different) (format: yyyy): ");
			int iYear = s.nextInt();
			System.out.println("Month of date pay was Received (format: MM): ");
			int iMonth = s.nextInt();
			System.out.println("Day of the month of date pay was Received (format: dd): ");
			int iDay = s.nextInt();
			
			System.out.println("Year of Pay Period Start date (format: yyyy): ");
			int sYear = s.nextInt();
			System.out.println("Month of Pay Period Start date (format: MM): ");
			int sMonth = s.nextInt();
			System.out.println("Day of the month of Pay Period Start date (format: dd): ");
			int sDay = s.nextInt();
			
			TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
			GregorianCalendar payReceivedDate = new GregorianCalendar(iYear, iMonth-1, iDay);
			payReceivedDate.setTimeZone(tz1);
			
			GregorianCalendar payPeriodStart = new GregorianCalendar(sYear, sMonth-1, sDay);
			payPeriodStart.setTimeZone(tz1);

			System.out.println("Is the Pay Period End Date different from the date pay was Received? (1 = Yes, 2 = No)");
			int isDiff = s.nextInt();
			while (isDiff < 1 || isDiff > 2){
				System.out.println("Invalid input. (1 = Yes, 2 = No)");
				System.out.println("Is the Pay Period End Date different from the date pay was Received? (1 = Yes, 2 = No)");
				isDiff = s.nextInt();
			}
	
			if(isDiff == 1){
				System.out.println("Year of Pay Period End date (format: yyyy): ");
				int eYear = s.nextInt();
				System.out.println("Month of Pay Period End date (format: mm): ");
				int eMonth = s.nextInt();
				System.out.println("Day of the month of Pay Period End date (format: dd): ");
				int eDay = s.nextInt();
				
				GregorianCalendar payPeriodEnd = new GregorianCalendar (eYear, eMonth-1, eDay);
				payPeriodEnd.setTimeZone(tz1);
				
				added = addWCPaycheck(grossAmount, payReceivedDate, payPeriodStart, payPeriodEnd, isContested);	
			}
			else{
				added = addWCPaycheck(grossAmount, payReceivedDate, payPeriodStart, isContested);
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
				System.out.println("1) Add Paycheck received from Work Comp insurer for hours not (Not to include any compensation from employer for vaction/sick days taken)");
				System.out.println("2) Done adding Prior Wages");
				selected = s.nextInt();
			}
		}
		sortWCPaymentsByDate();
	}*/
	
	public boolean addWCPaycheck(String grossAmount, GregorianCalendar payReceivedDate, GregorianCalendar payPeriodStart, GregorianCalendar payPeriodEnd, boolean isContested){
		WorkCompPaycheck pc = new WorkCompPaycheck(grossAmount, payReceivedDate, payPeriodStart, payPeriodEnd, isContested);
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		sortWCPaymentsByDate();
		
		//Calendar wcPRD = pc.getPaymentDate();
		//long wcPRDate = wcPRD.getTimeInMillis();
		Calendar pcPPS = pc.getPayPeriodStart();
		//long pcPPSDate = pcPPS.getTimeInMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		long mEPPS = this.claimSummary.priorWeekStart.getTimeInMillis() + mWeek;
		Calendar ePPS = new GregorianCalendar(tz1);
		Date epcPPS = new Date(mEPPS);
		ePPS.setTime(epcPPS);
		//Date dPPS = pcPPS.getTime();

		//Calendar pcPPE = pc.getPayPeriodEnd();
		//Date dPPE = pcPPE.getTime();

		if(pcPPS.compareTo(ePPS) < 0){
			System.out.println("Invalid paycheck start date. Pay Period Start Date must be on or after " + formatter.format(epcPPS) + " based on date of injury in accordance with State law.");
			return false;
		}
		
		this.wcPayments.add(pc);
		return true;
		
	}
	
	public boolean addWCPaycheck(String grossAmount, GregorianCalendar payReceivedDate, GregorianCalendar payPeriodStart, boolean isContested){
		WorkCompPaycheck pc = new WorkCompPaycheck(grossAmount, payReceivedDate, payPeriodStart, isContested);
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		sortWCPaymentsByDate();
		
		//Calendar wcPRD = pc.getPaymentDate();
		//long wcPRDate = wcPRD.getTimeInMillis();
		Calendar pcPPS = pc.getPayPeriodStart();
		//long pcPPSDate = pcPPS.getTimeInMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		long mEPPS = this.claimSummary.priorWeekStart.getTimeInMillis() + mWeek;
		Calendar ePPS = new GregorianCalendar(tz1);
		Date epcPPS = new Date(mEPPS);
		ePPS.setTime(epcPPS);
		//Date dPPS = pcPPS.getTime();

		//Calendar pcPPE = pc.getPayPeriodEnd();
		//Date dPPE = pcPPE.getTime();

		if(pcPPS.compareTo(ePPS) < 0){
			System.out.println("Invalid paycheck start date. Pay Period Start Date must be on or after " + formatter.format(epcPPS) + " based on date of injury in accordance with State law.");
			return false;
		}
		
		this.wcPayments.add(pc);
		return true;
		
	}
	
	//calculates and sets amountNotPaid total and sets the amountStillOwed for each pay period entered in wcPayments
	//also determines if wcPC was late and adds and late payment owed to wcPC grossAmnt
	public void computeAmountNotPaidAndAnyLateCompensation(){
		String aNP = "0.00";
		BigDecimal amountNotPaid = new BigDecimal(aNP);
		
		for (WorkCompPaycheck p : this.wcPayments){
			p.computeAnyAddtionalLatePaymentCompensation(this.calculatedWeeklyPayment);
			BigDecimal aSO = p.getAmountStillOwed();
			amountNotPaid = amountNotPaid.add(aSO);
		}
		amountNotPaid = amountNotPaid.setScale(2, RoundingMode.HALF_EVEN);
		this.amountNotPaid = amountNotPaid;
	}
	
	public void sortWCPaymentsByDate(){
		Collections.sort(this.wcPayments, new Comparator<WorkCompPaycheck>(){
			@Override
			public int compare(WorkCompPaycheck p1, WorkCompPaycheck p2) {
				
				return p1.compareTo(p2.getPayPeriodStart());
			}
			
		});
	}
	
	public void calculateAndSetWeeklyPayment(){
		BigDecimal calcWP = this.stateLawCalculation.computeCalculatedWeeklyPayment(this.claimSummary.avgPriorGrossWeeklyPayment);
		this.calculatedWeeklyPayment = calcWP;
	}
	
	public BigDecimal getWCPayToDate(){
		String zero = "0.00";
		BigDecimal wcPTD = new BigDecimal(zero);
		for(WorkCompPaycheck p : this.wcPayments){
			BigDecimal pay = p.getGrossAmount();
			wcPTD = wcPTD.add(pay);
		}
		wcPTD = wcPTD.setScale(2, RoundingMode.HALF_EVEN);
		return wcPTD;
	}
	
	public CompClaim getClaimSummary(){
		return this.claimSummary;
	}
	
	public BigDecimal getCalculatedWeeklyPayment(){
		return this.calculatedWeeklyPayment;
	}
	
	public BigDecimal getAmountNotPaid(){
		return this.amountNotPaid;
	}
	
	public ArrayList<WorkCompPaycheck> getWCPayments(){
		return this.wcPayments;
	}
	
	public void setClaimSummary(CompClaim cS){
		this.claimSummary = cS;
	}
	
	public void setCalculatedWeeklyPayment(BigDecimal cWP){
		this.calculatedWeeklyPayment = cWP;
	}
	
	public void setAmountNotPaid(BigDecimal aNP){
		this.amountNotPaid = aNP;
	}
	
	public void setWCPayments(ArrayList<WorkCompPaycheck> wcP){
		this.wcPayments = wcP;
	}
	
	// TODO: Setters and Getters and calcWP method taking interface class
	

}
