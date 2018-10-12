package gsan.server.gsan.api;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import gsan.distribution.gsan_api.read_write.ReadFile;
import gsan.server.gsan.api.service.GSAnService;
import gsan.server.gsan.api.service.task;
import gsan.server.gsan.api.service.taskRepository;

@Controller
public class GSAnController {

	@Autowired
	private GSAnService gsanService;
	@Autowired
	private taskRepository tRepository;
	@Autowired
	private JavaMailSender sender;
	

	public static String local = "http://localhost:8282/";
	
	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	

	// Create a new Note
	@RequestMapping("/note")
	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object>  createNote(String email) {
		try {
			//System.out.println(email);
			task t= new task(email);
			tRepository.save(t);
			//System.out.println(t.getId());
			//   TimeUnit.MINUTES.sleep(1);
			t.setfinish(true);
			tRepository.save(t);
		}
		catch (Exception ex) {
			return new ResponseEntity<Object>("Error creating the user: " + ex.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>("GOOD",HttpStatus.OK);}

	@RequestMapping("/start")
	public String start() {
		return "start";
	}
	
	@RequestMapping("/")
	public String wellcome() {
		return "home";
	}
	

	@RequestMapping("/doc")
	public String documentation() {
		return "doc";
	}
	
	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}
	
	@RequestMapping("/charts")
	public String visu() {
		return "Chart";
	}
	@RequestMapping("/visualization")
	public String uploadJSON(Model model,
			@RequestParam(name = "file",defaultValue="@null") MultipartFile json
			) {
	
		try {
			byte[] bytes = json.getBytes();
			String str = new String(bytes, "UTF-8"); 
			model.addAttribute("json", str);
			return "visual";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:/charts";
		}
        
		
	}
	
	@RequestMapping(value = "/api/foo.csv")
	public void fooAsCSV(HttpServletResponse response) {         
	    response.setContentType("text/plain; charset=utf-8");
	    try {
			response.getWriter().print("a,b,c\n1,2,3\n3,4,5");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/wait")
	String running() {
		return "wait";
	}

	@RequestMapping("/error400")
	String error400() {
		return "error";
	}


	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String overviewTask(Model model, @PathVariable("id") @NotNull UUID id) {

		
		try {
		if(tRepository.existsById(id)) {	
			task t = tRepository.getOne(id);
			if(!t.getError()) {
				if(t.isFinish()) {
					model.addAttribute("task", t);
					
					File fileJSON = new File("src/main/tmp/results/"+t.getId()+".json");
					if(fileJSON.exists()) {
					String jsonData = ReadFile.readFileJSON(fileJSON.getAbsolutePath());
					
					model.addAttribute("json", jsonData);
					if(t.getEmail()!=null && t.getEmail()!="") {
						try {
							sendMail(t.getEmail(), t.getId());
						}
						catch(Exception e) {
							log.debug("The email is not valide.");
						}
					}
					return "visual";
					}else {
						log.debug("The files of the id "+t.getId()+", is very old");
						return "noExist";
					}
					
					
				}else {
					return "wait";
				}
			}else { 
				CustomException ce  = CustomException.values()[t.getMSGError()];
				
				model.addAttribute("status",ce.getstatus());
				model.addAttribute("error",ce.getError());
				model.addAttribute("path","/"+t.getId());
				model.addAttribute("timestamp",new TimeStamp(new Date()).toDateString());
				model.addAttribute("message",ce.getMSG());
				log.error("There was an error in the process (Good query? good Annotation or equivalents ids between the query and the annotation?)");
				return "error";
			}
		}else {
			log.error("There are no task with this id.");
			return "error";
		}
		}catch(Exception e) {
			log.error(e.getLocalizedMessage());
			return "error";
		}
	}

	@RequestMapping("/sendMail")
	public String sendMail(String emailTo, UUID id) {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setTo(emailTo);
			helper.setText(local+id);
			helper.setSubject("[GSAn] Analysis finished");
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		sender.send(message);
		return "Mail Sent Success!";
	}
	
	@RequestMapping("/question")
	public String sendQuestion(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "msj", required = true) String msj) {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		try {
			helper.setTo("aaron.ayllon-benitez@u-bordeaux.fr");
			helper.setText(msj +"\nEmail: "+email);
			helper.setSubject("[GSAn] "+name +"'s question");
			
			
			
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		sender.send(message);
		System.out.println("Mail sended");
		return "contact";
	}

	@RequestMapping("/gsanGet")
	public String gsanRun(
			Model model,
			@RequestParam(value = "ontology", required = true) List<String> top,
			//@RequestParam(value = "query", required = true) List<String> query,
			@RequestParam(value = "query", required = true) MultipartFile file,
			@RequestParam(value = "organism", required = false,defaultValue = "homo_sapiens") String organism,
			@RequestParam(value = "useIEA", required = false,defaultValue = "true") boolean useiea,
			@RequestParam(value = "ic_incomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
			@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
			@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport,
			@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok,
			@RequestParam(value = "email", required = false) String email

			)  {
		List<String> query = new ArrayList<>();
		try {
			//System.out.println("start");
			StringWriter writer = new StringWriter();
		//	System.out.println(file.getContentType());
			IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);
			String theString = writer.toString().toLowerCase();
			String[] sa = theString.split(",");
		//	System.out.println("End1");
			for(String s :sa) {
				query.add(s);
			}
		//	System.out.println("End2");
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(query.size()>2) {
		//	System.out.println("EMAIL "+email);
			task t= new task(email);
			
			tRepository.save(t);
			
			gsanService.runService(tRepository, t, query, organism, useiea, ic_inc, top,
					ss, similarityRepValue, covering, geneSupport,percentile,prok);

			return "redirect:/"+t.getId();
		}
		else {
			CustomException ce  = CustomException.values()[1];
			model.addAttribute("status",ce.getstatus());
			model.addAttribute("error",ce.getError());
			model.addAttribute("path",ce.getpath());
			model.addAttribute("timestamp",new TimeStamp(new Date()).toDateString());
			model.addAttribute("message",ce.getMSG());
			return "error";
		}
	}
	@RequestMapping("/gsanPost")
	public String gsanRun(
			Model model,
			@RequestParam(value = "ontology", required = false, defaultValue = "GO:0008150") List<String> top,
			@RequestParam(value = "query", required = true) MultipartFile file,
			@RequestParam(value = "uploadFile", required = false,defaultValue = "homo_sapiens") File gaf,
			@RequestParam(value = "useIEA", required = false,defaultValue = "true") boolean useiea,
			@RequestParam(value = "ic_incomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
			@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
			@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport,
			@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok,
			@RequestParam(value = "email", required = false) String email
			) {
		List<String> query = new ArrayList<>();
		try {
			//System.out.println("start");
			StringWriter writer = new StringWriter();
			//System.out.println(file.getContentType());
			IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);
			String theString = writer.toString().toLowerCase();
			String[] sa = theString.split(",");
			//System.out.println("End1");
			for(String s :sa) {
				query.add(s);
			}
			//System.out.println("End2");
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	    
		if(query.size()>2) {
			//System.out.println("EMAIL "+email);
			task t= new task(email);
			
			tRepository.save(t);
			
			gsanService.runService(tRepository, t, query, gaf, useiea, ic_inc, top,
					ss, similarityRepValue, covering, geneSupport,percentile,prok);

			return "redirect:/"+t.getId();
		}
		else {
			
			CustomException ce  = CustomException.values()[1];
			
			model.addAttribute("status",ce.getstatus());
			model.addAttribute("error",ce.getError());
			model.addAttribute("path",ce.getpath());
			model.addAttribute("timestamp",new TimeStamp(new Date()).toDateString());
		//	System.out.println(ce.getMSG());
			model.addAttribute("message",ce.getMSG());
			return "error";
		}
	
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/gsanCsv")
	public void gsanRunCsv(
			HttpServletResponse response,
			@RequestParam(value = "ontology", required = true) List<String> top,
			@RequestParam(value = "query", required = true) MultipartFile file,
			@RequestParam(value = "organism", required = false,defaultValue = "homo_sapiens") String organism,
			@RequestParam(value = "useIEA", required = false,defaultValue = "true") boolean useiea,
			@RequestParam(value = "icIncomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
			@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
			@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport,
			@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok
			) {
		try {
			
			List<String> query = new ArrayList<>();
			try {
				StringWriter writer = new StringWriter();
				IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);
				String theString = writer.toString();
				String[] sa = theString.replaceAll("\"", "").replaceAll("\n", "").split(",");
				for(String s :sa) {
					query.add(s);
				}
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(query.size()>2) {
			
			Map<String,Object> map = gsanService.runService(query, organism, useiea, ic_inc, top,
					ss, similarityRepValue, covering, geneSupport,percentile,prok);

			List<String> rep = new ArrayList<>((Collection<String>)map.get("representatives"));
			List<String> scp = new ArrayList<>((Collection<String>)map.get("scp"));
			List<String> genes = new ArrayList<>((Collection<String>)map.get("GeneSet"));
			Map<String,Object> terms = (HashMap<String,Object>) map.get("terms");
			
			StringBuffer sb = new StringBuffer();
			sb.append("Id,Name,Onto,IC,QueryNumber,CoverNumber,Synthetic,Genes\n");
					
			for(String r : rep) {
				//System.out.println(terms.get(r));
				sb.append(r+",");
				sb.append("\""+(String)((Map<String,Object>) terms.get(r)).get("name")+"\",");
				sb.append((String)((Map<String,Object>) terms.get(r)).get("onto")+",");
				sb.append((Double) ((Map<String,Object>) terms.get(r)).get("IC")+",");
				sb.append(genes.size()+",");
				sb.append(((List<String>)((Map<String,Object>) terms.get(r)).get("geneSet")).size()+",");
				sb.append(scp.contains(r)+",");
				StringBuffer sb_genes = new StringBuffer();
				for(String g : (List<String>)((Map<String,Object>) terms.get(r)).get("geneSet") ) {
				sb_genes.append(g+";");	
				}
				sb_genes.deleteCharAt(sb_genes.length()-1);
				sb.append(sb_genes+"\n");
				
			}
			response.getWriter().print(sb.toString());
		}
		else {
		}
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
		
	}

	
	
	
}
