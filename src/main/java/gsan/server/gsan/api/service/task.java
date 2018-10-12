package gsan.server.gsan.api.service;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
 
@Entity
@Table(name="task")
public class task {
	@Id
	@NotNull
	@Type(type = "pg-uuid") // only works for postgres
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "task_id", columnDefinition = "uuid")
	private UUID id;
		
    @Column(name = "is_finished")
	private boolean finish;
    @Column(name = "error")
   	private boolean error;
    
    @Column(name = "msg_code")
   	private int msg_code;
    
    @Column(name = "date")
	private Timestamp date;
	
    @Column(name="email")
    private String email;
	
	public task(){
	}
	
	public task(String email){
	
		this.date = getCurrentTimeStamp();
		this.email = email;
		
	}
	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}
	
	public UUID getId(){
		return this.id;
	}
	
	public boolean isFinish(){
		return this.finish;
	}
	public boolean getError(){
		return this.error;
	}
	public int getMSGError(){
		return this.msg_code;
	}	
	public void setfinish(boolean bo){
		this.finish = bo;
	}
	public void setError(boolean bo){
		this.error = bo;
	}
	
	public void setMSGError(int bo){
		this.msg_code = bo;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
}