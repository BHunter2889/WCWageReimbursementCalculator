package Classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClaimPreparedStatements {
	
	protected Connection dbConnection;
	protected PreparedStatement stmtDeleteClaimRecord;
	protected PreparedStatement stmtDeletePaychecksFrmSingleClaim;
	protected PreparedStatement stmtUpdateClaimants;
	protected PreparedStatement stmtUpdateRSummary;
	protected PreparedStatement stmtUpdateClaimSummary;
	protected PreparedStatement stmtUpdatePaychecks;
	protected PreparedStatement stmtUpdateWCPaychecks;
	protected PreparedStatement stmtInsertClaimants;
	protected PreparedStatement stmtInsertRSummary;
	protected PreparedStatement stmtInsertClaimSummary;
	protected PreparedStatement stmtInsertPaychecks;
	protected PreparedStatement stmtInsertWCPaychecks;
	protected PreparedStatement stmtSelectClaimants;
	protected PreparedStatement stmtSelectClaimSummary;
	protected PreparedStatement stmtSelectTDType;
	protected PreparedStatement stmtSelectPCType;
	protected PreparedStatement stmtSelectWCPCType;
	protected PreparedStatement stmtSelectAllClaimants;

	public ClaimPreparedStatements(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		setStmtDeleteClaimRecord();
		setStmtDeletePaychecksFrmSingleClaim();
		setStmtUpdateClaimants();
		setStmtUpdateRSummary();
		setStmtUpdateClaimSummary();
		setStmtUpdatePaychecks();
		setStmtUpdateWCPaychecks();
		setStmtInsertClaimants();
		setStmtInsertRSummary();
		setStmtInsertClaimSummary();
		setStmtInsertPaychecks();
		setStmtInsertWCPaychecks();
		setStmtSelectClaimants();
		setStmtSelectClaimSummary();
		setStmtSelectTDType();
		setStmtSelectPCType();
		setStmtSelectWCPCType();
		setStmtSelectAllClaimants();
	}
	
	public void setStmtDeleteClaimRecord(){
		try {
			this.stmtDeleteClaimRecord = this.dbConnection.prepareStatement(
			        "DELETE FROM ? " +
			        "WHERE ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtDeletePaychecksFrmSingleClaim(){
		try {
			this.stmtDeletePaychecksFrmSingleClaim = this.dbConnection.prepareStatement(
			        "DELETE FROM ? " +
			        "WHERE ID = ? AND PC_TYPE = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtUpdateClaimants(){
		try {
			this.stmtUpdateClaimants = this.dbConnection.prepareStatement(
			    "UPDATE APP.CLAIMANTS " +
			    "SET LASTNAME = ?, " +
			    "    FIRSTNAME = ?, " +
			    "    MIDDLENAME = ?, " +
			    "    WORKPLACE = ?, " +
			    "    STATE = ?, " +
			    "WHERE ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtUpdateRSummary(){
		try {
			this.stmtUpdateRSummary = this.dbConnection.prepareStatement(
			    "UPDATE APP.R_SUMMARY " +
			    "SET TD_TYPE = ?, " +
			    "    BD_CALCWEEKPAY = ?, " +
			    "    BD_ANOTPAID = ?, " +
			    "WHERE CLAIM_ID = ? " + //This AND statement may throw an error
			    "AND TD_TYPE = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtUpdateClaimSummary(){
		try {
			this.stmtUpdateClaimSummary = this.dbConnection.prepareStatement(
			    "UPDATE APP.CLAIM_SUMMARY " +
			    "SET DATE_INJ = ?, " +
			    "    PRIOR_WS = ?, " +
			    "    EARLIEST_PW = ?, " +
			    "    BD_AVG_PGWP = ?, " +
			    "    DAYS_INJ = ?, " +
			    "    WEEKS_INJ = ?, " +
			    "WHERE CLAIM_ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtUpdatePaychecks(){
		try {
			this.stmtUpdatePaychecks = this.dbConnection.prepareStatement(
			    "UPDATE APP.PAYCHECKS " +
			    "SET PC_TYPE = ?, " +
			    "    PAY_DATE = ?, " +
			    "    PAY_START = ?, " +
			    "    PAY_END = ?, " +
			    "    BD_GROSS_AMNT = ?, " +
			    "WHERE CLAIM_ID = ? " +
			    "AND PC_TYPE = ?" +
			    "AND PAY_END = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtUpdateWCPaychecks(){
		try {
			this.stmtUpdateWCPaychecks = this.dbConnection.prepareStatement(
			    "UPDATE APP.WC_PAYCHECKS " +
			    "SET WC_PC_TYPE = ?, " +
			    "    IS_CONTEST = ?, " +
			    "    IS_LATE = ?, " +
			    "    FT_HOURS = ?, " +
			    "    PAY_RECEIVED = ?, " +
			    "    PAY_DATE = ?, " +
			    "    PAY_START = ?, " +
			    "    PAY_END = ?, " +
			    "    BD_GROSS_AMNT = ?, " +
			    "    BD_AMNT_OWED = ?, " +
			    "    CONTEST_RSLVD = ?, " +
			    "WHERE CLAIM_ID = ? " +
			    "AND WC_PC_TYPE = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtInsertClaimants(){
		try {
			this.stmtInsertClaimants = this.dbConnection.prepareStatement(
				"INSERT INTO APP.CLAIMANTS" + 
				"(LASTNAME, FIRSTNAME, MIDDLENAME, WORKPLACE, STATE) VALUES" +
				"(?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtInsertRSummary(){
		try {
			this.stmtInsertRSummary = this.dbConnection.prepareStatement(
				"INSERT INTO APP.R_SUMMARY" + 
				"(CLAIM_ID, TD_TYPE, BD_CALCWEEKPAY, BD_ANOTPAID) VALUES" +
				"(?,?,?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtInsertClaimSummary(){
		try {
			this.stmtInsertClaimSummary = this.dbConnection.prepareStatement(
				"INSERT INTO APP.CLAIM_SUMMARY" + 
				"(CLAIM_ID, DATE_INJ, PRIOR_WS, EARLIEST_PW, BD_AVG_PGWP, DAYS_INJ, WEEKS_INJ) VALUES" +
				"(?,?,?,?,?,?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtInsertPaychecks(){
		try {
			this.stmtInsertPaychecks = this.dbConnection.prepareStatement(
				"INSERT INTO APP.PAYCHECKS" + 
				"(CLAIM_ID, PC_TYPE, PAY_DATE, PAY_START, PAY_END, BD_GROSS_AMNT) VALUES" +
				"(?,?,?,?,?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtInsertWCPaychecks(){
		try {
			this.stmtInsertWCPaychecks = this.dbConnection.prepareStatement(
				"INSERT INTO APP.WC_PAYCHECKS" + 
				"(CLAIM_ID, WC_PC_TYPE, IS_CONTEST, IS_LATE, FT_HOURS, PAY_RECEIVED, PAY_DATE, PAY_START, PAY_END, BD_GROSS_AMNT, BD_AMNT_OWED, CONTEST_RSLVD) VALUES" +
				"(?,?,?,?,?,?,?,?,?,?,?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtSelectClaimants(){
		try {
			this.stmtSelectClaimants = this.dbConnection.prepareStatement(
				"SELECT * FROM APP.CLAIMANTS" + 
				"WHERE ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtSelectClaimSummary(){
		try {
			this.stmtSelectClaimSummary = this.dbConnection.prepareStatement(
				"SELECT * FROM APP.CLAIM_SUMMARY " + 
				"WHERE CLAIM_ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtSelectTDType(){
		try {
			this.stmtSelectTDType = this.dbConnection.prepareStatement(
				"SELECT * FROM APP.R_SUMMARY " + 
				"WHERE CLAIM_ID = ? AND TD_TYPE = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtSelectPCType(){
		try {
			this.stmtSelectPCType = this.dbConnection.prepareStatement(
				"SELECT * FROM APP.PAYCHECKS " + 
				"WHERE CLAIM_ID = ? AND PC_TYPE = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtSelectWCPCType(){
		try {
			this.stmtSelectWCPCType = this.dbConnection.prepareStatement(
				"SELECT * FROM APP.WC_PAYCHECKS " + 
				"WHERE CLAIM_ID = ? AND WC_PC_TYPE = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setStmtSelectAllClaimants(){
		try {
			this.stmtSelectAllClaimants = this.dbConnection.prepareStatement(
				"SELECT * FROM APP.CLAIMANTS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PreparedStatement getStmtDeleteClaimRecord(){
		return this.stmtDeleteClaimRecord;
	}
	
	public PreparedStatement getStmtDeletePaychecksFrmSingleClaim(){
		return this.stmtDeletePaychecksFrmSingleClaim;
	}
	
	public PreparedStatement getStmtUpdateClaimants(){
		return this.stmtUpdateClaimants;
	}
	
	public PreparedStatement getStmtUpdateRSummary(){
		return this.stmtUpdateRSummary;
	}
	
	public PreparedStatement getStmtUpdateClaimSummary(){
		return this.getStmtUpdateClaimSummary();
	}

	public PreparedStatement getStmtUpdatePaychecks(){
		return this.stmtUpdatePaychecks;
	}
	
	public PreparedStatement getStmtUpdateWCPaychecks(){
		return this.stmtUpdateWCPaychecks;
	}
	
	public PreparedStatement getStmtInsertClaimants(){
		return this.stmtInsertClaimants;
	}
	
	public PreparedStatement getStmtInsertRSummary(){
		return this.stmtInsertRSummary;
	}
	
	public PreparedStatement getStmtInsertClaimSummary(){
		return this.stmtInsertClaimSummary;
	}
	
	public PreparedStatement getStmtInsertPaychecks(){
		return this.stmtInsertPaychecks;
	}
	
	public PreparedStatement getStmtInsertWCPaychecks(){
		return this.stmtInsertWCPaychecks;
	}
	
	public PreparedStatement getStmtSelectClaimants(){
		return this.stmtSelectClaimants;
	}
	
	public PreparedStatement getStmtSelectClaimSummary(){
		return this.stmtSelectClaimSummary;
	}
	
	public PreparedStatement getStmtSelectTDType(){
		return this.stmtSelectTDType;
	}
	
	public PreparedStatement getStmtSelectPCType(){
		return this.stmtSelectPCType;
	}
	
	public PreparedStatement getStmtSelectWCPCType(){
		return this.stmtSelectWCPCType;
	}
	
	public PreparedStatement getStmtSelectAllClaimants(){
		return this.stmtSelectAllClaimants;
	}
	
	//To be called when statements are no longer needed for a session (i.e. Application is closed)
	public void shutdownAllPreparedStatements(){
		
		try {
			this.stmtDeleteClaimRecord.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try{
			this.stmtDeletePaychecksFrmSingleClaim.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtUpdateClaimants.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtUpdateRSummary.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtUpdateClaimSummary.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtUpdatePaychecks.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtUpdateWCPaychecks.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtInsertClaimants.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtInsertRSummary.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtInsertClaimSummary.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtInsertPaychecks.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtInsertWCPaychecks.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.stmtSelectAllClaimants.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		try {
			this.stmtSelectClaimants.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		try {
			this.stmtSelectTDType.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		try {
			this.stmtSelectWCPCType.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		try {
			this.stmtSelectPCType.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		try {
			this.dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
