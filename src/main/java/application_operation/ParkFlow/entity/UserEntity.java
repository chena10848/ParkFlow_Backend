package application_operation.ParkFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.ICommand;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    // 指定我們自己建立的 Sequence
    @Id
    @Comment("使用者編號")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "SEQ_USERS", allocationSize = 1)
    private Integer id;

    @Comment("中文名稱")
    @Column(name = "CHINESE_NAME")
    private String chineseName;

    @Comment("英文名稱")
    @Column(name = "ENGLISH_NAME")
    private String englishName;

    @Comment("電子郵件")
    @Column(name = "EMAIL", updatable = false)
    private String email;

    @Comment("電話號碼")
    @Column(name = "CELLPHONE")
    private String cellphone;

    @Comment("車牌號碼")
    @Column(name = "CAR_NUMBER")
    private String carNumber;

    @Comment("車子種類")
    @Column(name = "CAR_TYPE")
    private String carType;

    @Comment("權限編號")
    @Column(name = "ROLE_ID", insertable = false, updatable = false)
    private Integer roleId = 1;

    @Comment("新增時間")
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Comment("異動時間")
    @Column(name = "UPDATED_AT", insertable = false)
    private LocalDateTime updatedAt;
}
