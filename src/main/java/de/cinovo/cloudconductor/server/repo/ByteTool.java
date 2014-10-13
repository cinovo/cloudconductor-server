package de.cinovo.cloudconductor.server.repo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ByteTool {

	public String format(long bytes) {
		BigDecimal byteCount = new BigDecimal(bytes).setScale(3, RoundingMode.HALF_UP);
		int thousandStep = 0;

		while (byteCount.longValue() > 1024) {
			byteCount = byteCount.divide(new BigDecimal("1024"), 3, RoundingMode.HALF_UP);
			thousandStep++;
		}

		return byteCount.toPlainString() + " " + this.getUnit(thousandStep);
	}
	
	private String getUnit(int thousandStep) {
		switch (thousandStep) {
		case 0:
			return "B";
		case 1:
			return "KB";
		case 2:
			return "MB";
		case 3:
			return "GB";
		case 4:
			return "TB";
		case 5:
			return "PB";
		default:
			return "10^" + (thousandStep * 3) + " B";
		}
	}
}
