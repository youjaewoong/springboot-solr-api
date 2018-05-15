package com.solr.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/")
public class main {
	
	SolrClient solr = null;
	
	public main() {
		String urlString  = "http://localhost:8983/solr/test";
		solr = new HttpSolrClient.Builder(urlString).build();
	}
	
	@RequestMapping(value="search" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
	public SolrDocumentList solrSearch() throws IOException, SolrServerException  {
		
		SolrQuery query = new SolrQuery();
		
		String keyword = "테스트";
		String queryString = "";
		queryString = "*AND title : " + keyword + "*";
		
		query.setQuery(queryString);
		QueryResponse res = solr.query(query);
		SolrDocumentList docs=res.getResults();  
		return docs;
	}
	
	@RequestMapping(value="delete" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
	public int solrDelte() throws ClientProtocolException, IOException  {
		URL obj = new URL("http://localhost:8983/solr/test/update?commit=true&stream.body=<delete><query>*:*</query></delete>");
		HttpURLConnection con = (HttpURLConnection)obj.openConnection();
		return con.getResponseCode();
	}
	
	@RequestMapping(value="insert" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
	public String solrInset() throws ClientProtocolException, IOException, SolrServerException  {
		
/*		SolrInputDocument solrDoc = new SolrInputDocument();
		solrDoc.addField("id", "아이디 테스트");
		solrDoc.addField("title", "타이틀 테스트");
		solrDoc.addField("writer", "글쓰기 테스트");
		solrDoc.addField("board", "메모장 테스트");
		solrDoc.addField("date", 123);
		
		Collection<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();
		solrDocs.add(solrDoc);
		solr.add(solrDoc);
		solr.commit();
		*/
		
		HashMap<Object,String> params = new HashMap<Object,String>();
		params.put("commit", "true");
		params.put("stream.url", "http://localhost:8081/api/getListJsonToSolr");
		
		CloseableHttpClient client = HttpClients.createDefault();
		
		List<NameValuePair> paramList = convertParam(params);
		
        HttpGet get = new HttpGet("http://localhost:8983/solr/test/update/json?" + URLEncodedUtils.format(paramList, "UTF-8"));
        get.setHeader("Content-type", "application/json");
        
        CloseableHttpResponse res = client.execute(get);
        String json = EntityUtils.toString(res.getEntity(), "UTF-8");
        client.close();

		return json;
	}
	
	@RequestMapping(value="select" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
	public SolrDocumentList solrSelect() throws ClientProtocolException, IOException, SolrServerException  {
		
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(Integer.MAX_VALUE);
        query.addSort("id", ORDER.desc);

        QueryResponse rsp = solr.query(query);
        SolrDocumentList docs=rsp.getResults();  
        for(int i=0;i<docs.getNumFound();i++){
            System.out.println(docs.get(i));
        }

		return rsp.getResults();
	}
	
	private List<NameValuePair> convertParam(Map params){
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        Iterator<String> keys = params.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
         
        return paramList;
    }
	
    @RequestMapping(value = "getListJsonToSolr")
    @ResponseBody
    public List<?> getSearchListJsonToSolr() {
      
    	Map<String, Object> params = new HashMap<String, Object>();
      
		params.put("id", "아이디 테스트");
		params.put("title", "타이틀 테스트");
		params.put("writer", "글쓰기 테스트");
		params.put("board", "메모장 테스트");
		params.put("commit", "true");
		
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(params);
		
		return list;
    }
	
}
