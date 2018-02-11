package particratch;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**Zipアーカイバ*/
public class ZipArchiver
{
    byte[][] byteBufferImages;
    byte[] byteBufferJson;
    File outFile;

    public ZipArchiver(byte[][] image, byte[] json, File o)
    {
        byteBufferImages = image;
        byteBufferJson = json;
        outFile = o;
    }

    /**アーカイブ処理*/
    public void arcive() throws IOException
    {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        ZipOutputStream zipOutputStream = null;

        try
        {
            fileOutputStream = new FileOutputStream(this.outFile);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            zipOutputStream = new ZipOutputStream(bufferedOutputStream);

            zipOutputStream.setLevel(0);

            int progress = 0;
            int progress_old = 0;

            //画像をアーカイブ
            for(int i = 0; i < this.byteBufferImages.length; ++i)
            {
                zipOutputStream.putNextEntry(new ZipEntry(i + ".png"));
                zipOutputStream.write(this.byteBufferImages[i]);
                zipOutputStream.closeEntry();

                //進捗状況の表示
                progress = ((100 / this.byteBufferImages.length) * (i + 1)) / 4;
                if(progress_old < progress)
                {
                    for(int j = 0; j < progress - progress_old; ++j)
                        System.out.print("#");

                    progress_old = progress;
                }
            }

            //Jsonをアーカイブ
            zipOutputStream.putNextEntry(new ZipEntry("sprite.json"));
            zipOutputStream.write(this.byteBufferJson);
            zipOutputStream.closeEntry();

            System.out.println("OK");
        }
        catch(IOException ioe)
        {
            throw ioe;
        }
        catch(Exception e)
        {
            throw e;
        }
        finally
        {
            try{zipOutputStream.close();}catch (Exception e){}
            try{bufferedOutputStream.close();}catch (Exception e){}
            try{fileOutputStream.close();}catch (Exception e){}
        }

    }
}
