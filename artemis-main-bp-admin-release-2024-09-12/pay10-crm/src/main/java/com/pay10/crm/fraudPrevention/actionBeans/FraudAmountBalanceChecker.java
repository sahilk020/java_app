package com.pay10.crm.fraudPrevention.actionBeans;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class FraudAmountBalanceChecker {

    @Autowired
    MongoInstance mongoInstance;

    private static final String prefix = "MONGO_DB_";

    public Document getPaymentBalance(String payId, String currency, String paymentType){

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String dailyDate = sdf.format(date.getTime());

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        date = calendar.getTime();
        String weeklyDate = sdf.format(date.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        String monthlyDate = sdf.format(date.getTime());

        String startDate = monthlyDate.compareTo(weeklyDate)>0?weeklyDate:monthlyDate;
        MongoDatabase mongoDatabase = mongoInstance.getDB();

        MongoCollection<Document> coll = mongoDatabase.getCollection("transaction");
        List<Document> query = Arrays.asList(new Document("$match",
                        new Document("PAY_ID", payId)
                                .append("STATUS", "Captured")
                                .append("CURRENCY_CODE", currency)
                                .append("PAYMENT_TYPE", paymentType)
                                .append("CREATE_DATE",
                                        new Document("$gte", startDate))),
                new Document("$group",
                        new Document("_id", "$PAY_ID")
                                .append("dailyLimit",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", dailyDate)),
                                                        new Document("$toDouble", "$TOTAL_AMOUNT"), 0.0))))
                                .append("weeklyLimit",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", weeklyDate)),
                                                        new Document("$toDouble", "$TOTAL_AMOUNT"), 0.0))))
                                .append("monthlyLimit",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", monthlyDate)),
                                                        new Document("$toDouble", "$TOTAL_AMOUNT"), 0.0))))
                                .append("dailyVolume",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", dailyDate)), 1L, 0L))))
                                .append("weeklyVolume",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", weeklyDate)), 1L, 0L))))
                                .append("monthlyVolume",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(new Document("$gte", Arrays.asList("$CREATE_DATE", monthlyDate)), 1L, 0L))))));


        Iterator<Document> doc = coll.aggregate(query).iterator();
        if(doc.hasNext()){
            return doc.next();
        }
        return null;
    }
}
