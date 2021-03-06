package componentLayer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dataLayer.CSVDocumentModel;
import dataLayer.Entity;

public class CSVComponent {
    private Map<String, Integer> headersIndex;
	
	public <T extends Entity> List<T> Read(List<Filter> pFilter, T pInstance) 
	{
		List<T> bufferedList = new ArrayList<>();
		try{
		    String currentLine = "";
		    int auxCounter = 0;
		    
		    BufferedReader reader = new BufferedReader(new FileReader(pInstance.getFilePath()));
		    while ((currentLine = reader.readLine()) != null)
		    {
		    	//To refresh the headers indexes
		    	if(auxCounter == 0) {
		    		refreshHeaderIndexes(currentLine);
		    		auxCounter++;
		    		continue;
		    	}
		    	
		    	//If there are not filter or the row is OK with the filters
		    	if(pFilter.size() == 0 || rowFiltered(currentLine, pFilter)) {
		    		bufferedList.add((T)pInstance.getFromLine(currentLine, headersIndex));
		    	}
		    	auxCounter++;
		    }
		    reader.close();
		}
		catch(Exception ex){ System.out.println(ex.getMessage()); }
		finally { return bufferedList; }
	}
	
	private int indexCounter;
	private void refreshHeaderIndexes(String pHeader){
		indexCounter = 0;
		headersIndex = new HashMap<String, Integer>();
		
		for(String item : pHeader.split(CSVDocumentModel.COMMA_DELIMITER)) {			
			headersIndex.put(item, indexCounter);
			indexCounter++;
		}
	}
	
	private Boolean rowFiltered(String pToFilter, List<Filter> pFilters) {
		
		Boolean filtered = true;
		for(Filter filter : pFilters) {
			
			//Separates every column from my line
			String[] items = pToFilter.split(CSVDocumentModel.COMMA_DELIMITER);
			
			//get the index from the header by the column from filter.
			Integer indexColumnToFilter = headersIndex.get(filter.getColumn());
			
			//Filter the column from my line with the same index from the filter column header
			//if some column is not filtered, the entire line is consider not filtered
			if(filter.getValue().getClass().equals(String.class))
				if(!items[indexColumnToFilter].toString().equals((String)filter.getValue()))
					filtered = false;
			
			if(filter.getValue().getClass().equals(Integer.class))
				if(Integer.parseInt(items[indexColumnToFilter]) != ((Integer) filter.getValue()))
					filtered = false;
			
			if(filter.getValue().getClass().equals(Boolean.class))
				if(Boolean.parseBoolean(items[indexColumnToFilter]) != ((Boolean) filter.getValue()))
					filtered = false;
			
		}
		
		return filtered;
	}


    //CSV file header
	public static <T extends Entity> void Write(List<T> pBufferedList, T pInstance) {
        FileWriter fileWriter = null;
		//Create a new list of student objects        
        try {
            fileWriter = new FileWriter(pInstance.getFilePath());
            //Write the CSV file header
            fileWriter.append(pInstance.getFileHeader().toString());
            //Add a new line separator after the header
            fileWriter.append(CSVDocumentModel.NEW_LINE_SEPARATOR);
            //Write a new student object list to the CSV file
            for (T record : pBufferedList) {
            	fileWriter.append(record.getToLine(record));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
