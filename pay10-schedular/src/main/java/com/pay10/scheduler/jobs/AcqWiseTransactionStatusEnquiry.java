package com.pay10.scheduler.jobs;

import com.pay10.acqsched.AcquirerSechedulerConfig;
import com.pay10.acqsched.AcquirerSingleTask;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.scheduler.commons.ConfigurationProvider;
import com.pay10.scheduler.core.ServiceControllerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AcqWiseTransactionStatusEnquiry {

    @Autowired
    private ConfigurationProvider configurationProvider;
    @Autowired
    private MongoInstance mongoInstance;
    @Autowired
    private ServiceControllerProvider serviceControllerProvider;
  
    @Autowired
    JdbcTemplate jdbcTemplate;
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final Logger logger = LoggerFactory.getLogger(AcqWiseTransactionStatusEnquiry.class);

    //@Scheduled(fixedDelay = 60*1000)
    private void fetchAquiereWiseStatusCheck(){


        final String query="SELECT * FROM acq_sched_config";

        List<AcquirerSechedulerConfig> acqlist = jdbcTemplate.query(query, new RowMapper<AcquirerSechedulerConfig>() {
            @Override
            public AcquirerSechedulerConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
                AcquirerSechedulerConfig acquirerWiseScheduler = new AcquirerSechedulerConfig();
                acquirerWiseScheduler.setAcquirer(rs.getString("acquirer_name"));
                acquirerWiseScheduler.setInstrument(rs.getString("instrument"));
                acquirerWiseScheduler.setStartTime(rs.getString("start_time"));
                acquirerWiseScheduler.setMaxTime(rs.getString("max_time"));
                acquirerWiseScheduler.setRetry(rs.getString("status"));
                return acquirerWiseScheduler;
            }
        }, new Object[]{});
        logger.info("Acquirer list : " + acqlist.size());
        for (AcquirerSechedulerConfig acq : acqlist) {
            logger.info("Submitting request to thread pool for Acquirer : "+acq.getAcquirer());
            executorService.submit(new AcquirerSingleTask(mongoInstance,configurationProvider,acq));

        }}
}
