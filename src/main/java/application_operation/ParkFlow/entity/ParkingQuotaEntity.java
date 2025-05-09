package application_operation.ParkFlow.entity;

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
@Table(name = "PARKING_QUOTA")
public class ParkingQuotaEntity {

    @Id
    @Comment("停車上限編號")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parking_quota_seq_gen")
    @SequenceGenerator(name = "parking_quota_seq_gen", sequenceName = "SEQ_PARKING_QUOTA", allocationSize = 1)
    private Integer id;

    @Comment("申請的開始時間")
    @Column(name = "START_DATE", updatable = false)
    private LocalDateTime startDate;

    @Comment("可使用的車位數量")
    @Column(name = "TOTAL_SLOTS")
    private Integer totalSlots = 1;

    @Comment("新增時間")
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Comment("異動時間")
    @Column(name = "UPDATED_AT", insertable = false)
    private LocalDateTime updatedAt;
}
