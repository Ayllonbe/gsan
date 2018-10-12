package gsan.distribution.gsan_api.annotation;



public class ChooseAnnotation {

	public static String annotation(String organism, boolean iea){

		String goa_file;
		
		switch (organism) {
		case "homo_sapiens":
			 
			goa_file = iea? "goa_human.gaf_IEA.ser":"goa_human.gaf_NOIEA.ser";
			break;
		case "danio_rerio":
			goa_file = iea? "goa_zebrafish.gaf_IEA.ser":"goa_zebrafish.gaf_NOIEA.ser";
			break;
		case "saccharomyces_cerevisiae":
			goa_file = iea? "goa_yeast.gaf_IEA.ser":"goa_yeast.gaf_NOIEA.ser";
			break;
		case "escherichia_coli":
			goa_file = iea? "gene_association.ecocyc_IEA.ser":"gene_association.ecocyc_NOIEA.ser";
			break;
		case "mus_musculus":
			goa_file = iea? "goa_mouse.gaf_IEA.ser":"goa_mouse.gaf_NOIEA.ser";
			break;
		case "arabidopsis_thaliana":
			goa_file = iea? "goa_arabidopsis.gaf_IEA.ser":"goa_arabidopsis.gaf_NOIEA.ser";
			break;
		case "canis_lupus":
			goa_file = iea? "goa_dog.gaf_IEA.ser":"goa_dog.gaf_NOIEA.ser";
			break;
		case "sus_scrofa":
			goa_file = iea? "goa_pig.gaf_IEA.ser":"goa_pig.gaf_NOIEA.ser";
			break;
		case "rattus_norvegicus":
			goa_file = iea? "goa_rat.gaf_IEA.ser":"goa_rat.gaf_NOIEA.ser";
			break;
		case "gallus_gallus":
			goa_file = iea? "goa_chicken.gaf_IEA.ser":"goa_chicken.gaf_NOIEA.ser";
			break;
		case "candida_albicans":
			goa_file = iea? "gene_association.cgd_IEA.ser":"gene_association.cgd_NOIEA.ser";
			break;
		case "bos_taurus":
			goa_file = iea? "goa_cow.gaf_IEA.ser":"goa_cow.gaf_NOIEA.ser";
			break;
		case "drosophila_melanogaster":
			goa_file = iea? "goa_fly.gaf_IEA.ser":"goa_fly.gaf_NOIEA.ser";
			break;
		case "caenorhabditis_elegans":
			goa_file = iea? "goa_worm.gaf_IEA.ser":"goa_worm.gaf_NOIEA.ser";
		default:
			goa_file = "goa_human.gaf";
			break;
		
		}
		return goa_file;
			
	}
	
	
	
}
