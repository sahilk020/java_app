package com.pay10.scheduler.irctc;

import com.pay10.commons.entity.IRCTCRefundFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IRCTCRefundFileServiceImpl implements IRCTCRefundFileService{

    @Autowired
    IRCTCRefundFileRepository irctcRefundFileRepository;

    @Override
    public void save(IRCTCRefundFile irctcRefundFile) {
        irctcRefundFileRepository.save(irctcRefundFile);
    }
}
