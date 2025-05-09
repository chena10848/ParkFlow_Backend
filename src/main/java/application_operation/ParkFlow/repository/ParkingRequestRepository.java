package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.ParkingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingRequestRepository extends JpaRepository<ParkingRequestEntity, Integer> {

    @Query(
            value = """
                    SELECT
                        *
                    FROM
                        PARKING_REQUEST
                    WHERE
                        ID = :id
                    """, nativeQuery = true
    )
    List<ParkingRequestEntity> queryParkingRequestById(@Param("id") Integer id);

    @Query(
            value = """
                    SELECT
                        *
                    FROM
                        PARKING_REQUEST
                    WHERE
                        APPLICANT_ID = :applicantId
                    AND
                        START_DATE = :startDate
                    AND
                        STATUS IN (0, 1)
                    """, nativeQuery = true
    )
    List<ParkingRequestEntity> queryParkingRequestByApplicantId(
            @Param("applicantId") Integer applicantId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query(
            value = """
                    Select
                        *
                    FROM
                        PARKING_REQUEST
                    WHERE
                        START_DATE = :startDate
                    AND
                        STATUS IN (0, 1)
                    """, nativeQuery = true)
    List<ParkingRequestEntity> getCurrentRequest(@Param("startDate")LocalDateTime startDate);

    @Query(
            value = """
                    SELECT
                        r.APPLICATION_TIME as requestTime,
                        u.CHINESE_NAME as chineseName,
                        u.ENGLISH_NAME as englishName,
                        r.CAR_TYPE as carType,
                        r.CAR_NUMBER as carNumber,
                        r.CELLPHONE as cellphone,
                        r.PARKING_SLOT_NUMBER as parkingSlotNumber,
                        r.STATUS as status
                    FROM
                        USERS u
                    INNER JOIN
                        PARKING_REQUEST r
                    ON
                        u.ID = r.APPLICANT_ID
                    WHERE
                        r.START_DATE = :startDate
                    AND
                        u.ID = :userId
                    """, nativeQuery = true
    )
    List<Object[]> queryUserParkingRequest(
            @Param("startDate") LocalDateTime startDate,
            @Param("userId") Integer userId
    );

    @Query(
            value = """
                    SELECT
                        u1.ENGLISH_NAME ,
                        u1.EMAIL
                    FROM
                        PARKING_REQUEST pr
                    INNER JOIN
                        USER u1
                    ON
                        pr.APPLICANT_ID = u1.ID
                    INNER JOIN
                        USER u2
                    ON
                        pr.REVIEW_ID = u2.ID
                    WHERE
                        pr.Id = :id
                    """, nativeQuery = true)
    List<Object[]> findParkingRequestAndUserById(@Param("id") Integer id);

    @Query(
            value = """
                    SELECT
                        r.APPLICATION_TIME as requestTime,
                        u.CHINESE_NAME as chineseName,
                        r.CAR_TYPE as carType,
                        r.CAR_NUMBER as carNumber,
                        r.CELLPHONE as cellphone,
                        r.PARKING_SLOT_NUMBER as parkingSlotNumber,
                        r.STATUS as status,
                        r.ID as requestId,
                        u.ENGLISH_NAME as englishName
                    FROM
                        USERS u
                    INNER JOIN
                        PARKING_REQUEST r
                    ON
                        u.ID = r.APPLICANT_ID
                    WHERE
                        r.START_DATE = :startDate
                    """, nativeQuery = true
    )
    List<Object[]> queryParkingRequestByFmId(
            @Param("startDate") LocalDateTime startDate
    );
}
