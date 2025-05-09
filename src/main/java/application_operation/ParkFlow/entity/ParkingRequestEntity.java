package application_operation.ParkFlow.entity;

import application_operation.ParkFlow.enums.ParkingRequestEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PARKING_REQUEST")
public class ParkingRequestEntity {

    @Id
    @Comment("停車資訊編號")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parking_request_seq_gen")
    @SequenceGenerator(name = "parking_request_seq_gen", sequenceName = "SEQ_PARKING_REQUEST", allocationSize = 1)
    @Column(name = "ID")
    private Integer id;

    @Comment("申請的開始時間")
    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Comment("申請的電話號碼")
    @Column(name = "CELLPHONE")
    private String cellPhone;

    @Comment("申請的車牌號碼")
    @Column(name = "CAR_NUMBER")
    private String carNumber;

    @Comment("申請的車牌種類")
    @Column(name = "CAR_TYPE")
    private String carType;

    @Comment("停車位置")
    @Column(name = "PARKING_SLOT_NUMBER")
    private Integer parkingSlotNumber;

    @Comment("審核結果")
    @Column(name = "STATUS")
    private ParkingRequestEnum status;

    @Comment("申請人編號")
    @Column(name = "APPLICANT_ID")
    private Integer  applicantId;

    @Comment("申請時間")
    @Column(name = "APPLICATION_TIME")
    private LocalDateTime applicationTime;

    @Comment("審核人編號")
    @Column(name = "REVIEW_ID")
    private Integer reviewId;

    @Comment("審核時間")
    @Column(name = "REVIEW_TIME")
    private LocalDateTime reviewTime;

    @Comment("新增時間")
    @Column(name = "CREATED_AT", insertable = false, updatable = false)//新增時間
    private LocalDateTime createdAt;

    @Comment("異動時間")
    @Column(name = "UPDATED_AT", insertable = false)//更新時間
    private LocalDateTime updatedAt;
}
