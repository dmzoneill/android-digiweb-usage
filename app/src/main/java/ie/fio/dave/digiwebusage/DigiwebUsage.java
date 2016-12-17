package ie.fio.dave.digiwebusage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class DigiwebUsage extends AppCompatActivity
{
  public DigiwebUsage()
  {

  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    DigiwebShared.appcontext = this.getApplicationContext();
    DigiwebShared.app = this;

    SharedPreferences credentials = DigiwebShared.appcontext.getSharedPreferences("digiweb_preferences", DigiwebShared.appcontext.MODE_PRIVATE);

    EditText edtText1 = (EditText) this.findViewById(R.id.forumusernameeditbox);
    edtText1.setText(credentials.getString("forumusernameeditbox", ""));

    EditText edtText2 = (EditText) this.findViewById(R.id.forumpasswordeditbox);
    edtText2.setText(credentials.getString("forumpasswordeditbox", ""));

    EditText edtText3 = (EditText) this.findViewById(R.id.broadbandusernameeditbox);
    edtText3.setText(credentials.getString("broadbandusernameeditbox", ""));

    EditText edtText4 = (EditText) this.findViewById(R.id.broadbandpasswordeditbox);
    edtText4.setText(credentials.getString("broadbandpasswordeditbox", ""));
  }

  public void onSavedConnectClick(View view)
  {
    SharedPreferences userDetails = this.getApplicationContext().getSharedPreferences("digiweb_preferences", DigiwebShared.appcontext.MODE_PRIVATE);
    SharedPreferences.Editor edit = userDetails.edit();
    edit.clear();
    edit.putString("forumusernameeditbox", ((EditText) this.findViewById(R.id.forumusernameeditbox)).getText().toString().trim());
    edit.putString("forumpasswordeditbox", ((EditText) this.findViewById(R.id.forumpasswordeditbox)).getText().toString().trim());
    edit.putString("broadbandusernameeditbox", ((EditText) this.findViewById(R.id.broadbandusernameeditbox)).getText().toString().trim());
    edit.putString("broadbandpasswordeditbox", ((EditText) this.findViewById(R.id.broadbandpasswordeditbox)).getText().toString().trim());
    edit.commit();

    new DigiWebAsync().execute("Save");
  }

  public void onDigiwebForumsClick(View view)
  {
    String url = "http://support.smarttelecom.ie/forums/";
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }
}
