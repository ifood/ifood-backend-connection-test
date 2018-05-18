package br.com.ifood.connection.controller.pagination;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Component;

@Component
public class PagedResourceFactory<T extends ResourceSupport> {

    @Value("${spring.data.web.pageable.page-parameter}")
    private String pageParameter;

    @Value("${spring.data.web.pageable.size-parameter}")
    private String limitParameter;

    public PagedResource<T> createPagedResource() {
        return new PagedResource(pageParameter, limitParameter);
    }

}
