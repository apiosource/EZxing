package com.ccx.ezxing.decode;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;


/**
 * ================================================
 *
 * @author ：ccx
 * 版    本：1.0
 * 创建日期：2018/12/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PlanarYUVLuminanceSourceTest extends LuminanceSource {
    /**
     * Field yuvData
     */
    private final byte[] yuvData;
    /**
     * Field dataWidth
     */
    private final int dataWidth;
    /**
     * Field dataHeight
     */
    private final int dataHeight;
    /**
     * Field left
     */
    private final int left;
    /**
     * Field top
     */
    private final int top;

    /**
     * Constructor PlanarYUVLuminanceSource creates a new PlanarYUVLuminanceSource instance.
     *
     * @param yuvData    of type byte[]
     * @param dataWidth  of type int
     * @param dataHeight of type int
     * @param left       of type int
     * @param top        of type int
     * @param width      of type int
     * @param height     of type int
     */
    public PlanarYUVLuminanceSourceTest(byte[] yuvData, int dataWidth, int dataHeight, int left, int top,
                                    int width, int height) {
        super(width, height);

        if (left + width > dataWidth || top + height > dataHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }

        this.yuvData = yuvData;
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        this.left = left;
        this.top = top;
    }

    /**
     * Method getRow ...
     *
     * @param y   of type int
     * @param row of type byte[]
     * @return byte[]
     */
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

    /**
     * Method getMatrix returns the matrix of this PlanarYUVLuminanceSource object.
     *
     * @return the matrix (type byte[]) of this PlanarYUVLuminanceSource object.
     */
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
        byte[] yuv = yuvData;
        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
            inputOffset += dataWidth;
        }
        return matrix;
    }

    /**
     * Method isCropSupported returns the cropSupported of this PlanarYUVLuminanceSource object.
     *
     * @return the cropSupported (type boolean) of this PlanarYUVLuminanceSource object.
     */
    @Override
    public boolean isCropSupported() {
        return true;
    }

    /**
     * Method getDataWidth returns the dataWidth of this PlanarYUVLuminanceSource object.
     *
     * @return the dataWidth (type int) of this PlanarYUVLuminanceSource object.
     */
    public int getDataWidth() {
        return dataWidth;
    }

    /**
     * Method getDataHeight returns the dataHeight of this PlanarYUVLuminanceSource object.
     *
     * @return the dataHeight (type int) of this PlanarYUVLuminanceSource object.
     */
    public int getDataHeight() {
        return dataHeight;
    }

    /**
     * Method renderCroppedGreyscaleBitmap ...
     *
     * @return Bitmap
     */
    public Bitmap renderCroppedGreyscaleBitmap() {
        int width = getWidth();
        int height = getHeight();
        int[] pixels = new int[width * height];
        byte[] yuv = yuvData;
        int inputOffset = top * dataWidth + left;

        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            for (int x = 0; x < width; x++) {
                int grey = yuv[inputOffset + x] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += dataWidth;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
