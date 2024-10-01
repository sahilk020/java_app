
package com.pay10.pg.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.camspay.util.CamsPayValidator;

@Service
public class ProcessorValidatorFactory {

    @Autowired
    private AmexValidator amexValidator;
    @Autowired
    private FssValidator fssValidator;
    @Autowired
    private EasebuzzValidator easebuzzvalidator;
    @Autowired
    private EzeeClickValidator ezeeClickValidator;
    @Autowired
    private FirstDataValidator firstDataValidator;
    @Autowired
    private FederalValidator federalValidator;
    @Autowired
    private MigsValidator migsValidator;
    @Autowired
    private IPayValidator iPayValidator;
    @Autowired
    private BobValidator bobValidator;
    @Autowired
    private AtlValidator atlValidator;
    @Autowired
    private KotakValidator kotakValidator;
    @Autowired
    private IdbiValidator idbiValidator;
    @Autowired
    private KotakUpiValidator kotakUpiValidator;
    @Autowired
    private GooglePayValidator googlePayValidator;
    @Autowired
    private YesBankCbValidator yesBnakCbValidator;
    @Autowired
    private IciciMpgsValidator iciciMpgsValidator;
    @Autowired
    private IdfcUpiValidator idfcUpivalidator;
    @Autowired
    private DirecpayValidator direcpayValidator;
    @Autowired
    private ISGPayValidator iSGPayValidator;
    @Autowired
    private AxisBankValidator axisBankValidator;
    @Autowired
    private AtomValidator apblValidator;
    @Autowired
    private ApblValidator atomValidator;
    @Autowired
    private MobikwikValidator mobikwikValidator;
    @Autowired
    private IngenicoValidator ingenicoValidator;
    
    @Autowired
    private CityUnionBankValidator cityUnionBankValidator;
    @Autowired
    private PaytmValidator paytmValidator;
    @Autowired
    private PhonePeValidator phonePeValidator;
    @Autowired
    private PayuValidator payuValidator;
    @Autowired
    private BilldeskValidator billdeskValidator;
    @Autowired
    private LyraValidator lyraValidator;
    
    @Autowired
    private PaymentEdgeValidator paymentEdgeValidator;
    @Autowired
    private SbiValidator sbiValidator;
    @Autowired
    private SbiCardValidator sbiCardValidator;
    @Autowired
    private SbiNBValidator sbiNBValidator;
    @Autowired
    private FreeChargeValidator freeChargeValidator;
    @Autowired
    private CashfreeValidator cashfreeValidator;
    @Autowired
    private AgreepayValidator agreepayValidator;
    @Autowired
    private PinelabsValidator pinelabsValidator;
    @Autowired
    private CanaraNBValidator canaraNBValidator;
    @Autowired
    private TFPValidator tfpValidator;
    @Autowired
    private TMBNBValidator tmbnbValidator;
    @Autowired
    private CamsPayValidator camsPayValidator;
    @Autowired
    private ShivaliknbBankValidator shivaliknbBankValidator;

    @Autowired
    private YesbankNBValidator yesbankNBValidator;
    
    @Autowired
    private DemoValidator demoValidator;
    
    @Autowired
   	private PaytenValidator paytenValidator;
    
    @Autowired
    private QuomoValidator quomoValidator;

    public Validator getInstance(Fields fields) {

        AcquirerType acquirer = AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()));
        if (null == acquirer) {
            return null;
        }
        switch (acquirer) {
            case AMEX:
                return amexValidator;
            case EZEECLICK:
                return ezeeClickValidator;
            case EASEBUZZ:
                return easebuzzvalidator;
            case FSS:
                return fssValidator;
            case HDFC:
                return fssValidator;
            case COSMOS:
            case MATCHMOVE:
            case WL_MOBIKWIK:
            case WL_OLAMONEY:
            case NB_SBI:
            case NB_ALLAHABAD_BANK:
            case NB_AXIS_BANK:
            case NB_FEDERAL:
            case NB_CORPORATION_BANK:
            case NB_ICICI_BANK:
            case NB_KARUR_VYSYA_BANK:
            case NB_SOUTH_INDIAN_BANK:
            case NB_VIJAYA_BANK:
            case NB_KARNATAKA_BANK:
                return iPayValidator;
            case IDFC_FIRSTDATA:
                return firstDataValidator;
            case ICICI_FIRSTDATA:
                return firstDataValidator;
            case CITYUNIONBANK:
                return cityUnionBankValidator;
            case FEDERAL:
                return federalValidator;
            case PAY10:
    			return paytenValidator;
            case AXISMIGS:
                return migsValidator;
            case BOB:
                return bobValidator;
            case IDFC:
                return axisBankValidator;
            case JAMMU_AND_KASHMIR:
                return axisBankValidator;
            case ICICI_MPGS:
                return iciciMpgsValidator;
            case ICICIBANK:
                return axisBankValidator;
            case YESBANKCB:
                return yesBnakCbValidator;
            case SHIVALIKNBBANK:
                return shivaliknbBankValidator;
            case KOTAK:
                return kotakValidator;
            case KOTAK_CARD:
    			return kotakValidator;
            case IDBIBANK:
                return idbiValidator;
            case IDFCUPI:
                return idfcUpivalidator;
            case DIRECPAY:
                return direcpayValidator;
            case SBI:
                return sbiValidator;
            case SBICARD:
                return sbiCardValidator;
            case PAYMENTAGE:
                return paymentEdgeValidator;

            case SBINB:
                return sbiNBValidator;
            case ISGPAY:
                return iSGPayValidator;
            case AXISBANK:
                return axisBankValidator;
            case ATL:
                return atlValidator;
            case ATOM:
                return atomValidator;
            case APBL:
                return apblValidator;
            case MOBIKWIK:
                return mobikwikValidator;
            case INGENICO:
                return ingenicoValidator;
            case PAYTM:
                return paytmValidator;
            case PHONEPE:
                return phonePeValidator;
            case PAYU:
                return payuValidator;
            case CANARANBBANK:
                return canaraNBValidator;
            case TFP:
                return tfpValidator;
            case TMBNB:
                return tmbnbValidator;
            case BILLDESK:
                return billdeskValidator;
            case LYRA:
                return lyraValidator;
            case FREECHARGE:
                return freeChargeValidator;
            case CASHFREE:
                return cashfreeValidator;
            case AGREEPAY:
                return agreepayValidator;
            case YESBANKNB:
                return yesbankNBValidator;
            case PINELABS:
                return pinelabsValidator;
            case CAMSPAY:
                return camsPayValidator;
            case DEMO:
                return demoValidator;
            case QUOMO:
                return quomoValidator;
                
            case HTPAY:
                return quomoValidator;
            


            /*
             * case GOOGLEPAY: return googlePayValidator;
             */
            default:
                return null;
        }
    }
}
