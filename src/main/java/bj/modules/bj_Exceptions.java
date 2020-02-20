package bj.modules;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

public class bj_Exceptions {
	public static class bjLog {
		static String logPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/bj_apps/logs";
		bjLog(String LogPath) {
			super();
			this.logPath=LogPath;

		}
		public static void Write (String Tag, String Message, Activity activity) throws IOException {

			if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				// for ActivityCompat#requestPermissions for more details.

				ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
//            return;
			}



			Date date= Calendar.getInstance().getTime();
			String timestamp =""+date.getYear()+date.getMonth()+date.getDay();
			String filename = timestamp + ".log";
			String GPath=logPath + "/" + filename;
			Log.i("BJExceptions",Tag+": " +Message);
			bjFileWriteToTextFile(GPath,date.toString()+ "    "+ Tag+":    "+Message,true,true);
		}
		public static String Read (String Tag){
			return  "";
		}
	}
	public static class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

		private Thread.UncaughtExceptionHandler defaultUEH;

		private String localPath;

		private String url;

		/*
		 * if any of the parameters is null, the respective functionality
		 * will not be used
		 */
		public CustomExceptionHandler(String localPath, String url) {
			this.localPath = localPath;
			this.url = url;
			this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		}

		public void uncaughtException(Thread t, Throwable e) {
			Date date=Calendar.getInstance().getTime();
			String timestamp =""+date.getYear()+date.getMonth()+date.getDay();
			String filename = timestamp + ".stacktrace";
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String stacktrace = result.toString();
			printWriter.close();


			if (localPath != null) {
				writeToFile(stacktrace, filename);
			}
			if (url != null) {
				sendToServer(stacktrace, filename);
			}

			defaultUEH.uncaughtException(t, e);
		}

		private void writeToFile(String stacktrace, String filename) {
			try {
				BufferedWriter bos = new BufferedWriter(new FileWriter(
						localPath + "/" + filename));
				bos.write(stacktrace);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void sendToServer(String stacktrace, String filename) {
			//   DefaultHttpClient httpClient = new DefaultHttpClient();
			//   HttpPost httpPost = new HttpPost(url);
			//   List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			//   nvps.add(new BasicNameValuePair("filename", filename));
			//   nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
			//   try {
			//       httpPost.setEntity(
			//               new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			//       httpClient.execute(httpPost);
			//   } catch (IOException e) {
			//       e.printStackTrace();
			//   }
		}
	}
	public static void bjFileWriteToTextFile(String FilePath,String MyText,Boolean AddToEnd,Boolean InNewLine) throws IOException {
		File file=new File(FilePath);
		file.getParentFile().mkdirs();




		if (!AddToEnd) {
			//replace
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, false), "UTF-8"));
			writer.append(MyText);
			writer.close();
		}else {

			if (InNewLine) {
				//new line
				Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true), "UTF-8"));
				if (new File(FilePath).length()==0) {
					writer.append(MyText);
					writer.close();
				}else {
					writer.append(System.lineSeparator()+MyText);
					writer.close();
				}

			}else {
				//add to end
				Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true), "UTF-8"));

				writer.append(MyText);
				writer.close();
			}
		}
	}
}
