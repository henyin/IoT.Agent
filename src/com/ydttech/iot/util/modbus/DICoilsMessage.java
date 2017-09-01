package com.ydttech.iot.util.modbus;

import net.wimpi.modbus.util.BitVector;

/**
 * Created by Ean.Chung on 2017/5/8.
 */
public class DICoilsMessage {

    private BitVector bitVector;
    private int diAmount;


    public DICoilsMessage(int amount) {
        diAmount = amount;
        bitVector = new BitVector(amount);
    }

    public int getDIAmount() {
        return diAmount;
    }

    public void setDIAmount(int doAmount) {
        bitVector = null;
        bitVector = new BitVector(doAmount);
        this.diAmount = doAmount;
    }

    public BitVector getBitVector() {
        return bitVector;
    }

    public void setBitVector(BitVector bitVector) {
        this.bitVector = bitVector;
    }
}
