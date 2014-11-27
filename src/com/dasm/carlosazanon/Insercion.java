package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

public class Insercion extends Activity {
	
	private TextView dni;
	private String URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_insercion);

		Bundle extras = getIntent().getExtras();
		dni = (TextView)findViewById(R.id.dniI);
		dni.setText(extras.getString("dni"));
		dni.setEnabled(false);		

		URL=extras.getString("url");
	}
	
	public void insertarRegistro(View v) {
		try {
			
			EditText nombre = (EditText)findViewById(R.id.nombreI);
			EditText apellidos = (EditText)findViewById(R.id.apellidosI);
			EditText direccion = (EditText)findViewById(R.id.direccionI);
			EditText telefono = (EditText)findViewById(R.id.telefonoI);
			EditText equipo = (EditText)findViewById(R.id.equipoI);
			
			String json = "";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("DNI", dni.getText().toString());
			jsonObject.put("Nombre", nombre.getText().toString());
			jsonObject.put("Apellidos", apellidos.getText().toString());
			jsonObject.put("Direccion", direccion.getText().toString());
			jsonObject.put("Telefono", telefono.getText().toString());
			jsonObject.put("Equipo", equipo.getText().toString());
			json = jsonObject.toString();
			
			new InsertarBD().execute(json);
			
			Intent i=new Intent();
			i.putExtra("respuesta","Registro insertado");
			setResult(RESULT_OK,i);			
			finish();
		} catch (JSONException e) {
			Log.e("Error en la operacion(JSON)", e.toString());
			e.printStackTrace();
		} 
	}
	
	private class InsertarBD extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpPost httppost = new HttpPost(URL);
				StringEntity se = new StringEntity(json);
				httppost.setEntity(se);
				httpclient.execute(httppost);
				httpclient.close();
			} catch (IOException e) {
				Log.e("Error en la operacion(IO)", e.toString());
				e.printStackTrace();
			}			
			return null;
		}
		
	}
	
	@Override
	public void onBackPressed(){
		Intent i=new Intent();
		i.putExtra("respuesta","Insercion cancelada");
		setResult(RESULT_CANCELED,i);
		super.onBackPressed();
	}
}
