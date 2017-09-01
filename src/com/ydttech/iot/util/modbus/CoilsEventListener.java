package com.ydttech.iot.util.modbus;

/**
 * Created by Ean.Chung on 2017/5/8.
 */
public interface CoilsEventListener {
    void DIChangeEvent(DICoilsMessage diCoilsMessage);
    void DOChangeEvent(DOCoilsMessage doCoilsMessage);
}
