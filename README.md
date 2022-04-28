# Trade Store

Submission for Barclays Code Pairing Assessment. 

## Problem statement

Consider a scenario where thousands of trades flows into one store. Assume any way of transmission of trades. We need to create a one trade store, which stores the trade in the following order:

| Trade Id | Version | Counter-Party Id | Book-Id | Maturity Date | Created Date | Expired |
|----------|---------|------------------|---------|---------------|--------------|---------|
| T1       | 1       | CP-1             | B1      | 20/05/2020    | today date   | N       |
| T2       | 2       | CP-2             | B1      | 20/05/2021    | today date   | N       |
| T2       | 1       | CP-1             | B1      | 20/05/2021    | 14/03/2015   | N       |
| T3       | 3       | CP-3             | B2      | 20/05/2014    | today date   | Y       |

### Requirements

* During transmission if the lower version is being received by the store it will reject the trade and throw an exception. If the version is same it will override the existing record.
* Store should not allow the trade which has less maturity date than today date.
* Store should automatically update the expire flag if in a store the trade crosses the maturity date.

## Solution

A SpringBoot application that covers all these requirements. 

Application is exposed to the user via a REST API from where the user can send trade information using any REST API testing tool like Postman. 

The request must be HTTP POST to http://localhost:8080/api/v1/barclays/store/trades

Initial state of database:

![database before app startup](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img1.png "Inital state of DB table")

1. **Store should automatically update the expire flag if in a store the trade crosses the maturity date.**

Upon application startup a thread is created that periodically scans and updates trades inside the store that have crossed their maturity date. 

DB state after application startup shows trades marked as Y where maturity date is a past date.
![DB state after app startup](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img2.png "DB state after app startup")

2. **Store should not allow the trade which has less maturity date than today date.**

A trade request is sent where the maturity date is a past date.

![JSON request with invalid maturity date](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img3.png "JSON request with invalid maturity date")

Application sends back a HTTP 400: BAD REQUEST with proper error message.

![JSON response from app](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img4.png "JSON response from app")

3. **During transmission if the lower version is being received by the store it will reject the trade and throw an exception.**

A trade request is sent where the version is a lower version.

![JSON request with invalid version](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img5.png "JSON request with invalid version")

Application sends back a HTTP 400: BAD REQUEST with proper error message.

![JSON response from app](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img6.png "JSON response from app")

4. **If the version is same it will override the existing record.**

DB state before sending the request

![DB state before same version request is sent](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img7.png "DB state before same version request is sent")

A trade request is sent where the version is same as the latest trade version.

![JSON request with same version](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img8.png "JSON request with same version")

Application sends back a HTTP 200: OK with proper acknowledgement.

![JSON response from app](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img9.png "JSON response from app")

DB state after the request is processed.

![DB state after same version request is processed](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img9.1.png "DB state after same version request is processed")

5. **A trade for which trade id isn't already present in the store.**

Application sends a trade request for which trade id isn't present in the DB and receives a success message.

![JSON response from app](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img10.png "JSON response from app")

DB state after fresh trade is processed.

![DB state after fresh trade is processed](https://raw.githubusercontent.com/Shashank9830/TradeStore/main/references/img11.png "DB state after fresh trade is processed")

## Steps to execute

This is a Maven project and uses Java 11. Load the code from **BarclaysTradeStore** folder. Build it using Maven. Test cases are written which will get auto executed during the build phase.


* Please use **BarclaysTradeStore/src/main/resources/table_creation.sql** file for creating and populating the MySQL database on your system.

~~~~sql
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
~~~~

* MySQL related configuration is present in **BarclaysTradeStore/src/main/resources/application.properties**. Update the port, db name, username and password according to your MySQL settings.

~~~~
# MySQL settings
#Change these settings according to database you are using
spring.datasource.url=jdbc:mysql://localhost:3306/barclays_db
spring.datasource.username=root

#If MySQL installation is password proctored,then use below property to set password
spring.datasource.password=root

# Error messages
General.EXCEPTION_MESSAGE=Trade could not be processed due to some issue. Please try again !!!
TradeStoreService.INVALID_MATURITY_DATE=Maturity date must be today's date or a future date.
TradeStoreService.LOWER_VERSION_TRADE=Trade received has a lower version than existing trade with same Trade ID.

# Success messages
API.TRADE_SUCCESS=Trade successfully added to the store.

# Server settings (defaults to 8080)
#server.port=8080

# Batch job sleep time, default value is 24 Hours or 86400000 (24*60*60*1000) ms
BatchService.SLEEP_TIME=10000
~~~~

* Start the application as a Java/SpringBoot application from your IDE.

Entry point for the application is present at:

```BarclaysTradeStore/src/main/java/com/barclays/BarclaysTradeStoreApplication.java```

All Junit test cases are present at:

```BarclaysTradeStore/src/test/java/com/barclays/BarclaysTradeStore/BarclaysTradeStoreApplicationTests.java```

## Testing

Use Postman or similar application to send a HTTP POST request to the API endpoint with a raw JSON payload.

**API Endpoint**: http://localhost:8080/api/v1/barclays/store/trades

**Type**: HTTP POST

**Payload**: JSON

Payload should be of the following format:

```json
{
    "tradeId": "T3",
    "version": 1,
    "counterPartyId": "CP-1",
    "bookId": "B3",
    "maturityDate": "2023-04-20",
    "createdDate": "2022-01-01",
    "expired": "N"
}
```

For any clarifications/doubts regarding setup/code, please drop a mail to **shashank9830@gmail.com**.

## Authors

* **Shashank Singh** - *Complete work* - [shashank9830](https://github.com/shashank9830)
