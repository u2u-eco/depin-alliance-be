package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "skills")
public class Skill extends PanacheEntityBase {
    @Id
    public Long id;
    public String name;
    @Column(name = "order_display")
    public Integer orderDisplay;
}
