package com.ifood.ifoodclient.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ClientKeepAliveLog {

    @Id
    private String id;

    private String restaurantCode;

    private boolean available;

    private boolean online;

    @LastModifiedDate
    private DateTime lastModified;
}
