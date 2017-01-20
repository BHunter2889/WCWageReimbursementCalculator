package Classes;

import java.sql.Connection;
import java.sql.SQLException;

public class ClaimPreparedStatements {
	
	protected Connection dbConnection;
	protected String stmtDeleteClaimRecord;
	protected String stmtDeletePaychecksFrmSingleClaim;
	protected String stmtUpdateClaimants;
	protected String stmtUpdateRSummary;
	protected String stmtUpdateClaimSummary;
	protected String stmtUpdatePaychecks;
	protected String stmtUpdateWCPaychecks;
	protected String stmtInsertClaimants;
	protected String stmtInsertRSummary;
	protected String stmtInsertClaimSummary;
	protected String stmtInsertPaychecks;
	protected String stmtInsertWCPaychecks;
	protected String stmtSelectClaimants;
	protected String stmtSelectClaimSummary;
	protected String stmtSelectTDType;
	protected String stmtSelectPCType;
	protected String stmtSelectWCPCType;
	protected String stmtSelectAllClaimants;

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
			this.stmtDeleteClaimRecord =
			        "DELETE FROM " +
			        "tableName " +
			        "where ID = ?";
	}
	
	public void setStmtDeletePaychecksFrmSingleClaim(){
			this.stmtDeletePaychecksFrmSingleClaim =
			        "DELETE FROM APP.PAYCHECKS " +
			        "where ID = ? AND PC_TYPE = ?";
	}
	
	public void setStmtUpdateClaimants(){
			this.stmtUpdateClaimants =
			    "UPDATE APP.CLAIMANTS " +
			    "SET LASTNAME = ?, " +
			    "    FIRSTNAME = ?, " +
			    "    MIDDLENAME = ?, " +
			    "    WORKPLACE = ?, " +
			    "    STATE = ? " +
			    "where ID = ?";
	}
	
	public void setStmtUpdateRSummary(){
			this.stmtUpdateRSummary =
			    "UPDATE APP.R_SUMMARY " +
			    "SET TD_TYPE = ?, " +
			    "    BD_CALCWEEKPAY = ?, " +
			    "    BD_ANOTPAID = ?, " +
			    "where CLAIM_ID = ? " + //This AND statement may throw an error
			    "AND TD_TYPE = ?";
	}
	
	public void setStmtUpdateClaimSummary(){
			this.stmtUpdateClaimSummary =
				"UPDATE APP.CLAIM_SUMMARY " +
			    "SET DATE_INJ = ?, " +
			    "    PRIOR_WS = ?, " +
			    "    EARLIEST_PW = ?, " +
			    "    BD_AVG_PGWP = ?, " +
			    "    DAYS_INJ = ?, " +
			    "    WEEKS_INJ = ?, " +
			    "where CLAIM_ID = ?";
	}
	
	public void setStmtUpdatePaychecks(){
			this.stmtUpdatePaychecks =
				"UPDATE APP.PAYCHECKS " +
			    "SET PC_TYPE = ?, " +
			    "    PAY_DATE = ?, " +
			    "    PAY_START = ?, " +
			    "    PAY_END = ?, " +
			    "    BD_GROSS_AMNT = ?, " +
			    "where CLAIM_ID = ? " +
			    "AND PC_TYPE = ?" +
			    "AND PAY_END = ?";
	}
	
	public void setStmtUpdateWCPaychecks(){
			this.stmtUpdateWCPaychecks = 
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
			    "where CLAIM_ID = ? " +
			    "AND WC_PC_TYPE = ?";
	}
	
	public void setStmtInsertClaimants(){
			this.stmtInsertClaimants =
				"INSERT INTO APP.CLAIMANTS" + 
				"(LASTNAME, FIRSTNAME, MIDDLENAME, WORKPLACE, STATE) VALUES" +
				"(?,?,?,?,?)";
	}
	
	public void setStmtInsertRSummary(){
			this.stmtInsertRSummary =
				"INSERT INTO APP.R_SUMMARY" + 
				"(CLAIM_ID, TD_TYPE, BD_CALCWEEKPAY, BD_ANOTPAID) VALUES" +
				"(?,?,?,?)";
	}
	
	public void setStmtInsertClaimSummary(){
			this.stmtInsertClaimSummary =
				"INSERT INTO APP.CLAIM_SUMMARY" + 
				"(CLAIM_ID, DATE_INJ, PRIOR_WS, EARLIEST_PW, BD_AVG_PGWP, DAYS_INJ, WEEKS_INJ) VALUES" +
				"(?,?,?,?,?,?,?)";
	}
	
	public void setStmtInsertPaychecks(){
			this.stmtInsertPaychecks =
				"INSERT INTO APP.PAYCHECKS" + 
				"(CLAIM_ID, PC_TYPE, PAY_DATE, PAY_START, PAY_END, BD_GROSS_AMNT) VALUES" +
				"(?,?,?,?,?,?)";
	}
	
	public void setStmtInsertWCPaychecks(){
			this.stmtInsertWCPaychecks =
				"INSERT INTO APP.WC_PAYCHECKS" + 
				"(CLAIM_ID, WC_PC_TYPE, IS_CONTEST, IS_LATE, FT_HOURS, PAY_RECEIVED, PAY_DATE, PAY_START, PAY_END, BD_GROSS_AMNT, BD_AMNT_OWED, CONTEST_RSLVD) VALUES" +
				"(?,?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	public void setStmtSelectClaimants(){
			this.stmtSelectClaimants = 
				"SELECT * FROM APP.CLAIMANTS " + 
				"where ID = ?";
	}
	
	public void setStmtSelectClaimSummary(){
			this.stmtSelectClaimSummary =
				"SELECT * FROM APP.CLAIM_SUMMARY " + 
				"where CLAIM_ID = ?";
	}
	
	public void setStmtSelectTDType(){
			this.stmtSelectTDType =
				"SELECT * FROM APP.R_SUMMARY " + 
				"where CLAIM_ID = ? AND TD_TYPE = ?";
	}
	
	public void setStmtSelectPCType(){
			this.stmtSelectPCType =
				"SELECT * FROM APP.PAYCHECKS " + 
				"where CLAIM_ID = ? AND PC_TYPE = ?";
	}
	
	public void setStmtSelectWCPCType(){
			this.stmtSelectWCPCType =
				"SELECT * FROM APP.WC_PAYCHECKS " + 
				"where CLAIM_ID = ? AND WC_PC_TYPE = ?";

	}
	
	public void setStmtSelectAllClaimants(){
			this.stmtSelectAllClaimants =
				"SELECT * FROM APP.CLAIMANTS";
	}
	
	public String getStmtDeleteClaimRecord(String tableName){
		return this.setTableName(this.stmtDeleteClaimRecord, tableName);
	}
	
	public String getStmtDeletePaychecksFrmSingleClaim(){
		return this.stmtDeletePaychecksFrmSingleClaim;
	}
	
	public String getStmtUpdateClaimants(){
		return this.stmtUpdateClaimants;
	}
	
	public String getStmtUpdateRSummary(){
		return this.stmtUpdateRSummary;
	}
	
	public String getStmtUpdateClaimSummary(){
		return this.getStmtUpdateClaimSummary();
	}

	public String getStmtUpdatePaychecks(){
		return this.stmtUpdatePaychecks;
	}
	
	public String getStmtUpdateWCPaychecks(){
		return this.stmtUpdateWCPaychecks;
	}
	
	public String getStmtInsertClaimants(){
		return this.stmtInsertClaimants;
	}
	
	public String getStmtInsertRSummary(){
		return this.stmtInsertRSummary;
	}
	
	public String getStmtInsertClaimSummary(){
		return this.stmtInsertClaimSummary;
	}
	
	public String getStmtInsertPaychecks(){
		return this.stmtInsertPaychecks;
	}
	
	public String getStmtInsertWCPaychecks(){
		return this.stmtInsertWCPaychecks;
	}
	
	public String getStmtSelectClaimants(){
		return this.stmtSelectClaimants;
	}
	
	public String getStmtSelectClaimSummary(){
		return this.stmtSelectClaimSummary;
	}
	
	public String getStmtSelectTDType(){
		return this.stmtSelectTDType;
	}
	
	public String getStmtSelectPCType(){
		return this.stmtSelectPCType;
	}
	
	public String getStmtSelectWCPCType(){
		return this.stmtSelectWCPCType;
	}
	
	public String getStmtSelectAllClaimants(){
		return this.stmtSelectAllClaimants;
	}
	
	public String setTableName(String statement, String tableName){
		return statement.replace("tableName", tableName);
	}
	
	/*Dead Code from prior implementation, saving for reference
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
	*/
}
