package com.datayes.textmining.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: yuzhaojun
 * Date: 13-7-22
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class RabbitMQConfig {

    private ConnectionFactory connectionFactory;

    private Connection connection = null;

    private Channel channel = null;

    private QueueingConsumer consumer;
    
    private static Logger logger = Logger.getLogger("RptCls");

    public RabbitMQConfig(){

    }

    public RabbitMQConfig(String host, String user, String password, int port, String queueName) throws IOException
    {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
		while(true)
		{
			connection = connectionFactory.newConnection();
			if(connection == null)
			{
				logger.error("connection not found!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				break;
			}
		}
		while(true)
		{
			channel = connection.createChannel();
			if(channel == null)
			{
				logger.error("channel not found!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				break;
			}
		}
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

    }

    public ConnectionFactory getConnectionFactory() {

        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public QueueingConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(QueueingConsumer consumer) {
        this.consumer = consumer;
    }
}
