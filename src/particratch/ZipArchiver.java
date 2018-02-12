package particratch;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**Zipアーカイバ*/
public final class ZipArchiver
{
    private byte[][] byteBufferImages;
    private byte[] byteBufferJson;
    private File outFile;

    private int progress;
    private int progress_old = 0;

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

            //画像をアーカイブ
            for(int i = 0; i < this.byteBufferImages.length; ++i)
            {
                zipOutputStream.putNextEntry(new ZipEntry(i + ".png"));
                zipOutputStream.write(this.byteBufferImages[i]);
                zipOutputStream.closeEntry();

                //進捗状況の表示
                this.refleshProgressBar(this.byteBufferImages.length, i + 1);
            }

            //Jsonをアーカイブ
            zipOutputStream.putNextEntry(new ZipEntry("sprite.json"));
            zipOutputStream.write(this.byteBufferJson);
            zipOutputStream.closeEntry();

            System.out.println("OK");
        }
        finally
        {
            try{zipOutputStream.close();}catch (Exception e){}
            try{bufferedOutputStream.close();}catch (Exception e){}
            try{fileOutputStream.close();}catch (Exception e){}
        }

    }

    /**プログレスバーの表示・更新*/
    private void refleshProgressBar(int var0, int var2)
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
