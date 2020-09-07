package gsan.server.gsan.api.service.enumerations;


public enum CustomException {

	  //Objets directement construits
	  code0("200", "GOOD","/start","GSAn finish succefully")
	  ,
	  code1("404", "Bad Request","/start","The number of genes of query for the analysis is not enough. The min number is 5.")
	  ,
	code2("500", "Internal Server Error","GSAn","There is no result associated with this path. GSAn removes the results after 12h. "
			+ "If that is not the case, please contact us to solve this issue.")
	 ,
	  code3("404", "Bad Request","/start","The gene id of the query doesn't match with the gene id of GOA. Please, check if:\n"
	  		+ "1) You use the official symbol id.\n"
	  		+ "2) You use the good organism."
	  		+ "Another possible issue: There is no GO terms associate to input gene set.")
	  ,
		code4("500", "Internal Server Error","GSAn","there was an error in the clustering step. Maybe the number of "
				+ "annotation is insufficient. If that is not the case, please contact us to solve this issue.")
		  ,
			code5("500", "Internal Server Error","GSAn","It is possible that the set of representative terms is empty because "
					+ "there is no term kept by the filter. If that is not the case, please contact us to solve this issue."),
			code6("404", "Bad Request","/start","The used file is not in GAF 2.0 format. Please use the right format "+
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
