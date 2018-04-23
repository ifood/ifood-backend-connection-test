package com.ifood.ignite.repository;

import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;

@RepositoryConfig(cacheName = "RestaurantConnectionHealth")
public interface HealthRepositoryIgnite extends IgniteRepository<Boolean, String> {
}
