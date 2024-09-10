package org.sdamc.DomainObject;

public abstract class DomainObject {

    public abstract String getTableName();

    public abstract int getId();

    // For lazy load
    protected boolean initialed = false;

}