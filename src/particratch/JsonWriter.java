package particratch;

import java.security.MessageDigest;

/**Jsowを書くクラス*/
public final class JsonWriter
{
    private int frames;
    private int imageWidth;
    private int imageHeight;
    private double wait;
    private boolean optiloop;
    private String spriteName;
    private byte[][] byteBuffer;

    private int progress;
    private int progress_old = 0;

    public JsonWriter(int f, double w, boolean o, String n, byte[][] bytes, int width, int height)
    {
        this.frames = f;
        this.imageWidth = width;
        this.imageHeight = height;
        this.wait = w;
        this.optiloop = o;
        this.spriteName = n;
        this.byteBuffer = bytes;
    }

    /**jsonを書く*/
    public byte[] write() throws Exception
    {
        StringBuffer strbuf = new StringBuffer();
        String[] showHideBlock;
        String waitBlock;

        /*表示する/隠すブロック*/
        if(!optiloop)
        {
            showHideBlock = new String[]
                    {
                            "[\"show\"],",
                            ",[\"hide\"]"
                    };
        }
        else
        {
            showHideBlock = new String[]
                    {
                            "",
                            ""
                    };
        }

        if(wait >= 0.0)
        {
            waitBlock = ", [\"wait:elapsed:from:\", " + this.wait + "]";
        }
        else
        {
            waitBlock = "";
        }

        //Notepad++で調整したもの
        strbuf.append("{ \"objName\": \"" + this.spriteName + "\", \"variables\": [{ \"name\": \"ParticRatch: Anim\", \"value\": 3, \"isPersistent\": false }]," +
                " \"scripts\": [[10, 10, [[\"procDef\", \"ParticRatch: PlayEffect: X: %n Y: %n Size: %n rotation %n\", [\"x\", \"y\", \"size\", \"rotation\"], [0, 0, 100, 90], false], " +
                "[\"setVar:to:\", \"ParticRatch: Anim\", \"1\"], [\"gotoX:y:\", [\"getParam\", \"x\", \"r\"], [\"getParam\", \"y\", \"r\"]], [\"heading:\", [\"getParam\", \"rotation\", \"r\"]], [\"setSizeTo:\", [\"getParam\", \"size\", \"r\"]], " +
                "" + showHideBlock[0] + " [\"doRepeat\",  "+ this.frames + ", [[\"lookLike:\", [\"readVariable\", \"ParticRatch: Anim\"]], [\"changeVar:by:\", \"ParticRatch: Anim\", 1]" + waitBlock + "]]" + showHideBlock[1] + "]]]," +
                "\"currentCostumeIndex\":1,\"scratchX\":0,\"scratchY\":0,\"scale\":1,\"direction\":90,\"rotationStyle\":\"normal\",\"isDraggable\":false,\"indexInLibrary\":100000,\"visible\":true,\"spriteInfo\":{}," +
                "\"costumes\": [");

        //costumeの情報を書き出す
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] tmpMD5hash;
        String tmpStr;
        StringBuffer MD5Str;

        for(int i = 0; i < frames; ++i)
        {
            //画像のMD5ハッシュを求める
            tmpMD5hash = digest.digest(byteBuffer[i]);

            MD5Str = new StringBuffer();
            for(int j = 0; j < tmpMD5hash.length; ++j)
            {
                tmpStr = Integer.toHexString(tmpMD5hash[j] & 0xff);

                if(tmpStr.length() == 1)
                    MD5Str.append("0");

                MD5Str.append(tmpStr);
            }

            strbuf.append("{\"costumeName\": " + (i + 1) + "," +
                    "\"baseLayerID\": " + i + "," +
                    "\"baseLayerMD5\": \"" + MD5Str.toString() + ".png\"," +
                    "\"bitmapResolution\": 1," +
                    "\"rotationCenterX\": " + this.imageWidth / 2 + "," +
                    "\"rotationCenterY\": " + this.imageHeight / 2 + "},");

            //最後の括弧閉じ
            if(i + 1 == frames)
            {
                strbuf.deleteCharAt(strbuf.length() - 1);
                strbuf.append("]}");
            }

            //進捗状況の表示
            this.refleshProgressBar(this.frames, i + 1);
        }
        System.out.println("OK");

        return strbuf.toString().getBytes();
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
