package Classes;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;

import java.awt.Font;
import javax.swing.JButton;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.ButtonGroup;
import javax.swing.ListSelectionModel;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class WCReimbursementCalculatorMenu {

	private JFrame frmWorkersCompensationLost;
	private DefaultListModel<ReimbursementOverview> claimListModel;
	private JList<ReimbursementOverview> claimantList;
	private static WCReimbursementDAO dataAccess = new WCReimbursementDAO();
	private StateLawCalculable sLC;
	private ListSelectionModel listSelectionModel;
	private JTextPane overviewText;
	private ButtonGroup notCreate;
	private JButton btnCreateNewClaim;
	private JButton btnEditPersonalInfo;
	private JButton btnEditClaimHistory;
	private JButton btnChangeInjuryDate;
	private JButton btnEntercompletePriorWage;
	private JButton btnEditWageReimbursement;
	private JButton btnAddWorkComp;
	private JButton btnAddLightDuty;
	private JButton btnAddTtdWork;
	private JButton btnAddTpdWork;
	private JButton btnViewClaimDetails;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WCReimbursementCalculatorMenu window = new WCReimbursementCalculatorMenu();
					window.frmWorkersCompensationLost.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    		if(dataAccess.shutdownAllConnectionInstances()){
		    			System.gc();
		    			System.exit(0);
		    		}
		    		else {
		    			System.out.println("Error closing down database connection. You may need to force quit the application.");
		    		}
		    }
		});
	}

	/**
	 * Create the application.
	 */
	public WCReimbursementCalculatorMenu() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWorkersCompensationLost = new JFrame();
		frmWorkersCompensationLost.setFont(new Font("Dialog", Font.BOLD, 12));
		frmWorkersCompensationLost.setTitle("Worker's Compensation Lost Wages Calculator v1.0");
		frmWorkersCompensationLost.setBounds(100, 100, 700, 740);
		frmWorkersCompensationLost.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frmWorkersCompensationLost.getContentPane().setLayout(null);
		
		notCreate = new ButtonGroup();
		
		table = new JTable();
		table.setIntercellSpacing(new Dimension(10, 0));
		table.setFont(new Font("SansSerif", Font.BOLD, 15));
		table.setFillsViewportHeight(true);
		table.setRowHeight(100);
		table.setRowSelectionAllowed(false);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"Claim History", null},
				{"Temp. Total Disability", null},
				{"Temp. Partial Disability", null},
				{"Reimbursement Overview", ""},
			},
			new String[] {
				"", ""
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(188);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(428);
		table.setBounds(6, 291, 672, 401);
		frmWorkersCompensationLost.getContentPane().add(table);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setVgap(8);
		panel.setBounds(6, 11, 195, 151);
		frmWorkersCompensationLost.getContentPane().add(panel);
		
		btnCreateNewClaim = new JButton("Create New Claim");
		btnCreateNewClaim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editPersonalInfo(true);
			}
		});
		btnCreateNewClaim.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCreateNewClaim.setPreferredSize(new Dimension(180, 30));
		btnCreateNewClaim.setMinimumSize(new Dimension(130, 30));
		btnCreateNewClaim.setMaximumSize(new Dimension(130, 30));
		panel.add(btnCreateNewClaim);
		
		btnEditPersonalInfo = new JButton("Edit Claimant Personal Info");
		btnEditPersonalInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editPersonalInfo(false);
			}
		});
		btnEditPersonalInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		btnEditPersonalInfo.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEditPersonalInfo.setPreferredSize(new Dimension(180, 30));
		btnEditPersonalInfo.setMinimumSize(new Dimension(140, 30));
		btnEditPersonalInfo.setMaximumSize(new Dimension(140, 30));
		btnEditPersonalInfo = setButtonTextFit(btnEditPersonalInfo, "Edit Claimant Personal Info");
		panel.add(btnEditPersonalInfo);
		notCreate.add(btnEditPersonalInfo);
		
		this.claimantList = new JList<ReimbursementOverview>(this.claimListModel);
		claimantList.setName("Claims");
		claimantList.setValueIsAdjusting(true);
		claimantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		claimantList.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
		claimantList.setBounds(207, 11, 467, 151);
		frmWorkersCompensationLost.getContentPane().add(claimantList);
		
		listSelectionModel = claimantList.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
		
        btnEditClaimHistory = new JButton("Begin Claim History");
		btnEditClaimHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editClaimSummary(claimantList.getSelectedValue(), true, false, false);
				
			}
		});
		btnEditClaimHistory.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEditClaimHistory.setMinimumSize(new Dimension(140, 30));
		btnEditClaimHistory.setMaximumSize(new Dimension(140, 30));
		btnEditClaimHistory.setPreferredSize(new Dimension(180, 30));
		panel.add(btnEditClaimHistory);
		notCreate.add(btnEditClaimHistory);

		
		btnChangeInjuryDate = new JButton("Change Injury Date");
		btnChangeInjuryDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editClaimSummary(claimantList.getSelectedValue(), false, true, false);
			}
		});
		btnChangeInjuryDate.setFont(new Font("Dialog", Font.BOLD, 12));
		btnChangeInjuryDate.setPreferredSize(new Dimension(180, 30));
		panel.add(btnChangeInjuryDate);
		notCreate.add(btnChangeInjuryDate);

		
		this.claimListModel = new DefaultListModel<ReimbursementOverview>();
		loadExistingData();
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(11);
		flowLayout_1.setHgap(9);
		panel_1.setBounds(0, 165, 690, 127);
		frmWorkersCompensationLost.getContentPane().add(panel_1);
		
		btnEntercompletePriorWage = new JButton("Enter/Complete Prior Wage Payments");
		btnEntercompletePriorWage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editClaimSummary(claimantList.getSelectedValue(), false, false, true);
			}
		});
		btnEntercompletePriorWage.setPreferredSize(new Dimension(242, 30));
		btnEntercompletePriorWage.setFont(new Font("Dialog", Font.BOLD, 12));
		panel_1.add(btnEntercompletePriorWage);
		notCreate.add(btnEntercompletePriorWage);

		
		btnEditWageReimbursement = new JButton("Start Wage Reimbursement Details");
		btnEditWageReimbursement.setIconTextGap(10);
		btnEditWageReimbursement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startWageReimbursementDetails();
			}
		}); 
		btnEditWageReimbursement.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEditWageReimbursement.setPreferredSize(new Dimension(228, 30));
		btnEditWageReimbursement.setMaximumSize(new Dimension(228, 30));
		btnEditWageReimbursement.setHorizontalTextPosition(SwingConstants.CENTER);
		panel_1.add(btnEditWageReimbursement);
		notCreate.add(btnEditWageReimbursement);

		
		btnAddWorkComp = new JButton("Add Work Comp Payments");
		btnAddWorkComp.setMinimumSize(new Dimension(180, 30));
		btnAddWorkComp.setMaximumSize(new Dimension(180, 30));
		btnAddWorkComp.setIconTextGap(10);
		btnAddWorkComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEditWageReimbursement.doClick();
			}
		});
		btnAddWorkComp.setFont(new Font("Dialog", Font.BOLD, 12));
		btnAddWorkComp.setPreferredSize(new Dimension(180, 30));
		panel_1.add(btnAddWorkComp);
		notCreate.add(btnAddWorkComp);

		
		btnAddTtdWork = new JButton("Add TTD Work Comp Payments");
		btnAddTtdWork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTTDWCPaychecks(claimantList.getSelectedValue().getTTDRSumm(), false);
			}
		});
		btnAddTtdWork.setFont(new Font("Dialog", Font.BOLD, 12));
		panel_1.add(btnAddTtdWork);
		notCreate.add(btnAddTtdWork);
		
		btnAddTpdWork = new JButton("Add TPD Work Comp Payments");
		btnAddTpdWork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTPDWCPaychecks(claimantList.getSelectedValue().getTPDRSumm());
			}
		});
		btnAddTpdWork.setFont(new Font("Dialog", Font.BOLD, 12));
		panel_1.add(btnAddTpdWork);
		notCreate.add(btnAddTpdWork);
		
				
				btnAddLightDuty = new JButton("Add Light Duty Work Payment");
				btnAddLightDuty.setIconTextGap(10);
				btnAddLightDuty.setFont(new Font("Dialog", Font.BOLD, 12));
				btnAddLightDuty.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//boolean balancedPayments = 
						addWorkPayments(claimantList.getSelectedValue().getTPDRSumm()); // Make sure these payments are balanced at the end of the implemented method
					}
				});
				panel_1.add(btnAddLightDuty);
				notCreate.add(btnAddLightDuty);

		
		btnViewClaimDetails = new JButton("View Claim Details");
		btnViewClaimDetails.setIconTextGap(20);
		btnViewClaimDetails.setHorizontalTextPosition(SwingConstants.CENTER);
		btnViewClaimDetails.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnViewClaimDetails.setPreferredSize(new Dimension(140, 30));
		btnViewClaimDetails.setFont(new Font("Dialog", Font.BOLD, 12));
		panel_1.add(btnViewClaimDetails);
		
		overviewText = new JTextPane();
		overviewText.setToolTipText("Display of currently available data from Selected Claim Lost Wage Reimbursement Overview");
		overviewText.setEditable(false);
		overviewText.setBounds(6, 291, 672, 401);
		frmWorkersCompensationLost.getContentPane().add(overviewText);
	}
	
	public StateLawCalculable selectStateLawCalculable(){
		StateLawCalculable sLC = null;
		String state = (String) JOptionPane.showInternalInputDialog(frmWorkersCompensationLost, 
				"Select State in which claim is being filed:", 
				"Select Available State", 
				JOptionPane.PLAIN_MESSAGE, null, (new StatesWithCalculations()).getAvailableStateNamesArray().toArray(), null);
		for(StateLawCalculable s : (new StatesWithCalculations())){
			if(state.compareTo(s.getStateName()) == 0){
				sLC = s;
			}
		}
		this.sLC = sLC;
		return sLC;
	}
	
	public int getPositiveInt(String message, String title){
		String s = JOptionPane.showInputDialog(frmWorkersCompensationLost, message, title, JOptionPane.PLAIN_MESSAGE);
		int i = -1;
		while((i = Integer.parseInt(s)) < 1){
			s = JOptionPane.showInputDialog(frmWorkersCompensationLost, message + "(Enter only Positive numerical value)", title, JOptionPane.PLAIN_MESSAGE);
		}
		return i;
	}
	// To get Date Injured or RSumm paycheck/WCpaycheck dates
	public GregorianCalendar getCalendar(String message, String title, boolean isPayment, boolean isPPS){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		JXMonthView mV = new JXMonthView();
		JXDatePicker picker = new JXDatePicker();
		ReimbursementOverview r = claimantList.getSelectedValue();
		CompClaim cS = r.getTTDRSumm().getClaimSummary();
		if(isPayment){
			if(isPPS){
				Calendar start = cS.getDateInjured();
				start.setTimeInMillis(start.getTimeInMillis() - mWeek);
				Calendar end = new GregorianCalendar();
				mV.addSelectionInterval(start.getTime(), end.getTime());
				picker.setMonthView(mV);
			}
			else{
				Calendar start = cS.getDateInjured();
				Calendar end = new GregorianCalendar();
				mV.addSelectionInterval(start.getTime(), end.getTime());
				picker.setMonthView(mV);
			}
		}
		
		Object[] params = {message,picker};		
		GregorianCalendar selected = new GregorianCalendar();
		JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.PLAIN_MESSAGE);
		selected.setTime(((JXDatePicker)params[1]).getDate());
		while(selected.compareTo(new GregorianCalendar()) == 0){
			String m = "You must select a date within the dates provided in order to continue." +System.getProperty("line.separator")+
					"If you do not wish to continue and would like to enter dates at a later time, click CANCEL, otherwise OK to select a date and proceed.";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, m, null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION){
				return null;
			}
			JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.PLAIN_MESSAGE);
			selected.setTime(((JXDatePicker)params[1]).getDate());
		}
		
		return selected;
	}
	
	public GregorianCalendar getPriorWageCalendar(String message, String title, CompClaim claimSumm, boolean ppS){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		JXMonthView mV = new JXMonthView();
		JXDatePicker picker = new JXDatePicker();
		if(ppS){
			Calendar start = claimSumm.getEarliestPriorWageDate();
			start.setTimeInMillis(start.getTimeInMillis() - mWeek);
			Calendar end = claimSumm.getPriorWeekStart();
			end.setTimeInMillis(end.getTimeInMillis() + (mWeek - mDay));
			mV.addSelectionInterval(start.getTime(), end.getTime());
			picker.setMonthView(mV);
		}
		else {
			Calendar start = claimSumm.getEarliestPriorWageDate();
			start.setTimeInMillis(start.getTimeInMillis());
			Calendar end = claimSumm.getPriorWeekStart();
			end.setTimeInMillis(end.getTimeInMillis() + (mWeek*2));
			mV.addSelectionInterval(start.getTime(), end.getTime());
			picker.setMonthView(mV);
		}
		
		Object[] params = {message,picker};		
		GregorianCalendar selected = new GregorianCalendar();
		JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.PLAIN_MESSAGE);
		selected.setTime(((JXDatePicker)params[1]).getDate());
		while(selected.compareTo(new GregorianCalendar()) == 0){
			String m = "You must select a date within the dates provided in order to continue." +System.getProperty("line.separator")+
					"If you do not wish to continue and would like to add paychecks at a later time, click CANCEL, otherwise OK to select a date and proceed.";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, m, null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION){
				return null;
			}
			JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.PLAIN_MESSAGE);
			selected.setTime(((JXDatePicker)params[1]).getDate());
		}	
		
		return selected;
	}
	
	public Paycheck createPriorWagePaycheck(CompClaim claimSumm){
		Paycheck pc = null;
		GregorianCalendar pPS = getPriorWageCalendar("Select Pay Period Start Date", "Pay Period Start", claimSumm, true);
		if(pPS == null){
			return null;
		}
		GregorianCalendar pPE = getPriorWageCalendar("Select Pay Period End Date", "Pay Period End", claimSumm, false);
		if(pPE == null){
			return null;
		}
		GregorianCalendar pD = getPriorWageCalendar("Select Payment Date", "Payment Date", claimSumm, false);
		if(pD == null){
			return null;
		}
		String grossAmnt = getPositiveBigDecimalString("Enter Gross Amount (Before Taxes and Deductions) of Paycheck", "Enter Gross Amount");
		if(grossAmnt.compareTo("") == 0){
			return null;
		}
		pc = new Paycheck(grossAmnt, pD, pPS, pPE);
		return pc;
	}
	
	public WorkCompPaycheck createWorkCompPaycheck(){
		WorkCompPaycheck wcPC = null;
		GregorianCalendar pPS = getCalendar("Select Pay Period Start Date", "Pay Period Start", true, true);
		if(pPS == null){
			return null;
		}
		GregorianCalendar pPE = getCalendar("Select Pay Period End Date", "Pay Period End", true, false);
		if(pPE == null){
			return null;
		}
		GregorianCalendar pD = getCalendar("Select Payment Date", "Payment Date", true, false);
		if(pD == null){
			return null;
		}
		GregorianCalendar pRD = getCalendar("Select Day That Payment Was Received", "Payment Received Date", true, false);
		if(pRD == null){
			return null;
		}
		String grossAmnt = getPositiveBigDecimalString("Enter Gross Amount (Before Taxes and Deductions) of Paycheck", "Enter Gross Amount");
		if(grossAmnt.compareTo("") == 0){
			return null;
		}
		boolean isContested = (JOptionPane.showConfirmDialog(frmWorkersCompensationLost, 
				"Has the claim been contested at any point during this pay period and had to be resolved in court?", "Contested Pay Period?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
		GregorianCalendar contestRslvdDate = null;
		if(isContested){
			contestRslvdDate = getCalendar("Select Day That the Contest Was Resolved: ", "Contest Resolved Date", true, false);
		}
		wcPC = new WorkCompPaycheck(grossAmnt, pRD, pPS, pPE, isContested, sLC, pD);
		if(contestRslvdDate != null){
			wcPC.setContestResolutionDate(contestRslvdDate);
		}
		return wcPC;
	}
	
	//returns true if TPD is initialized within the selected ReimbursementOverview and inserted into the DB
	public boolean startWageReimbursementDetails(){
		String eol = System.getProperty("line.separator");
		
		ReimbursementOverview ro = claimantList.getSelectedValue();
		if(ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment() != null){
			ro.ttdRSumm.setCalculatedWeeklyPayment(sLC.computeCalculatedWeeklyPayment(ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment()));
			claimListModel.set(claimantList.getSelectedIndex(), ro);
		}
		if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "Is the Injured Person able to work any hours?", "Able to Work?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			if(dataAccess.selectTTDRSummary(ro.getClaimant().getID()) == null){
				dataAccess.insertRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), new BigDecimal("0"));
			}
			else{
				if(ro.getTTDRSumm().getAmountNotPaid().compareTo(new BigDecimal("0")) <= 0){
					dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), new BigDecimal("0"));
				}
				else{
					dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), ro.getTTDRSumm().getAmountNotPaid());
				}
			}
			String message = "Your current Calculated Weekly Payment owed from Work Comp is: $"+ro.getTTDRSumm().getCalculatedWeeklyPayment().toPlainString()+eol+
					"Would you like to add Work Comp Payments now? (You may enter them later if you wish)"+eol+
					"(Note: Work Comp Payment entry is required in order to compute and keep track of any missing pay owed to the Injured Person)";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Enter Work Comp Payments?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return false;
			}
			addTTDWCPaychecks(ro.getTTDRSumm(), true);
			ro = claimantList.getSelectedValue();
			return false;
		}
		else { 
			if(ro.getTPDRSumm() == null){
				ro.setTPDRSumm(new TPDReimbursementSummary(ro.getTTDRSumm()));
				ro.tpdRSumm.setAmountNotPaid(new BigDecimal("0"));
				claimListModel.set(claimantList.getSelectedIndex(), ro);
			}
			if(dataAccess.selectTPDRSummary(ro.getClaimant().getID()) == null){
				dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid());
			}
			else{
				dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid());
			}
				String message = "The current Calculated Weekly Payment owed from Work Comp during pay periods with no hours worked is: $"+ro.getTTDRSumm().getCalculatedWeeklyPayment().toPlainString()+eol+
					"Would you like to add Work Comp Supplemental Payments for pay periods in which the Injured person was able to work hours (TPD) now? (You may enter them later if you wish.)"+eol+
					"Select NO to continue to prompt for non-working (TTD) pay periods."+eol+
					"(Note: Work Comp Payment entry is required in order to compute and keep track of any missing pay owed to the Injured Person)";					
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Enter TPD Work Comp Payments?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				message = "Your current Calculated Weekly Payment owed from Work Comp is: $"+ro.getTTDRSumm().getCalculatedWeeklyPayment().toPlainString()+eol+
						"Would you like to add Work Comp (TTD) Payments now? (You may enter them later if you wish)"+eol+
						"(Note: Work Comp Payment entry is required in order to compute and keep track of any missing pay owed to the Injured Person)";
				if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Enter Work Comp (TTD) Payments?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
					return true;
				}
				addTTDWCPaychecks(ro.getTTDRSumm(), true);
				ro = claimantList.getSelectedValue();
			}
			message = "The current Calculated Weekly Payment owed from Work Comp during pay periods with no hours worked is: $"+ro.getTTDRSumm().getCalculatedWeeklyPayment().toPlainString()+eol+
					"Would you like to add Work Comp Supplemental Payments for pay periods in which the Injured person was able to work hours (TPD) now? (You may enter them later if you wish.)"+eol+
					"(Note: Work Comp Payment entry is required in order to compute and keep track of any missing pay owed to the Injured Person)";					
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Enter TPD Work Comp Payments?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return true;
			}
			else{
				addTPDWCPaychecks(ro.getTPDRSumm());
				ro = claimantList.getSelectedValue();
				return true;
			}

		}
	}
	
	public boolean addTTDWCPaychecks(TTDReimbursementSummary rs, boolean overridePrompt){
		ReimbursementOverview ro = claimantList.getSelectedValue();
		String eol = System.getProperty("line.separator");
		if(!overridePrompt){
			String message = "Your current Calculated Weekly Payment owed from Work Comp is: $"+rs.getCalculatedWeeklyPayment().toPlainString()+eol+
					"Select OK to add Work Comp (TTD) Payments. (You may add them later as well)"+eol+
					"(Note: Work Comp Payment entry is required in order to compute and keep track of any missing pay owed to the Injured Person)";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Enter Work Comp (TTD) Payments?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
				return false;
			}
		}
		int ok = JOptionPane.OK_OPTION;
		ArrayList<WorkCompPaycheck> wcPayments = new ArrayList<WorkCompPaycheck>();
		if(rs.getWCPayments().size() > 0){
			wcPayments = rs.getWCPayments();
		}
		
		label:while(ok == JOptionPane.OK_OPTION){
			boolean worked = false;
			WorkCompPaycheck wcPC = createWorkCompPaycheck();
			if(wcPC == null){
				ok = JOptionPane.CANCEL_OPTION;
				break label;
			}
			else{
				long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
				long mWeek = mDay * 7;
				Calendar lastFDDate = ro.getTTDRSumm().getClaimSummary().getPriorWeekStart();
				lastFDDate.setTimeInMillis(lastFDDate.getTimeInMillis() + ((mWeek*2)-mDay));
				if (wcPC.getPayPeriodStart().compareTo(lastFDDate) < 0){
					String message = "This work comp paycheck appears to be from the week of injury."+eol+
							"Were any hours worked during this week?";
					if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Any Hours Worked?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
						worked = true;
						message = "This work comp payment cannot be entered under TTD."+eol+ 
								"To add this payment, please do so under TPD work comp payments when you are finished adding TTD work comp payments.";
						JOptionPane.showMessageDialog(frmWorkersCompensationLost, message, "Enter Under TPD Payments", JOptionPane.PLAIN_MESSAGE);
					}
				}
				if(!worked){
					wcPayments = sLC.addWCPaycheck(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
					rs.setWCPayments(wcPayments);
					ro.setTTDRSumm(rs);
					claimListModel.set(claimantList.getSelectedIndex(), ro);
				}
			}
			String message = "";
			if(!worked){
				message = "Payment Added. "+eol+
						"Select OK to add another work comp payment, or Select CANCEL to save and exit Work Comp Payment Entry.";
			}
			else{
				message = "Select OK to add another TTD work comp payment, or Select CANCEL to save and exit Work Comp Payment Entry.";
			}
			ok = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Add Another Payment?", JOptionPane.OK_CANCEL_OPTION);
		}
		
		rs.setWCPayments(wcPayments);
		rs.computeAmountNotPaidAndAnyLateCompensation();
		ro.setTTDRSumm(rs);
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid());
		for(WorkCompPaycheck p : rs.wcPayments){
			dataAccess.insertWCPaychecks(ro.getClaimant().getID(), "TTD", p.getIsContested(),
					p.getIsLate(), p.getFullTimeHours(), (Date)p.getPayReceivedDate().getTime(), (Date)p.getPaymentDate().getTime(),
					(Date)p.getPayPeriodStart().getTime(), (Date)p.getPayPeriodEnd().getTime(), p.getGrossAmount(), p.getAmountStillOwed(), (Date)p.getContestResolutionDate().getTime());
		}
		return true;
	}
	
	public boolean addTPDWCPaychecks(TPDReimbursementSummary rs){ //use supplementalCalculation
		String eol = System.getProperty("line.separator");
		ReimbursementOverview ro = claimantList.getSelectedValue();
		int ok = JOptionPane.OK_OPTION;
		ArrayList<WorkCompPaycheck> wcPayments = new ArrayList<WorkCompPaycheck>();
		ArrayList<Paycheck> workPayments = new ArrayList<Paycheck>();
		if(rs.getWCPayments().size() > 0){
			wcPayments = rs.getWCPayments();
		}
		if(rs.getReceivedWorkPayments().size() > 0){
			workPayments = rs.getReceivedWorkPayments();
		}
		label:while(ok != JOptionPane.NO_OPTION){
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "For the pay period to be entered, did the Injured Person work regular Full-Time hours?",
					"Full Time Hours Worked?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				String message = "Since Full Time Hours were worked during this pay period, no supplemental wage payments are owed."+eol+
						"If you received a payment that needs entered, find out the pay period dates and enter the payment for those dates."+eol+
						"Entering the correct dates will determine if the payment is late which may result in addtional payment owed. "+
						"You may proceed to enter the payment without these dates, but it will only account for computing the difference in the total amount not paid for the duration of the claim."+eol+
						"(Note: Entered Payments are final and cannot be edited in the current version.)"+eol+
						"(You may need to contact the Workers' Compensation Insurance Adjuster if these dates are not listed on the paystub)";
				JOptionPane.showMessageDialog(frmWorkersCompensationLost, message, "No Payment Owed", JOptionPane.PLAIN_MESSAGE);
				if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "Would you like to continue entering this payment?", "Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					WorkCompPaycheck wcPC = createWorkCompPaycheck();
					if(wcPC == null){
						ok = JOptionPane.CANCEL_OPTION;
						break label;
					}
					else{
						wcPC.setFullTimeHours(true);
						wcPayments = sLC.addWCPaycheck(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
						rs.setWCPayments(wcPayments);
						ro.setTPDRSumm(rs);
						claimListModel.set(claimantList.getSelectedIndex(), ro);
					}
					message = "Payment Added. "+eol+
							"Select OK to add another payment, or Select NO to save and exit Work Comp Payment Entry.";
					ok = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Add Another Payment?", JOptionPane.YES_NO_OPTION);
				}
				
			}
			else{
				WorkCompPaycheck wcPC = createWorkCompPaycheck();
				if(wcPC == null){
					ok = JOptionPane.CANCEL_OPTION;
					break label;
				}
				else{
					wcPayments = sLC.addWCPaycheck(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
					rs.setWCPayments(wcPayments);
					ro.setTPDRSumm(rs);
					claimListModel.set(claimantList.getSelectedIndex(), ro);
				}
				String message = "Payment Added. "+eol+
						"Select OK to add another payment, or Select NO to save and exit Work Comp Payment Entry.";
				ok = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Add Another Payment?", JOptionPane.YES_NO_OPTION);
			}
			String message = "Would you like to enter a regular light duty paycheck from Employer for hours worked during this period? "+
					"(You may enter it separately at a later time if you wish.)"+eol+
					"(Note: This is necessary to compute appropriate amount owed for Work Comp Payments(TPD).)";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Enter Light Duty Work Payment?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				Paycheck pc = createWorkPayment();
				
				try {
					workPayments = sLC.addTPDWorkPaycheck(pc, workPayments, rs.getClaimSummary().getPriorWeekStart());
				} catch (Exception e) {
					e.printStackTrace();
					pc = trimWorkPayment(rs, pc);
					try {
						workPayments = sLC.addTPDWorkPaycheck(pc, workPayments, rs.getClaimSummary().getPriorWeekStart());
					} catch (Exception ex) {
						long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
						long mWeek = mDay * 7;
						ex.printStackTrace();
						System.out.println("Paycheck parameters: " + pc.toString());
						Calendar fD = rs.getClaimSummary().getPriorWeekStart();
						fD.setTimeInMillis(rs.getClaimSummary().getPriorWeekStart().getTimeInMillis() + mWeek);
						SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
						formatter.setLenient(false);
						Date day = (Date) fD.getTime();
						System.out.println("Paycheck start day needs to be on or after the following date to be added: " +  formatter.format(day));
					}
				}
	
				ArrayList<Paycheck> pcList = dataAccess.selectPaychecks(ro.getClaimant().getID(), "WORKPAYMENT");
				boolean exists = false;
				for(Paycheck p : pcList){
					exists = (p.getPayPeriodEnd().compareTo(pc.getPayPeriodEnd()) == 0);
				}
				if(!exists){
					dataAccess.insertPaychecks(ro.getClaimant().getID(), "WORKPAYMENT", (Date)pc.getPaymentDate().getTime(), (Date)pc.getPayPeriodStart().getTime(), (Date)pc.getPayPeriodEnd().getTime(), pc.getGrossAmount());
				}
				else{
					message = "Paycheck with same period end date already exists."+eol+
							"Select OK to Overwrite paycheck with same date, otherwise CANCEL to keep existing check with same date."+eol+ 
							"(If this is a pay adjustment check for a previously entered pay period, CANCEL and add the Gross Amount from the two checks and enter them as one check with the corresponding dates of pay period)";
					if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Overwrite Previous Pay Period Check?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
						dataAccess.updatePaychecks(ro.getClaimant().getID(), "WORKPAYMENT", (Date)pc.getPaymentDate().getTime(), (Date)pc.getPayPeriodStart().getTime(), (Date)pc.getPayPeriodEnd().getTime(), pc.getGrossAmount());
					}
					else{
						workPayments.remove(workPayments.size()-1);
					}
				}
				rs.updateReceivedWorkPayments(workPayments);
				ro.setTPDRSumm(rs);
				claimListModel.set(claimantList.getSelectedIndex(), ro);
			}
		}
		rs.computeAmountNotPaidAndAnyLateCompensation();
		ro.setTPDRSumm(rs);
		if(dataAccess.selectTPDRSummary(ro.getClaimant().getID()) == null){
			dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid());
		}
		else{
			dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid());
		}
		for(WorkCompPaycheck p : ro.tpdRSumm.wcPayments){
			dataAccess.insertWCPaychecks(ro.getClaimant().getID(), "TPD", p.getIsContested(),
					p.getIsLate(), p.getFullTimeHours(), (Date)p.getPayReceivedDate().getTime(), (Date)p.getPaymentDate().getTime(),
					(Date)p.getPayPeriodStart().getTime(), (Date)p.getPayPeriodEnd().getTime(), p.getGrossAmount(), p.getAmountStillOwed(), (Date)p.getContestResolutionDate().getTime());
		}
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		return true;
	}
	
	public boolean addWorkPayments(TPDReimbursementSummary rs){
		String eol = System.getProperty("line.separator");
		ReimbursementOverview ro = claimantList.getSelectedValue();
		ArrayList<Paycheck> workPayments = new ArrayList<Paycheck>();
		if(rs.getReceivedWorkPayments().size() > 0){
			workPayments = rs.getReceivedWorkPayments();
		}
		int yes = JOptionPane.YES_OPTION;
		while(yes == JOptionPane.YES_OPTION){
			Paycheck pc = createWorkPayment();
			if(pc == null){
				return false;
			}
			boolean exists = false;
			for(Paycheck p : workPayments){
				exists = (p.getPayPeriodEnd().compareTo(pc.getPayPeriodEnd()) == 0);
			}
			if(!exists){
				try {
					workPayments = sLC.addTPDWorkPaycheck(pc, workPayments, rs.getClaimSummary().getPriorWeekStart());
				} catch (Exception e) {
					e.printStackTrace();
					pc = trimWorkPayment(rs, pc);
					try {
						workPayments = sLC.addTPDWorkPaycheck(pc, workPayments, rs.getClaimSummary().getPriorWeekStart());
					} catch (Exception ex) {
						long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
						long mWeek = mDay * 7;
						ex.printStackTrace();
						System.out.println("Paycheck parameters: " + pc.toString());
						Calendar fD = rs.getClaimSummary().getPriorWeekStart();
						fD.setTimeInMillis(rs.getClaimSummary().getPriorWeekStart().getTimeInMillis() + mWeek);
						SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
						formatter.setLenient(false);
						Date day = (Date) fD.getTime();
						System.out.println("Paycheck start day needs to be on or after the following date to be added: " +  formatter.format(day));
					}
				}
	
				dataAccess.insertPaychecks(ro.getClaimant().getID(), "WORKPAYMENT", (Date)pc.getPaymentDate().getTime(), (Date)pc.getPayPeriodStart().getTime(), (Date)pc.getPayPeriodEnd().getTime(), pc.getGrossAmount());
			}
			else{
				String message = "Paycheck with same period end date already exists."+eol+
						"Select OK to Overwrite paycheck with same date, otherwise CANCEL to keep existing check with same date."+eol+ 
						"(If this is a pay adjustment check for a previously entered pay period, CANCEL and add the Gross Amount from the two checks and enter them as one check with the corresponding dates of pay period)";
				if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Overwrite Previous Pay Period Check?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
					dataAccess.updatePaychecks(ro.getClaimant().getID(), "WORKPAYMENT", (Date)pc.getPaymentDate().getTime(), (Date)pc.getPayPeriodStart().getTime(), (Date)pc.getPayPeriodEnd().getTime(), pc.getGrossAmount());
				}
				else{
					workPayments.remove(workPayments.size()-1);
				}
			}
			rs.updateReceivedWorkPayments(workPayments);
			yes = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "Would you like to add another Light Duty Work Payment? (You can enter more later.)", "Add Another Payment?", JOptionPane.YES_NO_OPTION);
		}
		rs.computeAmountNotPaidAndAnyLateCompensation();
		ro.setTPDRSumm(rs);
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		if(dataAccess.selectTPDRSummary(ro.getClaimant().getID()) == null){
			dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid());
		}
		else{
			dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid());
		}
		return true;
	}	

	
	public Paycheck createWorkPayment(){
		Paycheck pc = null;
		GregorianCalendar pPS = getCalendar("Select Pay Period Start Date", "Pay Period Start", true, true);
		if(pPS == null){
			return null;
		}
		GregorianCalendar pPE = getCalendar("Select Pay Period End Date", "Pay Period End", true, false);
		if(pPE == null){
			return null;
		}
		GregorianCalendar pD = getCalendar("Select Payment Date", "Payment Date", true, false);
		if(pD == null){
			return null;
		}
		String grossAmnt = getPositiveBigDecimalString("Enter Gross Amount (Before Taxes and Deductions) of Paycheck", "Enter Gross Amount");
		if(grossAmnt.compareTo("") == 0){
			return null;
		}
		pc = new Paycheck(grossAmnt, pD, pPS, pPE);
		return pc;
	}
	
	public Paycheck trimWorkPayment(TPDReimbursementSummary rs, Paycheck pc){
		String eol = System.getProperty("line.separator");
		String message = "To continue entering work payment from week of Inury, You will need the total hours worked during the entire pay period, AND the hours worked during the week injured."+eol+
				"If you wish to Exit and enter later, confirm without entering a value. Otherwise, Enter TOTAL Hours Worked for the pay period: ";
		String totalHrsWrked = getPositiveBigDecimalString(message, "Enter Period Total Hours Worked");
		
		message = "Enter Hours Worked ONLY from the start of the week in which Injury occurred: ";
		String wkInjHrsWrked = getPositiveBigDecimalString(message, "Enter Week of Injury Hours Worked");
		
		pc = rs.trimFirstWorkPayment(pc, totalHrsWrked, wkInjHrsWrked);
		
		return pc;
	}
	
	public String getPositiveBigDecimalString(String message, String title){
		String s = JOptionPane.showInputDialog(frmWorkersCompensationLost, message, title, JOptionPane.PLAIN_MESSAGE);
		while(Double.parseDouble(s) < 1){
			s = JOptionPane.showInputDialog(frmWorkersCompensationLost, message + "(Enter only Positive numerical value)", title, JOptionPane.PLAIN_MESSAGE);
		}
		
		return s;
	}
	
	public void loadExistingData(){
		ArrayList<ReimbursementOverview> roList = dataAccess.selectAllReimbursementOverviews();
		for(ReimbursementOverview r : roList){
			this.claimListModel.add(claimListModel.size(), r);
		}
	}
	
	public boolean editClaimSummary(ReimbursementOverview ro, boolean create, boolean dInjOnly, boolean pcOnly){
		if(create){
			String eol = System.getProperty("line.separator");
			String historyMessage = "To create history you must have the following:"+eol+
					"Date of Injury"+eol+
					"Paychecks covering previous 13 weeks of Work Payments starting with (and including) the week immediately preceding the week injury was sustained."+eol+
					"(Note: Pay for hours worked after the end of the prior week are not considered when computing the Average Gross Weekly Wage prior to injury)"+eol+
					"If You have this information available, select OK to continue. Otherwise, select CANCEL to enter later.";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, historyMessage, "Information Required to Continue", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
				return false;
			}
			// Int selection of date obsolete with popup date picker, saving in case problems are encountered
			//int year = getPositiveInt("Enter Year of Injury: ", "Enter Year Injured");
			//int month = getPositiveInt("Enter Month of Injury: ", "Enter Month Injured");
			//int day = getPositiveInt("Enter Day of Month (i.e. 21 for January 21st) of Injury: ", "Enter Day Injured");
			Calendar dateInjured = getCalendar("Select the Date of Injury", "Date Injured", false, false);
			CompClaim cHist = new CompClaim((Date)dateInjured.getTime(), sLC);
			dataAccess.insertClaimSummary(ro.getClaimant().getID(), (Date)dateInjured.getTime(), (Date)cHist.getPriorWeekStart().getTime(),
					(Date)cHist.getEarliestPriorWageDate().getTime(), new BigDecimal("-1"), cHist.getDaysInjured(), cHist.getWeeksInjured());
			ro.setTTDRSumm(new TTDReimbursementSummary());
			ro.ttdRSumm.setClaimSummary(cHist);
			claimListModel.set(claimantList.getSelectedIndex(), ro);
			int ok = JOptionPane.OK_OPTION;
			ArrayList<Paycheck> priorWages = new ArrayList<Paycheck>();
			while(!sLC.priorWagesIsComplete(priorWages) && ok == JOptionPane.OK_OPTION){
				Paycheck pc = createPriorWagePaycheck(cHist);
				if(pc == null){
					ok = JOptionPane.CANCEL_OPTION;
				}
				else{
					priorWages = sLC.addAndTrimToPriorWages(pc, priorWages, cHist.getPriorWeekStart());
					dataAccess.insertPaychecks(ro.getClaimant().getID(), "PRIORWAGES", (Date)pc.getPaymentDate().getTime(), (Date)pc.getPayPeriodStart().getTime(), (Date)pc.getPayPeriodEnd().getTime(), pc.getGrossAmount());
				}
			}
			if(sLC.priorWagesIsComplete(priorWages)){
				ro.ttdRSumm.claimSummary.setPriorWagesAndComputeAPGWP(priorWages);
				ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
				dataAccess.updateClaimSummary(ro.getClaimant().getID(), (Date)dateInjured.getTime(), (Date)cHist.getPriorWeekStart().getTime(),
						(Date)cHist.getEarliestPriorWageDate().getTime(), ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment(), 
						ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
				claimListModel.set(claimantList.getSelectedIndex(), ro);
				return true;
			}
			else {
				ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
				ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
				dataAccess.updateClaimSummary(ro.getClaimant().getID(), (Date)dateInjured.getTime(), (Date)cHist.getPriorWeekStart().getTime(),
						(Date)cHist.getEarliestPriorWageDate().getTime(), new BigDecimal("-1"), 
						ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
				claimListModel.set(claimantList.getSelectedIndex(), ro);
				return false;
			}
		}
		else{
			if(!pcOnly){
				String eol = System.getProperty("line.separator");
				String historyMessage = "To edit history you must have the following:"+eol+
						"Date of Injury"+eol+
						"(Note: This will automatically update the period for which paychecks prior to injury are used to calculate the weekly payment owed. "
						+ "If you have already entered prior wage paychecks they will be deleted and will need to be reentered.)"+eol+
						"If You still wish to continue, select OK to continue. Otherwise, select CANCEL to exit.";
				if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, historyMessage, "Information Required to Continue", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
					return false;
				}
				// Int selection of date obsolete with popup date picker, saving in case problems are encountered
				//int year = getPositiveInt("Enter Year of Injury: ", "Enter Year Injured");
				//int month = getPositiveInt("Enter Month of Injury: ", "Enter Month Injured");
				//int day = getPositiveInt("Enter Day of Month (i.e. 21 for January 21st) of Injury: ", "Enter Day Injured");
				Calendar dateInjured = getCalendar("Select the Date of Injury", "Date Injured", false, false);
				if (dateInjured.compareTo(ro.getTTDRSumm().getClaimSummary().getDateInjured()) == 0){
					return false;
				}
				dataAccess.deletePaychecksFrmSingleClaim(ro.getClaimant().getID(), "PRIORWAGES");
				CompClaim cHist = new CompClaim((Date)dateInjured.getTime(), sLC);
				dataAccess.updateClaimSummary(ro.getClaimant().getID(), (Date)dateInjured.getTime(), (Date)cHist.getPriorWeekStart().getTime(),
						(Date)cHist.getEarliestPriorWageDate().getTime(), new BigDecimal("-1"), cHist.getDaysInjured(), cHist.getWeeksInjured());
				ro.setTTDRSumm(new TTDReimbursementSummary());
				ro.ttdRSumm.setClaimSummary(cHist);
				claimListModel.set(claimantList.getSelectedIndex(), ro);
			}
			if(!dInjOnly){
				CompClaim cHist = ro.getTTDRSumm().getClaimSummary();
				Calendar dateInjured = cHist.getDateInjured();
				int ok = JOptionPane.OK_OPTION;
				ArrayList<Paycheck> priorWages = new ArrayList<Paycheck>();
				while(!sLC.priorWagesIsComplete(priorWages) && ok == JOptionPane.OK_OPTION){
					Paycheck pc = createPriorWagePaycheck(cHist);
					if(pc == null){
						ok = JOptionPane.CANCEL_OPTION;
					}
					else{
						priorWages = sLC.addAndTrimToPriorWages(pc, priorWages, cHist.getPriorWeekStart());
						dataAccess.insertPaychecks(ro.getClaimant().getID(), "PRIORWAGES", (Date)pc.getPaymentDate().getTime(), (Date)pc.getPayPeriodStart().getTime(), (Date)pc.getPayPeriodEnd().getTime(), pc.getGrossAmount());
					}
				}
				if(sLC.priorWagesIsComplete(priorWages)){
					ro.ttdRSumm.claimSummary.setPriorWagesAndComputeAPGWP(priorWages);
					ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
					dataAccess.updateClaimSummary(ro.getClaimant().getID(), (Date)dateInjured.getTime(), (Date)cHist.getPriorWeekStart().getTime(),
							(Date)cHist.getEarliestPriorWageDate().getTime(), ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment(), 
							ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
					claimListModel.set(claimantList.getSelectedIndex(), ro);
					return true;
				}
				else {
					ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
					ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
					dataAccess.updateClaimSummary(ro.getClaimant().getID(), (Date)dateInjured.getTime(), (Date)cHist.getPriorWeekStart().getTime(),
							(Date)cHist.getEarliestPriorWageDate().getTime(), new BigDecimal("-1"), 
							ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
					claimListModel.set(claimantList.getSelectedIndex(), ro);
					return false;
				}
			}
			if(sLC.priorWagesIsComplete(ro.getTTDRSumm().getClaimSummary().getPriorWages())){
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	public void editPersonalInfo(boolean create){
		if(create){
			String firstName = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's (Injured Person's) First Name?",
					"Enter Claimant's First Name", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String lastName = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's Last Name?",
					"Enter Claimant's Last Name", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String middleName = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's Middle Name?",
					"Enter Claimant's Middle Name", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String workplace = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's Place of Work?",
					"Enter Claimant's Place of Work", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String state = selectStateLawCalculable().getStateName();
			int id = claimListModel.size() + 1;
			Claimant c = new Claimant(id, lastName, firstName, middleName, workplace, state);
			id = dataAccess.insertClaimants(lastName, firstName, middleName, workplace, state);
			c.setID(id);
			ReimbursementOverview ro = new ReimbursementOverview();
			ro.setClaimant(c);
			claimListModel.add(claimListModel.size(), ro);
		}
		else{
			String firstName = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's (Injured Person's) First Name?",
					"Enter Claimant's First Name", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String lastName = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's Last Name?",
					"Enter Claimant's Last Name", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String middleName = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's Middle Name?",
					"Enter Claimant's Middle Name", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String workplace = (String) JOptionPane.showInputDialog(
					frmWorkersCompensationLost,
					"What is the Claimant's Place of Work?",
					"Enter Claimant's Place of Work", JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			String state = selectStateLawCalculable().getStateName();
			ReimbursementOverview ro = (ReimbursementOverview) this.claimantList.getSelectedValue();
			int id = ro.getClaimant().getID();
			Claimant c = new Claimant(id, lastName, firstName, middleName, workplace, state);
			boolean updated = dataAccess.updateClaimants(id, lastName, firstName, middleName, workplace, state);
			if(updated){
				ro.setClaimant(c);
				claimListModel.set(claimantList.getSelectedIndex(), ro);
			}
			else{
				JOptionPane.showMessageDialog(frmWorkersCompensationLost, "Cannot Update Personal Information. Please make sure that a claim has been created for the specified Claimant ID");
			}
		}
	}
	// Ensure Button Text Fits - adapted from: http://stackoverflow.com/a/19194949/6867420
	public JButton setButtonTextFit(JButton button, String text) {
		// Get the original Font from client properties
	    Font originalFont = (Font)button.getClientProperty("originalfont"); 
	    // First time we call it: add it
	    if (originalFont == null) { 
	        originalFont = button.getFont();
	        button.putClientProperty("originalfont", originalFont);
	    }

	    int stringWidth = button.getFontMetrics(originalFont).stringWidth(text);
	    int componentWidth = button.getWidth();

	    // Resize only if needed
	    if (stringWidth > componentWidth) { 
	        // Find out how much the font can shrink in width.
	        double widthRatio = (double)componentWidth / (double)stringWidth;
	        // Keep the minimum size
	        int newFontSize = (int)Math.floor(originalFont.getSize() * widthRatio);

	        // Set the label's font size to the newly determined size.
	        button.setFont(new Font(originalFont.getName(), originalFont.getStyle(), newFontSize));
	    } else{
	    	// Text fits, do not change font size
	    	button.setFont(originalFont); 
	    }
	    	
	    button.setText(text);
	    return button;
	}
	
	class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if(lsm.isSelectionEmpty()){
            	btnEditPersonalInfo.setEnabled(false);
            	btnEditClaimHistory.setEnabled(false);
            	btnChangeInjuryDate.setEnabled(false);
            	btnEntercompletePriorWage.setEnabled(false);
            	btnEditWageReimbursement.setEnabled(false);
            	btnAddWorkComp.setEnabled(false);
            	btnAddLightDuty.setEnabled(false);
            	btnAddTtdWork.setEnabled(false);
            	btnAddTpdWork.setEnabled(false);
            	btnViewClaimDetails.setEnabled(false);
            	TableModel tm = table.getModel();
            	tm.setValueAt("No Claim Selected.", 0, 1);
            	tm.setValueAt("No Claim Selected.", 1, 1);
            	tm.setValueAt("No Claim Selected.", 2, 1);
            	tm.setValueAt("No Claim Selected.", 3, 1);
            	table.setModel(tm);
            }
            else{
            	label:for(StateLawCalculable s : (new StatesWithCalculations())){
        			if(claimantList.getSelectedValue().getClaimant().getState().compareTo(s.getStateName()) == 0){
        				sLC = s;
        				break label;
        			}
        		}

            	if(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary() == null){
            		btnEditPersonalInfo.setEnabled(true);
   	            	btnEditClaimHistory.setEnabled(true);
            		btnChangeInjuryDate.setEnabled(false);
	            	btnEntercompletePriorWage.setEnabled(false);
	            	btnEditWageReimbursement.setEnabled(false);
	            	btnAddWorkComp.setEnabled(false);
	            	btnAddLightDuty.setEnabled(false);
	            	btnAddTtdWork.setEnabled(false);
	            	btnAddTpdWork.setEnabled(false);
	            	btnViewClaimDetails.setEnabled(true);
	            	TableModel tm = table.getModel();
	            	tm.setValueAt("Not Completed.", 0, 1);
	            	tm.setValueAt("Not Completed.", 1, 1);
	            	tm.setValueAt("Not Completed.", 2, 1);
	            	tm.setValueAt("Not Completed.", 3, 1);
	            	table.setModel(tm);
            	}
            	else{
            		if (!sLC.priorWagesIsComplete(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().getPriorWages())){
            			btnEditPersonalInfo.setEnabled(true);
    	            	btnEditClaimHistory.setEnabled(true);
    	            	btnChangeInjuryDate.setEnabled(true);
    	            	btnEntercompletePriorWage.setEnabled(true);
            			btnEditWageReimbursement.setEnabled(false);
		            	btnAddWorkComp.setEnabled(false);
		            	btnAddLightDuty.setEnabled(false);
		            	btnAddTtdWork.setEnabled(false);
		            	btnAddTpdWork.setEnabled(false);
    	            	btnViewClaimDetails.setEnabled(true);
    	            	TableModel tm = table.getModel();
		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
		            	tm.setValueAt("Not Completed.", 1, 1);
		            	tm.setValueAt("Not Completed.", 2, 1);
		            	tm.setValueAt("Not Completed.", 3, 1);
		            	table.setModel(tm);
            		}
            		else{
            			if(claimantList.getSelectedValue().getTTDRSumm().getWCPayments().size() < 1 && claimantList.getSelectedValue().getTPDRSumm().getWCPayments().size() < 1){
            				btnEditPersonalInfo.setEnabled(true);
        	            	btnEditClaimHistory.setEnabled(true);
        	            	btnChangeInjuryDate.setEnabled(true);
        	            	btnEntercompletePriorWage.setEnabled(true);
        	            	btnEditWageReimbursement.setEnabled(true);
            				btnAddWorkComp.setEnabled(false);
			            	btnAddLightDuty.setEnabled(false);
			            	btnAddTtdWork.setEnabled(false);
			            	btnAddTpdWork.setEnabled(false);
			            	btnViewClaimDetails.setEnabled(true);
			            	TableModel tm = table.getModel();
    		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
			            	tm.setValueAt("Not Completed.", 1, 1);
			            	tm.setValueAt("Not Completed.", 2, 1);
			            	tm.setValueAt("Not Completed.", 3, 1);
			            	table.setModel(tm);
            			}
            			else{
            				if(claimantList.getSelectedValue().getTPDRSumm() == null){
            					btnEditPersonalInfo.setEnabled(true);
            	            	btnEditClaimHistory.setEnabled(true);
            	            	btnChangeInjuryDate.setEnabled(true);
            	            	btnEntercompletePriorWage.setEnabled(true);
            	            	btnEditWageReimbursement.setEnabled(true);
	            				btnAddWorkComp.setEnabled(true);
				            	btnAddLightDuty.setEnabled(false);
				            	btnAddTtdWork.setEnabled(true);
				            	btnAddTpdWork.setEnabled(false);
				            	btnViewClaimDetails.setEnabled(true);
				            	TableModel tm = table.getModel();
	    		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
				            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().toString(), 1, 1);
				            	tm.setValueAt("Not Completed.", 2, 1);
				            	tm.setValueAt("N/A until TPD Setup Completed.", 3, 1);
				            	table.setModel(tm);
        	            	}
            				else{
            					btnEditPersonalInfo.setEnabled(true);
            	            	btnEditClaimHistory.setEnabled(true);
            	            	btnChangeInjuryDate.setEnabled(true);
            	            	btnEntercompletePriorWage.setEnabled(true);
            	            	btnEditWageReimbursement.setEnabled(true);
            	            	btnAddWorkComp.setEnabled(true);
            	            	btnAddLightDuty.setEnabled(true);
            	            	btnAddTtdWork.setEnabled(true);
            	            	btnAddTpdWork.setEnabled(true);
            	            	btnViewClaimDetails.setEnabled(true);
            	            	TableModel tm = table.getModel();
	    		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
				            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().toString(), 1, 1);
				            	tm.setValueAt(claimantList.getSelectedValue().getTPDRSumm().toString(), 2, 1);
				            	tm.setValueAt(claimantList.getSelectedValue().getTotalString(), 3, 1);
				            	table.setModel(tm);
            				}
            			}
            		}
            	}
            } 
        }
    }
}