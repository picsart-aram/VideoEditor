package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import adapter.CustomGalleryAdapter;
import item.CustomGalleryItem;
import item.PicsArtGalleryItem;

/**
 * Created by Tigran Isajanyan on 5/11/15.
 */
public class FileUtils {

    /**
     * @param context
     * @param masterlistrev
     * @param FileName
     */
    public static void writeListToFile(Context context, ArrayList<PicsArtGalleryItem> masterlistrev, String FileName) {
        if (null == FileName)
            throw new RuntimeException("FileName is null!");

        File myfile = context.getFileStreamPath(FileName);
        try {
            if (myfile.exists() || myfile.createNewFile()) {
                FileOutputStream fos = context.openFileOutput(FileName, context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(masterlistrev);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param context
     * @param FileName
     * @return
     */
    public static int loadListFromFile(Context context, String FileName) {
        if (null == FileName)
            return 0;

        ArrayList<String> masterlistrev = new ArrayList<>();
        File myfile = context.getFileStreamPath(FileName);
        try {
            if (myfile.exists()) {
                FileInputStream fis = context.openFileInput(FileName);
                ObjectInputStream ois = new ObjectInputStream(fis);
                masterlistrev = (ArrayList<String>) ois.readObject();
                fis.close();
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return masterlistrev.size();
    }

    /**
     * @param image
     * @param context
     * @param fileName
     */
    public static void saveImageToInternalStorage(Bitmap image, Context context, String fileName) {

        File file = new File(context.getFilesDir() + "/req_images", fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param flname
     * @return
     */
    public static String loadImageFromStorage(Context context, String flname) {

        File f = null;
        try {
            f = new File(context.getFilesDir() + "/req_images", flname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f.toString();

    }

    /**
     * @param context
     * @param arrayList
     * @param fileName
     */
    public static void writeListToJson(Context context, ArrayList<PicsArtGalleryItem> arrayList, String fileName) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("imagePath", arrayList.get(i).getImagePath());
            json.put("width", arrayList.get(i).getWidth());
            json.put("height", arrayList.get(i).getHeight());
            jsonArray.add(i, json);
        }
        Gson gson = new Gson();
        String ss = gson.toJson(jsonArray);
        Log.d("My_Logs", "json string  " + jsonArray);
        Log.d("My_Logs", "starting writing");
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(ss.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("My_Logs", "done");
    }

    /**
     * @param context
     * @param picsArtGalleryItems
     * @param fileName
     */
    public static void readListFromJson(Context context, ArrayList<PicsArtGalleryItem> picsArtGalleryItems, String fileName) {

        FileInputStream fis = null;
        Log.d("My_Logs", "starting reading");
        try {
            fis = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = sb.toString();
        Gson gson1 = new Gson();
        JSONArray jsonArray1 = gson1.fromJson(json, JSONArray.class);

        for (int j = 0; j < jsonArray1.size(); j++) {
            org.json.JSONObject object = null;
            try {
                org.json.JSONArray array = new org.json.JSONArray(jsonArray1.toJSONString());
                object = (org.json.JSONObject) array.get(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem();
            try {
                picsArtGalleryItem.setImagePath(object.getString("imagePath"));
                picsArtGalleryItem.setWidth(object.getInt("width"));
                picsArtGalleryItem.setHeight(object.getInt("height"));
                picsArtGalleryItem.setIsLoaded(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            picsArtGalleryItems.add(picsArtGalleryItem);
        }
    }


    public static void clearDir(File dir) {
        try {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isDirectory())
                        clearDir(f);
                    f.delete();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void craeteDir(String fileName) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + fileName);
        if (!myDir.exists()) {
            myDir.mkdirs();
        } else {
            clearDir(myDir);
            File file = new File(myDir.toString());
            file.delete();
            myDir.mkdirs();
        }
    }

    public static void writeListToJson1(Context context, ArrayList<CustomGalleryItem> arrayList, String fileName) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("path", arrayList.get(i).getImagePath());
            json.put("w",arrayList.get(i).getWidth());
            json.put("h",arrayList.get(i).getHeight());
            jsonArray.add(i, json);
        }
        Gson gson = new Gson();
        String ss = gson.toJson(jsonArray);
        Log.d("My_Logs", "json string  " + jsonArray);
        Log.d("My_Logs", "starting writing");
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(ss.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("My_Logs", "done");
    }

    public static void readListFromJson1(Context context, ArrayList<CustomGalleryItem> customGalleryItems, String fileName, CustomGalleryAdapter customGalleryAdapter) {

        FileInputStream fis = null;
        Log.d("My_Logs", "starting reading");
        try {
            fis = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = sb.toString();
        Gson gson1 = new Gson();
        JSONArray jsonArray1 = gson1.fromJson(json, JSONArray.class);

        for (int j = 0; j < jsonArray1.size(); j++) {
            org.json.JSONObject object = null;
            try {
                org.json.JSONArray array = new org.json.JSONArray(jsonArray1.toJSONString());
                object = (org.json.JSONObject) array.get(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                customGalleryItems.add(new CustomGalleryItem(object.getString("path"),false,object.getInt("w"),object.getInt("h")));
                customGalleryAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
