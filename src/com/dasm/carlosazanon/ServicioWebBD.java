package com.dasm.carlosazanon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ServicioWebBD extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void consultar(View v){
    	new ConsultaBD().execute();
    }
    
    private class ConsultaBD extends AsyncTask<Void,Void,Void>{

    	private ProgressDialog pDialog;
    	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){
				Log.i(e.toString(),"Error");
			}
			return null;			
		}
		
		@Override
		protected void onPostExecute(Void arg0) {
			pDialog.dismiss();
		}
    	
    }
}
