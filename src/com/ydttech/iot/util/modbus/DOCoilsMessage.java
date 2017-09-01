package com.ydttech.iot.util.modbus;

import net.wimpi.modbus.util.BitVector;

/**
 * Created by Ean.Chung on 2017/5/9.
 */
public class DOCoilsMessage {

    private BitVector bitVector;
    private int doAmount;


    public DOCoilsMessage(int amount) {
        doAmount = amount;
        bitVector = new BitVector(amount);
    }

    public int getDOAmount() {
        return doAmount;
    }

    public void setDOAmount(int doAmount) {
        bitVector = null;
        bitVector = new BitVector(doAmount);
        this.doAmount = doAmount;
    }

    public BitVector getBitVector() {
        return bitVector;
    }

    public void setBitVector(BitVector bitVector) {
        this.bitVector = bitVector;
    }
}
