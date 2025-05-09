package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    @Query(
            value = """
                    SELECT
                        r.ROLENAME as ROLENAME,
                        u.ENGLISH_NAME as ENGLISH_NAME,
                        u.EMAIL as EMAIL
                    FROM
                        USERS u
                    INNER JOIN
                        ROLE r
                    ON
                        u.ROLE_ID = r.ID
                    WHERE
                        u.ID = :userId
                    """, nativeQuery = true
    )
    List<Object[]> findUsersAndRoleById(@Param("userId") Integer userId);

    @Query(
            value = """
                    SELECT
                        u.ENGLISH_NAME as ENGLISH_NAME ,
                        u.EMAIL as EMAIL
                    FROM
                        USERS u
                    INNER JOIN
                        ROLE r
                    ON
                        u.ROLE_ID = r.ID
                    WHERE
                        r.ROLENAME = 'FM'
                    """, nativeQuery = true
    )
    List<Object[]> findUsersAndRoleByFM();

    @Query(
            value = """
                     SELECT
                        CASE WHEN COUNT(*) > 0
                            THEN 1
                            ELSE 0
                        END
                     FROM
                        USERS
                     WHERE
                        EMAIL = :email AND ID != :id
                    """, nativeQuery = true)
    Integer existsByIdExceptSelf(Integer id, String email);
}