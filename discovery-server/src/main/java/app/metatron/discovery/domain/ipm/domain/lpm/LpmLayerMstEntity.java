package app.metatron.discovery.domain.ipm.domain.lpm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ipm_lpm_layr_bas")
public class LpmLayerMstEntity {
    @Id
    @Column(name = "layr_id")
    private String layrId;

    @Column(name = "layr_grp_id")
    private String layrGrpId;    
    
    @Column(name = "layr_als_nm")
    private String alias;    
    
    @Column(name = "mod_yn")
    private String editable;    
    
    @Column(name = "geo_type")
    private String geometryType;    

    @Column(name = "min_zoom")    
    private int minZoom;
   
    @Column(name = "max_zoom")
    private int maxZoom;    
    
    @Column(name = "db_nm")
    private String name;
    
    @Column(name = "use_yn")
    private String overlap;

    @Column(name = "view_yn")
    private String visible;

    @Column(name = "sel_yn")
    private String selectable;
 
    @Column(name = "layr_typ")
    private String type;
    
    @Column(name = "geo_col_nm")
    private String geoColNm;
    
    @Column(name = "layr_keys")
    private String layrKeys;
    
    @Column(name = "data_unit")
    private String DataUnit;
    
    @Column(name = "layr_fltr")
    private String layrFltr;
    
    /*
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "layer_nm")
    private String layerNm;    

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "order_no")
    private Integer orderNo;    
    
    @Column(columnDefinition = "NUMBER(10) DEFAULT 1")
    @Transient
    
    @Column(columnDefinition = "NUMBER(10) DEFAULT 19")
    @Transient

    @Column(columnDefinition = "VARCHAR(100) DEFAULT 'msmc_byrtopcell_flow_popul_inf_1h'")
    @Transient
	
    
    @OneToMany
    @JoinColumn(name = "layer_id")
    @JsonBackReference
    private List<LpmLayerMstEntity> layerTest;
    
    */
}

