package com.rsh.easy_opm.springfactorybean;

import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.session.DefaultSession;
import com.rsh.easy_opm.session.SessionFactory;
import org.springframework.beans.factory.FactoryBean;

public class MapperFactoryBean<T> implements FactoryBean<T> {
    private SessionFactory sessionFactory;
    private DefaultSession session;
    private Class<T> mapperInterface;
    private SessionFactory.DB_TYPE db_type;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setDb_type(SessionFactory.DB_TYPE db_type) {
        this.db_type = db_type;
    }

    public DefaultSession getSession() {
        AssertError.notFoundError(db_type != null, "Member db_type", "MapperFactoryBean");
        AssertError.notFoundError(sessionFactory != null, "Member sessionFactory", "MapperFactoryBean");
        if (session == null)
            session = sessionFactory.getSession(db_type);
        return session;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T getObject(){
        AssertError.notFoundError(mapperInterface != null, "Member mapperInterface", "MapperFactoryBean");
        return getSession().getMapper(mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return mapperInterface;
    }
}
