package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.derby.tools.ij;

//adapted from http://www.oracle.com/technetwork/articles/javase/javadb-141163.html Credit: John O'Conner
public class WCReimbursementDAO {
	protected static final String dbName = "WCReimbursementDB";
	protected static final String dbDriverName = "org.apache.derby.jdbc.EmbeddedDriver";
	protected Connection dbConnection; //Close this connection upon exit by calling .close() or .shutdownAllConnectionInstances() to also close PreparedStatements first
	protected String systemDir;
	protected ClaimPreparedStatements preparedStatements; //Close these statements before exiting application by calling .shutdownAllConnectionInstances()
	protected StateLawCalculable stateLawCalculation;

	public WCReimbursementDAO() throws Exception {
		setDBSystemDir();
	    
	    loadDatabaseDriver(dbDriverName);
	    boolean success = false;
	    boolean tblsExist = false;
	    boolean tblsCreated = false;
    	label: try {
    		success = this.establishConnection();
    		if(success){
    			tblsExist = tablesExist();
    			if (!tblsExist){
    				tblsCreated = this.createTables();
    				if (tblsCreated){
    					break label;
    				}
    				else{
    					System.out.println("******Could not create tables: ln 48******");
    					this.shutdownAllConnectionInstances();
    					throw new Exception("Could not create tables");
    				}
    			}
    		}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
			if(!success){
				try {
		    		success = this.createDBAndEstablishConnection();
		    		if(success){
	    				tblsCreated = this.createTables();
	    				if (tblsCreated){
	    					break label;
	    				}
	    				else{
	    					System.out.println("******Could not create tables: ln 66******");
	    					this.shutdownAllConnectionInstances();
	    					throw new Exception("Could not create tables");
	    				}
		    		}
				} catch (SQLException se) {
					e.printStackTrace();
					se.printStackTrace();
				}
			}
		}
	    
	    if(!success){
			try {
				success = this.createDBAndEstablishConnection();
				if(success){
    				tblsCreated = this.createTables();
    				if (!tblsCreated){
    					System.out.println("******Could not create tables: ln 84******");
    					this.shutdownAllConnectionInstances();
    					throw new Exception("Could not create tables");
    				}
	    		}
				else {
					System.out.println("******Could not create/establish Connection******");
					throw new Exception("Could not create/establish Connection");
				}
	
			} catch (SQLException se) {
				se.printStackTrace();
				this.shutdownAllConnectionInstances();
				return;
			}
	    }
	}

	protected void loadDatabaseDriver(String driverName) {
	    // Load the Java DB driver.
	    try {
	        try {
				Class.forName(driverName).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
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
		String strUrl = "jdbc:derby:WCReimbursementDB";
		boolean success = false;
		
		//May use these for permission levels/multiple users at a later time
		Properties props = new Properties();
		props.put("derby.language.sequence.preallocator", 1);
		//props.put("user", username);
		//props.put("password", password);
		try {
			try{
				dbConnection = DriverManager.getConnection(strUrl, props);
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
		Properties props = new Properties();
		props.put("derby.language.sequence.preallocator", 1);
		//props.put("user", username);
		//props.put("password", password);
		try {
			try{
				dbConnection = DriverManager.getConnection(strUrl, props);
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
	

	protected boolean createTables() throws SQLException {
	    boolean bCreatedTables = false;
	    ClassLoader classLoader = getClass().getClassLoader();
	    File createSQL = new File(classLoader.getResource("CreateWCReimbursementTables.sql").getFile());
	    if (!createSQL.exists()){
	    	throw new SQLException("Could not LOCATE SQL File");
	    }
	    else if (!createSQL.isFile() || !createSQL.canExecute()){
	    	throw new SQLException("Could not RUN SQL File");
	    }
	    else{
		    try {
		        bCreatedTables = runScript(createSQL);
		    
		    } catch (Exception ex) {
		    	
		        ex.printStackTrace();
		    }
	    }
	    
	    return bCreatedTables;
	}
	
	protected boolean runScript(File scriptFile) { 
	    FileInputStream fileStream = null; 
	    try { 
	        fileStream = new FileInputStream(scriptFile); 
	        int result  = ij.runScript(this.dbConnection, fileStream, "UTF-8", System.out, "UTF-8"); 
	        System.out.println("Result code is: " + result); 
	        return (result==0); 
	    } 
	    catch (FileNotFoundException e) { 
	    	e.printStackTrace();
	        return false; 
	    } 
	    catch (UnsupportedEncodingException e) { 
	    	e.printStackTrace();
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
	
	protected boolean tablesExist() throws SQLException{
		boolean exist = false;
		DatabaseMetaData meta = null;
		try {
			meta = dbConnection.getMetaData();
		} catch (SQLException e1) {
			System.out.println("******NO DATABASE IS CONNECTED!!!*****");
			e1.printStackTrace();
			throw new SQLException("******NO DATABASE IS CONNECTED!!!*****. DAO ln 286.");
			//return exist;
		}
		ResultSet results = null;
		try {
			results = meta.getTables(null, "APP", "CLAIMANTS", 
			     new String[] {"TABLE"});
		} catch (SQLException e1) {
			System.out.println("******DatabaseMetaDataAccess Error!!!*****");
			e1.printStackTrace();
			throw new SQLException("Could not verify table name. DAO ln 295.");
			//return exist;
		}
		try {
			while (results.next()) {
				try {
					String tName = results.getString("TABLE_NAME");
		            if (tName != null && tName.equals("CLAIMANTS")) {
		                exist = true;
		                results.close();
		                return exist;
		            }
				} catch (SQLException e) {
					System.out.println("******Error getting TABLE_NAME******");
					e.printStackTrace();
					results.close();
					throw new SQLException("Could not verify table name. DAO ln 307.");
					//return exist;
				} 
			}
			results.close();
			return exist;
		} catch (SQLException e) {
			System.out.println("******Error2 getting TABLE_NAME******");
			e.printStackTrace();
			try {
				results.close();
				return exist;
			} catch (SQLException e1) {
				e1.printStackTrace();
				return exist;
			}
			
		}
	}
	
	public boolean deleteRecord(int id) {
	    boolean bDeleted = false;
	    
	    PreparedStatement stmtDeleteClaimRecord = null;
	    try {
    		stmtDeleteClaimRecord = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteClaimRecord("APP.CLAIMANTS"));
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setInt(1, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        stmtDeleteClaimRecord.close();
	       
    		stmtDeleteClaimRecord = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteClaimRecord("APP.R_SUMMARY"));
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setInt(1, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        stmtDeleteClaimRecord.close();
	       
    		stmtDeleteClaimRecord = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteClaimRecord("APP.CLAIM_SUMMARY"));
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setInt(1, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        stmtDeleteClaimRecord.close();
	        
    		stmtDeleteClaimRecord = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteClaimRecord("APP.PAYCHECKS"));
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setInt(1, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        stmtDeleteClaimRecord.close();
	       
    		stmtDeleteClaimRecord = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteClaimRecord("APP.WC_PAYCHECKS"));
	        stmtDeleteClaimRecord.clearParameters();
	        stmtDeleteClaimRecord.setInt(1, id);
	        stmtDeleteClaimRecord.executeUpdate();
	        stmtDeleteClaimRecord.close();
		        
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
		PreparedStatement stmtDeletePaychecksFrmSingleClaim = null;
		try {
			stmtDeletePaychecksFrmSingleClaim = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeletePaychecksFrmSingleClaim());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try{
			stmtDeletePaychecksFrmSingleClaim.clearParameters();
			stmtDeletePaychecksFrmSingleClaim.setInt(1, id);
			stmtDeletePaychecksFrmSingleClaim.setString(2, type);
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
	
	public boolean deleteSinglePaycheck(int id, int rowID){
		boolean deleted = false;
		PreparedStatement stmtDeleteSinglePC = null;
		try {
			stmtDeleteSinglePC = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteSinglePaycheck());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try{
			stmtDeleteSinglePC.clearParameters();
			stmtDeleteSinglePC.setInt(1, id);
			stmtDeleteSinglePC.setInt(2, rowID);
			int rows = stmtDeleteSinglePC.executeUpdate();
			deleted = true;
			System.out.println(String.valueOf(rows)+" rows successfully Deleted from Paychecks.");
		} catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	try {
	    		stmtDeleteSinglePC.close();
	    	} catch (SQLException e) {
				e.printStackTrace();
			}
	    }

		return deleted;
	}
	
	public boolean deleteSingleWCPaycheck(int id, int rowID){
		boolean deleted = false;
		PreparedStatement stmtDeleteSingleWCPC = null;
		try {
			stmtDeleteSingleWCPC = this.dbConnection.prepareStatement(this.preparedStatements.getStmtDeleteSingleWCPaycheck());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try{
			stmtDeleteSingleWCPC.clearParameters();
			stmtDeleteSingleWCPC.setInt(1, id);
			stmtDeleteSingleWCPC.setInt(2, rowID);
			stmtDeleteSingleWCPC.executeUpdate();
			deleted = true;
		} catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	try {
	    		stmtDeleteSingleWCPC.close();
	    	} catch (SQLException e) {
				e.printStackTrace();
			}
	    }

		return deleted;
	}
	
	public boolean updateClaimants(int id, String lastname, String firstname, String middlename, String workplace, String state){
		boolean updated = false;
		PreparedStatement stmtUpdateClaimants = null;
		try {
			stmtUpdateClaimants = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateClaimants());
		} catch (SQLException e1) {
			e1.printStackTrace();
			return updated;
		}
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
	
	public boolean updateRSummary(int id, String tdType, BigDecimal bdCalcWeekPay, BigDecimal bdAmntNotPaid, Date fullDutyDate){
		boolean updated = false;
		PreparedStatement stmtUpdateRSummary = null;
		try {
			stmtUpdateRSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateRSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtUpdateRSummary.clearParameters();
			stmtUpdateRSummary.setString(1, tdType);
			stmtUpdateRSummary.setBigDecimal(2, bdCalcWeekPay);
			stmtUpdateRSummary.setBigDecimal(3, bdAmntNotPaid);
			if (fullDutyDate == null) stmtUpdateRSummary.setNull(4, java.sql.Types.DATE);
			else stmtUpdateRSummary.setDate(4, fullDutyDate, tZ);
			stmtUpdateRSummary.setInt(5, id);
			stmtUpdateRSummary.setString(6, tdType);	
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
		PreparedStatement stmtUpdateClaimSummary = null;
		try {
			stmtUpdateClaimSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateClaimSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtUpdateClaimSummary.clearParameters();
			stmtUpdateClaimSummary.setDate(1, dateInj, tZ);
			stmtUpdateClaimSummary.setDate(2, priorWS, tZ);
			stmtUpdateClaimSummary.setDate(3, earliestPW, tZ);
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
		PreparedStatement stmtUpdatePaychecks = null;
		try {
			stmtUpdatePaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdatePaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtUpdatePaychecks.clearParameters();
			stmtUpdatePaychecks.setString(1, pcType);
			stmtUpdatePaychecks.setDate(2, payDate, tZ);
			stmtUpdatePaychecks.setDate(3, payStart, tZ);
			stmtUpdatePaychecks.setDate(4, payEnd, tZ);
			stmtUpdatePaychecks.setBigDecimal(5, bdGrossAmnt);
			stmtUpdatePaychecks.setInt(6, id);
			stmtUpdatePaychecks.setString(7, pcType);
			stmtUpdatePaychecks.setDate(8, payEnd, tZ);
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
	
	public boolean updateTPDPaychecks(Claimant clmnt, int id, String pcType, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt, BigDecimal bdWCCalc){
		boolean updated = false;
		PreparedStatement stmtUpdateTPDPaychecks = null;
		try {
			stmtUpdateTPDPaychecks = this.dbConnection.prepareStatement(
					"UPDATE APP.PAYCHECKS " +
				    "SET PC_TYPE = ?, " +
				    "    PAY_DATE = ?, " +
				    "    PAY_START = ?, " +
				    "    PAY_END = ?, " +
				    "    BD_GROSS_AMNT = ?, " +
				    "    BD_WC_CALC = ? " +
				    "where CLAIM_ID = ? " +
				    "AND PC_TYPE = ?" +
				    "AND ID = ?");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtUpdateTPDPaychecks.clearParameters();
			stmtUpdateTPDPaychecks.setString(1, pcType);
			stmtUpdateTPDPaychecks.setDate(2, payDate, tZ);
			stmtUpdateTPDPaychecks.setDate(3, payStart, tZ);
			stmtUpdateTPDPaychecks.setDate(4, payEnd, tZ);
			stmtUpdateTPDPaychecks.setBigDecimal(5, bdGrossAmnt);
			stmtUpdateTPDPaychecks.setBigDecimal(6, bdWCCalc);
			stmtUpdateTPDPaychecks.setInt(7, clmnt.getID());
			stmtUpdateTPDPaychecks.setString(8, pcType);
			stmtUpdateTPDPaychecks.setInt(9, id);
			stmtUpdateTPDPaychecks.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtUpdateTPDPaychecks.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean updateWCPaychecks(int id, String wcPCType, boolean isContest, boolean isLate, boolean ftHours, int stDaysToLate, 
			Date payReceived, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt, BigDecimal bdAmntOwed, Date contestRslvd){
		boolean updated = false;
		PreparedStatement stmtUpdateWCPaychecks = null;
		try {
			stmtUpdateWCPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateWCPaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtUpdateWCPaychecks.clearParameters();
			stmtUpdateWCPaychecks.setString(1, wcPCType);
			stmtUpdateWCPaychecks.setBoolean(2, isContest);
			stmtUpdateWCPaychecks.setBoolean(3, isLate);
			stmtUpdateWCPaychecks.setBoolean(4, ftHours);
			stmtUpdateWCPaychecks.setDate(5, payReceived, tZ);
			stmtUpdateWCPaychecks.setDate(6, payDate, tZ);
			stmtUpdateWCPaychecks.setDate(7, payStart, tZ);
			stmtUpdateWCPaychecks.setDate(8, payEnd, tZ);
			stmtUpdateWCPaychecks.setBigDecimal(9, bdGrossAmnt);
			stmtUpdateWCPaychecks.setBigDecimal(10, bdAmntOwed);
			stmtUpdateWCPaychecks.setInt(11, id);
			stmtUpdateWCPaychecks.setString(12, wcPCType);
			stmtUpdateWCPaychecks.setDate(13, contestRslvd, tZ);
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
		PreparedStatement stmtInsertClaimants = null;
		try {
			stmtInsertClaimants = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertClaimants(), PreparedStatement.RETURN_GENERATED_KEYS);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
	
	public boolean insertRSummary(int id, String tdType, BigDecimal bdCalcWeekPay, BigDecimal bdAmntNotPaid, Date fullDutyDate){
		boolean updated = false;
		PreparedStatement stmtInsertRSummary = null;
		try {
			stmtInsertRSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertRSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtInsertRSummary.clearParameters();
			stmtInsertRSummary.setInt(1, id);
			stmtInsertRSummary.setString(2, tdType);
			stmtInsertRSummary.setBigDecimal(3, bdCalcWeekPay);
			stmtInsertRSummary.setBigDecimal(4, bdAmntNotPaid);
			if (fullDutyDate == null) stmtInsertRSummary.setNull(5, java.sql.Types.DATE);
			else stmtInsertRSummary.setDate(5, fullDutyDate, tZ);
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
		try{
			if (dateInj == null){
				throw new NullPointerException("Date Injured is null.");
			}
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		PreparedStatement stmtInsertClaimSummary = null;
		try {
			stmtInsertClaimSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertClaimSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtInsertClaimSummary.clearParameters();
			stmtInsertClaimSummary.setInt(1, id);
			stmtInsertClaimSummary.setDate(2, dateInj, tZ);
			stmtInsertClaimSummary.setDate(3, priorWS, tZ);
			stmtInsertClaimSummary.setDate(4, earliestPW, tZ);
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
		PreparedStatement stmtInsertPaychecks = null;
		try {
			stmtInsertPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertPaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtInsertPaychecks.clearParameters();
			stmtInsertPaychecks.setInt(1, id);
			stmtInsertPaychecks.setString(2, pcType);
			stmtInsertPaychecks.setDate(3, payDate, tZ);
			stmtInsertPaychecks.setDate(4, payStart, tZ);
			stmtInsertPaychecks.setDate(5, payEnd, tZ);
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
	
	public boolean insertTPDPaychecks(int id, String pcType, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt, BigDecimal bdWCCalc){
		boolean updated = false;
		PreparedStatement stmtInsertTPDPaychecks = null;
		try {
			stmtInsertTPDPaychecks = this.dbConnection.prepareStatement(
					"INSERT INTO APP.PAYCHECKS" + 
					"(CLAIM_ID, PC_TYPE, PAY_DATE, PAY_START, PAY_END, BD_GROSS_AMNT, BD_WC_CALC) VALUES" +
					"(?,?,?,?,?,?,?)");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtInsertTPDPaychecks.clearParameters();
			stmtInsertTPDPaychecks.setInt(1, id);
			stmtInsertTPDPaychecks.setString(2, pcType);
			stmtInsertTPDPaychecks.setDate(3, payDate, tZ);
			stmtInsertTPDPaychecks.setDate(4, payStart, tZ);
			stmtInsertTPDPaychecks.setDate(5, payEnd, tZ);
			stmtInsertTPDPaychecks.setBigDecimal(6, bdGrossAmnt);
			stmtInsertTPDPaychecks.setBigDecimal(7, bdWCCalc);
			stmtInsertTPDPaychecks.executeUpdate();
			updated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmtInsertTPDPaychecks.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return updated;
	}
	
	public boolean insertWCPaychecks(int id, String wcPCType, boolean isContest, boolean isLate, boolean ftHours, 
			Date payReceived, Date payDate, Date payStart, Date payEnd, BigDecimal bdGrossAmnt, BigDecimal bdAmntOwed, Date contestRslvd){
		boolean updated = false;
		PreparedStatement stmtInsertWCPaychecks = null;
		try {
			stmtInsertWCPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertWCPaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			stmtInsertWCPaychecks.clearParameters();
			stmtInsertWCPaychecks.setInt(1, id);
			stmtInsertWCPaychecks.setString(2, wcPCType);
			stmtInsertWCPaychecks.setBoolean(3, isContest);
			stmtInsertWCPaychecks.setBoolean(4, isLate);
			stmtInsertWCPaychecks.setBoolean(5, ftHours);
			stmtInsertWCPaychecks.setDate(6, payReceived, tZ);
			stmtInsertWCPaychecks.setDate(7, payDate, tZ);
			stmtInsertWCPaychecks.setDate(8, payStart, tZ);
			stmtInsertWCPaychecks.setDate(9, payEnd, tZ);
			stmtInsertWCPaychecks.setBigDecimal(10, bdGrossAmnt);
			stmtInsertWCPaychecks.setBigDecimal(11, bdAmntOwed);
			stmtInsertWCPaychecks.setDate(12, contestRslvd, tZ);
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
	
	public Claimant selectClaimants(int id) {
		PreparedStatement stmtSelectClaimants = null;
		boolean exists = false;
		try {
			stmtSelectClaimants = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectClaimants());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		try{
			stmtSelectClaimants.clearParameters();
			stmtSelectClaimants.setInt(1, id);
			exists = stmtSelectClaimants.execute();
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
		}
		if (!exists){
			try {
				stmtSelectClaimants.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		try {
			results = stmtSelectClaimants.getResultSet();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Claimant clmnt = new Claimant();
		int row = -1;
		try{
			while(results.next()){
				row++;
	            clmnt.setID(results.getInt(1));
	            clmnt.setLastName(results.getString(2));
	            clmnt.setFirstName(results.getString(3));
	            clmnt.setMiddleName(results.getString(4));
	            clmnt.setWorkPlace(results.getString(5));
	            clmnt.setState(results.getString(6));
			}
			if (row < 0){
				results.close();
				stmtSelectClaimants.close();
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try {
				results.close();
				stmtSelectClaimants.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		
		try{
			results.close();
			stmtSelectClaimants.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return clmnt;
	}
	
	public ArrayList<Claimant> selectAllClaimants() throws SQLException{
		PreparedStatement stmtSelectAllClaimants = null;
		try {
			stmtSelectAllClaimants = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectAllClaimants());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		try{
			stmtSelectAllClaimants.clearParameters();
			if (!stmtSelectAllClaimants.execute()){
				stmtSelectAllClaimants.close();
				return null;
			}
			results = stmtSelectAllClaimants.getResultSet();
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try {
				stmtSelectAllClaimants.close();
				SQLException se = new SQLException(".execute returns False, but .executeQuery() does not return valid ResultSet");
				throw se;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
		
		ArrayList<Claimant> cList = new ArrayList<Claimant>();
		int row = -1;
		try{
			while(results.next()){
				if (results.getString(6) == null){
					throw new SQLException("No State was saved for Claimant" + results.getInt(1));
				}
				row++;
				Claimant clmnt = new Claimant();
	            clmnt.setID(results.getInt(1));
	            clmnt.setLastName(results.getString(2));
	            clmnt.setFirstName(results.getString(3));
	            clmnt.setMiddleName(results.getString(4));
	            clmnt.setWorkPlace(results.getString(5));
	            clmnt.setState(results.getString(6));
	            cList.add(clmnt);
			}
			if (row < 0){
				results.close();
				stmtSelectAllClaimants.close();
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			results.close();
			stmtSelectAllClaimants.close();
			return null;
		}
		try{
			results.close();
			stmtSelectAllClaimants.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return cList;
	}
	
	public CompClaim selectClaimSummary(Claimant clmnt){
		StateLawCalculable sLC = this.getStateLawCalculation(clmnt.getState());
		
		int id = clmnt.getID();
		PreparedStatement stmtSelectClaimSummary = null;
		try {
			stmtSelectClaimSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectClaimSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		try{
			stmtSelectClaimSummary.clearParameters();
			stmtSelectClaimSummary.setInt(1, id);
			if (!stmtSelectClaimSummary.execute()){
				stmtSelectClaimSummary.close();
				try{
					throw new NullPointerException("Null ClaimSummary returned from Query.");
				} catch(NullPointerException ne){
					ne.printStackTrace();
				}
				return null;
			}
			results = stmtSelectClaimSummary.getResultSet();
			if(results == null){
				try{
					throw new SQLException("Null ClaimSummary ResultSet for:"+clmnt.toString());
				} catch (SQLException e){
					e.printStackTrace();
					stmtSelectClaimSummary.close();
				}
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
		try{
			if(results.getFetchSize() < 1){
				throw new SQLException("No Results Returned for ClaimSummary Query. Size: "+String.valueOf(results.getFetchSize()));
			}
		} catch (SQLException e){
			e.printStackTrace();
			try {
				results.close();
				stmtSelectClaimSummary.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		CompClaim clmSm = null;
		
		//int row = -1;
		try{
			while(results.next()){
				//row++;
				System.out.println("In ClaimSumm while.next()");
				try{
					if (results.getDate(3) == null){
						throw new NullPointerException("No Value selected for DateInjured.");
					}
				} catch (NullPointerException e){
					e.printStackTrace();
				}
				clmSm = new CompClaim(results.getDate(3), sLC);
				
				try{
					if(clmSm.getEarliestPriorWageDate() == null){
						throw new Exception("ClaimSummary Null After Attempting Construction.");
					}
					Calendar pWS = new GregorianCalendar(sLC.getTimeZone());
					pWS.setTimeInMillis(results.getDate(4).getTime());
					
					Calendar ePW = new GregorianCalendar(sLC.getTimeZone());
					ePW.setTimeInMillis(results.getDate(5).getTime());
					
					if (clmSm.getPriorWeekStart().get(Calendar.DATE) != pWS.get(Calendar.DATE)
					&& clmSm.getEarliestPriorWageDate().get(Calendar.DATE) != ePW.get(Calendar.DATE)){
						String eol = System.getProperty("line.separator");
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						formatter.setLenient(false);
						formatter.setTimeZone(sLC.getTimeZone());
						java.util.Date sqlPWS = new java.util.Date(results.getDate(4).getTime());
						java.util.Date sqlEPW = new java.util.Date(results.getDate(5).getTime());
						java.util.Date sqlDI = new java.util.Date(results.getDate(3).getTime());
						throw new Exception("ClaimSummary computed dates and saved dates are not equal."+eol+
								"sqlPWSDATE: "+formatter.format(sqlPWS)+" / CCPWSDATE: "+formatter.format(clmSm.getPriorWeekStart().getTime())+
								"sqlEPWDATE: "+formatter.format(sqlEPW)+" / CCEPWDATE: "+formatter.format(clmSm.getEarliestPriorWageDate().getTime())+
								" sqlInjDATE: "+formatter.format(sqlDI)+" CCInjDATE: "+formatter.format(clmSm.getDateInjured().getTime()));
					}
					else if (clmSm.getPriorWeekStart().get(Calendar.DATE) != pWS.get(Calendar.DATE)){  //TODO : CHECK DATE COMPUTATION error is Here (check Date Injured is set same way)
						long mDay = (1000 * 60 * 60 * 24);
						String eol = System.getProperty("line.separator");
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						formatter.setLenient(false);
						formatter.setTimeZone(sLC.getTimeZone());
						java.util.Date sqlPWS = new java.util.Date(results.getDate(4).getTime());
						java.util.Date sqlDI = new java.util.Date(results.getDate(3).getTime());
						
						long diff = clmSm.getPriorWeekStart().getTimeInMillis() - results.getDate(4).getTime(); 
						long days = 0;
						if (diff >= mDay){
							days = diff/mDay;
						}
						throw new Exception("ClaimSummary computed PriorWeekStart date and saved date are not equal. Difference is: "+diff+"ms / "+days+" days. / "+eol+
								"sqlDATE: "+formatter.format(sqlPWS)+" / CCDATE: "+formatter.format(clmSm.getPriorWeekStart().getTime())+
								" sqlInjDATE: "+formatter.format(sqlDI)+" CCInjDATE: "+formatter.format(clmSm.getDateInjured().getTime()));  
					}
					else if (clmSm.getEarliestPriorWageDate().get(Calendar.DATE) != ePW.get(Calendar.DATE)){  
						long mDay = (1000 * 60 * 60 * 24);
						String eol = System.getProperty("line.separator");
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						formatter.setLenient(false);
						formatter.setTimeZone(sLC.getTimeZone());
						java.util.Date sqlEPW = new java.util.Date(results.getDate(5).getTime());
						java.util.Date sqlDI = new java.util.Date(results.getDate(3).getTime());
						
						long diff = clmSm.getEarliestPriorWageDate().getTimeInMillis() - results.getDate(5).getTime(); 
						long days = 0;
						if (diff >= mDay){
							days = diff/mDay;
						}
						throw new Exception("ClaimSummary computed EarliestPriorWage date and saved date are not equal. Difference is: "+diff+"ms / "+days+" days. / "+eol+
								"sqlDATE: "+formatter.format(sqlEPW)+" / CCDATE: "+formatter.format(clmSm.getEarliestPriorWageDate().getTime())+
								" sqlInjDATE: "+formatter.format(sqlDI)+" CCInjDATE: "+formatter.format(clmSm.getDateInjured().getTime()));  
					}
				}
				catch (Exception e){
					e.printStackTrace(); 
					try{
						results.close();
						stmtSelectClaimSummary.close();
					} catch (SQLException se){
						se.printStackTrace();
					}
					return null;
				} finally {
		            clmSm.setAvgPriorGrossWeeklyPayment(results.getBigDecimal(6));
		            clmSm.setPriorWages(selectPaychecks(id, "PRIORWAGES"));
				}
			}

			if (clmSm == null){	
				try{
					throw new Exception("Error setting CompClaim: Could not Access ResultSet data for Claimant: "+clmnt.toString());
				} catch (Exception e){
					e.printStackTrace();
					results.close();
					stmtSelectClaimSummary.close();
				}
					return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try {
				results.close();
				stmtSelectClaimSummary.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
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
		PreparedStatement stmtSelectPaychecks = null;
		try {
			stmtSelectPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectPCType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		
		try{
			stmtSelectPaychecks.clearParameters();
			stmtSelectPaychecks.setInt(1, id);
			stmtSelectPaychecks.setString(2, pcType);
			if (!stmtSelectPaychecks.execute()){
				stmtSelectPaychecks.close();
				return pcList;
			}
			results = stmtSelectPaychecks.getResultSet();

			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return pcList;
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		int row = -1;
		try{
			while(results.next()){
				row++;
				Paycheck p = new Paycheck();
				p.setPaymentDate(results.getDate(4, tZ));
				p.setPayPeriodStart(results.getDate(5, tZ));
				p.setPayPeriodEnd(results.getDate(6, tZ));
				p.setGrossAmount(results.getBigDecimal(7));
				pcList.add(p);
			}
			if (row < 0){
				results.close();
				stmtSelectPaychecks.close();
				return pcList;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectPaychecks.close();
				return pcList;
			} catch (SQLException se){
				se.printStackTrace();
				return pcList;
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
	
	public ArrayList<TPDPaycheck> selectTPDPaychecks(int id){
		ArrayList<TPDPaycheck> pcList = new ArrayList<TPDPaycheck>();
		PreparedStatement selectTPDPaychecks = null;
		try {
			selectTPDPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectPCType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		
		try{
			selectTPDPaychecks.clearParameters();
			selectTPDPaychecks.setInt(1, id);
			selectTPDPaychecks.setString(2, "WORKPAYMENT");
			if (!selectTPDPaychecks.execute()){
				selectTPDPaychecks.close();
				return pcList;
			}
			results = selectTPDPaychecks.getResultSet();

			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				selectTPDPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return pcList;
		}
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		int row = -1;
		try{
			while(results.next()){
				row++;
				TPDPaycheck p = new TPDPaycheck();
				p.setPaymentDate(results.getDate(4, tZ));
				p.setPayPeriodStart(results.getDate(5, tZ));
				p.setPayPeriodEnd(results.getDate(6, tZ));
				p.setGrossAmount(results.getBigDecimal(7));
				if(results.getBigDecimal(8) != null && results.getBigDecimal(8).compareTo(new BigDecimal("0")) > 0) p.setWCCalcPay(results.getBigDecimal(8));
				else p.setWCCalcPay("0");
				pcList.add(p);
			}
			if (row < 0){
				results.close();
				selectTPDPaychecks.close();
				return pcList;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				selectTPDPaychecks.close();
				return pcList;
			} catch (SQLException se){
				se.printStackTrace();
				return pcList;
			}
		}
		
		try{
			results.close();
			selectTPDPaychecks.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return pcList;
	}
	
	public SortedMap<Paycheck, Integer> selectPaychecksHashMap(int id, String pcType){
		SortedMap<Paycheck, Integer> pcList = new TreeMap<Paycheck, Integer>(Paycheck.PPS_COMPARATOR);
		PreparedStatement stmtSelectPaychecks = null;
		try {
			stmtSelectPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectPCType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		
		try{
			stmtSelectPaychecks.clearParameters();
			stmtSelectPaychecks.setInt(1, id);
			stmtSelectPaychecks.setString(2, pcType);
			if (!stmtSelectPaychecks.execute()){
				stmtSelectPaychecks.close();
				return pcList;
			}
			results = stmtSelectPaychecks.getResultSet();

			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return pcList;
		}
		Claimant clmnt = this.selectClaimants(id);
		StateLawCalculable sLC = this.getStateLawCalculation(clmnt.getState());
		Calendar tZ = Calendar.getInstance(sLC.getTimeZone());
		try{
			while(results.next()){
				Paycheck p = new Paycheck();
				p.setPaymentDate(results.getDate(4, tZ));
				p.setPayPeriodStart(results.getDate(5, tZ));
				p.setPayPeriodEnd(results.getDate(6, tZ));
				p.setGrossAmount(results.getBigDecimal(7));
				pcList.put(p, results.getInt(1));
			}
			
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectPaychecks.close();
				return pcList;
			} catch (SQLException se){
				se.printStackTrace();
				return pcList;
			}
		}
		
		try{
			results.close();
			stmtSelectPaychecks.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		//SortedMap<Paycheck, Integer> newPCList = sLC.sortPCHashMapByDate(pcList);
		return pcList;
	}
	
	public SortedMap<TPDPaycheck, Integer> selectTPDPaychecksHashMap(int id, String pcType){
		SortedMap<TPDPaycheck, Integer> pcList = new TreeMap<TPDPaycheck, Integer>(Paycheck.PPS_COMPARATOR);
		PreparedStatement stmtSelectTPDPaychecks = null;
		try {
			stmtSelectTPDPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectPCType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		
		try{
			stmtSelectTPDPaychecks.clearParameters();
			stmtSelectTPDPaychecks.setInt(1, id);
			stmtSelectTPDPaychecks.setString(2, pcType);
			if (!stmtSelectTPDPaychecks.execute()){
				stmtSelectTPDPaychecks.close();
				return pcList;
			}
			results = stmtSelectTPDPaychecks.getResultSet();

			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectTPDPaychecks.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return pcList;
		}
		Claimant clmnt = this.selectClaimants(id);
		StateLawCalculable sLC = this.getStateLawCalculation(clmnt.getState());
		Calendar tZ = Calendar.getInstance(sLC.getTimeZone());
		try{
			while(results.next()){
				TPDPaycheck p = new TPDPaycheck();
				p.setPaymentDate(results.getDate(4, tZ));
				p.setPayPeriodStart(results.getDate(5, tZ));
				p.setPayPeriodEnd(results.getDate(6, tZ));
				p.setGrossAmount(results.getBigDecimal(7));
				p.setWCCalcPay(results.getBigDecimal(8));
				pcList.put(p, results.getInt(1));
			}
			
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectTPDPaychecks.close();
				return pcList;
			} catch (SQLException se){
				se.printStackTrace();
				return pcList;
			}
		}
		
		try{
			results.close();
			stmtSelectTPDPaychecks.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		//SortedMap<Paycheck, Integer> newPCList = sLC.sortPCHashMapByDate(pcList);
		return pcList;
	}
	
	public ArrayList<WorkCompPaycheck> selectWorkCompPaychecks(int id, String wcpcType){
		ArrayList<WorkCompPaycheck> wcpcList = new ArrayList<WorkCompPaycheck>();
		PreparedStatement stmtSelectWCPaychecks = null;
		try {
			stmtSelectWCPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectWCPCType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		
		try{
			stmtSelectWCPaychecks.clearParameters();
			stmtSelectWCPaychecks.setInt(1, id);
			stmtSelectWCPaychecks.setString(2, wcpcType);
			if (!stmtSelectWCPaychecks.execute()){
				stmtSelectWCPaychecks.close();
				return wcpcList;
			}
			results = stmtSelectWCPaychecks.getResultSet();
			if (results == null){
				return wcpcList;
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
			return wcpcList;
		}
		
		Claimant clmnt = this.selectClaimants(id);
		Calendar tZ = Calendar.getInstance(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		int row = -1;
		try{
			while(results.next()){
				row++;
				WorkCompPaycheck wp = new WorkCompPaycheck();
				wp.setIsContested(results.getBoolean(4));
				wp.setIsLate(results.getBoolean(5));
				wp.setFullTimeHours(results.getBoolean(6));
				wp.setPayRecievedDate(results.getDate(7, tZ));
				wp.setPaymentDate(results.getDate(8, tZ));
				wp.setPayPeriodStart(results.getDate(9, tZ));
				wp.setPayPeriodEnd(results.getDate(10, tZ));
				wp.setGrossAmount(results.getBigDecimal(11));
				wp.setAmountStillOwed(results.getBigDecimal(12));
				wp.setContestResolutionDate(results.getDate(13, tZ));
				wp.setStateLawCalculation(this.stateLawCalculation);
				wcpcList.add(wp);
			}
			if (row < 0){
				results.close();
				stmtSelectWCPaychecks.close();
				return wcpcList;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectWCPaychecks.close();
				return wcpcList;
			} catch (SQLException se){
				se.printStackTrace();
				return wcpcList;
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
	
	public SortedMap<WorkCompPaycheck, Integer> selectWorkCompPaychecksHashMap(int id, String wcpcType){
		SortedMap<WorkCompPaycheck, Integer> wcpcList = new TreeMap<WorkCompPaycheck, Integer>(Paycheck.PPS_COMPARATOR);
		PreparedStatement stmtSelectWCPaychecks = null;
		try {
			stmtSelectWCPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectWCPCType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		
		try{
			stmtSelectWCPaychecks.clearParameters();
			stmtSelectWCPaychecks.setInt(1, id);
			stmtSelectWCPaychecks.setString(2, wcpcType);
			if (!stmtSelectWCPaychecks.execute()){
				stmtSelectWCPaychecks.close();
				return wcpcList;
			}
			results = stmtSelectWCPaychecks.getResultSet();
			if (results == null){
				return wcpcList;
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
			return wcpcList;
		}
		
		Claimant clmnt = this.selectClaimants(id);
		StateLawCalculable sLC = this.getStateLawCalculation(clmnt.getState());
		Calendar tZ = Calendar.getInstance(sLC.getTimeZone());
		try{
			while(results.next()){
				WorkCompPaycheck wp = new WorkCompPaycheck();
				wp.setIsContested(results.getBoolean(4));
				wp.setIsLate(results.getBoolean(5));
				wp.setFullTimeHours(results.getBoolean(6));
				wp.setPayRecievedDate(results.getDate(7, tZ));
				wp.setPaymentDate(results.getDate(8, tZ));
				wp.setPayPeriodStart(results.getDate(9, tZ));
				wp.setPayPeriodEnd(results.getDate(10, tZ));
				wp.setGrossAmount(results.getBigDecimal(11));
				wp.setAmountStillOwed(results.getBigDecimal(12));
				wp.setContestResolutionDate(results.getDate(13, tZ));
				wp.setStateLawCalculation(this.stateLawCalculation);
				wcpcList.put(wp, results.getInt(1));
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectWCPaychecks.close();
				return (SortedMap<WorkCompPaycheck, Integer>) wcpcList;
			} catch (SQLException se){
				se.printStackTrace();
				return (SortedMap<WorkCompPaycheck, Integer>) wcpcList;
			}
		}
		
		try{
			results.close();
			stmtSelectWCPaychecks.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		//SortedMap<WorkCompPaycheck, Integer> newWCPCList = sLC.sortWCPCHashMapByDate(wcpcList);
		
		return wcpcList;
	}
	
	//makes calls to selectClaimSummary, selectPaychecks, and selectWCPaychecks to return a fully formed TPDRSummary
	public TPDReimbursementSummary selectTPDRSummary(Claimant clmnt){
		int id = clmnt.getID();
		CompClaim claimSum = this.selectClaimSummary(clmnt);
		ArrayList<TPDPaycheck> workPay = this.selectTPDPaychecks(id);
		ArrayList<WorkCompPaycheck> tpdWCPay = this.selectWorkCompPaychecks(id, "TPD");
		PreparedStatement stmtSelectRSummary = null;
		try {
			stmtSelectRSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectTDType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		try{
			stmtSelectRSummary.clearParameters();
			stmtSelectRSummary.setInt(1, id);
			stmtSelectRSummary.setString(2, "TPD");
			if (!stmtSelectRSummary.execute()){
				stmtSelectRSummary.close();
				return null;
			}
			results = stmtSelectRSummary.getResultSet();
			

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
		int row = -1;
		try{
			while(results.next()){
				row++;
				tpdRSumm = new TPDReimbursementSummary(results.getBigDecimal(4), claimSum, results.getBigDecimal(5), tpdWCPay, workPay);
			}
			if (row < 0){
				results.close();
				stmtSelectRSummary.close();
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try {
				results.close();
				stmtSelectRSummary.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
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
	public TTDReimbursementSummary selectTTDRSummary(Claimant clmnt){
		int id = clmnt.getID();
		CompClaim claimSum = this.selectClaimSummary(clmnt);
		try{
			if (claimSum == null){
				throw new NullPointerException("No ClaimSummary for: "+clmnt.toString());
			}
		} catch (NullPointerException ne){
			ne.printStackTrace();
			return null;
		}
		ArrayList<WorkCompPaycheck> ttdWCPay = this.selectWorkCompPaychecks(id, "TTD");
		PreparedStatement stmtSelectRSummary = null;
		try {
			stmtSelectRSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtSelectTDType());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet results = null;
		try{
			stmtSelectRSummary.clearParameters();
			stmtSelectRSummary.setInt(1, id);
			stmtSelectRSummary.setString(2, "TTD");
			if (!stmtSelectRSummary.execute()){
				stmtSelectRSummary.close();
				try{
					throw new Exception("Could not Execute TTDRS Query.");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			results = stmtSelectRSummary.getResultSet();
			if(results == null){
				try{
					throw new SQLException("Null ClaimSummary ResultSet for:"+clmnt.toString());
				} catch (SQLException e){
					e.printStackTrace();
					stmtSelectRSummary.close();
				}
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
		
		TTDReimbursementSummary ttdRSumm = null;
		//int row = -1;
		try{
			while(results.next()){
				System.out.println("In TTDRS while.next()");
				//row++;
				ttdRSumm = new TTDReimbursementSummary(results.getBigDecimal(4), claimSum, results.getBigDecimal(5), ttdWCPay);
				System.out.println("TTDRS & Claim Summary for Claimant: "+clmnt.toString()+" Added.");

			}
			if (ttdRSumm == null && claimSum != null){
				try{
					this.insertRSummary(clmnt.getID(), "TTD", new BigDecimal("-1"), new BigDecimal("-1"), null);
				} catch (Exception e){
					e.printStackTrace();
					try{
						throw new Exception("Error setting TTDRS: Could not Access ResultSet data for Claimant: "+clmnt.toString()+" ClaimSummary added to new empty TTDRS");
					} catch (Exception ex){
						ex.printStackTrace();
					}
					results.close();
					stmtSelectRSummary.close();
				}
				ttdRSumm = new TTDReimbursementSummary();
				ttdRSumm.setClaimSummary(claimSum);
			}
			else if(ttdRSumm == null){
				try{
					throw new Exception("Error setting TTDRS: Could not Access ResultSet data for Claimant: "+clmnt.toString()+" ClaimSummary added to new empty TTDRS");
				} catch (Exception ex){
					ex.printStackTrace();
				}
				results.close();
				stmtSelectRSummary.close();
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try {
				results.close();
				stmtSelectRSummary.close();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
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
			//this.preparedStatements.shutdownAllPreparedStatements();
			this.dbConnection.close();
		} catch (SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Calendar selectFullDutyDate(Claimant clmnt){
		PreparedStatement stmtSelectFDDate = null;
		try {
			stmtSelectFDDate = this.dbConnection.prepareStatement("SELECT FD_DATE FROM APP.R_SUMMARY " + 
					"where CLAIM_ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet results = null;
		try{
			stmtSelectFDDate.clearParameters();
			stmtSelectFDDate.setInt(1, clmnt.getID());
			if (!stmtSelectFDDate.execute()){
				stmtSelectFDDate.close();
				try{
					throw new Exception("Could not Execute FD_DATE Query.");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			results = stmtSelectFDDate.getResultSet();
			if(results == null){
				try{
					throw new SQLException("Null FD_DATE ResultSet for:"+clmnt.toString());
				} catch (SQLException e){
					e.printStackTrace();
					stmtSelectFDDate.close();
				}
			}
			
			//ResultSetMetaData rsmd = results.getMetaData();
            //int numberCols = rsmd.getColumnCount();
		} catch (SQLException e){
			e.printStackTrace();
			try{
				stmtSelectFDDate.close();
			} catch (SQLException se){
				se.printStackTrace();
			}
			return null;
		}
		GregorianCalendar fdDate = new GregorianCalendar(this.getStateLawCalculation(clmnt.getState()).getTimeZone());
		try {
			if(results.next()){
				if (results.getDate("FD_DATE") != null) fdDate.setTimeInMillis(results.getDate("FD_DATE").getTime());
				else return null;
			}
			else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.getStateLawCalculation(clmnt.getState()).normalizeCalendarTime(fdDate);
	}
	
	public ReimbursementOverview selectReimbursementOverview(Claimant clmnt){
		System.out.println("Adding ReimbursementOverview for Claimant: "+clmnt.toString()+"...");

		ReimbursementOverview ro = new ReimbursementOverview();
		ro.setClaimant(clmnt);
		ro.setTTDRSumm(this.selectTTDRSummary(clmnt));
		ro.setFullDutyReturnDate(this.selectFullDutyDate(clmnt));
		if(ro.containsTTD()){
			System.out.println("TTDRS & ClaimSummar for: "+clmnt.toString()+" Added: "+ro.ttdRSumm.toString());
			System.out.println("TTDRS & ClaimSummar for: "+clmnt.toString()+" Added: "+ro.ttdRSumm.claimSummary.toString());
		}
		ro.setTPDRSumm(this.selectTPDRSummary(clmnt));
		return ro;
	}
	
	public ArrayList<ReimbursementOverview> selectAllReimbursementOverviews(){
		ArrayList<Claimant> cList = null;
		try {
			cList = this.selectAllClaimants();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		ArrayList<ReimbursementOverview> roList = new ArrayList<ReimbursementOverview>();
		if(cList != null){
			for (Claimant c : cList){
				roList.add(this.selectReimbursementOverview(c));
				System.out.println("RO for Claimant: "+c.toString()+" Added.");
			}
		}
		
		return roList;
	}
	
	public StateLawCalculable getStateLawCalculation(String state){
		for(StateLawCalculable s : (new StatesWithCalculations())){
			if(state.compareTo(s.getStateName()) == 0){
				return s;
			}
		}
		return null;
	}
}




