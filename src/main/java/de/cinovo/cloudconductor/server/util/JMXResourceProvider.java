package de.cinovo.cloudconductor.server.util;

import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EServiceState;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Component
public class JMXResourceProvider implements DynamicMBean {
	
	private static final String INTEGER_TYPE = "Integer";
	
	private static final String SERVICES_STOPPED = "services-stopped";
	
	private static final String SERVICES_RUNNING = "services-running";
	
	private static final String HOSTS_DOWN = "hosts-down";
	
	private static final String HOSTS_LIVE = "hosts-live";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JMXResourceProvider.class);
	
	@Autowired
	protected IServiceStateDAO dSvcState;
	
	@Autowired
	private IHostDAO dHost;
	
	private MBeanAttributeInfo[] attributeInfos;
	private MBeanInfo beanInfo;
	
	
	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		this.attributeInfos = new MBeanAttributeInfo[4];
		this.attributeInfos[0] = new MBeanAttributeInfo(JMXResourceProvider.HOSTS_LIVE, JMXResourceProvider.INTEGER_TYPE, "living hosts", true, false, false);
		this.attributeInfos[1] = new MBeanAttributeInfo(JMXResourceProvider.HOSTS_DOWN, JMXResourceProvider.INTEGER_TYPE, "dead hosts", true, false, false);
		this.attributeInfos[2] = new MBeanAttributeInfo(JMXResourceProvider.SERVICES_RUNNING, JMXResourceProvider.INTEGER_TYPE, "running services", true, false, false);
		this.attributeInfos[3] = new MBeanAttributeInfo(JMXResourceProvider.SERVICES_STOPPED, JMXResourceProvider.INTEGER_TYPE, "stopped services", true, false, false);
		this.beanInfo = new MBeanInfo(this.getClass().getName(), "", this.attributeInfos, null, null, null);
	}
	
	@Override
	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
		switch (attribute) {
		case JMXResourceProvider.SERVICES_STOPPED:
			return this.getStoppedServices();
		case JMXResourceProvider.SERVICES_RUNNING:
			return this.getStartedServices();
		case JMXResourceProvider.HOSTS_DOWN:
			return this.getDeadHosts();
		case JMXResourceProvider.HOSTS_LIVE:
			return this.getLiveHosts();
		}
		
		return null;
	}
	
	private Integer getLiveHosts() {
		int count = 0;
		DateTime now = new DateTime();
		for (EHost h : this.dHost.findList()) {
			if (now.minusMinutes(15).getMillis() <= h.getLastSeen()) {
				count++;
			}
		}
		return count;
	}
	
	private Integer getDeadHosts() {
		int count = 0;
		DateTime now = new DateTime();
		for (EHost h : this.dHost.findList()) {
			if (now.minusMinutes(15).getMillis() > h.getLastSeen()) {
				count++;
			}
		}
		return count;
	}
	
	private Integer getStartedServices() {
		int count = 0;
		for (EServiceState ss : this.dSvcState.findList()) {
			switch (ss.getState()) {
			
			case STARTING:
			case STARTED:
			case IN_SERVICE:
				count++;
				break;
			
			default:
				break;
			}
		}
		return count;
	}
	
	private Integer getStoppedServices() {
		int count = 0;
		for (EServiceState ss : this.dSvcState.findList()) {
			switch (ss.getState()) {
			case STOPPED:
			case STOPPING:
				count++;
				break;
			
			default:
				break;
			}
		}
		return count;
	}
	
	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		// nothing to do here
	}
	
	@Override
	public AttributeList getAttributes(String[] attributes) {
		AttributeList result = new AttributeList();
		for (String attribute : attributes) {
			try {
				result.add(this.getAttribute(attribute));
			} catch (AttributeNotFoundException | MBeanException | ReflectionException e) {
				JMXResourceProvider.LOGGER.error("Failed to collect attribute for JMX", e);
			}
		}
		return result;
	}
	
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		// nothing to do here
		return null;
	}
	
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		// nothing to do here
		return null;
	}
	
	@Override
	public MBeanInfo getMBeanInfo() {
		return this.beanInfo;
	}
	
}
