package businessLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import componentLayer.CSVComponent;
import componentLayer.Filter;
import dataLayer.CSVDocumentModelCity;
import dataLayer.City;

public class CityHandler {
	
	private List<Filter> filters;
	private CSVComponent reader = new CSVComponent();
	
	public List<City> ReadAll() {
		filters = new ArrayList<Filter>();
		return reader.<City>Read(filters, new City());
	}
	
	public List<City> ReadAllCapitals(){
		filters = new ArrayList<Filter>();
		filters.add(new Filter(CSVDocumentModelCity.CAPITAL, true));
		List<City> returner = reader.<City>Read(filters, new City());
		
		returner.sort((City c1, City c2) -> c1.getName().compareTo(c2.getName()));
		
		return returner;
	}
	
	public City ReadByIBGEId(Integer pIBGEId){
		filters = new ArrayList<Filter>();
		filters.add(new Filter(CSVDocumentModelCity.IBGE_ID, pIBGEId));
		
		List<City> returner = reader.<City>Read(filters, new City());
		return returner.get(0);
	}
	
	public List<City> ReadByState(String pStateUF){
		filters = new ArrayList<Filter>();
		filters.add(new Filter(CSVDocumentModelCity.UF, pStateUF));
		
		List<City> returner = reader.<City>Read(filters, new City());
		return returner;
	}

	public int TotalRecords(){
		filters = new ArrayList<Filter>();
		List<City> returner = reader.<City>Read(filters, new City());
		return returner.size();
	}
	
	public HashMap<String, Integer> CountByState(){		
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		
		Integer qtd = 0;
		for(City record : this.ReadAll()) {
			
			if(count.get(record.getUf()) != null) {
				qtd = count.get(record.getUf()).intValue();
			} else {
				qtd = 0;
			}
			
			qtd++;
			count.put(record.getUf(), qtd);
		}
		return count;
	}
	
	public String StateWithLowerQtdOfCities() {
		HashMap<String, Integer> counted = this.CountByState();
		
		int qtdAux = Integer.MAX_VALUE;
		String lower = "";
		for(Map.Entry<String,Integer> e : counted.entrySet()) {
			if(e.getValue() < qtdAux) {
				lower = e.getKey();
				qtdAux = e.getValue();
			}
		}
		return lower;
	}
	
	public String StateWithHigherQtdOfCities() {
		HashMap<String, Integer> counted = this.CountByState();
		
		int qtdAux = Integer.MIN_VALUE;
		String higher = "";
		for(Map.Entry<String,Integer> e : counted.entrySet()) {
			if(e.getValue() > qtdAux) {
				higher = e.getKey();
				qtdAux = e.getValue();
			}
		}
		return higher;
	}
	
	public List<City> HigherDistance(){
		List<City> returner = new ArrayList();
		
		Double difLonAux = 0D;
		Double difLatAux = 0D;
		Double sumAux = 0D;
		
		for(City recordLoop1 : this.ReadAll()) {
			for(City recordLoop2 : this.ReadAll()) {
				
				
				if((recordLoop1.getLon() > 0 && recordLoop2.getLon() > 0) ||
						(recordLoop1.getLon() < 0 && recordLoop2.getLon() < 0)) {
					difLonAux = (recordLoop1.getLon() * (+1)) - (recordLoop2.getLon() * (+1)); 
				} else
					difLonAux = (recordLoop1.getLon() - recordLoop2.getLon());
				
				
				
				if((recordLoop1.getLat() > 0 && recordLoop2.getLat() > 0) ||
						(recordLoop1.getLat() < 0 && recordLoop2.getLat() < 0)) {
					difLatAux = (recordLoop1.getLat() * (+1)) - (recordLoop2.getLat() * (+1)); 
				} else
					difLatAux = (recordLoop1.getLat() - recordLoop2.getLat());
				
				
				Double dif = (difLonAux + difLatAux);  
				if(dif > sumAux) {
					sumAux = dif;
					returner.clear();
					returner.add(recordLoop1);
					returner.add(recordLoop2);
				}
			}
		}
		return returner;
	}
	
	public void Delete(Integer pIdIBGE) {
		List<City> bufferedList = this.ReadAll();
		City toDelete = this.ReadByIBGEId(pIdIBGE);
		
		bufferedList.remove(toDelete);
		CSVComponent.<City>Write(bufferedList, new City());
	}
	
	public void Update(City pToUpdate) {
		List<City> bufferedList = this.ReadAll();
		City recordBeforeUpdated = this.ReadByIBGEId(pToUpdate.getIbge_id());
		bufferedList.remove(recordBeforeUpdated);
		
		//
		
		bufferedList.add(recordBeforeUpdated);
		CSVComponent.<City>Write(bufferedList, new City());
	}
}
