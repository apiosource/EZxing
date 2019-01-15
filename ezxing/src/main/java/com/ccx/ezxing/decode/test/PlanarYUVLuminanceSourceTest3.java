package com.ccx.ezxing.decode.test;

import com.google.zxing.LuminanceSource;

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
public final class PlanarYUVLuminanceSourceTest3 extends LuminanceSource {
    public static final  int    MEASURED_STATE_MASK    = -16777216;
    private static final int    THUMBNAIL_SCALE_FACTOR = 2;
    private final        byte[] yuvData;
    private final        int    dataWidth;
    private final        int    dataHeight;
    private final        int    left;
    private final        int    top;



    public PlanarYUVLuminanceSourceTest3(byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z) {
        super(i5, i6);
        if (i3 + i5 > i || i4 + i6 > i2) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
        this.yuvData = bArr;
        this.dataWidth = i;
        this.top = i2;
        this.left = i3;
        this.dataHeight = i4;
        if (z) {
            reverseHorizontal(i5, i6);
        }
    }


    @Override
    public byte[] getRow(int i, byte[] bytes) {
        if (i < 0 || i >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + i);
        }
        byte[] bArr2 = null;
        int    width = getWidth();
        if (bArr2 == null || bArr2.length < width) {
            bArr2 = new byte[width];
        }
        System.arraycopy(this.yuvData, ((this.dataHeight + i) * this.dataWidth) + this.left, bArr2, 0, width);
        return bArr2;
    }

    @Override
    public byte[] getMatrix() {
        int i  = 0;
        int width = getWidth();
        int height = getHeight();
        if (width == this.dataWidth && height == this.top) {
            return this.yuvData;
        }
        int    i2   = width * height;
        byte[] bArr = new byte[i2];
        int    i3   = (this.dataHeight * this.dataWidth) + this.left;
        if (width == this.dataWidth) {
            System.arraycopy(this.yuvData, i3, bArr, 0, i2);
            return bArr;
        }
        while (i < height) {
            System.arraycopy(this.yuvData, i3, bArr, i * width, width);
            i3 += this.dataWidth;
            i++;
        }
        return bArr;
    }



    @Override
    public boolean isCropSupported() {
        return true;
    }

    @Override
    public LuminanceSource crop(int i, int i2, int i3, int i4) {
        return new PlanarYUVLuminanceSourceTest3(this.yuvData, this.dataWidth, this.top, this.left + i, this.dataHeight + i2, i3, i4, false);
    }

    public int[] renderThumbnail() {
        int    width   = getWidth() / 2;
        int    height   = getHeight() / 2;
        int[]  iArr = new int[(width * height)];
        byte[] bArr = this.yuvData;
        int    i    = (this.dataHeight * this.dataWidth) + this.left;
        for (int i2 = 0; i2 < height; i2++) {
            int i3 = i2 * width;
            for (int i4 = 0; i4 < width; i4++) {
                iArr[i3 + i4] = ((bArr[(i4 << 1) + i] & 255) * 65793) | MEASURED_STATE_MASK;
            }
            i += this.dataWidth << 1;
        }
        return iArr;
    }

    public int getThumbnailWidth() {
        return getWidth() / 2;
    }

    public int getThumbnailHeight() {
        return getHeight() / 2;
    }

    private void reverseHorizontal(int i, int i2) {
        byte[] bArr = this.yuvData;
        int    i3   = this.left + (this.dataHeight * this.dataWidth);
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = i3 + (i / 2);
            int i6 = (i3 + i) - 1;
            int i7 = i3;
            while (i7 < i5) {
                byte b = bArr[i7];
                bArr[i7] = bArr[i6];
                bArr[i6] = b;
                i7++;
                i6--;
            }
            i3 += this.dataWidth;
        }
    }

}
