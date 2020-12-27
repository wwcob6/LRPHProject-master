package com.app.aidl;

import android.os.RemoteException;

import com.app.model.TaskInfo;
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.TaskInfoInterface;

import java.util.ArrayList;
import java.util.List;

import static com.app.sip.SipInfo.devId;

/**
 * Author chzjy
 * Date 2016/12/22.
 */

public class TaskImpl extends TaskInfoInterface.Stub {
    @Override
    public List<TaskInfo> getTaskInfo() throws RemoteException {
        List<TaskInfo> taskInfos= new ArrayList<>();
        taskInfos.addAll(SipInfo.tasklist);
        SipInfo.tasklist.clear();
        return taskInfos;
    }

    @Override
    public void sendConserveTask(String conserveTaskId, int seq, String direction, String lane, String roadCondition) throws RemoteException {
        SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to
                , SipInfo.dev_from, BodyFactory.createMaintEvent(conserveTaskId,seq,direction,lane,roadCondition)));
    }

    @Override
    public void sendTaskCheck(String TaskId) throws RemoteException {
        SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to
                , SipInfo.dev_from, BodyFactory.createTaskCheck(TaskId)));
    }

    @Override
    public void sendTaskReplyStart(String taskId, String timeType, String time) throws RemoteException {
        SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to
                , SipInfo.dev_from, BodyFactory.createTaskReplyTimeBody(devId,taskId,timeType,time)));
    }

    @Override
    public void sendTaskReplyFee(String taskId, String accarNum, String fee, String feeActual) throws RemoteException {
        SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to
                , SipInfo.dev_from, BodyFactory.createTaskReplyFeeBody(taskId,accarNum,fee,feeActual)));
    }

    @Override
    public void sendTaskReplySatisfaction(String taskId, String sat) throws RemoteException {
        SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to
                , SipInfo.dev_from, BodyFactory.createTaskReplySatisfactionBody(devId,taskId,sat)));
    }

    @Override
    public void sendTaskComplete(String taskId) throws RemoteException {
        SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to
                , SipInfo.dev_from, BodyFactory.createTaskCompleteBody(taskId)));
    }

    @Override
    public String getDevId() throws RemoteException {
        return SipInfo.devId;
    }
}
