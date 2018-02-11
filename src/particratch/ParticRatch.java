package particratch;

import java.io.File;
import java.io.IOException;
/*
####    #   ####  ##### #####  ###  ####    #   #####  ###  #   #
#   #  # #  #   #   #     #   #   # #   #  # #    #   #   # #   #
####   # #  #####   #     #   #     #####  # #    #   #     #####
#     ##### #   #   #     #   #   # #   # #####   #   #   # #   #
#     #   # #   #   #   #####  ###  #   # #   #   #    ###  #   #
 */

/**メインクラス*/
public class ParticRatch
{
    public static final String PROJECT_NAME = "ParticRatch";
    public static final String PROJECT_VERSION = "0.0_beta";

    private static File inFile;
    private static File outFile;
    private static int splitX;
    private static int splitY;
    private static int frames;
    private static double wait;
    private static boolean optiloop;
    private static String spliteName;

    public static void main(String[] args)
    {
        // java -jar ParticRatch.jar Image.png 4 5 18 -w 1 -o Out.sb2 --optiloop
        // 画像のファイルパス 横分割数 縦分割数 総フレーム    -w ウェイト  -o 出力ファイル名  --optiloop (ループに最適化)

        //System.out.println(args.length);

        /*ﾍﾙﾌﾟ表示とか*/
        if(args.length == 1)
        {
            switch (args[0])
            {
                case "--version":
                    showVersion();
                    break;

                default:
                    showHelp();
            }
            return;
        }
        else if(args.length < 4)
        {
            showHelp();
            return;
        }


        try
        {
            /*コマンドライン引数の読み取り*/

            inFile = new File(args[0]);
            splitX = Integer.parseInt(args[1]);
            splitY = Integer.parseInt(args[2]);
            frames = Integer.parseInt(args[3]);

            outFile = new File("Out.sb2");
            spliteName = "Effect";
            wait = -1.0;
            optiloop = false;

            for(int i = 4; i < args.length; ++i)
            {
                switch (args[i])
                {
                    case "-w":

                        if(args.length >= ++i)
                            wait = Double.parseDouble(args[i]);
                        break;


                    case "-o":

                        if(args.length >= ++i)
                            outFile = new File(args[i]);
                        break;


                    case "-n":

                        if(args.length >= ++i)
                            spliteName = args[i];
                        break;


                    case "--optiloop":
                        
                        optiloop = true;
                        break;
                }
            }

            /*
            System.out.println(inFile.getPath());
            System.out.println(outFile.getPath());
            System.out.println(splitX);
            System.out.println(splitY);
            System.out.println(frames);
            System.out.println(wait);
            System.out.println(optiloop);
            */

            /*エラーチェック*/
            checkArgErr();

            ImageSpliter imageSpliter;
            JsonWriter jsonWriter;
            ZipArchiver zipArchiver;
            byte[][] byteBufferImages;
            byte[] byteBufferJson;

            imageSpliter = new ImageSpliter(inFile, splitX, splitY, frames);
            byteBufferImages = imageSpliter.split();

            jsonWriter = new JsonWriter(frames, wait, optiloop, spliteName, byteBufferImages, imageSpliter.getSplitedImageWidth(), imageSpliter.getSplitedImageHeight());
            byteBufferJson = jsonWriter.write();

            zipArchiver = new ZipArchiver(byteBufferImages, byteBufferJson, outFile);
            zipArchiver.arcive();

            System.out.println(outFile.getPath() + " へ出力が完了しました。");

            //画像出力てすと
            /*
            File testDir = new File("test/");
            if(testDir.exists())
            {
                File[] fs = testDir.listFiles();
                for(File f : fs)
                    f.delete();
            }
            else
            {
                testDir.mkdir();
            }

            for(int i = 0; i < byteBufferImages.length; ++i)
            {
                FileOutputStream stream = new FileOutputStream("test/" +i + ".png");
                stream.write(byteBufferImages[i]);
                stream.close();
            }

            //Json出力てすと
            FileOutputStream stream = new FileOutputStream("test/sprite.json");
            stream.write(byteBufferJson);
            stream.close();
            */
        }
        catch(NumberFormatException nfe)
        {
            nfe.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**コマンドライン引数のエラーチェック*/
    private static void checkArgErr() throws IOException,Exception
    {
        if(splitX * splitY < frames)
            throw new Exception( frames + "が" + splitX + " * " + splitY + "より大きくなることはありません。");

        if(!inFile.exists())
            throw new IOException(inFile.getPath() + "は存在しません。");

        String ex[] = inFile.getName().split("\\.");
        //System.out.println("length" + ex.length);
        if(!ex[ex.length - 1].toLowerCase().equals("png") && !ex[ex.length - 1].toLowerCase().equals("bmp"))
            throw new IOException("サポートされていないファイル形式です。 :" + ex[ex.length - 1]);

    }

    /**ヘルプ表示*/
    private static void showHelp()
    {
        String help =
                "使用法\njava -jar ParticRatch.jar [画像ﾌｧｲﾙのﾊﾟｽ] [横の分割数] [縦の分割数] [総フレーム数]\n\n" +
                "その他オプション\n-w [1ﾌﾚｰﾑ当たりのｳｪｲﾄ]\t\t1ﾌﾚｰﾑごとに待ち時間を入れます。\n" +
                "-n [ｽﾌﾟﾗｲﾄ名]\t\t\t\tｽﾌﾟﾗｲﾄ名を指定できます。\n" +
                "-o [出力ﾌｧｲﾙ名]\t\t\t\t出力ﾌｧｲﾙ名を指定できます。\n" +
                "--optiloop\t\t\t\t\tｴﾌｪｸﾄのﾙｰﾌﾟ再生時のちらつきを無くします。\n" +
                "--help \t\t\t\t\t\tﾍﾙﾌﾟを表示します。\n" +
                "--version \t\t\t\t\tﾊﾞｰｼﾞｮﾝを表示します。";

        System.out.println(help);
    }

    /**バージョン表示*/
    private static void showVersion()
    {
        System.out.println(PROJECT_NAME + " " + PROJECT_VERSION);
    }
}
