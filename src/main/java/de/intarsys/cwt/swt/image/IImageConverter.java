package de.intarsys.cwt.swt.image;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.ImageData;

public abstract interface IImageConverter {

	boolean accept(BufferedImage image);

	ImageData createImageData(BufferedImage srcImage);
}
