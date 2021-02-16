package gsan.server.gsan.api;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

//import com.RestCom.db2db.db2db;

import gsan.distribution.gsan_api.annotation.ChooseAnnotation;
import gsan.distribution.gsan_api.read_write.ReadFile;
import gsan.server.gsan.api.service.GSAnService;
import gsan.server.gsan.api.service.enumerations.CustomException;
import gsan.server.gsan.api.service.jpa.taskRepository;
import gsan.server.gsan.api.service.model.task;

@Controller
public class GSAnController {

	@Autowired
	private GSAnService gsanService;
	@Autowired
	private taskRepository tRepository;
	@Autowired
	private JavaMailSender sender;



	//public static String local = "http://localhost:8282/";


	@Value("${version.number}")
	private String versionNumber;

	private final Logger log = LoggerFactory.getLogger(this.getClass());


//	// Create a new Note
//	@RequestMapping("/note")
//	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Object>  createNote(String email) {
//		try {
//			//System.out.println(email);
//			task t= new task();
//			tRepository.save(t);
//			//System.out.println(t.getId());
//			//   TimeUnit.MINUTES.sleep(1);
//			t.setfinish(true);
//			tRepository.save(t);
//		}
//		catch (Exception ex) {
//			return new ResponseEntity<Object>("Error creating the user: " + ex.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		return new ResponseEntity<Object>("GOOD",HttpStatus.OK);}

	@RequestMapping("/start")
	public String start(Model m,@RequestParam(value = "query", required = false) String query) {		
		m.addAttribute("query",query);
		m.addAttribute("version", versionNumber);
		return "start";
	}
	
	
//	@RequestMapping("/idconverter")
//	public String idconverter(Model m) {
//		m.addAttribute("version", versionNumber);
//		return "convert";
//	}

	@RequestMapping("/")
	public String wellcome(Model m) {
		m.addAttribute("version", versionNumber);
		return "home";
	}


	@RequestMapping("/documentation")
	public String documentation(Model m) {
		m.addAttribute("version", versionNumber);
		return "doc";
	}

	@RequestMapping("/contact")
	public String contact(Model m) {
		m.addAttribute("version", versionNumber);
		return "contact";
	}

	@RequestMapping("/visualization")
	public String visu(Model m) {
		m.addAttribute("version", versionNumber);
		return "Chart";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/releases")
	public String releases(Model model) {
		model.addAttribute("version", versionNumber);
		String goFile = "src/main/resources/static/ontology/go.owl";
		Map<String, Object> process = new HashMap<>();
		Map<String, String> instance = new HashMap<>();

		instance.put("name", "Gene Ontology");
		instance.put("file", "go.owl");
		File gof = new File(goFile);
		DateFormat df = new SimpleDateFormat("dd MMM yyyy");
		instance.put("date",df.format(gof.lastModified()));
		List<Map<String,String>> liM = new ArrayList<Map<String,String>>();
		liM.add(instance);

		process.put("ontology",liM);

		String[] goas = new String[] {
				"homo_sapiens",
				"danio_rerio",
				"saccharomyces_cerevisiae",
				"escherichia_coli",
				"mus_musculus",
				"arabidopsis_thaliana",
				"canis_lupus",
				"sus_scrofa",
				"rattus_norvegicus",
				"gallus_gallus",
				"candida_albicans",
				"bos_taurus",
				"drosophila_melanogaster",
				"caenorhabditis_elegans"
		};
		liM = new ArrayList<Map<String,String>>();

		for(String goaOrg:goas) {
			String org = ChooseAnnotation.annotation(goaOrg);
			String goaFile = "src/main/resources/static/AssociationTAB/"+org;
			File goaf = new File(goaFile);
		//	System.out.println(goaOrg+"\t"+org+"\t" + df.format(goaf.lastModified()));
			instance = new HashMap<>();

			instance.put("name", goaOrg);
			instance.put("file",org);
			instance.put("date",df.format(goaf.lastModified()));
			liM.add(instance);


		}

		process.put("annotation", liM);

		JSONObject jo = new JSONObject();
		jo.putAll(process);

		model.addAttribute("json", jo.toJSONString());
		return "releases";
	}

	@RequestMapping("/results")
	public String uploadJSON(Model model,
			@RequestParam(name = "file",defaultValue="@null") MultipartFile json
			) {
		model.addAttribute("version", versionNumber);
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



	@RequestMapping("/wait")
	String running() {
		return "wait";
	}




	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String overviewTask(Model model, @PathVariable("id") @NotNull UUID id) {
		model.addAttribute("version", versionNumber);
	//	String email = uidd2email.get(id);
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
				CustomException ce  = CustomException.values()[2];
				model.addAttribute("status",ce.getstatus());
				model.addAttribute("error",ce.getError());
				model.addAttribute("path",ce.getpath());
				model.addAttribute("timestamp",new TimeStamp(new Date()).toDateString());
				model.addAttribute("message",ce.getMSG());
				log.error("There are no task with this id.");
				return "error";
			}
		}catch(Exception e) {
			log.error(e.getLocalizedMessage());
			return "error";
		}
	}
	//

	

//	public void sendEmailWithoutTemplating(String email, UUID id) throws UnsupportedEncodingException{
//
//		MimeMessage message = sender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message);
//		// Set From: header field of the header.
//		try {
//			helper.setFrom(new InternetAddress("no-reply-gsan@labri.fr", "NO-REPLY"));
//			// Set To: header field of the header.
//			helper.setTo(new InternetAddress(email));
//			// Set Subject: header field
//			message.setSubject("[GSAn] Analysis finished");
//
//			task task = tRepository.getOne(id);
//			LocalDateTime ts = task.getDate().toLocalDateTime();
//
//			// Fill the message
//			helper.setText("** This is an automatic email, Please don't reply to it **"+
//					"\n\n"+
//							"Your analysis sent on "+ts.format(format)+" is finished and you can access to the results using the following link:"+
//							"\n https://gsan.labri.fr/"+id+
//							"\n\n"+
//									"Regards,"+
//									"\n\n"+
//									"GSAn team\n\n"+
//									"**  If you have any questions about GSAn, please contact us in  https://gsan.labri.fr/contact **\r\n"
//									);
//	    sender.send(message);
//	    log.debug("Sent message successfully....");
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			log.error("The email is incorrect");
//			//e.printStackTrace();
//		}catch(MailSendException e) {
//			log.error("The email is null or incorrect");
//		}
//
//
//
//		// Get the default Session object.
//	}


	@RequestMapping("/question")
	public String sendQuestion(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "msj", required = true) String msj) throws UnsupportedEncodingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setFrom(new InternetAddress("no-reply-gsan@labri.fr", "NO-REPLY"));
			// Set To: header field of the header.
			helper.setTo(new InternetAddress("ayllonbenitez.aaron@gmail.com"));
			// Set Subject: header field
			message.setSubject("[GSAn] Question");
			// Fill the message
			helper.setText(msj+" \nMail: "+email+" \nName: "+name);
			 sender.send(message);
			    log.debug("Sent message successfully....");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					log.error("The email is incorrect");
					//e.printStackTrace();
				}catch(MailSendException e) {
					log.error("The email is null or incorrect");
				}

		 message = sender.createMimeMessage();
		 helper = new MimeMessageHelper(message);

			try {
				helper.setFrom(new InternetAddress("no-reply-gsan@labri.fr", "NO-REPLY"));
				// Set To: header field of the header.
				helper.setTo(new InternetAddress(email));
				// Set Subject: header field
				message.setSubject("[GSAn] Question");
				message.setText("** This is an automatic email, Please don't reply to it **"+
						"\n\n"+"Dear "+name+",\n\nThank you for your interest in GSAn, we will contact you as soon as possible.\n\nRegards,\n\nGSAn team");
				 sender.send(message);
				    log.debug("Sent message successfully....");
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						log.error("The email is incorrect");
						//e.printStackTrace();
					}catch(MailSendException e) {
						log.error("The email is null or incorrect");
					}
		return "contact";
	}




}
