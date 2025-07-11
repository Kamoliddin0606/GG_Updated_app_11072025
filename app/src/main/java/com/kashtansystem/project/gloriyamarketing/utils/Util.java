package com.kashtansystem.project.gloriyamarketing.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by FlameKaf on 17.05.2017.
 * ----------------------------------
 */

public class Util
{
    public static boolean IntToBool(int a)
    {
        return (a != 0);
    }

    public static int BoolToInt(boolean a)
    {
        return (a ? 1 : 0);
    }

    /**
     * @return Цену, разделённую пробелом, например: 1 000, 10 000... (dalbayeb decimal format zacem togda?)
     * */
    @SuppressLint("DefaultLocale")
    public static String getParsedPrice(double price)
    {
//
//        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.getDefault()));
//        df.setMaximumFractionDigits(2);
//
//        final String in = df.format(price);
//
//        String number = in, end = "";
//        if (in.contains(","))
//        {
//            number = in.substring(0, in.indexOf(","));
//            end = in.substring(in.indexOf(","), in.length());
//        }
//
//        String result = "";
//        int counter = 0;
//        for (int i = number.length() - 1; i >= 0; i--)
//        {
//            if (counter != 3)
//                result = String.format("%s%s", number.substring(i, i+1), result);
//            else
//            {
//                counter = 0;
//                result = String.format("%s %s", number.substring(i, i+1), result);
//            }
//            counter++;
//        }
//
//        return result + end;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat =new  DecimalFormat("#,###.##", symbols);
        return decimalFormat.format(price);


    }

    public static String getDoubleToString(double in)
    {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        df.setMaximumFractionDigits(2);
        return df.format(in);
    }

    /**
     * Удаляет с даты, полученной с сервера, лишние символы
     * Т.к. "-", "Т", ":"
     * @return Дату ввиде yyyMMddHHmmss
     * */
    public static String replaceSymbols(String date)
    {
        return date.replace("-", "").replace("T", "").replace(":", "");
    }

    /**
     * @return Преобразованную дату, формата "2017-07-27T10:08:21", в "dd.MM.yyyy"
     * */
    public static String convertToNormalViewDate(String date)
    {
        final String in = date.substring(0, date.indexOf("T"));
        int ind = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < in.length(); i++)
        {
            if (in.substring(i, (i < in.length() ? i + 1 : i)).equals("-"))
            {
                result.insert(0, in.substring(ind, i)).insert(0,".");
                ind = i + 1;
            }
        }
        result.insert(0, in.substring(ind, in.length()));
        return result.toString();
    }

    /**
     * форматирует дату с yyyyMMddHHmmss в dd.MM.yyyy
     * */
    public static String parseStringDate1(String date) throws ParseException
    {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
            new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).parse(date));
    }

    /**
     * форматирует дату с yyyy-MM-ddTHH:mm:ss в dd.MM.yyyy
     * */
    public static String parseStringDate2(String date) throws ParseException
    {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date.replace("T", " ")));
    }

    /**
     * @param desc полный путь до файла
     * @return Сжатый bitmap
     * */
    public static Bitmap getDecodedFile(String desc)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(desc, bmOptions);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 8;
        //noinspection deprecation
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(desc, bmOptions);
    }

    /**
     * @return Картинку, сконвертированную в Base64
     * */
    private static String getImgBase64(String link)
    {
        if (!link.isEmpty())
        {
            Bitmap bitmap = BitmapFactory.decodeFile(link);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return "";
    }
}