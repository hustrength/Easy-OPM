package com.rsh.easy_opm.factorybean;

import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.sqlsession.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;


public class SessionFactoryBean implements FactoryBean<SessionFactory>, InitializingBean {
    private SessionFactory sessionFactory;
    private Resource configLocation;

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public SessionFactory getObject() throws Exception {
        if (this.sessionFactory == null) {
            afterPropertiesSet();
        }
        return this.sessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return sessionFactory == null ? SessionFactory.class : sessionFactory.getClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AssertError.notFoundError(configLocation != null, "Easy-OPM Configuration File");
        sessionFactory = new SessionFactory(configLocation);
    }
}
