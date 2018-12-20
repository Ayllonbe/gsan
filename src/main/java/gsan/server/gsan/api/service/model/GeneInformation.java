package gsan.server.gsan.api.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="GeneInformation")
public class GeneInformation {
	@Id
	@GeneratedValue
	@Column(name = "geneinformation_id")
	private Long id;
	
	@Column(name = "Gene_name")
	private String geneName;
	
	@Column(name="UniProtKB")
	private String uniprotkb;
	
	@Column(name="taxon")
	private int taxon;
	
	public GeneInformation(String gn, String u, int t) {
		this.geneName = gn;
		this.uniprotkb = u;
		this.taxon = t;
	}
	public GeneInformation(String u, int t) {
		this.geneName = null;
		this.uniprotkb = u;
		this.taxon = t;
	}
	
	public String getGeneName() {
		return this.geneName;
	}
	
	public String getUniProt() {
		return this.uniprotkb;
	}
	public int getTaxon() {
		return this.taxon;
	}
	
	
	

}
