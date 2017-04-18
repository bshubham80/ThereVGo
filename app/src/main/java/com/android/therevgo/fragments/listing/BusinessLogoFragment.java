package com.android.therevgo.fragments.listing;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.therevgo.R;
import com.android.therevgo.activities.ContainerActivity;
import com.android.therevgo.adapters.DialogAdapter;
import com.android.therevgo.dto.BusinessImageModel;
import com.android.therevgo.dto.DialogBean;
import com.android.therevgo.library.ImageLoader;
import com.android.therevgo.networks.HttpConnection;
import com.android.therevgo.networks.ResponseListener;
import com.android.therevgo.utility.PrefManager;
import com.android.therevgo.utility.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessLogoFragment extends Fragment implements ResponseListener {

    public static final String TAG = BusinessLogoFragment.class.getName();

    static final int REQUEST_CAMERA = 0;
    static final int REQUEST_GALLERY = 1;

    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_IMAGE_GALLERY = 1;

    public static final String URL_SUFFIX = "http://www.therevgo.in/product_img/";

    private Button btn_upload;
    private Button btn_submit, btn_update;
    private ImageView logo;

    private Context context ;

    public int con_id, image_id = -1 ;
    private String user_id ;
    
    private ContainerActivity containerActivity ;

    private Resources res ;

    private Uri mCurrentPhotoUri, selectedImageUri;
    private String selectedImagePath, mCurrentPhotoPath ;

    private ProgressDialog dialog;

    private File uploadableFile;

    private Utils utils = Utils.getInstance();

    private ImageLoader imageLoader;

    private Handler handler = new Handler();

    private boolean fetchingData, insertingData, updatingData;

    public BusinessLogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_business_logo, container, false);

        context = getActivity();

        containerActivity = (ContainerActivity) context;
        containerActivity.setTitle("Business Logo");

        res = context.getResources();

        imageLoader = new ImageLoader(context);

        user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);

        logo = (ImageView) view.findViewById(R.id.logo);
        btn_upload = (Button) view.findViewById(R.id.btn_upload);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_update = (Button) view.findViewById(R.id.btn_update);

        btn_submit.setOnClickListener(new MyClickListener());
        btn_update.setOnClickListener(new MyClickListener());
        btn_upload.setOnClickListener(new MyClickListener());

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);


        Bundle bundle = getArguments();
        if (bundle != null){
            con_id = bundle.getInt(BusinessContactInfoFragment.CONTACT_ID);
            String url = "http://tapi.therevgo.in/api/BusinessImgListing/GetBusImgSel?" +
                    "userid=" +
                    user_id+
                    "&con_id="+con_id;

            fetchingData = true ;
            dialog.show();
            HttpConnection.RequestGet(url, this);
        }
        return view ;
    }

    @Override
    public void onResponse(int Statuscode, JSONObject jsonObject) {
        Gson gson = new Gson();
        final BusinessImageModel resObj = gson.fromJson(jsonObject.toString(), BusinessImageModel.class);

        if(fetchingData) {
            fetchingData = false;

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (resObj.error == null) {
                        dialog.dismiss();

                        if (resObj.Data != null && resObj.Data.size() > 0) {
                            dialog.dismiss();
                            image_id = resObj.Data.get(0).id;
                            String image_path = URL_SUFFIX+resObj.Data.get(0).image_name;
                            btn_update.setVisibility(View.VISIBLE);
                            btn_submit.setText("SKIP");
                            if (imageLoader.getBitmap(image_path) == null) {
                                loadLogo(image_path);
                            } else {
                                logo.setImageBitmap(imageLoader.getBitmap(image_path));
                            }
                        }
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }

        if(insertingData) {
            insertingData = false;

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (resObj.error == null) {
                        dialog.dismiss();

                        if (resObj.Data != null && resObj.Data.size() > 0) {
                            dialog.dismiss();

                            String image_path = URL_SUFFIX+resObj.Data.get(0).image_name;
                            loadLogo(image_path);
                        }
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }

        if(updatingData) {
            updatingData = false;

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (resObj.error == null) {
                        dialog.dismiss();

                        if (resObj.Data != null && resObj.Data.size() > 0) {
                            dialog.dismiss();

                            String image_path = URL_SUFFIX+resObj.Data.get(0).image_name;
                            loadLogo(image_path);
                        }
                    } else {
                        onError(resObj.error);
                    }
                }
            });
        }
    }

    @Override
    public void onError(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLogo(String imageURL){
        Glide.with(context).load(imageURL).into(logo);
    }

    public void intentForCapturingImage() {
        Activity activity = (Activity) context;

        final Resources res = activity.getResources();
        final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);

        ListView lv = new ListView(activity);
        lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ArrayList<DialogBean> models = new ArrayList<>();
        models.add(new DialogBean(res.getString(R.string.camera),0));
        models.add(new DialogBean(res.getString(R.string.gallery),0));

        DialogAdapter adapter = new DialogAdapter(activity, R.layout.dialog_row_layout, models);
        lv.setAdapter(adapter);

        dlgAlert.setView(lv);
        dlgAlert.setCancelable(true);
        final AlertDialog dialog = dlgAlert.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();

                switch (position) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (context.checkSelfPermission(Manifest.permission.CAMERA) !=
                                                              PackageManager.PERMISSION_GRANTED ) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}
                                                  ,REQUEST_CAMERA);
                            } else {
                                startCameraOperation();
                            }
                        } else {
                            startCameraOperation();
                        }
                        break;

                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                               != PackageManager.PERMISSION_GRANTED ) {
                                requestPermissions(new String[]
                                     {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_GALLERY);
                            } else {
                                startGalleryOperation();
                            }
                        } else {
                            startGalleryOperation();
                        }
                        break;
                }

            }
        });
    }

    private void startCameraOperation(){
        try {
            File photoFile = createImageFile(getActivity());
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = photoFile.getAbsolutePath();

            if (Build.VERSION.SDK_INT >= 24) {
                mCurrentPhotoUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".provider", photoFile);
            } else {
                mCurrentPhotoUri = Uri.fromFile(photoFile);
            }

            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
            getActivity().startActivityFromFragment(this, captureIntent, REQUEST_IMAGE_CAPTURE);
        }catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
            Log.e(TAG, "Exception: " + ex.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startGalleryOperation(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);

        getActivity().startActivityFromFragment(this,photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (Build.VERSION.SDK_INT >= 24) {
                        selectedImageUri = Uri.parse(mCurrentPhotoPath);
                    } else {
                        selectedImageUri = mCurrentPhotoUri;
                    }
                    selectedImagePath = getRealPathFromURI(getActivity(), selectedImageUri);
                    break;

                case REQUEST_IMAGE_GALLERY:
                    selectedImageUri = data == null ? null : data.getData();
                    selectedImagePath = getImagePathFromUri(getActivity(), selectedImageUri);
                    break;
            }

            if (selectedImagePath != null)
                new ImageCompressionAsyncTask(false).execute(selectedImagePath);
            else {
                utils.displayAlert(context,res.getString(R.string.error), getString(R.string.not_valid_photo));
            }
        }
    }


    private File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
    }

    // TODO: Evaluate whether this method can be replaced by getImagePathFromUri
    public String getRealPathFromURI(Activity activity, Uri imageUri) {
        Cursor cursor = activity.getContentResolver().query(imageUri, null, null, null, null);
        if (cursor == null) {
            return imageUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
    }

    public String getImagePathFromUri(Activity activity, Uri imageUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        //Cursor cursor = activity.getContentResolver().query(imageUri, filePathColumn, null, null, null);
        Cursor imageCursor = null;
        String str[] = imageUri.getLastPathSegment().split(":");
        if (str.length >= 2) {
            imageCursor = activity.getContentResolver()
                    .query(getUri(), filePathColumn, MediaStore.Images.Media._ID + "=" + str[str.length - 1], null, null);
        } else {
            imageCursor = activity.getContentResolver().query(imageUri, filePathColumn, null, null, null);
        }
        //cursor.moveToFirst();
        String picturePath = null;

        if(imageCursor != null ) {
            if (imageCursor.moveToFirst()) {
                int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
                picturePath = imageCursor.getString(columnIndex);
            }
            imageCursor.close();
        } else{
            picturePath = getRealPathFromURI(activity,imageUri);
        }

        return picturePath;
    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    private boolean isImageFile(Uri selectedImageUri) {
        String type = getMimeType(selectedImageUri);

        if (type != null && !type.isEmpty())
            return type.split("/")[0].equalsIgnoreCase("image");

        return false;
    }

    private boolean isDocumentFile(Uri selectedImageUri) {
        String type = getMimeType(selectedImageUri);

        String[] ACCEPT_MIME_TYPES = {
                "application/msword",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
                "application/vnd.openxmlformats-officedocument.presentationml.template",
                "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.presentationml.slide",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
                "application/vnd.ms-excel.addin.macroEnabled.12",
                "application/vnd.ms-excel.sheet.binary.macroEnabled.12",
                "application/pdf"
        };

        if (type != null && !type.isEmpty())
            return Arrays.asList(ACCEPT_MIME_TYPES).contains(type);
        //return type.split("/")[0].equalsIgnoreCase("application");

        return false;
    }

   private String getMimeType(Uri selectedImageUri) {
        String type ;
        String extension = null;

        if (selectedImageUri != null)
            extension = MimeTypeMap.getFileExtensionFromUrl(selectedImageUri.toString());

        if (extension != null && !extension.equals(""))
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        else {
            ContentResolver resolver = context.getContentResolver();
            type = resolver.getType(selectedImageUri);
        }

        return type;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

   public static void setPic(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        // Determine how much to scale down the image
        int scaleFactor = 0;
        if (targetW != 0 && targetH != 0)
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
   }

   private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_submit:
                    Button btn = (Button) v;
                    if(btn.getText().toString().equals("SKIP")) {

                        Fragment fragment = new BusinessDealFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(BusinessContactInfoFragment.CONTACT_ID, con_id);

                        fragment.setArguments(bundle);
                        containerActivity.attachFragment(fragment,BusinessDealFragment.TAG);
                    } else {
                        insertingData = true ;

                        dialog.show();

                        String convertFile = uploadableFile.getAbsolutePath();
                        ContentBody cbFile = new FileBody(uploadableFile);

                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        entityBuilder.addPart("image", cbFile);
                        entityBuilder.addTextBody("userid",user_id);
                        entityBuilder.addTextBody("con_id", String.valueOf(con_id));

                        String url = "http://tapi.therevgo.in/api/BusinessImgListing/BUSImgINS";

                        HttpConnection.uploadFile(url, entityBuilder, BusinessLogoFragment.this);
                        //  insertValues();

                    }
                break;

                case R.id.btn_update:
                    updatingData = true ;

                    dialog.show();

                    String convertFile = uploadableFile.getAbsolutePath();
                    ContentBody cbFile = new FileBody(uploadableFile);

                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.addPart("image", cbFile);
                    entityBuilder.addTextBody("userid",user_id);
                    entityBuilder.addTextBody("con_id", String.valueOf(con_id));

                    String url ;
                    if(image_id != -1) {
                        entityBuilder.addTextBody("id", String.valueOf(image_id));
                        url = "http://tapi.therevgo.in/api/BusinessImgListing/BUSImgUPD";
                        HttpConnection.uploadFile(url, entityBuilder, BusinessLogoFragment.this);
                    }
                break;

                case R.id.btn_upload:
                    intentForCapturingImage();
                break;
            }
        }
   }

   private class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        private boolean fromGallery;
        private ProgressDialog pd;

        ImageCompressionAsyncTask(boolean fromGallery) {
            this.fromGallery = fromGallery;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "", res.getString(R.string.please_wait), true);
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath;

            File file = new File(selectedImagePath);

            if (isImageFile(Uri.fromFile(file))) {
                filePath = compressImage(params[0]);
            } else if (isDocumentFile(Uri.fromFile(file))) {
                filePath = params[0];
            } else {
                filePath = null;
            }

            return filePath;
        }

        public String compressImage(String filePath) {
            Bitmap scaledBitmap = null;

            if (filePath == null) {
                return null;
            }
            if (filePath.startsWith("http")) {
                return null;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            if (actualHeight == 0 && actualWidth == 0) {
                return null;
            }

            float maxHeight = 1200.0f;
            float maxWidth = 1200.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                scaledBitmap = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                if (scaledBitmap != null) {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return filename;

        }

        String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "RenewBuy/Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null) {
                utils.displayAlert(context,res.getString(R.string.error), getString(R.string.not_valid_photo));
                pd.dismiss();
                return;
            }
            uploadableFile = new File(selectedImagePath);
            Uri fileUri = Uri.fromFile(uploadableFile);

            setPic(logo, selectedImagePath);
            pd.dismiss();
        }
   }

}
