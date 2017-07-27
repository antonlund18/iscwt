package de.intarsys.cwt.swt.image;

import org.eclipse.swt.graphics.PaletteData;

import de.intarsys.tools.system.SystemTools;

public abstract class ComponentOrder {

	public static final ComponentOrder PlatformInstance;

	static {
		if (SystemTools.isWindows()) {
			PlatformInstance = new ComponentOrderBGR();
		} else {
			PlatformInstance = ComponentOrderRGB.Instance;
		}
	}

	private int[] bandOffsets;
	private PaletteData paletteData;

	public ComponentOrder(PaletteData paletteData) {
		this.paletteData = paletteData;
		bandOffsets = new int[3];
		fillBandOffsets(bandOffsets);
	}

	public abstract void fillBandOffsets(int[] bandOffsets);

	public int[] getBandOffsets() {
		return bandOffsets;
	}

	public PaletteData getPaletteData() {
		return paletteData;
	}
}
