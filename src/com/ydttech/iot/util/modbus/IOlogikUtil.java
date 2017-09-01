package com.ydttech.iot.util.modbus;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.facade.ModbusTCPMaster;
import net.wimpi.modbus.util.BitVector;

public class IOlogikUtil extends ModbusMaster implements IModbusMaster {

    final int E2212_DI_AMOUNT = 8;
    final int E2212_DO_AMOUNT = 8;
    final int E2212_REF_ADDRESS = 0x0000;
    final int E2212_PULSE_ADDRESS = 0x0024;

    public IOlogikUtil(String ipAddress, int port, boolean enablePulse) {

        this.ipAddress = ipAddress;
        this.port = port;
        this.enablePulse = enablePulse;

        refAddress = E2212_REF_ADDRESS;
        diAmount = E2212_DI_AMOUNT;
        doAmount = E2212_DO_AMOUNT;


        if (diAmount > 0 )
            diCoilsMessage = new DICoilsMessage(diAmount);
        if (doAmount > 0) {
            doCoilsMessage = new DOCoilsMessage(doAmount);
            if (enablePulse)
                doPulseMessage = new DOCoilsMessage(doAmount);
        }

    }

    @Override
    public void setDIAmount(int amount) {
        diAmount = amount;
        diCoilsMessage = new DICoilsMessage(diAmount);
    }

    @Override
    public void setDOAmount(int amount) {
        doAmount = amount;
        doCoilsMessage.setDOAmount(doAmount);
        if (enablePulse)
            doPulseMessage = new DOCoilsMessage(doAmount);
    }

    @Override
    public int getDIAmount() {
        return diAmount;
    }

    @Override
    public int getDOAmount() {
        return doAmount;
    }

    @Override
    public boolean setDOValue(int doIndex, boolean doValue) throws ModbusException {

        Boolean resultCode = null;

        resultCode = modbusTCPMaster.writeCoil(1, refAddress + doIndex, doValue);
        return resultCode.booleanValue();

    }

    @Override
    public BitVector getDOValue(int doIndex) throws ModbusException {

        BitVector bitVector = null;

        bitVector = modbusTCPMaster.readCoils(refAddress, doAmount);

        return bitVector;
    }

    @Override
    public BitVector getDIValue(int diIndex) throws ModbusException {

        BitVector bitVector = null;

        bitVector = modbusTCPMaster.readInputDiscretes(refAddress, doAmount);

        return bitVector;
    }

    @Override
    public void setRefAddress(int refAddress) {
        this.refAddress = refAddress;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public ModbusTCPMaster createConn() throws Exception {

        modbusTCPMaster = new ModbusTCPMaster(ipAddress, port);
        modbusTCPMaster.connect();
        isAlive = true;

        return modbusTCPMaster;
    }

    @Override
    public void stopConn() {

        modbusTCPMaster.disconnect();
        isAlive = false;
    }

    @Override
    public void doCoilsDetect() throws ModbusException {

        BitVector bitVector;

        if (doCoilsMessage.getDOAmount() > 0) {

            boolean isDOChanged = false;

            bitVector = modbusTCPMaster.readCoils(refAddress, doCoilsMessage.getDOAmount());
            for (int index = 0; index < doAmount; index++) {
                if (bitVector.getBit(index) != doCoilsMessage.getBitVector().getBit(index)) {
                    isDOChanged = true;
                    break;
                }
            }

            doCoilsMessage.setBitVector(bitVector);
            if (isDOChanged) {
                coilsEventListener.DOChangeEvent(doCoilsMessage);
            }
        }
    }

    @Override
    public void doPulseDetect() throws ModbusException {

        BitVector bitVector;

        if (doPulseMessage.getDOAmount() > 0) {

            boolean isDOChanged = false;

            bitVector = modbusTCPMaster.readCoils(E2212_PULSE_ADDRESS, doPulseMessage.getDOAmount());
            for (int index = 0; index < doAmount; index++) {
                if (bitVector.getBit(index) != doPulseMessage.getBitVector().getBit(index)) {
                    isDOChanged = true;
                    break;
                }
            }

            doPulseMessage.setBitVector(bitVector);
            if (isDOChanged) {
                coilsEventListener.DOChangeEvent(doPulseMessage);
            }
        }
    }

    @Override
    public void diCoilsDetect() throws ModbusException {

        BitVector bitVector;

        if (diCoilsMessage.getDIAmount() > 0) {

            boolean isDIChanged = false;

            bitVector = modbusTCPMaster.readInputDiscretes(refAddress, diCoilsMessage.getDIAmount());
            for (int index = 0; index < diAmount; index++) {
                if (bitVector.getBit(index) != diCoilsMessage.getBitVector().getBit(index)) {
                    isDIChanged = true;
                    break;
                }
            }

            diCoilsMessage.setBitVector(bitVector);
            if (isDIChanged) {
                coilsEventListener.DIChangeEvent(diCoilsMessage);
            }
        }
    }

    @Override
    public void registerEvent(CoilsEventListener eventListener) {
        this.coilsEventListener = eventListener;
    }

    @Override
    public void activeEvent() {
        threadIsRunning = true;
        selfThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(threadIsRunning) {
                    if (selfThread != null && modbusTCPMaster != null) {
                        try {
                            doCoilsDetect();
                            Thread.sleep(eventTimer);
                            diCoilsDetect();
                            Thread.sleep(eventTimer);
                            if (enablePulse) {
                                doPulseDetect();
                                Thread.sleep(eventTimer);
                            }
                        } catch (Exception e) {
                            threadIsRunning = false;
                            stopConn();
                            break;
                        }
                    }
                    Thread.yield();
                }
            }
        });
        selfThread.setDaemon(true);
        selfThread.start();
    }

    @Override
    public void standbyEvent() {
        threadIsRunning = false;
    }
}
