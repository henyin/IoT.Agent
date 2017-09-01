package com.ydttech.iot.util.mq;

import org.apache.activemq.*;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import javax.jms.Message;

public class ActiveMQUtil implements IMQUtil {

    private String connURI, username, password;
    private Connection connection;
    private ActiveMQSession session;

    public ActiveMQUtil(String connURI, String username, String password) {
        this.connURI = connURI;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection createConn(String clientID) throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connURI);
        connection = connectionFactory.createConnection(this.username, this.password);
        connection.setClientID(clientID);
        connection.start();

        return connection;

    }

    @Override
    public Session createSess(int acknowledgeMode) throws JMSException {

        session = (ActiveMQSession) connection.createSession(false, acknowledgeMode);

        return session;

    }

    @Override
    public void sendMsg(String topic, Message message) throws JMSException {

        ActiveMQDestination destination = (ActiveMQDestination) session.createTopic(topic);

        ActiveMQMessageProducer messageProducer = (ActiveMQMessageProducer) session.createProducer(destination);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        messageProducer.send(message);
    }

    @Override
    public Message createTextMsg(String correlationID, String jmsType, String msgData) throws JMSException {

        ActiveMQTextMessage textMessage = (ActiveMQTextMessage) session.createTextMessage();

        textMessage.setText(msgData);
        textMessage.setJMSType(jmsType);
        textMessage.setCorrelationId(correlationID);

        return textMessage;
    }

    @Override
    public void createTopic(String topic) throws JMSException {

        ActiveMQDestination destination = (ActiveMQDestination) session.createTopic(topic);

    }

    @Override
    public void stopConn() throws JMSException {
        connection.stop();
        connection.close();
    }
}
