package com.netease.picture.controller;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.netease.picture.service.JigsawService;

/** 
* @author bjchenyuan
* @version 2016年6月13日 下午4:41:21
*/
@Controller
@RequestMapping
public class PictureController
{

	private static final Logger logger = LoggerFactory.getLogger(PictureController.class);

	@Autowired
	private JigsawService jigsawService;

	@RequestMapping(value = "/show.html", method = RequestMethod.POST)
	public void transfromPicture(String targetName, @RequestParam("file") MultipartFile file, ModelMap map,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		BufferedImage srcPicture = ImageIO.read(file.getInputStream());
		BufferedImage newPic = jigsawService.genNewPicture(srcPicture, targetName);
		String filePath = saveFile(request, newPic);
		filePath = filePath.replace("\\", "/");
		filePath = filePath.substring(filePath.indexOf("/picture/upload"));
		response.sendRedirect("index.html?filePath=" + filePath);
	}

	@RequestMapping(value = "/pageHome.html", method = RequestMethod.GET)
	public String retuanView()
	{
		return "index.html";
	}

	/***
	* 保存文件
	*
	* @param file
	* @return
	*/
	private String saveFile(HttpServletRequest request, BufferedImage image)
	{
		long time = System.currentTimeMillis();
		String filePath = request.getSession().getServletContext().getRealPath("/") + "upload" + File.separator + time
				+ ".jpg";
		File file = new File(filePath);
		// 判断文件是否为空
		if (!file.exists())
		{
			try
			{
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				// 转存文件
				String formate = "JPEG";
				ImageIO.write(image, formate, file);
				return filePath;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

}
