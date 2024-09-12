package org.sdamc;

import org.sdamc.Mapper.DataMapper;
import org.sdamc.DomainObject.DomainObject;
import java.util.List;
import java.util.ArrayList;

public class UnitofWork {

    private static ThreadLocal<UnitofWork> current = ThreadLocal.withInitial(() -> null);

    ;

    private List<DomainObject> newObjects = new ArrayList<DomainObject>();

    private List<DomainObject> dirtyObjects = new ArrayList<DomainObject>();

    private List<DomainObject> deletedObjects = new ArrayList<DomainObject>();

    public static void newCurrent() {
        current.set(new UnitofWork());
    }

    public static UnitofWork getCurrent() {
        return current.get();
    }

    public void registerNew(DomainObject obj) {
        newObjects.add(obj);
    }

    public void registerDirty(DomainObject obj) {
        dirtyObjects.add(obj);
    }

    public void registerDeleted(DomainObject obj) {
        deletedObjects.add(obj);
    }

    public void commit() {
        for (DomainObject obj : newObjects) {
            DataMapper.GetMapper(obj.getTableName()).insert(obj);
        }
        for (DomainObject obj : dirtyObjects) {
            DataMapper.GetMapper(obj.getTableName()).update(obj);
        }
        for (DomainObject obj : deletedObjects) {
            DataMapper.GetMapper(obj.getTableName()).delete(obj);
        }
    }

}
