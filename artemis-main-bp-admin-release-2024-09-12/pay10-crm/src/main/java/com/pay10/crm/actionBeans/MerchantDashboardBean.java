package com.pay10.crm.actionBeans;


public class MerchantDashboardBean {
	private long total_cc_count;
	private long total_dc_count;
	private long total_nb_count;
	private long total_wallet_count;
	private long total_upi_count;
	private long total_null_count;
	private long total_total_count;
	private double total_cc_amount;
	private double total_dc_amount;
	private double total_nb_amount;
	private double total_wallet_amount;
	private double total_upi_amount;
	private double total_null_amount;
	private double total_total_amount;
	
	
	private long capture_cc_count;
	private long capture_dc_count;
	private long capture_nb_count;
	private long capture_wallet_count;
	private long capture_upi_count;
	private long capture_total_count;
	private double capture_cc_amount;
	private double capture_dc_amount;
	private double capture_nb_amount;
	private double capture_wallet_amount;
	private double capture_upi_amount;
	private double capture_total_amount;
	
	private long pending_cc_count;
	private long pending_dc_count;
	private long pending_nb_count;
	private long pending_wallet_count;
	private long pending_upi_count;
	private long pending_null_count;
	private long pending_total_count;
	private double pending_cc_amount;
	private double pending_dc_amount;
	private double pending_nb_amount;
	private double pending_wallet_amount;
	private double pending_upi_amount;
	private double pending_null_amount;
	private double pending_total_amount;
	
	
	private long harddecline_cc_count;
	private long harddecline_dc_count;
	private long harddecline_nb_count;
	private long harddecline_wallet_count;
	private long harddecline_upi_count;
	private long harddecline_null_count;
	private long harddecline_total_count;
	private double harddecline_cc_amount;
	private double harddecline_dc_amount;
	private double harddecline_nb_amount;
	private double harddecline_wallet_amount;
	private double harddecline_upi_amount;
	private double harddecline_null_amount;
	private double harddecline_total_amount;
	
	
	private long softdecline_cc_count;
	private long softdecline_dc_count;
	private long softdecline_nb_count;
	private long softdecline_wallet_count;
	private long softdecline_upi_count;
	private long softdecline_null_count;
	private long softdecline_total_count;
	private double softdecline_cc_amount;
	private double softdecline_dc_amount;
	private double softdecline_nb_amount;
	private double softdecline_wallet_amount;
	private double softdecline_upi_amount;
	private double softdecline_null_amount;
	private double softdecline_total_amount;
	
	private long summary_nb_capture_count;
	private long summary_nb_pending_count;
	private long summary_nb_hard_decline_count;
	private long summary_nb_soft_decline_count;
	private double summary_nb_capture_volume;
	private double summary_nb_pending_volume;
	private double summary_nb_hard_decline_volume;
	private double summary_nb_soft_decline_volume;
	
	private long summary_upi_capture_count;
	private long summary_upi_pending_count;
	private long summary_upi_hard_decline_count;
	private long summary_upi_soft_decline_count;
	private double summary_upi_capture_volume;
	private double summary_upi_pending_volume;
	private double summary_upi_hard_decline_volume;
	private double summary_upi_soft_decline_volume;
	
	private long summary_cc_dc_capture_count;
	private long summary_cc_dc_pending_count;
	private long summary_cc_dc_hard_decline_count;
	private long summary_cc_dc_soft_decline_count;
	private double summary_cc_dc_capture_volume;
	private double summary_cc_dc_pending_volume;
	private double summary_cc_dc_hard_decline_volume;
	private double summary_cc_dc_soft_decline_volume;
	
	private long summary_wallet_capture_count;
	private long summary_wallet_pending_count;
	private long summary_wallet_hard_decline_count;
	private long summary_wallet_soft_decline_count;
	private double summary_wallet_capture_volume;
	private double summary_wallet_pending_volume;
	private double summary_wallet_hard_decline_volume;
	private double summary_wallet_soft_decline_volume;
	
	private long summary_overall_capture_count;
	private long summary_overall_pending_count;
	private long summary_overall_hard_decline_count;
	private long summary_overall_soft_decline_count;
	private double summary_overall_capture_volume;
	private double summary_overall_pending_volume;
	private double summary_overall_hard_decline_volume;
	private double summary_overall_soft_decline_volume;
	public long getTotal_cc_count() {
		return total_cc_count;
	}
	public void setTotal_cc_count(long total_cc_count) {
		this.total_cc_count = total_cc_count;
	}
	public long getTotal_dc_count() {
		return total_dc_count;
	}
	public void setTotal_dc_count(long total_dc_count) {
		this.total_dc_count = total_dc_count;
	}
	public long getTotal_nb_count() {
		return total_nb_count;
	}
	public void setTotal_nb_count(long total_nb_count) {
		this.total_nb_count = total_nb_count;
	}
	public long getTotal_wallet_count() {
		return total_wallet_count;
	}
	public void setTotal_wallet_count(long total_wallet_count) {
		this.total_wallet_count = total_wallet_count;
	}
	public long getTotal_upi_count() {
		return total_upi_count;
	}
	public void setTotal_upi_count(long total_upi_count) {
		this.total_upi_count = total_upi_count;
	}
	public long getTotal_null_count() {
		return total_null_count;
	}
	public void setTotal_null_count(long total_null_count) {
		this.total_null_count = total_null_count;
	}
	public long getTotal_total_count() {
		return total_total_count;
	}
	public void setTotal_total_count(long total_total_count) {
		this.total_total_count = total_total_count;
	}
	public double getTotal_cc_amount() {
		return total_cc_amount;
	}
	public void setTotal_cc_amount(double total_cc_amount) {
		this.total_cc_amount = total_cc_amount;
	}
	public double getTotal_dc_amount() {
		return total_dc_amount;
	}
	public void setTotal_dc_amount(double total_dc_amount) {
		this.total_dc_amount = total_dc_amount;
	}
	public double getTotal_nb_amount() {
		return total_nb_amount;
	}
	public void setTotal_nb_amount(double total_nb_amount) {
		this.total_nb_amount = total_nb_amount;
	}
	public double getTotal_wallet_amount() {
		return total_wallet_amount;
	}
	public void setTotal_wallet_amount(double total_wallet_amount) {
		this.total_wallet_amount = total_wallet_amount;
	}
	public double getTotal_upi_amount() {
		return total_upi_amount;
	}
	public void setTotal_upi_amount(double total_upi_amount) {
		this.total_upi_amount = total_upi_amount;
	}
	public double getTotal_null_amount() {
		return total_null_amount;
	}
	public void setTotal_null_amount(double total_null_amount) {
		this.total_null_amount = total_null_amount;
	}
	public double getTotal_total_amount() {
		return total_total_amount;
	}
	public void setTotal_total_amount(double total_total_amount) {
		this.total_total_amount = total_total_amount;
	}
	public long getCapture_cc_count() {
		return capture_cc_count;
	}
	public void setCapture_cc_count(long capture_cc_count) {
		this.capture_cc_count = capture_cc_count;
	}
	public long getCapture_dc_count() {
		return capture_dc_count;
	}
	public void setCapture_dc_count(long capture_dc_count) {
		this.capture_dc_count = capture_dc_count;
	}
	public long getCapture_nb_count() {
		return capture_nb_count;
	}
	public void setCapture_nb_count(long capture_nb_count) {
		this.capture_nb_count = capture_nb_count;
	}
	public long getCapture_wallet_count() {
		return capture_wallet_count;
	}
	public void setCapture_wallet_count(long capture_wallet_count) {
		this.capture_wallet_count = capture_wallet_count;
	}
	public long getCapture_upi_count() {
		return capture_upi_count;
	}
	public void setCapture_upi_count(long capture_upi_count) {
		this.capture_upi_count = capture_upi_count;
	}
	public long getCapture_total_count() {
		return capture_total_count;
	}
	public void setCapture_total_count(long capture_total_count) {
		this.capture_total_count = capture_total_count;
	}
	public double getCapture_cc_amount() {
		return capture_cc_amount;
	}
	public void setCapture_cc_amount(double capture_cc_amount) {
		this.capture_cc_amount = capture_cc_amount;
	}
	public double getCapture_dc_amount() {
		return capture_dc_amount;
	}
	public void setCapture_dc_amount(double capture_dc_amount) {
		this.capture_dc_amount = capture_dc_amount;
	}
	public double getCapture_nb_amount() {
		return capture_nb_amount;
	}
	public void setCapture_nb_amount(double capture_nb_amount) {
		this.capture_nb_amount = capture_nb_amount;
	}
	public double getCapture_wallet_amount() {
		return capture_wallet_amount;
	}
	public void setCapture_wallet_amount(double capture_wallet_amount) {
		this.capture_wallet_amount = capture_wallet_amount;
	}
	public double getCapture_upi_amount() {
		return capture_upi_amount;
	}
	public void setCapture_upi_amount(double capture_upi_amount) {
		this.capture_upi_amount = capture_upi_amount;
	}
	public double getCapture_total_amount() {
		return capture_total_amount;
	}
	public void setCapture_total_amount(double capture_total_amount) {
		this.capture_total_amount = capture_total_amount;
	}
	public long getPending_cc_count() {
		return pending_cc_count;
	}
	public void setPending_cc_count(long pending_cc_count) {
		this.pending_cc_count = pending_cc_count;
	}
	public long getPending_dc_count() {
		return pending_dc_count;
	}
	public void setPending_dc_count(long pending_dc_count) {
		this.pending_dc_count = pending_dc_count;
	}
	public long getPending_nb_count() {
		return pending_nb_count;
	}
	public void setPending_nb_count(long pending_nb_count) {
		this.pending_nb_count = pending_nb_count;
	}
	public long getPending_wallet_count() {
		return pending_wallet_count;
	}
	public void setPending_wallet_count(long pending_wallet_count) {
		this.pending_wallet_count = pending_wallet_count;
	}
	public long getPending_upi_count() {
		return pending_upi_count;
	}
	public void setPending_upi_count(long pending_upi_count) {
		this.pending_upi_count = pending_upi_count;
	}
	public long getPending_null_count() {
		return pending_null_count;
	}
	public void setPending_null_count(long pending_null_count) {
		this.pending_null_count = pending_null_count;
	}
	public long getPending_total_count() {
		return pending_total_count;
	}
	public void setPending_total_count(long pending_total_count) {
		this.pending_total_count = pending_total_count;
	}
	public double getPending_cc_amount() {
		return pending_cc_amount;
	}
	public void setPending_cc_amount(double pending_cc_amount) {
		this.pending_cc_amount = pending_cc_amount;
	}
	public double getPending_dc_amount() {
		return pending_dc_amount;
	}
	public void setPending_dc_amount(double pending_dc_amount) {
		this.pending_dc_amount = pending_dc_amount;
	}
	public double getPending_nb_amount() {
		return pending_nb_amount;
	}
	public void setPending_nb_amount(double pending_nb_amount) {
		this.pending_nb_amount = pending_nb_amount;
	}
	public double getPending_wallet_amount() {
		return pending_wallet_amount;
	}
	public void setPending_wallet_amount(double pending_wallet_amount) {
		this.pending_wallet_amount = pending_wallet_amount;
	}
	public double getPending_upi_amount() {
		return pending_upi_amount;
	}
	public void setPending_upi_amount(double pending_upi_amount) {
		this.pending_upi_amount = pending_upi_amount;
	}
	public double getPending_null_amount() {
		return pending_null_amount;
	}
	public void setPending_null_amount(double pending_null_amount) {
		this.pending_null_amount = pending_null_amount;
	}
	public double getPending_total_amount() {
		return pending_total_amount;
	}
	public void setPending_total_amount(double pending_total_amount) {
		this.pending_total_amount = pending_total_amount;
	}
	public long getHarddecline_cc_count() {
		return harddecline_cc_count;
	}
	public void setHarddecline_cc_count(long harddecline_cc_count) {
		this.harddecline_cc_count = harddecline_cc_count;
	}
	public long getHarddecline_dc_count() {
		return harddecline_dc_count;
	}
	public void setHarddecline_dc_count(long harddecline_dc_count) {
		this.harddecline_dc_count = harddecline_dc_count;
	}
	public long getHarddecline_nb_count() {
		return harddecline_nb_count;
	}
	public void setHarddecline_nb_count(long harddecline_nb_count) {
		this.harddecline_nb_count = harddecline_nb_count;
	}
	public long getHarddecline_wallet_count() {
		return harddecline_wallet_count;
	}
	public void setHarddecline_wallet_count(long harddecline_wallet_count) {
		this.harddecline_wallet_count = harddecline_wallet_count;
	}
	public long getHarddecline_upi_count() {
		return harddecline_upi_count;
	}
	public void setHarddecline_upi_count(long harddecline_upi_count) {
		this.harddecline_upi_count = harddecline_upi_count;
	}
	public long getHarddecline_null_count() {
		return harddecline_null_count;
	}
	public void setHarddecline_null_count(long harddecline_null_count) {
		this.harddecline_null_count = harddecline_null_count;
	}
	public long getHarddecline_total_count() {
		return harddecline_total_count;
	}
	public void setHarddecline_total_count(long harddecline_total_count) {
		this.harddecline_total_count = harddecline_total_count;
	}
	public double getHarddecline_cc_amount() {
		return harddecline_cc_amount;
	}
	public void setHarddecline_cc_amount(double harddecline_cc_amount) {
		this.harddecline_cc_amount = harddecline_cc_amount;
	}
	public double getHarddecline_dc_amount() {
		return harddecline_dc_amount;
	}
	public void setHarddecline_dc_amount(double harddecline_dc_amount) {
		this.harddecline_dc_amount = harddecline_dc_amount;
	}
	public double getHarddecline_nb_amount() {
		return harddecline_nb_amount;
	}
	public void setHarddecline_nb_amount(double harddecline_nb_amount) {
		this.harddecline_nb_amount = harddecline_nb_amount;
	}
	public double getHarddecline_wallet_amount() {
		return harddecline_wallet_amount;
	}
	public void setHarddecline_wallet_amount(double harddecline_wallet_amount) {
		this.harddecline_wallet_amount = harddecline_wallet_amount;
	}
	public double getHarddecline_upi_amount() {
		return harddecline_upi_amount;
	}
	public void setHarddecline_upi_amount(double harddecline_upi_amount) {
		this.harddecline_upi_amount = harddecline_upi_amount;
	}
	public double getHarddecline_null_amount() {
		return harddecline_null_amount;
	}
	public void setHarddecline_null_amount(double harddecline_null_amount) {
		this.harddecline_null_amount = harddecline_null_amount;
	}
	public double getHarddecline_total_amount() {
		return harddecline_total_amount;
	}
	public void setHarddecline_total_amount(double harddecline_total_amount) {
		this.harddecline_total_amount = harddecline_total_amount;
	}
	public long getSoftdecline_cc_count() {
		return softdecline_cc_count;
	}
	public void setSoftdecline_cc_count(long softdecline_cc_count) {
		this.softdecline_cc_count = softdecline_cc_count;
	}
	public long getSoftdecline_dc_count() {
		return softdecline_dc_count;
	}
	public void setSoftdecline_dc_count(long softdecline_dc_count) {
		this.softdecline_dc_count = softdecline_dc_count;
	}
	public long getSoftdecline_nb_count() {
		return softdecline_nb_count;
	}
	public void setSoftdecline_nb_count(long softdecline_nb_count) {
		this.softdecline_nb_count = softdecline_nb_count;
	}
	public long getSoftdecline_wallet_count() {
		return softdecline_wallet_count;
	}
	public void setSoftdecline_wallet_count(long softdecline_wallet_count) {
		this.softdecline_wallet_count = softdecline_wallet_count;
	}
	public long getSoftdecline_upi_count() {
		return softdecline_upi_count;
	}
	public void setSoftdecline_upi_count(long softdecline_upi_count) {
		this.softdecline_upi_count = softdecline_upi_count;
	}
	public long getSoftdecline_null_count() {
		return softdecline_null_count;
	}
	public void setSoftdecline_null_count(long softdecline_null_count) {
		this.softdecline_null_count = softdecline_null_count;
	}
	public long getSoftdecline_total_count() {
		return softdecline_total_count;
	}
	public void setSoftdecline_total_count(long softdecline_total_count) {
		this.softdecline_total_count = softdecline_total_count;
	}
	public double getSoftdecline_cc_amount() {
		return softdecline_cc_amount;
	}
	public void setSoftdecline_cc_amount(double softdecline_cc_amount) {
		this.softdecline_cc_amount = softdecline_cc_amount;
	}
	public double getSoftdecline_dc_amount() {
		return softdecline_dc_amount;
	}
	public void setSoftdecline_dc_amount(double softdecline_dc_amount) {
		this.softdecline_dc_amount = softdecline_dc_amount;
	}
	public double getSoftdecline_nb_amount() {
		return softdecline_nb_amount;
	}
	public void setSoftdecline_nb_amount(double softdecline_nb_amount) {
		this.softdecline_nb_amount = softdecline_nb_amount;
	}
	public double getSoftdecline_wallet_amount() {
		return softdecline_wallet_amount;
	}
	public void setSoftdecline_wallet_amount(double softdecline_wallet_amount) {
		this.softdecline_wallet_amount = softdecline_wallet_amount;
	}
	public double getSoftdecline_upi_amount() {
		return softdecline_upi_amount;
	}
	public void setSoftdecline_upi_amount(double softdecline_upi_amount) {
		this.softdecline_upi_amount = softdecline_upi_amount;
	}
	public double getSoftdecline_null_amount() {
		return softdecline_null_amount;
	}
	public void setSoftdecline_null_amount(double softdecline_null_amount) {
		this.softdecline_null_amount = softdecline_null_amount;
	}
	public double getSoftdecline_total_amount() {
		return softdecline_total_amount;
	}
	public void setSoftdecline_total_amount(double softdecline_total_amount) {
		this.softdecline_total_amount = softdecline_total_amount;
	}
	public long getSummary_nb_capture_count() {
		return summary_nb_capture_count;
	}
	public void setSummary_nb_capture_count(long summary_nb_capture_count) {
		this.summary_nb_capture_count = summary_nb_capture_count;
	}
	public long getSummary_nb_pending_count() {
		return summary_nb_pending_count;
	}
	public void setSummary_nb_pending_count(long summary_nb_pending_count) {
		this.summary_nb_pending_count = summary_nb_pending_count;
	}
	public long getSummary_nb_hard_decline_count() {
		return summary_nb_hard_decline_count;
	}
	public void setSummary_nb_hard_decline_count(long summary_nb_hard_decline_count) {
		this.summary_nb_hard_decline_count = summary_nb_hard_decline_count;
	}
	public long getSummary_nb_soft_decline_count() {
		return summary_nb_soft_decline_count;
	}
	public void setSummary_nb_soft_decline_count(long summary_nb_soft_decline_count) {
		this.summary_nb_soft_decline_count = summary_nb_soft_decline_count;
	}
	public double getSummary_nb_capture_volume() {
		return summary_nb_capture_volume;
	}
	public void setSummary_nb_capture_volume(double summary_nb_capture_volume) {
		this.summary_nb_capture_volume = summary_nb_capture_volume;
	}
	public double getSummary_nb_pending_volume() {
		return summary_nb_pending_volume;
	}
	public void setSummary_nb_pending_volume(double summary_nb_pending_volume) {
		this.summary_nb_pending_volume = summary_nb_pending_volume;
	}
	public double getSummary_nb_hard_decline_volume() {
		return summary_nb_hard_decline_volume;
	}
	public void setSummary_nb_hard_decline_volume(double summary_nb_hard_decline_volume) {
		this.summary_nb_hard_decline_volume = summary_nb_hard_decline_volume;
	}
	public double getSummary_nb_soft_decline_volume() {
		return summary_nb_soft_decline_volume;
	}
	public void setSummary_nb_soft_decline_volume(double summary_nb_soft_decline_volume) {
		this.summary_nb_soft_decline_volume = summary_nb_soft_decline_volume;
	}
	public long getSummary_upi_capture_count() {
		return summary_upi_capture_count;
	}
	public void setSummary_upi_capture_count(long summary_upi_capture_count) {
		this.summary_upi_capture_count = summary_upi_capture_count;
	}
	public long getSummary_upi_pending_count() {
		return summary_upi_pending_count;
	}
	public void setSummary_upi_pending_count(long summary_upi_pending_count) {
		this.summary_upi_pending_count = summary_upi_pending_count;
	}
	public long getSummary_upi_hard_decline_count() {
		return summary_upi_hard_decline_count;
	}
	public void setSummary_upi_hard_decline_count(long summary_upi_hard_decline_count) {
		this.summary_upi_hard_decline_count = summary_upi_hard_decline_count;
	}
	public long getSummary_upi_soft_decline_count() {
		return summary_upi_soft_decline_count;
	}
	public void setSummary_upi_soft_decline_count(long summary_upi_soft_decline_count) {
		this.summary_upi_soft_decline_count = summary_upi_soft_decline_count;
	}
	public double getSummary_upi_capture_volume() {
		return summary_upi_capture_volume;
	}
	public void setSummary_upi_capture_volume(double summary_upi_capture_volume) {
		this.summary_upi_capture_volume = summary_upi_capture_volume;
	}
	public double getSummary_upi_pending_volume() {
		return summary_upi_pending_volume;
	}
	public void setSummary_upi_pending_volume(double summary_upi_pending_volume) {
		this.summary_upi_pending_volume = summary_upi_pending_volume;
	}
	public double getSummary_upi_hard_decline_volume() {
		return summary_upi_hard_decline_volume;
	}
	public void setSummary_upi_hard_decline_volume(double summary_upi_hard_decline_volume) {
		this.summary_upi_hard_decline_volume = summary_upi_hard_decline_volume;
	}
	public double getSummary_upi_soft_decline_volume() {
		return summary_upi_soft_decline_volume;
	}
	public void setSummary_upi_soft_decline_volume(double summary_upi_soft_decline_volume) {
		this.summary_upi_soft_decline_volume = summary_upi_soft_decline_volume;
	}
	public long getSummary_cc_dc_capture_count() {
		return summary_cc_dc_capture_count;
	}
	public void setSummary_cc_dc_capture_count(long summary_cc_dc_capture_count) {
		this.summary_cc_dc_capture_count = summary_cc_dc_capture_count;
	}
	public long getSummary_cc_dc_pending_count() {
		return summary_cc_dc_pending_count;
	}
	public void setSummary_cc_dc_pending_count(long summary_cc_dc_pending_count) {
		this.summary_cc_dc_pending_count = summary_cc_dc_pending_count;
	}
	public long getSummary_cc_dc_hard_decline_count() {
		return summary_cc_dc_hard_decline_count;
	}
	public void setSummary_cc_dc_hard_decline_count(long summary_cc_dc_hard_decline_count) {
		this.summary_cc_dc_hard_decline_count = summary_cc_dc_hard_decline_count;
	}
	public long getSummary_cc_dc_soft_decline_count() {
		return summary_cc_dc_soft_decline_count;
	}
	public void setSummary_cc_dc_soft_decline_count(long summary_cc_dc_soft_decline_count) {
		this.summary_cc_dc_soft_decline_count = summary_cc_dc_soft_decline_count;
	}
	public double getSummary_cc_dc_capture_volume() {
		return summary_cc_dc_capture_volume;
	}
	public void setSummary_cc_dc_capture_volume(double summary_cc_dc_capture_volume) {
		this.summary_cc_dc_capture_volume = summary_cc_dc_capture_volume;
	}
	public double getSummary_cc_dc_pending_volume() {
		return summary_cc_dc_pending_volume;
	}
	public void setSummary_cc_dc_pending_volume(double summary_cc_dc_pending_volume) {
		this.summary_cc_dc_pending_volume = summary_cc_dc_pending_volume;
	}
	public double getSummary_cc_dc_hard_decline_volume() {
		return summary_cc_dc_hard_decline_volume;
	}
	public void setSummary_cc_dc_hard_decline_volume(double summary_cc_dc_hard_decline_volume) {
		this.summary_cc_dc_hard_decline_volume = summary_cc_dc_hard_decline_volume;
	}
	public double getSummary_cc_dc_soft_decline_volume() {
		return summary_cc_dc_soft_decline_volume;
	}
	public void setSummary_cc_dc_soft_decline_volume(double summary_cc_dc_soft_decline_volume) {
		this.summary_cc_dc_soft_decline_volume = summary_cc_dc_soft_decline_volume;
	}
	public long getSummary_wallet_capture_count() {
		return summary_wallet_capture_count;
	}
	public void setSummary_wallet_capture_count(long summary_wallet_capture_count) {
		this.summary_wallet_capture_count = summary_wallet_capture_count;
	}
	public long getSummary_wallet_pending_count() {
		return summary_wallet_pending_count;
	}
	public void setSummary_wallet_pending_count(long summary_wallet_pending_count) {
		this.summary_wallet_pending_count = summary_wallet_pending_count;
	}
	public long getSummary_wallet_hard_decline_count() {
		return summary_wallet_hard_decline_count;
	}
	public void setSummary_wallet_hard_decline_count(long summary_wallet_hard_decline_count) {
		this.summary_wallet_hard_decline_count = summary_wallet_hard_decline_count;
	}
	public long getSummary_wallet_soft_decline_count() {
		return summary_wallet_soft_decline_count;
	}
	public void setSummary_wallet_soft_decline_count(long summary_wallet_soft_decline_count) {
		this.summary_wallet_soft_decline_count = summary_wallet_soft_decline_count;
	}
	public double getSummary_wallet_capture_volume() {
		return summary_wallet_capture_volume;
	}
	public void setSummary_wallet_capture_volume(double summary_wallet_capture_volume) {
		this.summary_wallet_capture_volume = summary_wallet_capture_volume;
	}
	public double getSummary_wallet_pending_volume() {
		return summary_wallet_pending_volume;
	}
	public void setSummary_wallet_pending_volume(double summary_wallet_pending_volume) {
		this.summary_wallet_pending_volume = summary_wallet_pending_volume;
	}
	public double getSummary_wallet_hard_decline_volume() {
		return summary_wallet_hard_decline_volume;
	}
	public void setSummary_wallet_hard_decline_volume(double summary_wallet_hard_decline_volume) {
		this.summary_wallet_hard_decline_volume = summary_wallet_hard_decline_volume;
	}
	public double getSummary_wallet_soft_decline_volume() {
		return summary_wallet_soft_decline_volume;
	}
	public void setSummary_wallet_soft_decline_volume(double summary_wallet_soft_decline_volume) {
		this.summary_wallet_soft_decline_volume = summary_wallet_soft_decline_volume;
	}
	public long getSummary_overall_capture_count() {
		return summary_overall_capture_count;
	}
	public void setSummary_overall_capture_count(long summary_overall_capture_count) {
		this.summary_overall_capture_count = summary_overall_capture_count;
	}
	public long getSummary_overall_pending_count() {
		return summary_overall_pending_count;
	}
	public void setSummary_overall_pending_count(long summary_overall_pending_count) {
		this.summary_overall_pending_count = summary_overall_pending_count;
	}
	public long getSummary_overall_hard_decline_count() {
		return summary_overall_hard_decline_count;
	}
	public void setSummary_overall_hard_decline_count(long summary_overall_hard_decline_count) {
		this.summary_overall_hard_decline_count = summary_overall_hard_decline_count;
	}
	public long getSummary_overall_soft_decline_count() {
		return summary_overall_soft_decline_count;
	}
	public void setSummary_overall_soft_decline_count(long summary_overall_soft_decline_count) {
		this.summary_overall_soft_decline_count = summary_overall_soft_decline_count;
	}
	public double getSummary_overall_capture_volume() {
		return summary_overall_capture_volume;
	}
	public void setSummary_overall_capture_volume(double summary_overall_capture_volume) {
		this.summary_overall_capture_volume = summary_overall_capture_volume;
	}
	public double getSummary_overall_pending_volume() {
		return summary_overall_pending_volume;
	}
	public void setSummary_overall_pending_volume(double summary_overall_pending_volume) {
		this.summary_overall_pending_volume = summary_overall_pending_volume;
	}
	public double getSummary_overall_hard_decline_volume() {
		return summary_overall_hard_decline_volume;
	}
	public void setSummary_overall_hard_decline_volume(double summary_overall_hard_decline_volume) {
		this.summary_overall_hard_decline_volume = summary_overall_hard_decline_volume;
	}
	public double getSummary_overall_soft_decline_volume() {
		return summary_overall_soft_decline_volume;
	}
	public void setSummary_overall_soft_decline_volume(double summary_overall_soft_decline_volume) {
		this.summary_overall_soft_decline_volume = summary_overall_soft_decline_volume;
	}
	
		
}