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

import org.hibernate.annotations.Proxy;

import com.pay10.commons.util.PaymentType;

@Entity
@Proxy(lazy = false)
public class PaymentInternational implements Serializable {


	private static final long serialVersionUID = 8202563771213589360L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@OneToMany(targetEntity = MopInternational.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<MopInternational> mops = new HashSet<MopInternational>();

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Set<MopInternational> getMops() {
		return mops;
	}

	public void setMops(Set<MopInternational> mops) {
		this.mops = mops;
	}

	public void addMop(MopInternational mop) {
		mops.add(mop);
	}

	public void removeMop(MopInternational mop) {
		mops.remove(mop);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", paymentType=" + paymentType + ", mops=" + mops + "]";
	}

}
