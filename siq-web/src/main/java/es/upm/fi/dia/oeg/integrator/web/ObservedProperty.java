package es.upm.fi.dia.oeg.integrator.web;

public class ObservedProperty
{
	private String name;
	private String uri;
	private String label;
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}
	public String getLabel()
	{
		return label;
	}
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	public String getUri()
	{
		return uri;
	}

}
