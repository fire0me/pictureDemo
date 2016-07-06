package com.netease.picture.domain;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class SubPicture {

	private BufferedImage img;
	private double averageR;
	private double averageG;
	private double averageB;

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public double getAverageR() {
		return averageR;
	}

	public void setAverageR(double averageR) {
		this.averageR = averageR;
	}

	public double getAverageG() {
		return averageG;
	}

	public void setAverageG(double averageG) {
		this.averageG = averageG;
	}

	public double getAverageB() {
		return averageB;
	}

	public void setAverageB(double averageB) {
		this.averageB = averageB;
	}

}
