package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingQuotaRepository extends JpaRepository<ParkingQuotaEntity, Integer> {

    ParkingQuotaEntity findByStartDate(LocalDateTime startDate);

    @Query(
            value = """
                    SELECT
                        *
                    FROM
                        PARKING_QUOTA
                    WHERE
                        START_DATE = :startDate
                    """, nativeQuery = true)
    List<ParkingQuotaEntity> getTotalSlots(@Param("startDate") LocalDateTime startDate);

    Boolean existsByStartDate(LocalDateTime startDate);

    @Query(
            value = """
                     SELECT
                        CASE WHEN COUNT(*) > 0
                            THEN 1
                            ELSE 0
                        END
                     FROM
                        PARKING_QUOTA
                     WHERE
                        START_DATE = :startDate AND ID != :id
                    """, nativeQuery = true)
    Integer existsByStartDateExceptSelf(Integer id, LocalDateTime startDate);

    @Query(
            value = """
                     SELECT
                        COUNT(*)
                     FROM
                        PARKING_REQUEST
                     WHERE
                        START_DATE = :startDate AND STATUS IN (0,1)
                    """, nativeQuery = true)
    Integer getAllReservedSlots(LocalDateTime startDate);
}
