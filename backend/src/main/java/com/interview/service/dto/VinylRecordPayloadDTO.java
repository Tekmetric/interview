package com.interview.service.dto;

import com.interview.resource.exception.ErrorConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public class VinylRecordPayloadDTO {

    @NotBlank(message = ErrorConstants.ERR_VR_TITLE_IS_REQUIRED)
    private String title;

    @NotEmpty(message = ErrorConstants.ERR_ARTISTS_ARE_REQUIRED)
    private List<Long> artistIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(List<Long> artistIds) {
        this.artistIds = artistIds;
    }
}
