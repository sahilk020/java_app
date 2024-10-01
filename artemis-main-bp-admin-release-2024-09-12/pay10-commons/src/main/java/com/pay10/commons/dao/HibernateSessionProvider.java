package com.pay10.commons.dao;


import java.io.File;

import com.pay10.commons.entity.*;
import com.pay10.commons.slack.SlackUtil;
import com.pay10.commons.user.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.entity.AcqurerNodalMapping;
import com.pay10.commons.entity.BSESMonthlyInvoiceReportEntity;
import com.pay10.commons.entity.BankList;
import com.pay10.commons.entity.ChargebackReasonEntity;
import com.pay10.commons.entity.Country;
import com.pay10.commons.entity.CurrencyCode;
import com.pay10.commons.entity.IRCTCRefundFile;
import com.pay10.commons.entity.LiabilityPgRefNumbersEntity;
import com.pay10.commons.entity.NeutrinoDetails;
import com.pay10.commons.entity.State;
import com.pay10.commons.entity.TdrWaveOffSettingEntity;
import com.pay10.commons.payoutEntity.PayoutAcquirer;
import com.pay10.commons.repository.Comment;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.repository.DMSFileData;
import com.pay10.commons.repository.Fileentitiy;
import com.pay10.commons.slack.SlackUtil;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.AccountCurrencyPayout;
import com.pay10.commons.user.AcquirerCodeMapping;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;
import com.pay10.commons.user.AcquirerDowntimeScheduling;
import com.pay10.commons.user.AcquirerMasterDB;
import com.pay10.commons.user.AcquirerMasterPayout;
import com.pay10.commons.user.AcquirerSchadular;
import com.pay10.commons.user.AcquirerSwitchHistory;
import com.pay10.commons.user.AndroidOtpAutoreadConfig;
import com.pay10.commons.user.BankSettle;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.user.BinCardInfo;
import com.pay10.commons.user.ChannelMaster;
import com.pay10.commons.user.Chargeback;
import com.pay10.commons.user.ChargebackComment;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CycleMaster;
import com.pay10.commons.user.DashboardPreferenceSet;
import com.pay10.commons.user.DynamicPaymentPage;
import com.pay10.commons.user.EkycGenTable;
import com.pay10.commons.user.EkycVerTable;
import com.pay10.commons.user.ErrorCodeMappingPayout;
import com.pay10.commons.user.EventPayload;
import com.pay10.commons.user.EventProcessedStatus;
import com.pay10.commons.user.Events;
import com.pay10.commons.user.ForgetPin;
import com.pay10.commons.user.FraudPrevention;
import com.pay10.commons.user.GstRSaleReport;
import com.pay10.commons.user.InvoiceNumberCounter;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginOtp;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.MerchantAcquirerProperties;
import com.pay10.commons.user.MerchantFpsAccess;
import com.pay10.commons.user.MerchantKeySalt;
import com.pay10.commons.user.MerchantReturnURL;
import com.pay10.commons.user.MerchantStatusLog;
import com.pay10.commons.user.Mop;
import com.pay10.commons.user.MopInternational;
import com.pay10.commons.user.MopTransaction;
import com.pay10.commons.user.MopTransactionInternational;
import com.pay10.commons.user.MprUploadDetails;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.user.NodalAmount;
import com.pay10.commons.user.NodalCreditDebitHistory;
import com.pay10.commons.user.OfflineRefund;
import com.pay10.commons.user.Payment;
import com.pay10.commons.user.PaymentInternational;
import com.pay10.commons.user.PendingMappingRequest;
import com.pay10.commons.user.PendingResellerMappingApproval;
import com.pay10.commons.user.PendingUserApproval;
import com.pay10.commons.user.Permission;
import com.pay10.commons.user.Permissions;
import com.pay10.commons.user.PgCodeMapping;
import com.pay10.commons.user.QuomoCurrencyConfiguration;
import com.pay10.commons.user.RefundConfiguration;
import com.pay10.commons.user.RefundValidationDetails;
import com.pay10.commons.user.ReportColumnDetail;
import com.pay10.commons.user.ResellerCharges;
import com.pay10.commons.user.ResellerPayout;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.RoleWiseMenu;
import com.pay10.commons.user.Roles;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterConfigurationPayout;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.RouterRulePayout;
import com.pay10.commons.user.SbiToken;
import com.pay10.commons.user.Segment;
import com.pay10.commons.user.SegmentInfo;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.SettlementDataUpdate;
import com.pay10.commons.user.SettlementDataUpload;
import com.pay10.commons.user.SubscriberConfig;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.TPAPTransactionLimit;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingPayout;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserCommision;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserRecords;
import com.pay10.commons.user.VpaMaster;
import com.pay10.commons.user.VpaValidateMid;
import com.pay10.commons.util.Constants;

/**
 * @author Puneet
 */
public class HibernateSessionProvider {
    private static final String hbmddlAutoSettingName = "hibernate.hbm2ddl.auto";
    private static final String hbmddlAutoSetting = "update";
    private static Logger logger = LoggerFactory.getLogger(HibernateSessionProvider.class.getName());
    private SessionFactory factory;

    private HibernateSessionProvider() {

        // configures settings from hibernate.cfg.xml
        final File hibernateFile = new File(System.getenv("BPGATE_PROPS") + Constants.HIBERNATE_FILE_NAME.getValue());
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure(hibernateFile).applySetting(hbmddlAutoSettingName, hbmddlAutoSetting)
                .build();

        try {
            factory = new MetadataSources(registry).addAnnotatedClass(User.class)
                    .addAnnotatedClass(UserRecords.class)
                    .addAnnotatedClass(Roles.class)
                    .addAnnotatedClass(Permissions.class)
                    .addAnnotatedClass(LoginHistory.class)
                    .addAnnotatedClass(Account.class)
                    .addAnnotatedClass(Payment.class)

                    .addAnnotatedClass(Mop.class)
                    .addAnnotatedClass(MopTransaction.class)

                    .addAnnotatedClass(PaymentInternational.class)
                    .addAnnotatedClass(MopInternational.class)
                    .addAnnotatedClass(MopTransactionInternational.class)
                    .addAnnotatedClass(AcquirerDowntimeScheduling.class)
                    .addAnnotatedClass(AcquirerSwitchHistory.class)
					.addAnnotatedClass(MultCurrencyCode.class)
					.addAnnotatedClass(PayoutBankCodeconfiguration.class)


                    .addAnnotatedClass(ChargingDetails.class)
//												   .addAnnotatedClass(Invoice.class)
//												   .addAnnotatedClass(InvoiceOld.class)
                    .addAnnotatedClass(DynamicPaymentPage.class)
                    // .addAnnotatedClass(Token.class)
                    .addAnnotatedClass(AcquirerCodeMapping.class)
                    .addAnnotatedClass(PgCodeMapping.class)
                    .addAnnotatedClass(AccountCurrency.class)
                    .addAnnotatedClass(FraudPrevention.class)
                    .addAnnotatedClass(VpaMaster.class)
                    .addAnnotatedClass(Chargeback.class)
                    .addAnnotatedClass(ChargebackComment.class)
                    .addAnnotatedClass(SurchargeDetails.class)
                    .addAnnotatedClass(Surcharge.class)
                    .addAnnotatedClass(ServiceTax.class)
                    .addAnnotatedClass(PendingUserApproval.class)
                    //	   .addAnnotatedClass(NotificationDetail.class)
                    //	   .addAnnotatedClass(BinRange.class)
                    .addAnnotatedClass(PendingResellerMappingApproval.class)
                    //	   .addAnnotatedClass(NotificationEmailer.class)
                    .addAnnotatedClass(PendingMappingRequest.class)
                    .addAnnotatedClass(RouterRule.class)
                    .addAnnotatedClass(GstRSaleReport.class)
                    .addAnnotatedClass(RouterConfiguration.class)
                    .addAnnotatedClass(MerchantAcquirerProperties.class)
                    .addAnnotatedClass(BeneficiaryAccounts.class)
                    .addAnnotatedClass(NodalAmount.class)
                    .addAnnotatedClass(MerchantReturnURL.class)
                    .addAnnotatedClass(AcquirerSchadular.class)

                    .addAnnotatedClass(RefundValidationDetails.class)
                    .addAnnotatedClass(SettlementDataUpload.class)
                    .addAnnotatedClass(MprUploadDetails.class)
                    .addAnnotatedClass(LoginOtp.class)
                    .addAnnotatedClass(ForgetPin.class)
                    .addAnnotatedClass(SettlementDataUpdate.class)
                    .addAnnotatedClass(NodalCreditDebitHistory.class)
                    .addAnnotatedClass(CompanyProfile.class)
                    .addAnnotatedClass(InvoiceNumberCounter.class)
                    .addAnnotatedClass(DashboardPreferenceSet.class)
                    .addAnnotatedClass(MerchantFpsAccess.class)
                    .addAnnotatedClass(SuccessRateThresholdSetting.class)
                    .addAnnotatedClass(NodalAccountDetails.class)
                    .addAnnotatedClass(AndroidOtpAutoreadConfig.class)
                    .addAnnotatedClass(ResellerCharges.class)
                    .addAnnotatedClass(Role.class)
                    .addAnnotatedClass(Menu.class)
                    .addAnnotatedClass(Permission.class)
                    .addAnnotatedClass(RoleWiseMenu.class)
                    .addAnnotatedClass(UserGroup.class)
                    .addAnnotatedClass(AuditTrail.class)
                    .addAnnotatedClass(ActionMessageByAction.class)
                    .addAnnotatedClass(SegmentInfo.class)

                    .addAnnotatedClass(DMSEntity.class)
                    .addAnnotatedClass(Comment.class)
                    .addAnnotatedClass(Fileentitiy.class)

                    .addAnnotatedClass(CycleMaster.class)
                    .addAnnotatedClass(Resellercommision.class)
                    .addAnnotatedClass(ResellerPayout.class)

                    .addAnnotatedClass(Segment.class)

                    .addAnnotatedClass(NeutrinoDetails.class)
                    .addAnnotatedClass(ErrorCodeMappingPayout.class)
                    .addAnnotatedClass(Country.class)
                    .addAnnotatedClass(State.class)
                    .addAnnotatedClass(BankSettle.class)
                    .addAnnotatedClass(LiabilityPgRefNumbersEntity.class)
                    .addAnnotatedClass(DMSFileData.class)
                    .addAnnotatedClass(NeutrinoDetails.class)
                    .addAnnotatedClass(Country.class)
                    .addAnnotatedClass(State.class)
                    .addAnnotatedClass(SbiToken.class)
                    .addAnnotatedClass(RefundConfiguration.class)
                    .addAnnotatedClass(OfflineRefund.class)
                    .addAnnotatedClass(ChargebackReasonEntity.class)
                    .addAnnotatedClass(AcquirerDownTimeConfiguration.class)
                    .addAnnotatedClass(ChannelMaster.class)
                    .addAnnotatedClass(AcquirerMasterPayout.class)
                    .addAnnotatedClass(TdrWaveOffSettingEntity.class)
                    .addAnnotatedClass(LiabilityPgRefNumbersEntity.class)
                    .addAnnotatedClass(TdrSetting.class)
                    .addAnnotatedClass(VpaMaster.class)
                    .addAnnotatedClass(EventPayload.class)
                    .addAnnotatedClass(EventProcessedStatus.class)
                    .addAnnotatedClass(Events.class)
                    .addAnnotatedClass(SubscriberConfig.class)
                    .addAnnotatedClass(VpaValidateMid.class)
                    .addAnnotatedClass(TdrSettingPayout.class)
                    .addAnnotatedClass(TPAPTransactionLimit.class)
                    .addAnnotatedClass(RouterConfigurationPayout.class)
                    .addAnnotatedClass(RouterRulePayout.class)
                    .addAnnotatedClass(PaymentInternational.class)
                    .addAnnotatedClass(AccountCurrencyPayout.class)
                    .addAnnotatedClass(MopInternational.class)
                    .addAnnotatedClass(MopTransactionInternational.class)
                    .addAnnotatedClass(MerchantKeySalt.class)  .addAnnotatedClass(EkycGenTable.class)
                    .addAnnotatedClass(EkycVerTable.class)
                    .addAnnotatedClass(BankList.class)
                    .addAnnotatedClass(AcqurerNodalMapping.class)
                    .addAnnotatedClass(BinCardInfo.class)
                    .addAnnotatedClass(BSESMonthlyInvoiceReportEntity.class)
                    .addAnnotatedClass(IRCTCRefundFile.class)
                    .addAnnotatedClass(CurrencyCode.class)
                    .addAnnotatedClass(QuomoCurrencyConfiguration.class)
                    .addAnnotatedClass(ReportColumnDetail.class)
                    .addAnnotatedClass(MerchantStatusLog.class)
                    .addAnnotatedClass(AcquirerMasterDB.class)
                    .addAnnotatedClass(UserCommision.class)
                    .addAnnotatedClass(ResetMerchantKey.class)
                    .addAnnotatedClass(CryptoDetails.class)
                    .addAnnotatedClass(PayoutAcquirer.class)
                    .addAnnotatedClass(AcquirerMasterPayout.class)
                    .addAnnotatedClass(WhiteListedIp.class)
                    .addAnnotatedClass(FiatDetails.class)
                    .addAnnotatedClass(VersionControlEntity.class)
                    .addAnnotatedClass(AadharEntity.class)
                    .addAnnotatedClass(PGWebHookPostConfigURL.class)
                    .buildMetadata().buildSessionFactory();
            } catch (Exception exception) {
            logger.error("Error creating hibernate session" + exception);
            StandardServiceRegistryBuilder.destroy(registry);
            throw exception;
        }
    }

    public static SessionFactory getSessionFactory() {
        return SessionHelper.provider.getFactory();
    }

    public static Session getSession() {
        try {
            return getSessionFactory().openSession();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception While connecting to mysql server {}", e.getMessage());
            SlackUtil.sendMysqlDbConnectionResetAlert(null);
        }
        throw new RuntimeException();
    }

    public static void closeSession(Session session) {
        if (null != session && session.isOpen()) {
            session.close();
            session = null;
        }
    }

    private SessionFactory getFactory() {
        return factory;
    }

    private static class SessionHelper {
        private static final HibernateSessionProvider provider = new HibernateSessionProvider();
    }
}
