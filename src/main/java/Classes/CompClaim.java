package Classes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import Interfaces.StateLawCalculable;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;



public class CompClaim {
	//fields
	protected ArrayList<Paycheck> priorWages; //MUST set manually by calling either setPriorWages() or a series of addPaycheck post-construction
	protected Calendar dateInjured;
	protected Calendar priorWeekStart;
	protected Calendar earliestPriorWageDate;
	protected long daysInjured;
	protected long weeksInjured;
	protected BigDecimal avgPriorGrossWeeklyPayment; //calculated after priorWages is set. See above. 
	protected Scanner s;
	protected StateLawCalculable stateLawCalculation;
	//moved to StateLawCalculable: protected BigDecimal stateWeeksPriorPeriod; //for Missouri, this is 13 weeks.
	
	
	//constructor - month should be actual month (i.e. January = 1), not Java Calendar month
	public CompClaim(int year, int month, int day) {
		this.s = new Scanner(System.in);
		this.priorWages = new ArrayList<Paycheck>();
		setDateInjured(year, month, day);

		updateDaysAndWeeksInjured();
		
		setOrUpdatePriorWeekStart();

		setOrUpdateEarliestPriorWageDate();
	}
	
	//constructor w/ call to setPriorWages - month should be actual month (i.e. January = 1), not Java Calendar month
	public CompClaim(int year, int month, int day, boolean setWages) {
		this.s = new Scanner(System.in);
		this.priorWages = new ArrayList<Paycheck>();
		setDateInjured(year, month, day);

		updateDaysAndWeeksInjured();
		
		setOrUpdatePriorWeekStart();

		setOrUpdateEarliestPriorWageDate();
		if (setWages){
			setPriorWages();
		}
		
	}
		
	//constructor to set stateWeeksPriorPeriod
	public CompClaim(int year, int month, int day, StateLawCalculable sLC) {
		this.s = new Scanner(System.in);
		this.priorWages = new ArrayList<Paycheck>();
		setDateInjured(year, month, day);

		updateDaysAndWeeksInjured();
		
		setStateLawCalculation(sLC);
		
		setOrUpdatePriorWeekStart();

		setOrUpdateEarliestPriorWageDate();
	}
	
	//constructor to set stateWeeksPriorPeriod w/ call to setPriorWages
	public CompClaim(int year, int month, int day, StateLawCalculable sLC, boolean setWages) {
		this.s = new Scanner(System.in);
		this.priorWages = new ArrayList<Paycheck>();
		setDateInjured(year, month, day);

		updateDaysAndWeeksInjured();
		
		setStateLawCalculation(sLC);
		
		setOrUpdatePriorWeekStart();

		setOrUpdateEarliestPriorWageDate();
		if (setWages){
			setPriorWages();
		}
	}
	
	// Constructor to complete all CompClaim fields when called from DAO, should take sql.Date object
	public CompClaim(Date dateInjured, StateLawCalculable sLC) {
		this.s = new Scanner(System.in);
		this.priorWages = new ArrayList<Paycheck>();
		setDateInjured(dateInjured);

		updateDaysAndWeeksInjured();
		
		setStateLawCalculation(sLC);
		
		setOrUpdatePriorWeekStart();

		setOrUpdateEarliestPriorWageDate();
	}

	private void setStateLawCalculation(StateLawCalculable sLC) {
		this.stateLawCalculation = sLC;
	}

	//methods
	//uses sLC interface class to set ePWDate
	public void setOrUpdateEarliestPriorWageDate(){
		this.earliestPriorWageDate = this.stateLawCalculation.computeEarliestPriorWageDate(this.priorWeekStart);
	}
	
	public void setDateInjured(int year, int month, int day){
		this.dateInjured = new GregorianCalendar(year, month-1, day);
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		this.dateInjured.setTimeZone(tz1);
	}
	
	public void setDateInjured(Date dateInjured){
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		this.dateInjured = new GregorianCalendar(tz1);
		this.dateInjured.setTime(dateInjured);;
	}
	
	public void setOrUpdatePriorWeekStart(){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		long mDI = this.dateInjured.getTimeInMillis();
		long injDOW = this.dateInjured.get(Calendar.DAY_OF_WEEK);
		long mInjFDW = (mDI - ((injDOW - 1) * mDay)); 
		long mPWS = mInjFDW - mWeek;
		
		this.priorWeekStart = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		this.priorWeekStart.setTimeInMillis(mPWS);
	}
	
	public void setPriorWagesAndComputeAPGWP(ArrayList<Paycheck> priorWages){
		this.priorWages = priorWages;
		this.computeAvgPriorGrossWeeklyPayment();
		this.sortPaychecksByDate();
	}
	
	public void setPriorWages(ArrayList<Paycheck> priorWages){
		this.priorWages = priorWages;
		this.sortPaychecksByDate();
	}

	public void setPriorWages(){
		boolean complete = false;
		boolean added = false;
		System.out.println("Please Select an Option:");
		System.out.println("1) Add Paycheck from before Injury (not to exceed period prior to date injured as set by state law)");
		System.out.println("2) Done adding Prior Wages");
		int selected = s.nextInt();
		while(selected < 1 || selected > 2){
			System.out.println("Invalid selection.");
			System.out.println("Please Select an Option:");
			System.out.println("1) Add Paycheck from before Injury (not to exceed period prior to date injured as set by state law)");
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
//Should work now. Fixed issue where logic had not been completed from here to end of method.			
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
				complete = priorWagesIsComplete();
				if(complete){
					//assume that prior wages are completed and compute aPGWP
					computeAvgPriorGrossWeeklyPayment();
					System.out.println("Total time period of prior wages entered now meets State Law criteria. Done adding prior wages.");
/* may want to add information to clear all or delete wages here*/					
					return;
				}
			}
			else{
				System.out.println("Error adding paycheck. Please ensure all inputs follow correct format.");
			}
			selected = 0;
			while(selected < 1 || selected > 2){
				System.out.println("Please Select an Option:");
				System.out.println("1) Add Another Paycheck from before Injury (not to exceed 13 weeks prior to date injured)");
				System.out.println("2) Done adding Prior Wages");
				selected = s.nextInt();
			}
		}
		complete = priorWagesIsComplete();
		if(complete){
			//assume that prior wages are completed and compute aPGWP
			computeAvgPriorGrossWeeklyPayment();
		}
		else{
			System.out.println("Total time period of prior wages entered does not complete State Law criteria. If all dates are correct and claimant has earned wages during the time period specified by law, additional paycheck(s) will need to be added later.");
		}
	}
	
	public void addPaycheck(Paycheck pc, StateLawCalculable stateLawCalc){
		this.priorWages = stateLawCalc.addAndTrimToPriorWages(pc, this.priorWages, this.priorWeekStart);
		this.sortPaychecksByDate();
	}
	
	//if payment date is same as period end date
	public boolean addPaycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay){
		Paycheck pc = new Paycheck(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay);
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		//long mWeek = mDay * 7;
		sortPaychecksByDate();
		boolean unaddable = priorWagesIsComplete();
		if(unaddable){
			System.out.println("Paycheck cannot be added. Total time period of Prior Wages entered meets State Law criteria.");
			return false;
		}
		
		//Calendar pcPD = pc.getPaymentDate();
		//long pcPDate = pcPD.getTimeInMillis();
		Calendar pcPPS = pc.getPayPeriodStart();
		long pcPPSDate = pcPPS.getTimeInMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		Date ePWD = (Date) this.earliestPriorWageDate.getTime();
		Date dPPS = (Date) pcPPS.getTime();
		 
		long mPWeekEnd =  this.priorWeekStart.getTimeInMillis() + (mDay * 6);
		Calendar priorWeekEnd = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		priorWeekEnd.setTimeInMillis(mPWeekEnd);
		Calendar pcPPE = pc.getPayPeriodEnd();
		Date dPPE = (Date) pcPPE.getTime();
		Date dPWE = (Date) priorWeekEnd.getTime();
		

		if(pcPPS.compareTo(priorWeekEnd) > 0){
			System.out.println("Invalid paycheck start date. Pay Period Start Date must be before the end of the week immediately prior to week of injury.");
			return false;
		}
		else if(pcPPS.compareTo(this.earliestPriorWageDate) < 0){
			

			//if period overlaps ePWD, alter the paycheck to represent only applicable portion (days after ePWD)
			if (pcPPE.compareTo(this.earliestPriorWageDate) > 0){
				long pcE = pcPPE.getTimeInMillis();
				long mEPW = this.earliestPriorWageDate.getTimeInMillis();
				long mNewPP = (pcE - mEPW);
				int PPDays = (int) Math.ceil(mNewPP / mDay);

				pc.setPayPeriodStart(this.earliestPriorWageDate);
				String pG = String.valueOf(mNewPP / (pcE - pcPPSDate));
				BigDecimal percentGross = new BigDecimal(pG);
				BigDecimal gA = pc.getGrossAmount();
				BigDecimal pGAMult = gA.multiply(percentGross);
				BigDecimal nG = pGAMult.setScale(2, RoundingMode.HALF_EVEN);
				String newGross = String.valueOf(nG);
				pc.setGrossAmount(newGross);
				System.out.println("Only last " + PPDays + " Days of entered Pay Check entered and calculated for Gross Amount due to earliest accepted date for prior wages relative to date of injury");
				System.out.println("If work hours used to calculate pay during this period were not evenly distributed for the above number of days,");
				System.out.println("please manually enter Gross Income ONLY for the hours worked this pay period STARTING on " + formatter.format(ePWD) +".");
				System.out.println("Gross Amount is currently set at: " + pc.getGrossAmount() + "Enter '1' to set Gross Income manually ONLY if meeting above criteria.");
				System.out.println("Enter '2' to confirm and finish adding Paycheck.");
				int selection = s.nextInt();
				while(selection < 1 || selection > 2){
					System.out.println("Invalid input.");
					System.out.println("Please Select an Option:");
					System.out.println("1) Enter Manual Gross Income for pay period starting on " + formatter.format(ePWD) +".");
					System.out.println("2) Confirm and finish adding Paycheck.");
					selection = s.nextInt();
				}

				if (selection == 1){
					System.out.println("Enter Manual Gross Income for wages earned by hours worked from " + formatter.format(ePWD) +" through " + formatter.format(dPPE) + ":");
					String gI = s.next();
					pc.setGrossAmount(gI);
				}
			}
			else{
				System.out.println("Invalid paycheck end date. Pay Period End Date must be after " + formatter.format(ePWD) + " based on date of injury in accordance with State law.");
				return false;
			}	
		}
		else if (pcPPE.compareTo(priorWeekEnd) > 0){
			//if PPE is past PWE, alter the paycheck to represent days up to PWE
			if (pcPPS.compareTo(priorWeekEnd) < 0){
				long pcE = pcPPE.getTimeInMillis();
				
				long mNewPP = (mPWeekEnd - pcPPSDate);
				int PPDays = (int) Math.ceil(mNewPP / mDay);
				pc.setPayPeriodEnd(priorWeekEnd);
				String pG = String.valueOf(mNewPP / (pcE - pcPPSDate));
				BigDecimal percentGross = new BigDecimal(pG);
				BigDecimal gA = pc.getGrossAmount();
				BigDecimal pGAMult = gA.multiply(percentGross);
				BigDecimal nG = pGAMult.setScale(2, RoundingMode.HALF_EVEN);
				String newGross = String.valueOf(nG);
				pc.setGrossAmount(newGross);
				System.out.println("Only first " + PPDays + " Days of entered Pay Check entered and calculated for Gross Amount due to last accepted date for prior wages relative to date of injury (end of prior week)");
				System.out.println("If work hours used to calculate pay during this period were not evenly distributed for the above number of days,");
				System.out.println("please manually enter Gross Income ONLY for the hours worked this pay period STARTING on " + formatter.format(dPPS) + " through " + formatter.format(dPWE) + ".");
				System.out.println("Gross Amount is currently set at: " + pc.getGrossAmount() + "Enter '1' to set Gross Income manually ONLY if meeting above criteria.");
				System.out.println("Enter '2' to confirm and finish adding Paycheck.");
				int selection = s.nextInt();
				while(selection < 1 || selection > 2){
					System.out.println("Invalid input.");
					System.out.println("Please Select an Option:");
					System.out.println("1) Enter Manual Gross Income for pay period starting on " + formatter.format(dPPS) + " through " + formatter.format(dPWE) + ".");
					System.out.println("2) Confirm and finish adding Paycheck.");
					selection = s.nextInt();
				}

				if (selection == 1){
					System.out.println("Enter Manual Gross Income for wages earned by hours worked from " + formatter.format(dPPS) +" through " + formatter.format(dPWE) + ":");
					String gI = s.next();
					pc.setGrossAmount(gI);
				}
			}
			else{
				System.out.println("Invalid paycheck start date. Pay Period Start Date must be before " + formatter.format(dPWE) + " based on date of injury in accordance with State law.");
				return false;
			}	
		}
		boolean added = this.priorWages.add(pc);
		return added;
		
	}

	//if end date is different from payment date
	public boolean addPaycheck(String grossAmount, int iYear, int iMonth, int iDay, int sYear, int sMonth, int sDay, int eYear, int eMonth, int eDay){
		Paycheck pc = new Paycheck(grossAmount, iYear, iMonth, iDay, sYear, sMonth, sDay, eYear, eMonth, eDay);
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		//long mWeek = mDay * 7;
		sortPaychecksByDate();
		boolean unaddable = priorWagesIsComplete();
		if(unaddable){
			System.out.println("Paycheck cannot be added. Total time period of Prior Wages entered meets State Law criteria.");
			return false;
		}
		
		//Calendar pcPD = pc.getPaymentDate();
		//long pcPDate = pcPD.getTimeInMillis();
		Calendar pcPPS = pc.getPayPeriodStart();
		long pcPPSDate = pcPPS.getTimeInMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		Date ePWD = (Date) this.earliestPriorWageDate.getTime();
		Date dPPS = (Date) pcPPS.getTime();
		 
		long mPWeekEnd =  this.priorWeekStart.getTimeInMillis() + (mDay * 6);
		Calendar priorWeekEnd = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		priorWeekEnd.setTimeInMillis(mPWeekEnd);
		Calendar pcPPE = pc.getPayPeriodEnd();
		Date dPPE = (Date) pcPPE.getTime();
		Date dPWE = (Date) priorWeekEnd.getTime();
		

		if(pcPPS.compareTo(priorWeekEnd) > 0){
			System.out.println("Invalid paycheck start date. Pay Period Start Date must be before the end of the week immediately prior to week of injury.");
			return false;
		}
		else if(pcPPS.compareTo(this.earliestPriorWageDate) < 0){
			

			//if period overlaps ePWD, alter the paycheck to represent only applicable portion (days after ePWD)
			if (pcPPE.compareTo(this.earliestPriorWageDate) > 0){
				long pcE = pcPPE.getTimeInMillis();
				long mEPW = this.earliestPriorWageDate.getTimeInMillis();
				long mNewPP = (pcE - mEPW);
				int PPDays = (int) Math.ceil(mNewPP / mDay);

				pc.setPayPeriodStart(this.earliestPriorWageDate);
				String pG = String.valueOf(mNewPP / (pcE - pcPPSDate));
				BigDecimal percentGross = new BigDecimal(pG);
				BigDecimal gA = pc.getGrossAmount();
				BigDecimal pGAMult = gA.multiply(percentGross);
				BigDecimal nG = pGAMult.setScale(2, RoundingMode.HALF_EVEN);
				String newGross = String.valueOf(nG);
				pc.setGrossAmount(newGross);
				System.out.println("Only last " + PPDays + " Days of entered Pay Check entered and calculated for Gross Amount due to earliest accepted date for prior wages relative to date of injury");
				System.out.println("If work hours used to calculate pay during this period were not evenly distributed for the above number of days,");
				System.out.println("please manually enter Gross Income ONLY for the hours worked this pay period STARTING on " + formatter.format(ePWD) +".");
				System.out.println("Gross Amount is currently set at: " + pc.getGrossAmount() + "Enter '1' to set Gross Income manually ONLY if meeting above criteria.");
				System.out.println("Enter '2' to confirm and finish adding Paycheck.");
				int selection = s.nextInt();
				while(selection < 1 || selection > 2){
					System.out.println("Invalid input.");
					System.out.println("Please Select an Option:");
					System.out.println("1) Enter Manual Gross Income for pay period starting on " + formatter.format(ePWD) +".");
					System.out.println("2) Confirm and finish adding Paycheck.");
					selection = s.nextInt();
				}

				if (selection == 1){
					System.out.println("Enter Manual Gross Income for wages earned by hours worked from " + formatter.format(ePWD) +" through " + formatter.format(dPPE) + ":");
					String gI = s.next();
					pc.setGrossAmount(gI);
				}
			}
			else{
				System.out.println("Invalid paycheck end date. Pay Period End Date must be after " + formatter.format(ePWD) + " based on date of injury in accordance with State law.");
				return false;
			}	
		}
		else if (pcPPE.compareTo(priorWeekEnd) > 0){
			//if PPE is past PWE, alter the paycheck to represent days up to PWE
			if (pcPPS.compareTo(priorWeekEnd) < 0){
				long pcE = pcPPE.getTimeInMillis();
				
				long mNewPP = (mPWeekEnd - pcPPSDate);
				int PPDays = (int) Math.ceil(mNewPP / mDay);
				pc.setPayPeriodEnd(priorWeekEnd);
				String pG = String.valueOf(mNewPP / (pcE - pcPPSDate));
				BigDecimal percentGross = new BigDecimal(pG);
				BigDecimal gA = pc.getGrossAmount();
				BigDecimal pGAMult = gA.multiply(percentGross);
				BigDecimal nG = pGAMult.setScale(2, RoundingMode.HALF_EVEN);
				String newGross = String.valueOf(nG);
				pc.setGrossAmount(newGross);
				System.out.println("Only first " + PPDays + " Days of entered Pay Check entered and calculated for Gross Amount due to last accepted date for prior wages relative to date of injury (end of prior week)");
				System.out.println("If work hours used to calculate pay during this period were not evenly distributed for the above number of days,");
				System.out.println("please manually enter Gross Income ONLY for the hours worked this pay period STARTING on " + formatter.format(dPPS) + " through " + formatter.format(dPWE) + ".");
				System.out.println("Gross Amount is currently set at: " + pc.getGrossAmount() + "Enter '1' to set Gross Income manually ONLY if meeting above criteria.");
				System.out.println("Enter '2' to confirm and finish adding Paycheck.");
				int selection = s.nextInt();
				while(selection < 1 || selection > 2){
					System.out.println("Invalid input.");
					System.out.println("Please Select an Option:");
					System.out.println("1) Enter Manual Gross Income for pay period starting on " + formatter.format(dPPS) + " through " + formatter.format(dPWE) + ".");
					System.out.println("2) Confirm and finish adding Paycheck.");
					selection = s.nextInt();
				}

				if (selection == 1){
					System.out.println("Enter Manual Gross Income for wages earned by hours worked from " + formatter.format(dPPS) +" through " + formatter.format(dPWE) + ":");
					String gI = s.next();
					pc.setGrossAmount(gI);
				}
			}
			else{
				System.out.println("Invalid paycheck start date. Pay Period Start Date must be before " + formatter.format(dPWE) + " based on date of injury in accordance with State law.");
				return false;
			}	
		}
		boolean added = this.priorWages.add(pc);
		return added;
		
	}

	//Super method. Can be altered for other states in child classes.
	public void computeAvgPriorGrossWeeklyPayment(){
		BigDecimal aPGWP = this.stateLawCalculation.computeAvgPriorGrossWeeklyPayment(this.priorWages);
		if (aPGWP.scale() != 2){
			this.avgPriorGrossWeeklyPayment = aPGWP.setScale(2, RoundingMode.HALF_EVEN);
		} else {
			this.avgPriorGrossWeeklyPayment = aPGWP;
		}
	}
	
	public void setAvgPriorGrossWeeklyPayment(BigDecimal aPGWP){
		this.avgPriorGrossWeeklyPayment = aPGWP;
	}

	public void updateDaysAndWeeksInjured(){
		TimeZone tz1 = TimeZone.getTimeZone("America/Chicago");
		Calendar current = new GregorianCalendar(tz1);
		
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		long mDI = this.dateInjured.getTimeInMillis();
		long cur = current.getTimeInMillis();
		this.daysInjured = (long) Math.floor((cur - mDI) / mDay);
		this.weeksInjured = (long) Math.floor((cur - mDI) / mWeek);
	}
	
	public void sortPaychecksByDate(){
		Collections.sort(this.priorWages, new Comparator<Paycheck>(){
			@Override
			public int compare(Paycheck p1, Paycheck p2) {
				
				return p1.compareTo(p2.getPayPeriodStart());
			}
			
		});
	}
	
	public boolean priorWagesIsComplete(){
		return this.stateLawCalculation.priorWagesIsComplete(this.priorWages);
	}

	public Calendar getDateInjured(){
		return this.dateInjured;
	}
	
	public Calendar getPriorWeekStart(){
		return this.priorWeekStart;
	}
	
	public Calendar getEarliestPriorWageDate(){
		return this.earliestPriorWageDate;
	}
	
	public long getDaysInjured(){
		return this.daysInjured;
	}
	
	public long getWeeksInjured(){
		return this.weeksInjured;
	}
	
	public BigDecimal getAvgPriorGrossWeeklyPayment(){
		return this.avgPriorGrossWeeklyPayment;
	}
	
	public ArrayList<Paycheck> getPriorWages(){
		return this.priorWages;
	}
	
	//sorts Prior Wages by date and then returns them on newlines in the format "#) pPS - pPE: $grossAmount paid on pD"
	public String listPriorWages(){
		sortPaychecksByDate();
		String eol = System.getProperty("line.separator");
		String list = "";
		int num = 1;
		for(Paycheck p : this.priorWages){
			list += num + ")" + p.toString() + eol;
			num++;
		}
		
		return list;
	}
	
	public String toStringDateInjured(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		Date dI = (Date) this.dateInjured.getTime();
		return formatter.format(dI);
	}
	
	public String toStringPriorWeekStart(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		Date pWS = (Date) this.priorWeekStart.getTime();
		return formatter.format(pWS);
	}
	
	public String toStringEarliestPriorWageDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		Date ePWD = (Date) this.earliestPriorWageDate.getTime();
		return formatter.format(ePWD);
	}
	
	//returns a string of primary (but not all) fields (includes: dateInjured, daysInjured, weeksInjured, avgPGWP, and a list of priorWages)
	public String toString(){
		String eol = System.getProperty("line.separator");
		String list = this.listPriorWages();
		return "Date Injured: "+this.toStringDateInjured()+eol+"Days Injured: "+this.daysInjured+eol+"Weeks Injured: "+this.weeksInjured+eol+
				"Average Prior Gross Weekly Payment: $"+this.avgPriorGrossWeeklyPayment.toPlainString()+eol+"Prior Wage Paychecks: "+eol+list;
	}
	
	public String toTableString(){
		return "Date Injured: "+this.toStringDateInjured()+" | Days Injured: "+String.valueOf(this.daysInjured)+" | Weeks Injured: "+String.valueOf(this.weeksInjured)+
				" | Average Prior Gross Weekly Payment: $"+this.avgPriorGrossWeeklyPayment.toPlainString();
	}
	
}
