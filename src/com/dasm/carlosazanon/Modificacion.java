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

	private JSONArray registro;
	private String URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_modificacion);
		
		Bundle extras = getIntent().getExtras();
		String datos = extras.getString("datos");
		
		URL=extras.getString("url");
		
		try {
			registro = new JSONArray(datos);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject dato;
		try {
			dato = registro.getJSONObject(1);

			TextView dni = (TextView)findViewById(R.id.dniM);
			EditText nombre = (EditText)findViewById(R.id.nombreM);
			EditText apellidos = (EditText)findViewById(R.id.apellidosM);
			EditText direccion = (EditText)findViewById(R.id.direccionM);
			EditText telefono = (EditText)findViewById(R.id.telefonoM);
			EditText equipo = (EditText)findViewById(R.id.equipoM);

			dni.setText(dato.getString("DNI"));
			nombre.setText(dato.getString("Nombre"));
			apellidos.setText(dato.getString("Apellidos"));
			direccion.setText(dato.getString("Direccion"));
			telefono.setText(dato.getString("Telefono"));
			equipo.setText(dato.getString("Equipo"));
			
			//dni.setEnabled(false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void modificarRegistro(View v) {
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
			
			new ModificarBD().execute(json);
			
			Intent i=new Intent();
			i.putExtra("respuesta","Registro modificado");
			setResult(RESULT_OK,i);
			
			finish();
		} catch (JSONException e) {
			Log.e("Error en la operacion(JSON)", e.toString());
			e.printStackTrace();
		} 
	}
	
	private class ModificarBD extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpPut httpput = new HttpPut(URL);
				StringEntity se = new StringEntity(json);
				httpput.setEntity(se);
				httpclient.execute(httpput);
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
		i.putExtra("respuesta","Modificacion cancelada");
		setResult(RESULT_CANCELED,i);
		super.onBackPressed();
	}
}
