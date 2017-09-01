package com.ydttech.iot.util.rfid;

import com.mmm.data.DataManager;
import com.mmm.driver.ConnectionException;
import com.mmm.driver.IEventListener;
import com.mmm.mapping.*;
import com.mmm.mapping.tag.TagFunctions;

public class ReaderUtil implements IReaderUtil, IEventListener {

    private String ipAddress;
    private int timeout;

    private DataManager dataManager;
    private TagFunctions tagFunctions;
    private SetupManager setupManager;
    private AntennasManager antennasManager;
    private DioManager dioManager;
    private String channelId;

    final int DIO_FUNCTION_VARIABLES_NUMBER = 3; // 3 means "out", 1 means "in"

    public ReaderUtil(String ipAddress, int timeout) {
        this.ipAddress = ipAddress;
        this.timeout = timeout;
    }

    @Override
    public void createConn(String ipAddress, int timeout) throws ConnectionException {

        if (dataManager == null)
            dataManager = new DataManager(DataManager.ConnectionTypes.SOCKET, ipAddress, 3000);

        tagFunctions = new TagFunctions(dataManager);

    }

    @Override
    public void stopConn() throws ConnectionException {

        if (dataManager.isLive()) {
            if (setupManager != null)
                setupManager = null;
            if (antennasManager != null)
                antennasManager = null;
            if (dioManager != null)
                dioManager = null;

            dataManager.close();
            dataManager = null;
        }

    }

    @Override
    public int setReportField(String reportField, String arriveField, String departField)
            throws ConnectionException, ReaderException {

        if (!reportField.isEmpty())
            dataManager.send("tag.reporting.report_fields=".concat("tag_id type time tid antenna rssi freq"));

        if (!arriveField.isEmpty())
            dataManager.send("tag.reporting.arrive_fields=".concat("tag_id type time tid antenna rssi freq"));

        if (!departField.isEmpty())
            dataManager.send("tag.reporting.depart_fields=".concat("ttag_id type time tid antenna"));

        return 0;
    }

    @Override
    public String getTidByAnt(String antenna) {
        return tagFunctions.readTid(tagFunctions.readId(antenna), "", antenna);
    }

    @Override
    public String getTidByEpc(String epc) {
        return tagFunctions.readTid(epc, "", "");
    }

    @Override
    public String getEpc(String antenna) {
        return tagFunctions.readId(antenna);
    }

    @Override
    public boolean setDO(int doIndex, String value) throws ConnectionException, ReaderException {

        boolean resultCode = false;


        dioManager.setDioVariable(doIndex, DIO_FUNCTION_VARIABLES_NUMBER, value);
        resultCode = true;

        return resultCode;
    }

    @Override
    public String getDO(int doIndex) {

        return dioManager.getDioVariable(doIndex, DIO_FUNCTION_VARIABLES_NUMBER);
    }

    @Override
    public int getOpMode() {

        return setupManager.getOperatingMode();
    }

    @Override
    public String registerEvent(IEventListener iEventListener, String eventType)
            throws ConnectionException, ReaderException {

        channelId = null;
        channelId = dataManager.getEventChannel(iEventListener == null ? this : iEventListener);
        if (channelId != null) {
            dataManager.registerEvent(channelId, eventType);
        }

        return channelId;
    }

    @Override
    public void activeEvent() throws ConnectionException, ReaderException {

        setupManager.setOperatingMode(SetupManager.OPERATING_MODE_TYPES.ACTIVE);
    }

    @Override
    public void standbyEvent() throws ConnectionException, ReaderException {

        setupManager.setOperatingMode(SetupManager.OPERATING_MODE_TYPES.STANDBY);
    }

    @Override
    public void EventFound(Object o, EventInfo eventInfo) {

    }
}
