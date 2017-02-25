package Classes;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
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
import java.util.SortedMap;

import javax.swing.ButtonGroup;
import javax.swing.ListSelectionModel;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class WCReimbursementCalculatorMenu {

	private JFrame frmWorkersCompensationLost;
	private DefaultListModel<ReimbursementOverview> claimListModel;
	private JList<ReimbursementOverview> claimantList;
	private static WCReimbursementDAO dataAccess;
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
	private JButton btnDeleteAPaycheck;
	private JButton btnFullDutyDate;
	private JButton btnViewClaimDetails;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		 try { 
		        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
		 } catch (Exception e){ 
		    try {
				throw new Exception("Could not change LookAndFeel");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		 }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WCReimbursementCalculatorMenu window = new WCReimbursementCalculatorMenu();
					window.attachShutDownHook();
					window.frmWorkersCompensationLost.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
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
	
	//ShutDownHook to be added on application start
	public void attachShutDownHook() {
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	if(dataAccess.shutdownAllConnectionInstances()){
	        		System.out.println("Shutting Down Application... ");
	    			System.gc();
	    			System.out.println("Garbage Collected, Now Exiting...");
	    			System.exit(0);
	    		}
	    		else {
	    			System.out.println("Error closing down database connection. You may need to force quit the application.");
	    		}	        
	        }
	    });

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			WCReimbursementCalculatorMenu.dataAccess = new WCReimbursementDAO();
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
			if(dataAccess.dbConnection != null){
				dataAccess.shutdownAllConnectionInstances();
				dataAccess = null;
			}
			return;
		}
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
		/*MultiLineTableCellRenderer r = new MultiLineTableCellRenderer(){
			private static final long serialVersionUID = 1L;
			Font font = new Font("SansSerif", Font.PLAIN, 12);

		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		    	
		        if (value instanceof String[]) {
		            setListData((String[]) value);
		        }
		        table.setValueAt(value, row, column);
		        setFont(font);
		        return this;
		    }
		};
		*/
		
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"Claim History", null},
				{"Temp. Total Disability", null},
				{"Temp. Partial Disability", null},
				{"Reimbursement Overview", null},
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
			/*
			@SuppressWarnings("unchecked")
			@Override
			public void setValueAt(Object value, int row, int col)
			{
			    
				Vector<Object> column = (Vector<Object>) this.dataVector.elementAt(row);
				column.setElementAt(value, col);
				this.dataVector.setElementAt(column, row);
			    fireTableCellUpdated(row,col);
			}
			*/
		});
		
		LineWrapCellRenderer r = new LineWrapCellRenderer();
		//table.setDefaultRenderer(String[].class, r);
		
		/*
		// set column 1 font size to smaller - adapted from: http://stackoverflow.com/a/16118913/6867420
		DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			Font font = new Font("SansSerif", Font.PLAIN, 12);

		    @Override
		    public Component getTableCellRendererComponent(JTable table,
		            Object value, boolean isSelected, boolean hasFocus,
		            int row, int column) {
		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
		                row, column);
		        setFont(font);
		        return this;
		    }

		};
		*/
		//table.setDefaultRenderer(String[].class, r);
		table.getColumnModel().getColumn(1).setCellRenderer(r);
		
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
				//selectedROEnabler();			
			}
		});
		btnCreateNewClaim.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCreateNewClaim.setPreferredSize(new Dimension(180, 30));
		btnCreateNewClaim.setMinimumSize(new Dimension(130, 30));
		btnCreateNewClaim.setMaximumSize(new Dimension(130, 30));
		panel.add(btnCreateNewClaim);
		
		btnEditPersonalInfo = new JButton("Edit Claimant Info");
		btnEditPersonalInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editPersonalInfo(false);
				//selectedROEnabler();			
			}
		});
		btnEditPersonalInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		btnEditPersonalInfo.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEditPersonalInfo.setPreferredSize(new Dimension(180, 30));
		btnEditPersonalInfo.setMinimumSize(new Dimension(140, 30));
		btnEditPersonalInfo.setMaximumSize(new Dimension(140, 30));
		//btnEditPersonalInfo = setButtonTextFit(btnEditPersonalInfo, "Edit Claimant Personal Info");
		btnEditPersonalInfo.setEnabled(false);
		panel.add(btnEditPersonalInfo);
		notCreate.add(btnEditPersonalInfo);
		
		this.claimListModel = new DefaultListModel<ReimbursementOverview>();
		claimListModel.addListDataListener(new ROListDataChangeListener());
		boolean data = loadExistingData();
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(11);
		flowLayout_1.setHgap(9);
		panel_1.setBounds(0, 165, 690, 127);
		frmWorkersCompensationLost.getContentPane().add(panel_1);
		
		this.claimantList = new JList<ReimbursementOverview>(this.claimListModel);
		claimantList.setName("Claims");
		claimantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		claimantList.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
		claimantList.setBounds(207, 11, 467, 151);
		claimantList.setValueIsAdjusting(false);
		
		listSelectionModel = claimantList.getSelectionModel();
		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
		//claimantList.addListSelectionListener(new SharedListSelectionHandler()); // TODO Does this work here?
		claimantList.setSelectionModel(listSelectionModel);
		
        btnEditClaimHistory = new JButton("Begin Claim History");
		btnEditClaimHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editClaimSummary(claimantList.getSelectedValue(), true, false, false);
				//selectedROEnabler();
				CompClaim cHist = claimantList.getSelectedValue().getTTDRSumm().getClaimSummary();
				System.out.println("Post-Begin CS InjDate: "+cHist.toStringDateInjured());
				System.out.println("Post-Begin CS PWSDate: "+cHist.toStringPriorWeekStart());
				System.out.println("Post-Begin CS EPWDate: "+cHist.toStringEarliestPriorWageDate());
				System.out.println("Prior Wages: "+cHist.listPriorWages());
				
			}
		});
		btnEditClaimHistory.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEditClaimHistory.setMinimumSize(new Dimension(140, 30));
		btnEditClaimHistory.setMaximumSize(new Dimension(140, 30));
		btnEditClaimHistory.setPreferredSize(new Dimension(180, 30));
		btnEditClaimHistory.setEnabled(false);
		panel.add(btnEditClaimHistory);
		notCreate.add(btnEditClaimHistory);
		
		btnChangeInjuryDate = new JButton("Change Injury Date");
		btnChangeInjuryDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editClaimSummary(claimantList.getSelectedValue(), false, true, false);
				//selectedROEnabler();			
				}
		});
		btnChangeInjuryDate.setFont(new Font("Dialog", Font.BOLD, 12));
		btnChangeInjuryDate.setPreferredSize(new Dimension(180, 30));
		panel.add(btnChangeInjuryDate);
		btnChangeInjuryDate.setEnabled(false);
		notCreate.add(btnChangeInjuryDate);
		
		btnEntercompletePriorWage = new JButton("Enter/Complete Prior Wage Payments");
		btnEntercompletePriorWage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editClaimSummary(claimantList.getSelectedValue(), false, false, true);
				//selectedROEnabler();
				CompClaim cHist = claimantList.getSelectedValue().getTTDRSumm().getClaimSummary();
				System.out.println("Post-Begin CS InjDate: "+cHist.toStringDateInjured());
				System.out.println("Post-Begin CS PWSDate: "+cHist.toStringPriorWeekStart());
				System.out.println("Post-Begin CS EPWDate: "+cHist.toStringEarliestPriorWageDate());
				System.out.println("Prior Wages: "+cHist.listPriorWages());
			}
		});
		btnEntercompletePriorWage.setPreferredSize(new Dimension(242, 30));
		btnEntercompletePriorWage.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEntercompletePriorWage.setEnabled(false);
		panel_1.add(btnEntercompletePriorWage);
		notCreate.add(btnEntercompletePriorWage);
		
		btnEditWageReimbursement = new JButton("Start Wage Reimbursement Details");
		btnEditWageReimbursement.setEnabled(false);
		btnEditWageReimbursement.setIconTextGap(10);
		btnEditWageReimbursement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startWageReimbursementDetails();
				//selectedROEnabler();
			}
		}); 
		btnEditWageReimbursement.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEditWageReimbursement.setPreferredSize(new Dimension(228, 30));
		btnEditWageReimbursement.setMaximumSize(new Dimension(228, 30));
		btnEditWageReimbursement.setHorizontalTextPosition(SwingConstants.CENTER);
		panel_1.add(btnEditWageReimbursement);
		notCreate.add(btnEditWageReimbursement);
		
		btnAddWorkComp = new JButton("Add Work Comp Payments");
		btnAddWorkComp.setEnabled(false);
		btnAddWorkComp.setMinimumSize(new Dimension(180, 30));
		btnAddWorkComp.setMaximumSize(new Dimension(180, 30));
		btnAddWorkComp.setIconTextGap(10);
		btnAddWorkComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEditWageReimbursement.doClick();
				//selectedROEnabler();
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
				//selectedROEnabler();
			}
		});
		btnAddTtdWork.setFont(new Font("Dialog", Font.BOLD, 12));
		panel_1.add(btnAddTtdWork);
		btnAddTtdWork.setEnabled(false);
		notCreate.add(btnAddTtdWork);
		
		btnAddTpdWork = new JButton("Add TPD Work Comp Payments");
		btnAddTpdWork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTPDWCPaychecks(claimantList.getSelectedValue().getTPDRSumm());
				//selectedROEnabler();
			}
		});
		btnAddTpdWork.setFont(new Font("Dialog", Font.BOLD, 12));
		btnAddTpdWork.setEnabled(false);
		panel_1.add(btnAddTpdWork);
		notCreate.add(btnAddTpdWork);		
				
		btnAddLightDuty = new JButton("Add Light Duty Work Payment");
		btnAddLightDuty.setIconTextGap(10);
		btnAddLightDuty.setFont(new Font("Dialog", Font.BOLD, 12));
		btnAddLightDuty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//boolean balancedPayments = 
				addWorkPayments(claimantList.getSelectedValue().getTPDRSumm()); // Make sure these payments are balanced at the end of the implemented method
				//selectedROEnabler();
			}
		});
		btnAddLightDuty.setEnabled(false);
		panel_1.add(btnAddLightDuty);
		notCreate.add(btnAddLightDuty);
		
		btnDeleteAPaycheck = new JButton("Delete A Paycheck");
		btnDeleteAPaycheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!deleteAPaycheck(claimantList.getSelectedValue())){
					System.out.println("Paycheck NOT deleted.");
				}

				//selectedROEnabler();
			}
		});
		btnDeleteAPaycheck.setFont(new Font("SansSerif", Font.BOLD, 12));
    	btnDeleteAPaycheck.setEnabled(false);
		panel_1.add(btnDeleteAPaycheck);
		
		btnFullDutyDate = new JButton("Enter Full-Time Return Date");
		btnFullDutyDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFullDutyReturnDate(claimantList.getSelectedValue());
				//selectedROEnabler();
			}
		});
		btnFullDutyDate.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnFullDutyDate.setEnabled(true);
		panel_1.add(btnFullDutyDate);
		
		btnViewClaimDetails = new JButton("View Claim Details");
		btnViewClaimDetails.setIconTextGap(20);
		btnViewClaimDetails.setHorizontalTextPosition(SwingConstants.CENTER);
		btnViewClaimDetails.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnViewClaimDetails.setPreferredSize(new Dimension(140, 30));
		btnViewClaimDetails.setFont(new Font("Dialog", Font.BOLD, 12));
		btnViewClaimDetails.setEnabled(false);
		panel_1.add(btnViewClaimDetails);
		
		overviewText = new JTextPane();
		overviewText.setToolTipText("Display of currently available data from Selected Claim Lost Wage Reimbursement Overview");
		overviewText.setEditable(false);
		overviewText.setBounds(6, 291, 672, 401);
		frmWorkersCompensationLost.getContentPane().add(overviewText);
		
		frmWorkersCompensationLost.getContentPane().add(claimantList);
		if(data) claimantList.setSelectedIndex(0);
	}
	
	public StateLawCalculable selectStateLawCalculable(){
		StateLawCalculable sLC = null;
		String state = (String) JOptionPane.showInternalInputDialog(frmWorkersCompensationLost.getContentPane(), 
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
	public Calendar getCalendar(String message, String title, boolean isPayment, boolean isPPS){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		JXMonthView mV = new JXMonthView();
		mV.setTimeZone(sLC.getTimeZone());
		mV.setTraversable(true);
		JXDatePicker picker = new JXDatePicker();
		ReimbursementOverview r = claimantList.getSelectedValue();
		CompClaim cS = null;
    	cS = r.getTTDRSumm().getClaimSummary();
		java.util.Date start = new java.util.Date(cS.getDateInjured().getTimeInMillis());
		//final java.util.Date startDate = new java.util.Date(cS.getDateInjured().getTimeInMillis());
		java.util.Date end = new java.util.Date(new GregorianCalendar(sLC.getTimeZone()).getTimeInMillis());
		//final java.util.Date endDate = new java.util.Date(new GregorianCalendar(sLC.getTimeZone()).getTimeInMillis());
		if(isPayment){
			if(isPPS){
				start.setTime(start.getTime() - mWeek);
				mV.addSelectionInterval(start, end);
				picker.setMonthView(mV);
			}
			else{
				mV.addSelectionInterval(start, end);
				picker.setMonthView(mV);
			}
		}
		
		Object[] params = {message,picker};		
		GregorianCalendar selected = new GregorianCalendar(this.sLC.getTimeZone());
		int cancel = JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.OK_CANCEL_OPTION);
		if (cancel == JOptionPane.CANCEL_OPTION || cancel == JOptionPane.CLOSED_OPTION) return null;
		selected.setTime(((JXDatePicker)params[1]).getDate());
		while(selected.compareTo(new GregorianCalendar(sLC.getTimeZone())) == 0){
			String m = "You must select a date within the dates provided in order to continue." +System.getProperty("line.separator")+
					"If you do not wish to continue and would like to enter dates at a later time, click CANCEL, otherwise OK to select a date and proceed.";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, m, null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION){
				return null;
			}
			cancel = JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.OK_CANCEL_OPTION);
			if (cancel == JOptionPane.CANCEL_OPTION || cancel == JOptionPane.CLOSED_OPTION) return null;
			selected.setTime(((JXDatePicker)params[1]).getDate());
		}
		
		return sLC.normalizeCalendarTime(selected);
	}
	
	public Calendar getInjuryDateCalendar(String message, String title){
		//long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		//long mWeek = mDay * 7;
		//boolean nulled = false;
		JXMonthView mV = new JXMonthView();
		JXDatePicker picker = new JXDatePicker();
		//ReimbursementOverview r = claimantList.getSelectedValue();
		mV.setTimeZone(this.sLC.getTimeZone());
		mV.setTraversable(true);
		picker.setMonthView(mV);
		
		Object[] params = {message,picker};		
		GregorianCalendar selected = new GregorianCalendar(this.sLC.getTimeZone());
		JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.OK_CANCEL_OPTION);
		selected.setTime(((JXDatePicker)params[1]).getDate());
		/*
		while(selected.compareTo(new GregorianCalendar()) == 0){
			String m = "You must select a date within the dates provided in order to continue." +System.getProperty("line.separator")+
					"If you do not wish to continue and would like to enter dates at a later time, click CANCEL, otherwise OK to select a date and proceed.";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, m, null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION){
				return null;
			}
			JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.PLAIN_MESSAGE);
			selected.setTime(((JXDatePicker)params[1]).getDate());
		}
		*/
		//long mDayStart = selected.getTimeInMillis() - (selected.getTimeInMillis() % mDay);
		//selected.setTimeInMillis(mDayStart);
		return sLC.normalizeCalendarTime(selected);
	}
	
	public Calendar getPriorWageCalendar(String message, String title, CompClaim claimSumm, boolean ppS, boolean payDate){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		JXMonthView mV = new JXMonthView();
		mV.setTimeZone(sLC.getTimeZone());
		mV.setFlaggedDayForeground(java.awt.Color.RED);
		JXDatePicker picker = new JXDatePicker();
		java.util.Date start = new java.util.Date(claimSumm.getEarliestPriorWageDate().getTimeInMillis());
		final java.util.Date startDate = new java.util.Date(claimSumm.getEarliestPriorWageDate().getTimeInMillis());
		java.util.Date end = new java.util.Date(claimSumm.getPriorWeekStart().getTimeInMillis()+mWeek);
		final java.util.Date endDate = new java.util.Date(claimSumm.getPriorWeekStart().getTimeInMillis()+mWeek);
		if(ppS){
			start.setTime(startDate.getTime() - mWeek);
			end.setTime(endDate.getTime() + (mWeek - mDay));
			mV.addSelectionInterval(start, end);
			mV.setTraversable(true);
			mV.setFlaggedDates(startDate, endDate);
			
			picker.setMonthView(mV);
		}
		else if(payDate) {
			start.setTime(startDate.getTime());
			end.setTime(endDate.getTime() + (mWeek*4));
			mV.addSelectionInterval(start, end);
			mV.setTraversable(true);
			mV.setFlaggedDates(startDate, endDate);
			picker.setMonthView(mV);
		}
		else{
			start.setTime(startDate.getTime());
			end.setTime(endDate.getTime() + (mWeek*2));
			mV.addSelectionInterval(start, end);
			mV.setTraversable(true);
			mV.setFlaggedDates(startDate, endDate);
			picker.setMonthView(mV);
		}
		String eol = System.getProperty("line.separator");
		message = message+eol+"NOTE: Dates In red represent the first and last dates for which Prior Wages are calculated."+eol+
				"You may still select dates beyond these, but the paycheck will be appropriately trimmed.";
		Object[] params = {message,picker};		
		GregorianCalendar selected = new GregorianCalendar(this.sLC.getTimeZone());
		if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) return null;
		while (ppS && ((JXDatePicker) params[1]).getDate().compareTo(endDate) >= 0){
			params[0] = "Entered Start Date is after the last accepted date for Prior Wages."+eol+
					"Make sure date is entered correctly or enter the Paycheck under Wage Reimbursement Details/TPD Light Duty."+eol+message;
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) return null;
		}
		selected.setTime(((JXDatePicker)params[1]).getDate());
		
		while(selected.getTime().compareTo(end) >= 0){
			String m = "You must select a date within the dates provided in order to continue." +eol+
					"If you do not wish to continue and would like to add paychecks at a later time, click CANCEL, otherwise OK to select a date and proceed.";
			if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, m, null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION){
				return null;
			}
			JOptionPane.showConfirmDialog(frmWorkersCompensationLost,params,title, JOptionPane.OK_CANCEL_OPTION);
			selected.setTime(((JXDatePicker)params[1]).getDate());
		}	
		
		//long mDayStart = selected.getTimeInMillis() - (selected.getTimeInMillis() % mDay);
		//selected.setTimeInMillis(mDayStart);
		return sLC.normalizeCalendarTime(selected);
	}
	
	public Paycheck createPriorWagePaycheck(CompClaim claimSumm){
		Paycheck pc = null;
		Calendar pPS = getPriorWageCalendar("Select Pay Period Start Date", "Pay Period Start", claimSumm, true, false);
		if(pPS == null){
			return null;
		}
		Calendar pPE = getPriorWageCalendar("Select Pay Period End Date", "Pay Period End", claimSumm, false, false);
		if(pPE == null){
			return null;
		}
		Calendar pD = getPriorWageCalendar("Select Payment Date", "Payment Date", claimSumm, false, true);
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
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		WorkCompPaycheck wcPC = null;
		String eol = System.getProperty("line.separator");
		String message = "Do you know the pay period Start and End dates?"+eol+
				"If not, you will still need to input the Payment Date and Pay Recieved Date."+eol+
				"You will also not be able to immediately calculate any late payment adjustments.";
		int knownDates = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Pay Period Dates Known?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(knownDates == JOptionPane.CANCEL_OPTION) return null;
		
		Calendar pPS = null;
		Calendar pPE = null;
		if(knownDates == JOptionPane.YES_OPTION){
			pPS = getCalendar("Select Pay Period Start Date", "Pay Period Start", true, true);
			if(pPS == null){
				return null;
			}
			pPE = getCalendar("Select Pay Period End Date", "Pay Period End", true, false);
			if(pPE == null){
				return null;
			}
		}
		
		Calendar pD = getCalendar("Select Payment Date (Check Date)", "Payment Date", true, false);
		if(pD == null){
			return null;
		}
		Calendar pRD = getCalendar("Select Day That Payment Was Received (Date Check was Received)", "Payment Received Date", true, false);
		if(pRD == null){
			return null;
		}
		String grossAmnt = getPositiveBigDecimalString("Enter Gross Amount (Before Taxes and Deductions) of Paycheck", "Enter Gross Amount");
		if(grossAmnt.compareTo("") == 0){
			return null;
		}
		boolean isContested = (JOptionPane.showConfirmDialog(frmWorkersCompensationLost, 
				"Has the claim been contested at any point during this pay period and had to be resolved in court?", "Contested Pay Period?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
		Calendar contestRslvdDate = null;
		if(isContested){
			contestRslvdDate = getCalendar("Select Day That the Contest Was Resolved: ", "Contest Resolved Date", true, false);
		}
		
		GregorianCalendar epoch = new GregorianCalendar(sLC.getTimeZone());
		epoch.setTimeInMillis(mDay);
		if (knownDates == JOptionPane.NO_OPTION) wcPC = new WorkCompPaycheck(grossAmnt, pRD, epoch, epoch, isContested, sLC, pD);
		else wcPC = new WorkCompPaycheck(grossAmnt, pRD, pPS, pPE, isContested, sLC, pD);
		
		if(contestRslvdDate != null){
			wcPC.setContestResolutionDate(contestRslvdDate);
		}
		return wcPC;
	}
	
	//returns true if TPD is initialized within the selected ReimbursementOverview and inserted into the DB
	public boolean startWageReimbursementDetails(){
		String eol = System.getProperty("line.separator");
		
		ReimbursementOverview ro = claimantList.getSelectedValue();	
		ro.ttdRSumm.setCalculatedWeeklyPayment(sLC.computeCalculatedWeeklyPayment(ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment()));
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		
		if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "Is the Injured Person able to work any hours?", "Able to Work?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			if(dataAccess.selectTTDRSummary(ro.getClaimant()) == null){
				dataAccess.insertRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), new BigDecimal("0"), null);
			}
			else{
				if(ro.getTTDRSumm().getAmountNotPaid().compareTo(new BigDecimal("0")) <= 0){
					if(ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), new BigDecimal("0"),
							new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
					else dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), new BigDecimal("0"), null);
				}
				else{
					if(ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), ro.getTTDRSumm().getAmountNotPaid(),
							new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
					else dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), ro.getTTDRSumm().getAmountNotPaid(), null);
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
			claimListModel.set(claimantList.getSelectedIndex(), ro);
			return false;
		}
		else { 
			if(ro.getTPDRSumm() == null){
				ro.setTPDRSumm(new TPDReimbursementSummary(ro.getTTDRSumm()));
				ro.tpdRSumm.setAmountNotPaid(new BigDecimal("0"));
				claimListModel.set(claimantList.getSelectedIndex(), ro);
			}
			if(dataAccess.selectTPDRSummary(ro.getClaimant()) == null){
				if(ro.isFullDuty()) dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid(), 
						new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
				else dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid(), null);
			}
			else{
				if(ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid(),
						new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
				else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid(), null);
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
				claimListModel.set(claimantList.getSelectedIndex(), ro);
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
				claimListModel.set(claimantList.getSelectedIndex(), ro);
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
		if(!rs.getWCPayments().isEmpty()){
			wcPayments = rs.getWCPayments();
		}
		
  label:while(ok == JOptionPane.OK_OPTION){
			boolean worked = false;
			WorkCompPaycheck wcPC = null;
			wcPC = createWorkCompPaycheck();
			if(wcPC == null){
				ok = JOptionPane.CANCEL_OPTION;
				break label;
			}
			else{
				long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
				long mWeek = mDay * 7;
				Calendar lastFDDate = new GregorianCalendar(sLC.getTimeZone());
				GregorianCalendar epoch = new GregorianCalendar(sLC.getTimeZone());
				epoch.setTimeInMillis(mDay);
				boolean knownPP = wcPC.getPayPeriodStart().compareTo(epoch) > 0;
				lastFDDate.setTimeInMillis(ro.getTTDRSumm().getClaimSummary().getPriorWeekStart().getTimeInMillis() + ((mWeek*2)-mDay));
				if(knownPP){
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
				}
				if(knownPP && !worked){
					wcPayments = sLC.addWCPaycheck(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
					WorkCompPaycheck p = wcPayments.get(wcPayments.size()-1);
					dataAccess.insertWCPaychecks(ro.getClaimant().getID(), "TTD", p.getIsContested(),
							p.getIsLate(), p.getFullTimeHours(),new java.sql.Date(p.getPayReceivedDate().getTimeInMillis()), new java.sql.Date(p.getPaymentDate().getTimeInMillis()),
							new java.sql.Date(p.getPayPeriodStart().getTimeInMillis()), new java.sql.Date(p.getPayPeriodEnd().getTimeInMillis()), p.getGrossAmount(), p.getAmountStillOwed(),
							new java.sql.Date(p.getContestResolutionDate().getTimeInMillis()));
					System.out.println("Known Pay Period WCPC "+p.toString()+" inserted.");
					rs.setWCPayments(wcPayments);
					ro.setTTDRSumm(rs);
					claimListModel.set(claimantList.getSelectedIndex(), ro);
					
				}
				if(!knownPP){
					wcPayments = sLC.addWCPaycheckNoKnownPP(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
					WorkCompPaycheck p = wcPayments.get(wcPayments.size()-1);
					boolean inserted = dataAccess.insertWCPaychecks(ro.getClaimant().getID(), "TTD", p.getIsContested(),
							p.getIsLate(), p.getFullTimeHours(), new java.sql.Date(p.getPayReceivedDate().getTimeInMillis()), new java.sql.Date(p.getPaymentDate().getTimeInMillis()),
							new java.sql.Date(p.getPayPeriodStart().getTimeInMillis()), new java.sql.Date(p.getPayPeriodEnd().getTimeInMillis()), p.getGrossAmount(), p.getAmountStillOwed(),
							new java.sql.Date(p.getContestResolutionDate().getTimeInMillis()));
					if(inserted)System.out.println("Work Comp Paycheck "+p.toString()+" Inserted");
					else{
						try{
							throw new SQLException("WCPC Insert returns false, but does not throw prior exception.");
						} catch (SQLException e){
							e.printStackTrace();
							return false;
						}
					}
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
		
		ro.setTTDRSumm(rs);
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		if (ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(),
				new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
		else dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(), null);
		//below is obsolete. leaving for reference until final completion
		/*for(WorkCompPaycheck p : rs.wcPayments){
			dataAccess.insertWCPaychecks(ro.getClaimant().getID(), "TTD", p.getIsContested(),
					p.getIsLate(), p.getFullTimeHours(),new java.sql.Date(p.getPayReceivedDate().getTimeInMillis()), new java.sql.Date(p.getPaymentDate().getTimeInMillis()),
					new java.sql.Date(p.getPayPeriodStart().getTimeInMillis()), new java.sql.Date(p.getPayPeriodEnd().getTimeInMillis()), p.getGrossAmount(), p.getAmountStillOwed(),
					new java.sql.Date(p.getContestResolutionDate().getTimeInMillis()));
		}*/
		return true;
	}
	
	public boolean addTPDWCPaychecks(TPDReimbursementSummary rs){ //use supplementalCalculation
		String eol = System.getProperty("line.separator");
		ReimbursementOverview ro = claimantList.getSelectedValue();
		int ok = JOptionPane.OK_OPTION;
		ArrayList<WorkCompPaycheck> wcPayments = new ArrayList<WorkCompPaycheck>();
		ArrayList<TPDPaycheck> workPayments = new ArrayList<TPDPaycheck>();
		if(rs.getWCPayments().size() > 0){
			wcPayments = rs.getWCPayments();
		}
		if(rs.getReceivedWorkPayments().size() > 0){
			workPayments = rs.getReceivedWorkPayments();
		}
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		Calendar lastFDDate = new GregorianCalendar(sLC.getTimeZone());
		GregorianCalendar epoch = new GregorianCalendar(sLC.getTimeZone());
		epoch.setTimeInMillis(mDay);
		lastFDDate.setTimeInMillis(ro.getTTDRSumm().getClaimSummary().getPriorWeekStart().getTimeInMillis() + ((mWeek*2)-mDay));
		boolean knownPP = false;
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
						knownPP = wcPC.getPayPeriodStart().compareTo(epoch) > 0;
						wcPC.setFullTimeHours(true);
						if (knownPP) wcPayments = sLC.addWCPaycheck(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
						else wcPayments = sLC.addWCPaycheckNoKnownPP(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
						rs.setWCPayments(wcPayments);
						ro.setTPDRSumm(rs);
						claimListModel.set(claimantList.getSelectedIndex(), ro);
					}
					message = "Payment Added. "+eol+
							"Select YES to add another payment, or Select NO to save and exit Work Comp Payment Entry.";
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
					knownPP = wcPC.getPayPeriodStart().compareTo(epoch) > 0;
					if (knownPP) wcPayments = sLC.addWCPaycheck(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
					else wcPayments = sLC.addWCPaycheckNoKnownPP(wcPC, wcPayments, rs.getClaimSummary().getPriorWeekStart());
					WorkCompPaycheck p = wcPayments.get(wcPayments.size()-1);
					dataAccess.insertWCPaychecks(ro.getClaimant().getID(), "TPD", p.getIsContested(),
							p.getIsLate(), p.getFullTimeHours(), new java.sql.Date(p.getPayReceivedDate().getTimeInMillis()), new java.sql.Date(p.getPaymentDate().getTimeInMillis()),
							new java.sql.Date(p.getPayPeriodStart().getTimeInMillis()), new java.sql.Date(p.getPayPeriodEnd().getTimeInMillis()), p.getGrossAmount(), p.getAmountStillOwed(),
							new java.sql.Date(p.getContestResolutionDate().getTimeInMillis()));
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
				TPDPaycheck pc = createWorkPayment();
				if (pc == null) break label;
				pc.computeWCCalcPay(rs.getClaimSummary().getAvgPriorGrossWeeklyPayment());
				try {
					workPayments = sLC.addTPDWorkPaycheck(pc, workPayments, rs.getClaimSummary().getPriorWeekStart());
				} catch (Exception e) {
					e.printStackTrace();
					pc = trimWorkPayment(rs, pc);
					try {
						workPayments = sLC.addTPDWorkPaycheck(pc, workPayments, rs.getClaimSummary().getPriorWeekStart());
					} catch (Exception ex) {
						
						ex.printStackTrace();
						System.out.println("Paycheck parameters: " + pc.toString());
						Calendar fD = rs.getClaimSummary().getPriorWeekStart();
						fD.setTimeInMillis(rs.getClaimSummary().getPriorWeekStart().getTimeInMillis() + mWeek);
						SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
						formatter.setLenient(false);
						Date day = new java.sql.Date(fD.getTimeInMillis());
						System.out.println("Paycheck start day needs to be on or after the following date to be added: " +  formatter.format(day));
					}
				}
	
				ArrayList<Paycheck> pcList = dataAccess.selectPaychecks(ro.getClaimant().getID(), "WORKPAYMENT");
				boolean exists = false;
				for(Paycheck p : pcList){
					exists = (Math.abs(p.getPayPeriodEnd().getTimeInMillis() - pc.getPayPeriodEnd().getTimeInMillis()) < mDay);
				}
				if(!exists){
					dataAccess.insertPaychecks(ro.getClaimant().getID(), "WORKPAYMENT", new java.sql.Date(pc.getPaymentDate().getTimeInMillis()), new java.sql.Date(pc.getPayPeriodStart().getTimeInMillis()),
							new java.sql.Date(pc.getPayPeriodEnd().getTimeInMillis()), pc.getGrossAmount());
				}
				else{
					message = "Paycheck with same period end date already exists."+eol+
							"Select OK to Overwrite paycheck with same date, otherwise CANCEL to keep existing check with same date."+eol+ 
							"(If this is a pay adjustment check for a previously entered pay period, CANCEL and add the Gross Amount from the two checks and enter them as one check with the corresponding dates of pay period)";
					if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Overwrite Previous Pay Period Check?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
						dataAccess.updatePaychecks(ro.getClaimant().getID(), "WORKPAYMENT", new java.sql.Date(pc.getPaymentDate().getTimeInMillis()), new java.sql.Date(pc.getPayPeriodStart().getTimeInMillis()),
								new java.sql.Date(pc.getPayPeriodEnd().getTimeInMillis()), pc.getGrossAmount());
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
		if(dataAccess.selectTPDRSummary(ro.getClaimant()) == null){
			if (ro.isFullDuty()) dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(),
					new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
			else  dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(), null);
		}
		else{
			if (ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(),
					new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
			else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(), null);
		}
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		return true;
	}
	
	public boolean addWorkPayments(TPDReimbursementSummary rs){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
		String eol = System.getProperty("line.separator");
		ReimbursementOverview ro = claimantList.getSelectedValue();
		ArrayList<TPDPaycheck> workPayments = new ArrayList<TPDPaycheck>();
		if(rs.getReceivedWorkPayments().size() > 0){
			workPayments = rs.getReceivedWorkPayments();
		}
		int yes = JOptionPane.YES_OPTION;
		while(yes == JOptionPane.YES_OPTION){
			TPDPaycheck pc = createWorkPayment();
			if(pc == null){
				return false;
			}
			pc.computeWCCalcPay(rs.getClaimSummary().getAvgPriorGrossWeeklyPayment());
			boolean exists = false;
			for(Paycheck p : workPayments){
				exists = (Math.abs(p.getPayPeriodEnd().getTimeInMillis() - pc.getPayPeriodEnd().getTimeInMillis()) < mDay);
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
						ex.printStackTrace();
						System.out.println("Paycheck parameters: " + pc.toString());
						Calendar fD = rs.getClaimSummary().getPriorWeekStart();
						fD.setTimeInMillis(rs.getClaimSummary().getPriorWeekStart().getTimeInMillis() + mWeek);
						SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
						formatter.setLenient(false);
						Date day = new java.sql.Date(fD.getTimeInMillis());
						System.out.println("Paycheck start day needs to be on or after the following date to be added: " +  formatter.format(day));
					}
				}
	
				dataAccess.insertPaychecks(ro.getClaimant().getID(), "WORKPAYMENT", new java.sql.Date(pc.getPaymentDate().getTimeInMillis()), new java.sql.Date(pc.getPayPeriodStart().getTimeInMillis()),
						new java.sql.Date(pc.getPayPeriodEnd().getTimeInMillis()), pc.getGrossAmount());
			}
			else{
				String message = "Paycheck with same period end date already exists."+eol+
						"Select OK to Overwrite paycheck with same date, otherwise CANCEL to keep existing check with same date."+eol+ 
						"(If this is a pay adjustment check for a previously entered pay period, CANCEL and add the Gross Amount from the two checks and enter them as one check with the corresponding dates of pay period)";
				if(JOptionPane.showConfirmDialog(frmWorkersCompensationLost, message, "Overwrite Previous Pay Period Check?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
					dataAccess.updatePaychecks(ro.getClaimant().getID(), "WORKPAYMENT", new java.sql.Date(pc.getPaymentDate().getTimeInMillis()), new java.sql.Date(pc.getPayPeriodStart().getTimeInMillis()),
							new java.sql.Date(pc.getPayPeriodEnd().getTimeInMillis()), pc.getGrossAmount());
				}
				else{
					workPayments.remove(workPayments.size()-1);
				}
			}
			rs.updateReceivedWorkPayments(workPayments);
			ro.setTPDRSumm(rs);
			claimListModel.set(claimantList.getSelectedIndex(), ro);
			yes = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "Would you like to add another Light Duty Work Payment? (You can enter more later.)", "Add Another Payment?", JOptionPane.YES_NO_OPTION);
		}
		rs.computeAmountNotPaidAndAnyLateCompensation();
		ro.setTPDRSumm(rs);
		claimListModel.set(claimantList.getSelectedIndex(), ro);
		if(dataAccess.selectTPDRSummary(ro.getClaimant()) == null){
			if (ro.isFullDuty()) dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(),
					new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
			else dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(), null);
		}
		else{
			if (ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(),
					new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
			else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", rs.getCalculatedWeeklyPayment(), rs.getAmountNotPaid(), null);
		}
		return true;
	}	

	
	public TPDPaycheck createWorkPayment(){
		TPDPaycheck pc = null;
		Calendar pPS = getCalendar("Select Pay Period Start Date", "Pay Period Start", true, true);
		if(pPS == null){
			return null;
		}
		Calendar pPE = getCalendar("Select Pay Period End Date", "Pay Period End", true, false);
		if(pPE == null){
			return null;
		}
		Calendar pD = getCalendar("Select Payment Date", "Payment Date", true, false);
		if(pD == null){
			return null;
		}
		String grossAmnt = getPositiveBigDecimalString("Enter Gross Amount (Before Taxes and Deductions) of Paycheck", "Enter Gross Amount");
		if(grossAmnt.compareTo("") == 0){
			return null;
		}
		pc = new TPDPaycheck(grossAmnt, pD, pPS, pPE, sLC);
		return pc;
	}
	
	public TPDPaycheck trimWorkPayment(TPDReimbursementSummary rs, TPDPaycheck pc){
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
		if (s == null) return "";
		while(Double.parseDouble(s) < 1){
			s = JOptionPane.showInputDialog(frmWorkersCompensationLost, message + "(Enter only Positive numerical value)", title, JOptionPane.PLAIN_MESSAGE);
		}
		
		return s;
	}
	
	public boolean loadExistingData(){
		ArrayList<ReimbursementOverview> roList = dataAccess.selectAllReimbursementOverviews();
		if(roList.isEmpty()) return false;
		
		for(ReimbursementOverview r : roList){
			this.claimListModel.add(claimListModel.size(), r);
		}
		return true;
	}
	
	public boolean editClaimSummary(ReimbursementOverview ro, boolean create, boolean dInjOnly, boolean pcOnly){
		long mDay = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
		long mWeek = mDay * 7;
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
			Calendar dateInjured = getInjuryDateCalendar("Select the Date of Injury", "Date Injured");
			CompClaim cHist = new CompClaim(new java.sql.Date(dateInjured.getTimeInMillis()), sLC);
			dataAccess.insertClaimSummary(ro.getClaimant().getID(), new java.sql.Date(dateInjured.getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
					new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), new BigDecimal("-1"), cHist.getDaysInjured(), cHist.getWeeksInjured());
			if(ro.isFullDuty()) dataAccess.insertRSummary(ro.getClaimant().getID(), "TTD", new BigDecimal("-1"), new BigDecimal("-1"),
					new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
			else dataAccess.insertRSummary(ro.getClaimant().getID(), "TTD", new BigDecimal("-1"), new BigDecimal("-1"), null);
			ro.setTTDRSumm(new TTDReimbursementSummary());
			ro.ttdRSumm.setClaimSummary(cHist);
			claimListModel.set(claimantList.getSelectedIndex(), ro);
			int ok = JOptionPane.OK_OPTION;
			ArrayList<Paycheck> priorWages = new ArrayList<Paycheck>();
			while(!sLC.priorWagesIsComplete(priorWages) && ok == JOptionPane.OK_OPTION){
				Paycheck pc = createPriorWagePaycheck(cHist);
				if(pc == null){
					System.out.println("Paycheck was NOT added.");
					ok = JOptionPane.CANCEL_OPTION;
				}
				else{
					long pWE = cHist.getPriorWeekStart().getTimeInMillis();
					pWE += mWeek;
					if(pc.getPayPeriodEnd().getTimeInMillis() > pWE && pc.getPayPeriodStart().getTimeInMillis() < pWE){
	
						//BigDecimal gA = pc.getGrossAmount();
						/*
						TPDPaycheck tpdPC = new TPDPaycheck();
						tpdPC.setGrossAmount(gA);
						tpdPC.setPayPeriodStart(pc.getPayPeriodStart());
						tpdPC.setPayPeriodEnd(pc.getPayPeriodEnd());
						tpdPC.setPaymentDate(pc.getPaymentDate());
						tpdPC.setWCCalcPay("0");
						*/
						Paycheck[] splits = sLC.splitDateInjuredPayPeriodChecks(pc, cHist);
						dataAccess.insertPaychecks(ro.getClaimant().getID(), "PRIORWAGES", new java.sql.Date(splits[0].getPaymentDate().getTimeInMillis()), new java.sql.Date(splits[0].getPayPeriodStart().getTimeInMillis()),
							new java.sql.Date(splits[0].getPayPeriodEnd().getTimeInMillis()), splits[0].getGrossAmount());
						dataAccess.insertTPDPaychecks(ro.getClaimant().getID(), "WORKPAYMENT", new java.sql.Date(splits[1].getPaymentDate().getTimeInMillis()),
								new java.sql.Date(splits[1].getPayPeriodStart().getTimeInMillis()), new java.sql.Date(splits[1].getPayPeriodEnd().getTimeInMillis()), splits[1].getGrossAmount(),
								((TPDPaycheck) splits[1]).getWCCalcPay());
						if(dataAccess.selectTPDRSummary(ro.getClaimant()) == null) dataAccess.insertRSummary(ro.getClaimant().getID(), "TPD", new BigDecimal("-1"), new BigDecimal("-1"), null);
						ro.setTPDRSumm(new TPDReimbursementSummary());
						ro.tpdRSumm.setClaimSummary(cHist);
						ro.tpdRSumm.addPaycheck((TPDPaycheck) splits[1]);
						
						priorWages = sLC.addAndTrimToPriorWages(splits[0], priorWages, cHist);
						if (priorWages.isEmpty()){
							return false;
						}
						ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
						ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
						ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
					}
					else{
						priorWages = sLC.addAndTrimToPriorWages(pc, priorWages, cHist);
						if (priorWages.isEmpty()){
							return false;
						}
						Paycheck newPC = priorWages.get(priorWages.size() - 1);
					
						dataAccess.insertPaychecks(ro.getClaimant().getID(), "PRIORWAGES", new java.sql.Date(newPC.getPaymentDate().getTimeInMillis()), new java.sql.Date(newPC.getPayPeriodStart().getTimeInMillis()),
								new java.sql.Date(newPC.getPayPeriodEnd().getTimeInMillis()), newPC.getGrossAmount());
						ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
						ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
						ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
					}
					
					claimListModel.set(claimantList.getSelectedIndex(), ro);
				}
			}
			if(sLC.priorWagesIsComplete(priorWages)){
				ro.ttdRSumm.claimSummary.setPriorWagesAndComputeAPGWP(priorWages);
				ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
				dataAccess.updateClaimSummary(ro.getClaimant().getID(), new java.sql.Date(dateInjured.getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
						new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment(), 
						ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
				ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
				claimListModel.set(claimantList.getSelectedIndex(), ro);
				return true;
			}
			else {
				ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
				ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
				dataAccess.updateClaimSummary(ro.getClaimant().getID(), new java.sql.Date(dateInjured.getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
						new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), new BigDecimal("-1"), 
						ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
				ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
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
				int cancel = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, historyMessage, "Information Required to Continue", JOptionPane.OK_CANCEL_OPTION);
				if(cancel == JOptionPane.CANCEL_OPTION || cancel == JOptionPane.CLOSED_OPTION) return false;
				// Int selection of date obsolete with popup date picker, saving in case problems are encountered
				//int year = getPositiveInt("Enter Year of Injury: ", "Enter Year Injured");
				//int month = getPositiveInt("Enter Month of Injury: ", "Enter Month Injured");
				//int day = getPositiveInt("Enter Day of Month (i.e. 21 for January 21st) of Injury: ", "Enter Day Injured");
				Calendar dateInjured = getCalendar("Select the Date of Injury", "Date Injured", false, false);
				if (dateInjured == null) return false;
				if (dateInjured.compareTo(ro.getTTDRSumm().getClaimSummary().getDateInjured()) == 0) return false;
				
				dataAccess.deletePaychecksFrmSingleClaim(ro.getClaimant().getID(), "PRIORWAGES");
				CompClaim cHist = new CompClaim(new java.sql.Date(dateInjured.getTimeInMillis()), sLC);
				dataAccess.updateClaimSummary(ro.getClaimant().getID(), new java.sql.Date(dateInjured.getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
						new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), new BigDecimal("-1"), cHist.getDaysInjured(), cHist.getWeeksInjured());
				ro.setTTDRSumm(new TTDReimbursementSummary());
				ro.ttdRSumm.setClaimSummary(cHist);
				claimListModel.set(claimantList.getSelectedIndex(), ro);
			}
			if(!dInjOnly){
				CompClaim cHist = ro.getTTDRSumm().getClaimSummary();
				Calendar dateInjured = cHist.getDateInjured();
				int ok = JOptionPane.OK_OPTION;
				ArrayList<Paycheck> priorWages = cHist.getPriorWages();
				while(!sLC.priorWagesIsComplete(priorWages) && ok == JOptionPane.OK_OPTION){
					Paycheck pc = createPriorWagePaycheck(cHist);
					if(pc == null){
						ok = JOptionPane.CANCEL_OPTION;
					}
					else{
						priorWages = sLC.addAndTrimToPriorWages(pc, priorWages, cHist);
						if (priorWages.isEmpty()){
							return false;
						}
						pc = null;
						pc = priorWages.get(priorWages.size() - 1);

						dataAccess.insertPaychecks(ro.getClaimant().getID(), "PRIORWAGES", new java.sql.Date(pc.getPaymentDate().getTimeInMillis()), new java.sql.Date(pc.getPayPeriodStart().getTimeInMillis()),
								new java.sql.Date(pc.getPayPeriodEnd().getTimeInMillis()), pc.getGrossAmount());
						ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
						ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
						ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
						claimListModel.set(claimantList.getSelectedIndex(), ro);
					}
				}
				if(sLC.priorWagesIsComplete(priorWages)){
					ro.ttdRSumm.claimSummary.setPriorWagesAndComputeAPGWP(priorWages);
					ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
					dataAccess.updateClaimSummary(ro.getClaimant().getID(), new java.sql.Date(dateInjured.getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
							new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment(), 
							ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
					ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
					claimListModel.set(claimantList.getSelectedIndex(), ro);
					return true;
				}
				else {
					ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
					ro.ttdRSumm.claimSummary.updateDaysAndWeeksInjured();
					dataAccess.updateClaimSummary(ro.getClaimant().getID(), new java.sql.Date(dateInjured.getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
							new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), new BigDecimal("-1"), 
							ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
					ro.ttdRSumm.setClaimSummary(ro.ttdRSumm.claimSummary);
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
	
	public boolean setFullDutyReturnDate(ReimbursementOverview ro){
		boolean fullDuty = false;
		int yes = JOptionPane.showConfirmDialog(frmWorkersCompensationLost, "Has the employee returned to Full-Time Hours?", "Returned Full-Time?", JOptionPane.YES_NO_OPTION);
		if(yes != JOptionPane.YES_OPTION) return false;
		
		Calendar fDReturn = this.getCalendar("Select the date first returned to Full-Time: ", "Select Full-Time Return Date", false, false);
		if (fDReturn == null) return false;
		
		ro.setFullDutyReturnDate(fDReturn);
		
		try {
			if (ro.getTTDRSumm().getCalculatedWeeklyPayment() == null) throw new NullPointerException("Calculated Weekly Payment Returns Null.");
			if (ro.getTTDRSumm().getAmountNotPaid() == null) throw new NullPointerException("Amount Not Paid Returns Null.");
			if (ro.getFullDutyReturnDate() == null) throw new NullPointerException("FD Return Date Returns Null.");
		} catch (NullPointerException e){
			e.printStackTrace();
			return false;
		}
		try{
			if(ro.containsTTD() || ro.containsTPD()){
				if(ro.containsTTD()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TTD", ro.getTTDRSumm().getCalculatedWeeklyPayment(), ro.getTTDRSumm().getAmountNotPaid(), new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
				if(ro.containsTPD()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.getTPDRSumm().getCalculatedWeeklyPayment(), ro.getTPDRSumm().getAmountNotPaid(), new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
				fullDuty = true;
			}
			else{
				fullDuty = false;
			}
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		this.claimListModel.set(this.claimantList.getSelectedIndex(), ro);
		return fullDuty;
	}
	
	public boolean deleteAPaycheck(ReimbursementOverview ro){
		int selected = 0;
		boolean deleted = false;
		boolean tpd = this.btnAddWorkComp.isEnabled();
		if(tpd ){
			String eol = System.getProperty("line.separator");
			String message = "Delete a Paycheck from Employer or Work Comp?"+eol+
					"NOTE: Employer issued paychecks include Payments prior to Injury and TPD Work Payments.";
			String[] options = {"Employer", "Work Comp", "Cancel"};
			selected = JOptionPane.showOptionDialog(frmWorkersCompensationLost, message, "From Employer or Work Comp?", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, null);
		}
		
		long mDay = (1000*60*60*24);
		
		if(selected == 0){
			String message = "Please select the Paycheck you would like to Delete: ";
			SortedMap<Paycheck, Integer> paychecksDB = dataAccess.selectPaychecksHashMap(ro.getClaimant().getID(), "PRIORWAGES");
			SortedMap<Paycheck, Integer> workPayments = dataAccess.selectPaychecksHashMap(ro.getClaimant().getID(), "WORKPAYMENT");
			if(!workPayments.isEmpty()){
				paychecksDB.putAll(workPayments);
			}

			Paycheck toDelete = (Paycheck) JOptionPane.showInternalInputDialog(frmWorkersCompensationLost.getContentPane(), 
					message, 
					"Select Paycheck to Delete", 
					JOptionPane.PLAIN_MESSAGE, null, paychecksDB.keySet().toArray(), null);
			if (toDelete == null){
				return deleted;
			}
			deleted = dataAccess.deleteSinglePaycheck(ro.getClaimant().getID(), paychecksDB.get(toDelete));
			if (deleted){
				if(toDelete.getPayPeriodStart().compareTo(ro.getTTDRSumm().getClaimSummary().getDateInjured()) < 0){
					ArrayList<Paycheck> priorWages = ro.ttdRSumm.claimSummary.priorWages;
					for(Paycheck p : priorWages){
						if (Math.abs(p.getPaymentDate().getTimeInMillis() - toDelete.getPaymentDate().getTimeInMillis()) < mDay){
							try{
								priorWages.remove(p);
								ro.ttdRSumm.claimSummary.setPriorWages(priorWages);
								if(ro.containsTPD()) ro.tpdRSumm.claimSummary.setPriorWages(priorWages);
								CompClaim cHist = ro.getTTDRSumm().getClaimSummary();
								dataAccess.updateClaimSummary(ro.getClaimant().getID(), new java.sql.Date(cHist.getDateInjured().getTimeInMillis()), new java.sql.Date(cHist.getPriorWeekStart().getTimeInMillis()),
										new java.sql.Date(cHist.getEarliestPriorWageDate().getTimeInMillis()), ro.getTTDRSumm().getClaimSummary().getAvgPriorGrossWeeklyPayment(), 
										ro.getTTDRSumm().getClaimSummary().getDaysInjured(), ro.getTTDRSumm().getClaimSummary().getWeeksInjured());
								ro.ttdRSumm.setClaimSummary(cHist);
								if(ro.containsTPD()){
									ro.tpdRSumm.setClaimSummary(cHist);
									if (ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(),
											new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
									else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(), null);
								}
								claimListModel.set(claimantList.getSelectedIndex(), ro);
								return deleted;
							} catch (Exception e){
								e.printStackTrace();
								try{
									throw new Exception("Paycheck deleted from database but not from loaded Client side data.");
								} catch (Exception ex){
									ex.printStackTrace();
								}
							}
						}
					}
				}
				else{
					ArrayList<TPDPaycheck> workPay = ro.tpdRSumm.receivedWorkPayments;
					for(Paycheck p : workPay){
						if (Math.abs(p.getPaymentDate().getTimeInMillis() - toDelete.getPaymentDate().getTimeInMillis()) < mDay){
							try{
								workPay.remove(p);
								ro.tpdRSumm.setReceivedWorkPayments(workPay);
								if(ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(),
										new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
								else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(), null);
								claimListModel.set(claimantList.getSelectedIndex(), ro);
								return deleted;
							} catch (Exception e){
								e.printStackTrace();
								try{
									throw new Exception("Paycheck deleted from database but not from loaded Client side data.");
								} catch (Exception ex){
									ex.printStackTrace();
								}
							}
						}
					}
					try{
						throw new Exception("Paycheck deleted from database but not from loaded Client side data.");
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}
			else{
				JOptionPane.showMessageDialog(frmWorkersCompensationLost, "Paycheck could not be deleted from DataBase.", "Paycheck Not Deleted", JOptionPane.ERROR_MESSAGE);
				return deleted;
			}
		}
		else if(selected == 1){
			String message = "Please select the Work Comp Paycheck you would like to Delete: ";
			SortedMap<WorkCompPaycheck, Integer> wcPaychecksDB = dataAccess.selectWorkCompPaychecksHashMap(ro.getClaimant().getID(), "TTD");
			SortedMap<WorkCompPaycheck, Integer> wcPaychecksDB2 = dataAccess.selectWorkCompPaychecksHashMap(ro.getClaimant().getID(), "TPD");
			if (!wcPaychecksDB2.isEmpty()){
				wcPaychecksDB.putAll(wcPaychecksDB2);
			}
			if(wcPaychecksDB.isEmpty()){
				JOptionPane.showMessageDialog(frmWorkersCompensationLost, "There are no Work Comp Paychecks to delete.", "No Work Comp Paychecks", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			WorkCompPaycheck toDelete = (WorkCompPaycheck) JOptionPane.showInternalInputDialog(frmWorkersCompensationLost.getContentPane(), 
					message, 
					"Select Work Comp Paycheck to Delete", 
					JOptionPane.PLAIN_MESSAGE, null, wcPaychecksDB.keySet().toArray(), null);
			if (toDelete == null){
				return deleted;
			}
			deleted = dataAccess.deleteSingleWCPaycheck(ro.getClaimant().getID(), wcPaychecksDB.get(toDelete));
			if (deleted){
				ArrayList<WorkCompPaycheck> wcTTDPay = ro.ttdRSumm.wcPayments;
				
				for(WorkCompPaycheck p : wcTTDPay){
					if (Math.abs(p.getPaymentDate().getTimeInMillis() - toDelete.getPaymentDate().getTimeInMillis()) < mDay){
						try{
							wcTTDPay.remove(p);
							ro.ttdRSumm.setWCPayments(wcTTDPay);
							if(!ro.ttdRSumm.determineAnyLatePay()) ro.computeTTDaNPNoLatePayCalculation(); 
								
							if (ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(),
									new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
							else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(), null);
							claimListModel.set(claimantList.getSelectedIndex(), ro);
							return deleted;
						} catch (Exception e){
							e.printStackTrace();
							try{
								throw new Exception("Paycheck deleted from database but not from loaded Client side data.");
							} catch (Exception ex){
								ex.printStackTrace();
							}
						}
					}
				}
				
				ArrayList<WorkCompPaycheck> wcTPDPay = ro.tpdRSumm.wcPayments;
				
				for(WorkCompPaycheck p : wcTPDPay){
					if (Math.abs(p.getPaymentDate().getTimeInMillis() - toDelete.getPaymentDate().getTimeInMillis()) < mDay){
						try{
							wcTPDPay.remove(p);
							ro.tpdRSumm.setWCPayments(wcTPDPay);
							if(ro.isFullDuty()) dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(),
									new Date(ro.getFullDutyReturnDate().getTimeInMillis()));
							else dataAccess.updateRSummary(ro.getClaimant().getID(), "TPD", ro.tpdRSumm.getCalculatedWeeklyPayment(), ro.tpdRSumm.getAmountNotPaid(), null);
							claimListModel.set(claimantList.getSelectedIndex(), ro);
							return deleted;
						} catch (Exception e){
							e.printStackTrace();
							try{
								throw new Exception("Paycheck deleted from database but not from loaded Client side data.");
							} catch (Exception ex){
								ex.printStackTrace();
							}
						}
					}
				}
				try{
					throw new Exception("Paycheck deleted from database but not from loaded Client side data.");
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
			else{
				JOptionPane.showMessageDialog(frmWorkersCompensationLost, "Paycheck could not be deleted from DataBase.", "Paycheck Not Deleted", JOptionPane.ERROR_MESSAGE);
				return deleted;
			}
		}		
		return deleted;
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
	
	//enable JTable to display multi-line Strings
	public class MultiLineTableCellRenderer extends JList<String> implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		Font font = new Font("SansSerif", Font.PLAIN, 12);

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	
	        if (value instanceof String[]) {
	            setListData((String[]) value);
	        }
	        
	        setFont(font);
	        return this;
	    }
	}
	
	public class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
		        int row, int column) {
			String text = String.valueOf(value);
			if (value == null || text == null) text = "";
			Font font = new Font("SansSerif", Font.PLAIN, 14);
			this.setFont(font);
		    this.setText(text);
		    this.setWrapStyleWord(true);
		    this.setLineWrap(true);
		    /*
		    int fontHeight = this.getFontMetrics(this.getFont()).getHeight();
		    int textLength = this.getText().length();
		    int lines = textLength / this.getColumnWidth();
		    if (lines == 0) {
		        lines = 1;
		    }
	
		    int height = fontHeight * lines;
		    table.setRowHeight(row, height);
			*/
		    return this;
		 }

		

	}
	
	protected void selectedROEnabler(){
		boolean nulled = false;
		if (listSelectionModel == null) return;
		
        if(listSelectionModel.isSelectionEmpty()){
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
        	btnDeleteAPaycheck.setEnabled(false);
        	TableModel tm = table.getModel();
        	tm.setValueAt("No Claim Selected.", 0, 1);
        	tm.setValueAt("No Claim Selected.", 1, 1);
        	tm.setValueAt("No Claim Selected.", 2, 1);
        	tm.setValueAt("No Claim Selected.", 3, 1);
        	table.setModel(tm);
        }
        else{
        	if(claimantList.getSelectedValue().isFullDuty()) btnFullDutyDate.setText("Change Full-Time Return Date");
        	else btnFullDutyDate.setText("Enter Full Duty Return Date");
        	
        	label:for(StateLawCalculable s : (new StatesWithCalculations())){
    			if(claimantList.getSelectedValue().getClaimant().getState().compareTo(s.getStateName()) == 0){
    				sLC = s;
    				break label;
    			}
    		}
        	try{
        		nulled = !claimantList.getSelectedValue().containsTTD();
        		if(!nulled){
        			nulled = !claimantList.getSelectedValue().getTTDRSumm().containsCompClaim();
        		}
        		
        	} catch (NullPointerException ne) {
        		ne.printStackTrace();
        		nulled = true;
        	}
        	if(nulled){
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
	            	btnEditClaimHistory.setEnabled(false);
	            	btnChangeInjuryDate.setEnabled(true);
	            	btnEntercompletePriorWage.setEnabled(true);
        			btnEditWageReimbursement.setEnabled(false);
	            	btnAddWorkComp.setEnabled(false);
	            	btnAddLightDuty.setEnabled(false);
	            	btnAddTtdWork.setEnabled(false);
	            	btnAddTpdWork.setEnabled(false);
	            	btnViewClaimDetails.setEnabled(true);
	            	btnDeleteAPaycheck.setEnabled(true);
	            	TableModel tm = table.getModel();
	            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
	            	tm.setValueAt("Not Completed.", 1, 1);
	            	tm.setValueAt("Not Completed.", 2, 1);
	            	tm.setValueAt("Not Completed.", 3, 1);
	            	table.setModel(tm);
        		}
        		else{
        			if(claimantList.getSelectedValue().getTTDRSumm().getWCPayments().isEmpty() && !claimantList.getSelectedValue().containsTPD()){
        				btnEditPersonalInfo.setEnabled(true);
    	            	btnEditClaimHistory.setEnabled(true);
    	            	btnChangeInjuryDate.setEnabled(true);
    	            	btnEntercompletePriorWage.setEnabled(false);
    	            	btnEditWageReimbursement.setEnabled(true);
        				btnAddWorkComp.setEnabled(false);
		            	btnAddLightDuty.setEnabled(false);
		            	btnAddTtdWork.setEnabled(false);
		            	btnAddTpdWork.setEnabled(false);
		            	btnViewClaimDetails.setEnabled(true);
    	            	btnDeleteAPaycheck.setEnabled(true);
		            	TableModel tm = table.getModel();
		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().toTableString(claimantList.getSelectedValue().getTotalTTDCalcOwed()), 1, 1);
		            	tm.setValueAt("Not Completed.", 2, 1);
		            	tm.setValueAt("Not Completed.", 3, 1);
		            	table.setModel(tm);
        			}
        			else{
        				if(!claimantList.getSelectedValue().containsTPD()){
        					btnEditPersonalInfo.setEnabled(true);
        	            	btnEditClaimHistory.setEnabled(true);
        	            	btnChangeInjuryDate.setEnabled(true);
        	            	btnEntercompletePriorWage.setEnabled(false);
        	            	btnEditWageReimbursement.setEnabled(true);
            				btnAddWorkComp.setEnabled(true);
			            	btnAddLightDuty.setEnabled(false);
			            	btnAddTtdWork.setEnabled(true);
			            	btnAddTpdWork.setEnabled(false);
			            	btnViewClaimDetails.setEnabled(true);
	    	            	btnDeleteAPaycheck.setEnabled(true);
			            	TableModel tm = table.getModel();
    		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
			            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().toTableString(claimantList.getSelectedValue().getTotalTTDCalcOwed()), 1, 1);
			            	tm.setValueAt("Not Completed.", 2, 1);
			            	tm.setValueAt("N/A until TPD Setup Completed.", 3, 1);
			            	table.setModel(tm);
    	            	}
        				else{
        					btnEditPersonalInfo.setEnabled(true);
        	            	btnEditClaimHistory.setEnabled(true);
        	            	btnChangeInjuryDate.setEnabled(true);
        	            	btnEntercompletePriorWage.setEnabled(false);
        	            	btnEditWageReimbursement.setEnabled(true);
        	            	btnAddWorkComp.setEnabled(true);
        	            	btnAddLightDuty.setEnabled(true);
        	            	btnAddTtdWork.setEnabled(true);
        	            	btnAddTpdWork.setEnabled(true);
        	            	btnViewClaimDetails.setEnabled(true);
        	            	btnDeleteAPaycheck.setEnabled(true);
        	            	TableModel tm = table.getModel();
    		            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().getClaimSummary().toTableString(), 0, 1);
			            	tm.setValueAt(claimantList.getSelectedValue().getTTDRSumm().toTableString(claimantList.getSelectedValue().getTotalTTDCalcOwed()), 1, 1);
			            	tm.setValueAt(claimantList.getSelectedValue().getTPDRSumm().toTableString(), 2, 1);
			            	tm.setValueAt(claimantList.getSelectedValue().getTotalString(), 3, 1);
			            	table.setModel(tm);
        				}
        			}
        		}
        	}
        }
	}
	
	public class SharedListSelectionHandler implements ListSelectionListener{
		@Override
        public void valueChanged(ListSelectionEvent e) {
			selectedROEnabler();
        }		
    }
	
	public class ROListDataChangeListener implements ListDataListener{

		@Override
		public void contentsChanged(ListDataEvent e) {
			selectedROEnabler();			
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			selectedROEnabler();			
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			selectedROEnabler();			
		}	
	}
	
}
