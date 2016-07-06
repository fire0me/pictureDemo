package com.netease.picture.service.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.netease.picture.constant.PictureTargetConstant;
import com.netease.picture.controller.PictureController;
import com.netease.picture.domain.SubPicture;
import com.netease.picture.service.JigsawService;

@Service
public class JigsawServiceImpl implements JigsawService
{

	private static final Logger logger = LoggerFactory.getLogger(JigsawServiceImpl.class);
	//	static int srcWidth;
	//	static int srcHight;
	private int dstWidth = 120;
	private int dstHight = 90;
	private int dstIconWidth = 32;
	private int dstIconHight = 32;

	public BufferedImage genNewPicture(BufferedImage srcPicture, String name) throws Exception
	{
		String path = PictureTargetConstant.getPath(name);
		List<SubPicture> subPictures = getSubPictures(path);
		BufferedImage smallPic = resize(srcPicture, dstWidth, dstHight);
		BufferedImage newPic = genNewPicture(smallPic, subPictures);
		return newPic;
	}

	//	public static void main(String[] args) throws Exception
	//	{
	//		// TODO Auto-generated method stub
	//		List<SubPicture> subPictures = getSubPictures("D:/programing/java/dota");
	//		BufferedImage srcPicture = loadSrcPicture("D:/programing/java/test.jpg");
	//		BufferedImage smallPic = resize(srcPicture, dstWidth, dstHight);
	//		BufferedImage newPic = genNewPicture(smallPic, subPictures);
	//		savePicture(newPic, "D:/programing/java/tou1301.jpg");
	//
	//		System.out.print("OK");
	//	}

	private List<SubPicture> getSubPictures(String path) throws Exception
	{
		List<SubPicture> subPictures = new ArrayList<SubPicture>();
		String classPath = PictureController.class.getResource("/").getPath();
		classPath = classPath.replace("%20", " ");
		if (isWindows())
			classPath = classPath.substring(1);
		path = classPath + path;
		File iconFolder = new File(path);
		for (File icon : iconFolder.listFiles())
		{
			BufferedImage image = ImageIO.read(icon);
			image = resize(image, dstIconWidth, dstIconHight);
			SubPicture subPicture = new SubPicture();
			subPicture.setImg(image);
			subPictures.add(subPicture);
			double averageR = 0;
			double averageG = 0;
			double averageB = 0;
			short tmpRGB;
			for (int i = 0; i < image.getWidth(); i++)
				for (int j = 0; j < image.getHeight(); j++)
				{
					int rgb = image.getRGB(i, j);
					tmpRGB = (byte) rgb;
					if (tmpRGB < 0)
						tmpRGB += 256;
					averageB = averageB + tmpRGB; // b
					rgb = rgb >>> 8;
					tmpRGB = (byte) rgb;
					if (tmpRGB < 0)
						tmpRGB += 256;
					averageG = averageG + tmpRGB; // g
					rgb = rgb >>> 8;
					tmpRGB = (byte) rgb;
					if (tmpRGB < 0)
						tmpRGB += 256;
					averageR = averageR + tmpRGB; // r
				}
			subPicture.setAverageB(averageB / dstIconWidth / dstIconHight);
			subPicture.setAverageR(averageR / dstIconWidth / dstIconHight);
			subPicture.setAverageG(averageG / dstIconWidth / dstIconHight);
		}
		return subPictures;
	}

	private BufferedImage getNearestImg(short[] rgbs, List<SubPicture> subPictures)
	{
		double sum;
		double min = 65535;
		BufferedImage rightImg = subPictures.get(0).getImg();
		SubPicture icon;
		for (int i = 0; i < subPictures.size(); i++)
		{
			icon = subPictures.get(i);
			sum = Math.abs(icon.getAverageB() - rgbs[0]) + Math.abs(icon.getAverageG() - rgbs[1])
					+ Math.abs(icon.getAverageR() - rgbs[2]);
			if (sum < min)
			{
				min = sum;
				rightImg = icon.getImg();
			}
		}
		return rightImg;
	}

	//	public void copyImage()
	//	{
	//
	//	}

	private BufferedImage genNewPicture(BufferedImage smallPic, List<SubPicture> subPictures)
	{

		int hight = smallPic.getHeight();
		int width = smallPic.getWidth();
		BufferedImage newImage = new BufferedImage(width * dstIconWidth, hight * dstIconHight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();
		short[] rgbs = new short[3];

		for (int i = 0; i < width; i++)
			for (int j = 0; j < hight; j++)
			{
				int rgb = smallPic.getRGB(i, j);
				rgbs[0] = (byte) rgb; // b
				if (rgbs[0] < 0)
					rgbs[0] += 256;
				rgb = rgb >>> 8;
				rgbs[1] = (byte) rgb; // g
				if (rgbs[1] < 0)
					rgbs[1] += 256;
				rgb = rgb >>> 8;
				rgbs[2] = (byte) rgb; // r
				if (rgbs[2] < 0)
					rgbs[2] += 256;
				Image rightImage = getNearestImg(rgbs, subPictures);
				g.drawImage(rightImage, i * dstIconWidth, j * dstIconHight, null);
			}
		g.dispose();
		return newImage;

	}

	private BufferedImage loadSrcPicture(String path) throws Exception
	{
		File imageFile = new File(path);
		BufferedImage image = ImageIO.read(imageFile);
		return image;
	}

	private BufferedImage resize(BufferedImage source, int targetW, int targetH)
	{
		// targetW，targetH分别表示目标长和宽
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		// 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
		// 则将下面的if else语句注释即可
		if (sx < sy)
		{
			sx = sy;
			targetW = (int) (sx * source.getWidth());
		}
		else
		{
			sy = sx;
			targetH = (int) (sy * source.getHeight());
		}
		if (type == BufferedImage.TYPE_CUSTOM)
		{ // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		}
		else
			target = new BufferedImage(targetW, targetH, type);
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

	private void savePicture(BufferedImage img, String path)
	{
		try
		{
			File file = new File(path);
			String formate = "JPEG";
			ImageIO.write(img, formate, file);
		}
		catch (Exception e)
		{

		}
	}

	private boolean isWindows()
	{
		if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1)
			return true;
		return false;
	}

}
