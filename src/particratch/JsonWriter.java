package particratch;

import java.security.MessageDigest;

/**Jsowを書くクラス*/
public class JsonWriter
{
    private int frames;
    private int imageWidth;
    private int imageHeight;
    private double wait;
    private boolean optiloop;
    private String spriteName;
    private byte[][] byteBuffer;

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
                "" + showHideBlock[0] + " [\"doRepeat\",  "+ this.frames + ", [[\"lookLike:\", [\"readVariable\", \"ParticRatch: Anim\"]], [\"changeVar:by:\", \"ParticRatch: Anim\", 1]" + waitBlock + "]]" + showHideBlock[1] + "]]],");

        strbuf.append("\"currentCostumeIndex\":1,\"scratchX\":0,\"scratchY\":0,\"scale\":1,\"direction\":90,\"rotationStyle\":\"normal\",\"isDraggable\":false,\"indexInLibrary\":100000,\"visible\":true,\"spriteInfo\":{}," +
                "\"costumes\": [");


        //画像のMD5ハッシュを求める
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] tmpMD5hash;
        for(int i = 0; i < frames; ++i)
        {

            tmpMD5hash = digest.digest(byteBuffer[i]);

            StringBuffer MD5Str = new StringBuffer();
            for(int j = 0; j < tmpMD5hash.length; ++j)
            {
                String tmpStr = Integer.toHexString(tmpMD5hash[j] & 0xff);

                if(tmpStr.length() == 1)
                {
                    MD5Str.append("0");
                }
                MD5Str.append(tmpStr);
            }


            strbuf.append("{\"costumeName\": " + (i + 1) + "," +
                    "\"baseLayerID\": " + i + "," +
                    "\"baseLayerMD5\": \"" + MD5Str.toString() + ".png\"," +
                    "\"bitmapResolution\": 1," +
                    "\"rotationCenterX\": " + this.imageWidth / 2 + "," +
                    "\"rotationCenterY\": " + this.imageHeight / 2 + "}");

            if(i + 1 != frames)
            {
                strbuf.append(",");
            }
            else
            {
                strbuf.append("]}");
            }
        }

        return strbuf.toString().getBytes();
    }
}
