package com.pay10.commons.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.pay10.commons.util.MopType;


@Entity
public class MopInternational implements Serializable {
	
	private static final long serialVersionUID = 3427412715511705729L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private MopType mopType;
			
	@OneToMany(targetEntity=MopTransactionInternational.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval=true)
	private Set<MopTransactionInternational> mopTransactionTypes = new HashSet<MopTransactionInternational>();
	
	public MopInternational(){
		
	}
	
	public MopInternational(MopType mopType){
		this.mopType = mopType;
	}

	public MopType getMopType() {
		return mopType;
	}

	public void setMopType(MopType mopType) {
		this.mopType = mopType;
	}
	
	public Set<MopTransactionInternational> getMopTransactionTypes() {
		return mopTransactionTypes;
	}

	public void setMopTransactionTypes(Set<MopTransactionInternational> mopTransactionTypes) {
		this.mopTransactionTypes = mopTransactionTypes;
	}
	
	public void addMopTransaction(MopTransactionInternational mopTransaction) {
		mopTransactionTypes.add(mopTransaction);
	}

	public void removeTransactionType(MopTransactionInternational mopTransaction) {
		mopTransactionTypes.remove(mopTransaction);
	}
}
