package de.intarsys.cwt.swt.image;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.eclipse.swt.graphics.ImageData;

public class ICComponentOpaqueToPlatform implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		if (!(colorModel instanceof ComponentColorModel)) {
			return false;
		}
		return colorModel.getTransparency() == Transparency.OPAQUE;
	}

	@Override
	public ImageData createImageData(BufferedImage srcImage) {
		ColorModel srcColorModel = srcImage.getColorModel();
		ColorSpace srcColorSpace = srcColorModel.getColorSpace();
		ColorSpace destColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorConvertOp colorConvertOp = new ColorConvertOp(srcColorSpace,
				destColorSpace, null);
		Raster srcRaster = srcImage.getRaster();
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		int[] bandOffsets = ComponentOrder.PlatformInstance.getBandOffsets();
		WritableRaster destRaster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, srcWidth, srcHeight, srcWidth * 3, 3,
				bandOffsets, new Point(0, 0));
		colorConvertOp.filter(srcRaster, destRaster);

		DataBufferByte dataBuffer = (DataBufferByte) destRaster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		ImageData imageData = new ImageData(srcWidth, srcHeight, 24,
				ComponentOrder.PlatformInstance.getPaletteData(), 3, data);
		return imageData;
	}
}
