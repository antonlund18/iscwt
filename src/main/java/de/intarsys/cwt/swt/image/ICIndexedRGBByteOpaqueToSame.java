package de.intarsys.cwt.swt.image;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class ICIndexedRGBByteOpaqueToSame implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		if (!(colorModel instanceof IndexColorModel)) {
			return false;
		}
		if (!colorModel.getColorSpace().isCS_sRGB()) {
			return false;
		}
		if (!(colorModel.getTransparency() == Transparency.OPAQUE)) {
			return false;
		}
		DataBuffer dataBuffer = image.getRaster().getDataBuffer();
		return dataBuffer instanceof DataBufferByte;
	}

	@Override
	public ImageData createImageData(BufferedImage srcImage) {
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		IndexColorModel srcColorModel = (IndexColorModel) srcImage
				.getColorModel();
		int srcMapSize = srcColorModel.getMapSize();
		int[] srcMap = new int[srcMapSize];
		srcColorModel.getRGBs(srcMap);
		int srcPixelSize = srcColorModel.getPixelSize();
		RGB[] colors = new RGB[(int) Math.pow(2, srcPixelSize)];
		for (int index = 0; index < srcMap.length; index++) {
			RGB rgb = new RGB((srcMap[index] >> 16) & 0xFF,
					(srcMap[index] >> 8) & 0xFF, srcMap[index] & 0xFF);
			colors[index] = rgb;
		}
		for (int index = srcMap.length; index < colors.length; index++) {
			colors[index] = new RGB(0, 0, 0);
		}
		PaletteData paletteData = new PaletteData(colors);
		DataBufferByte dataBuffer = (DataBufferByte) srcImage.getRaster()
				.getDataBuffer();
		byte[] data = dataBuffer.getData();
		ImageData imageData = new ImageData(srcWidth, srcHeight, srcPixelSize,
				paletteData, 3, data);
		return imageData;
	}
}
