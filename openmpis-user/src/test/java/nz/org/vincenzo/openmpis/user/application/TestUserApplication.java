/*
 * This file is part of OpenMPIS, the Open Source Missing Persons Information System.
 * Copyright (C) 2008-2017  Rey Vincent Babilonia <rvbabilonia@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nz.org.vincenzo.openmpis.user.application;

import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import nz.org.vincenzo.openmpis.common.database.BerkeleyTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

/**
 * The OpenMPIS user microservice application.
 *
 * @author Rey Vincent Babilonia
 * @since 2.0.0
 */
@Configuration
@ComponentScan(value = "nz.org.vincenzo.openmpis.user",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = UserApplication.class))
@PropertySource(value = "classpath:test-application.properties", ignoreResourceNotFound = true)
public class TestUserApplication {

    private final Environment environment;

    /**
     * Default constructor.
     *
     * @param environment the {@link Environment}
     */
    @Autowired
    public TestUserApplication(Environment environment) {
        this.environment = environment;
    }

    /**
     * Returns the {@link PropertySourcesPlaceholderConfigurer}.
     *
     * @return the {@link PropertySourcesPlaceholderConfigurer}
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Returns the {@link EntityStore} to be used by data access objects.
     *
     * @return the {@link EntityStore}
     */
    @Bean
    public EntityStore entityStore() {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);

        return new EntityStore(bdbEnvironment(), "EntityStore", storeConfig);
    }

    /**
     * Returns the {@link PlatformTransactionManager}.
     *
     * @return the {@link PlatformTransactionManager}
     */
    @Bean
    public BerkeleyTransactionManager transactionManager() {
        return new BerkeleyTransactionManager(bdbEnvironment());
    }

    private com.sleepycat.je.Environment bdbEnvironment() {
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        String databaseDirectory = environment.getProperty("database.directory", "/tmp/openmpis/user");
        File file = new File(databaseDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        return new com.sleepycat.je.Environment(file, environmentConfig);
    }
}
