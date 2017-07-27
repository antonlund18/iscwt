package de.intarsys.cwt.swt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

public class ICComponentGrayByteTransparentToSame implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		if (!(colorModel instanceof ComponentColorModel)) {
			return false;
		}
		if (!ColorSpace.getInstance(ColorSpace.CS_GRAY).equals(
				colorModel.getColorSpace())) {
			return false;
		}
		if (!(colorModel.getTransparency() == Transparency.TRANSLUCENT)) {
			return false;
		}
		DataBuffer dataBuffer = image.getRaster().getDataBuffer();
		return dataBuffer instanceof DataBufferByte;
	}

	@Override
	public ImageData createImageData(BufferedImage srcImage) {
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		byte[] alphaData = new byte[srcWidth * srcHeight];
		byte[] data = new byte[srcWidth * srcHeight];
		Raster srcRaster = srcImage.getRaster();
		byte[] srcData = ((DataBufferByte) srcRaster.getDataBuffer()).getData();
		for (int index = 0; index < alphaData.length; index++) {
			data[index] = srcData[index * 2 + 0];
			alphaData[index] = srcData[index * 2 + 1];
		}
		ImageData imageData = new ImageData(srcWidth, srcHeight, 8,
				new PaletteData(0xFF, 0xFF, 0xFF), 1, data);
		imageData.alphaData = alphaData;
		return imageData;
	}
}
