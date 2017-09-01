package com.ydttech.iot;

import com.ydttech.iot.util.db.DBUtil;
import com.ydttech.iot.util.http.HttpUtil;
import com.ydttech.iot.util.modbus.CoilsEventListener;
import com.ydttech.iot.util.modbus.DICoilsMessage;
import com.ydttech.iot.util.modbus.DOCoilsMessage;
import com.ydttech.iot.util.modbus.IOlogikUtil;
import com.ydttech.iot.util.mq.QpidMQUtil;
import com.ydttech.iot.util.pathwatch.PathWatchEventListener;
import com.ydttech.iot.util.pathwatch.PathWatchUtil;
import com.ydttech.iot.vo.config.IBGSConfig;
import net.wimpi.modbus.facade.ModbusTCPMaster;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.IOException;
import java.nio.file.WatchKey;
import java.sql.SQLException;

import static java.nio.file.StandardWatchEventKinds.*;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Hello {}", Main.class.getCanonicalName());
        logger.info("User working directory:[{}]", System.getProperty("user.dir"));

        try {

            if (!read_env_cfg()) {
                logger.error("Configuration file:[{}] content is not correct!", IBGSConfig.initCfg);
                System.exit(1);
            }

            init_mq();
            init_db();
            test_http();
//            init_gpio();
            init_watch();


            Thread.sleep(6000);

        } catch (Exception e) {
            logger.info("{}", e.getMessage());
            System.exit(1);
        }



        logger.info("main process is done!");

    }

    private static void init_watch() throws IOException {

        PathWatchUtil pathWatchUtil = new PathWatchUtil("D:\\Temp\\",
                ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        PathWatchEventListener pathWatchEventListener = new PathWatchEventListener() {
            @Override
            public void fileCreated(WatchKey watchKey, String rootPath, String name) {
                logger.info("file:{} in {} is created!", name, rootPath);
            }

            @Override
            public void fileDeleted(WatchKey watchKey, String rootPath, String name) {
                logger.info("file:{} in {} is deleted!", name, rootPath);
            }

            @Override
            public void fileModified(WatchKey watchKey, String rootPath, String name) {
                logger.info("file:{} in {} is modified!", name, rootPath);
            }
        };

        pathWatchUtil.registerEvent(pathWatchEventListener);
        pathWatchUtil.activeEvent();

    }

    private static void init_gpio() {

        IOlogikUtil iOlogikUtil;
        ModbusTCPMaster modbusTCPMaster;

        CoilsEventListener simulatorEvent = new CoilsEventListener() {
            @Override
            public void DIChangeEvent(DICoilsMessage diCoilsMessage) {

                logger.info("DI value is changed:{}", diCoilsMessage.toString());

            }

            @Override
            public void DOChangeEvent(DOCoilsMessage doCoilsMessage) {

                logger.info("DO value is changed:{}", doCoilsMessage.getBitVector().getBit(1));

            }
        };

        iOlogikUtil = new IOlogikUtil("192.168.1.60", 502, false);

        try {
            iOlogikUtil.createConn();
            iOlogikUtil.registerEvent(simulatorEvent);

            logger.info("do value:{}", iOlogikUtil.getDOValue(1));
            logger.info("di value:{}", iOlogikUtil.getDIValue(0));

            iOlogikUtil.activeEvent();

        } catch (Exception e) {
            logger.error("gpio init failure:{}", e.getMessage());
        }


    }

    private static void init_db() throws ClassNotFoundException, SQLException {

        DBUtil dbUtil;

        dbUtil = new DBUtil(IBGSConfig.DBCFG_DRIVERNAME);

        dbUtil.createConn(IBGSConfig.DBCFG_DBURL +
                "?user="+ IBGSConfig.DBCFG_USERNAME +
                "&password=" + IBGSConfig.DBCFG_PASSWORD, true
         );

        logger.info("db connection is create done!");

        dbUtil.stopConn();

        logger.info("db connection is stop done!");

    }

    static boolean read_env_cfg() {

        boolean resultCode = true;

        try {
            IBGSConfig.init();
        } catch (DocumentException e) {
            logger.error("{}", e.getMessage());
            resultCode = false;
        }

        return resultCode;
    }

    static void init_mq() throws JMSException {

        QpidMQUtil mqUtil;

        mqUtil = new QpidMQUtil(
                IBGSConfig.MQCFG_MQURL, IBGSConfig.MQCFG_USERNAME, IBGSConfig.MQCFG_PASSWORD
        );

        mqUtil.createConn("123");
        mqUtil.createSess(Session.AUTO_ACKNOWLEDGE);
        Message myMsg = mqUtil.createTextMsg("1234", "TextMessage", "1235");
        mqUtil.sendMsg("abc", myMsg);
        mqUtil.createTopic("ActiveMQTopic");

        mqUtil.stopConn();

    }

    static void test_http() {

        HttpUtil httpUtil;

        httpUtil = new HttpUtil(IBGSConfig.HTTPCFG_SILOURI);

        httpUtil.postStringData("abc");

        while (!httpUtil.isAvailable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        logger.info("rsp data:{}" , new String(httpUtil.getPostStringData()));

        httpUtil.postStringData("def");

        while (!httpUtil.isAvailable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        logger.info("rsp data:{}" , new String(httpUtil.getPostStringData()));

        httpUtil.postStringData("ghi");

        while (!httpUtil.isAvailable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        logger.info("rsp data:{}" , new String(httpUtil.getPostStringData()));



    }
}
