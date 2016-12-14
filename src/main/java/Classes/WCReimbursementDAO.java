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
import java.util.ArrayList;
import javax.swing.JOptionPane;

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
			e.printStackTrace();
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
	
	public boolean updateRSummary(int id, String tdType, BigDecimal bdCalcWeekPay, BigDecimal bdAmntNotPaid){
		boolean updated = false;
		PreparedStatement stmtUpdateRSummary = null;
		try {
			stmtUpdateRSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateRSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		PreparedStatement stmtUpdateClaimSummary = null;
		try {
			stmtUpdateClaimSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateClaimSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		PreparedStatement stmtUpdatePaychecks = null;
		try {
			stmtUpdatePaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdatePaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		PreparedStatement stmtUpdateWCPaychecks = null;
		try {
			stmtUpdateWCPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtUpdateWCPaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
	
	public boolean insertRSummary(int id, String tdType, BigDecimal bdCalcWeekPay, BigDecimal bdAmntNotPaid){
		boolean updated = false;
		PreparedStatement stmtInsertRSummary = null;
		try {
			stmtInsertRSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertRSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		PreparedStatement stmtInsertClaimSummary = null;
		try {
			stmtInsertClaimSummary = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertClaimSummary());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		PreparedStatement stmtInsertPaychecks = null;
		try {
			stmtInsertPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertPaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
		PreparedStatement stmtInsertWCPaychecks = null;
		try {
			stmtInsertWCPaychecks = this.dbConnection.prepareStatement(this.preparedStatements.getStmtInsertWCPaychecks());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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
				SQLException se = new SQLException(".execute returns False, but .executeQuery() ln 792 does not return valid ResultSet");
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
				row++;
				Claimant clmnt = new Claimant();
	            clmnt.setID(results.getInt(1));
	            clmnt.setLastName(results.getString(2));
	            clmnt.setFirstName(results.getString(3));
	            clmnt.setMiddleName(results.getString(4));
	            clmnt.setWorkPlace(results.getString(5));
	            clmnt.setState(results.getString(6));
	            cList.add(cList.size(), clmnt);
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
	
	public CompClaim selectClaimSummary(int id){
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
				return null;
			}
			results = stmtSelectClaimSummary.getResultSet();
			
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
		
		int row = -1;
		try{
			while(results.next()){
				row++;
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
			}
			if (row < 0){
				results.close();
				stmtSelectClaimSummary.close();
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
				return null;
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
			return null;
		}
		
		int row = -1;
		try{
			while(results.next()){
				row++;
				Paycheck p = new Paycheck();
				p.setPaymentDate(results.getDate(4));
				p.setPayPeriodStart(results.getDate(5));
				p.setPayPeriodEnd(results.getDate(6));
				p.setGrossAmount(results.getBigDecimal(7));
				pcList.add(p);
			}
			if (row < 0){
				results.close();
				stmtSelectPaychecks.close();
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectPaychecks.close();
				return null;
			} catch (SQLException se){
				se.printStackTrace();
				return null;
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
				return null;
			}
			results = stmtSelectWCPaychecks.getResultSet();
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
		
		int row = -1;
		try{
			while(results.next()){
				row++;
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
			if (row < 0){
				results.close();
				stmtSelectWCPaychecks.close();
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			try{
				results.close();
				stmtSelectWCPaychecks.close();
				return null;
			} catch (SQLException se){
				se.printStackTrace();
				return null;
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
	public TTDReimbursementSummary selectTTDRSummary(int id){
		CompClaim claimSum = this.selectClaimSummary(id);
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
				return null;
			}
			results = stmtSelectRSummary.getResultSet();
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
		int row = -1;
		try{
			while(results.next()){
				row++;
				ttdRSumm = new TTDReimbursementSummary(results.getBigDecimal(4), claimSum, results.getBigDecimal(5), ttdWCPay);
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
	
	public ReimbursementOverview selectReimbursementOverview(Claimant clmnt){
		ReimbursementOverview ro = new ReimbursementOverview();
		ro.setClaimant(clmnt);
		ro.setTTDRSumm(this.selectTTDRSummary(clmnt.getID()));
		ro.setTPDRSumm(this.selectTPDRSummary(clmnt.getID()));
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
		for (Claimant c : cList){
			roList.add(this.selectReimbursementOverview(c));
		}
		return roList;
	}
}




