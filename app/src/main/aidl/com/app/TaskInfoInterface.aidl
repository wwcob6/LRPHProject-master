// TaskInfoInterface.aidl
package com.app;

// Declare any non-default types here with import statements
import com.app.model.TaskInfo;
interface TaskInfoInterface  {
    List<TaskInfo> getTaskInfo();
    //发送养护任务
    void sendConserveTask(String conserveTaskId,int seq,String direction, String lane, String roadCondition);
    //发送工单确认消息
    void sendTaskCheck(String TaskId);
    //发送工单开始执行时间消息
    void sendTaskReplyStart(String taskId, String timeType, String time);
    //发送工单收费情况消息
    void sendTaskReplyFee(String taskId, String accarNum, String fee, String feeActual);
    //发送工单满意度消息
    void sendTaskReplySatisfaction(String taskId, String sat);
    //发送工单完成消息
    void sendTaskComplete(String taskId);
    //获取设备id
    String getDevId();
}
