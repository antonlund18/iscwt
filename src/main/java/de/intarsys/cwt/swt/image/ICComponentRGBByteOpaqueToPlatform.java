package de.intarsys.cwt.swt.image;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

import org.eclipse.swt.graphics.ImageData;

public class ICComponentRGBByteOpaqueToPlatform implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		if (!(colorModel instanceof ComponentColorModel)) {
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
		int srcSize = srcWidth * srcHeight;
		byte[] data = new byte[srcSize * 3];
		Raster srcRaster = srcImage.getRaster();
		int[] bandOffsets = ComponentOrder.PlatformInstance.getBandOffsets();
		ComponentSampleModel srcSampleModel = (ComponentSampleModel) srcImage
				.getSampleModel();
		int[] srcBandOffsets = srcSampleModel.getBandOffsets();
		int zero = bandOffsets[0] - srcBandOffsets[0];
		int one = bandOffsets[1] - srcBandOffsets[1] + 1;
		int two = bandOffsets[2] - srcBandOffsets[2] + 2;
		byte[] srcData = ((DataBufferByte) srcRaster.getDataBuffer()).getData();
		for (int index = 0; index < srcSize; index++) {
			data[index * 3] = srcData[index * 3 + zero];
			data[index * 3 + 1] = srcData[index * 3 + one];
			data[index * 3 + 2] = srcData[index * 3 + two];
		}
		ImageData imageData = new ImageData(srcWidth, srcHeight, 24,
				ComponentOrder.PlatformInstance.getPaletteData(), 3, data);
		return imageData;
	}
}
