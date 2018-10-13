package gsan.server.gsan.api.service.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "annotationsources")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class AnnotationSource implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "integrationsource_id", nullable = false)
	@JsonIgnore
	private IntegrationSource integrationSource;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "downloadinformation_id", nullable = false)
	@JsonIgnore
	private DownloadInformation downloadInformation;
	@Column(name = "name")
	public String name;
	@Column(name = "file_name") 
	public String file_name;
	@Column(name = "serialized_name") 
	public String serialized_name;
	@Column(name = "type_download")
		public boolean compresed;
	 @Column(name = "delimiter")
		public String delimiter;
	 @Column(name = "symbolcomment")
		public String symbolComment;
	 
		 
	 
	public AnnotationSource() {

	}
	
	public AnnotationSource(String name, String f,boolean c,String d, String sc) {
		this.name=name;
		this.file_name = f;
		this.compresed=c;
		this.delimiter=d;
		this.symbolComment=sc;

	}
	 public void setcompression(boolean c) {
			this.compresed = c;
		}
		public boolean isCompressed() {
			return this.compresed;
		} 
		public void setDelimiter(String d) {
			this.delimiter = d;
		}
		public String getDelimiter() {
			return this.delimiter;
		} 
		public void setSymbolComment(String c) {
			this.delimiter = c;
		}
		public String getSymbolComment() {
			return this.symbolComment;
		} 
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	public void setfilename(String f1) {
		this.file_name = f1;
	}
	public String getfilename() {
		return this.file_name;
	}
	public void setSerializedname(String f1) {
		this.serialized_name = f1;
	}
	public String getSerializedname() {
		return this.serialized_name;
	}

	
	public void setIntegrationSource(IntegrationSource is) {
		this.integrationSource= is;
	}
	
	public IntegrationSource getIntegrationSource() {
		return this.integrationSource;
	}	
	
	public void setDownloadInformation(DownloadInformation is) {
		this.downloadInformation= is;
	}
	
	public DownloadInformation getDownloadInformation() {
		return this.downloadInformation;
	}
	


}

