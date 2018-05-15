package com.solr.test;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class SolorDritver {

	String urlString  = "http://192.168.56.1/solr/#/test";
	SolrClient solr = new HttpSolrClient.Builder(urlString).build();

}
