package framework.templates.springbootwebflux.service.web.domain;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

public class ServiceHeaders {
    public static final Set<MediaType> RANDOM_QUOTE_ALLOWED_ACCEPT_TYPES = Stream.of(APPLICATION_JSON, TEXT_PLAIN).collect(toSet());
    public static final Set<MediaType> SEARCH_QUOTE_ALLOWED_ACCEPT_TYPES = Stream.of(APPLICATION_JSON, TEXT_PLAIN).collect(toSet());

    public static MediaType getFirstValidAcceptHeader(Set<MediaType> mediaTypeSet, List<MediaType> requestHeaderAcceptType) {
        return mediaTypeSet.stream()
                .filter(mediaType -> requestHeaderAcceptType.stream().anyMatch(mediaType::isCompatibleWith))
                .findFirst()
                .orElse(null);
    }
}
