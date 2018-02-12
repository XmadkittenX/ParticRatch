package particratch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**画像をぶった切る専門*/
public final class ImageSpliter
{
    private BufferedImage inImage;
    private int splitX;
    private int splitY;
    private int frames;
    private int width;
    private int height;

    private int progress = 0;
    private int progress_old = 0;

    public ImageSpliter(File infile, int sx, int sy, int f) throws IOException, Exception
    {
        this.inImage = ImageIO.read(infile);
        this.splitX = sx;
        this.splitY = sy;
        this.frames = f;
        this.width = inImage.getWidth();
        this.height = inImage.getHeight();

        if(this.width % this.splitX != 0 || this.height % this.splitY != 0)//割り切れんのはどう見てもおかしい
        {
            throw new Exception(this.width + " * " + this.height + " の画像を " + this.splitX + " * " + this.splitY + " で分割することは出来ません。");
        }
    }

    /**画像の分割処理*/
    public byte[][] split() throws IOException
    {
        byte[][] byteBufferImages = new byte[this.frames][];
        ByteArrayOutputStream byteArrayOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        int oImageWidth = this.width / this.splitX;
        int oImageHeight = this.height / this.splitY;

        try
        {
            int f = 0;
            for (int y = 0; y < this.splitY; ++y)
            {
                for (int x = 0; x < this.splitX; ++x)
                {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);

                    ImageIO.write(inImage.getSubimage(oImageWidth * x, oImageHeight * y, oImageWidth, oImageHeight), "png", bufferedOutputStream);
                    byteBufferImages[f++] = byteArrayOutputStream.toByteArray();

                    bufferedOutputStream.close();
                    byteArrayOutputStream.close();

                    //進捗状況の表示
                    this.refleshProgressBar(frames, f);

                    if (f >= this.frames)
                        break;
                }

                if (f >= this.frames)
                    break;
            }

            System.out.println("OK");
        }
        finally
        {
            try{byteArrayOutputStream.close();}catch(Exception e){}
            try{bufferedOutputStream.close();}catch(Exception e){}
        }

        return byteBufferImages;
    }

    public int getSplitedImageWidth()
    {
        return this.width / this.splitX;
    }

    public int getSplitedImageHeight()
    {
        return this.height / this.splitY;
    }

    /**プログレスバーの表示・更新*/
    private void refleshProgressBar(int var0, int var2)//スーパークラスを作ってそこに置いとく方がいいかも
    {
        this.progress = ((100 / var0) * var2) / 4;
        if(this.progress_old < this.progress)
        {
            for(int i = 0; i < this.progress - this.progress_old; ++i)
                System.out.print("#");

            this.progress_old = this.progress;
        }
    }
}
