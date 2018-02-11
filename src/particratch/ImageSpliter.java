package particratch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**画像をぶった切る専門*/
public class ImageSpliter
{
    private BufferedImage inImage;
    private int splitX;
    private int splitY;
    private int frames;
    private int width;
    private int height;


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

    /**staticで定義した方がいい気がするが動けばいいので気にしないこと*/
    public byte[][] split() throws IOException
    {
        byte[][] byteBuffer = new byte[this.frames][];
        ByteArrayOutputStream byteArrayOutputStream = null;
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
                    ImageIO.write(inImage.getSubimage(oImageWidth * x, oImageHeight * y, oImageWidth, oImageHeight), "png", byteArrayOutputStream);
                    byteBuffer[f++] = byteArrayOutputStream.toByteArray();

                    byteArrayOutputStream.close();

                    if (f >= this.frames)
                        break;
                }

                if (f >= this.frames)
                    break;
            }
        }
        catch(IOException ioe)
        {
            throw ioe;
        }
        finally
        {
            try{byteArrayOutputStream.close();}catch(Exception e){}
        }

        return byteBuffer;
    }

    public int getSplitedImageWidth()
    {
        return this.width / this.splitX;
    }

    public int getSplitedImageHeight()
    {
        return this.height / this.splitY;
    }
}
