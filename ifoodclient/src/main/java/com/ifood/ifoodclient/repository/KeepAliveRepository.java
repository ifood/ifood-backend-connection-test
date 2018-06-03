package com.ifood.ifoodclient.repository;

import com.ifood.ifoodclient.domain.KeepAlive;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KeepAliveRepository extends MongoRepository<KeepAlive, String> {
}
