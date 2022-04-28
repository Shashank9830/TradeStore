drop database if exists barclays_db;
create database barclays_db;
use barclays_db;

create table trade (
	id int primary key,
	trade_id VARCHAR(10) NOT NULL,
	version int NOT NULL,
	counter_party_id VARCHAR(10) NOT NULL,
	book_id VARCHAR(10) NOT NULL,
	maturity_date Date NOT NULL,
	created_date Date NOT NULL,
	expired CHAR(1) NOT NULL CHECK (expired IN ('N', 'Y'))
);

insert into trade values (1, 'T1', 1, 'CP-1', 'B1', '2020-05-20', current_date(), 'N');
insert into trade values (2, 'T2', 2, 'CP-2', 'B1', '2021-05-20', current_date(), 'N');
insert into trade values (3, 'T2', 1, 'CP-1', 'B1', '2021-05-20', '2015-03-14', 'N');
insert into trade values (4, 'T1', 3, 'CP-3', 'B2', '2014-05-20', current_date(), 'Y');
commit;

select * from trade;