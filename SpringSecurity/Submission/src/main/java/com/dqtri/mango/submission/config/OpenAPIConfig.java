/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
        name = "refresh_token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
@SecurityScheme(
        name = "access_token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(info = @Info(title = "Submission API", version = "v1.0.0", description = "Sample RestAPIs application",
            contact = @Contact(email = "w.dquoctri@gmail.com", name = "Deadl!ne", url = "https://github.com/dquoctri"),
            license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")),
        externalDocs = @ExternalDocumentation(description = "Submission Wiki Documentation",
                url = "https://github.com/dquoctri/Mango/wiki")
)
public class OpenAPIConfig {
}
