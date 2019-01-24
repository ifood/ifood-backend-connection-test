package br.com.ifood.ifoodbackendconnection.repository.ignite;

import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;

@RepositoryConfig(cacheName = "ifood-restaurant-check-connection-health")
public interface HealthRepositoryIgnite extends IgniteRepository<Boolean, String> {
}