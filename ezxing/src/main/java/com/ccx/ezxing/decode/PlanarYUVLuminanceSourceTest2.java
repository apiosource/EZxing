package com.ccx.ezxing.decode;

import com.google.zxing.LuminanceSource;
import com.google.zxing.PlanarYUVLuminanceSource;

/**
 * ================================================
 *
 * @author ：ccx
 * 版    本：1.0
 * 创建日期：2019/1/15
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PlanarYUVLuminanceSourceTest2 extends LuminanceSource {

    private static final int THUMBNAIL_SCALE_FACTOR = 2;

    private final byte[] yuvData;
    private final int dataWidth;
    private final int dataHeight;
    private final int left;
    private final int top;

    public PlanarYUVLuminanceSourceTest2(byte[] yuvData,
                                    int dataWidth,
                                    int dataHeight,
                                    int left,
                                    int top,
                                    int width,
                                    int height,
                                    boolean reverseHorizontal) {
        super(width, height);

        if (left + width > dataWidth || top + height > dataHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }

        this.yuvData = yuvData;
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        this.left = left;
        this.top = top;
        if (reverseHorizontal) {
            reverseHorizontal(width, height);
        }
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        int offset = (y + top) * dataWidth + left;
        System.arraycopy(yuvData, offset, row, 0, width);
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();

        // If the caller asks for the entire underlying image, save the copy and give them the
        // original data. The docs specifically warn that result.length must be ignored.
        if (width == dataWidth && height == dataHeight) {
            return yuvData;
        }

        int area = width * height;
        byte[] matrix = new byte[area];
        int inputOffset = top * dataWidth + left;

        // If the width matches the full width of the underlying data, perform a single copy.
        if (width == dataWidth) {
            System.arraycopy(yuvData, inputOffset, matrix, 0, area);
            return matrix;
        }

        // Otherwise copy one cropped row at a time.
        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            System.arraycopy(yuvData, inputOffset, matrix, outputOffset, width);
            inputOffset += dataWidth;
        }
        return matrix;
    }

    @Override
    public boolean isCropSupported() {
        return true;
    }

    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
        return new PlanarYUVLuminanceSource(yuvData,
                dataWidth,
                dataHeight,
                this.left + left,
                this.top + top,
                width,
                height,
                false);
    }

    public int[] renderThumbnail() {
        int width = getWidth() / THUMBNAIL_SCALE_FACTOR;
        int height = getHeight() / THUMBNAIL_SCALE_FACTOR;
        int[] pixels = new int[width * height];
        byte[] yuv = yuvData;
        int inputOffset = top * dataWidth + left;

        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            for (int x = 0; x < width; x++) {
                int grey = yuv[inputOffset + x * THUMBNAIL_SCALE_FACTOR] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += dataWidth * THUMBNAIL_SCALE_FACTOR;
        }
        return pixels;
    }

    /**
     * @return width of image from {@link #renderThumbnail()}
     */
    public int getThumbnailWidth() {
        return getWidth() / THUMBNAIL_SCALE_FACTOR;
    }

    /**
     * @return height of image from {@link #renderThumbnail()}
     */
    public int getThumbnailHeight() {
        return getHeight() / THUMBNAIL_SCALE_FACTOR;
    }

    private void reverseHorizontal(int width, int height) {
        byte[] yuvData = this.yuvData;
        for (int y = 0, rowStart = top * dataWidth + left; y < height; y++, rowStart += dataWidth) {
            int middle = rowStart + width / 2;
            for (int x1 = rowStart, x2 = rowStart + width - 1; x1 < middle; x1++, x2--) {
                byte temp = yuvData[x1];
                yuvData[x1] = yuvData[x2];
                yuvData[x2] = temp;
            }
        }
    }
}
