
DELIMITER $$ 
CREATE DEFINER=`root`@`localhost` PROCEDURE `misReportsExport`(IN dateFrom varchar(250),IN dateTo varchar(250),IN payId varchar(250), IN acquirer varchar(250),IN userType varchar(250))
BEGIN
	SELECT
        `t1`.`PAY_ID` AS `PAY_ID`,
        `t1`.`CREATE_DATE` AS `CREATE_DATE`,
        `t1`.`TXN_ID` AS `TXN_ID`,
        `t1`.`UPDATE_DATE` AS `UPDATE_DATE`,
        `t1`.`CUST_EMAIL` AS `CUST_EMAIL`,
        `t1`.`PAY_ID` AS `PAY_ID`,
        `t1`.`ACQUIRER_TYPE` AS `ACQUIRER_TYPE`,
        `t1`.`PAYMENT_TYPE` AS `PAYMENT_TYPE`,
        `t1`.`MOP_TYPE` AS `MOP_TYPE`,
        `t1`.`TXNTYPE` AS `TXNTYPE`,
        `t1`.`CURRENCY_CODE` AS `CURRENCY_CODE`,
        `su`.`businessName` AS `businessName`,
		`su`.`ifscCode` AS `ifscCode`,
		`su`.`bankName` AS `bankName`,
		`su`.`accountNo` AS `accountNo`,
        `t1`.`AMOUNT` AS `AMOUNT`,
        `t1`.`status` as STATUS
       
    FROM
        `TRANSACTION` `t1` left join User su on PAY_ID = su.payId
    where (CREATE_DATE between dateFrom and dateTo)  and   ((`t1`.`TXNTYPE` = 'SALE' AND `t1`.`STATUS` = 'Captured') or (`t1`.`TXNTYPE` = 'REFUND' AND `t1`.`STATUS` = 'Captured') or (`t1`.`TXNTYPE` = 'CAPTURE' AND `t1`.`STATUS` = 'Captured'))
	and (CASE when payId='ALL' then 1 else (CASE when PAY_ID = payId then 1 else 0 END) END)
    and (CASE when acquirer='ALL'  then ACQUIRER_TYPE not in('CITRUS') else (CASE when ACQUIRER_TYPE = acquirer then 1 else 0 END) END)
    and (CASE when (userType='ADMIN' or userType='SUBADMIN' or userType='SUPERADMIN') and (payId = 'ALL') and (su.userType <> 'POSMERCHANT') then 1 else (CASE when PAY_ID = payId then 1 else 0 END) END);
END$$
DELIMITER ;

DELIMITER $$ 
CREATE DEFINER=`root`@`localhost` PROCEDURE `binRange_Records`(IN cardType varchar(250),IN mopType varchar(250),IN userType varchar(250),IN start int,IN length int)
BEGIN
SELECT
        `b`.`binCodeLow` AS `binCodeLow`,
        `b`.`binCodeHigh` AS `binCodeHigh`,
        `b`.`binRangeLow` AS `binRangeLow`,
        `b`.`binRangeHigh` AS `binRangeHigh`,
        `b`.`issuerBankName` AS `issuerBankName`,
        `b`.`mopType` AS `mopType`,
        `b`.`cardType` AS `cardType`,
        `b`.`issuerCountry` AS `issuerCountry`,
        `b`.`productName` AS `productName`,
        `b`.`groupCode` AS `groupCode`,
        `b`.`rfu1` AS `rfu1`,
        `b`.`rfu2` AS `rfu2`
     FROM
	   `BinRange` `b` 
      where (CASE when cardType='ALL' then 1 else (CASE when `b`.`cardType` = cardType then 1 else 0 END) END)
	  and (CASE when mopType='ALL' then 1 else (CASE when `b`.`mopType` = mopType then 1 else 0 END) END)
    and (CASE when (userType='ADMIN' or userType='SUBADMIN' or userType='SUPERADMIN') then 1 else 0 END) LIMIT start,length;
END$$
DELIMITER ;

DELIMITER $$ 
CREATE DEFINER=`root`@`localhost` PROCEDURE `binRange_Records_count`(IN cardType varchar(250),IN mopType varchar(250),IN userType varchar(250))
BEGIN
SELECT count(*) recordsTotal
     FROM
	 `BinRange` `b` 
      where (CASE when cardType='ALL' then 1 else (CASE when cardType = cardType then 1 else 0 END) END)
	  and (CASE when mopType='ALL' then 1 else (CASE when mopType = mopType then 1 else 0 END) END)
      and (CASE when (userType='ADMIN' or userType='SUBADMIN' or userType='SUPERADMIN') then 1 else 0 END);
END$$
DELIMITER ;

