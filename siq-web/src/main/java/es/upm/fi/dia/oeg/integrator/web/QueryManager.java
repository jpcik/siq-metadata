package es.upm.fi.dia.oeg.integrator.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.model.SelectItem;

public class QueryManager
{

	private String zoom;
	private String centerLat;
	private String centerLon;
	private String observedProperty;
	private String bboxLat1;
	private String bboxLon1;
	private String bboxLat2;
	private String bboxLon2;
	private String startDate;
	private String endDate;
	private String deploymentName;
	private String platformName;
	private Collection<ObservedProperty> obsProperties;
	private SelectItem[] properties;
	private QueryExecutor exe;
	private Collection<Sensor> sensors;
	
	public QueryManager()
	{
		exe = new QueryExecutor("http://localhost:8080/openrdf-workbench/repositories/owlimDemo/query");
		obsProperties = exe.queryProperties();
	}
	
	public String query()
	{
		System.out.println("We are doing things"+observedProperty);
		//Map<String, String> params = new HashMap<String,String>();
		/*params.put("observedProperty", observedProperty);
		params.put("bboxLat1", "46.85");
		params.put("bboxLon1", "9.75");
		params.put("bboxLat2", "47.31");
		params.put("bboxLon2", "10.08");*/
		sensors = exe.query(this);
		System.out.println(sensors.size());
		return "queried";
	}

	public void setObservedProperty(String observedProperty)
	{
		this.observedProperty = observedProperty;
	}

	public String getObservedProperty()
	{
		return observedProperty;
	}


	public void setProperties(SelectItem[] properties)
	{
		this.properties = properties;
	}

	public SelectItem[] getProperties()
	{
		return properties;
	}

	public void setBboxLat1(String bboxLat1)
	{
		this.bboxLat1 = bboxLat1;
	}

	public String getBboxLat1()
	{
		return bboxLat1;
	}

	public void setBboxLon1(String bboxLon1)
	{
		this.bboxLon1 = bboxLon1;
	}

	public String getBboxLon1()
	{
		return bboxLon1;
	}

	public void setBboxLon2(String bboxLon2)
	{
		this.bboxLon2 = bboxLon2;
	}

	public String getBboxLon2()
	{
		return bboxLon2;
	}

	public void setBboxLat2(String bboxLat2)
	{
		this.bboxLat2 = bboxLat2;
	}

	public String getBboxLat2()
	{
		return bboxLat2;
	}

	public void setSensors(Collection<Sensor> sensors)
	{
		this.sensors = sensors;
	}

	public Collection<Sensor> getSensors()
	{
		return sensors;
	}

	public void setObsProperties(Collection<ObservedProperty> obsProperties)
	{
		this.obsProperties = obsProperties;
	}

	public Collection<ObservedProperty> getObsProperties()
	{
		return obsProperties;
	}

	public void setZoom(String zoom)
	{
		this.zoom = zoom;
	}

	public String getZoom()
	{
		return zoom;
	}

	public void setCenterLat(String centerLat)
	{
		this.centerLat = centerLat;
	}

	public String getCenterLat()
	{
		return centerLat;
	}

	public void setCenterLon(String centerLon)
	{
		this.centerLon = centerLon;
	}

	public String getCenterLon()
	{
		return centerLon;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setDeploymentName(String deploymentName)
	{
		this.deploymentName = deploymentName;
	}

	public String getDeploymentName()
	{
		return deploymentName;
	}

	public void setPlatformName(String platformName)
	{
		this.platformName = platformName;
	}

	public String getPlatformName()
	{
		return platformName;
	}
}
