package com.netease.picture.service;

import java.awt.image.BufferedImage;

/** 
* @author bjchenyuan
* @version 2016年6月13日 下午3:54:55
*/
public interface JigsawService
{
	public BufferedImage genNewPicture(BufferedImage srcPicture, String name) throws Exception;
}
