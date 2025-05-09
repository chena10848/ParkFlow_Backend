package application_operation.ParkFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ROLE")
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {

    @Id
    @Comment("權限編號")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_gen")
    @SequenceGenerator(name = "role_seq_gen", sequenceName = "SEQ_ROLE", allocationSize = 1)
    private Integer id;

    @Comment("權限名稱")
    @Column(name = "ROLENAME")
    private String roleName;

    @Comment("新增時間")
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Comment("異動時間")
    @Column(name = "UPDATED_AT", insertable = false)
    private LocalDateTime updatedAt;
}
