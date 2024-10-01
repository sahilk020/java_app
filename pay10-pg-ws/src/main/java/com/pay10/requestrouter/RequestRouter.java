package com.pay10.requestrouter;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;

//@Service
public class RequestRouter {

	@Autowired
	@Qualifier("securityProcessor")
	private Processor securityProcessor;

	@Autowired
	@Qualifier("historyProcessor")
	private Processor historyProcessor;

	@Autowired
	@Qualifier("hdfcProcessor")
	private Processor hdfcProcessor;

	
	@Autowired
	@Qualifier("cityunionProcessor")
	private Processor cityUnionProcessor;
	
	
	@Autowired
	@Qualifier("fssProcessor")
	private Processor fssProcessor;

	@Autowired
	@Qualifier("amexProcessor")
	private Processor amexProcessor;

	@Autowired
	@Qualifier("ezeeClickProcessor")
	private Processor ezeeClickProcessor;

	@Autowired
	@Qualifier("freeChargeProcessor")
	private Processor freeChargeProcessor;

	@Autowired
	@Qualifier("firstDataProcessor")
	private Processor firstDataProcessor;

	@Autowired
	@Qualifier("cosmosProcessor")
	private Processor CosmosProcessor;
	
	
	@Autowired
	@Qualifier("federalProcessor")
	private Processor federalProcessor;

	@Autowired
	@Qualifier("ipayProcessor")
	private Processor ipayProcessor;

	@Autowired
	@Qualifier("sbiProcessor")
	private Processor sbiProcessor;

	@Autowired
	@Qualifier("sbiCardProcessor")
	private Processor sbiCardProcessor;

	@Autowired
	@Qualifier("sbiNBProcessor")
	private Processor sbiNBProcessor;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("merchantHostedProcessor")
	private Processor merchantHostedProcessor;

	@Autowired
	@Qualifier("fraudProcessor")
	private Processor fraudProcessor;

	@Autowired
	@Qualifier("axisProcessor")
	private Processor axisProcessor;

	@Autowired
	@Qualifier("bobProcessor")
	private Processor bobProcessor;

	@Autowired
	@Qualifier("cSourceProcessor")
	private Processor cSourceProcessor;

	@Autowired
	@Qualifier("kotakProcessor")
	private Processor kotakProcessor;

	@Autowired
	@Qualifier("iciciMpgsProcessor")
	private Processor iciciMpgsProcessor;

	@Autowired
	@Qualifier("idbiProcessor")
	private Processor idbiProcessor;

	@Autowired
	@Qualifier("idfcUpiProcessor")
	private Processor idfcUpiProcessor;

	@Autowired
	@Qualifier("idfcProcessor")
	private Processor idfcProcessor;
	
	@Autowired
	@Qualifier("JammuAndkashmirProcessor")
	private Processor JammuAndkashmirProcessor;

	
	@Autowired
	@Qualifier("atlProcessor")
	private Processor atlProcessor;

	@Autowired
	@Qualifier("googlePayProcessor")
	private Processor googlePayProcessor;

	@Autowired
	@Qualifier("matchMoveProcessor")
	private Processor matchMoveProcessor;

	@Autowired
	@Qualifier("direcpayProcessor")
	private Processor direcpayProcessor;

	@Autowired
	@Qualifier("isgPayProcessor")
	private Processor iSGPayProcessor;

	@Autowired
	@Qualifier("axisBankProcessor")
	private Processor axisBankProcessor;

	@Autowired
	@Qualifier("atomProcessor")
	private Processor atomProcessor;

	@Autowired
	@Qualifier("apblProcessor")
	private Processor apblProcessor;

	@Autowired
	@Qualifier("mobikwikProcessor")
	private Processor mobikwikProcessor;

	@Autowired
	@Qualifier("ingenicoProcessor")
	private Processor ingenicoProcessor;
	@Autowired
	@Qualifier("TMBNBProcessor")
	private Processor TMBNBProcessor;
	@Autowired
	@Qualifier("TFPProcessor")
	private Processor TFPProcessor;

	
	@Autowired
	@Qualifier("quomoProcessor")
	private Processor quomoProcessor;

	@Autowired
	@Qualifier("paytmProcessor")
	private Processor paytmProcessor;

	@Autowired
	@Qualifier("phonepeProcessor")
	private Processor phonepeProcessor;
	
	
	@Autowired
	@Qualifier("htpayProcessor")
	private Processor htpayProcessor;

	@Autowired
	@Qualifier("CanaraNBProcessor")
	private Processor CanaraNBProcessor;
		
	@Autowired
	@Qualifier("payuProcessor")
	private Processor payuProcessor;


	@Autowired
	@Qualifier("billdeskProcessor")
	private Processor billdeskProcessor;

	@Autowired
	@Qualifier("lyraProcessor")
	private Processor lyraProcessor;

	@Autowired
	@Qualifier("iciciBankNBProcessor")
	private Processor iciciBankNBProcessor;

	@Autowired
	@Qualifier("cashfreeProcessor")
	private Processor cashfreeProcessor;
	
	@Autowired
	@Qualifier("paytenProcessor")
	private Processor paytenProcessor;
	

	@Autowired
	@Qualifier("paymentEdgeProcessor")
	private Processor paymentEdgeProcessor;

	@Autowired
	@Qualifier("easebuzzProcessor")
	private Processor easebuzzProcessor;
	@Autowired
	@Qualifier("agreepayProcessor")
	private Processor agreepayProcessor;

	@Autowired
	@Qualifier("kotakProcessorCard")
	private Processor kotakProcessorCards;
	
	@Autowired
	@Qualifier("federalNBProcess")
	private Processor federalNBProcess;

	@Autowired
	@Qualifier("yesbankNBProcess")
	private Processor yesbankNBProcessor;


	@Autowired
	@Qualifier("pinelabsProcessor")
	private Processor pinelabsProcessor;

	@Autowired
	@Qualifier("camsPayProcessor")
	private Processor camsPayProcessor;
	
	@Autowired
	@Qualifier("demoProcessor")
	private Processor demoProcessor;
	@Autowired
	@Qualifier("SHIVALIKNBProcessor")
	private Processor shivalikProcessor;


	@Autowired
	private ResponseCreator responseCreator;

	Fields fields = null;

	public RequestRouter(Fields fields) {
		this.fields = fields;
	}

	public Map<String, String> route(Fields fields) {

		String shopifyFlag = fields.get(FieldType.INTERNAL_SHOPIFY_YN.getName());
		String flagIrctc = fields.get(FieldType.INTERNAL_IRCTC_YN.getName());

		// Process security
		ProcessManager.flow(securityProcessor, fields, false);

		// fraud Prevention processor
		ProcessManager.flow(fraudProcessor, fields, false);

		// History Processor
		ProcessManager.flow(historyProcessor, fields, false);

		// Insert new order
		ProcessManager.flow(merchantHostedProcessor, fields, false);

		// Process transaction with HDFC
		ProcessManager.flow(hdfcProcessor, fields, false);
		
		ProcessManager.flow(paymentEdgeProcessor, fields, false);


		// Process transaction with AMEX
		ProcessManager.flow(amexProcessor, fields, false);

		// Process transaction with FirstData
		ProcessManager.flow(firstDataProcessor, fields, false);
		
		
		ProcessManager.flow(paytenProcessor, fields, false);


		// Process transaction with FEDERAL
		ProcessManager.flow(federalProcessor, fields, false);

		ProcessManager.flow(quomoProcessor, fields, false);

		// Process transaction with BOB
		ProcessManager.flow(bobProcessor, fields, false);
		ProcessManager.flow(cityUnionProcessor, fields, false);

		// Process transaction with IDBI
		ProcessManager.flow(idbiProcessor, fields, false);
		ProcessManager.flow(idfcProcessor, fields, false);//added my abhi
		ProcessManager.flow(JammuAndkashmirProcessor, fields, false); // added by abhi

		// Process transaction with IDBIUPI
		ProcessManager.flow(idfcUpiProcessor, fields, false);

		// Process transaction with GOOGLEPAY
		ProcessManager.flow(googlePayProcessor, fields, false);

		// Process transaction with YESBANKCB
		ProcessManager.flow(cSourceProcessor, fields, false);

		// Process transaction with EzeeClick AMEX
		ProcessManager.flow(ezeeClickProcessor, fields, false);
		ProcessManager.flow(CanaraNBProcessor, fields, false);

		// Process transaction with IPay
		ProcessManager.flow(ipayProcessor, fields, false);

		// Process transaction with AXIS
		ProcessManager.flow(axisProcessor, fields, false);

		// Process transaction with KOTAK
		ProcessManager.flow(kotakProcessor, fields, false);

		// Process transaction with ICICI MPGS
		ProcessManager.flow(iciciMpgsProcessor, fields, false);

		// Process transaction with Auodebebit
		ProcessManager.flow(atlProcessor, fields, false);
		
		ProcessManager.flow(kotakProcessorCards, fields, false);


		// Process transaction with matchMmoveProcessor
		ProcessManager.flow(matchMoveProcessor, fields, false);
		
		
		ProcessManager.flow(kotakProcessorCards, fields, false);


		// Process transaction with direcpayProcessor
		ProcessManager.flow(direcpayProcessor, fields, false);

		// Process transaction with iSGPayProcessor
		ProcessManager.flow(iSGPayProcessor, fields, false);

		// Process transaction with FSS
		ProcessManager.flow(fssProcessor, fields, false);

		// Process transaction with Axis Bank
		ProcessManager.flow(axisBankProcessor, fields, false);

		// Process transaction with ATOM
		ProcessManager.flow(atomProcessor, fields, false);

		// Process transaction with APBL
		ProcessManager.flow(apblProcessor, fields, false);

		// Process transaction with Mobikwik
		ProcessManager.flow(mobikwikProcessor, fields, false);

		// Process transaction with Ingenico
		ProcessManager.flow(ingenicoProcessor, fields, false);

		// Process transaction with Paytm
		ProcessManager.flow(paytmProcessor, fields, false);

		// Process transaction with FREECHARGE
		ProcessManager.flow(freeChargeProcessor, fields, false);
		ProcessManager.flow(CosmosProcessor, fields, false);

		// Process transaction with SBI
		ProcessManager.flow(sbiProcessor, fields, false);

		// Process transaction with SBI-Card
		ProcessManager.flow(sbiCardProcessor, fields, false);

		// Process transaction with SBI-Netbanking
		ProcessManager.flow(sbiNBProcessor, fields, false);

		// Process transaction with Phone Pe
		ProcessManager.flow(phonepeProcessor, fields, false);

		// Process transaction with Payu
		ProcessManager.flow(payuProcessor, fields, false);

		// Process transaction with Billdesk
		ProcessManager.flow(billdeskProcessor, fields, false);

		// Process transaction with Lyra
		ProcessManager.flow(lyraProcessor, fields, false);

		// Process transaction with ICICI Bank
		ProcessManager.flow(iciciBankNBProcessor, fields, false);

		// Process transaction with Cashfree
		ProcessManager.flow(cashfreeProcessor, fields, false);

		// Process transaction with Easebuzz
		ProcessManager.flow(easebuzzProcessor, fields, false);
		
		ProcessManager.flow(htpayProcessor, fields, false);


		// Process transaction with Agreepay
		ProcessManager.flow(agreepayProcessor, fields, false);

		// Process transaction with Federal Bank NB
		ProcessManager.flow(federalNBProcess, fields, false);

		// Process transaction with YesbankNB
		ProcessManager.flow(yesbankNBProcessor, fields, false);

		// Process transaction with Pinelabs
		ProcessManager.flow(pinelabsProcessor, fields, false);
		ProcessManager.flow(TMBNBProcessor, fields, false);
		// Process transaction with Camspay
		ProcessManager.flow(camsPayProcessor, fields, false);
		ProcessManager.flow(TFPProcessor, fields, false);

		ProcessManager.flow(demoProcessor, fields, false);

		ProcessManager.flow(demoProcessor, fields, false);
		ProcessManager.flow(shivalikProcessor, fields, false);
		// Update processor
		ProcessManager.flow(updateProcessor, fields, true);

		// Generate response for user
		createResponse(fields);

		if (!StringUtils.isEmpty(shopifyFlag)) {
			fields.put(FieldType.INTERNAL_SHOPIFY_YN.getName(), shopifyFlag);
		} else if (!StringUtils.isEmpty(flagIrctc)) {
			fields.put(FieldType.INTERNAL_IRCTC_YN.getName(), flagIrctc);
		}
		return fields.getFields();
	}

	public void createResponse(Fields fields) {
		responseCreator.create(fields);
	}
}
