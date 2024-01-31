package com.fermi4.particle.validate;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.common.config.ConfigDef.Validator;
import org.apache.kafka.common.config.ConfigException;

/**
 * Validates configuration entry which belongs to a group of possible options and is
 * not null
 * 
 * @author Fermi-4
 *
 * @param <T>
 */
public class OneOfValidator<T> implements Validator {

	private final List<T> validOptions;
	private final Class<T> clazz;

	@SafeVarargs
	private OneOfValidator(Class<T> clazz, T... o) {
		this.validOptions = Arrays.asList(o);
		this.clazz = clazz;
	}

	@Override
	public void ensureValid(String name, Object value) {
		if (name == null) {
			throw new ConfigException(name, value, String.format("Configuration parameter [%s] failed validation. Key was null", name));
		}
		if (value == null) {
			throw new ConfigException(name, value,
					String.format("Configuration parameter [%s] failed validation. Value was null", name));
		}
		if (!this.clazz.isAssignableFrom(value.getClass())) {
			throw new ConfigException(name, value, String.format(
					"Configuration parameter [%s] failed validation. Value was not of expected type... expected [%s] and got [%s]",
					name, this.clazz.getName(), value.getClass().getName()));
		}
		if (!validOptions.contains(value)) {
			throw new ConfigException(name, value,
					String.format(
							"Configuration parameter [%s] failed validation. Value [%s] does not belong in group [%s]",
							name, value, this.validOptions));
		}
	}

	@SafeVarargs
	public static <T> OneOfValidator<T> isOneOfAndNotNull(Class<T> clazz, T... o) {
		return new OneOfValidator<T>(clazz, o);
	}

}
