package com.netease.picture.enums;

/** 
* @author bjchenyuan
* @version 2016年6月13日 下午4:31:32
*/
public enum PictureEnum
{
	DOTA(1, "dota", "picture/dota/"), LOL(2, "lol", "picture/lol/");

	private PictureEnum(int value, String name, String path)
	{
		this.value = value;
		this.name = name;
		this.path = path;
	}

	private int value;

	private String name;

	private String path;

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

}
