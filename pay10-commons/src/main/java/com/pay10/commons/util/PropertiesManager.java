package com.pay10.commons.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.FileModifiedWatcher;
import com.pay10.commons.user.SurchargeDetails;

@Service("propertiesManager")
public class PropertiesManager {

	private static Logger logger = LoggerFactory.getLogger(PropertiesManager.class.getName());
	private static final String fileLocation = System.getenv("BPGATE_PROPS");
	private static final String ymlFileLocation = System.getenv("BPGATE_PROPS");
	private static final String saltPropertiesFile = "salt.properties";
	private static final String keySaltPropertiesFile = "keysalt.properties";
	private static final String keyPropertiesFile = "key.properties";
	private static final String currencyFile = "currency.properties";
	private static final String currencyAlphabaticToNumericFile = "alphabatic-to-numeric.properties";
	private static final String emailPropertiesFile = "emailer.properties";
	public static Map<String, String> saltStore = new HashMap<String, String>();
	public static Map<String, String> keySaltStore = new HashMap<String, String>();
	private static final String currencyNameFile = "alphabatic-currencycode.properties";
	private static final String amexPropertiesFile ="amex.properties";
	private static final String subUserPermissionPropertiesFile ="subUserPermission.properties";
	private static final String subAdminPermissionPropertiesFile ="subAdminPermission.properties";
	private static final String ipaySmsPropertiesFile = "iPaySms.properties";
	private static final String recurringPaymentsPropertiesFile ="citrusPayRecurringPayments.properties";
	private static final String industrySubcategoryPropertiesFile ="industry_sub_category.properties";
	private static final String pendingRequestMessagesPropertiesFile = "pendingRequestMessages.properties";
	private static final String testPropertiesFile = "test.properties";
	public static Map<String, String> propertiesMap = new HashMap<String, String>(); 
	public static Map<String, String> acquirerMap = new HashMap<String, String>(); 
	public static Map<String, SurchargeDetails> surchargeMap = new HashMap<String, SurchargeDetails>(); 
	private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

	public PropertiesManager() {
		
		if (propertiesMap.size() < 1) {
			
			System.out.println("THIS IS INSIDE CONSTRUCTOR");
			Resource resource = new FileSystemResource(ymlFileLocation+"application.yml");
			List<PropertySource<?>> propertySource = loadYaml(resource);
			
			for (PropertySource ps : propertySource ) {
				
				
                if (ps instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) ps).getPropertyNames()) {
                        if (key != null && ps.getProperty(key) != null) {
                        	PropertiesManager.propertiesMap.put(key, ps.getProperty(key).toString());
                        }
                    }
                }
		}
			
			FileModifiedWatcher.init(ymlFileLocation+"application.yml",300);
		}
		
		// Initialize poller to update properties file when application.yml is updated
		
	}
	
	private List<PropertySource<?>> loadYaml(Resource path) {
		if (!path.exists()) {
			throw new IllegalArgumentException("Resource " + path + " does not exist");
		}
		try {
			return this.loader.load("custom-resource", path);
		}
		catch (IOException ex) {
			throw new IllegalStateException(
					"Failed to load yaml configuration from " + path, ex);
		}
	}

/*	public String getAzureKeyVaultProperties(String key){
		return getProperty(key, azureKeyVaultProperties);
	}*/
	
	public String getKey(String key){
		return getProperty(key, keyPropertiesFile);
	}

	public String getSystemProperty(String key){
		return propertiesMap.get(key);
	}

	public String getEmailProperty(String key){
		return getProperty(key, emailPropertiesFile);
	}

	public String getResetPasswordProperty(String key){
		return getProperty(key, emailPropertiesFile);
	}
	
/*	public String getCurrencyPlaces(String currencyCode){
		return getProperty(currencyCode, currencyFile);
	}
*/
	public String getIpaySmsPropertiesFile(String key){
		return getProperty(key, ipaySmsPropertiesFile);
	}
	
	public String getAmexProperty(String key){
		return getProperty(key, amexPropertiesFile);
	}

	public String getSubUserPermissionProperty(String key){
		return getProperty(key, subUserPermissionPropertiesFile);
	}

	public String getSuAdminPermissionProperty(String key){
		return getProperty(key, subAdminPermissionPropertiesFile);
	}
	
	public String getAlphabaticCurrencyCode(String numericCurrencyCode){
		return getProperty(numericCurrencyCode, currencyNameFile);
	}

	public String getNumericCurrencyCode(String alphabeticCode){
		return getProperty(alphabeticCode, currencyAlphabaticToNumericFile);
	}

	/*public String getAcquirerMopType(String key){
		return getProperty(key, acquirerMop);
	}*/

/*	public String getNetbankingProperty(String key){
		return getProperty(key, netbankingPropertiesFile);
	}*/

	public String getCitrusRecurringPaymentProperty(String key){
		return getProperty(key, recurringPaymentsPropertiesFile);
	}

	public String getIndustrySubcategories(String category){
		return getProperty(category, industrySubcategoryPropertiesFile);
	}
   /* public String getMisReports(String file ){
        return getProperty(file, misReportPropertiesFile);
    }
    
    public String getMisAllAcquireExportCSV(String file ){
        return getProperty(file, exportMisReportPropertiesFile);
    }
    
    public String getservicesURL(String key ){
        return getProperty(key, servicesURLPropertiesFile);
    }*/
    
    public String getPendingMessages(String key ){
        return getProperty(key, pendingRequestMessagesPropertiesFile);
    }
    
  /*  public String getmongoDbParam(String key ){
    	String value = propertiesMap.get(key);
		if(null==value) {
			return getProperty(key, mongoDBPropertiesFile);
		}
        return value;
    }*/
    public String getTestParam(String key ){
        return getProperty(key, testPropertiesFile);
    }

	/*public String getIpayAcquirerProperty(String key){
		return getProperty(key, ipayAcquirerPropertiesFile);
	}*/
	
	public String getSalt(String payId){
		
		try{
		String salt = saltStore.get(payId);
		
		if(null == salt || salt.isEmpty()){
			
			salt = getProperty(payId, saltPropertiesFile);
			if(null != salt && !salt.isEmpty()){
				saltStore.put(payId, salt);
				return salt;
			}
		}
		return salt;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.info("Salt not found ");
		}
	
		return null;
	}
	
	public String getKeySalt(String payId){
		
		try{
		String salt = keySaltStore.get(payId);
		
		if(null == salt || salt.isEmpty()){
			
			salt = getProperty(payId, keySaltPropertiesFile);
			if(null != salt && !salt.isEmpty()){
				keySaltStore.put(payId, salt);
				return salt;
			}
		}
		return salt;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.info("Salt not found ");
		}
	
		return null;
	}
	
	public boolean setKey(String key, String value){
		return setProperty(key, value, keyPropertiesFile);
	}
	
	protected boolean setProperty(String key, String value, String fileName) {

		boolean result = true;
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			
			FileInputStream input = new FileInputStream(fileLocation+fileName);
			prop.load(input);
			input.close();
			
			output = new FileOutputStream(fileLocation+fileName);
			prop.setProperty(key, value);
			
			prop.store(output, null);

		} catch (IOException ioException) {
			logger.error("Unable to update properties file = " + fileName + ", Details = " + ioException.getMessage(), ioException);			
			result = false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException ioException) {
					logger.error("Unable to update properties file = " + fileName + ", Details = " + ioException.getMessage(), ioException);
					result = false;
				}
			}
		}
		
		return result;
	}

	public Map<String, String> getAllProperties(String fileName){
		Map<String, String> responseMap = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(fileLocation+fileName);

			// load a properties file
			prop.load(input);
			
			for(Object key : prop.keySet()){
				responseMap.put(key.toString(), prop.getProperty(key.toString()));
			}

		} catch (IOException ioException) {
			logger.error("Unable to update properties file = " + fileName + ", Details = " + ioException.getMessage(), ioException);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioException) {
					logger.error("Unable to update properties file = " + fileName + ", Details = " + ioException.getMessage(), ioException);
				}
			}
		}

		return responseMap;
	}//getAllProperties()

	private String getProperty(String key, String fileName) {

		Properties prop = new Properties();
		InputStream input = null;
		String value = null;

		try {

			input = new FileInputStream(fileLocation+fileName);
			prop.load(input);
			value = prop.getProperty(key);

		} catch (IOException ioException) {
			logger.error("Unable to update properties file = " + fileName + ", Details = " + ioException.getMessage(), ioException);
		} catch(NullPointerException npe){
			logger.error("property file error " + npe);
		}
			finally {
		
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioException) {
					logger.error("Unable to update properties file = " + fileName + ", Details = " + ioException.getMessage(), ioException);
				}
			}
		}
		propertiesMap.put(key, value);
		return value;
	}

/*	public static String getSystempropertiesfile() {
		return systemPropertiesFile;
	}*/

	public static String getSaltpropertiesfile() {
		return saltPropertiesFile;
	}

	public static String getKeypropertiesfile() {
		return keyPropertiesFile;
	}

	public static String getCurrencyfile() {
		return currencyFile;
	}

	public static String getEmailpropertiesfile() {
		return emailPropertiesFile;
	}

	public static String getAmexpropertiesfile() {
		return amexPropertiesFile;
	}

	public static String getSubuserpermissionpropertiesfile() {
		return subUserPermissionPropertiesFile;
	}

	/*public static String getNetbankingpropertiesfile() {
		return netbankingPropertiesFile;
	}

	public static String getServicesurlpropertiesfile() {
		return servicesURLPropertiesFile;
	}

	public static String getIpayacquirerpropertiesfile() {
		return ipayAcquirerPropertiesFile;
	}
	*/
}
