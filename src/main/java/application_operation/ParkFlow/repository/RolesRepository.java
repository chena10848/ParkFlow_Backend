package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<RoleEntity, Integer> {
}
