package com.client.therevgo.library;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.client.therevgo.utility.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Stack;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getName() ;
    // the simplest in-memory cache implementation. This should be replaced with
    // something like SoftReference or BitmapOptions.inPurgeable(since 1.6)

    private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

    private File cacheDir = null;
    private Bitmap useThisBitmap = null;

    private PhotosQueue photosQueue = new PhotosQueue();

    @SuppressWarnings("unused")
    private Context ctx = null;

    public ImageLoader(Context context)
    {
        // Make the background thread low priority. This way it will not affect
        // the UI performance

        ctx = context;
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

        // Find the dir to save cached images
        /*if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"DownloadImages/AlbumArt/");
        else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();*/

        File root = android.os.Environment.getExternalStorageDirectory();
        cacheDir = new File(root.getAbsolutePath() + "/path");
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public void displayImage(String url, ImageView imageView) {
        url = url.replace(" ", "%20");
        if(!url.equals("")) {
            if (cache.containsKey(url)) {
                imageView.setImageBitmap(cache.get(url));
            } else {
                queuePhoto(url, imageView);
            }
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        // This ImageView may be used for other images before. So there may be
        // some old tasks in the queue. We need to discard them.

        photosQueue.Clean(imageView);
        PhotoToLoad p = new PhotoToLoad(url, imageView);

        synchronized (photosQueue.photosToLoad)
        {
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }

        // start thread if it's not started yet
        if (photoLoaderThread.getState() == Thread.State.NEW)
            photoLoaderThread.start();
    }

    public Bitmap getBitmap(String url) {
        try {
            // I identify images by hashcode. Not a perfect solution, good for the
            // demo.
            String filename = URLEncoder.encode(url, "utf-8"); //String.valueOf(url.hashCode());
            File f = new File(cacheDir, filename);
            //f.createNewFile();

            // from SD cache
            Bitmap b = decodeFile(f);
            if (b != null)
                return b;

            // from web
            try {
                Bitmap bitmap = null;
                if(!url.equals("")){
                    Log.i(TAG, url);
                    InputStream is = new URL(url).openStream();
                    OutputStream os = new FileOutputStream(f);
                    Utils.CopyStream(is, os);
                    os.close();
                    bitmap = decodeFile(f);
                }
                return bitmap;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * decodes image and scales it to reduce memory consumption
     * @param f file path
     * @return bitmap
     * */
    private Bitmap decodeFile(File f){
        Bitmap b = null;
        try {
            useThisBitmap = null;
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            final int IMAGE_MAX_SIZE = 70;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int scale = 2;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = 2 ^ (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();

            o2.inSampleSize = scale;
            b = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            useThisBitmap = b;

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return useThisBitmap;
    }

    // Task for the queue
    private class PhotoToLoad {
        String url;
        ImageView imageView;

        PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    // stores list of photos to download
    private class PhotosQueue {

        private final Stack<PhotoToLoad> photosToLoad = new Stack<>();

        // removes all instances of this ImageView
        private void Clean(ImageView image) {
            for (int j = 0; j < photosToLoad.size();) {
                if (photosToLoad.get(j).imageView == image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }

    private class PhotosLoader extends Thread {
        public void run() {
            try {
                while (true) {
                    // thread waits until there are any images to load in the
                    // queue
                    if (photosQueue.photosToLoad.size() == 0)
                        synchronized (photosQueue.photosToLoad) {
                            photosQueue.photosToLoad.wait();
                        }
                    if (photosQueue.photosToLoad.size() != 0) {
                        PhotoToLoad photoToLoad;
                        synchronized (photosQueue.photosToLoad) {
                            photoToLoad = photosQueue.photosToLoad.pop();
                        }

                        Bitmap bmp = getBitmap(photoToLoad.url);
                        cache.put(photoToLoad.url, bmp);

                        if (photoToLoad.imageView.getTag().equals(photoToLoad.url)) {
                            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView);
                            Activity a;// = (Activity) photoToLoad.imageView.getContext();
                            if (photoToLoad.imageView.getContext() instanceof Activity) {
                                a = (Activity) photoToLoad.imageView.getContext();
                            } else {
                                a = (Activity) ((ContextWrapper)photoToLoad.imageView.getContext()).getBaseContext();
                            }
                            a.runOnUiThread(bd);
                        }
                    }
                    if (Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                // allow thread to exit
            }
        }
    }

    private PhotosLoader photoLoaderThread = new PhotosLoader();

    // Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        private Bitmap bitmap;
        private ImageView imageView;

        private BitmapDisplayer(Bitmap b, ImageView i) {
            bitmap = b;
            imageView = i;
        }

        public void run() {
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    public void stopThread() {
        photoLoaderThread.interrupt();
    }

    public void clearCache() {
        cache.clear();
        File[] files = cacheDir.listFiles();
        for (File f : files)
            f.delete();
    }
}