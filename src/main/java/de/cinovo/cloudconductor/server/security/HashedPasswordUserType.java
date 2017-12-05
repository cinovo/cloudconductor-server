package de.cinovo.cloudconductor.server.security;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import de.taimos.dvalin.jaxrs.security.HashedPassword;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class HashedPasswordUserType implements CompositeUserType, Serializable {
	
	private static final String PROP_OFFSET = "roundOffset";
	
	public static final String TYPE = "de.cinovo.cloudconductor.server.security.HashedPasswordUserType";
	
	private static final long serialVersionUID = 3971017062432792007L;
	
	private static final Type[] SQL_TYPES = new Type[] {StandardBasicTypes.INTEGER, StandardBasicTypes.STRING, StandardBasicTypes.STRING};
	private static final String[] PROPERTIES = new String[] {"roundOffset", "hash", "salt"};
	
	
	@Override
	public String[] getPropertyNames() {
		return HashedPasswordUserType.PROPERTIES;
	}
	
	@Override
	public Type[] getPropertyTypes() {
		return HashedPasswordUserType.SQL_TYPES;
	}
	
	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		if (component == null) {
			return null;
		}
		if (component instanceof HashedPassword) {
			switch (property) {
			case 0:
				return ((HashedPassword) component).getRoundOffset();
			case 1:
				return ((HashedPassword) component).getHash();
			case 2:
				return ((HashedPassword) component).getSalt();
			}
		}
		return null;
	}
	
	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		if (component == null) {
			return;
		}
		if (component instanceof HashedPassword) {
			switch (property) {
			case 0:
				((HashedPassword) component).setRoundOffset((Integer) value);
				break;
			case 1:
				((HashedPassword) component).setHash((String) value);
				break;
			case 2:
				((HashedPassword) component).setSalt((String) value);
				break;
			}
		}
	}
	
	@Override
	public Class<HashedPassword> returnedClass() {
		return HashedPassword.class;
	}
	
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == y) || ((x != null) && x.equals(y));
	}
	
	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}
	
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		final Integer roundOffset = (Integer) StandardBasicTypes.INTEGER.nullSafeGet(rs, names[0], session, owner);
		final String hash = (String) StandardBasicTypes.STRING.nullSafeGet(rs, names[1], session, owner);
		final String salt = (String) StandardBasicTypes.STRING.nullSafeGet(rs, names[2], session, owner);
		if ((roundOffset == null) || (hash == null) || (salt == null)) {
			return null;
		}
		return new HashedPassword(roundOffset, hash, salt);
	}
	
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			StandardBasicTypes.INTEGER.nullSafeSet(st, null, index, session);
			StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session);
			StandardBasicTypes.STRING.nullSafeSet(st, null, index + 2, session);
		} else {
			HashedPassword hp = (HashedPassword) value;
			StandardBasicTypes.INTEGER.nullSafeSet(st, hp.getRoundOffset(), index, session);
			if (hp.getHash() == null) {
				StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session);
			} else {
				StandardBasicTypes.STRING.nullSafeSet(st, hp.getHash(), index + 1, session);
			}
			if (hp.getHash() == null) {
				StandardBasicTypes.STRING.nullSafeSet(st, null, index + 2, session);
			} else {
				StandardBasicTypes.STRING.nullSafeSet(st, hp.getSalt(), index + 2, session);
			}
		}
	}
	
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) {
			return null;
		}
		if (value instanceof HashedPassword) {
			return new HashedPassword(((HashedPassword) value).getRoundOffset(), ((HashedPassword) value).getHash(), ((HashedPassword) value).getSalt());
		}
		return value;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		return (Serializable) value;
	}
	
	@Override
	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
		return cached;
	}
	
	@Override
	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
		return this.deepCopy(original);
	}
	
}
