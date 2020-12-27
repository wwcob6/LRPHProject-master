package com.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acer on 2016/11/27.
 */

public class TaskInfo implements Parcelable {
    private String id;
    private String dev_id;
    private String receiver;
    private String receive_time;
    private String info_source;
    private String task_type;
    private String task_id;
    private String task_name;
    private String order;
    private String location;
    private String event_time;
    private String pic_path_down;
    private String vehicle_fault_num;
    private String vehicle_fault_type;
    private String parking_position;
    private String cargo;
    private String wrecker_num;
    private String wrecker_type;
    private String driver;
    private String co_driver;
    private String state;
    private String TxPath;
    public TaskInfo(){
    }
    protected TaskInfo(Parcel in) {
        id = in.readString();
        dev_id = in.readString();
        receiver = in.readString();
        receive_time = in.readString();
        info_source = in.readString();
        task_type = in.readString();
        task_id = in.readString();
        task_name = in.readString();
        order = in.readString();
        location = in.readString();
        event_time = in.readString();
        pic_path_down = in.readString();
        vehicle_fault_num = in.readString();
        vehicle_fault_type = in.readString();
        parking_position = in.readString();
        cargo = in.readString();
        wrecker_num = in.readString();
        wrecker_type = in.readString();
        driver = in.readString();
        co_driver = in.readString();
        state = in.readString();
        TxPath = in.readString();
    }

    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        @Override
        public TaskInfo createFromParcel(Parcel in) {
            return new TaskInfo(in);
        }

        @Override
        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };


    public String getId() {
        return id;
    }

    public String getDev_id() {
        return dev_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getReceive_time() {
        return receive_time;
    }

    public String getInfo_source() {
        return info_source;
    }

    public String getTask_type() {
        return task_type;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public String getOrder() {
        return order;
    }

    public String getLocation() {
        return location;
    }

    public String getEvent_time() {
        return event_time;
    }

    public String getPic_path_down() {
        return pic_path_down;
    }

    public String getVehicle_fault_num() {
        return vehicle_fault_num;
    }

    public String getVehicle_fault_type() {
        return vehicle_fault_type;
    }

    public String getParking_position() {
        return parking_position;
    }

    public String getCargo() {
        return cargo;
    }

    public String getWrecker_num() {
        return wrecker_num;
    }

    public String getWrecker_type() {
        return wrecker_type;
    }

    public String getDriver() {
        return driver;
    }

    public String getCo_driver() {
        return co_driver;
    }

    public String getState() {
        return state;
    }

    public String getTxPath() {
        return TxPath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public void setInfo_source(String info_source) {
        this.info_source = info_source;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public void setPic_path_down(String pic_path_down) {
        this.pic_path_down = pic_path_down;
    }

    public void setVehicle_fault_num(String vehicle_fault_num) {
        this.vehicle_fault_num = vehicle_fault_num;
    }

    public void setVehicle_fault_type(String vehicle_fault_type) {
        this.vehicle_fault_type = vehicle_fault_type;
    }

    public void setParking_position(String parking_position) {
        this.parking_position = parking_position;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setWrecker_num(String wrecker_num) {
        this.wrecker_num = wrecker_num;
    }

    public void setWrecker_type(String wrecker_type) {
        this.wrecker_type = wrecker_type;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setCo_driver(String co_driver) {
        this.co_driver = co_driver;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTxPath(String txPath) {
        TxPath = txPath;
    }


    /**养护工单id*/
    public static String conserve_Task_Id;
    /**养护桩号?*/
    public static int seq;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(dev_id);
        dest.writeString(receiver);
        dest.writeString(receive_time);
        dest.writeString(info_source);
        dest.writeString(task_id);
        dest.writeString(task_name);
        dest.writeString(task_type);
        dest.writeString(order);
        dest.writeString(location);
        dest.writeString(event_time);
        dest.writeString(pic_path_down);
        dest.writeString(vehicle_fault_num);
        dest.writeString(vehicle_fault_type);
        dest.writeString(parking_position);
        dest.writeString(cargo);
        dest.writeString(wrecker_num);
        dest.writeString(wrecker_type);
        dest.writeString(driver);
        dest.writeString(co_driver);
        dest.writeString(state);
        dest.writeString(TxPath);
    }
}
