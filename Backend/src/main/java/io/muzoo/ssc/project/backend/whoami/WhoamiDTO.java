package io.muzoo.ssc.project.backend.whoami;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WhoamiDTO {

    private boolean loggedIn = false;

    private String username;

    private String name;

    private String role;

    private String displayName;

    private Long id;

    @JsonIgnore
    private byte[] profilePicture;
}
