package com.muvhulawa.observatory.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end check that {@link StressLabKeyFilter} is wired into the servlet
 * chain and actually guards the live stress-lab endpoints, while leaving the
 * open metrics endpoint reachable.
 */
@SpringBootTest(properties = "stresslab.key=integration-test-key")
@AutoConfigureMockMvc
class StressLabSecurityIntegrationTest {

    private static final String KEY = "integration-test-key";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void stressEndpointRejectedWithoutKey() throws Exception {
        mockMvc.perform(post("/api/load/memory-clear"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void stressEndpointRejectedWithWrongKey() throws Exception {
        mockMvc.perform(post("/api/load/memory-clear").header(StressLabKeyFilter.HEADER, "nope"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void stressEndpointAllowedWithCorrectKey() throws Exception {
        mockMvc.perform(post("/api/load/memory-clear").header(StressLabKeyFilter.HEADER, KEY))
                .andExpect(status().isOk());
    }

    @Test
    void metricsEndpointOpenWithoutKey() throws Exception {
        mockMvc.perform(get("/api/metrics/jvm"))
                .andExpect(status().isOk());
    }
}
