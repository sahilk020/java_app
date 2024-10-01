package com.pay10.commons.audittrail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

/**
 * This component use to store the details of audit trail as per provided
 * details.
 *
 * @author Jay Gajera
 */
@Component
public class AuditTrailService {

    private static final Logger logger = LoggerFactory.getLogger(AuditTrailService.class.getName());

    @Autowired
    private AuditTrailDao auditTrailDao;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    protected UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
    ObjectMapper mapper = new ObjectMapper();

    /**
     * Set the timestamp details in provided #auditTrail and store it in db.
     *
     * @param auditTrail {@link AuditTrail}
     */
    public void saveAudit(AuditTrail auditTrail) {
        LocalDateTime now = LocalDateTime.now();
        String formatDateTime = now.format(formatter);
        auditTrail.setTimestamp(formatDateTime);
        if (StringUtils.isNotBlank(auditTrail.getPreviousValue())) {
            auditTrail.setDiffValue(findDiffAsJson(auditTrail));
        }
        auditTrailDao.save(auditTrail);
        logger.info("audit info save successfully by={} for action={}", auditTrail.getEmailId(),
                auditTrail.getActionMessageByAction().getActionMessage());
    }

    /**
     * prepare and set the required audit trail detail and store it in database. for
     * this method user need to set payload, previousValue and action message
     * instance. all other fields prepare by this method itself using request.
     *
     * @param request    the {@link HttpServletRequest}
     * @param auditTrail the {@link AuditTrail}
     */
    public void saveAudit(HttpServletRequest request, AuditTrail auditTrail) {
        LocalDateTime now = LocalDateTime.now();
        HttpSession session = request.getSession();
        String osDetails = request.getHeader(CrmFieldConstants.USER_AGENT.getValue());

        System.out.println("@@@@@ saveAudit osDetails :  " + osDetails);

        String browserName = getBrowserName(osDetails);

        System.out.println("@@@ browserName ::: " + browserName);

        String ip = StringUtils.isNotBlank(request.getHeader("X-Forwarded-For")) ? request.getHeader("X-Forwarded-For")
                : request.getRemoteAddr();

        ReadableUserAgent agent = parser.parse(osDetails);
        User user = (User) session.getAttribute(Constants.USER.getValue());
        String formatDateTime = now.format(formatter);
        auditTrail.setEmailId(user.getEmailId());
        auditTrail.setFirstName(StringUtils.defaultIfBlank(user.getFirstName(), user.getBusinessName()));
        auditTrail.setBrowser(browserName);
        auditTrail.setIp(ip);
        auditTrail.setOs(agent.getOperatingSystem().getName());
        auditTrail.setTimestamp(formatDateTime);
        System.out.println("---------------------------------------");
        logger.info("@@@ auditTrail : " + auditTrail);
        if (StringUtils.isNotBlank(auditTrail.getPreviousValue())) {
            auditTrail.setDiffValue(findDiffAsJson(auditTrail));
        }
        auditTrailDao.save(auditTrail);
        logger.info("audit info save successfully by={} for action={}", auditTrail.getEmailId(),
                auditTrail.getActionMessageByAction().getActionMessage());
    }

    public static String getBrowserName(String userAgent) {
        if (userAgent == null) {
            logger.info("jj");

            return "Unknown";
        }

        if (userAgent.contains("Edg")) {
            return "Edge";
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }

    /**
     * This method is used to find difference as JSON between payload and
     * previousValue.
     *
     * @param auditTrail {@link AuditTrail}
     * @return difference as JSON {@link String}
     */
    private String findDiffAsJson(AuditTrail auditTrail) {
        JsonNode diffNode = null;
        try {
            JsonNode source = mapper.readTree(auditTrail.getPreviousValue());
            JsonNode target = mapper.readTree(auditTrail.getPayload());
            diffNode = JsonDiff.asJson(source, target);
            return mapper.writeValueAsString(diffNode);
        } catch (Exception jpe) {
            logger.warn("findDiffAsJson:: failed to convert as Json diffNode={}", diffNode, jpe);
            return "";
        }
    }
}
