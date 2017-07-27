package de.intarsys.cwt.swt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

import org.eclipse.swt.graphics.ImageData;

public class ICComponentByteTransparentToPlatform implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		if (!(colorModel instanceof ComponentColorModel)) {
			return false;
		}
		if(colorModel.getColorSpace() instanceof ICC_ColorSpace) {
			// could handle but others are faster
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
		int srcSize = srcWidth * srcHeight;
		byte[] alphaData = new byte[srcSize];
		byte[] data = new byte[srcSize * 3];
		Object[] allCache = new Object[256];
		ColorModel srcColorModel = srcImage.getColorModel();
		ColorSpace srcColorSpace = srcColorModel.getColorSpace();
		int srcNumColorComponents = srcColorModel.getNumColorComponents();
		Raster srcRaster = srcImage.getRaster();
		byte[] srcData = ((DataBufferByte) srcRaster.getDataBuffer()).getData();
		float[] temp = new float[srcNumColorComponents];
		ComponentSampleModel srcSampleModel = (ComponentSampleModel) srcImage
				.getSampleModel();
		int[] srcBandOffsets = srcSampleModel.getBandOffsets();
		int[] bandOffsets = ComponentOrder.PlatformInstance.getBandOffsets();
		int srcNumComponents = srcColorModel.getNumComponents();
		for (int index = 0; index < srcSize; index++) {
			Object[] componentCache = allCache;
			int value;
			for (int subIndex = 0; subIndex < srcNumColorComponents - 1; subIndex++) {
				value = srcData[index * srcNumComponents
						+ srcBandOffsets[subIndex]] & 0xFF;
				Object[] current = (Object[]) componentCache[value];
				if (current == null) {
					current = new Object[256];
					componentCache[value] = current;
				}
				componentCache = current;
			}
			value = srcData[index * srcNumComponents
					+ srcBandOffsets[srcNumColorComponents - 1]] & 0xFF;
			byte[] rgbs = (byte[]) componentCache[value];
			if (rgbs == null) {
				temp[srcNumColorComponents - 1] = ((float) value) / 255;
				for (int subIndex = 0; subIndex < srcNumColorComponents - 1; subIndex++) {
					temp[subIndex] = ((float) (srcData[index * srcNumComponents
							+ srcBandOffsets[subIndex]] & 0xFF)) / 255;
				}
				float[] rgb = srcColorSpace.toRGB(temp);
				rgbs = new byte[3];
				rgbs[0] = (byte) (rgb[0] * 255);
				rgbs[1] = (byte) (rgb[1] * 255);
				rgbs[2] = (byte) (rgb[2] * 255);
				componentCache[value] = rgbs;
			}
			data[index * 3] = rgbs[bandOffsets[0]];
			data[index * 3 + 1] = rgbs[bandOffsets[1]];
			data[index * 3 + 2] = rgbs[bandOffsets[2]];
			alphaData[index] = srcData[index * srcNumComponents
					+ srcNumComponents - 1];
		}
		ImageData imageData = new ImageData(srcWidth, srcHeight, 24,
				ComponentOrder.PlatformInstance.getPaletteData(), 3, data);
		imageData.alphaData = alphaData;
		return imageData;
	}
}
