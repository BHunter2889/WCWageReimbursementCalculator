CREATE table APP.CLAIMANTS (
    ID          INTEGER NOT NULL 
                PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
    LASTNAME    VARCHAR(30), 
    FIRSTNAME   VARCHAR(30),
    MIDDLENAME  VARCHAR(30),
    WORKPLACE	VARCHAR(255),
    STATE		VARCHAR(30) );
    
CREATE table APP.R_SUMMARY (
    ID          	 INTEGER NOT NULL 
                	 PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                	 (START WITH 1, INCREMENT BY 1),
    CLAIM_ID         INTEGER NOT NULL
					 CONSTRAINT RSUMM_FK
					 REFERENCES CLAIMANTS (ID),
	TD_TYPE			 VARCHAR(30),
    BD_CALCWEEKPAY   DECIMAL(10,2),
    BD_ANOTPAID  	 DECIMAL(10,2),
    FD_DATE			 DATE DEFAULT NULL);      
    
CREATE table APP.CLAIM_SUMMARY (
    ID          	 INTEGER NOT NULL 
                	 PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                	 (START WITH 1, INCREMENT BY 1),
    CLAIM_ID         INTEGER NOT NULL
					 CONSTRAINT CSUMM_FK
					 REFERENCES CLAIMANTS (ID), 
	DATE_INJ		 DATE,
	PRIOR_WS		 DATE,
	EARLIEST_PW		 DATE,
    BD_AVG_PGWP  	 DECIMAL(10,2),
    DAYS_INJ		 BIGINT,
    WEEKS_INJ		 BIGINT );
    
CREATE table APP.PAYCHECKS (
    ID          	 INTEGER NOT NULL 
                	 PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                	 (START WITH 1, INCREMENT BY 1),
    CLAIM_ID         INTEGER NOT NULL
					 CONSTRAINT PC_FK
					 REFERENCES CLAIMANTS (ID),
	PC_TYPE			 VARCHAR(30),
	PAY_DATE		 DATE,
	PAY_START		 DATE,
	PAY_END			 DATE,
    BD_GROSS_AMNT  	 DECIMAL(10,2),
    BD_WC_CALC 		 DECIMAL(10,2) DEFAULT NULL);

CREATE table APP.WC_PAYCHECKS (
    ID          	 INTEGER NOT NULL 
                	 PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                	 (START WITH 1, INCREMENT BY 1),
    CLAIM_ID         INTEGER NOT NULL
					 CONSTRAINT WCPC_FK
					 REFERENCES CLAIMANTS (ID),
	WC_PC_TYPE		 VARCHAR(30),
	IS_CONTEST		 BOOLEAN,
	IS_LATE			 BOOLEAN,
	FT_HOURS		 BOOLEAN,
	PAY_RECEIVED	 DATE,
	PAY_DATE		 DATE,
	PAY_START		 DATE,
	PAY_END			 DATE,
    BD_GROSS_AMNT  	 DECIMAL(10,2),
    BD_AMNT_OWED  	 DECIMAL(10,2),
    CONTEST_RSLVD	 DATE DEFAULT NULL);
    
    
    
    
    