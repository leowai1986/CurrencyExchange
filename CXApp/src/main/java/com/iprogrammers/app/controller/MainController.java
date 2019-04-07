package com.iprogrammers.app.controller;

import com.iprogrammers.app.Model.Currency;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.*;

@Controller
public class MainController {
	
	public Page<Currency> findPaginated(Pageable pageable, List<Currency> currencies) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage == 1 ? 0 : (currentPage-1) * pageSize;
        List<Currency> list;
 
        if (currencies.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, currencies.size());
            list = currencies.subList(startItem, toIndex);
        }
        
        PageRequest page_req = new PageRequest(currentPage, pageSize, Direction.DESC, "idNode");
 
        Page<Currency> currPage = new PageImpl<Currency>(list, page_req, currencies.size());
 
        return currPage;
    }
	
	@GetMapping(value="/history")
    public String history (ModelMap model, 
    	      @RequestParam("page") Optional<Integer> page, 
    	      @RequestParam("size") Optional<Integer> size) throws IOException {
		List<Currency> currencyList = new ArrayList<Currency>();
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(new File("src\\main\\java\\HistoryExchange.txt"), "rw");
			String line;
			while((line = f.readLine()) != null) { 
				String[] arr = line.split("-");
				Currency currency = new Currency();
				currency.setUpdate(arr[0]);
				currency.setSource(arr[1]);
				currency.setTarget(arr[2]);
				currency.setValue(new Double(arr[3]));
				currency.setQuantity(new Double(arr[4]));
				currency.setAmount(new Double(arr[5]));
				currencyList.add(currency);
        	}
			f.close();
		} catch(Exception e) {
			e.printStackTrace();			
		}
		
		Collections.reverse(currencyList); 
		
		int currentPage = page.orElse(1);
        int pageSize = size.orElse(15);
        
        PageRequest page_req = new PageRequest(currentPage, pageSize, Direction.DESC, "idNode");
 
        Page<Currency> currPage = findPaginated(page_req, currencyList);
 
        model.addAttribute("currPage", currPage);
 
        int totalPages = (int) Math.ceil(new Double(currencyList.size()) / new Double(pageSize));
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
		
        return "history";
	}
	
	@GetMapping(value="/")
    public String home (ModelMap model) { 
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src\\main\\java\\config.properties"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		RestTemplate restTemplate = new RestTemplate();  
		HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange("http://api.valuta.money/v1/full/USD/json?key=" + prop.getProperty("key").toString(), HttpMethod.GET, entity, String.class);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
	    
	    JSONObject obj = new JSONObject(response);
	    String bodyString = obj.getString("body");
	    JSONObject jsonObj = new JSONObject(bodyString);
	    JSONObject resultObj = jsonObj.getJSONObject("result");

	    List<String> curString = new ArrayList<String>();
	    curString.add(resultObj.getString("from"));
	    JSONArray arr = resultObj.getJSONArray("conversion");
	    for (int i = 0; i < arr.length(); i++)
	    {
	        curString.add(arr.getJSONObject(i).getString("to"));
	    }
		
	    model.addAttribute("currList", curString);
	    
        return "index";
    }
	
	@PostMapping(value="/result")
	public String CurrencyExchange(ModelMap model, String currencySrc, String currencyTarget, String qty){
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src\\main\\java\\config.properties"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		RestTemplate restTemplate = new RestTemplate();  
		HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange("https://api.cambio.today/v1/quotes/" + currencySrc + "/" + currencyTarget + "/json?quantity=" + qty + "&key=" + prop.getProperty("key").toString(), HttpMethod.GET, entity, String.class);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
	    
	    JSONObject obj = new JSONObject(response);
	    Currency currency = new Currency();
	    String bodyString = obj.getString("body");
	    
	    JSONObject jsonObj = new JSONObject(bodyString);
	    
	    JSONObject resultObj = jsonObj.getJSONObject("result");
	    
	    currency.setUpdate((new Date()).toString());
	    currency.setSource(resultObj.getString("source"));
	    currency.setTarget(resultObj.getString("target"));
	    currency.setValue(resultObj.getDouble("value"));
	    currency.setQuantity(resultObj.getDouble("quantity"));
	    currency.setAmount(resultObj.getDouble("amount"));

	    RandomAccessFile f;
		try {
			f = new RandomAccessFile(new File("src\\main\\java\\HistoryExchange.txt"), "rw");
			while(f.readLine() != null) {}
			f.write((currency.getUpdate() + "-" + currency.getSource() + "-" + currency.getTarget() + "-" + currency.getValue() + "-" + currency.getQuantity() + "-" + currency.getAmount()).getBytes());
			f.write("\n".getBytes());
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("result", currency);
        return "result";
	}
}