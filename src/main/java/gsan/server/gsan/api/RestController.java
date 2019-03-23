package gsan.server.gsan.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.RestCom.db2db.db2db;

@Controller
public class RestController {
	@RequestMapping("/gsanConverter")
	@ResponseBody
	public String gsanConverter(
			Model model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "organism", required = true,defaultValue = "homo_sapiens") int organism,
			@RequestParam(value = "id", required = true, defaultValue="geneid") String input

			)  {
		
			try {
				return db2db.run(query, organism, input);
			} catch (Exception e) {
				return "Nothing";
			}
		}
	
	

}
