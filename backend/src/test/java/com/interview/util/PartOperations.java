package com.interview.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.Part;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.interview.util.UtilityMethods.fromMvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AllArgsConstructor
public class PartOperations {
    private final MockMvc mvc;
    private final ObjectMapper om;

    public Part getPart(Long id) throws Exception {
        var result = mvc.perform(get("/api/part/{partId}", id))
                .andExpect(status().isOk())
                .andReturn();

        return fromMvcResult(result, Part.class);
    }

    public Part createPart(Part part) throws Exception {
        var result = mvc.perform(
                        post("/api/part")
                                .content(om.writeValueAsString(part))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return fromMvcResult(result, Part.class);
    }

    public Part updatePart(Long id, Part part) throws Exception {
        var result = mvc.perform(
                        put("/api/part/{partId}", id)
                                .content(om.writeValueAsString(part))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return fromMvcResult(result, Part.class);
    }

    public void deletePart(Long id) throws Exception {
        mvc.perform(delete("/api/part/{partId}", id))
                .andExpect(status().isOk())
                .andReturn();
    }
}
