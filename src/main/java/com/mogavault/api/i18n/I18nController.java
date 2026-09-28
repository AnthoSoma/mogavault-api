package com.mogavault.api.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/i18n")
public class I18nController {

    @GetMapping(value = "/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getTranslations(HttpServletRequest request) {
        // Extrait tout ce qui se trouve après "/api/v1/i18n/"
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String prefix = "/api/v1/i18n/";
        String relativePath = path.startsWith(prefix) ? path.substring(prefix.length()) : path;

        // Découpe le chemin pour isoler le fichier de langue (dernier segment)
        String[] parts = relativePath.split("/");
        if (parts.length == 0 || parts[0].isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Le dernier segment est toujours la langue ("fr", "en", etc.)
        String rawLang = parts[parts.length - 1];
        String sanitizedLang = rawLang.equalsIgnoreCase("en") ? "en" : "fr";

        String targetResourcePath;
        if (parts.length == 1) {
            // Cas du tronc commun
            targetResourcePath = "i18n/messages_" + sanitizedLang + ".json";
        } else {
            // Cas d'un scope imbriqué
            StringBuilder scopePath = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                String cleanPart = parts[i].replaceAll("[^a-zA-Z0-9_-]", "");
                if (!cleanPart.isBlank()) {
                    scopePath.append(cleanPart).append("/");
                }
            }
            targetResourcePath = "i18n/" + scopePath + sanitizedLang + ".json";
        }

        Resource resource = new ClassPathResource(targetResourcePath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .body(resource);
    }
}