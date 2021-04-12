MERGE INTO hotIssues h
USING issues i
ON h.issueID = i.issueID

WHEN MATCHED AND i.lastUpdated = CURRENT_DATE 
    THEN UPDATE SET h.lastUpdated = i.lastUpdated
WHEN MATCHED AND i.lastUpdated < CURRENT_DATE THEN DELETE
WHEN NOT MATCHED AND i.lastUpdated = CURRENT_DATE 
    THEN INSERT VALUES ( i.issueID, i.lastUpdated );

MERGE INTO companies c
USING adhocInvoices a
ON a.companyName = c.companyName
WHEN NOT MATCHED THEN INSERT ( companyName ) VALUES ( a.companyName );

MERGE INTO warehouse.productList w
USING production.productList p
ON w.productID = p.productID
WHEN MATCHED and w.lastUpdated != p.lastUpdated
    THEN UPDATE SET lastUpdated = p.lastUpdated, 
                    description = p.description, 
                    price = p.price
WHEN NOT MATCHED
    THEN INSERT values ( p.productID, p.lastUpdated, p.description,
                         p.price );

       private final static String queryMergeState       = "MERGE INTO "+table_name_state+
                                                                 //             1
                                                                 "USING (SELECT ? RECORD_ID from DUAL) S2 "+
                                                                 "ON ( DS.RECORD_ID=S2.RECORD_ID) "+
                                                                 "WHEN MATCHED THEN "+
                                                                 //                            2            3         4                   5
                                                                 "UPDATE SET DS.ID=0,DS.VAR_TIME=?,DS.VAR_DATA=?,DS.INTER=? where DS.RECORD_ID=? "+
                                                                 "WHEN NOT MATCHED THEN "+    
                                                                 //                                                                 6 7 8 9                      
                                                                 "INSERT (DS.ID,DS.VAR_TIME,DS.VAR_DATA,DS.INTER,DS.RECORD_ID) VALUES (0,?,?,?,?)"
                                                            ;


SELECT E.MAIL_UID,E.MAIL_UID,E.MAIL_ID,E.ADDR_FROM,E.ADDR_TO,E.SUBJECT,E.FILENAME
,E.CREATE_DATE,E.SENT_DATE,E.RECEIVE_DATE,E.DEL_DATE,E.ANS_DATE,E.MIME,E.ATTACH_SIZE,E.X509_TXT,E.X509_ID
,X.X509_ID,X.X509_TYPE,X.X509_TYPE_FILE,X.X509_BEGIN_DATE,X.X509_END_DATE,X.X509_SERIAL,X.X509_SUBJECT,X.X509_ISSUER,X.X509_BIN 
FROM arh_eml E,arh_x509 X WHERE E.X509_ID=X.X509_ID ORDER BY E.CREATE_DATE 