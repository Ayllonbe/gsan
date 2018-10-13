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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "downloadinformations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class DownloadInformation implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	@Column(name = "uri")
	public String uri;
	@Column(name = "pathway")
	public String pathway;
	 @Column(name = "auth")
	 public String auth;
	 @Column(name = "passw")
	 public String passw;
	 @Column(name = "type_download")
		public String type_download;
	 
	 @OneToMany(mappedBy = "downloadInformation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
		private Set<AnnotationSource> annotations;
	
	 @OneToMany(mappedBy = "downloadInformation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
		private Set<IntegrationSource> integrations;
		
	 public DownloadInformation() {
		// TODO Auto-generated constructor stub
	}
	 
	 public DownloadInformation(String uri, String path, String auth, String ps, String type) {
		 this.uri = uri;
		 this.pathway = path;
		 this.auth = auth;
		 this.passw = ps;
		 this.type_download = type;
	 }
	 
	 public void setId(Long id) {
			this.id = id;
		}
		public Long getId() {
			return this.id;
		} 
	 public void setUri(String uri) {
			this.uri = uri;
		}
		public String getUri() {
			return this.uri;
		}
		public void setPath(String path) {
			this.pathway = path;
		}
		public String getPath() {
			return this.pathway;
		}
		public void setIdentifients(String auth, String ps) {
			this.auth = auth;
			this.passw = ps;
		}
		public String getAuth() {
			return this.auth;
		}
		public String getPassw() {
			return this.passw;
		}
		public void setAnnotations(Set<AnnotationSource> annotations) {
			this.annotations = annotations;
		}
		
		public Set<AnnotationSource> getAnnotations(){
			return this.annotations;
		}
		public void setIntegrations(Set<IntegrationSource> annotations) {
			this.integrations = annotations;
		}
		
		public Set<IntegrationSource> getIntegration(){
			return this.integrations;
		}
		public void setTypeDownload(String type) {
			this.type_download = type;
		}
		public String getTypeDownload() {
			return this.type_download;
		}	
}
