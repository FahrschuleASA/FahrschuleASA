package org.projekt17.fahrschuleasa.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A TeachingDiagram.
 */
@Entity
@Table(name = "teaching_diagram")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TeachingDiagram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "basic")
    private Integer basic = 0;

    @Column(name = "advanced")
    private Integer advanced = 0;

    @Column(name = "performance")
    private Integer performance = 0;

    @Column(name = "independence")
    private Integer independence = 0;

    @Column(name = "overland")
    private Integer overland = 0;

    @Column(name = "autobahn")
    private Integer autobahn = 0;

    @Column(name = "night")
    private Integer night = 0;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBasic() {
        return basic;
    }

    public TeachingDiagram basic(Integer basic) {
        this.basic = basic;
        return this;
    }

    public void setBasic(Integer basic) {
        this.basic = basic;
    }

    public Integer getAdvanced() {
        return advanced;
    }

    public TeachingDiagram advanced(Integer advanced) {
        this.advanced = advanced;
        return this;
    }

    public void setAdvanced(Integer advanced) {
        this.advanced = advanced;
    }

    public Integer getPerformance() {
        return performance;
    }

    public TeachingDiagram performance(Integer performance) {
        this.performance = performance;
        return this;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Integer getIndependence() {
        return independence;
    }

    public TeachingDiagram independence(Integer independence) {
        this.independence = independence;
        return this;
    }

    public void setIndependence(Integer independence) {
        this.independence = independence;
    }

    public Integer getOverland() {
        return overland;
    }

    public TeachingDiagram overland(Integer overland) {
        this.overland = overland;
        return this;
    }

    public void setOverland(Integer overland) {
        this.overland = overland;
    }

    public Integer getAutobahn() {
        return autobahn;
    }

    public TeachingDiagram autobahn(Integer autobahn) {
        this.autobahn = autobahn;
        return this;
    }

    public void setAutobahn(Integer autobahn) {
        this.autobahn = autobahn;
    }

    public Integer getNight() {
        return night;
    }

    public TeachingDiagram night(Integer night) {
        this.night = night;
        return this;
    }

    public void setNight(Integer night) {
        this.night = night;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeachingDiagram)) {
            return false;
        }
        return id != null && id.equals(((TeachingDiagram) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TeachingDiagram{" +
            "id=" + getId() +
            ", basic=" + getBasic() +
            ", advanced=" + getAdvanced() +
            ", performance=" + getPerformance() +
            ", independence=" + getIndependence() +
            ", overland=" + getOverland() +
            ", autobahn=" + getAutobahn() +
            ", night=" + getNight() +
            "}";
    }
}
