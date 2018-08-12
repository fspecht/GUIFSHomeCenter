package com.fsolutions.homecenter.devices;

/**
 * Created by specht on 20/03/17.
 */

public class DeviceDefinition{
    private String moduleID;
    private Class dataclass;
    private Class activityclass;

    public DeviceDefinition(String moduleID, Class dataclass, Class activityclass){
        if(moduleID==null||dataclass==null||activityclass==null){
            throw new IllegalArgumentException("Argument can not be null");
        }

        this.moduleID = moduleID;
        this.dataclass = dataclass;
        this.activityclass = activityclass;
    }

    public String getModuleID() {
        return moduleID;
    }

    public Class getDataclass() {
        return dataclass;
    }

    public Class getActivityclass() {
        return activityclass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!DeviceDefinition.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final DeviceDefinition other = (DeviceDefinition) obj;

        if (this.moduleID != other.moduleID) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return moduleID.hashCode();
    }
}