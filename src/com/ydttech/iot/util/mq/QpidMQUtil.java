package com.ydttech.iot.util.mq;

import javax.jms.*;

import org.apache.qpid.jms.JmsConnectionFactory;

public class QpidMQUtil implements IMQUtil {

    private String connURI, username, password;
    private Connection connection;
    private Session session;

    public QpidMQUtil(String connURI, String username, String password) {
        this.connURI = connURI;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection createConn(String clientID) throws JMSException {

        ConnectionFactory connectionFactory = new JmsConnectionFactory(connURI);
        connection = connectionFactory.createConnection(this.username, this.password);
        connection.setClientID(clientID);
        connection.start();

        return connection;

    }

    @Override
    public Session createSess(int acknowledgeMode) throws JMSException {

        session = connection.createSession(false, acknowledgeMode);

        return session;

    }

    @Override
    public void sendMsg(String topic, Message message) throws JMSException {

        Destination destination =  session.createTopic(topic);

        MessageProducer messageProducer = session.createProducer(destination);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        messageProducer.send(message);
    }

    @Override
    public Message createTextMsg(String correlationID, String jmsType, String msgData) throws JMSException {

        TextMessage textMessage = session.createTextMessage();

        textMessage.setText(msgData);
        textMessage.setJMSType(jmsType);
        textMessage.setJMSCorrelationID(correlationID);

        return textMessage;
    }

    @Override
    public void createTopic(String topic) throws JMSException {

        Destination destination = session.createTopic(topic);

    }

    @Override
    public void stopConn() throws JMSException {
        connection.stop();
        connection.close();
    }
}
