package com.app.video;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class StreamBuf {
    private int len;
    private int curLen;
    private int readyLen;
    private boolean isReady;
    private StreamBufNode head;
    private StreamBufNode tail;

    public StreamBuf(int len, int readyLen) {
        this.len = len;
        this.readyLen = readyLen;
        curLen = 0;
        isReady = false;
        head = null;
        tail = null;
    }

    public boolean isEmpty() {
        return curLen == 0;
    }

    public int getCurLen() {
        return curLen;
    }

    public boolean isReady() {
        return isReady;
    }

    public StreamBufNode getFromBuf() {
        if (isEmpty()) {
            return null;
        } else {
            StreamBufNode node = new StreamBufNode(head);
            head = head.getNext();
            curLen --;
            if (curLen < readyLen) {
                isReady = false;
            }
            return node;
        }
    }

    public boolean addToBufBySeq(StreamBufNode node) {
        if (curLen >= len) {
            return false;
        }
        if (curLen == 0) {
            head = node;
            tail = node;
            curLen++;
            isReady = false;
            return true;
        }
        if (node.getSeqNum() > tail.getSeqNum()) {
            tail.setNext(node);
            tail = node;
        } else if (node.getSeqNum() < head.getSeqNum()) {
            node.setNext(head);
            head = node;
        } else {
            StreamBufNode temp = head;
            while (temp.getNext() != null) {
                if (node.getSeqNum() < temp.getNext().getSeqNum()) {
                    node.setNext(temp.getNext());
                    temp.setNext(node);
                    break;
                } else {
                    temp = temp.getNext();
                }
            }
        }
        curLen++;
        isReady = (curLen >= readyLen);
        return true;
    }
}
