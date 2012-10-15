package au.com.dius.resilience.persistence.repository;

import au.com.dius.resilience.model.Incident;
import au.com.dius.resilience.persistence.RepositoryCommandResultListener;

/**
 * @author georgepapas
 */
public interface IncidentRepository { //extends Repository<Incident> {

  void findById(RepositoryCommandResultListener<Incident> listener, long id);
  void save(RepositoryCommandResultListener<Incident> listener, Incident incident);
  void findAll(RepositoryCommandResultListener<Incident> listener);
  void findClosest(RepositoryCommandResultListener<Incident> listener);
  void findTracked(RepositoryCommandResultListener<Incident> listener);
}
