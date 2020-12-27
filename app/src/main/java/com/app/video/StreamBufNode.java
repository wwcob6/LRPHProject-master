package com.app.video;

import jlibrtp.DataFrame;

/**
 * Author chzjy
 * Date 2016/12/19.
 */
public class StreamBufNode {
    private DataFrame dataFrame;
    private int[] seqNums;
    private int seqNum;
    private StreamBufNode next;

    public StreamBufNode() {
    }

    public StreamBufNode(StreamBufNode streamBufNode) {
        this(streamBufNode.getDataFrame());
    }

    public StreamBufNode(DataFrame dataFrame) {
        if (dataFrame != null) {
            this.dataFrame = dataFrame;
            seqNums = dataFrame.sequenceNumbers();
            seqNum = seqNums[0];
        }
    }

    public DataFrame getDataFrame() {
        return dataFrame;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public StreamBufNode getNext() {
        return next;
    }

    public int getLen() {
        return dataFrame.getTotalLength();
    }

    public void setNext(StreamBufNode next) {
        this.next = next;
    }
}
