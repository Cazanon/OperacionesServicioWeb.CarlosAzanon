package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.client.methods.HttpDelete;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Borrado extends Activity {

	private JSONArray records;
	private JSONObject record;
	private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_borrado);
        
        Intent i = getIntent();
        extras = i.getExtras();
        String datos = extras.getString("datos");

		try {
			records = new JSONArray(datos);
			record = records.getJSONObject(1);

			TextView dni = (TextView)findViewById(R.id.dniB);
			EditText nombre = (EditText)findViewById(R.id.nombreB);
			EditText apellidos = (EditText)findViewById(R.id.apellidosB);
			EditText direccion = (EditText)findViewById(R.id.direccionB);
			EditText telefono = (EditText)findViewById(R.id.telefonoB);
			EditText equipo = (EditText)findViewById(R.id.equipoB);

			dni.setText(record.getString("DNI"));
			nombre.setText(record.getString("Nombre"));
			apellidos.setText(record.getString("Apellidos"));
			direccion.setText(record.getString("Direccion"));
			telefono.setText(record.getString("Telefono"));
			equipo.setText(record.getString("Equipo"));
			
			dni.setEnabled(false);
			nombre.setFocusable(false);
			apellidos.setFocusable(false);
			direccion.setFocusable(false);
			telefono.setFocusable(false);
			equipo.setFocusable(false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    
    public void deleteRecord(View v) {
    	new DeleteBD().execute();
    	
    	Intent i=new Intent();
		i.putExtra("respuesta","Borrado realizado");
		setResult(RESULT_OK,i);
    	
		finish();
    }
    
    private class DeleteBD extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			String url = extras.getString("url");
			String dni = extras.getString("dni");
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpDelete httpdelete = new HttpDelete(url + "/" + dni);
				httpclient.execute(httpdelete);
				httpclient.close();
			} catch (IOException e) {
				Log.e("Error en la operación (IO)", e.toString());
				e.printStackTrace();
			}
			return null;
		}
    }
    
    @Override
	public void onBackPressed(){
		Intent i=new Intent();
		i.putExtra("respuesta","Borrado cancelado");
		setResult(RESULT_CANCELED,i);
		super.onBackPressed();
	}
}
