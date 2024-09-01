package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "skill_points")
public class SkillPoint extends PanacheEntityBase {
    @Id
    public Long id;
    @Column(name = "point")
    public BigDecimal point;
    public static SkillPoint getPointRequire(long userId) {
        return find("id = (select count(id)+1 from HistoryUpgradeSkill where userId = :userId) ",
                Parameters.with("userId", userId)).firstResult();
    }
}