package br.com.ifood.connection.controller.pagination;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class to add HATEOAS _links (first, previous, next, last) to the paged resource.
 */
public class PagedResource<T extends ResourceSupport> {

    private String pageParameter;

    private String limitParameter;

    private Page<?> page;

    private T resource;

    private Object invocationValue;

    PagedResource(String pageParameter, String limitParameter) {
        this.pageParameter = pageParameter;
        this.limitParameter = limitParameter;
    }

    /**
     * Sets the {@link Page} object
     */
    public PagedResource<T> of(Page<?> page) {
        this.page = page;

        return this;
    }

    /**
     * Sets the HATEOAS {@link ResourceSupport}
     */
    public PagedResource<T> on(T resource) {
        this.resource = resource;

        return this;
    }

    /**
     * Sets the returned value from the proxy invocation of {@link ControllerLinkBuilder#methodOn(Class,
     * Object...)}.<br/> It is used to create a {@link Link} object.
     */
    public PagedResource<T> based(Object invocationValue) {
        this.invocationValue = invocationValue;

        return this;
    }

    public T build() {

        int limit = page.getSize();

        addFirstLink(limit);
        addPreviousLinkIfExists(limit);
        addNextLinkIfExists(limit);
        addLastLink(limit);

        return resource;
    }

    private void addFirstLink(int limit) {

        resource.add(createLink(1, limit, Link.REL_FIRST));
    }

    private void addPreviousLinkIfExists(int limit) {

        if (page.hasPrevious()) {

            resource
                .add(createLink(getCorrectPageNumber(page.previousPageable()), limit,
                    Link.REL_PREVIOUS));
        }
    }

    private void addNextLinkIfExists(int limit) {

        if (page.hasNext()) {

            resource
                .add(createLink(getCorrectPageNumber(page.nextPageable()), limit, Link.REL_NEXT));
        }
    }

    private void addLastLink(int limit) {

        resource.add(createLink(page.getTotalPages(), limit, Link.REL_LAST));
    }

    /**
     * Corrects the page number to be one-indexed.<br/> Although the propertie
     * spring.data.web.pageable.one-indexed-parameters is set to true, it is only used to accept it
     * one-indexed, the PageImpl internally use the page zero-indexed.
     */
    private int getCorrectPageNumber(Pageable pageable) {
        return pageable.getPageNumber() + 1;
    }

    private Link createLink(int page, int limit, String rel) {

        Link link = getLinkPaged(linkTo(invocationValue), page, limit);

        return link.withRel(rel);
    }

    private Link getLinkPaged(ControllerLinkBuilder linkBuilder, int page, int limit) {
        return getLinkPaged(linkBuilder, String.valueOf(page), String.valueOf(limit));
    }

    private Link getLinkPaged(ControllerLinkBuilder linkBuilder, String page, String limit) {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put(pageParameter, Collections.singletonList(page));
        map.put(limitParameter, Collections.singletonList(limit));

        UriComponentsBuilder builder = linkBuilder.toUriComponentsBuilder().queryParams(map);

        return new Link(builder.build().toUriString());
    }
}
