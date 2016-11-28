package Interfaces;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import Classes.Paycheck;
import Classes.WorkCompPaycheck;


public class MissouriCalculation implements StateLawCalculable {
	private static final String stateName = "Missouri";
	private static final String stateAbbrv = "MO";
	private static final BigDecimal stateWPP = new BigDecimal("13.0"); 
	private static final int stateDaysToLate = 30; 
	private static final TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");

	public MissouriCalculation() {
	}

	@Override
	public ArrayList<Paycheck> addAndTrimToPriorWages(Paycheck pc, ArrayList<Paycheck> pchecks, Calendar priorWeekStart) {
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		//long mWeek = mDay * 7;
		String message = "";
		boolean unaddable = priorWagesIsComplete(pchecks);
		if(unaddable){
			message ="Paycheck cannot be added. Total time period of prior wages entered meets Missouri Law criteria.";
			JOptionPane.showMessageDialog(null, message);
			return pchecks;
		}
		
		//Calendar pcPD = pc.getPaymentDate();
		//long pcPDate = pcPD.getTimeInMillis();
		Calendar pcPPS = pc.getPayPeriodStart();
		long pcPPSDate = pcPPS.getTimeInMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		Date ePWD = (Date) this.computeEarliestPriorWageDate(priorWeekStart).getTime();
		//Date dPPS = (Date) pcPPS.getTime();
		 
		long mPWeekEnd =  priorWeekStart.getTimeInMillis() + (mDay * 6);
		Calendar priorWeekEnd = new GregorianCalendar(timeZone);
		priorWeekEnd.setTimeInMillis(mPWeekEnd);
		Calendar pcPPE = pc.getPayPeriodEnd();
		//Date dPPE = (Date) pcPPE.getTime();
		Date dPWE = (Date) priorWeekEnd.getTime();
		

		if(pcPPS.compareTo(priorWeekEnd) > 0){
			message = "Invalid paycheck start date. Pay Period Start Date must be before the end of the week immediately prior to week of injury.";
			JOptionPane.showMessageDialog(null, message);
			return pchecks;
		}
		else if(pcPPS.compareTo(this.computeEarliestPriorWageDate(priorWeekStart)) < 0){
			

			//if period overlaps ePWD, alter the paycheck to represent only applicable portion (days after ePWD)
			if (pcPPE.compareTo(this.computeEarliestPriorWageDate(priorWeekStart)) > 0){
				long pcE = pcPPE.getTimeInMillis();
				long mEPW = this.computeEarliestPriorWageDate(priorWeekStart).getTimeInMillis();
				long mNewPP = (pcE - mEPW);
				int PPDays = (int) Math.ceil(mNewPP / mDay);

				pc.setPayPeriodStart(this.computeEarliestPriorWageDate(priorWeekStart));
				String pG = String.valueOf(mNewPP / (pcE - pcPPSDate));
				BigDecimal percentGross = new BigDecimal(pG);
				BigDecimal gA = pc.getGrossAmount();
				BigDecimal pGAMult = gA.multiply(percentGross);
				BigDecimal nG = pGAMult.setScale(2, RoundingMode.HALF_EVEN);
				String newGross = String.valueOf(nG);
				pc.setGrossAmount(newGross);
				message = "Only last " + PPDays + " Days of submitted Pay Check entered and calculated for Gross Amount due to earliest accepted date for prior wages relative to date of injury";
				JOptionPane.showMessageDialog(null, message);
				//System.out.print("If work hours used to calculate pay during this period were not evenly distributed for the above number of days,");
				//System.out.print("please manually enter Gross Income ONLY for the hours worked this pay period STARTING on " + formatter.format(ePWD) +".");
				//System.out.println("Gross Amount is currently set at: " + pc.getGrossAmount() + "Enter '1' to set Gross Income manually ONLY if meeting above criteria.");
				//System.out.println("Enter '2' to confirm and finish adding Paycheck.");
				//int selection = s.nextInt();
				/*while(selection < 1 || selection > 2){
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
				}*/
			}
			else{
				message = "Invalid paycheck end date. Pay Period End Date must be after " + formatter.format(ePWD) + " based on date of injury in accordance with State law.";
				JOptionPane.showMessageDialog(null, message);
				return pchecks;
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
				message = "Only first " + PPDays + " Days of entered Pay Check entered and calculated for Gross Amount due to last accepted date for prior wages relative to date of injury (end of prior week)";
				JOptionPane.showMessageDialog(null, message);
				/*System.out.println("If work hours used to calculate pay during this period were not evenly distributed for the above number of days,");
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
				}*/
			}
			else{
				message = "Invalid paycheck start date. Pay Period Start Date must be before " + formatter.format(dPWE) + " based on date of injury in accordance with State law.";
				JOptionPane.showMessageDialog(null, message);
				return pchecks;
			}	
		}
		
		pchecks.add(pc);
		return pchecks;
	}

	@Override
	public ArrayList<Paycheck> addTPDWorkPaycheck(Paycheck pc, ArrayList<Paycheck> pchecks, Calendar priorWeekStart) throws Exception {
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		String message = "";
		Calendar pcPPS = pc.getPayPeriodStart();
		//long pcPPSDate = pcPPS.getTimeInMillis();
		//SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		//formatter.setLenient(false);
		//Date dPPS = (Date) pcPPS.getTime();
		 
		long mPWeekEnd =  priorWeekStart.getTimeInMillis() + (mDay * 6);
		Calendar priorWeekEnd = new GregorianCalendar(timeZone);
		priorWeekEnd.setTimeInMillis(mPWeekEnd);
		Calendar pcPPE = pc.getPayPeriodEnd();
		//Date dPPE = (Date) pcPPE.getTime();
		//Date dPWE = (Date) priorWeekEnd.getTime();
		

		if(pcPPE.compareTo(priorWeekEnd) <= 0){
			message = "Invalid paycheck end date. Pay Period End Date must be after the end of the week immediately prior to week of injury.";
			JOptionPane.showMessageDialog(null, message);
			return pchecks;
		}
		else if(pcPPS.compareTo(priorWeekEnd) <= 0){
			throw new Exception("Paycheck Start Date must be trimmed using ReimbursementSummary.trimWorkPayment(Paycheck, totalHrsWorked, weekInjHrsWorked)");
		}
		pchecks.add(pc);
		
		
		return pchecks;
	}

	@Override
	public ArrayList<WorkCompPaycheck> addWCPaycheck(WorkCompPaycheck wcPC, ArrayList<WorkCompPaycheck> wcPayments, Calendar priorWeekStart) {
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		String message = "";
		
		//Calendar wcPRD = pc.getPaymentDate();
		//long wcPRDate = wcPRD.getTimeInMillis();
		Calendar pcPPS = wcPC.getPayPeriodStart();
		//long pcPPSDate = pcPPS.getTimeInMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		formatter.setLenient(false);
		long mEPPS = priorWeekStart.getTimeInMillis() + mWeek;
		Calendar ePPS = new GregorianCalendar(timeZone);
		Date epcPPS = new Date(mEPPS);
		ePPS.setTime(epcPPS);
		//Date dPPS = pcPPS.getTime();

		//Calendar pcPPE = pc.getPayPeriodEnd();
		//Date dPPE = pcPPE.getTime();

		if(pcPPS.compareTo(ePPS) < 0){
			message = "Invalid paycheck start date. Pay Period Start Date must be on or after " + formatter.format(epcPPS) + " based on date of injury in accordance with Missouri law.";
			JOptionPane.showMessageDialog(null, message);
			return wcPayments;
		}
		
		wcPayments.add(wcPC);
		return wcPayments;
	}

	@Override
	public BigDecimal computeAnyLatePaymentCompensation(BigDecimal grossAmnt, BigDecimal calculatedWeeklyPayment) {
		BigDecimal tenth = calculatedWeeklyPayment.multiply(new BigDecimal("0.10"));
		BigDecimal tenthAdded = calculatedWeeklyPayment.add(tenth);
		BigDecimal diff = tenthAdded.subtract(grossAmnt);
		return diff.setScale(2, RoundingMode.HALF_EVEN);
	}

	@Override
	public BigDecimal computeAvgPriorGrossWeeklyPayment(ArrayList<Paycheck> priorWages) {
		String pWT = "0.00";
		BigDecimal priorWT = new BigDecimal(pWT);
		BigDecimal priorWageTotal = priorWT.setScale(2, RoundingMode.HALF_EVEN);
		
		for (Paycheck p : priorWages){
			priorWageTotal = priorWageTotal.add(p.getGrossAmount());
		}
		
		BigDecimal avgPriorGrossWeeklyPayment = priorWageTotal.divide(stateWPP, 2, RoundingMode.HALF_EVEN);
		return avgPriorGrossWeeklyPayment;
	}

	@Override
	public BigDecimal computeCalculatedWeeklyPayment(BigDecimal avgPGrossWeekPay) {
		BigDecimal two3 = new BigDecimal(String.valueOf(2/3));
		BigDecimal cWP = avgPGrossWeekPay.multiply(two3);
		return cWP.setScale(2, RoundingMode.HALF_EVEN);
	}

	/* probably wont need, will delete or implement later 
	 * @Override
	public BigDecimal computeAmountNotPaid(ArrayList<WorkCompPaycheck> wcPayments, BigDecimal calcWP) {
		String aNP = "0.00";
		BigDecimal amountNotPaid = new BigDecimal(aNP);
		
		for (WorkCompPaycheck p : this.wcPayments){
			BigDecimal aSO = this.calculatedWeeklyPayment.subtract(p.getGrossAmount());
			amountNotPaid = amountNotPaid.add(aSO);
			aSO = aSO.setScale(2, RoundingMode.HALF_EVEN);
			p.setAmountStillOwed(aSO);
		}
		amountNotPaid = amountNotPaid.setScale(2, RoundingMode.HALF_EVEN);
		this.amountNotPaid = amountNotPaid;		return null;
	}*/

	@Override
	public Calendar computeEarliestPriorWageDate(Calendar priorWeekStart) {
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		long mPWS = priorWeekStart.getTimeInMillis();
		
		//based on Missouri Law (13 weeks of paychecks starting with the week immediately preceding the week injured)
		long mEPW = (mPWS - (mWeek * 12));
		Calendar ePWDate = new GregorianCalendar(timeZone);
		ePWDate.setTimeInMillis(mEPW);		
		return ePWDate;
	}

	@Override
	//formulas based on law at: http://www.moga.mo.gov/mostatutes/stathtml/28700001801.html 
	//returns Work Comp supplemental payment amount (BigDecimal) for hours actually worked during TPD period. Amount calculated based on Missouri Law.
	public BigDecimal computeWCSupplementalPayment(Paycheck workPayment, BigDecimal avgPriorGrossWeeklyPayment) {
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		long mPP = workPayment.getPayPeriodEnd().getTimeInMillis() - workPayment.getPayPeriodStart().getTimeInMillis();
		//long mDR = mPP % mWeek;
		//long daysRemaining = Math.round((mPP % mWeek) / mDay);
		//BigDecimal week = new BigDecimal(String.valueOf(mWeek));
		//BigDecimal weekPercentRemainder =  week.divide(new BigDecimal(String.valueOf(Math.round((mPP % mWeek) / mDay))), RoundingMode.UNNECESSARY);
		BigDecimal payPWeeks = (new BigDecimal(String.valueOf(mPP)).divide(new BigDecimal(String.valueOf(mWeek))));
		BigDecimal weeklyPayment = (avgPriorGrossWeeklyPayment.multiply(payPWeeks).subtract(workPayment.getGrossAmount())).multiply(new BigDecimal(String.valueOf(2/3)));
	
		return weeklyPayment.setScale(2, RoundingMode.HALF_EVEN);
	}

	@Override
	public boolean determineAndSetIsLate(Calendar payPeriodEnd, Calendar payReceived) {
		boolean isLate = false;
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		//long mWeek = mDay * 7;
		long mPPE = payPeriodEnd.getTimeInMillis();
		long mPRD = payReceived.getTimeInMillis();
		int daysLate = (int) Math.ceil((mPRD - mPPE) / mDay);
		if(daysLate > stateDaysToLate){
			isLate = true;
		}
		else{
			isLate = false;
		}		
		return isLate;
	}

	@Override
	public String getStateAbbrv() {
		return MissouriCalculation.stateAbbrv;
	}
	
	public int getStateDaysToLate(){
		return MissouriCalculation.stateDaysToLate;
	}
	
	@Override
	public String getStateName(){
		return MissouriCalculation.stateName;
	}
	public BigDecimal getStateWeeksPriorPeriod(){
		return MissouriCalculation.stateWPP;
	}
	
	public TimeZone getTimeZone(){
		return MissouriCalculation.timeZone;
	}

	@Override
	public boolean priorWagesIsComplete(ArrayList<Paycheck> priorWages) {
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		long mPeriod = 0;
		for (Paycheck p1 : priorWages){
			long mP1S = p1.getPayPeriodStart().getTimeInMillis();
			long mP1E = p1.getPayPeriodEnd().getTimeInMillis();
			mPeriod += (mP1E - mP1S);
		}
		if (stateWPP.compareTo(BigDecimal.valueOf(Math.ceil(mPeriod / mWeek))) > 0){
			return false;
		}
		else{
			return true;
		}		
	}
	
	/* for testing purposes
	public void main(){
		WorkCompPaycheck wc = new WorkCompPaycheck();
		BigDecimal test = computeWCSupplementalPayment(wc, new BigDecimal("500"));
	}*/
}
