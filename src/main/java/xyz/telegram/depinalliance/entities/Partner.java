package xyz.telegram.depinalliance.entities;

import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.models.response.PartnerResponse;

import java.util.List;

/**
 * @author holden on 03-Sep-2024
 */
@Entity
@Table(name = "partners")
public class Partner extends BaseEntity {
  @Id
  @SequenceGenerator(name = "partnerSequence", sequenceName = "partner_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partnerSequence")
  public Long id;
  public String name;
  public String description;
  public long participants;
  public String image;
  public int orders;

  public List<PartnerResponse> findAllPartner() {
    return findAll(Sort.ascending("orders")).project(PartnerResponse.class).list();
  }
}