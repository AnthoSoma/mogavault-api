package com.mogavault.api.i18n;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/i18n")
public class I18nController {

    @GetMapping(value = "/{lang}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getTranslations(@PathVariable String lang) {
        String sanitizedLang = lang.equalsIgnoreCase("en") ? "en" : "fr";
        Resource resource = new ClassPathResource("i18n/" + sanitizedLang + ".json");

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Cache HTTP : évite au front de re-télécharger le dictionnaire à chaque rechargement
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .body(resource);
    }
}