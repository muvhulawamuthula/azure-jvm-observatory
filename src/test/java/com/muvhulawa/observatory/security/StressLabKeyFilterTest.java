package com.muvhulawa.observatory.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class StressLabKeyFilterTest {

    private static final String KEY = "s3cr3t";

    private MockHttpServletResponse filter(StressLabKeyFilter filter,
                                           MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    private MockHttpServletRequest load(String key) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/load/cpu");
        if (key != null) {
            request.addHeader(StressLabKeyFilter.HEADER, key);
        }
        return request;
    }

    @Test
    void rejectsRequestWithoutKey() throws Exception {
        MockHttpServletResponse response = filter(new StressLabKeyFilter(KEY), load(null));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void rejectsRequestWithWrongKey() throws Exception {
        MockHttpServletResponse response = filter(new StressLabKeyFilter(KEY), load("wrong"));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void allowsRequestWithCorrectKey() throws Exception {
        MockHttpServletRequest request = load(KEY);
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new StressLabKeyFilter(KEY).doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(chain.getRequest(), "request should pass through to the chain");
    }

    @Test
    void failsClosedWhenNoKeyConfigured() throws Exception {
        // Even a "correct-looking" request is rejected when no key is configured.
        MockHttpServletResponse response = filter(new StressLabKeyFilter(""), load(KEY));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void doesNotFilterNonStressEndpoints() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/metrics/jvm");
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // No key configured and none provided, yet metrics must remain open.
        new StressLabKeyFilter("").doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(chain.getRequest(), "metrics request should pass through to the chain");
    }
}
