import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "keyword")
@ToString(exclude = {"children", "parent"})
@EqualsAndHashCode(of = {"uuid"})
public class KeyWord {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "uuid")
  private String uuid;

  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "parent", referencedColumnName = "uuid")
  private Riddle parent;

}
