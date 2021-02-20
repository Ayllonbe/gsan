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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//import com.RestCom.db2db.db2db;

import gsan.distribution.gsan_api.annotation.ChooseAnnotation;
import gsan.distribution.gsan_api.read_write.ReadFile;
import gsan.server.gsan.api.service.GSAnService;
import gsan.server.gsan.api.service.enumerations.CustomException;
import gsan.server.gsan.api.service.jpa.taskRepository;
import gsan.server.gsan.api.service.model.task;

@RestController
public class GSAnRESTController {

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

		
	@RequestMapping("/gsanGet")
	public String gsanRun(
			Model model,
			@RequestParam(value = "ontology", required = true) List<String> top,
			//@RequestParam(value = "query", required = true) List<String> query,
			@RequestParam(value = "query", required = true) MultipartFile file,
			@RequestParam(value = "organism", required = false,defaultValue = "homo_sapiens") String organism,
			@RequestParam(value = "useIEA", required = false,defaultValue = "true") boolean useiea,
			//@RequestParam(value = "ic_incomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "ids", required = false, defaultValue = "2" ) int ids,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
		//	@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
		//	@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport,
		//	@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok,
			@RequestParam(value = "email", required = false) String email

			)  {
		List<String> query = new ArrayList<>();
		try {
			//System.out.println("start");
			StringWriter writer = new StringWriter();
			//	System.out.println(file.getContentType());
			IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);
			String theString = writer.toString();
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
			task t= new task();

			tRepository.save(t);

			gsanService.runService(tRepository, t, query, organism, useiea, top,
					ss, geneSupport,percentile, ids,email);
			return ""+t.getId();
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
			@RequestParam(value = "uploadFile", required = true) MultipartFile  gaf,
			@RequestParam(value = "useIEA", required = false,defaultValue = "true") boolean useiea,
			//@RequestParam(value = "ic_incomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "ids", required = false, defaultValue = "2" ) int ids,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
			//@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
			//@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport,
			//@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok,
			@RequestParam(value = "email", required = false) String email
			) {
		List<String> query = new ArrayList<>();
		try {
			//System.out.println("start");
			StringWriter writer = new StringWriter();
			//System.out.println(file.getContentType());
			IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);
			String theString = writer.toString();
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
		String gafString  = new String();
		try {
			//System.out.println("start");
			StringWriter writer = new StringWriter();
			//System.out.println(file.getContentType());
			IOUtils.copy(gaf.getInputStream(), writer, StandardCharsets.UTF_8);
			gafString = writer.toString();
			//System.out.println(theString);

			//System.out.println("End2");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(query.size()>2) {
			//System.out.println("EMAIL "+email);
			task t= new task();

			tRepository.save(t);

			gsanService.runService(tRepository, t, query, useiea, top,
					ss, geneSupport,percentile, gafString,ids,email);
			return ""+t.getId();
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
//			@RequestParam(value = "icIncomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "ids", required = false, defaultValue = "2" ) int ids,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
			//@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
			//@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport
			//@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok
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

				Map<String,Object> map = gsanService.runService(query, organism, useiea, top,
						ss, geneSupport,percentile,ids);

				List<String> rep = new ArrayList<>((Collection<String>)map.get("representatives"));
				List<String> scp = new ArrayList<>((Collection<String>)map.get("scp"));
				List<String> genes = new ArrayList<>((Collection<String>)map.get("GeneSet"));
				List<String> anngenes = new ArrayList<>((Collection<String>)map.get("AnnotatedGeneSet"));
				Map<String,Object> terms = (HashMap<String,Object>) map.get("terms");

				StringBuffer sb = new StringBuffer();
				sb.append("SetQuality,Id,Name,Onto,IC,QueryNumber,AnnotationQueryNumber,CoverNumber,Synthetic,Genes\n");

				for(String r : rep) {
					//System.out.println(terms.get(r));
					sb.append((Double)map.get("GS2")+",");
					sb.append(r+",");
					sb.append("\""+(String)((Map<String,Object>) terms.get(r)).get("name")+"\",");
					sb.append((String)((Map<String,Object>) terms.get(r)).get("onto")+",");
					sb.append((Double) ((Map<String,Object>) terms.get(r)).get("IC")+",");
					sb.append(genes.size()+",");
					sb.append(anngenes.size()+",");
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
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/gsanJSON")
	public void gsanRunJSON(
			HttpServletResponse response,
			@RequestParam(value = "ontology", required = true) List<String> top,
			@RequestParam(value = "query", required = true) MultipartFile file,
			@RequestParam(value = "organism", required = false,defaultValue = "homo_sapiens") String organism,
			@RequestParam(value = "useIEA", required = false,defaultValue = "true") boolean useiea,
//			@RequestParam(value = "icIncomplete", required = false, defaultValue = "3" ) int ic_inc,
			@RequestParam(value = "ids", required = false, defaultValue = "2" ) int ids,
			@RequestParam(value = "percentile", required = false, defaultValue = "25" ) int percentile,
			@RequestParam(value = "semanticSimilarity", required = false, defaultValue = "lin") String ss,
			@RequestParam(value = "moduleID", required = true) String m,
			@RequestParam(value = "moduleName", required = true) String mName,
			//@RequestParam(value = "simRepValue", required = false, defaultValue = "0") double similarityRepValue,
			//@RequestParam(value = "covering", required = false, defaultValue = "1") double covering,
			@RequestParam(value = "minGeneSupport", required = false, defaultValue = "3") int geneSupport
			//@RequestParam(value = "prokaryote", required = false, defaultValue = "false") boolean prok
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

				Map<String,Object> map = gsanService.runService(query, organism, useiea, top,
						ss, geneSupport,percentile,ids);
				map.put("ModuleID", m);
				map.put("ModuleName", mName);
				
				JSONObject jo = new JSONObject();
				jo.putAll(map);
				response.getWriter().print(jo.toJSONString());
			}
			else {
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	


}
