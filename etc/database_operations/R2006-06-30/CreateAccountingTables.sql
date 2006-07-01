drop table if exists ACCOUNT;
create table ACCOUNT (
	ID_INTERNAL int(11) NOT NULL auto_increment,
	KEY_ROOT_DOMAIN_OBJECT int(11) NOT NULL default '1',
	KEY_PARTY int(11) NOT NULL,
	ACCOUNT_TYPE varchar(100) default null,
	PRIMARY KEY (ID_INTERNAL),
	UNIQUE KEY PARTY_ACCOUNT_TYPE (KEY_PARTY, ACCOUNT_TYPE)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

drop table if exists ACCOUNTING_TRANSACTION;
create table ACCOUNTING_TRANSACTION (
	ID_INTERNAL int(11) NOT NULL auto_increment,
	WHEN_REGISTED datetime NOT NULL default '0000-00-00 00:00:00',
	KEY_EVENT int(11) NOT NULL,
	KEY_RESPONSIBLE_USER int(11) NOT NULL,
	KEY_ROOT_DOMAIN_OBJECT int(11) NOT NULL default '1',
	PRIMARY KEY (ID_INTERNAL)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

drop table if exists ACCOUNTING_ENTRY;
create table ACCOUNTING_ENTRY (
	ID_INTERNAL int(11) NOT NULL auto_increment,
	KEY_ROOT_DOMAIN_OBJECT int(11) NOT NULL default '1',
	KEY_ACCOUNT int(11) NOT NULL,
	KEY_RECEIPT int(11) NULL,
	KEY_ACCOUNTING_TRANSACTION int(11) NOT NULL,
	WHEN_BOOKED datetime NOT NULL default '0000-00-00 00:00:00',
	AMOUNT varchar(250) NOT NULL,
	ENTRY_TYPE varchar(250) NOT NULL,
	PRIMARY KEY (ID_INTERNAL)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

drop table if exists ACCOUNTING_EVENT;
create table ACCOUNTING_EVENT (
	ID_INTERNAL int(11) NOT NULL auto_increment,
	KEY_ROOT_DOMAIN_OBJECT int(11) NOT NULL default '1',
	OJB_CONCRETE_CLASS varchar(250) NOT NULL,
	EVENT_TYPE varchar(100) NOT NULL,
	WHEN_OCCURED datetime NOT NULL default '0000-00-00 00:00:00',
	CLOSED BIT NOT NULL,
	KEY_PARTY int(11) NULL,
	KEY_CANDIDACY int(11) NULL,
	PRIMARY KEY (ID_INTERNAL)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

drop table if exists RECEIPT;
create table RECEIPT (
	ID_INTERNAL int(11) NOT NULL auto_increment,
	NUMBER int(11) NOT NULL,
	YEAR int(4) NOT NULL,
	KEY_PARTY int(11) NOT NULL,
	KEY_CONTRIBUTOR int(11) NOT NULL,
	KEY_ROOT_DOMAIN_OBJECT int(11) NOT NULL default '1',
	PRIMARY KEY (ID_INTERNAL),
	UNIQUE U1 (YEAR,NUMBER)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

drop table if exists RECEIPT_VERSION;
create table RECEIPT_VERSION (
	ID_INTERNAL int(11) NOT NULL auto_increment,
	WHEN_CREATED datetime NOT NULL,
	KEY_ROOT_DOMAIN_OBJECT int(11) NOT NULL default '1',
	KEY_RECEIPT int(11) NOT NULL,
	KEY_EMPLOYEE int(11) NOT NULL,
	PRIMARY KEY (ID_INTERNAL)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
