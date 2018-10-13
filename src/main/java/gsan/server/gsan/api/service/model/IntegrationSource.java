package gsan.server.gsan.api.service.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gsan.server.gsan.api.service.enumerations.KnowledgeType;

@Entity
@Table(name = "integrationsources")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class IntegrationSource implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	@NotNull
	@Column(name = "name")
	public String name;
	 
	 @Column(name = "type_source")
	public String type_source;
	 @NotNull
	 @Column(name = "file_name1") 
	public String file_name1;
	 @Column(name = "file_name2")
	public String file_name2;
	 @Column(name = "type_download")
	public String type_download;
	 @Column(name = "compresed")
		public boolean compresed;
		 
	
	 @ManyToOne(fetch = FetchType.LAZY, optional = false)
		@JoinColumn(name = "downloadinformation_id", nullable = false)
		@JsonIgnore
		private DownloadInformation downloadInformation;
	
	
	 @OneToMany(mappedBy = "integrationSource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AnnotationSource> annotations;
	
	 public IntegrationSource() {
		
	}
	 
	 public IntegrationSource(String name, String f1, boolean c ) {
		 this.name = name;
		 this.file_name1 = f1;
		 this.type_source = KnowledgeType.ontology.toString();
		 this.compresed=c;
	 }
	 
	 public IntegrationSource(String name, String f1, String f2,boolean c ) {
		 this.name = name;
		 this.file_name1 = f1;
		 this.file_name2 = f2;
		 this.type_source = KnowledgeType.other.toString();
		 this.compresed=c;
	 }
	
	 
	 public void setId(Long id) {
			this.id = id;
		}
		public Long getId() {
			return this.id;
		} 
		 public void setcompression(boolean c) {
				this.compresed = c;
			}
			public boolean isCompressed() {
				return this.compresed;
			} 
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	public void setTypeSource(String type) {
		this.type_source = type;
	}
	public String getTypeSource() {
		return this.type_source;
	}
	public void setfilename(String f1) {
		this.file_name1 = f1;
	}
	public String getfilename() {
		return this.file_name1;
	}
	public void setAnexefilename(String f2) {
		this.file_name2 = f2;
	}
	public String getAnexefilename() {
		return this.file_name2;
	}
	public void setTypeDownload(String type) {
		this.type_download = type;
	}
	public String getTypeDownload() {
		return this.type_source;
	}	
	
	

	public void setAnnotations(Set<AnnotationSource> annotations) {
		this.annotations = annotations;
	}
	
	public Set<AnnotationSource> getAnnotations(){
		return this.annotations;
	}
	public void setDownloadInformation(DownloadInformation is) {
		this.downloadInformation= is;
	}
	
	public DownloadInformation getDownloadInformation() {
		return this.downloadInformation;
	}
	
}
