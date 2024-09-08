package com.interview.utility;

import org.hibernate.MappingException;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

/*
This is a custom dialect class to make sure that the tables all have id column set as auto increment
reference to this provided in application.properties
 */
public class CustomDialect extends H2Dialect {

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() throws MappingException {
        return new IdentityColumnSupportImpl(){
            @Override
            public String getIdentityColumnString(int type){
                return "AUTO_INCREMENT";
            }
        };
    }
}
