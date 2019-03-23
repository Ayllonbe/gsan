package gsan.distribution.gsan_api.annotation;



public class ChooseAnnotation {

	
	public static String annotation(String organism){

		String goa_file;
		
		switch (organism) {
		case "homo_sapiens":
			 
			goa_file = "goa_human.gaf";
			break;
		case "danio_rerio":
			goa_file ="goa_zebrafish.gaf";
			break; 
		case "saccharomyces_cerevisiae":
			goa_file ="goa_yeast.gaf";
			break;
		case "escherichia_coli":
			goa_file = "ecocyc.gaf";
			break;
		case "mus_musculus":
			goa_file ="goa_mouse.gaf";
			break;
		case "arabidopsis_thaliana":
			goa_file ="goa_arabidopsis.gaf";
			break;
		case "canis_lupus":
			goa_file = "goa_dog.gaf";
			break;
		case "sus_scrofa":
			goa_file ="goa_pig.gaf";
			break;
		case "rattus_norvegicus":
			goa_file = "goa_rat.gaf";
			break;
		case "gallus_gallus":
			goa_file = "goa_chicken.gaf";
			break;
		case "candida_albicans":
			goa_file = "cgd.gaf";
			break;
		case "bos_taurus":
			goa_file = "goa_cow.gaf";
			break;
		case "drosophila_melanogaster":
			goa_file = "goa_fly.gaf";
			break;
		case "caenorhabditis_elegans":
			goa_file ="goa_worm.gaf";
			break;
		default:
			goa_file = "goa_human.gaf";
			break;
		
		}
		return goa_file;
			
	}
	
	
	
}
