package com.ydttech.iot.util.mq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public interface IMQUtil {

    Connection createConn(String clientID) throws JMSException;
    Session createSess(int acknowledgeMode) throws JMSException;
    void sendMsg(String topic, Message message ) throws JMSException;
    Message createTextMsg(String correlationID, String jmsType, String msgData) throws JMSException;
    void createTopic(String topic) throws JMSException;
    void stopConn() throws JMSException;


}
