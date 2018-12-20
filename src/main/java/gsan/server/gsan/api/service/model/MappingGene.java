package gsan.server.gsan.api.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="GeneInformation")
public class MappingGene {
	@Id
	@GeneratedValue
	@Column(name = "mapping_id")
	private Long id;
	
	@Column(name = "geneinformation_id")
	private Long geneinf_id;
	
	@Column(name="db")
	private String db;
	
	@Column(name="map")
	private String map;
	
//	public MappingGene(String gn, String u, int t) {
//		this.geneName = gn;
//		this.uniprotkb = u;
//		this.taxon = t;
//	}
//	public MappingGene(String u, int t) {
//		this.geneName = null;
//		this.uniprotkb = u;
//		this.taxon = t;
//	}
//	
//	public String getGeneName() {
//		return this.geneName;
//	}
//	
//	public String getUniProt() {
//		return this.uniprotkb;
//	}
//	public int getTaxon() {
//		return this.taxon;
//	}
	
	
	

}
