package com.crmws.controller;

import java.util.List;
import java.util.Map;

import com.crmws.util.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ChargeBackAutoPopulateData;
import com.crmws.dto.ChargeBackReasonDTO;
import com.crmws.dto.DMSdto;
import com.crmws.entity.ResponseMessage;
import com.crmws.exception.DateValidationException;
import com.crmws.service.ChargebackReasonService;
import com.crmws.service.DMSService;
import com.crmws.service.FileStorageService;
import com.google.gson.Gson;
import com.pay10.commons.entity.ChargebackReasonEntity;

@CrossOrigin
@RestController

public class DMSController {

    private static final Logger logger = LoggerFactory.getLogger(DMSController.class.getName());

    @Autowired
    private DMSService dmsService;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ChargebackReasonService chargebackReasonService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage> save(@RequestBody DMSdto dto, ChargeBackAutoPopulateData data) {

        ResponseMessage resp = dmsService.save(dto);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }


    @GetMapping("/getById/{id}")
    public ResponseEntity<DMSdto> getDMSdtoById(@PathVariable("id") long id) {

        logger.info("id.." + id);
        DMSdto dto = dmsService.getDMSdtoById(id);

        return new ResponseEntity<DMSdto>(dto, HttpStatus.OK);
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<DMSdto>> getDMSdto(@RequestParam(required = false) String payId) {

        List<DMSdto> dmslists = dmsService.listAll(payId);
        return new ResponseEntity<List<DMSdto>>(dmslists, HttpStatus.OK);
    }

    @RequestMapping(path = "/listChargebackStatus", method = RequestMethod.GET)
    public ResponseEntity<List<DMSdto>> listChargebackStatus(@RequestParam(required = false) String payId) {

        List<DMSdto> dmslists = dmsService.listChargebackStatus(payId);
        return new ResponseEntity<List<DMSdto>>(dmslists, HttpStatus.OK);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> update(@RequestBody DMSdto dto, @PathVariable("id") long id) {

        logger.info("status get from ui.." + dto.getStatus());

        ResponseMessage resp = dmsService.update(dto, id);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }


    @PutMapping("/accept/{id}")
    public ResponseEntity<ResponseMessage> accept(@RequestBody DMSdto dto, @PathVariable("id") long id) {

        logger.info("status get from ui.." + dto.getStatus());

        ResponseMessage resp = dmsService.accept(dto, id);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }


    @PutMapping("/close/{id}")
    public ResponseEntity<ResponseMessage> close(@RequestBody DMSdto dto, @PathVariable("id") long id) {

        logger.info("status get from ui.." + dto.getStatus());

        ResponseMessage resp = dmsService.close(dto, id);
        logger.info("Close Response : " + resp);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

    @PostMapping("/updateCbFavourMerchant/{id}")
    public ResponseEntity<ResponseMessage> updateCbFavourMerchant(@PathVariable("id") long id) {

        logger.info("ID get from ui.." + id);

        ResponseMessage resp = dmsService.updateCbFavourMerchant(id);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

    @PostMapping("/updateCbFavourBankAcquirer/{id}")
    public ResponseEntity<ResponseMessage> updateCbFavour(@PathVariable("id") long id) {

        logger.info("ID get from ui.." + id);

        ResponseMessage resp = dmsService.updateCbFavourBankAcquirer(id);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }


    @PostMapping("/uploadsheet")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {

        String message = "";

        try {
            String count = dmsService.save(file);

            return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(count, HttpStatus.OK)));
        } catch (Exception e) {
            logger.info("exception while uploading={}" + e);
            e.printStackTrace();
            message = "Could not upload the file error: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(e.getMessage(), HttpStatus.EXPECTATION_FAILED));
        } catch (DateValidationException e) {
            e.printStackTrace();
            message = "Could not upload the file error: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST));
        }

    }

    @GetMapping("/pgrefno-by-payid")
    public ResponseEntity<List<String>> getPgRefNosByPayId(@RequestParam String payId) {
        return new ResponseEntity<List<String>>(dmsService.getPgRefNosByPayId(payId), HttpStatus.OK);
    }

    @GetMapping("/by-pgrefno")
    public ResponseEntity<ChargeBackAutoPopulateData> getByPgRefNo(@RequestParam String pgRefNo) {
        return new ResponseEntity<ChargeBackAutoPopulateData>(dmsService.getByPgRefNo(pgRefNo), HttpStatus.OK);
    }

    @PostMapping("/saveChargebackReason")
    public ResponseEntity<ResponseMessage> saveChargeBackReason(@RequestBody ChargeBackReasonDTO chargeBackReasonDTO) {
        logger.info("Form value " + new Gson().toJson(chargeBackReasonDTO));
        ResponseMessage responseMessage = chargebackReasonService.saveChargeBackReason(chargeBackReasonDTO);
        return ResponseEntity.status(responseMessage.getHttpStatus()).body(responseMessage);
    }

    @GetMapping("/getAllChargebackReasons")
    public ResponseEntity<List<ChargeBackReasonDTO>> getAllChargebackReasons() {
        List<ChargeBackReasonDTO> chargeBackReasonDTOs = chargebackReasonService.getAllChargebackReasons();
        return new ResponseEntity<List<ChargeBackReasonDTO>>(chargeBackReasonDTOs, HttpStatus.OK);
    }


    @PutMapping("/deleteChargebackReasons/{id}")
    public ResponseEntity<Map<String, Object>> deleteChargebackReasons(@PathVariable("id") long id) {
        logger.info("@@@ call deleteChargebackReasons api id : {} ", id);
        boolean isDeleteChargebackReasons = chargebackReasonService.deleteChargebackReasons(id);
        if (isDeleteChargebackReasons) {
            return new ResponseEntity(MapUtils.<String, Object>of("status", HttpStatus.OK, "statusCode", HttpStatus.OK.value(), "statusMessage", HttpStatus.OK.getReasonPhrase(), "message", "Chargeback reason deleted successfully."), HttpStatus.OK);
        } else {
            return new ResponseEntity(MapUtils.<String, Object>of("status", HttpStatus.NOT_FOUND, "statusCode", HttpStatus.NOT_FOUND.value(), "statusMessage", HttpStatus.NOT_FOUND.getReasonPhrase(), "message", "Chargeback reason not found."), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/chargebackStatusBulkUpload")
    public ResponseEntity<ResponseMessage> chargebackStatusBulkUpload(@RequestParam("file") MultipartFile file) {
        logger.info("Bulk Uploading Start method name chargebackStatusBulkUpload()");
        String message = "";

        try {
            if (file.isEmpty() && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx")
                    && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsm")
                    && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")
            ) {
                return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("Please upload valid excel file .xlsx, .xlsm, .xls only.", HttpStatus.OK)));
            } else {
                message = dmsService.chargebackStatusBulkUpload(file);
            }

        } catch (Exception e) {
            logger.error("Exception Occur in chargebackStatusBulkUpload() : ", e);
            e.printStackTrace();
        }
        logger.info("Bulk Uploading Start method name chargebackStatusBulkUpload()");
        return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(message, HttpStatus.OK)));
    }

    @GetMapping("/getcbReasonDescriptionFromcbReasonCode/{cbReasonCode}")
    public ResponseEntity<ChargebackReasonEntity> getcbReasonDescriptionFromcbReasonCode(@PathVariable String cbReasonCode) {
        ChargebackReasonEntity chargeBackReasonDTOs = chargebackReasonService.getcbReasonDescriptionFromcbReasonCode(cbReasonCode);
        return new ResponseEntity<ChargebackReasonEntity>(chargeBackReasonDTOs, HttpStatus.OK);
    }
}