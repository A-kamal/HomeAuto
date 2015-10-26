package ayman.homoauto;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
 
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class MainActivity extends Activity implements OnClickListener {
 
	//private EditText value;
	private ToggleButton fan,lights,manual,alarm,window,blinds; 
	public ListView mList;
	public Button speakButton;
	private ProgressBar pb;
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		//value=(EditText)findViewById(R.id.editText1);s
		pb=(ProgressBar)findViewById(R.id.progressBar1);
		pb.setVisibility(View.GONE);
		fan=(ToggleButton)findViewById(R.id.t1); //Linking button from layout to main 
		fan.setOnClickListener(this);
		lights=(ToggleButton)findViewById(R.id.led);
		lights.setOnClickListener(this);
		manual=(ToggleButton)findViewById(R.id.manual);
		manual.setOnClickListener(this);
		alarm=(ToggleButton)findViewById(R.id.alarm);
		alarm.setOnClickListener(this);
		blinds=(ToggleButton)findViewById(R.id.blinds);
		blinds.setOnClickListener(this);
		speakButton=(Button)findViewById(R.id.voice);
		speakButton.setOnClickListener(this);
		mList=(ListView)findViewById(R.id.list);
		PackageManager pm = getPackageManager();
		List activities = pm.queryIntentActivities(new Intent(
		RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
		    speakButton.setOnClickListener(this);
		} else {
		    speakButton.setEnabled(false);
		    speakButton.setText("Voice Recognition not available");
		}
	}
	public void startVoiceRecognitionActivity() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
	        "Speech recognition demo");
	    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void checkState(ToggleButton toggle, String filename){ //checkState(Button being checked, name of php variable to be changed)
		if (toggle.isChecked()){ //checks if the button is now in the "on" state
			new MyAsyncTask().execute("1",filename); //Writes a 1 to the php variable being passed from the onClick method
		}											 
		else {										 
			new MyAsyncTask().execute("0",filename); //writes a 0 to the php variable beign passed from the onClick method
		}

	}
	
	public void onClick(View v) {
		switch(v.getId()){ // Check which button was pressed by checking its id
		case R.id.voice:
			startVoiceRecognitionActivity();
		case R.id.led:
			checkState(lights,"state"); //Checks what state the button will be in
		case R.id.t1: //If the button is now in the on or off state
			checkState(fan,"state2");
			break;
		case R.id.manual:
			checkState(manual,"state5");
		case R.id.alarm:
			checkState(alarm,"state1");
		case R.id.blinds:
			checkState(blinds,"state4");
		}
		pb.setVisibility(View.VISIBLE);

		// TODO Auto-generated method stub
		
		
 
	} 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	        // Fill the list view with the strings the recognizer thought it
	        // could have heard
	        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches));

	        
	        if (matches!=null){
	        	handleResults(matches);
	        }
	        
	        }
	    super.onActivityResult(requestCode, resultCode, data);
	    }
	private void handleResults(ArrayList<String> matches) { //Brute force method for voice recognition.
		if (matches.contains("lights on")){
			new MyAsyncTask().execute("1","state");
			Toast.makeText(getApplicationContext(), "Turning Lights on", Toast.LENGTH_SHORT).show();
			lights.setChecked(true);
		}else if (matches.contains("lights off")){
			new MyAsyncTask().execute("0","state");
			lights.setChecked(false);
			Toast.makeText(getApplicationContext(), "Turning Lights off", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("manual on")){
			new MyAsyncTask().execute("1","state5");
			manual.setChecked(true);
			Toast.makeText(getApplicationContext(), "Turning Manual Control on", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("manual off")){
			new MyAsyncTask().execute("0","state5");
			manual.setChecked(false);
			Toast.makeText(getApplicationContext(), "Turning Manual Control off", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("fan on")){
			new MyAsyncTask().execute("1","state2");
			fan.setChecked(true);
			Toast.makeText(getApplicationContext(), "Turning fan on", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("fan off")){
			new MyAsyncTask().execute("0","state2");
			fan.setChecked(false);
			Toast.makeText(getApplicationContext(), "Turning fan off", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("blinds on")){
			new MyAsyncTask().execute("1","state4");
			blinds.setChecked(true);
			Toast.makeText(getApplicationContext(), "Turning blinds on", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("blinds off")){
			new MyAsyncTask().execute("0","state4");
			blinds.setChecked(false);
			Toast.makeText(getApplicationContext(), "Turning blinds off", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("alarm on")){
			new MyAsyncTask().execute("1","state1");
			alarm.setChecked(true);
			Toast.makeText(getApplicationContext(), "Turning alarm on", Toast.LENGTH_SHORT).show();
		}else if (matches.contains("alarm off")){
			new MyAsyncTask().execute("0","state1");
			alarm.setChecked(false);
			Toast.makeText(getApplicationContext(), "Turning alarm off", Toast.LENGTH_SHORT).show();
		}
		
		
	}
 
	public class MyAsyncTask extends AsyncTask<String, Integer, Double>{
 
		@Override
		protected Double doInBackground(String... params) { //Takes in several String parameters that are passed on to the postData parameters
			// TODO Auto-generated method stub
			postData(params[0],params[1]); //Calls postData to be done in the background, 
			return null;
		}
 
		protected void onPostExecute(Double result){
			pb.setVisibility(View.GONE);
			Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
		}
		protected void onProgressUpdate(Integer... progress){
			pb.setProgress(progress[0]);
		}
 
		public void postData(String valueIWantToSend, String filename) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://elec3907.com/mobileapp.php");
 
			try {
				// Add data to be sent to the php 
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(filename, valueIWantToSend));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
 
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
 
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
 
	}
}