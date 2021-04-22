package com.rsh.easy_opm.session;

import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.executor.BaseCqlExecutor;
import org.neo4j.driver.Driver;


public class DefaultCqlSession extends DefaultSession{
    public DefaultCqlSession(Configuration config, Driver driver) {
        this.config = config;
        this.executor = new BaseCqlExecutor(driver, this);
    }
}
