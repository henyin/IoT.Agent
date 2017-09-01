package com.ydttech.iot.util.modbus;

import net.wimpi.modbus.facade.ModbusTCPMaster;

abstract class ModbusMaster {

    ModbusTCPMaster modbusTCPMaster = null;
    String ipAddress = "localhost";
    int port = 502;
    int doAmount, diAmount, refAddress;
    DICoilsMessage diCoilsMessage;
    DOCoilsMessage doCoilsMessage;
    DOCoilsMessage doPulseMessage;
    boolean enablePulse = false;
    boolean isAlive = false;
    Thread selfThread = null;
    boolean threadIsRunning = false;
    CoilsEventListener coilsEventListener;
    long eventTimer = 100;

}
