package com.ydttech.iot.util.modbus;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.facade.ModbusTCPMaster;
import net.wimpi.modbus.util.BitVector;

public interface IModbusMaster {

    void setDIAmount(int amount);
    void setDOAmount(int amount);
    int getDIAmount();
    int getDOAmount();
    void setRefAddress(int refAddress);
    boolean setDOValue(int doIndex, boolean doValue) throws ModbusException;
    BitVector getDOValue(int doIndex) throws ModbusException;
    BitVector getDIValue(int diIndex) throws ModbusException;
    boolean isAlive();
    ModbusTCPMaster createConn() throws Exception;
    void stopConn();

    void doCoilsDetect() throws ModbusException;
    void doPulseDetect() throws ModbusException;
    void diCoilsDetect() throws ModbusException;

    void registerEvent(CoilsEventListener eventListener);
    void activeEvent();
    void standbyEvent();

}
