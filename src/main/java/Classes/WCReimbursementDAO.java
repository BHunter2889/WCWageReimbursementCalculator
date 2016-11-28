package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.derby.tools.ij;

import Interfaces.StateLawCalculable;

//adapted from http://www.oracle.com/technetwork/articles/javase/javadb-141163.html Credit: John O'Conner
public class WCReimbursementDAO {
	protected static final String dbName = "WCReimbursementDB";
	protected Properties dbProperties = null;
	protected Connection dbConnection; //Close this connection upon exit by calling .close() or .shutdownAllConnectionInstances() to also close PreparedStatements first
	protected String systemDir;
	protected ClaimPreparedStatements preparedStatements; //Close these statements before exiting application by calling .shutdownAllConnectionInstances()
	protected StateLawCalculable stateLawCalculation;

	public WCReimbursementDAO() {
		setDBSystemDir();
	    this.dbProperties = loadDBProperties();
	    String driverName = this.dbProperties.getProperty("derby.driver"); 
	    loadDatabaseDriver(driverName);
	    boolean success = false;
    	label: try {
    		success = this.establishConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
			if(!success){
				try {
		    		success = this.createDBAndEstablishConnection();
					success = this.createTables(this.dbConnection);
					if(success){
						break label;
					}
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
	    
	    if(!success){
			try {
				this.createDBAndEstablishConnection();
				this.createTables(this.dbConnection);
	
			} catch (SQLException se) {
				se.printStackTrace();
			}
	    }
	}

	protected Properties loadDBProperties() {
	    InputStream dbPropInputStream = null;
	    dbPropInputStream = WCReimbursementDAO.class.getResourceAsStream("Configuration.properties");
	    Properties dbProperties = new Properties();
	    try {
	        dbProperties.load(dbPropInputStream);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return dbProperties;
	}
	
	protected void loadDatabaseDriver(String driverName) {
	    // Load the Java DB driver.
	    try {
	        Class.forName(driverName);
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	}
	
	protected void setDBSystemDir() {
	    // Decide on the db system directory: <userhome>/DerbyDB/WCReimbursementDB/
	    String userHomeDir = System.getProperty("user.home", ".");
	    String systemDir = userHomeDir + "/DerbyDB/WCReimbursementDB";

	    // Set the db system directory.
	    System.setProperty("derby.system.home", systemDir);
	    this.systemDir = systemDir;
	}
/*  May implement later for User authentication
	//check for user cfg file
	protected void checkUser() {
	    Properties props = new Properties();
	    InputStream is = null;
	 
	    // First try loading from the current directory
	    try {
	        File f = new File(this.systemDir + "/" + ); 			// Start here by figuring out where to save to (supposedly where derby launches from)
	        is = new FileInputStream( f );
	    }
	    catch ( Exception e ) { is = null; }
	 
	    try {
	        if ( is == null ) {
	            // Try loading from classpath
	            is = getClass().getResourceAsStream("server.properties");
	        }
	 
	        // Try loading properties from the file (if found)
	        props.load( is );
	    }
	    catch ( Exception e ) { }
	 
	    serverAddr = props.getProperty("ServerAddress", "192.168.0.1");
	    serverPort = new Integer(props.getProperty("ServerPort", "8080"));
	    threadCnt  = new Integer(props.getProperty("ThreadCount", "5"));
	}
*/	
	//retrieve a connection to the DB driver
	protected boolean establishConnection() throws SQLException{
		Connection dbConnection = null;
		String strUrl = "jdbc:derby:WCReibursementDB";
		boolean success = false;
		
		//May use these for permission levels/multiple users at a later time
		//Properties props = new Properties();
		//props.put("user", username);
		//props.put("password", password);
		try {
			try{
				dbConnection = DriverManager.getConnection(strUrl);
			} catch (SQLTimeoutException ste){
				ste.printStackTrace();
				return false;
			}
		    success = true;
		} catch(SQLException sqle) {
		    sqle.printStackTrace();
		    return false;
		}
		//if connection established, set connection and generate preparedStatements (will need to be closed within the created object)
		if (success){
			this.dbConnection = dbConnection;
			try {
				// Close these statements before exiting application by calling (YOURClaimPreparedStatements).shutdownAllPreparedStatements()
				this.preparedStatements = new ClaimPreparedStatements(this.dbConnection); 
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return success;
		}
		else{
			dbConnection.rollback();
			return success;
		}
	}
	
	//create the database and establish connection
	protected boolean createDBAndEstablishConnection() throws SQLException{
		Connection dbConnection = null;
		String strUrl = "jdbc:derby:WCReimbursementDB;create=true";
		boolean success = false;
		
		//May use these for permission levels/multiple users at a later time
		//Properties props = new Properties();
		//props.put("user", username);
		//props.put("password", password);
		try {
			try{
				dbConnection = DriverManager.getConnection(strUrl);
			} catch (SQLTimeoutException ste){
				ste.printStackTrace();
				return false;
			}
		    success = true;
		} catch(SQLException sqle) {
		    sqle.printStackTrace();
		    return false;
		}
		//if connection established, set connection and generate preparedStatements (will need to be closed within the created object)
		if (success){
			this.dbConnection = dbConnection;
			try {
				// Close these statements before exiting application by calling (YOURClaimPreparedStatements).shutdownAllPreparedStatements()
				this.preparedStatements = new ClaimPreparedStatements(this.dbConnection); 
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return success;
		}
		else{
			dbConnection.rollback();
			return success;
		}
	}
	

	protected boolean createTables(Connection dbConnection) {
	    boolean bCreatedTables = false;
	    File createSQL = new File("CreateWCReimbursementTables.sql");
	    try {
	        bCreatedTables = runScript(createSQL, dbConnection);
	    
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
	    return bCreatedTables;
	}
	
	protected boolean runScript(File scriptFile, Connection connection) { 
	    FileInputStream fileStream = null; 
	    try { 
	        fileStream = new FileInputStream(scriptFile); 
	        int result  = ij.runScript(connection,fileStream,"UTF-8",System.out,"UTF-8"); 
	        System.out.println("Result code is: " + result); 
	        return (result==0); 
	    } 
	    catch (FileNotFoundException e) { 
	        return false; 
	    } 
	    catch (UnsupportedEncodingException e) { 
	        return false; 
	    } 
	    finally { 
	        if(fileStream!=null) { 
	            try { 
	                fileStream.close(); 
	            } 
	            catch (IOException e) { 
	            } 
	        } 
	    } 
	}
	
	public boolean deleteRecord(int id) {
	    boolean bDeleted = false;
	    PreparedStatement stmtDeleteClaimRecord = this.preparedStatements.getStmtDeleteClaimRecord();
	    try {
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setString(1, "APP.CLAIMANTS");
	        stmtDeleteClaimRecord.setInt(2, id);
	        stmtDeleteClaimRecord.executeUpdate();
	       
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setString(1, "APP.R_SUMMARY");
	        stmtDeleteClaimRecord.setInt(2, id);
	        stmtDeleteClaimRecord.executeUpdate();
	       
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setString(1, "APP.CLAIM_SUMMARY");
	        stmtDeleteClaimRecord.setInt(2, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setString(1, "APP.PAYCHECKS");
	        stmtDeleteClaimRecord.setInt(2, id);
	        stmtDeleteClaimRecord.executeUpdate();
	       
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setString(1, "APP.WC_PAYCHECKS");
	        stmtDeleteClaimRecord.setInt(2, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        bDeleted = true;
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	try {
				stmtDeleteClaimRecord.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    
	    
	    return bDeleted;
	}
	
	public boolean deletePaychecksFrmSingleClaim(int id, String type){
		boolean deleted = false;
		PreparedStatement stmtDeletePaychecksFrmSingleClaim = this.preparedStatements.getStmtDeletePaychecksFrmSingleClaim();
		try{
			stmtDeletePaychecksFrmSingleClaim.clearParameters();
			stmtDeletePaychecksFrmSingleClaim.setString(1, "APP.PAYCHECKS");
			stmtDeletePaychecksFrmSingleClaim.setInt(2, id);
			stmtDeletePaychecksFrmSingleClaim.setString(3, type);
			stmtDeletePaychecksFrmSingleClaim.executeUpdate();
			deleted = true;
		} catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	try {
	    		stmtDeletePaychecksFrmSingleClaim.close();
	    	} catch (SQLException e) {
				e.printStackTrace();
			}
	    }

		return deleted;
	}
	
	public boolean updateClaimants(int id, String lastname, String firstname, String middlename, String workplace, String state){
		boolean updated = false;
		PreparedStatement stmtUpdateClaimants = this.preparedStatements.getStmtUpdateClaimants();
		try {
			stmtUpdateClaimants.clearParameters();
			stmtUpdateClaimants.setString(1, lastname);
			stmtUpdateClaimants.setString(2, firstname);
			stmtUpdateClaimants.setString(3, middlename);
			stmtUpdateClaimants.setString(4, workplace);
			stmtUpdateClaimants.setString(5, state);
			stmtUpdateClaimants.setInt(6, id);
			stmtUpdateClaimants.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtUpdateClaimants.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean updateRSummary(int id, String tdType, BigDecimal bdCalcWeekPay, BigDecimal bdAmntNotPaid){
		boolean updated = false;
		PreparedStatement stmtUpdateRSummary = this.preparedStatements.getStmtUpdateRSummary();
		try {
			stmtUpdateRSummary.clearParameters();
			stmtUpdateRSummary.setString(1, tdType);
			stmtUpdateRSummary.setBigDecimal(2, bdCalcWeekPay);
			stmtUpdateRSummary.setBigDecimal(3, bdAmntNotPaid);
			stmtUpdateRSummary.setInt(4, id);
			stmtUpdateRSummary.setString(5, tdType);
			stmtUpdateRSummary.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtUpdateRSummary.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean updateClaimSummary(int id, Date dateInj, Date priorWS, Date earliestPW, BigDecimal avgPGWP, long daysInj, long weeksInj){
		boolean updated = false;
		PreparedStatement stmtUpdateClaimSummary = this.preparedStatements.getStmtUpdateClaimSummary();
		try {
			stmtUpdateClaimSummary.clearParameters();
			stmtUpdateClaimSummary.setDate(1, dateInj);
			stmtUpdateClaimSummary.setDate(2, priorWS);
			stmtUpdateClaimSummary.setDate(3, earliestPW);
			stmtUpdateClaimSummary.setBigDecimal(4, avgPGWP);
			stmtUpdateClaimSummary.setLong(5, daysInj);
			stmtUpdateClaimSummary.setLong(6, weeksInj);
			stmtUpdateClaimSummary.setInt(7, id);
			stmtUpdateClaimSummary.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtUpdateClaimSummary.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean updatePaychecks(int id, String pcType, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt){
		boolean updated = false;
		PreparedStatement stmtUpdatePaychecks = this.preparedStatements.getStmtUpdatePaychecks();
		try {
			stmtUpdatePaychecks.clearParameters();
			stmtUpdatePaychecks.setString(1, pcType);
			stmtUpdatePaychecks.setDate(2, payDate);
			stmtUpdatePaychecks.setDate(3, payStart);
			stmtUpdatePaychecks.setDate(4, payEnd);
			stmtUpdatePaychecks.setBigDecimal(5, bdGrossAmnt);
			stmtUpdatePaychecks.setInt(6, id);
			stmtUpdatePaychecks.setString(7, pcType);
			stmtUpdatePaychecks.setDate(8, payEnd);
			stmtUpdatePaychecks.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtUpdatePaychecks.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean updateWCPaychecks(int id, String wcPCType, boolean isContest, boolean isLate, boolean ftHours, int stDaysToLate, 
			Date payReceived, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt, BigDecimal bdAmntOwed, Date contestRslvd){
		boolean updated = false;
		PreparedStatement stmtUpdateWCPaychecks = this.preparedStatements.getStmtUpdateWCPaychecks();
		try {
			stmtUpdateWCPaychecks.clearParameters();
			stmtUpdateWCPaychecks.setString(1, wcPCType);
			stmtUpdateWCPaychecks.setBoolean(2, isContest);
			stmtUpdateWCPaychecks.setBoolean(3, isLate);
			stmtUpdateWCPaychecks.setBoolean(4, ftHours);
			stmtUpdateWCPaychecks.setDate(5, payReceived);
			stmtUpdateWCPaychecks.setDate(6, payDate);
			stmtUpdateWCPaychecks.setDate(7, payStart);
			stmtUpdateWCPaychecks.setDate(8, payEnd);
			stmtUpdateWCPaychecks.setBigDecimal(9, bdGrossAmnt);
			stmtUpdateWCPaychecks.setBigDecimal(10, bdAmntOwed);
			stmtUpdateWCPaychecks.setInt(11, id);
			stmtUpdateWCPaychecks.setString(12, wcPCType);
			stmtUpdateWCPaychecks.setDate(13, contestRslvd);
			stmtUpdateWCPaychecks.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtUpdateWCPaychecks.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public int insertClaimants(String lastname, String firstname, String middlename, String workplace, String state){
		int id = -1;
		PreparedStatement stmtInsertClaimants = this.preparedStatements.getStmtInsertClaimants();
		try {
			stmtInsertClaimants.clearParameters();
			stmtInsertClaimants.setString(1, lastname);
			stmtInsertClaimants.setString(2, firstname);
			stmtInsertClaimants.setString(3, middlename);
			stmtInsertClaimants.setString(4, workplace);
			stmtInsertClaimants.setString(5, state);
			stmtInsertClaimants.executeUpdate();
			ResultSet results = stmtInsertClaimants.getGeneratedKeys();
	        if (results.next()) {
	            id = results.getInt(1);
	        }
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtInsertClaimants.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return id;
	}
	
	public boolean insertRSummary(int id, String tdType, BigDecimal bdCalcWeekPay, BigDecimal bdAmntNotPaid){
		boolean updated = false;
		PreparedStatement stmtInsertRSummary = this.preparedStatements.getStmtInsertRSummary();
		try {
			stmtInsertRSummary.clearParameters();
			stmtInsertRSummary.setInt(1, id);
			stmtInsertRSummary.setString(2, tdType);
			stmtInsertRSummary.setBigDecimal(3, bdCalcWeekPay);
			stmtInsertRSummary.setBigDecimal(4, bdAmntNotPaid);
			stmtInsertRSummary.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtInsertRSummary.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean insertClaimSummary(int id, Date dateInj, Date priorWS, Date earliestPW, BigDecimal avgPGWP, long daysInj, long weeksInj){
		boolean updated = false;
		PreparedStatement stmtInsertClaimSummary = this.preparedStatements.getStmtInsertClaimSummary();
		try {
			stmtInsertClaimSummary.clearParameters();
			stmtInsertClaimSummary.setInt(1, id);
			stmtInsertClaimSummary.setDate(2, dateInj);
			stmtInsertClaimSummary.setDate(3, priorWS);
			stmtInsertClaimSummary.setDate(4, earliestPW);
			stmtInsertClaimSummary.setBigDecimal(5, avgPGWP);
			stmtInsertClaimSummary.setLong(6, daysInj);
			stmtInsertClaimSummary.setLong(7, weeksInj);
			stmtInsertClaimSummary.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtInsertClaimSummary.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean insertPaychecks(int id, String pcType, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt){
		boolean updated = false;
		PreparedStatement stmtInsertPaychecks = this.preparedStatements.getStmtInsertPaychecks();
		try {
			stmtInsertPaychecks.clearParameters();
			stmtInsertPaychecks.setInt(1, id);
			stmtInsertPaychecks.setString(2, pcType);
			stmtInsertPaychecks.setDate(3, payDate);
			stmtInsertPaychecks.setDate(4, payStart);
			stmtInsertPaychecks.setDate(5, payEnd);
			stmtInsertPaychecks.setBigDecimal(6, bdGrossAmnt);
			stmtInsertPaychecks.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtInsertPaychecks.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean insertWCPaychecks(int id, String wcPCType, boolean isContest, boolean isLate, boolean ftHours, 
			Date payReceived, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt, BigDecimal bdAmntOwed, Date contestRslvd){
		boolean updated = false;
		PreparedStatement stmtInsertWCPaychecks = this.preparedStatements.getStmtInsertWCPaychecks();
		try {
			stmtInsertWCPaychecks.clearParameters();
			stmtInsertWCPaychecks.setInt(1, id);
			stmtInsertWCPaychecks.setString(2, wcPCType);
			stmtInsertWCPaychecks.setBoolean(3, isContest);
			stmtInsertWCPaychecks.setBoolean(4, isLate);
			stmtInsertWCPaychecks.setBoolean(5, ftHours);
			stmtInsertWCPaychecks.setDate(6, payReceived);
			stmtInsertWCPaychecks.setDate(7, payDate);
			stmtInsertWCPaychecks.setDate(8, payStart);
			stmtInsertWCPaychecks.setDate(9, payEnd);
			stmtInsertWCPaychecks.setBigDecimal(10, bdGrossAmnt);
			stmtInsertWCPaychecks.setBigDecimal(11, bdAmntOwed);
			stmtInsertWCPaychecks.setDate(12, contestRslvd);
			stmtInsertWCPaychecks.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtInsertWCPaychecks.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public Claimant selectClaimants(int id){
		PreparedStatement stmtSelectClaimants = this.preparedStatements.getStmtSelectClaimants();
		ResultSet results = null;
		try{
			stmtSelectClaimants.clearParameters();
			stmtSelectClaimants.setInt(1, id);
			results = stmtSelectClaimants.executeQuery();
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		Claimant clmnt = new Claimant();
		try{
            clmnt.setID(results.getInt(1));
            clmnt.setLastName(results.getString(2));
            clmnt.setFirstName(results.getString(3));
            clmnt.setMiddleName(results.getString(4));
            clmnt.setWorkPlace(results.getString(5));
            clmnt.setState(results.getString(6));
		} catch (SQLException e){
			e.printStackTrace();
		}
		try{
			results.close();
			stmtSelectClaimants.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return clmnt;
	}
	
	public ArrayList<Claimant> selectAllClaimants(){
		PreparedStatement stmtSelectClaimants = this.preparedStatements.getStmtSelectClaimants();
		ResultSet results = null;
		try{
			stmtSelectClaimants.clearParameters();
			results = stmtSelectClaimants.executeQuery();
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		ArrayList<Claimant> cList = new ArrayList<Claimant>();
		try{
			while(results.next()){
				Claimant clmnt = new Claimant();
	            clmnt.setID(results.getInt(1));
	            clmnt.setLastName(results.getString(2));
	            clmnt.setFirstName(results.getString(3));
	            clmnt.setMiddleName(results.getString(4));
	            clmnt.setWorkPlace(results.getString(5));
	            clmnt.setState(results.getString(6));
	            cList.add(cList.size(), clmnt);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		try{
			results.close();
			stmtSelectClaimants.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return cList;
	}
	
	public CompClaim selectClaimSummary(int id){
		PreparedStatement stmtSelectClaimSummary = this.preparedStatements.getStmtSelectClaimSummary();
		ResultSet results = null;
		try{
			stmtSelectClaimSummary.clearParameters();
			stmtSelectClaimSummary.setInt(1, id);
			if((results = stmtSelectClaimSummary.executeQuery()) == null){
				try{
					stmtSelectClaimSummary.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
				return null;
			}
			
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectClaimSummary.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return null;
		}
		
		CompClaim clmSm = null;
		try{
			clmSm = new CompClaim(results.getDate(3),  this.stateLawCalculation);
			try{
				if (clmSm.getPriorWeekStart().getTime().compareTo(results.getDate(4)) != 0 || clmSm.getEarliestPriorWageDate().getTime().compareTo(results.getDate(5)) != 0){
					throw new Exception("ClaimSummary computed dates and saved dates are not equal.");
				}
			}
			catch (Exception e){
				JOptionPane.showMessageDialog(null, "Error: "+ e.getCause().getMessage());
				try{
					results.close();
					stmtSelectClaimSummary.close();
				} catch (SQLException se){
					se.printStackTrace();
				}
			} finally {
	            clmSm.setAvgPriorGrossWeeklyPayment(results.getBigDecimal(6));
	            clmSm.setPriorWages(selectPaychecks(id, "PRIORWAGES"));
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectClaimSummary.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
		}
		try{
			results.close();
			stmtSelectClaimSummary.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return clmSm;
	}
	
	public ArrayList<Paycheck> selectPaychecks(int id, String pcType){
		ArrayList<Paycheck> pcList = new ArrayList<Paycheck>();
		PreparedStatement stmtSelectPaychecks = this.preparedStatements.getStmtSelectPCType();
		ResultSet results = null;
		
		try{
			stmtSelectPaychecks.clearParameters();
			stmtSelectPaychecks.setInt(1, id);
			stmtSelectPaychecks.setString(2, pcType);
			if((results = stmtSelectPaychecks.executeQuery()) == null){
				try{
					stmtSelectPaychecks.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
				return null;
			}	
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return null;
		}
		
		try{
			while(results.next()){
				Paycheck p = new Paycheck();
				p.setPaymentDate(results.getDate(4));
				p.setPayPeriodStart(results.getDate(5));
				p.setPayPeriodEnd(results.getDate(6));
				p.setGrossAmount(results.getBigDecimal(7));
				pcList.add(p);
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
		}
		
		try{
			results.close();
			stmtSelectPaychecks.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return pcList;
	}
	
	public ArrayList<WorkCompPaycheck> selectWorkCompPaychecks(int id, String wcpcType){
		ArrayList<WorkCompPaycheck> wcpcList = new ArrayList<WorkCompPaycheck>();
		PreparedStatement stmtSelectWCPaychecks = this.preparedStatements.getStmtSelectWCPCType();
		ResultSet results = null;
		
		try{
			stmtSelectWCPaychecks.clearParameters();
			stmtSelectWCPaychecks.setInt(1, id);
			stmtSelectWCPaychecks.setString(2, wcpcType);
			if((results = stmtSelectWCPaychecks.executeQuery()) == null){
				try{
					stmtSelectWCPaychecks.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
				return null;
			}
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectWCPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return null;
		}
		
		try{
			while(results.next()){
				WorkCompPaycheck wp = new WorkCompPaycheck();
				wp.setIsContested(results.getBoolean(4));
				wp.setIsLate(results.getBoolean(5));
				wp.setFullTimeHours(results.getBoolean(6));
				wp.setPayRecievedDate(results.getDate(7));
				wp.setPaymentDate(results.getDate(8));
				wp.setPayPeriodStart(results.getDate(9));
				wp.setPayPeriodEnd(results.getDate(10));
				wp.setGrossAmount(results.getBigDecimal(11));
				wp.setAmountStillOwed(results.getBigDecimal(12));
				wp.setContestResolutionDate(results.getDate(13));
				wp.setStateLawCalculation(this.stateLawCalculation);
				wcpcList.add(wp);
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectWCPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
		}
		
		try{
			results.close();
			stmtSelectWCPaychecks.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return wcpcList;
	}
	
	//makes calls to selectClaimSummary, selectPaychecks, and selectWCPaychecks to return a fully formed TPDRSummary
	public TPDReimbursementSummary selectTPDRSummary(int id){
		CompClaim claimSum = this.selectClaimSummary(id);
		ArrayList<Paycheck> workPay = this.selectPaychecks(id, "WORKPAYMENTS");
		ArrayList<WorkCompPaycheck> tpdWCPay = this.selectWorkCompPaychecks(id, "TPD");
		PreparedStatement stmtSelectRSummary = this.preparedStatements.getStmtSelectTDType();
		ResultSet results = null;
		try{
			stmtSelectRSummary.clearParameters();
			stmtSelectRSummary.setInt(1, id);
			stmtSelectRSummary.setString(2, "TPD");
			if((results = stmtSelectRSummary.executeQuery()) == null){
				try{
					stmtSelectRSummary.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
				return null;
			}
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectRSummary.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return null;
		}
		
		TPDReimbursementSummary tpdRSumm = null;
		try{
			tpdRSumm = new TPDReimbursementSummary(results.getBigDecimal(4), claimSum, results.getBigDecimal(5), tpdWCPay, workPay);
		} catch (SQLException e){
			try{
				results.close();
				stmtSelectRSummary.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			e.printStackTrace();
		}
		try{
			results.close();
			stmtSelectRSummary.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return tpdRSumm;
	}
	
	//makes calls to selectClaimSummary and selectWCPaychecks to return a fully formed TTDRSummary
	public TTDReimbursementSummary selectTTDRSummary(int id){
		CompClaim claimSum = this.selectClaimSummary(id);
		ArrayList<WorkCompPaycheck> ttdWCPay = this.selectWorkCompPaychecks(id, "TTD");
		PreparedStatement stmtSelectRSummary = this.preparedStatements.getStmtSelectTDType();
		ResultSet results = null;
		try{
			stmtSelectRSummary.clearParameters();
			stmtSelectRSummary.setInt(1, id);
			stmtSelectRSummary.setString(2, "TTD");
			if((results = stmtSelectRSummary.executeQuery()) == null){
				try{
					stmtSelectRSummary.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
				return null;
			}
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			try{
				stmtSelectRSummary.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return null;
		}
		
		TTDReimbursementSummary ttdRSumm = null;
		try{
			ttdRSumm = new TTDReimbursementSummary(results.getBigDecimal(4), claimSum, results.getBigDecimal(5), ttdWCPay);
		} catch (SQLException e){
			try{
				results.close();
				stmtSelectRSummary.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			e.printStackTrace();

		}
		try{
			results.close();
			stmtSelectRSummary.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return ttdRSumm;
	}
	
	public boolean shutdownAllConnectionInstances(){
		try {
			this.preparedStatements.shutdownAllPreparedStatements();
			this.dbConnection.close();
		} catch (SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ReimbursementOverview selectReimbursementOverview(Claimant clmnt){
		ReimbursementOverview ro = new ReimbursementOverview();
		ro.setClaimant(clmnt);
		ro.setTTDRSumm(this.selectTTDRSummary(clmnt.getID()));
		ro.setTPDRSumm(this.selectTPDRSummary(clmnt.getID()));
		return ro;
	}
	
	public ArrayList<ReimbursementOverview> selectAllReimbursementOverviews(){
		ArrayList<Claimant> cList = this.selectAllClaimants();
		ArrayList<ReimbursementOverview> roList = new ArrayList<ReimbursementOverview>();
		for (Claimant c : cList){
			roList.add(this.selectReimbursementOverview(c));
		}
		return roList;
	}
}




