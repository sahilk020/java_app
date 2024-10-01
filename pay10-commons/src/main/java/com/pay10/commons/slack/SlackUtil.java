package com.pay10.commons.slack;

import com.pay10.commons.util.ProfileUtil;
import com.pay10.commons.util.PropertiesManager;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class SlackUtil {

    private static Logger logger = LoggerFactory.getLogger(SlackUtil.class.getName());

    private static final String DEAULT_CHANNEL_NAME = PropertiesManager.propertiesMap.get("Slack_Channel");
    private static final String TOKEN = PropertiesManager.propertiesMap.get("Slack_Token");



    public static void sendMongoDbConnectionResetAlert(String channel) {
        String text = "Connection To Mongo Db Server Lost";
        if(ProfileUtil.isAlertEnabledForActiveProfile()) {
            Slack slack = Slack.getInstance();

            MethodsClient methods = slack.methods(TOKEN);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(StringUtils.isEmpty(channel) ? DEAULT_CHANNEL_NAME : channel)
                    .text(":fire:" + text + ":fire:")
                    .iconEmoji(":rotating_light:")
                    .username("Transaction Alert")
                    .build();

            try {
                ChatPostMessageResponse response = methods.chatPostMessage(request);
            } catch (IOException e) {
                logger.error(text,e);
            } catch (SlackApiException e) {
                logger.error(text,e);
            }
        }else{
            logger.error(text);
        }

    }



    public static void sendMysqlDbConnectionResetAlert(String channel) {
        String text = "Connection To Mysql Db Server Lost";
        if(ProfileUtil.isAlertEnabledForActiveProfile()) {
            Slack slack = Slack.getInstance();

            MethodsClient methods = slack.methods(TOKEN);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(StringUtils.isEmpty(channel) ? DEAULT_CHANNEL_NAME : channel)
                    .text(":fire:" + text + ":fire:")
                    .iconEmoji(":rotating_light:")
                    .username("Transaction Alert")
                    .build();

            try {
                ChatPostMessageResponse response = methods.chatPostMessage(request);
            } catch (IOException e) {
                logger.error(text,e);
            } catch (SlackApiException e) {
                logger.error(text,e);
            }
        }else{
            logger.error(text);
        }

    }
}
