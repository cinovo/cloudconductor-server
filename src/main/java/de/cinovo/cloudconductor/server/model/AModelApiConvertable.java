package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;

import java.beans.Transient;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AModelApiConvertable<T> {

	@Transient
	public abstract Class<T> getApiClass();

	@Transient
	public T toApi() {
		return GenericModelApiConverter.convert(this, this.getApiClass());
	}

	protected <K extends INamed> Set<String> namedModelToStringSet(Collection<K> models) {
		Set<String> result = new HashSet<>();
		for(K model : models) {
			if(!model.getName().isEmpty()) {
				result.add(model.getName());
			}
		}
		return result;
	}

	protected <M, K extends AModelApiConvertable<M>> Set<M> convertableModelToApiSet(Collection<K> models) {
		Set<M> result = new HashSet<>();
		for(AModelApiConvertable<M> model : models) {
			result.add(model.toApi());
		}
		return result;
	}
}
