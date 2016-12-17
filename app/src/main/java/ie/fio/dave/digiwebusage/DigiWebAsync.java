package ie.fio.dave.digiwebusage;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.join;


public class DigiWebAsync extends AsyncTask<String, Integer, Double>
{
  private static String lastupdate;

  @Override
  protected Double doInBackground(String... params)
  {
    postData(params[0]);
    return null;
  }

  public static String getLastupdate()
  {
    return lastupdate;
  }

  public void postData(String valueIWantToSend)
  {
    SharedPreferences userDetails = DigiwebShared.appcontext.getSharedPreferences("digiweb_preferences", DigiwebShared.appcontext.MODE_PRIVATE);

    URL url = null;
    HttpURLConnection urlConnection = null;
    CookieManager msCookieManager = new CookieManager();
    String COOKIES_HEADER = "Set-Cookie";

    try
    {
      String username = userDetails.getString("forumusernameeditbox", "");
      String password = userDetails.getString("forumpasswordeditbox", "");
      String md5_smart_password = "";

      byte[] bytesOfMessage = password.getBytes("UTF-8");

      try
      {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < thedigest.length; i++)
        {
          sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16).substring(1));
        }

        md5_smart_password = sb.toString();
      }
      catch (NoSuchAlgorithmException e)
      {
        return;
      }

      String urlParameters = "vb_login_username=" + username;
      urlParameters += "&vb_login_password=" + "";
      urlParameters += "&s=" + "";
      urlParameters += "&securitytoken=guest";
      urlParameters += "&do=login";
      urlParameters += "&vb_login_md5password=" + md5_smart_password;
      urlParameters += "&vb_login_md5password_utf=" + md5_smart_password;

      byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
      int postDataLength = postData.length;
      String request = "http://support.smarttelecom.ie/forums/login.php?do=login";
      url = new URL(request);

      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setDoOutput(true);
      urlConnection.setInstanceFollowRedirects(true);
      urlConnection.setRequestMethod("POST");
      urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      urlConnection.setRequestProperty("charset", "utf-8");
      urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
      urlConnection.setUseCaches(false);

      try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream()))
      {
        wr.write(postData);
      }

      Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
      List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

      if (cookiesHeader != null)
      {
        for (String cookie : cookiesHeader)
        {
          msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
      }

      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
    }
    catch (IOException e)
    {
      e.printStackTrace();
      urlConnection.disconnect();

      if(valueIWantToSend.compareTo("")!=0)
      {
        DigiwebShared.app.runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            Toast.makeText(DigiwebShared.appcontext, "There was some problem accessing the site, please try agian later", Toast.LENGTH_LONG).show();
          }
        });
      }
    }


    try
    {
      String username = userDetails.getString("broadbandusernameeditbox", "");
      String password = userDetails.getString("broadbandpasswordeditbox", "");

      String urlParameters = "user=" + username;
      urlParameters += "&pass=" + password;
      urlParameters += "&submit=SUBMIT";

      byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
      int postDataLength = postData.length;
      String request = "http://support.smarttelecom.ie/forums/smart_usage";
      url = new URL(request);

      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setDoOutput(true);
      urlConnection.setInstanceFollowRedirects(true);
      urlConnection.setRequestMethod("POST");
      urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      urlConnection.setRequestProperty("charset", "utf-8");
      urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
      urlConnection.setUseCaches(false);

      if (msCookieManager.getCookieStore().getCookies().size() > 0)
      {
        urlConnection.setRequestProperty("Cookie", join(";", msCookieManager.getCookieStore().getCookies()));
      }

      try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream()))
      {
        wr.write(postData);
      }

      InputStream in = new BufferedInputStream(urlConnection.getInputStream());

      String pattern = "<table width=800 border=\"0\" style=\"border-collapse:collapse;\" cellspacing=\"0\">(.*?)</table>";

      // Create a Pattern object
      Pattern r = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL);

      String data = readStream(in);

      // Now create matcher object.
      Matcher m = r.matcher(data);

      if (m.find())
      {
        String pattern1 = "<td>(.*?)</td>";
        Pattern rt = Pattern.compile(pattern1);
        Matcher m1 = rt.matcher(m.group(1));

        String last = "";

        while (m1.find())
        {
          last = m1.group(1);
        }

        final String result = last.substring(last.indexOf("(") + 1, last.length() - 5);
        lastupdate = result;

        if(valueIWantToSend.compareTo("")!=0)
        {
          DigiwebShared.app.runOnUiThread(new Runnable()
          {

            @Override
            public void run()
            {
              Toast.makeText(DigiwebShared.appcontext, "Successfully queried bandwidth, now add the widget to your home screen", Toast.LENGTH_LONG).show();
            }
          });
        }
      }
      else
      {
        if(valueIWantToSend.compareTo("")!=0)
        {
          DigiwebShared.app.runOnUiThread(new Runnable()
          {

            @Override
            public void run()
            {
              Toast.makeText(DigiwebShared.appcontext, "Problem logging in, check your credentials", Toast.LENGTH_LONG).show();
            }
          });
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      urlConnection.disconnect();

      if(valueIWantToSend.compareTo("")!=0)
      {
        DigiwebShared.app.runOnUiThread(new Runnable()
        {

          @Override
          public void run()
          {
            Toast.makeText(DigiwebShared.appcontext, "There was some problem accessing the site, please try agian later", Toast.LENGTH_LONG).show();
          }
        });
      }
    }
  }

  private String readStream(InputStream is)
  {
    try
    {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      int i = is.read();
      while (i != -1)
      {
        bo.write(i);
        i = is.read();
      }
      return bo.toString();
    }
    catch (IOException e)
    {
      return "";
    }
  }
}