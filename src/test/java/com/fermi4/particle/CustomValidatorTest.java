package com.fermi4.particle;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import com.fermi4.particle.validate.OneOfValidator;

public class CustomValidatorTest {
	
	public OneOfValidator<String> validator = OneOfValidator.isOneOfAndNotNull(String.class, "all", "product", "dev");

	@Test 
	public void testOneOfValidator_WrongType() {
		assertThrows(ConfigException.class, () -> {
			validator.ensureValid("test_name", 123);			
		});
	}
	
	@Test
	public void testOneOfValidator_WrongValue() {
		assertThrows(ConfigException.class, () -> {
			validator.ensureValid("test_name", "ghost");	
		});
	}

	@Test
	public void testOneOfValidator_Null() {
		assertThrows(ConfigException.class, () -> {
			validator.ensureValid(null, null);			
		});
	}
	
	@Test
	public void testOneOfValidator_NullValue() {
		assertThrows(ConfigException.class, () -> {
			validator.ensureValid("test_name", null);			
		});
	}

	@Test
	public void testOneOfValidator_NullKey() {
		assertThrows(ConfigException.class, () -> {
			validator.ensureValid(null, "all");	
		});
	}
	
	@Test
	public void testOneOfValidator_ShouldPass() {
		List<String> str = Arrays.asList("all", "dev", "product");
		str.forEach(o -> validator.ensureValid("test", o));
	}
}
