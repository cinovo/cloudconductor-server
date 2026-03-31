package de.cinovo.cloudconductor.server;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Postgresql connection pool configuration using HikariCP. Hikari acts as a wrapper for the underlying datasource.
 * <p>
 * Copyright 2025 Cinovo AG<br>
 * <br>
 *
 * @author lkomarek
 */
@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "POSTGRESQL")
public class HikariConnectionPoolConfig {
	
	@Value("${ds.pgsql.host}")
	private String host;
	
	@Value("${ds.pgsql.port}")
	private int port;
	
	@Value("${ds.pgsql.db}")
	private String database;
	
	@Value("${ds.pgsql.user}")
	private String username;
	
	@Value("${ds.pgsql.password}")
	private String password;
	
	@Value("${ds.pgsql.sslMode:}")
	private String sslMode;
	
	// connection pool configuration
	@Value("${ds.pgsql.maxPoolSize:10}")
	private int maxPoolSize;
	
	@Value("${ds.pgsql.minIdle:2}")
	private int minIdle;
	
	@Value("${ds.pgsql.connectionTimeout:30000}")
	private int connectionTimeout;
	
	@Value("${ds.pgsql.idleTimeout:600000}")
	private int idleTimeout;
	
	@Value("${ds.pgsql.maxLifetime:1800000}")
	private int maxLifetime;
	
	/**
	 * HikariCP with postgres datasource. You can disable hikari with 'ds.hikari.enabled=false'
	 *
	 * @return datasource bean
	 */
	@Bean(name = "dataSourceHikari")
	@Primary // surpress de.taimos.dvalin.jpa.config.DatabasePOSTGRESQL.dataSource()
	public DataSource dataSource() {
		
		// check 'ds.hikari.enabled' flag
		String hikariDataSourceFlag = System.getProperty("ds.hikari.enabled");
		if (hikariDataSourceFlag != null && !hikariDataSourceFlag.isEmpty() && !Boolean.parseBoolean(hikariDataSourceFlag)) {
			return this.initDataSource();
		}
		
		// Hikari acts as a wrapper for the underlying datasource
		HikariConfig poolConf = this.setupHikariConnectionPool(this.initDataSource());
		// hint: use hikaricp.configurationFile property for a more complex configuration
		return new HikariDataSource(poolConf);
	}
	
	private HikariConfig setupHikariConnectionPool(DataSource dataSource) {
		HikariConfig poolConf = new HikariConfig();
		poolConf.setDataSource(dataSource);
		poolConf.setPoolName("HikariConnectionPool");
		poolConf.setMaximumPoolSize(this.maxPoolSize);
		poolConf.setMinimumIdle(this.minIdle);
		
		poolConf.setConnectionTimeout(this.connectionTimeout);
		poolConf.setIdleTimeout(this.idleTimeout);
		poolConf.setMaxLifetime(this.maxLifetime);
		
		poolConf.setRegisterMbeans(false); //  JMX monitoring
		return poolConf;
	}
	
	
	private String buildPostgreSQLUrl() {
		StringBuilder url = new StringBuilder("jdbc:postgresql://");
		url.append(this.host).append(":").append(this.port).append("/").append(this.database);
		url.append("?tcpKeepAlive=true");
		if (this.sslMode != null && !this.sslMode.isEmpty()) {
			url.append("&sslmode=").append(this.sslMode);
		}
		url.append("&ApplicationName=").append(HikariConnectionPoolConfig.getServiceName());
		return url.toString();
	}
	
	private static String getServiceName() {
		return DaemonStarter.getDaemonName();
	}
	
	
	private DataSource initDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl(this.buildPostgreSQLUrl());
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		// setLoginTimeout not supported from javax.sql.DataSource: default 5 seconds, ignore startup fail message
		return dataSource;
	}
}
