package com.pay10.commons.user;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;

@Component
public class BankSettleDao extends HibernateAbstractDao {

	public String insert(BankSettle bankSettle) {
		super.saveOrUpdate(bankSettle);
		return "insert successfully";
	}

	@SuppressWarnings("unchecked")
	public List<BankSettle> fetchAllBankSettleDetails(String merchantEmail){
		String hql="from BankSettle where email='"+merchantEmail+"'";
		return super.findAllBy(hql);
	}
	
	public String deleteBank(long id,String sessionEmail) {
		
		BankSettle bankSettleDb= (BankSettle)super.find(BankSettle.class, id);
		bankSettleDb.setDeletedFlag(true);
		bankSettleDb.setLastUpdatedDate(new Date());
		bankSettleDb.setUpdatedBy(sessionEmail);
	
		super.saveOrUpdate(bankSettleDb);
		
		return "delete successfully";
	}
	
	public List<BankSettle> getSplitAccountDetail(String productid) {
		String hql="from BankSettle where productid='"+productid+"'";
		return super.findAllBy(hql);
	}

	public List<BankSettle> fetchAllBankSettleDetailsCurrency(String email,String bankCurrency) {
		String hql="from BankSettle where email='"+email+"' and bankCurrency='"+bankCurrency+"'";
		return super.findAllBy(hql);
	}
}
