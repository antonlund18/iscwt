package de.intarsys.cwt.swt.image;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.Arrays;

import org.eclipse.swt.graphics.ImageData;

public class ICComponentRGBByteOpaqueToSame implements IImageConverter {

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
		if (!(dataBuffer instanceof DataBufferByte)) {
			return false;
		}
		/*
		 * as our primary target is rendering, we always want to get the
		 * platform palette for RGB, because with that the rendering is much
		 * faster; therefore we only accept images here where the band offsets
		 * are those of the platform
		 */
		ComponentSampleModel sampleModel = (ComponentSampleModel) image
				.getSampleModel();
		return Arrays.equals(ComponentOrder.PlatformInstance.getBandOffsets(),
				sampleModel.getBandOffsets());
	}

	@Override
	public ImageData createImageData(BufferedImage srcImage) {
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		Raster srcRaster = srcImage.getRaster();
		byte[] srcData = ((DataBufferByte) srcRaster.getDataBuffer()).getData();
		byte[] data = srcData;
		ImageData imageData = new ImageData(srcWidth, srcHeight, 24,
				ComponentOrder.PlatformInstance.getPaletteData(), 3, data);
		return imageData;
	}
}
