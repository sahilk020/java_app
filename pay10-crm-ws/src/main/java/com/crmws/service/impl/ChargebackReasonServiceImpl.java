package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.crmws.dto.ChargeBackReasonDTO;
import com.crmws.entity.ResponseMessage;
import com.crmws.service.ChargebackReasonService;
import com.pay10.commons.entity.ChargebackReasonEntity;
import com.pay10.commons.repository.ChargebackReasonRepository;

@Service
public class ChargebackReasonServiceImpl implements ChargebackReasonService {

    @Autowired
    private ChargebackReasonRepository chargebackReasonRepository;

    private static final Logger logger = LoggerFactory.getLogger(ChargebackReasonServiceImpl.class.getName());

    @Override
    public ResponseMessage saveChargeBackReason(ChargeBackReasonDTO chargeBackReasonDTO) {
        try {
            if (StringUtils.isBlank(chargeBackReasonDTO.getCbReasonCode()) || chargeBackReasonDTO.getCbReasonCode().length() > 15) {
                return new ResponseMessage("1001", HttpStatus.OK);
            } else if (StringUtils.isBlank(chargeBackReasonDTO.getCbReasonDescription()) || chargeBackReasonDTO.getCbReasonDescription().length() > 250) {
                return new ResponseMessage("1002", HttpStatus.OK);
            } else {
                ChargebackReasonEntity chargebackReasonEntity = new ChargebackReasonEntity();
                BeanUtils.copyProperties(chargeBackReasonDTO, chargebackReasonEntity);
                chargebackReasonRepository.save(chargebackReasonEntity);
                return new ResponseMessage("1000", HttpStatus.OK);
            }

        } catch (ConstraintViolationException e) {
            logger.error("Exception Occur in saveChargeBackReason() ", e);
            e.printStackTrace();
            return new ResponseMessage("1004", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception Occur in saveChargeBackReason() ", e);
            e.printStackTrace();
            return new ResponseMessage("1003", HttpStatus.OK);
        }
    }

    @Override
    public List<ChargeBackReasonDTO> getAllChargebackReasons() {
        List<ChargeBackReasonDTO> chargeBackReasonDTOs = new ArrayList<ChargeBackReasonDTO>();
        try {
            List<ChargebackReasonEntity> chargebackReasonEntities = chargebackReasonRepository.getAllChargebackReasons();
            chargebackReasonEntities.stream().forEach(comment -> {
                ChargeBackReasonDTO dto = new ChargeBackReasonDTO();
                BeanUtils.copyProperties(comment, dto);
                chargeBackReasonDTOs.add(dto);
            });
        } catch (Exception e) {
            logger.error("Exception Occur in getAllChargebackReasons() ", e);
            e.printStackTrace();
        }
        return chargeBackReasonDTOs;

    }

    @Override
    public ChargebackReasonEntity getcbReasonDescriptionFromcbReasonCode(String cbReasonCode) {
        ChargebackReasonEntity chargebackReasonEntities = null;
        try {
            chargebackReasonEntities = chargebackReasonRepository.getcbReasonDescriptionFromcbReasonCode(cbReasonCode);

        } catch (Exception e) {
            logger.error("Exception Occur in getcbReasonDescriptionFromcbReasonCode() ", e);
            e.printStackTrace();
        }
        return chargebackReasonEntities;

    }

    @Override
    public boolean deleteChargebackReasons(long id) {
        return chargebackReasonRepository.deleteChargebackReasons(id);
    }


}
