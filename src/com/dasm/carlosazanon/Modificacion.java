package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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

public class Modificacion extends Activity {

	private JSONArray registros;
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consulta);
		
		extras = getIntent().getExtras();
		String datos = extras.getString("datos");

		try {
			registros = new JSONArray(datos);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject registro;

		try {
			registro = registros.getJSONObject(1);

			TextView dni = (TextView)findViewById(R.id.dniM);
			EditText nombre = (EditText)findViewById(R.id.nombreM);
			EditText apellidos = (EditText)findViewById(R.id.apellidosM);
			EditText direccion = (EditText)findViewById(R.id.direccionM);
			EditText telefono = (EditText)findViewById(R.id.telefonoM);
			EditText equipo = (EditText)findViewById(R.id.equipoM);

			dni.setText(registro.getString("DNI"));
			nombre.setText(registro.getString("Nombre"));
			apellidos.setText(registro.getString("Apellidos"));
			direccion.setText(registro.getString("Direccion"));
			telefono.setText(registro.getString("Telefono"));
			equipo.setText(registro.getString("Equipo"));
			
			dni.setEnabled(false);
		} catch (JSONException e1) {
			e1.printStackTrace();
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
	
	public void updateregistro(View v) {
		try {
			EditText dni = (EditText)findViewById(R.id.dniM);
			EditText nombre = (EditText)findViewById(R.id.nombreM);
			EditText apellidos = (EditText)findViewById(R.id.apellidosM);
			EditText direccion = (EditText)findViewById(R.id.direccionM);
			EditText telefono = (EditText)findViewById(R.id.telefonoM);
			EditText equipo = (EditText)findViewById(R.id.equipoM);
			
			String json = "";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("DNI", dni.getText().toString());
			jsonObject.put("Nombre", nombre.getText().toString());
			jsonObject.put("Apellidos", apellidos.getText().toString());
			jsonObject.put("Direccion", direccion.getText().toString());
			jsonObject.put("Telefono", telefono.getText().toString());
			jsonObject.put("Equipo", equipo.getText().toString());
			json = jsonObject.toString();
			
			new UpdateBD().execute(json);
			
			Intent i=new Intent();
			i.putExtra("respuesta","Actualización realizada");
			setResult(RESULT_OK,i);
			
			finish();
		} catch (JSONException e) {
			Log.e("Error en la operaciÃ³n (JSON)", e.toString());
			e.printStackTrace();
		} 
	}
	
	private class UpdateBD extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpPut httpput = new HttpPut(extras.getString("url"));
				StringEntity se = new StringEntity(json);
				httpput.setEntity(se);
				httpclient.execute(httpput);
				httpclient.close();
			} catch (IOException e) {
				Log.e("Error en la operacion (IO)", e.toString());
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
