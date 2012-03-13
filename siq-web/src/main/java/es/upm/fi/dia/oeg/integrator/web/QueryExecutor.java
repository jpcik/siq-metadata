package es.upm.fi.dia.oeg.integrator.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.resultset.ResultSetException;

public class QueryExecutor
{

	private String gsnServerUrl;
	private String metadataSparqlUrl;
	
	public QueryExecutor(String metadataSparqlUrl)
	{
		this.metadataSparqlUrl = metadataSparqlUrl;
	}

	
	public Collection<ObservedProperty> queryProperties()
	{
		String sparql = "PREFIX geo-pos: <http://www.w3.org/2003/01/geo/wgs84_pos#> "+
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
		"PREFIX swissex: <http://swiss-experiment.ch/metadata#> "+  
		"PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "+ 
		"SELECT DISTINCT ?prop "+
		"WHERE { "+
		"?sensor ssn:observes ?propinst." +
		"?propinst a ?prop. " +
		"FILTER isIRI(?prop)."+
		"}";
		
		Query qry = QueryFactory.create(sparql);
		QueryExecution qrexec = QueryExecutionFactory.sparqlService(metadataSparqlUrl,qry);
		

		ResultSet resp = qrexec.execSelect();

		Collection<ObservedProperty> properties = new ArrayList<ObservedProperty>();
		while (resp.hasNext())
		{
			QuerySolution qs = resp.nextSolution();
			System.out.println(qs);
			Resource propertyClass = qs.getResource("prop");
			ObservedProperty obsProp = new ObservedProperty();
			obsProp.setUri(propertyClass.getURI());
			obsProp.setLabel(propertyClass.getLocalName());
			properties.add(obsProp);
		}
		return properties;
	}
	
	public Collection<Sensor> query(QueryManager manager)
	{
		
		String obsPropertyClassUri = manager.getObservedProperty();
		String lat1 = manager.getBboxLat1();
		String lon1 = manager.getBboxLon1();
		String lat2 = manager.getBboxLat2();
		String lon2 = manager.getBboxLon2();
		
		String deploymentString = "FILTER regex(?deploymentName,\""+manager.getDeploymentName()+"\",\"i\") . ";
		if (manager.getDeploymentName().trim().equals(""))
			deploymentString = "";
		String platformString = "FILTER regex(?platformName,\""+manager.getPlatformName()+"\",\"i\") . ";
		if (manager.getPlatformName().trim().equals(""))
			platformString = "";
		
		String endString = "OPTIONAL {  ?sensor  ssn:endTime ?etime . "+
					"FILTER (xsd:dateTime(?etime)<=xsd:dateTime(\""+manager.getEndDate()+"\")) } . ";
		String startString = "OPTIONAL {  ?sensor  ssn:startTime ?stime }. "+
					"FILTER (xsd:dateTime(?stime)>=xsd:dateTime(\""+manager.getStartDate()+"\")) . ";
		if (manager.getEndDate().trim().equals("")) 
			endString = "OPTIONAL {  ?sensor  ssn:endTime ?etime } . "; 
		if (manager.getStartDate().trim().equals("")) 
			startString = "OPTIONAL {  ?sensor  ssn:startTime ?stime } . "; 
		
		String sparql = "PREFIX geo-pos: <http://www.w3.org/2003/01/geo/wgs84_pos#> "+
		"PREFIX geo-ont: <http://www.geonames.org/ontology#> "+
		"PREFIX omgeo: <http://www.ontotext.com/owlim/geo#> "+
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
		"PREFIX dul:	<http://www.loa-cnr.it/ontologies/DUL.owl#> "+
		"PREFIX swissex: <http://swiss-experiment.ch/metadata#> "+  
		"PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "+ 
		"PREFIX propPressure: <http://sweet.jpl.nasa.gov/2.1/propPressure.owl#> "+ 
		"PREFIX propTemperature: <http://sweet.jpl.nasa.gov/2.1/propTemperature.owl#> "+ 
		"PREFIX rr: <http://www.w3.org/ns/r2rml#> "+
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
		"SELECT DISTINCT ?lat ?long ?platform ?sensor ?field ?table ?platformName ?deploymentName ?stime "+
		"WHERE { "+
		"?tMap 	rr:predicateObjectMap ?obspoMap, ?obppoMap;" +
		" 		rr:refPredicateObjectMap ?fpoMap; "+
		"		rr:tableName ?table. "+
		"?obspoMap rr:objectMap [ rr:object ?sensor ]. "+  
		"?obppoMap rr:objectMap [ rr:object [ a <"+obsPropertyClassUri+"> ] ]. "+
		"?fpoMap rr:refObjectMap   [ rr:parentTriplesMap ?outputTMap ]. "+  		
		"?outputTMap rr:refPredicateObjectMap [ rr:refObjectMap [ rr:parentTriplesMap ?obsValueTMap]]. "+
		"?obsValueTMap rr:predicateObjectMap [rr:objectMap [rr:column ?field] ] . "+
		"?sensor ssn:observes [ a <"+obsPropertyClassUri+"> ]. "+
		startString+endString+
		"?system ssn:hasSubSystem ?sensor; "+
		"        ssn:onPlatform ?platform; "+
		"        ssn:hasDeployment ?deployment. "+
		"?deployment foaf:name ?deploymentName. "+
		deploymentString+
		"?platform dul:hasLocation ?region. "+
		"?platform foaf:name ?platformName. " +
		platformString+
		"?region swissex:hasGeometry ?link. "+
		"?link omgeo:within("+lat1+" "+lon1+" "+lat2+" "+lon2+") . "+
		"?link geo-pos:lat ?lat . "+
		"?link geo-pos:long ?long . "+
		"} ";
		System.out.println(sparql);
		return execute(sparql);
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<Sensor> execute(String queryString)
	{
		Collection<Sensor> col = new ArrayList<Sensor>();

		Query qry = QueryFactory.create(queryString);
		QueryExecution qrexec = QueryExecutionFactory.sparqlService(metadataSparqlUrl,qry);


		ResultSet resp = null;
		try{
			resp =qrexec.execSelect();
		} catch (ResultSetException e)
		{
			System.out.println(e.getMessage());
			return col;
		}
		int i=0;
		//int j=0;
		List<String> urls = new ArrayList<String>();
		Map<String,Object> map =null;// new HashMap<String,Object>();
		Sensor s=null;
		String tableTemp ="";
		while (resp.hasNext())
		{
			QuerySolution qs = resp.next();
			System.out.println(qs);

			Resource sensor = qs.getResource("sensor");
			Resource tMap = qs.getResource("tMap");
			Literal field = qs.getLiteral("field");
			Literal deploymentName = qs.getLiteral("deploymentName");
			Literal platformName = qs.getLiteral("platformName");
			Literal table = qs.getLiteral("table");
			Literal lat = qs.getLiteral("lat");
			Literal lon = qs.getLiteral("long");
			Literal stime = qs.getLiteral("stime");
				
			if (!tableTemp.equals(table.getString()))
			{
				//String url= "http://planetdata.epfl.ch:22001/multidata?";
				s = new Sensor();
				s.setName(table.getString());
				s.setDeployment(deploymentName.getString());
				s.setPlatform(platformName.getString());
				s.setLat(Double.parseDouble(lat.getString()));
				s.setLon(Double.parseDouble(lon.getString()));
				if (stime!=null)
					s.setStartTime(stime.getString());
				//map.put("vs", table.getString());
				s.setFields(new HashSet<String>());
				//map.put("fields", new HashSet<String>());
				col.add(s);
				//col.add(map);
				tableTemp=table.getString();
				urls.add(i, "vs["+i+"]="+tableTemp+"&field["+i+"]=");
				System.out.println("vs["+i+"]="+table.getString());
				i++;
				//j=0;
			}
			//else
			//{j++;}
			Set<String> set = s.getFields();
			set.add(field.getString());
			urls.add(i-1 ,urls.remove(i-1)+field.getString()+",");
			//System.out.println("field["+(i-1)+"]="+field.getString());				
				
	
		}		
		for (String url : urls)
		{
			System.out.println(url);
		}

		return col;
	}
}
