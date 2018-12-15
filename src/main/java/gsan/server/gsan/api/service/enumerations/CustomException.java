package gsan.server.gsan.api.service.enumerations;


public enum CustomException {

	  //Objets directement construits
	  code0("200", "GOOD","/start","GSAn finish succefully")
	  ,
	  code1("404", "Bad Request","/start","The number of genes of query to the analysis is not enough. The number min is 3.")
	  ,
	code2("500", "Internal Server Error","GSAn","The path where the JSON files are stored don't exist. Please contact us to solve this issue.")
	 ,
	  code3("404", "Bad Request","/start","The gene ids of query don't match with the gene ids of GOA. Please, check if:\n"
	  		+ "1) You use the official symbol id.\n"
	  		+ "2) You use the good organism.")
	  ,
		code4("500", "Internal Server Error","GSAn","there was an error in the clustering step. Maybe the number of "
				+ "annotation is insufficient. If that is not the case, please contact us to solve this issue.")
		  ,
			code5("500", "Internal Server Error","GSAn","It is possible that the representative terms is empty because "
					+ "any terms over the filter. If that is not the case, please contact us to solve this issue."),
			code6("404", "Bad Request","/start","The used file is not in GAF 2.0 format. Please use the good format "+
					 "(see http://www.geneontology.org/page/go-annotation-file-format-20). The colums are separated by tabulation")
			
				;


	  private String status = "";
	  private String error = "";
	  private String path = "";
	  private String message = "";
	   

	  //Constructeur

	  CustomException(String status, String error, String path, String msg){

	    this.status = status;
	    this.error = error;
	    this.path = path;
	    this.message = msg;

	  }

	   

	  public String getstatus(){

	    return this.status;

	  }

	   

	  public String getError(){

	    return this.error;

	  }
	  public String getpath(){

		    return this.path;

		  }  
	  public String getMSG(){

		    return this.message;

		  }

	   

	 

	}
