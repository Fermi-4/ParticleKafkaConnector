package com.github.fermi4.particle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.fermi4.particle.sse.Event;
import com.github.fermi4.particle.sse.EventProvider;
import com.github.fermi4.particle.sse.providers.CompositeEventProvider;

public class CompositeEventProviderTest {
	
	/**
	 * 
	 * Tests the CompositeEventProvider with two fake SSE providers pointing to separate servers.
     * Joins them into a single composite provider. The total data received should be the sum
     * of both providers if the composite worked correctly.
	 * 
	 * @throws InterruptedException
	 * @throws AssertionError
	 */
	@Test
	public void testTwoProvidersInComposite() throws InterruptedException, AssertionError {
		
		int EXPECTED_DATA_COUNT = 2;
		
		// Create mocks
		EventProvider fakeSSEProvider1 = mock(EventProvider.class);
		EventProvider fakeSSEProvider2 = mock(EventProvider.class);

		// Stub the get() method to return dummy data
        when(fakeSSEProvider1.get()).thenReturn(Arrays.asList(new Event("id", "type", "data")));
        when(fakeSSEProvider2.get()).thenReturn(Arrays.asList(new Event("id", "type", "data")));

		EventProvider provider = CompositeEventProvider.builder()
				.addProvider(fakeSSEProvider1)
				.addProvider(fakeSSEProvider2)
				.build();
		provider.start();
		
		// should return the combined output of each provider
		List<?> records = provider.get();
		
		// assertions
		Assertions.assertEquals(EXPECTED_DATA_COUNT, records.size());
	}
}
