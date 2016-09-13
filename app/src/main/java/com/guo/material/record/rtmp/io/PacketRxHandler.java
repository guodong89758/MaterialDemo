package com.guo.material.record.rtmp.io;


import com.guo.material.record.rtmp.packets.RtmpPacket;

/**
 * Handler interface for received RTMP packets
 * @author francois
 */
public interface PacketRxHandler {
    
    public void handleRxPacket(RtmpPacket rtmpPacket);
    
    public void notifyWindowAckRequired(final int numBytesReadThusFar);    
}
