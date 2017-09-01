package com.ydttech.iot.util.rfid;

import com.mmm.driver.ConnectionException;
import com.mmm.driver.IEventListener;
import com.mmm.mapping.ReaderException;

public interface IReaderUtil {

    void createConn(String ipAddress, int timeout) throws ConnectionException;
    void stopConn() throws ConnectionException;
    int setReportField(String reportField, String arriveField, String departField) throws ConnectionException, ReaderException;
    String getTidByAnt(String antenna);
    String getTidByEpc(String epc);
    String getEpc(String antenna);
    boolean setDO(int doIndex, String value) throws ConnectionException, ReaderException;
    String getDO(int doIndex);
    public int getOpMode();
    String registerEvent(IEventListener iEventListener, String eventType) throws ConnectionException, ReaderException;
    void activeEvent() throws ConnectionException, ReaderException;
    void standbyEvent() throws ConnectionException, ReaderException;

}
