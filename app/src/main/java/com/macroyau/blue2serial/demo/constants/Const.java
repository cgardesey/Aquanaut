package com.macroyau.blue2serial.demo.constants;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.macroyau.blue2serial.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

final public class Const {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static final int EMAIL_ALREADY_EXISTS = 0;
    public static final int EMAIL_NOT_VERIFIED = -1;
    public static final int INCORRECT_PASSWORD = 2;
    public static final int SUCCESSFUL = 1;
    public static final int USER_NOT_FOUND = 0;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static String INSTRUCTOR_API_BASE_URL = "https://2schooldirect.net/uv_instructor_api/";
    public static String BASE_URL = "https://aquanaut-watm.herokuapp.com/";
    public static String API_URL = BASE_URL;
    public static String GUID_WS_URL = "ws://2schooldirect.net:55554/adonis-ws";
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static String APP_HASH = "MeaESTipMwxlywdYgQ9v+lJlq8324=";
    public static String[] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat preciseDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static Toast toast;
    public static byte[] key = new byte[]{-72, 105, -16, 84, 8, 58, 95, -99, -8, -116, -48, -31, 77, 72, -16, -110};

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState(); 
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void showToast(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String fileSize(long length) {
        String file_size;
        if (length < 1000L) {
            file_size = length + " B";
        } else if (length >= 1000L && length < 1000000L) {
            file_size = length / 1000L + " KB";
        } else {
            file_size = length / 1000000L + " MB";
        }
        return file_size;
    }

    public static void myVolleyError(Context context, VolleyError error) {
        Log.d("My VolleyError", error.toString());
        if (error instanceof NoConnectionError) {
            //This indicates that the reuest has either time out or there is no connection
            showToast(context, context.getString(R.string.connection_error));

        } else if (error instanceof TimeoutError) {
            // Error indicating that there was an Authentication Failure while performing the request
            showToast(context, context.getString(R.string.timeout_error));

        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            // showToast(context, context.getString(R.string.authentication_failure));

        } else if (error instanceof ServerError) {
            StackTraceElement[] stackTrace = error.getStackTrace();
            String message = error.getMessage();
            //Indicates that the server responded with a error response
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = null;
                    if (res.contains("tag with this tag uid already exists.")) {
                        Toast.makeText(context, "Tag with this tag uid already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        obj = new JSONObject(res);
                        if (obj.has("message")) {
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
            else {
                showToast(context, "Server error");
            }
//            showToast(context, context.getString(R.string.server_error));
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            showToast(context, context.getString(R.string.network_error));
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static final SimpleDateFormat sfd_date = new SimpleDateFormat("MMMM d, yyyy");

    public static String getFormattedDate(Context context, long timeInMillis) {
        String formattedDate = "";
        Calendar cal = Calendar.getInstance();
        long timeNowInMillis = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -1);
        long timeYestInMillis = cal.getTimeInMillis();
        SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat sfd_month = new SimpleDateFormat("MMMM d");
        if (sfd_date.format(new java.util.Date(timeInMillis)).equals(sfd_date.format(new java.util.Date(timeNowInMillis)))) {
            formattedDate = context.getString(R.string.today);
        } else if (sfd_date.format(new java.util.Date(timeInMillis)).equals(sfd_date.format(new java.util.Date(timeYestInMillis)))) {
            formattedDate = context.getString(R.string.yesterday);
        } else if (sfd_year.format(new java.util.Date(timeInMillis)).equals(sfd_year.format(new java.util.Date(timeNowInMillis)))) {
            formattedDate = sfd_month.format(new java.util.Date(timeInMillis));
        } else {
            formattedDate = sfd_date.format(new java.util.Date(timeInMillis));
        }
        return formattedDate;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
    public static List<String> getPackagesOfDialerApps(Context context) {

        List<String> packageNames = new ArrayList<>();

        // Declare action which target application listen to initiate phone call
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        // Query for all those applications
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        // Read package name of all those applications
        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            packageNames.add(activityInfo.applicationInfo.packageName);
        }

        return packageNames;
    }

    public static void clearAppData(Context context) {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = context.getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean isValidMtnno(String target) {
        if (target.length() != 10) {
            return false;
        } else if (!(target.startsWith("024") || target.startsWith("054") || target.startsWith("055") || target.startsWith("059"))) {
            return false;
        }
        return true;
    }

    public static boolean isValidPhonenumber(String target) {
        if (target.length() < 10) {
            return false;
        }
        return true;
    }

    public static byte[] generateKey(String password) throws Exception {
        // password = "SKT@Ghana!!1";
        return new byte[]{-72, 105, -16, 84, 8, 58, 95, -99, -8, -116, -48, -31, 77, 72, -16, -110};

        /*byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();*/
    }

    public static void encodeFile(String filePath) throws Exception {
        // Convert file path to file
        File file = new File(filePath);

        // Convert file to byte array
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }

        // encode file
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(fileData);

        // Save file to external storage
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

        bos.write(encrypted);
        bos.flush();
        bos.close();

    }

    public static void decodeFile(String filePath, String internalFilePath) throws Exception {
        // Convert file path to file
        File file = new File(filePath);
        // Convert file to byte array
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        // Decode file
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        // Save file to internal storage
        BufferedOutputStream bos;

        File internalFile = new File(internalFilePath);
        if (!internalFile.getParentFile().exists()) {
            internalFile.getParentFile().mkdirs();
        }

        if (!internalFile.exists()) {
            internalFile.createNewFile();
        }

        bos = new BufferedOutputStream(new FileOutputStream(internalFile));

        bos.write(decrypted);
        bos.flush();
        bos.close();
    }

    public static void encodeTextFile(Context context, String filePath) throws Exception {
        // Convert file path to file
        File file = new File(filePath);

        // Read String from file
        String s = readFromFile(context, filePath);

        // convert stringBuilder to bytes
        byte[] fileBytes = s.getBytes();
        Log.d("asdffds", "S: " + s);

        // encode file bytes
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileBytes);

        Log.d("asdffds", "Encrypted: " + String.valueOf(encrypted));

        // Save file to external storage
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

        bos.write(Base64.encodeToString(encrypted, Base64.DEFAULT).getBytes());
        bos.flush();
        bos.close();

    }
    public static void writeToFile(String data, String filepath) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(filepath);
            if (!file.getParentFile().exists()) {
                boolean mkdirs = file.getParentFile().mkdirs();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String readFromFile(Context context, String filepath) {
        Log.d("asdffds76", filepath);
        String ret = "";
        try {
            InputStream inputStream = new FileInputStream(filepath);

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8192);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static String[] SplitStringByByteLength(String src, String encoding, int maxsize) {
        Charset cs = Charset.forName(encoding);
        CharsetEncoder coder = cs.newEncoder();
        ByteBuffer out = ByteBuffer.allocate(maxsize);  // output buffer of required size
        CharBuffer in = CharBuffer.wrap(src);
        List<String> ss = new ArrayList<>();            // a list to store the chunks
        int pos = 0;
        while (true) {
            CoderResult cr = coder.encode(in, out, true); // try to encode as much as possible
            int newpos = src.length() - in.length();
            String s = src.substring(pos, newpos);
            ss.add(s);                                  // add what has been encoded to the list
            pos = newpos;                               // store new input position
            out.rewind();                               // and rewind output buffer
            if (!cr.isOverflow()) {
                break;                                  // everything has been encoded
            }
        }
        return ss.toArray(new String[0]);
    }

    public static String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }

    public static byte[][] splitArray(byte[] arrayToSplit, int chunkSize){
        if(chunkSize<=0){
            return null;  // just in case :)
        }
        // first we have to check if the array can be split in multiple 
        // arrays of equal 'chunk' size
        int rest = arrayToSplit.length % chunkSize;  // if rest>0 then our last array will have less elements than the others 
        // then we check in how many arrays we can split our input array
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0); // we may have to add an additional array for the 'rest'
        // now we know how many arrays we need and create our result array
        byte[][] arrays = new byte[chunks][];
        // we create our resulting arrays by copying the corresponding 
        // part from the input array. If we have a rest (rest>0), then
        // the last array will have less elements than the others. This 
        // needs to be handled separately, so we iterate 1 times less.
        for(int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++){
            // this copies 'chunk' times 'chunkSize' elements into a new array
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if(rest > 0){ // only when we have a rest
            // we copy the remaining elements into the last chunk
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays; // that's it
    }

    public static void copy(File src, File dst) throws IOException {
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        dst.createNewFile();
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static String roundToFiveDecimalPlaces(String decimalString) {
        try {
            BigDecimal decimalValue = new BigDecimal(decimalString);
            decimalValue = decimalValue.setScale(5, RoundingMode.HALF_UP);
            return decimalValue.toPlainString();
        } catch (NumberFormatException e) {
            // Handle invalid input
            e.printStackTrace();
            return null;
        }
    }
}