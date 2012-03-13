package es.upm.fi.dia.oeg.integrator.web;

import java.util.Set;

public class Sensor
{
	private String name;
	private String deployment;
	private String platform;	
	private Set<String> fields;
	private double lat;
	private double lon;
	private String startTime;
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	public void setFields(Set<String> fields)
	{
		this.fields = fields;
	}
	public Set<String> getFields()
	{
		return fields;
	}
	public void setLat(double lat)
	{
		this.lat = lat;
	}
	public double getLat()
	{
		return lat;
	}
	public void setLon(double lon)
	{
		this.lon = lon;
	}
	public double getLon()
	{
		return lon;
	}
	public void setDeployment(String deployment)
	{
		this.deployment = deployment;
	}
	public String getDeployment()
	{
		return deployment;
	}
	public void setPlatform(String platform)
	{
		this.platform = platform;
	}
	public String getPlatform()
	{
		return platform;
	}
	
	public String fieldsList()
	{
		String fieldList="";
		for (String f :fields)
		{
			fieldList+=(f.startsWith("_")?f.substring(1):f)+",";
		}
		return fieldList.substring(0,fieldList.length()-1);
	}
	
	public String getGsnUrl()
	{
		return "http://montblanc.slf.ch:22001/multidata?vs[0]="+name+"&field[0]="+fieldsList();
	}
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	public String getStartTime()
	{
		return startTime;
	}
}
